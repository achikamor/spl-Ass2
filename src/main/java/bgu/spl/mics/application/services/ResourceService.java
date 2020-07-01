package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private ResourcesHolder resourcesHolder=ResourcesHolder.getInstance();
	private LinkedList<Future<DeliveryVehicle>> linkedList;
	private ServiceCounter counter=ServiceCounter.getInstance();
	private AtomicInteger tick=new AtomicInteger(0);

	public ResourceService(String name){
		this(name, null);
	}
	public ResourceService(String name, DeliveryVehicle[] VehiclesToLoad){
		super(name);
		linkedList=new LinkedList<>();
	}

	@Override
	protected void initialize() {

		subscribeEvent(AcquireVehicleEvent.class, AV->{
			Future<DeliveryVehicle> future=resourcesHolder.acquireVehicle();
			linkedList.add(future);
			complete(AV,future);
		});
		subscribeEvent(ReleaseVehicleEvent.class, RV->{
			resourcesHolder.releaseVehicle(RV.VehicleToRelease);
			complete(RV,null); //check if its correct?
		});
		subscribeBroadcast(TerminateBroadcast.class, c -> {
			for(Future<DeliveryVehicle> f: linkedList){
				if(!f.isDone())
					f.resolve(null);
			}
			terminate();
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