package tool.xmodeler.didactic_ml.self_assessment_test_tasks.genSpec1;

import java.util.Map;

import tool.xmodeler.didactic_ml.diagram_preperation_actions.GeneralizationSpecializationPreparation;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssesmentTestManager;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;

public class GeneralizationSpecializationTasks extends SelfAssessmentTestTasks {

	public GeneralizationSpecializationTasks() {
		super(SelfAssessmentTest.GENERALIZATION_SPECIALIZATION_I);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		tasks = Map.ofEntries(
				Map.entry("ADD_PARENT", 1)
				);
		
	}

}
