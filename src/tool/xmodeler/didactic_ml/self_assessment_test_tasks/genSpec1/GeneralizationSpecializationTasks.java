package tool.xmodeler.didactic_ml.self_assessment_test_tasks.genSpec1;

import java.util.HashMap;
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
		tasks = new HashMap<String, Integer>();
		tasks.put("Think_About_Inheritance", 1);
		tasks.put("Add_Parent", 2);
		tasks.put("Reflect_About_Inheritance", 3);		
	}

}
