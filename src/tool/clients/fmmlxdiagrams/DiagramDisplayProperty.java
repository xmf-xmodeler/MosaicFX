package tool.clients.fmmlxdiagrams;

public enum DiagramDisplayProperty {
	OPERATIONS("Operations"),
	OPERATIONVALUES("Operation Values"),
	SLOTS("Slots"),
	GETTERSANDSETTERS("Getter & Setter"),
	DERIVEDOPERATIONS("Derrived Operations"),
	DERIVEDATTRIBUTES("Derrived Attributes"),
	CONSTRAINTS("Constraints"),
	CONSTRAINTREPORTS("Constraint Reports"),
	METACLASSNAME("Metaclass name"),
	CONCRETESYNTAX("Concrete Syntax"),
	ISSUETABLE("Issue Table");
	
	 public final String label;

    private DiagramDisplayProperty(String label) {
        this.label = label;
    }

	public String getLabel() {
		return label;
	}
}