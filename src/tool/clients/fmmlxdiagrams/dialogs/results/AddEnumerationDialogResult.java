package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxEnum;

public class AddEnumerationDialogResult extends DialogResult{
	
	private FmmlxEnum enumeration;
	
	public AddEnumerationDialogResult(FmmlxEnum enum1) {
		this.enumeration=enum1;
	}

	public FmmlxEnum getEnumeration() {
		return enumeration;
	}
	
}
