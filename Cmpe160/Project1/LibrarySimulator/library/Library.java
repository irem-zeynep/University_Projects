package library;

import java.util.Scanner;

import books.*;
import librarymembers.Academic;
import librarymembers.LibraryMember;
import librarymembers.Student;

/**
 * @author I. Zeynep Alagoz
 * Read the remaining parts of the input line and executes the actions with other classes help.                      
 *
 */
public class Library{

	/**
	 * Object to read the informations from the file.
	 */
	Scanner infoScan;
	/**
	 * the number of the books in library
	 */
	private static int booksInLibrary = 0;
	/**
	 * the array which stores IDs of the library's book
	 */
	private Book[] books = new Book[(int) 1E+6];
	/**
	 * the number of the members in library 
	 */
	private static int membersInLibrary = 0;
	/**
	 * the array which stores IDs of the library members
	 */
	private LibraryMember[] members = new LibraryMember[(int) 1E+6]; 
	/**
	 * The total fee which is paid to the library
	 */
	private static int totalFee = 0;

	/**
	 * Constructor of the class.
	 * @param scanner  scans the file to read informations about actions.
	 */
	public Library(Scanner scanner) {
		infoScan = scanner;
	}

	/**
	 * Gets the total fee which is paid to the library.
	 * @return The total fee which is paid to the library
	 */
	public static int getTotalFee() {
		return totalFee;
	}


	/**
	 * Gets the number of the book in the library.
	 * @return the number of the books in library 
	 */
	public static int getBooksInLibrary() {
		return booksInLibrary;
	}

	/**
	 * Getter for the book array.
	 * @return the array which stores IDs of the library's book
	 */
	public Book[] getBooks() {
		return books;
	}

	/**
	 * Gets the number of the members in the library.
	 * @return the number of the members in library 
	 */
	public static int getMembersInLibrary() {
		return membersInLibrary;
	}

	/**
	 * Getter for the member array.
	 * @return the array which stores IDs of the library members
	 */
	public LibraryMember[] getMembers() {
		return members;
	}

	/**
	 * Reads the book's type from the file and creates a book object which belongs to that type.
	 * Then increments the number of books in the library.
	 */
	public void addBook() {
		String bookType = infoScan.next();
		if(booksInLibrary  < 1E+6) {
			books[booksInLibrary] = bookType.equals("P") ? new Printed(booksInLibrary+1) : 
				new Handwritten(booksInLibrary+1);
			booksInLibrary++;
		}
	}

	/**
	 * Reads the member's type from the file and creates a member object belongs to that type
	 * Then increments the number of library members.
	 */
	public void addMember() {
		String memberType = infoScan.next();

		if(membersInLibrary  < 1E+6) {
			members[membersInLibrary] = memberType.equals("S") ? new Student(membersInLibrary+1) : 
				new Academic(membersInLibrary+1);
			membersInLibrary++;
		}
	}

	/**
	 * Reads the book's ID and member's ID from the file and checks whether these informations provides the
	   necessary conditions for borrowing book. If it does calls {@link printed#borrowBook(String, int)}.
	 * Then adds the book to the member's book history.
	 * @param tick represents the time tick that book is borrowed
	 */
	public void borrowBook(int tick) {
		int idOfBook = infoScan.nextInt();
		int idOfMember = infoScan.nextInt();

		Book currentBook = books[idOfBook-1];
		LibraryMember currentMember = members[idOfMember-1];

		if(currentBook != null && currentMember != null && !currentBook.isTaken() &&
				currentBook.getBookType().equals("P") && 
				currentMember.getNumBooks() < currentMember.getMaxNumberOfBooks()) {


			for(Book book : books) {
				if(book != null && book.getBookID() != idOfBook && book.getBookType().equals("P") && book.isTaken() && 
						book.getWhoHas().equals(currentMember) && ((Printed) book).getDeadLine() < tick) {
					return;
				}
			}

			((Printed) currentBook).borrowBook(currentMember, tick);

			if(!currentMember.getTheHistory().contains(currentBook)) {
				currentMember.addToHistory(currentBook);
			}
		}

	}

	/**
	 * Reads the book's ID and member's ID from the file and checks whether these informations provides the
	   necessary conditions for returning book.
	 * When these conditions are satisfied, member pays fee if deadline is missed.
	 * Finally returns the book by calling  {@link Printed#returnBook(LibraryMember member)} or {@link Handwritten#returnBook(LibraryMember member)}
	 * @param tick represents the time tick that book is borrowed
	 */
	public void returnBook(int tick) {
		int idOfBook = infoScan.nextInt();
		int idOfMember = infoScan.nextInt();

		Book currentBook = books[idOfBook-1];
		LibraryMember currentMember = members[idOfMember-1];

		if(currentBook != null && currentMember != null && currentBook.isTaken() &&
				currentBook.getWhoHas().equals(currentMember)) {

			if(currentBook.getBookType().equals("P")) {
				if(((Printed) currentBook).getDeadLine() < tick) {
					totalFee = totalFee + tick - ((Printed) currentBook).getDeadLine();
				}
				if(((Printed) currentBook).getDeadLine() > 0) {
					currentMember.setNumBooks(currentMember.getNumBooks() - 1);
				}
				((Printed) currentBook).returnBook(currentMember);
			} else {
				((Handwritten) currentBook).returnBook(currentMember);
			}

		}
	}

	/**
	 * Reads the book's ID and and member's ID from the file.
	 * If deadline is not missed, extends the deadline of the book for the borrower.
	 * @param tick represents the time tick that book is borrowed
	 * @see Printed#extend(LibraryMember, int)
	 */
	public void extendBook(int tick) {
		int idOfBook = infoScan.nextInt();
		int idOfMember = infoScan.nextInt();

		Book currentBook = books[idOfBook-1];
		LibraryMember currentMember = members[idOfMember-1];

		if(currentBook != null && currentMember != null && currentBook.isTaken() &&
				currentBook.getBookType().equals("P") && !((Printed) currentBook).isExtended() && 
				currentBook.getWhoHas().equals(currentMember) && ((Printed) currentBook).getDeadLine() >= tick) {
			((Printed) currentBook).extend(currentMember, tick);
		}

	}

	/**
	 * Reads the book's ID and member's ID from the file and if necessary conditions are satisfied,
	   calls {@link Printed#readBook(LibraryMember)} or {@link Handwritten#readBook(LibraryMember)} (depends on the book's type).
	 * Then adds the book to the member's book history.
	 */
	public void readInLibrary() {
		int idOfBook = infoScan.nextInt();
		int idOfMember = infoScan.nextInt();

		Book currentBook = books[idOfBook-1];
		LibraryMember currentMember = members[idOfMember-1];

		if(currentBook != null && currentMember != null && !currentBook.isTaken() &&
				(currentBook.getBookType().equals("P") || currentMember.getMemberType().equals("A"))) {

			if(currentBook.getBookType().equals("P")) {
				((Printed) currentBook).readBook(currentMember);
			} else {
				((Handwritten) currentBook).readBook(currentMember);
			}

			if(!currentMember.getTheHistory().contains(currentBook)) {
				currentMember.addToHistory(currentBook);
			}

		}
	}

}