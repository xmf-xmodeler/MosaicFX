package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class FmmlxAttribute implements FmmlxProperty {

	String name;
	private Multiplicity multiplicity;
	Integer owner;
	int level;
	String type;
	private PropertyType propertyType = PropertyType.Attribute;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FmmlxAttribute(String name, int level, String type, Integer owner, Multiplicity multiplicity) {
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

	public int getOwnerId() {
		// TODO Auto-generated method stub
		return owner;
	}

	public String getType() {
		// TODO Auto-generated method stub
		return type;
	}

	public Multiplicity getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(Multiplicity multiplicity) {
		this.multiplicity = multiplicity;
	}
}
