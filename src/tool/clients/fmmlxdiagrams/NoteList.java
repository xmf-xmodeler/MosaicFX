package tool.clients.fmmlxdiagrams;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Is a helper class to organize Notes of a diagram. Offers further management operations and extends an arrayList. So you can also use it in the classical way to operate with lists.
 */
public class NoteList extends ArrayList<Note> {
	
	public Note getNote(int noteId) {
		for (Note note : this) {
			if (note.getId() == noteId) {
				return note;
			}
		}
		throw new NoSuchElementException("Diagram does not contain Note with " + noteId);
	}

	public boolean contain(int noteId) {
		for (Note note : this) {
			if (note.getId() == noteId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if all notes in the list are hidden
	 * @return true if all nodes are hidden false in the other case
	 */
	public boolean allhidden() {
		boolean allHidden = true;
		for (Note note : this) {
			if (!note.isHidden()) {
				allHidden = false;
			}
		}
		return allHidden;
	}
}
