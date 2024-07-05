package tool.xmodeler.didactic_ml.frontend.learning_unit_chooser;

public enum LearningUnit {
	
	TOOL_INTRO("Tool Introduction", 0, true),
	CLASSIFICATION_INSTANTIATION("Classification and instantiation", 1, false),
	OBJECT_REFERENCES("References between objects: associations and links", 2, false),
	ATTRIBUTE_MULTIPLICITY("Multiplicity of attributes", 3, false),
	DEFICIENT_CLASSES("Deficient Classes", 4, false),
	ATTRIBUTE_TYPES("Types for attributes: primitive types, domain-specific types, and enumerations", 5, false),
	GENERALIZATION_SPECIALIZATION_I("Generalization/Specialization I: Inheritance and Abstract Classes", 6, false),
	GENERALIZATION_SPECIALIZATION_II("Generalization/Specialization II: Pitfalls of Specialization and Delegation", 7, false),
	CIRCLES("Model Circles", 8, false),
	DERIVED_CONCEPTS("Derivable Attributes and Operations", 9, false),
	CONSTRAINTS("Custom Constraints using an XOCL-based Language", 10, false);
	
	private final String prettyName;
	private final int id;
	/**
	 * Defines if the LearningUnit is already implemented. Depending on this value the row is enabled in the table view
	 * of the LearningUnitChooser.
	 */
	private final boolean implemented;
	
	LearningUnit(String prettyName, int id, boolean implemented) {
		this.prettyName = prettyName;
		this.id = id;
		this.implemented = implemented;
	}

	public String getPrettyName() {
		return prettyName;
	}

	public int getId() {
		return id;
	}

	public boolean isImplemented() {
		return implemented;
	}
}