package tool.xmodeler.didactic_ml.sucess_conditions;

import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxEnum;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.xmodeler.didactic_ml.backend_aux.DiagramConditionChecks;
import tool.xmodeler.didactic_ml.learning_unit_steps.LearningUnitTasks;
import tool.xmodeler.didactic_ml.learning_unit_steps.ToolIntroductionTasks;

public class ClassificationInstantiationConditions extends SuccessCondition {
		
	@Override
	public boolean checkSuccessCondition() {
		switch (LearningUnitTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName())) {
		case 1:
			return true;
		case 2:
			return true;
		case 3:
			return true;
		default:
			throw new IllegalArgumentException("No condition for the precedence defined: " + LearningUnitTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName()) + "in " + LearningUnitTasks.getLearningUnitName());
		}
	}
}