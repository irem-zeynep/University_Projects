package librarymembers;

import java.util.ArrayList;

import books.Book;

/**
 * @author I.Zeynep Alagoz 
 * Holds the common properties of the members and related methods. 
 */
public abstract class LibraryMember{

	/**
	 *  the member type which can be academic or student
	 */
	protected String memberType;
	/**
	 * the ID of the library member
	 */
	protected int id;
	/**
	 * max number of books could be simultaneously be borrowed
	 */
	protected int maxNumberOfBooks;
	/**
	 * the limit of time for returning book
	 */
	protected int timeLimit;
	/**
	 * the number of books which are borrowed by the member
	 */
	protected int numBooks;
	/**
	 * the history of the books which are read or borrowed by the member
	 */
	protected ArrayList<Book> history = new ArrayList<Book>();
	
	/**
	 * Getter for the history of the books which are read or borrowed by the member.
	 * @return the history of the books which are read or borrowed by the member
	 */
	public abstract ArrayList<Book> getTheHistory();
	
	/**
	 * Adds the book which is taken by parameter to the book history of the member.
	 * @param readBook the book which is read by the member
	 */
	public abstract void addToHistory(Book readBook);

	/**
	 * Gets the member type (academic or student).
	 * @return the member type
	 */
	public String getMemberType() {
		return memberType;
	}

	/**
	 * Gets ID of the library member.
	 * @return the ID of the library member
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets max number of books could be simultaneously be borrowed. It depends on the type of the library member.
	 * @return max number of books could be simultaneously be borrowed
	 */
	public int getMaxNumberOfBooks() {
		return maxNumberOfBooks;
	}

	/**
	 * Gets the time limit to return the borrowed book .
	 * @return the limit of time for returning book.
	 */
	public int getTimeLimit() {
		return timeLimit;
	}

	
	/**
	 * Getter of the current book number which  are borrowed by the member.
	 * @return the number of books which are borrowed by the member
	 */
	public int getNumBooks() {
		return numBooks;
	}
	
	/** 
	 * Sets the updated number of the borrowed books.
	 * @param numBooks the number of books which are borrowed by the member
	 */
	public void setNumBooks(int numBooks) {
		this.numBooks = numBooks;
	}
	
}