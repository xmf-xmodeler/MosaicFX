package tool.clients.fmmlxdiagrams.dialogs.results;

public class ChangeEnumNameDialogResult {

	String oldName;
	String newName;
	
	
	public ChangeEnumNameDialogResult(String oldName, String newName) {
		super();
		this.oldName = oldName;
		this.newName = newName;
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
