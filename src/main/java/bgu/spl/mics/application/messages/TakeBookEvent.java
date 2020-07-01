package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import bgu.spl.mics.application.passiveObjects.OrderResult;

public class TakeBookEvent implements Event<OrderResult> {
    public String  name;
    public TakeBookEvent(String name){
        this.name=name;
    }

}
