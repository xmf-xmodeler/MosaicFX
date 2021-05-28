package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxEnum;

public class ChangeEnumItemNameDialogResult {
	
	String oldName;
	String newName;
	String enumName;
	
	public ChangeEnumItemNameDialogResult(String enumName, String oldName, String newName) {
		super();
		this.oldName = oldName;
		this.newName = newName;
		this.enumName=enumName;
	}
	
	public String getEnumName() {
		return enumName;
	}
	
	public String getNewName() {
		return newName;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}
}
