package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.Enum;

public class AddEnumerationDialogResult extends DialogResult{
	
	private Enum enumeration;
	
	public AddEnumerationDialogResult(Enum enum1) {
		this.enumeration=enum1;
	}

	public Enum getEnumeration() {
		return enumeration;
	}
	
}
