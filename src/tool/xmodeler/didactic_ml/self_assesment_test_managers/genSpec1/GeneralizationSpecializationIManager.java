package tool.xmodeler.didactic_ml.self_assesment_test_managers.genSpec1;

import tool.xmodeler.didactic_ml.diagram_preperation_actions.GeneralizationSpecializationPreparation;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssesmentTestManager;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.tool_intro.ToolIntroductionTasks;
import tool.xmodeler.didactic_ml.sucess_conditions.GeneralizationSpecializationConditions;

public class GeneralizationSpecializationIManager extends SelfAssesmentTestManager {
		public GeneralizationSpecializationIManager() {
			super();
			new ToolIntroductionTasks().init();
			selfAssessmentTest = SelfAssessmentTest.GENERALIZATION_SPECIALIZATION_I;
			sucessCondition = new GeneralizationSpecializationConditions();
			preperationActions = new GeneralizationSpecializationPreparation();
		}
	}
