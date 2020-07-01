package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.atomic.AtomicInteger;

import static bgu.spl.mics.application.passiveObjects.OrderResult.SUCCESSFULLY_TAKEN;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {
	private String BookName;
	private MoneyRegister moneyRegister=MoneyRegister.getInstance();
	private AtomicInteger tick=new AtomicInteger(0);
	private ServiceCounter counter=ServiceCounter.getInstance();



	public SellingService(String name) {
		super(name);
	}

	public SellingService(String BookName,String name) {
		super(name);
		this.BookName = BookName;
	}


	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, c -> {
			terminate();
		});

		subscribeBroadcast(TickBroadcast.class,TB-> {
			tick.set(TB.currentTick);
		});

		subscribeEvent(BookOrderEvent.class, BOE -> {
			Future<Integer> price = sendEvent(new CheckAvailabilityEvent(BOE.getBook()));
			if(price!=null && price.get()!=-1 & price.get()<=BOE.getCustomer().wallet.amount) {
				Future<OrderResult> OrderResult = sendEvent(new TakeBookEvent(BOE.getBook()));
				if(OrderResult!=null){
					if(OrderResult.get().equals(SUCCESSFULLY_TAKEN)){
						synchronized (BOE.getCustomer().wallet){
							moneyRegister.chargeCreditCard(BOE.getCustomer(),price.get());
						}
						synchronized (moneyRegister){
							OrderReceipt o = new OrderReceipt(0, this.getName(), BOE.getCustomer().getId(), BOE.getBook(), price.get(), tick.get());
							o.setOrderTick(BOE.getOrderTic());
							o.setIssuedTick(tick.get());
							BOE.getCustomer().addReceipt(o);
							moneyRegister.file(o);
							complete(BOE,o);
						}
						sendEvent(new DeliveryEvent(BOE));
					} else
						complete(BOE, null);
				}else
					complete(BOE, null);
			}else
				complete(BOE, null);

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