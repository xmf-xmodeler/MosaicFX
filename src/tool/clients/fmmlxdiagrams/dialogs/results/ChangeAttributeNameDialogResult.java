package tool.clients.fmmlxdiagrams.dialogs.results;

public class ChangeAttributeNameDialogResult {
	
	private String oldName;
	private String newName;
	private int classID;

	public ChangeAttributeNameDialogResult(int classID, String oldName, String newName ) {
		
	}
	
	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public int getClassID() {
		return classID;
	}

	public void setClassID(int classID) {
		this.classID = classID;
	}


}
