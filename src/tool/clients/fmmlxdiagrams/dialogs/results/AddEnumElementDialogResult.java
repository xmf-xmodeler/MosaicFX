package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.EnumElement;

public class AddEnumElementDialogResult extends DialogResult{

	String name;
	
	public AddEnumElementDialogResult(String name) {
		super();
		this.name = name;
	}

	public EnumElement convertToElement() {
		// TODO Auto-generated method stub
		return new EnumElement(name);
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

}
