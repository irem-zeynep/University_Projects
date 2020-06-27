package librarymembers;

import java.util.ArrayList;

import books.Book;

/**
 * @author I.Zeynep Alagoz 
 * Holds properties of academic which is one of the library member type.
 */
public class Academic extends LibraryMember{
	

	/**
	 * Constructor of the class
	 * Initializes the fields according to academic member's properties.
	 * @param id the ID of the library member
	 */
	public Academic(int id) {
		memberType = "A";
		super.id = id;
		maxNumberOfBooks = 20;
		timeLimit = 50;
		numBooks = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Book> getTheHistory() {
		return history;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addToHistory(Book readBook) {
		history.add(readBook);
	}


}