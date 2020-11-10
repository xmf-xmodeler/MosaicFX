package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxEnum;

public class EditEnumerationDialogResult {
	
	private String oldEnumName;
	private FmmlxEnum newEditedEnum;
	
	public String getEnumName() {
		return oldEnumName;
	}

	public void setEnumName(String enumName) {
		this.oldEnumName = enumName;
	}

	public FmmlxEnum getNewEditedEnum() {
		return newEditedEnum;
	}

	public void setNewEditedEnum(FmmlxEnum newEditedEnum) {
		this.newEditedEnum = newEditedEnum;
	}

	public EditEnumerationDialogResult(String enumName, FmmlxEnum newEditedEnum) {
		super();
		this.oldEnumName = enumName;
		this.newEditedEnum = newEditedEnum;
	}
	
	

}
