package tool.clients.fmmlxdiagrams;

import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

import java.util.Vector;

public class FmmlxOperation implements FmmlxProperty, Comparable<FmmlxOperation> {
	private final PropertyType propertyType = PropertyType.Operation;

	String name;
	Integer level;
	String type;
	Integer owner;
	private boolean isMonitored;
	String body;
	Vector<String> paramNames;
	Vector<String> paramTypes;


	public FmmlxOperation(String name, Vector<String> paramNames, Vector<String> paramTypes, Integer level, String type, String body, Integer owner, Multiplicity multiplicity, boolean isMonitored) {
		this.name = name;
		this.level = level;
		this.type = type;
		this.owner = owner;
//		this.multiplicity = multiplicity;
		this.isMonitored = isMonitored;
		this.body = body;
		this.paramNames = paramNames;
		this.paramTypes = paramTypes;
	}
	
	public Vector<String> getParamNames() {
		return paramNames;
	}



	public Vector<String> getParamTypes() {
		return paramTypes;
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

	public String getFullString(FmmlxDiagram diagram) {
		String params = "";
		for(int i = 0; i < paramNames.size(); i++) {
			if(!"".equals(params)) params = params+", ";
			params = params + paramNames.get(i)+ ":" + diagram.convertPath2Short(paramTypes.get(i));
		}
		return name + "("+params+"):" + diagram.convertPath2Short(type);
	}

	@Override
	public int compareTo(FmmlxOperation that) {
		if(this.level < that.level) return -1; 
		if(this.level > that.level) return 1;
		return this.name.compareTo(that.name);
	}
}
