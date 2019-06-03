package tool.clients.fmmlxdiagrams;

public class FmmlxOperation {
	String name;
	Integer level;
	String type;
	Integer owner;

	public FmmlxOperation(String name, Integer level, String type, Integer owner, String multiplicity) {
		this.name = name;
		this.level = level;
		this.type = type;
		this.owner = owner;
//		this.multiplicity = multiplicity;
	}

	public String getName() {
		return name;
	}

	public Integer getLevel() {
		return level;
	}

	public String getType() {
		return type;
	}

	public Integer getOwner() {
		return -1;
	}
}
