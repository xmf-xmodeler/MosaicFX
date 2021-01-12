package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class FmmlxSlot implements FmmlxProperty {
	private PropertyType propertyType = PropertyType.Slot;
	private FmmlxObject owner;
	private String name;
	private String value;

	public FmmlxSlot(String name, String value, FmmlxObject owner) {
		this.name = name;
		this.value = value;
		this.owner = owner;
	}

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
	
	public String getType(FmmlxDiagram diagram) {
		Vector<FmmlxAttribute> allAttributes = new Vector<>();
		FmmlxObject next = owner;
		while (next != null) {
			allAttributes.addAll(next.getOwnAttributes());
			allAttributes.addAll(next.getOtherAttributes());
			next = diagram.getObjectByPath(next.getOfPath());
		}

		for (FmmlxAttribute attribute : allAttributes) {
			if (attribute.getName().equals(getName()) && attribute.level == owner.level) {
				return attribute.getType();
			}
		}
		
		throw new RuntimeException("Slot type not found");
	}
}
