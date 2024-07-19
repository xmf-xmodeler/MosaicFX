package tool.xmodeler.didactic_ml.self_assesment_test_managers.classification_instantioation;

import tool.xmodeler.didactic_ml.diagram_preperation_actions.ClassificationInstantiationPreparation;
import tool.xmodeler.didactic_ml.frontend.learning_unit_chooser.LearningUnit;
import tool.xmodeler.didactic_ml.learning_unit_tasks.ClassificationInstantiationTasks;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssesmentTestManager;
import tool.xmodeler.didactic_ml.sucess_conditions.ClassificationInstantiationConditions;

public class ClassificationInstantiationManager extends SelfAssesmentTestManager {

	public ClassificationInstantiationManager() {
		super("ClassificationInstantiationABC", "ClassificationInstantiationDiagramXYZ");
		new ClassificationInstantiationTasks().init();
		learningUnit = LearningUnit.CLASSIFICATION_INSTANTIATION;
		sucessCondition = new ClassificationInstantiationConditions();
		preperationActions = new ClassificationInstantiationPreparation();
	}	
}