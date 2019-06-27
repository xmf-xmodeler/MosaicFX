package tool.clients.fmmlxdiagrams;

import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class FmmlxSlot implements FmmlxProperty {
	private PropertyType propertyType = PropertyType.Slot;

	public FmmlxSlot(String name, String value) {
		this.name = name;
		this.value = value;
	}

	private String name = "TestSlot";
	private String value = "SlotValue";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public PropertyType getPropertyType() {
		return propertyType;
	}
}
