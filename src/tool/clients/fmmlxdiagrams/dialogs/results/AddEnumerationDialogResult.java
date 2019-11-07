package tool.clients.fmmlxdiagrams.dialogs.results;

import java.util.LinkedList;

public class AddEnumerationDialogResult {
	
	private String name;
	private LinkedList<String> elements;
	
	public AddEnumerationDialogResult(String name, LinkedList<String> elements) {
		super();
		this.name = name;
		this.elements = elements;
	}
	
	public String getName() {
		return name;
	}

	public LinkedList<String> getElements() {
		return elements;
	}
}
