package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.ServiceCounter;

import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private ConcurrentHashMap<Class<? extends Event>, LinkedBlockingQueue<MicroService>> EMhashmap = new ConcurrentHashMap<>();//for subscribing suitable microServices
    private ConcurrentHashMap<Event, Future> EFhashmap = new ConcurrentHashMap<>();//outcomes of each event
    private ConcurrentHashMap<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> BMhashmap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> MMhashmap = new ConcurrentHashMap<>();// what events are left to work on
    private ServiceCounter terminateCounter=ServiceCounter.getInstance();
    private static class MessageBusImplHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    private MessageBusImpl() {


    }

    public static MessageBusImpl getInstance() {
        return MessageBusImplHolder.instance;
    }

    /**
     *
     * @param type The type to subscribe to,
     * @param m    The subscribing micro-service.
     * @param <T>
     */
    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        EMhashmap.putIfAbsent(type,new LinkedBlockingQueue<>());
        EMhashmap.get(type).add(m);
    }

    /**
     *
     * @param type 	The type to subscribe to.
     * @param m    	The subscribing micro-service.
     */
    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        BMhashmap.putIfAbsent(type,new LinkedBlockingQueue<>());
            BMhashmap.get(type).add(m);

    }

    /**
     *
     * @param e      The completed event.
     * @param result The resolved result of the completed event.
     * @param <T>
     */
    @Override
    public synchronized <T> void complete(Event<T> e, T result) {
        if(EFhashmap.get(e)!=null)
            EFhashmap.get(e).resolve(result);
    }

    /**
     *
     * @param b 	The Broadcast that need to be sent to everyone (that would like to get these broadcasts)
     */
    @Override
    public void sendBroadcast(Broadcast b) {
        if (BMhashmap.containsKey(b.getClass())) {//if there are services waiting for this broadcast
            for (MicroService m : BMhashmap.get(b.getClass())) {//adds the broadcast to the right MicroServices
                if(MMhashmap.get(m)!=null)
                MMhashmap.get(m).add(b);
            }
        }
    }

    /**
     *
     * @param e     	The event to add to the queue.
     * @param <T>   the future that return from the function
     * @return
     */
    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        Future<T> future = new Future<>();
        EFhashmap.put(e, future);

       LinkedBlockingQueue<MicroService> tmp=EMhashmap.get(e.getClass());
        if(tmp!=null) {
            synchronized (tmp) {
                    if(!tmp.isEmpty()) {
                        MicroService microTmp = tmp.poll();      //takes the closest associated microService
                        MMhashmap.get(microTmp).add(e);
                        tmp.add(microTmp);//puts it back at the top of the queue
                        return future;
                    }
            }
        }
        complete(e,null);
        return future;
    }

    /**
     *
     * @param m the micro-service that need to be register to busmessage
     */
    @Override
    public void register(MicroService m) {
        if (!MMhashmap.containsKey(m)) {
            MMhashmap.put(m, new LinkedBlockingQueue<>());
        }
    }

    /**
     *
     * @param m the micro-service to unregister.
     */
    @Override
    public void unregister(MicroService m) {


        for (LinkedBlockingQueue<MicroService > value : EMhashmap.values()) {
            synchronized (value) {
                if(value.contains(m))
                    value.remove(m);
            }
        }
        for (LinkedBlockingQueue<MicroService > value : BMhashmap.values()) {
            if(value.contains(m))
                value.remove(m);
        }



        if (MMhashmap.containsKey(m)) {
            LinkedBlockingQueue<Message> tmp=MMhashmap.get(m);
            synchronized (tmp) {
                while (!tmp.isEmpty()) {
                    Message msg = tmp.poll();
                    if(msg instanceof Event) {
                        Future<?> future = EFhashmap.get(msg);
                        future.resolve(null);
                    }
                }
            }
        }
            MMhashmap.remove(m);
        int oldvale;
        int newval;
        do{
            oldvale=terminateCounter.counter.get();
            newval=oldvale+1;

        }
        while(!terminateCounter.counter.compareAndSet(oldvale,newval));

    }

    /**
     *
     * @param m The micro-service requesting to take a message from its message
     *          queue.  waits until his queue will contain a message
     * @return
     * @throws InterruptedException
     */
    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        return MMhashmap.get(m).take();//Take function is able to block, just like we want
    }
}