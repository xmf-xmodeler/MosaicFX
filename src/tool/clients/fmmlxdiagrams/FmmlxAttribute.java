package tool.clients.fmmlxdiagrams;

public class FmmlxAttribute {

	String name;
	int level;
	String type;
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public FmmlxAttribute(String name, int level, String type, String multiplicity) {

		this.name = name;
		this.level = level;
		this.type = type;
//		this.multiplicity = multiplicity;
	}

}
