package books;

import interfaces.ReadInLibrary;
import librarymembers.LibraryMember;

/**
 * @author I.Zeynep Alagoz
 *
 */
public class Handwritten extends Book implements ReadInLibrary{


	/**
	 * Constructor of this class.
	 * Calls parents constructor {@link Book#Book(String, int)} and creates object.
	 * @param bookID the ID of the book which is determined by the library 
	 */
	public Handwritten(int bookID) {
		super("H", bookID);
	}

	/** 
	 * {@inheritDoc}
	 * Since currently nobody has the book, sets whoHas as null and isTaken as false. 
	 * @see Book#setWhoHas(LibraryMember whoHas)
	 * @see Book#setTaken(boolean isTaken) 
	 */
	@Override
	public void returnBook(LibraryMember member) {
		setWhoHas(null);
		setTaken(false);
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