package tool.clients.fmmlxdiagrams;

public enum DiagramDisplayProperty {
	OPERATIONS("Operations", true),
	OPERATIONVALUES("Operation Values", true),
	SLOTS("Slots", true),
	GETTERSANDSETTERS("Getter & Setter", true),
	DERIVEDOPERATIONS("Derived Operations", true),
	DERIVEDATTRIBUTES("Derived Attributes", true),
	CONSTRAINTS("Constraints", true),
	CONSTRAINTREPORTS("Constraint Reports", true),
	METACLASSNAME("Metaclass name", false),
	CONCRETESYNTAX("Concrete Syntax", true),
	ISSUETABLE("Issue Table", false);
	
	 private final String label;
	 private final boolean defaultValue;

    private DiagramDisplayProperty(String label, boolean defaultValue) {
        this.label = label;
		this.defaultValue = defaultValue;
    }

	public String getLabel() {
		return label;
	}
	
	public boolean getDefaultValue() {
		return defaultValue;
	}
}
