package tool.xmodeler.didactic_ml.self_assesment_test_managers.tool_intro;

import tool.xmodeler.didactic_ml.diagram_preperation_actions.ToolIntroductionPreparation;
import tool.xmodeler.didactic_ml.frontend.learning_unit_chooser.LearningUnit;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssesmentTestManager;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.tool_intro.ToolIntroductionTasks;
import tool.xmodeler.didactic_ml.sucess_conditions.ToolIntroductionConditions;

public class ToolIntroductionManager extends SelfAssesmentTestManager {

	public ToolIntroductionManager() {
		super("ToolIntroductionABC", "ToolIntroductionDiagramXYZ");
		new ToolIntroductionTasks().init();
		selfAssessmentTest = SelfAssessmentTest.TOOL_INTRO;
		sucessCondition = new ToolIntroductionConditions();
		preperationActions = new ToolIntroductionPreparation();
	}
}