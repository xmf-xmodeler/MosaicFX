package tool.clients.fmmlxdiagrams;

import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

import java.util.Vector;

public class FmmlxOperation implements FmmlxProperty, Comparable<FmmlxOperation> {
	private final PropertyType propertyType = PropertyType.Operation;

	private String name;
	private int level;
	private String type;
	private String owner;
	private boolean isMonitored;
	private String body;
	private Vector<String> paramNames;
	private Vector<String> paramTypes;
	private boolean delegateToClassAllowed;


	public FmmlxOperation(String name, Vector<String> paramNames, Vector<String> paramTypes, Integer level, String type, String body, String owner, Multiplicity multiplicity, boolean isMonitored, boolean delegateToClassAllowed) {
		this.name = name;
		this.level = level;
		this.type = type;
		this.owner = owner;
//		this.multiplicity = multiplicity;
		this.delegateToClassAllowed = delegateToClassAllowed;
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

	public String getOwner() {
		return owner;
	}

	public boolean isMonitored() {
		return isMonitored;
	}

	public boolean isDelegateToClassAllowed() {
		return delegateToClassAllowed;
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

	public String getFullString(AbstractPackageViewer diagram) {
		String params = "";
		for(int i = 0; i < paramNames.size(); i++) {
			if(!"".equals(params)) params = params+", ";
			params = params + paramNames.get(i)+ " : " + diagram.convertPath2Short(paramTypes.get(i));
		}
		return name + "("+params+") : " + diagram.convertPath2Short(type);
	}

	@Override
	public int compareTo(FmmlxOperation that) {
		if(this.level < that.level) return -1; 
		if(this.level > that.level) return 1;
		return this.name.compareTo(that.name);
	}
}
