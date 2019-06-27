package tool.clients.fmmlxdiagrams;

import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class FmmlxOperationValue implements FmmlxProperty {
	String name;
	String value;
	private PropertyType propertyType = PropertyType.OperationValue;

	public FmmlxOperationValue(String name, String value) {
		super();
		this.name = name;
		this.value = value;
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
}
