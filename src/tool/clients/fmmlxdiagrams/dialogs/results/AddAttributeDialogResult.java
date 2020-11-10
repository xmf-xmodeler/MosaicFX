package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.Multiplicity;

public class AddAttributeDialogResult {
	
	public final String name;
	public final String type;
	public final int level;
	public final String className;
	public final Multiplicity multi;

	public AddAttributeDialogResult(String className, String name, int level, String type, Multiplicity multi) {
		this.className= className;
		this.name = name;
		this.level = level;
		this.type =type;
		this.multi=multi;
	}
}
