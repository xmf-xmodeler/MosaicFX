package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxObject;

public class ChangeNameDialogResult extends DialogResult {

	private final String type;
	private FmmlxObject object;
	private String oldName;
	private String newName;

	// Result to change class name
	public ChangeNameDialogResult(String type, FmmlxObject object, String newName) {
		this.type = type;
		this.object = object;
		this.newName = newName;
	}

	// Result to change attribute or operation name
	public ChangeNameDialogResult(String type, FmmlxObject object, String oldName, String newName) {
		this.type = type;
		this.object = object;
		this.oldName = oldName;
		this.newName = newName;
	}

	public int getObjectId() {
		return object.getId();
	}

	public String getType() {
		return type;
	}

	public String getNewName() {
		return newName;
	}

	public String getOldName() {
		return oldName;
	}

	public String toString() {
		return ("Type:" + type + " Object: " + object.getName() + " Old: " + oldName + " New: " + newName);
	}
}
