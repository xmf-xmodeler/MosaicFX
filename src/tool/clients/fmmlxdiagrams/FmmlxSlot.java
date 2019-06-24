package tool.clients.fmmlxdiagrams;

public class FmmlxSlot {
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
}
