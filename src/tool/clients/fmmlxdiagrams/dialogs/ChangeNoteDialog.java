package tool.clients.fmmlxdiagrams.dialogs;

import tool.clients.fmmlxdiagrams.Note;

public class ChangeNoteDialog extends NoteCreationDialog {

	public ChangeNoteDialog(Note note) {
		super();
		setTitle("Change Note");
		getDialogPane().setHeaderText("Note " + note.getId());
		contentTextField.setText(note.getContent());
		colorPicker.setValue(note.getNoteColor());
	}
}