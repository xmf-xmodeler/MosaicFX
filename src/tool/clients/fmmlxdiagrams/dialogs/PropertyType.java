package tool.clients.fmmlxdiagrams.dialogs;

public enum PropertyType {
	Class("Class"),
	Attribute("Attribute"),
	Operation("Operation"),
	OperationValue("Operation value"),
	Association("Association"),
	AssociationInstance("AssociationInstance"),
	Slot("Slot"),
	Selection("Selection");

	private String name;

	private PropertyType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
