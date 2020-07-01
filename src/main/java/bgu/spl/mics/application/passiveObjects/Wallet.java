package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * this class was made to make synchronization (of the customers available amount of money) easier
 */
public class Wallet implements Serializable {
    public int creditNumber;
    public int amount;
    public Wallet(int creditnumber, int Amout){
        this.amount=Amout;
        this.creditNumber=creditnumber;
    }
}
