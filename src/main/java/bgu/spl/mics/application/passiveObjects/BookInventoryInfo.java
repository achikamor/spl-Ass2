package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;


/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo implements Serializable {
	private String BookTitle;
	private int Price;
	private Integer Amount;

	public BookInventoryInfo(String name,Integer amount,int price){
		this.BookTitle=name;
		this.Amount=amount;
		this.Price=price;
	}
	/**
	 * Retrieves the title of this book.
	 * <p>
	 * @return The title of this book.
	 */
	public String getBookTitle() {

		return this.BookTitle;
	}

	/**
	 * Retrieves the amount of books of this type in the inventory.
	 * <p>
	 * @return amount of available books.
	 */
	public Integer getAmountInInventory() {

		return this.Amount;
	}

	/**
	 * Retrieves the price for  book.
	 * <p>
	 * @return the price of the book.
	 */
	public int getPrice() {

		return this.Price;
	}

	/**
	 *
	 * @param x ,add x to the amount of the book
	 */
	public void setAmount(int x){
		this.Amount=Amount+x;
	}

}