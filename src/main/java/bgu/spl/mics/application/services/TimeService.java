package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link //Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	public int speed;
	public int duration;
	public int currentTick;

	public TimeService(int duration, int speed) {
		super("TimeService");
		this.speed=speed;
		this.duration=duration;
		currentTick=1;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, TB->{
			this.terminate();

		});
		Timer timer=new Timer();
		TimerTask timerTask= new TimerTask() {
			@Override
			public void run() {
				if(currentTick>duration){
					sendBroadcast(new TickBroadcast(currentTick,duration));
					sendBroadcast(new TerminateBroadcast());

					timer.cancel();
					timer.purge();

				}
				else {
					sendBroadcast(new TickBroadcast(currentTick, duration));
					currentTick++;
				}
			}
		};

		timer.scheduleAtFixedRate(timerTask,0,speed);

	}

	public int getCurrentTick(){
		return currentTick;
	}

	public int getDuration() {
		return duration;
	}
}