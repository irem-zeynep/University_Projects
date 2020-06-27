package books;

import interfaces.Borrow;
import interfaces.ReadInLibrary;
import librarymembers.LibraryMember;

/**
 * @author I.Zeynep Alagoz <p>
 * Properties of printed book are implemented
 *
 */
public class Printed extends Book implements ReadInLibrary, Borrow{
	

	/**
	 * Holds the deadline of the book.
	 */
	private int deadLine = 0;
	/**
	 * The variable that holds whether the deadline of the book is extended
	 */
	private boolean isExtended = false;

	/**
	 * Constructor of the class.
	 * Calls parent's constructor {@link Book#Book(String, int)} and creates new object 
	 * @param bookID the ID of the which is given by the library
	 */
	public Printed(int bookID) {
		super("P", bookID);
	}

	/** 
	 * {@inheritDoc}
	 * Since currently nobody has the book; initializes the deadline as zero, isExtended as false
	   and sets the whoHas as null, isTaken as false. 
	 * @see Book#setWhoHas(LibraryMember whoHas)
	 * @see Book#setTaken(boolean isTaken)
	 */
	@Override
	public void returnBook(LibraryMember member) {
		setWhoHas(null);
		setTaken(false);
		deadLine = 0;
		isExtended = false;
	}
	
	/**
	 * Library member borrows the desired printed book.
	 * Sets whoHas and deadline according to parameters and sets isTaken as true. 
	 * Increments the number of the book which is borrowed by the member. 
	 * @param member the library member who borrows a desired book.
	 * @param tick represents the time tick that event occurs.
	 * @see Book#setWhoHas(LibraryMember whoHas)
	 * @see Book#setTaken(boolean isTaken)
	 * 
	 */
	@Override
	public void borrowBook(LibraryMember member, int tick) {
		setWhoHas(member);
		setTaken(true);
		deadLine = tick + member.getTimeLimit();
		member.setNumBooks(member.getNumBooks() + 1);
	}

	/**
	 * If the deadline is not missed yet and the deadline of the the book is not extended before, 
	   this method provides an opportunity to extend the deadline of the book by doubling the time limit.
	 * Also sets isExtented as true.
	 * @param member the library member who wants to extends the deadline
 	 * @param tick represents the time tick that event occurs.
	 */
	@Override
	public void extend(LibraryMember member, int tick) {
		deadLine += member.getTimeLimit();
		isExtended = true;
	}

	/**
	 * Member reads the book in the library.
	 * Since the book is not borrowed by the member, there is no deadline.
	 * Sets whoHas according to parameter and isTaken as true. 
	 * @param member the library member who reads the book in the library
	 * @see Book#setWhoHas(LibraryMember whoHas)
	 * @see Book#setTaken(boolean isTaken)
	 */
	@Override
	public void readBook(LibraryMember member) {
		setWhoHas(member);
		setTaken(true);
	}
	
	/**
	 * Getter for deadline of the book.
	 * @return the deadline of the book
	 */
	public int getDeadLine() {
		return deadLine;
	}

	/**
	 * Gets info about whether the deadline of the book is extended by its borrower or not.
	 * @return true if it's deadline is extended else returns false
	 */
	public boolean isExtended() {
		return isExtended;
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String getBookType() {
		return super.getBookType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getBookID() {
		return super.getBookID();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTaken() {
		return super.isTaken();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTaken(boolean isTaken) {
		super.setTaken(isTaken);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LibraryMember getWhoHas() {
		return super.getWhoHas();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWhoHas(LibraryMember whoHas) {
		super.setWhoHas(whoHas);
	}

}