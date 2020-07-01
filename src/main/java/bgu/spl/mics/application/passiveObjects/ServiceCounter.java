package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * this class is using for counting the services that have been initialized
 */
public class ServiceCounter {
    public AtomicInteger counter=new AtomicInteger(0);

    //********************Singleton**********************
   private static class ServiceCounterHolder {
        private static ServiceCounter instance = new ServiceCounter();
    }

    private ServiceCounter() {
    }

    public static ServiceCounter getInstance() {
        return ServiceCounter.ServiceCounterHolder.instance;
    }
}
