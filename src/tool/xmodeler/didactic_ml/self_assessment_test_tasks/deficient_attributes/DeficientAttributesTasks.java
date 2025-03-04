package tool.xmodeler.didactic_ml.self_assessment_test_tasks.deficient_attributes;

import java.util.Map;

import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;

public class DeficientAttributesTasks extends SelfAssessmentTestTasks {
	
	public DeficientAttributesTasks() {
		super(SelfAssessmentTest.DEFICIENT_ATTRIBUTES);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		tasks = Map.ofEntries(
				Map.entry("Think_About_Inheritance", 1),
				Map.entry("Select_Deficient_Attributes", 2),
				Map.entry("Placeholder_for_now", 3)
				);
		
	}

}
