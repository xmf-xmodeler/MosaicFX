package tool.xmodeler.didactic_ml.self_assesment_test_managers.deficient_attributes;

import tool.xmodeler.didactic_ml.diagram_preperation_actions.ClassificationInstantiationPreparation;
import tool.xmodeler.didactic_ml.diagram_preperation_actions.DeficientAttributesPreparation;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssesmentTestManager;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.classification_instantiation.ClassificationInstantiationTasks;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.deficient_attributes.DeficientAttributesTasks;
import tool.xmodeler.didactic_ml.sucess_conditions.ClassificationInstantiationConditions;
import tool.xmodeler.didactic_ml.sucess_conditions.DeficientAttributesConditions;

public class DeficientAttributesManager extends SelfAssesmentTestManager {
	public DeficientAttributesManager() {
		super("DeficientAttributesABC", "DeficientAttributesDiagramXYZ");
		new DeficientAttributesTasks().init();
		selfAssessmentTest = SelfAssessmentTest.DEFICIENT_ATTRIBUTES;
		sucessCondition = new DeficientAttributesConditions();
		preperationActions = new DeficientAttributesPreparation();
	}	
}
