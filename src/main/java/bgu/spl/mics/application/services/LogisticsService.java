package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private AtomicInteger tick=new AtomicInteger(0);
	private ServiceCounter counter=ServiceCounter.getInstance();

	public LogisticsService(String name) {
		super(name);

	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, c -> {
			terminate();
		});

		subscribeEvent(DeliveryEvent.class, delivery->{
			Future<Future<DeliveryVehicle>> futureFuture=sendEvent(new AcquireVehicleEvent());
			if(futureFuture!=null) {
				if (futureFuture.get() != null) {
					Future<DeliveryVehicle> future = futureFuture.get();
					if (future.get() != null) {

						DeliveryVehicle vehicle = future.get();
						vehicle.deliver("", delivery.bookOrderEvent.getCustomer().getDistance());//sending the book
						complete(delivery, vehicle);
						sendEvent(new ReleaseVehicleEvent(vehicle));
					} else {
						futureFuture.resolve(null);
						future.resolve(null);
					}
				}
				else
					complete(delivery, null);
			}
			else
				complete(delivery,null);

		});
		int oldvale;
		int newval;
		do{
			oldvale=counter.counter.get();
			newval=oldvale-1;
		}
		while(!counter.counter.compareAndSet(oldvale,newval));

	}


}