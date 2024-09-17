package tool.xmodeler.didactic_ml.sucess_conditions;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.xmodeler.didactic_ml.backend_aux.DiagramConditionChecks;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;

public class GeneralizationSpecializationConditions extends SuccessCondition {
	
	FmmlxObject car;
	FmmlxObject train;
	FmmlxObject bus;
	FmmlxObject horse;
	FmmlxObject vehicle;
	
	@Override
	public boolean checkSuccessCondition() {
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
		car = diagram.getObjectByName("Car");
		train = diagram.getObjectByName("Train");
		bus = diagram.getObjectByName("Bus");
		horse = diagram.getObjectByName("Horse");
		
		if(diagram.getObjectByName("Vehicle")!=null) {
		vehicle = diagram.getObjectByName("Vehicle");}
		else {return false;}
		
		return checkParent() && checkAllAttributes();
	}
	
	private boolean checkParent() {	//not checking for horse because that one is deemed correct either way
		boolean correctParent = false;	
		if(car.getAllAncestors().size()>0 && train.getAllAncestors().size()>0) {	
			if(car.getAllAncestors().get(0)==vehicle && train.getAllAncestors().get(0)==vehicle && bus.getAllAncestors().get(0)==vehicle && horse.getAllAncestors().size()==0){
				correctParent = true;
			}
		}
		return correctParent;
	}
	
	private boolean checkAllAttributes() {
		if(!checkAttributes(car)) {
			return false;
		}
		if(!checkAttributes(train)) {
			return false;
		}
		if(!checkAttributes(bus)) {
			return false;
		}
		if(!checkAttributes(vehicle)) {
			return false;
		}
		return true;
	}
	
	private boolean checkAttributes(FmmlxObject o) {
		if(DiagramConditionChecks.hasAttributeOfType(o, "constructionDate", "Date") && o!=vehicle) {
			return false;
		}
		if(DiagramConditionChecks.hasAttributeOfType(o, "maxNumberOfPassengers", "Integer") && o!=vehicle && o!=horse) {
			return false;
		}
		if(o==car) {
			if(!(DiagramConditionChecks.hasAttributeOfType(o, "hasBoardComputer", "Boolean") && DiagramConditionChecks.hasAttributeOfType(o, "brandName", "String"))) {
			return false;
			}
		}
		if(o==train) {
			if(!(DiagramConditionChecks.hasAttributeOfType(o, "usedForPublicTransportation", "Boolean") && DiagramConditionChecks.hasAttributeOfType(o, "trainCategory", "String"))) {
			return false;
			}
		}
		if(o==bus) {
			if(!( DiagramConditionChecks.hasAttributeOfType(o, "lineNumber", "String"))) {
			return false;
			}
		}
		if(o==vehicle) {
			if(!(DiagramConditionChecks.hasAttributeOfType(o, "constructionDate", "Date") &&
					DiagramConditionChecks.hasAttributeOfType(o, "maxNumberOfPassengers", "Integer") &&
					DiagramConditionChecks.hasAttributeOfType(o, "numberOfSeats", "Integer") &&
					DiagramConditionChecks.hasAttributeOfType(o, "isElectric", "Boolean"))) {
			return false;
			}
		}
		return true;
	}

}
