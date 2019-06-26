package tool.clients.fmmlxdiagrams;

import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class FmmlxAttribute implements FmmlxProperty {

	String name;
	Multiplicity multiplicity;
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

	public FmmlxAttribute(String name, int level, String type, Integer owner, String multiplicity) {
		this.name = name;
		this.level = level;
		this.type = type;
		this.multiplicity = Multiplicity.parseMultiplicity(multiplicity);
		this.owner = owner;
	}

	@Override
	public PropertyType getPropertyType() {
		return propertyType;
	}
}
