package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.services.OrderPair;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {

	private int id;
	private String name;
	private List<OrderPair> orders;
	private String address;
	private int distance;
	private List <OrderReceipt> Reciepts;
	private int creditCard ;
	private int availableAmoutInCreditCard;
	public Wallet wallet;


	public Customer(int id,String name,LinkedList<OrderPair> orders,String address,int distance,List<OrderReceipt> Reciepts,int creditCard,int aviableAmoutInCreditCard){
		this.id=id;
		this.name=name;
		this.orders=orders;
		this.address=address;
		this.distance=distance;
		this.Reciepts=Reciepts;
		this.creditCard=creditCard;
		this.availableAmoutInCreditCard=aviableAmoutInCreditCard;
		this.wallet=new Wallet(this.creditCard,this.availableAmoutInCreditCard);
	}

	/**
     * Retrieves the name of the customer.
     */
	public String getName() {

		return this.name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {

		return this.id;
	}

	public List<OrderPair> getOrders(){
		return this.orders;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return this.address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return this.distance;
	}


	public void addReceipt(OrderReceipt order){
		this.Reciepts.add(order);
	}
	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return this.Reciepts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return this.wallet.amount;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return this.wallet.creditNumber;
	}

	/**
	 *
	 * @param price, reducing "price" from wallet and 'available amount of money'
	 */
	public void setAviableAmoutInCreditCard(int price) {    //Check if aviable has changed,if not that's mean that the price was greater than aviable
			this.availableAmoutInCreditCard = this.availableAmoutInCreditCard + price;
			this.wallet.amount=this.wallet.amount+price;

	}

}
