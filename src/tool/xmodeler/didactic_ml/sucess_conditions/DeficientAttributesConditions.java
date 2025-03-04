package tool.xmodeler.didactic_ml.sucess_conditions;

import java.util.ArrayList;
import java.util.List;

import tool.xmodeler.didactic_ml.frontend.task_description_viewer.TaskDescriptionViewer;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;

public class DeficientAttributesConditions extends SuccessCondition {
	int numberOfCorrectAnwsers = 0;
	
	private int[] correctAnwsers = {3,6, 7,10, 13}; //should be age, numberOfTickets, totalPriceOfreservation, isAvailable, availableSeats
	
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
		List selectedAnswers =  taskViewer.getAnwserLV().getSelectionModel().getSelectedIndices();
		numberOfCorrectAnwsers=0;
		for(int i=0; i<selectedAnswers.size();i++) {
		for(int i2=0; i2<correctAnwsers.length;i2++) {
			if(Integer.parseInt(taskViewer.getAnwserLV().getSelectionModel().getSelectedIndices().get(i).toString()) == correctAnwsers[i2]) {
				numberOfCorrectAnwsers++;
				System.err.println(numberOfCorrectAnwsers);
			}
		}
		}
		return numberOfCorrectAnwsers==correctAnwsers.length;
	}
}
