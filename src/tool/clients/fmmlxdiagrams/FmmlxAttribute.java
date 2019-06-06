package tool.clients.fmmlxdiagrams;

public class FmmlxAttribute {

	String name;
	Multiplicity multiplicity;
	Integer owner;
	int level;
	String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FmmlxAttribute(String name, int level, String type, Integer owner, String multiplicity) {
		this.name = name;
		this.level = level;
		this.type = type;
		this.multiplicity = Multiplicity.parseMultiplicity(multiplicity);
		this.owner = owner;
	}
}
