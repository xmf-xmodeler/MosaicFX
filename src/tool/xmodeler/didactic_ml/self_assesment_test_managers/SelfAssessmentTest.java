package tool.xmodeler.didactic_ml.self_assesment_test_managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import tool.xmodeler.didactic_ml.frontend.learning_unit_chooser.LearningUnit;

/**
 * Class represents all SelfAssesmentTests. Be aware that they must have unique names.
 * Every SelfAssesmentTest have an Attribute prettyName. So you can use SelfAssesmentTest with the same display name.
 */
public enum SelfAssessmentTest {
	
	TOOL_INTRO("Tool Introduction", LearningUnit.TOOL_INTRO, 1),
	CLASSIFICATION_INSTANTIATION("Classification and Instantiation 1", LearningUnit.CLASSIFICATION_INSTANTIATION, 1),
	CLASSIFICATION_INSTANTIATION1("Classification and Instantiation 2", LearningUnit.CLASSIFICATION_INSTANTIATION, 2),
	CLASSIFICATION_INSTANTIATION2("Classification and Instantiation 3", LearningUnit.CLASSIFICATION_INSTANTIATION, 3);
	
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

	public LearningUnit getLearningUnit() {
		return learningUnit;
	}
	
	/**
	 * Returns all SelfAssessmentTest for a specific Learning Unit
	 * @param LearningUnit for which the tests are returned
	 * @return sorted list of test. Order number is used to sort them.
	 */
	public static List<SelfAssessmentTest> getTestsForLearningUnit(LearningUnit lu) {
		ArrayList<SelfAssessmentTest> testList = new ArrayList<>();
		for (SelfAssessmentTest test : SelfAssessmentTest.values()) {
			if (test.getLearningUnit().equals(lu)) {
				testList.add(test);
			}
		}
		testList.sort(Comparator.comparing(SelfAssessmentTest::getOrderNumber));
		return testList;
	}
}