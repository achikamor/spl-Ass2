package bgu.spl.mics.application.services;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

import java.io.Serializable;

/**
 * this class was made to save a pair of string name and it's order tick by customer's order schedule
 */
public class OrderPair implements Serializable {
    private String book;
    private int Tick;

    public OrderPair(String book, int orderTick){
        this.book=book;
        this.Tick=orderTick;
    }
    public OrderPair(){

    }

    public void setBook(String s){
        this.book=s;

    }
    public void setTick(int Tick){
        this.Tick=Tick;
    }

    public int getTick(){return this.Tick;}

    public String getBook(){
        return this.book;
    }

    public OrderPair(OrderPair x){
        this.setBook(x.getBook());
        this.setTick(x.getTick());

    }

}