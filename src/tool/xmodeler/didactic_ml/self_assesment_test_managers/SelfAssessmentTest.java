package tool.xmodeler.didactic_ml.self_assesment_test_managers;

import tool.xmodeler.didactic_ml.frontend.learning_unit_chooser.LearningUnit;

/**
 * Class represents all SelfAssesmentTests. Be aware that they must have unique names.
 * Every SelfAssesmentTest have an Attribute prettyName. So you can use SelfAssesmentTest with the same display name.
 */
public enum SelfAssessmentTest {
	
	TOOL_INTRO("Tool Introduction", LearningUnit.TOOL_INTRO, 1),
	CLASSIFICATION_INSTANTIATION("Classification and Instantiation", LearningUnit.CLASSIFICATION_INSTANTIATION, 1);

	
	private final String prettyName;
	private final LearningUnit learningUnit;
	/**
	 * This attribute defines the order for displaying SelfAssesmentTest inside a LearningUnit.
	 * The ordering starts with the value 1.
	 */
	private final int orderNumber;
	
	private SelfAssessmentTest(String prettyName, LearningUnit learningUnit, int orderNumber) {
		this.prettyName = prettyName;
		this.learningUnit = learningUnit;
		this.orderNumber = orderNumber;
	}

	public String getPrettyName() {
		return prettyName;
	}

	public int getOrderNumber() {
		return orderNumber;
	}
}