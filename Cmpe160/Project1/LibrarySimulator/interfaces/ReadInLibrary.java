package interfaces;

import librarymembers.LibraryMember;

/**
 * @author I.Zeynep Alagoz
 *
 */
public interface ReadInLibrary {
	
	/**
	 * Member reads the in the library.
	 * Since the book is not borrowed by the member, there is no deadline.
	 * @param member the library member who reads the book in the library
	 */
	public void readBook(LibraryMember member);
	
}
