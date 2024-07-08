package tool.xmodeler.didactic_ml.frontend.learning_unit_chooser;

import tool.xmodeler.didactic_ml.learning_unit_managers.LearningUnitManager;
import tool.xmodeler.didactic_ml.learning_unit_managers.ToolIntroductionManager;

public class LearningUnitManagerFactory {

	public static LearningUnitManager createLearningUnitManager(LearningUnit learningUnit) {
		switch (learningUnit) {
		case TOOL_INTRO:
			return new ToolIntroductionManager();
		default:
			throw new IllegalArgumentException("If the learning unit is not represented in this factory maybe its not implemented: " + learningUnit.getPrettyName());
		}
	}
}