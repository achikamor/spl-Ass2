package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	private Vector<DeliveryVehicle> vehicles;
	private Vector<Boolean> isBusy;
	private ConcurrentLinkedQueue<Future> futures=new ConcurrentLinkedQueue<>();
	Semaphore semaphore;


	//******* Singleton implementation**********
	private static class ResourcesHolderHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	private ResourcesHolder() {
		this.vehicles = new Vector<>();
		this.isBusy=new Vector<>();

	}

	public static ResourcesHolder getResourcesHolderInstance() {
		return ResourcesHolderHolder.instance;
	}
//*******************

	/**
	 * Retrieves the single instance of this class.
	 */
	public static ResourcesHolder getInstance() {
		return getResourcesHolderInstance();
	}


	/**
	 * Tries to acquire a vehicle and gives a future object which will
	 * resolve to a vehicle.
	 * <p>
	 *
	 * @return {@link Future<DeliveryVehicle>} object which will resolve to a
	 * {@link DeliveryVehicle} when completed.
	 */
	public synchronized Future<DeliveryVehicle> acquireVehicle() {            //else add this future to a list of futures need to be done
		Future<DeliveryVehicle> future = new Future<>();
		if (semaphore.tryAcquire()) {
			for (int i = 0; i < vehicles.size(); i++) {
				synchronized (isBusy) {
					if (isBusy.get(i) == false) {
						isBusy.set(i, true);
						future.resolve(vehicles.get(i));
						return future;
					}
				}

			}
		} else
			futures.add(future);
		return future;
	}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 *
	 * @param vehicle {@link DeliveryVehicle} to be released.
	 */
	public synchronized void releaseVehicle(DeliveryVehicle vehicle) { //if futureList is empty add the car to the garage else use this car to resolve the future in the list
		if(!futures.isEmpty()){
			futures.remove().resolve(vehicle);
		}
		else{
			for(int i=0;i<vehicles.size();i++){
				if(vehicles.get(i)==vehicle){
					isBusy.set(i,false);
					semaphore.release();
				}
			}
		}
	}

	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 *
	 * @param vehicles Array of {@link DeliveryVehicle} instances to store.
	 */
	public synchronized void load(DeliveryVehicle[] vehicles) {
		for (int i = 0; i < vehicles.length; i++) {
			this.vehicles.add(vehicles[i]);
			this.isBusy.add(false);
		}
		this.semaphore=new Semaphore(vehicles.length);
	}

}