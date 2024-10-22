package tool.xmodeler.didactic_ml.frontend.learning_unit_chooser;

import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssesmentTestManager;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.classification_instantioation.ClassificationInstantiationManager;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.genSpec1.GeneralizationSpecializationIManager;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.tool_intro.ToolIntroductionManager;

public class LearningUnitManagerFactory {
	
	  private LearningUnitManagerFactory() {
		    throw new IllegalStateException("Use onyl as static class");
		  }

	public static SelfAssesmentTestManager createLearningUnitManager(SelfAssessmentTest selfAssessmentTest) {
		switch (selfAssessmentTest) {
			case TOOL_INTRO:
				return new ToolIntroductionManager();
			//case CLASSIFICATION_INSTANTIATION:
			//	return new ClassificationInstantiationManager();
			case GENERALIZATION_SPECIALIZATION_I:
				return new GeneralizationSpecializationIManager();
			default:
				throw new IllegalArgumentException(
						"If the learning unit is not represented in this factory maybe its not implemented: "
								+ selfAssessmentTest.getPrettyName());
		}
	}
}