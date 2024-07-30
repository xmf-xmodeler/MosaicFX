package tool.xmodeler.didactic_ml.frontend.learning_unit_chooser;

import java.util.List;

import tool.xmodeler.didactic_ml.UserDataProcessor;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;

public enum LearningUnit {
	
	TOOL_INTRO("Tool Introduction", "ToolIntro", 0, true),
	CLASSIFICATION_INSTANTIATION("Classification and instantiation", "ClassificationInstantiation", 1, true),
	OBJECT_REFERENCES("References between objects: associations and links", "", 2, false),
	ATTRIBUTE_MULTIPLICITY("Multiplicity of attributes","", 3, false),
	DEFICIENT_CLASSES("Deficient Classes","", 4, false),
	ATTRIBUTE_TYPES("Types for attributes: primitive types, domain-specific types, and enumerations","", 5, false),
	GENERALIZATION_SPECIALIZATION_I("Generalization/Specialization I: Inheritance and Abstract Classes","", 6, false),
	GENERALIZATION_SPECIALIZATION_II("Generalization/Specialization II: Pitfalls of Specialization and Delegation","", 7, false),
	CIRCLES("Model Circles","", 8, false),
	DERIVED_CONCEPTS("Derivable Attributes and Operations","", 9, false),
	CONSTRAINTS("Custom Constraints using an XOCL-based Language","", 10, false);
	
	private final String prettyName;
	private final int id;
	/**
	 * Defines if the LearningUnit is already implemented. Depending on this value the row is enabled in the table view
	 * of the LearningUnitChooser.
	 */
	private final boolean implemented;
	/**
	 * Defines the folder name where the resources for this learningUnit are defined.
	 */
	private final String pathName;
	
	LearningUnit(String prettyName, String pathName, int id, boolean implemented) {
		this.prettyName = prettyName;
		this.pathName = pathName;
		this.id = id;
		this.implemented = implemented;
	}

	public String getPrettyName() {
		return prettyName;
	}

	public String getPathName() {
		return pathName;
	}

	public int getId() {
		return id;
	}

	public boolean isImplemented() {
		return implemented;
	}

	/**
	 * Check in the user data if all related SelfAssessmentTests are finished
	 * @return true if all related tests are finished by the user
	 */
	boolean isFinished() {
		List<SelfAssessmentTest> relatedTests = SelfAssessmentTest.getRelatedTests(this);
		for (SelfAssessmentTest selfAssessmentTest : relatedTests) {
			if (!UserDataProcessor.userHasFinishedTest(selfAssessmentTest)) {
				return false;
			}
		}
		return true;
	}
}