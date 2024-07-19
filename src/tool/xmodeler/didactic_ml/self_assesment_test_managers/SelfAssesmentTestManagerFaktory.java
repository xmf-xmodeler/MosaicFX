package tool.xmodeler.didactic_ml.self_assesment_test_managers;

import tool.xmodeler.didactic_ml.frontend.learning_unit_chooser.LearningUnit;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.classification_instantioation.ClassificationInstantiationManager;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.tool_intro.ToolIntroductionManager;

public class SelfAssesmentTestManagerFaktory {
	public static SelfAssesmentTestManager instance;

	public SelfAssesmentTestManagerFaktory(SelfAssesmentTestManager instance) {
		SelfAssesmentTestManagerFaktory.instance = instance;
	}

	/**
	 * Depending on which constructor was called this function returns another
	 * subtype of class LearningUnitManager. Be aware that you need to extend this
	 * function if you want to add new subclass of {@link LearningUnit}.
	 * 
	 * @return dynamic subtyped instance of LearningUnitManager
	 */
	public synchronized <Manager extends SelfAssesmentTestManager> Manager getInstance() {
		switch (SelfAssesmentTestManager.learningUnit) {
			case TOOL_INTRO:
				return (Manager) ToolIntroductionManager.class.cast(instance);
	
			case CLASSIFICATION_INSTANTIATION:
				return (Manager) ClassificationInstantiationManager.class.cast(instance);
	
			default:
				throw new RuntimeException("LearningUnitManager instance needs to be first intialized.");
		}
	}
}