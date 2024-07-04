package tool.xmodeler.didactic_ml;

public enum LearningUnit {
	
	TOOL_INTRO("Tool Introduction", 0),
	CLASSIFICATION_INSTANTIATION("Classification and instantiation", 1),
	OBJECT_REFERENCES("References between objects: associations and links", 2),
	ATTRIBUTE_MULTIPLICITY("Multiplicity of attributes", 3),
	DEFICIENT_CLASSES("Deficient Classes", 4),
	ATTRIBUTE_TYPES("Types for attributes: primitive types, domain-specific types, and enumerations", 5),
	GENERALIZATION_SPECIALIZATION_I("Generalization/Specialization I: Inheritance and Abstract Classes", 6),
	GENERALIZATION_SPECIALIZATION_II("Generalization/Specialization II: Pitfalls of Specialization and Delegation", 7),
	CIRCLES("Model Circles", 8),
	DERIVED_CONCEPTS("Derivable Attributes and Operations", 9),
	CONSTRAINTS("Custom Constraints using an XOCL-based Language", 10);
	
	private final String prettyName;
	private final int id;
	
	LearningUnit(String prettyName, int id) {
		this.prettyName = prettyName;
		this.id = id;
	}

	public String getPrettyName() {
		return prettyName;
	}

	public int getId() {
		return id;
	}
}