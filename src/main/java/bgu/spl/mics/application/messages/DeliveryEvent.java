package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;


public class DeliveryEvent implements Event<Object> {
    public BookOrderEvent bookOrderEvent;//in order to know which event called it

    public DeliveryEvent(BookOrderEvent bookOrderEvent) {

        this.bookOrderEvent = bookOrderEvent;
    }
}
