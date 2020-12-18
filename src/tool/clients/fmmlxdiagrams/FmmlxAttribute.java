package tool.clients.fmmlxdiagrams;

import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class FmmlxAttribute implements FmmlxProperty, Comparable<FmmlxAttribute>{

	String name;
	private Multiplicity multiplicity;
	String owner;
	int level;
	String type;
	private PropertyType propertyType = PropertyType.Attribute;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FmmlxAttribute(String name, int level, String type, String owner, Multiplicity multiplicity) {
		this.name = name;
		this.level = level;
		this.type = type;
		this.multiplicity = multiplicity;
		this.owner = owner;
	}

	@Override
	public PropertyType getPropertyType() {
		return propertyType;
	}

	public int getLevel() {
		return level;
	}

	public String getOwnerId() {
		return owner;
	}

	public String getType() {
		return type;
	}
	
	public String getTypeShort() {
		if(type.startsWith("Auxiliary::")) return type.substring("Auxiliary::".length());
		return type;
	}

	@Override
	public int compareTo(FmmlxAttribute that) {
		if(this.level < that.level) return -1;
		if(this.level > that.level) return 1;
		return this.name.compareTo(that.name);
	}

	public Multiplicity getMultiplicity() {
		return multiplicity;
	}
//
//	public void setMultiplicity(Multiplicity multiplicity) {
//		this.multiplicity = multiplicity;
//	}
}
