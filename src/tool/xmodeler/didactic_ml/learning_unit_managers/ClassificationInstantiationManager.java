package tool.xmodeler.didactic_ml.learning_unit_managers;

import tool.xmodeler.didactic_ml.frontend.learning_unit_chooser.LearningUnit;
import tool.xmodeler.didactic_ml.learning_unit_tasks.ClassificationInstantiationTasks;
import tool.xmodeler.didactic_ml.sucess_conditions.ClassificationInstantiationConditions;

public class ClassificationInstantiationManager extends LearningUnitManager {

	public ClassificationInstantiationManager() {
		super("ClassificationInstantiationABC", "ClassificationInstantiationDiagramXYZ");
		new ClassificationInstantiationTasks().init();
		learningUnit = LearningUnit.CLASSIFICATION_INSTANTIATION;
		sucessCondition = new ClassificationInstantiationConditions();
	}	
}