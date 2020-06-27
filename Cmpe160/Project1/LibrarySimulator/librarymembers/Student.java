package librarymembers;

import java.util.ArrayList;

import books.Book;

/**
 * @author I.Zeynep Alagoz 
 * Holds properties of student which is one of the library member type.
 */
public class Student extends LibraryMember{
	
	public Student(int id) {
		memberType = "S";
		super.id = id;
		maxNumberOfBooks = 10;
		timeLimit = 20;
		numBooks = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public ArrayList<Book> getTheHistory() {
		return history;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addToHistory(Book readBook) {
		history.add(readBook);
	}
	
}