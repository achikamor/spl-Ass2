package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {
	private List<OrderReceipt> receiptList=new LinkedList<>();
	private Integer profits;
	/**
     * Retrieves the single instance of this class.
     */


	private static class MoneyRegisterHolder{
		private static MoneyRegister instance=new MoneyRegister();
	}
	private MoneyRegister(){
		this.profits=0;
	}

	public static MoneyRegister getInstance() {

		return MoneyRegisterHolder.instance;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		receiptList.add(r);
		synchronized (profits) {
			profits = profits + r.getPrice();
		}
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		return this.profits;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		synchronized (c.wallet) {         //synchronizing only the wallet so it will ce posible to 'work' on the customer simultaneous

			if (amount <= c.getAvailableCreditAmount())
				c.setAviableAmoutInCreditCard(-amount);        //changing  the customer field and the wallet
		}
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		try{
			List<OrderReceipt> toPrint=new LinkedList<>(receiptList);
			FileOutputStream fileOut=new FileOutputStream(filename);
			ObjectOutputStream out=new ObjectOutputStream(fileOut);
			out.writeObject(toPrint);
			out.close();
			fileOut.close();
		} catch(IOException i){}
	}
}
