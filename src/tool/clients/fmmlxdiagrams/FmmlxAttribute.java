package tool.clients.fmmlxdiagrams;

public class FmmlxAttribute {
	
	String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	int level;
	String type;

	public FmmlxAttribute(String name, int level, String type) {
		this.name = name;
		this.level = level;
		this.type = type;
	}

}
