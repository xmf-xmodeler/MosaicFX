package tool.xmodeler.didactic_ml.frontend.learning_unit_chooser;

import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssesmentTestManager;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.classification_instantioation.ClassificationInstantiationManager;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.tool_intro.ToolIntroductionManager;

public class LearningUnitManagerFactory {
	
	  private LearningUnitManagerFactory() {
		    throw new IllegalStateException("Use onyl as static class");
		  }

	public static SelfAssesmentTestManager createLearningUnitManager(LearningUnit learningUnit) {
		switch (learningUnit) {
			case TOOL_INTRO:
				return new ToolIntroductionManager();
			case CLASSIFICATION_INSTANTIATION:
				return new ClassificationInstantiationManager();
			default:
				throw new IllegalArgumentException(
						"If the learning unit is not represented in this factory maybe its not implemented: "
								+ learningUnit.getPrettyName());
		}
	}
}