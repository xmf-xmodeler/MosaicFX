package tool.xmodeler.didactic_ml.learning_unit_managers;

import tool.xmodeler.didactic_ml.frontend.learning_unit_chooser.LearningUnit;
import tool.xmodeler.didactic_ml.learning_unit_steps.ToolIntroductionTasks;
import tool.xmodeler.didactic_ml.sucess_conditions.ToolIntroductionConditions;

public class ToolIntroductionManager extends LearningUnitManager {

	public ToolIntroductionManager() {
		super("ToolIntroductionABC", "ToolIntroductionDiagramXYZ");
		new ToolIntroductionTasks().init();
		learningUnit = LearningUnit.TOOL_INTRO;
		sucessCondition = new ToolIntroductionConditions();
	}

//	public static ToolIntroductionManager getInstance() {
//		return LearningUnitManager.getInstance(ToolIntroductionManager.class);
//	}	
}