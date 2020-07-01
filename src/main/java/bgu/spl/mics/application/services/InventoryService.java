package bgu.spl.mics.application.services;




import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.atomic.AtomicInteger;

import static bgu.spl.mics.application.passiveObjects.OrderResult.NOT_IN_STOCK;
import static bgu.spl.mics.application.passiveObjects.OrderResult.SUCCESSFULLY_TAKEN;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private Inventory inventory=Inventory.getInstance();
	private AtomicInteger tick=new AtomicInteger(0);
	private ServiceCounter counter=ServiceCounter.getInstance();


	public InventoryService(String name) {
		super(name);
	}

	public void load(BookInventoryInfo[] BooksToLoad){
		this.inventory.load(BooksToLoad);
	}


	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, TB->{
			this.tick.set(TB.currentTick);
			if(tick.get()>TB.duration)
				terminate();
		});
		subscribeEvent(CheckAvailabilityEvent.class, CAE ->{
		int price=this.inventory.checkAvailabiltyAndGetPrice(CAE.bookName);
		if(price==-1)
			complete(CAE,null);   //the book is not available
		else
			complete(CAE,price);
		});
		subscribeEvent(TakeBookEvent.class,TBE ->{
			OrderResult result=inventory.take(TBE.name);
			if(result==NOT_IN_STOCK)
				complete(TBE,null);
			else
				complete(TBE, SUCCESSFULLY_TAKEN);

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
