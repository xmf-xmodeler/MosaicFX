package tool.xmodeler.didactic_ml.sucess_conditions;

import tool.xmodeler.didactic_ml.frontend.task_description_viewer.TaskDescriptionViewer;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;

public class DeficientAttributesConditions extends SuccessCondition {
	
	public boolean checkSuccessCondition(TaskDescriptionViewer taskViewer) {
		switch (SelfAssessmentTestTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName())) {
		case 1:
			return true;	//Just press the button
		case 2:
			return checkPhase2();
		case 3:
			return true;		//Just the end button
		}
		return true;
	}
	
	private boolean checkPhase2() {
		
		return true;
	}
}
