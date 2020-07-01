package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.List;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private List<OrderPair> orderSchedule;
	private OrderPair[] SortedOrder;
	private Customer customer;
	private AtomicInteger tick=new AtomicInteger(0);
	private ConcurrentLinkedQueue<OrderPair> sortedQueue=new ConcurrentLinkedQueue<>();
	private ServiceCounter counter=ServiceCounter.getInstance();

	public APIService() {

		super("APIService");

	}
	public APIService(Customer customer,String nameOfAPI){
		super(nameOfAPI);
		this.orderSchedule=customer.getOrders();
		this.SortedOrder=new OrderPair[customer.getOrders().size()];
		for(int i=0; i<customer.getOrders().size();i++){//copying the content of the list to an array for sorting purposes
			SortedOrder[i]=customer.getOrders().get(i);
		}
		this.SortList();
		this.customer=customer;
	}

	/**
	 * sorting the list of the order schedule by their ticks
	 */
	public void SortList(){//sorts the array
		OrderPair tmp;
		for(int j=0;j<orderSchedule.size();j++){
			for(int i=0;i<orderSchedule.size()-1;i++){
				if((Integer)SortedOrder[i].getTick()>(Integer) SortedOrder[i+1].getTick()){
					tmp=SortedOrder[i];
					SortedOrder[i]=SortedOrder[i+1];
					SortedOrder[i+1]=tmp;
				}
			}
		}
		for(int i=0;i<SortedOrder.length;i++)
			sortedQueue.add(SortedOrder[i]);

	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, TB->{
			this.tick.set(TB.currentTick);
			if(TB.currentTick>TB.duration)
				terminate();

			while((!sortedQueue.isEmpty()) && sortedQueue.peek().getTick()==tick.get()) {
				BookOrderEvent boe=new BookOrderEvent(customer,sortedQueue.peek().getBook(),tick.get());
				sortedQueue.poll();
				sendEvent(boe);

			}

			if(sortedQueue.isEmpty())
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