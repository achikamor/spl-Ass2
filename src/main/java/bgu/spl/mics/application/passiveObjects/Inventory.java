package bgu.spl.mics.application.passiveObjects;


import java.util.HashMap;
import java.util.Vector;
import java.io.*;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {
	private int size;
	private Vector<BookInventoryInfo> books=new Vector<>();

	/**
	 * Retrieves the single instance of this class.
	 */

	private static class InventoryHolder {
		private static Inventory instance = new Inventory();
	}

	private Inventory() {
		this.size = 0;
	}

	public static Inventory getInstance() {
		return InventoryHolder.instance;
	}

	public int getSize() {

		return this.size;
	}

	/**
	 * Initializes the store inventory. This method adds all the items given to the store
	 * inventory.
	 * <p>
	 *
	 * @param inventory Data structure containing all data necessary for initialization
	 *                  of the inventory.
	 */
	public synchronized void load(BookInventoryInfo[] inventory) {
		for (BookInventoryInfo b: inventory) {
			if(b!=null)
				books.add(b);
		}

	}

	/**
	 * Attempts to take one book from the store.
	 * <p>
	 *
	 * @param book Name of the book to take from the store
	 * @return an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
	 * The first should not change the state of the inventory while the
	 * second should reduce by one the number of books of the desired type.
	 */
	public OrderResult take(String book) {
		for (BookInventoryInfo b:books) {
			if (b.getBookTitle().equals(book)) {
				synchronized (b.getAmountInInventory()) {
					b.setAmount(-1);
					return OrderResult.SUCCESSFULLY_TAKEN;
				}
			}
		}
		return OrderResult.NOT_IN_STOCK;
	}


	/**
	 * Checks if a certain book is available in the inventory.
	 * <p>
	 *
	 * @param book Name of the book.
	 * @return the price of the book if it is available, -1 otherwise.
	 */
	public int checkAvailabiltyAndGetPrice(String book) {
		for (int i=0;i<books.size();i++) {
			if (books.get(i).getBookTitle().equals(book) && books.get(i).getAmountInInventory() > 0) {
				return books.get(i).getPrice();
			}
		}
		return -1;
	}

	/**
	 * <p>
	 * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
	 * should be the titles of the books while the values (type {@link Integer}) should be
	 * their respective available amount in the inventory.
	 * This method is called by the main method in order to generate the output.
	 */
	public synchronized void printInventoryToFile(String filename) {
		try{
			HashMap<String,Integer> map=new HashMap<>();
			for(BookInventoryInfo b: books){
				BookInventoryInfo tmp=b;
				map.put(tmp.getBookTitle(),tmp.getAmountInInventory());
			}
			FileOutputStream fileOut = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(map);
			out.close();
			fileOut.close();

		} catch(IOException i){}

	}

	/**
	 *
	 * @param name , name of book to find in books Vector
	 * @return the index of the book
	 */
	private int getBookIndex(String name) {
		for (int i = 0; i < this.books.size(); i++) {
			if (books.get(i).getBookTitle() == name)
				return i;
		}
		return -1;
	}

}