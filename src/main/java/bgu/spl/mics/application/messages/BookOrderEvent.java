package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event {
    public String book;
    public int OrderTic;
    public Customer customer;

    public BookOrderEvent(Customer customer, String book, int OrderTic) {
        this.customer=customer;
        this.book=book;
        this.OrderTic= OrderTic;
    }
    public Customer getCustomer(){

        return this.customer;
    }
    public String getBook(){
        return  this.book;
    }

    public  int getOrderTic(){
        return this.OrderTic;
    }

}