package tool.clients.fmmlxdiagrams;

import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class Constraint implements FmmlxProperty, Comparable<Constraint>{
	
	final String  name;
	final Integer level;
	final String  bodyRaw;
	final String  bodyFull;
	final String  reasonRaw;
	final String  reasonFull;

	public Constraint(String name, Integer level, 
			String bodyRaw,   String bodyFull, 
			String reasonRaw, String reasonFull) {
		this.name = name;
		this.level = level;
		this.bodyRaw = bodyRaw;
		this.bodyFull = bodyFull;
		this.reasonRaw = reasonRaw;
		this.reasonFull = reasonFull;
	}

	@Override public int compareTo(Constraint that) { return this.name.compareTo(that.name); }
	@Override public PropertyType getPropertyType() { return PropertyType.Constraint; }
	@Override public String getName() { return name;}
	public int getLevel() { return level;}
	public String getBodyRaw() { return bodyRaw;}
	public String getBodyFull() { return bodyFull;}
	public String getReasonRaw() { return reasonRaw;}
	public String getReasonFull() { return reasonFull;}

}
