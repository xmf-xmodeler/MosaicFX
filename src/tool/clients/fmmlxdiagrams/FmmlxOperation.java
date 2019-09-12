package tool.clients.fmmlxdiagrams;

import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

import java.util.Vector;

public class FmmlxOperation implements FmmlxProperty {
	private final PropertyType propertyType = PropertyType.Operation;

	String name;
	Integer level;
	String type;
	Integer owner;
	private boolean isMonitored;
	String body;


	public FmmlxOperation(String name, Integer level, String type, String body, Integer owner, Multiplicity multiplicity, boolean isMonitored, Vector<Object> args) {
		this.name = name;
		this.level = level;
		this.type = type;
		this.owner = owner;
//		this.multiplicity = multiplicity;
		this.isMonitored = isMonitored;
		this.body = body;
	}

	public String getName() {
		return name;
	}

	public Integer getLevel() {
		return level;
	}

	public String getLevelString() {
		return level >= 0 ? ("" + level) : " ";
	}

	public String getType() {
		return type;
	}

	public Integer getOwner() {
		return owner;
	}

	public boolean isMonitored() {
		return isMonitored;
	}

	@Override
	public PropertyType getPropertyType() {
		return propertyType;
	}

	public String getBody() {
		if (body == null) {
			return "";
		} else {
			return body;
		}
	}
}
