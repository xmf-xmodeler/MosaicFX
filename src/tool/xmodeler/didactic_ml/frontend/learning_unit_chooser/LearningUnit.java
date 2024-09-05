package tool.xmodeler.didactic_ml.frontend.learning_unit_chooser;

import java.util.List;

import tool.xmodeler.didactic_ml.UserDataProcessor;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;

public enum LearningUnit {
	
	TOOL_INTRO("UML++ Introduction", "ToolIntro", 0, true),
	CLASSIFICATION_INSTANTIATION("Classification and Instantiation", "01_Classification and Instantiation", 1, true),
	OBJECT_REFERENCES("References between Objects: Associations and Links", "02_References between objects", 2, true),
	ATTRIBUTE_MULTIPLICITY("Multiplicity of Attributes","03_Multiplicity of attributes", 3, true),
	DEFICIENT_CLASSES("Deficient Classes","04_Deficient Classes", 4, true),
	ATTRIBUTE_TYPES("Types for Attributes: Default types, Domain-specific types, and Enumerations","05_Types for attributes", 5, true),
	GENERALIZATION_SPECIALIZATION_I("Generalization/Specialization I: Inheritance and Abstract Classes","06_Generalization Specialization I", 6, true),
	GENERALIZATION_SPECIALIZATION_II("Generalization/Specialization II: Pitfalls of Specialization and Delegation","07_Generalization Specialization II", 7, true),
	CIRCLES("Model Circles","08_Model Circles", 8, true),
	DERIVED_CONCEPTS("Derivable Attributes and Operations","09_Derivable Attributes and Operations", 9, true),
	CONSTRAINTS("Custom Constraints using an OCL-based Language","10_Custom Constraints", 10, true);
	
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