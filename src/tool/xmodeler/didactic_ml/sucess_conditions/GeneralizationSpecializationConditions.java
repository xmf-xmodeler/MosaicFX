package tool.xmodeler.didactic_ml.sucess_conditions;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;

public class GeneralizationSpecializationConditions extends SuccessCondition {

	@Override
	public boolean checkSuccessCondition() {
		switch (SelfAssessmentTestTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName())) {
		case 1:
			return true;	//Just press the button
		case 2:
			return checkParent();
		case 3:
			return true;		//Just the end button
		}
		return true;
	}
	
	private boolean checkParent() {	//not checking for horse because that one is deemed correct either way
		boolean correctParent = false;
		FmmlxObject car = diagram.getObjectByName("Car");
		FmmlxObject train = diagram.getObjectByName("Train");
		FmmlxObject vehicle = diagram.getObjectByName("Vehicle");
		if(car.getAllAncestors().get(0)==vehicle && train.getAllAncestors().get(0)==vehicle){
			correctParent = true;
		}
		return correctParent;
	}

}
