package tool.xmodeler.didactic_ml.learning_unit_managers;

import tool.xmodeler.didactic_ml.frontend.learning_unit_chooser.LearningUnit;

public class LearningUnitManagerFaktory {
	public static LearningUnitManager instance;

	public LearningUnitManagerFaktory(LearningUnitManager instance) {
		LearningUnitManagerFaktory.instance = instance;
	}

	/**
	 * Depending on which constructor was called this function returns another
	 * subtype of class LearningUnitManager. Be aware that you need to extend this
	 * function if you want to add new subclass of {@link LearningUnit}.
	 * 
	 * @return dynamic subtyped instance of LearningUnitManager
	 */
	public synchronized <Manager extends LearningUnitManager> Manager getInstance() {
		switch (LearningUnitManager.learningUnit) {
			case TOOL_INTRO:
				return (Manager) ToolIntroductionManager.class.cast(instance);
	
			case CLASSIFICATION_INSTANTIATION:
				return (Manager) ClassificationInstantiationManager.class.cast(instance);
	
			default:
				throw new RuntimeException("LearningUnitManager instance needs to be first intialized.");
		}
	}
}