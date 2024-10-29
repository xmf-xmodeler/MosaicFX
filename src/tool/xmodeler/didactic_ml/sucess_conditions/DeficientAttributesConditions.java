package tool.xmodeler.didactic_ml.sucess_conditions;

import java.util.ArrayList;
import java.util.List;

import tool.xmodeler.didactic_ml.frontend.task_description_viewer.TaskDescriptionViewer;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;

public class DeficientAttributesConditions extends SuccessCondition {
	int numberOfCorrectAnwsers = 0;
	
	private int[] correctAnwsers = {3,6,7};
	
	public boolean checkSuccessCondition(TaskDescriptionViewer taskViewer) {
		switch (SelfAssessmentTestTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName())) {
		case 1:
			return true;	//Just press the button
		case 2:
			return checkPhase2(taskViewer);
		case 3:
			return true;		//Just the end button
		}
		return true;
	}
	
	private boolean checkPhase2(TaskDescriptionViewer taskViewer) {
		List selectedAnwsers =  taskViewer.getAnwserLV().getSelectionModel().getSelectedIndices();
		for(int i=0; i<selectedAnwsers.size();i++) {
			if(Integer.parseInt(taskViewer.getAnwserLV().getSelectionModel().getSelectedIndices().get(i).toString()) == correctAnwsers[i]) {
				numberOfCorrectAnwsers++;
			}
		}
		return numberOfCorrectAnwsers==correctAnwsers.length;
	}
}
