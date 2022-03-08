package tool.clients.fmmlxdiagrams;

import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class Constraint implements FmmlxProperty, Comparable<Constraint>{
	
	String  name;
	Integer level;
	String  bodyRaw;
	String  bodyFull;
	String  reasonRaw;
	String  reasonFull;
	
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
	@Deprecated public String getBodyFull() { return bodyFull;}
	public String getReasonRaw() { return reasonRaw;}
	@Deprecated public String getReasonFull() { return reasonFull;}
	public void setName(String name) { this.name=name;}
	public void setBodyRaw(String bodyRaw) { this.bodyRaw=bodyRaw;}
	public void setBodyFull(String bodyFull) { this.bodyFull=bodyFull;}
	public void setReasonRaw(String reasonRaw) { this.bodyRaw=reasonRaw;}
	public void setReasonFull(String reasonFull) { this.bodyRaw=reasonFull;}
	public void setBodyRaw(Integer level) { this.level=level;}

}
