package tool.clients.fmmlxdiagrams;

import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

import java.util.Vector;

public class FmmlxOperation implements FmmlxProperty{
	String name;
	Integer level;
	String type;
	Integer owner;
	private boolean isMonitored;


	public FmmlxOperation(String name, Integer level, String type, Integer owner, Multiplicity multiplicity, boolean isMonitored, Vector<Object> args) {
		this.name = name;
		this.level = level;
		this.type = type;
		this.owner = owner;
//		this.multiplicity = multiplicity;
		this.isMonitored = isMonitored;
	}

	public String getName() {
		return name;
	}

	public Integer getLevel() {
		return level;
	}
	
	public String getLevelString() {
		return level>=0?(""+level):" ";
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
		return null;
	}
}
