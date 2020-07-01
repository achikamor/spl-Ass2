package bgu.spl.mics.application.messages;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import bgu.spl.mics.Event;
public class AcquireVehicleEvent implements Event<Future<DeliveryVehicle>>{
public String name;

public AcquireVehicleEvent(){
    this.name="";
}
}
