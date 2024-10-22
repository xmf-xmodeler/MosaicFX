package tool.xmodeler.didactic_ml.sucess_conditions;

import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxEnum;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.xmodeler.didactic_ml.backend_aux.DiagramConditionChecks;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.tool_intro.ToolIntroductionTasks;

public class ClassificationInstantiationConditions extends SuccessCondition {
		
	@Override
	public boolean checkSuccessCondition() {
		switch (SelfAssessmentTestTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName())) {
		case 1:
			return true;
		case 2:
			return true;
		case 3:
			return true;
		default:
			throw new IllegalArgumentException("No condition for the precedence defined: " + SelfAssessmentTestTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName()) + "in " + SelfAssessmentTestTasks.getSelfAssessmentTest());
		}
	}
}