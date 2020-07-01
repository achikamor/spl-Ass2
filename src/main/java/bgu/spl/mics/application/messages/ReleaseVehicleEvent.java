package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicleEvent implements Event<Object> {
    public DeliveryVehicle VehicleToRelease;

    public ReleaseVehicleEvent(DeliveryVehicle VehicleToRelease){

        this.VehicleToRelease=VehicleToRelease;
    }
}