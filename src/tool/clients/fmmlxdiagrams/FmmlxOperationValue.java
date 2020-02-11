package tool.clients.fmmlxdiagrams;

import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class FmmlxOperationValue implements FmmlxProperty {
	String name;
	String value;
	private PropertyType propertyType = PropertyType.OperationValue;
	boolean hasRange;
	boolean isInRange;
	
	public FmmlxOperationValue(String name, String value, boolean hasRange, boolean isInRange) {
		super();
		this.name = name;
		this.value = value;
		this.hasRange = hasRange;
		this.isInRange = isInRange;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}


	@Override
	public PropertyType getPropertyType() {
		return propertyType;
	}

	public boolean isInRange() {
		return (!hasRange) || isInRange;
	}
}
