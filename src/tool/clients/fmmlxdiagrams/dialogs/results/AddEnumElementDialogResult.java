package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.EnumElement;

public class AddEnumElementDialogResult extends DialogResult{

	private String name;
	
	public AddEnumElementDialogResult(String name) {
		super();
		this.name = name;
	}

	public EnumElement convertToElement() {
		return new EnumElement(name);
	}

	public String getName() {
		return name;
	}

}
