package books;

import librarymembers.LibraryMember;

/**
 * @author I. Zeynep Alagoz <p>
 * Common properties of book are implemented.
 */

public abstract class Book{

	/**
	 * Type of the book which can be H for handwritten or P for printed.
	 */
	private String bookType;
	/**
	 * ID of the book which can be at most 6 digit number and determined by the library.
	 */
	private int bookID;
	/**
	 * The boolean that holds whether book is taken or not.
	 */
	private boolean isTaken;
	/**
	 * The variable that holds who took the book, if it is taken.
	 */
	private LibraryMember whoHas;

	/**
	 * Constructor of the class.
	 * Initializes bookType and bookID according to parameters.
	 * @param bookType type of book which can be handwritten or printed 
	 * @param bookID id of the book which is determined by library
	 */
	public Book(String bookType, int bookID){
		this.bookType = bookType;
		this.bookID = bookID;
		isTaken = false;
		whoHas = null;
	}

	/**
	 * Returns the book to the library. <p>
	 * @param member the library member who returns the book  
	 */
	public abstract void returnBook(LibraryMember member);

	/**
	 * Gets the type of the book
	 * @return the type of the book
	 */
	public String getBookType() {
		return bookType;
	}

	/**
	 * Getter for the ID info of the book
	 * @return the ID of the book
	 */
	public int getBookID() {
		return bookID;
	}

	/**
	 * Gets info about current availability of the book
	 * @return true if book is taken else returns false
	 */
	public boolean isTaken() {
		return isTaken;
	}

	/**
	 * Sets the updated availability of the book.
	 * @param isTaken the new info about whether the book has been taken by member or not
	 */
	public void setTaken(boolean isTaken) {
		this.isTaken = isTaken;
	}

	/**
	 * Gets id of the library member who has taken the book.
	 * @return the id of the library member who has the book 
	 */
	public LibraryMember getWhoHas() {
		return whoHas;
	}

	/**
	 * Sets the id of the library member who currently has the book
	 * @param whoHas the id of the library member who has taken the book
	 */
	public void setWhoHas(LibraryMember whoHas) {
		this.whoHas = whoHas;
	}

}