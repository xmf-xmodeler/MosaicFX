package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.Multiplicity;

public class AddAttributeDialogResult extends DialogResult {
	
	private String name;
	private String type;
	private int level;
	private int classID;
	private Multiplicity multi;

	public AddAttributeDialogResult(int classID, String name, int level, String type, Multiplicity multi) {
		this.classID= classID;
		this.name = name;
		this.level = level;
		this.type =type;
		this.multi=multi;
	}
	
	public int getClassID() {
		return classID;
	}

	public void setClassID(int classID) {
		this.classID = classID;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Multiplicity getMultiplicity() {
		return multi;
	}
	
	public void setMulti(Multiplicity multi) {
		this.multi = multi;
	}
}
