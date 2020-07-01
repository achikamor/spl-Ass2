package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckAvailabilityEvent implements Event {
    public String bookName;

    public CheckAvailabilityEvent(String name){

        this.bookName=name;
    }

    public String getBookName(){
        return this.bookName;
    }
}
