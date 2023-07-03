package tool.clients.fmmlxdiagrams;

public enum DiagramsDisplayProperty {
	OPERATIONS("Operations"),
	OPERATIONVALUES("Operation Values"),
	SLOTS("Slots"),
	GETTERSANDSETTERS("Getter & Setter"),
	DERIVEDOPERATIONS("Derived Operations"),
	DERIVEDATTRIBUTES("Derived Attributes"),
	CONSTRAINTS("Constraints"),
	CONSTRAINTREPORTS("Constraint Reports"),
	METACLASSNAME("Metaclass name"),
	CONCRETESYNTAX("Concrete Syntax"),
	ISSUETABLE("Issue Table");
	
	 public final String label;

    private DiagramsDisplayProperty(String label) {
        this.label = label;
    }

	public String getLabel() {
		return label;
	}
}
