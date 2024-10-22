package tool.xmodeler.didactic_ml.self_assessment_test_tasks.classification_instantiation;

import java.util.HashMap;

import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;

public class ClassificationInstantiationTasks extends SelfAssessmentTestTasks {

	public ClassificationInstantiationTasks() {
		super(SelfAssessmentTest.CLASSIFICATION_INSTANTIATION);
	}
 
	@Override
	public void init() {
//		tasks = Map.ofEntries(
//				Map.entry("BASIC_ONE", 1),
//				Map.entry("BASIC_TWO", 2),
//				Map.entry("BASIC_THREE", 3));
		tasks = new HashMap<String, Integer>();
		tasks.put("BASIC_ONE", 1);
		tasks.put("BASIC_TWO", 2);
		tasks.put("BASIC_THREE", 3);
	}
}