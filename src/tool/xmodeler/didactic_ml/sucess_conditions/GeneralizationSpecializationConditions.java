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
		if(car.getAllAncestors().size()>0 && train.getAllAncestors().size()>0 && bus.getAllAncestors().size()>0 && horse.getAllAncestors().size()==0) {	
			if(car.getAllAncestors().get(0)==vehicle && train.getAllAncestors().get(0)==vehicle && bus.getAllAncestors().get(0)==vehicle){
				correctParent = true;
			}
		}
		System.out.println("checkParent="+correctParent);
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
		System.out.println("All Attributes Correct");
		return true;
	}
	
	private boolean checkAttributes(FmmlxObject o) {
		System.out.println(o.getOwnAttributes());
		if(DiagramConditionChecks.hasOwnAttributeOfType(o, "constructionDate", "Date") && o!=vehicle) {	//only vehicle has construction date
			System.out.println(o+"containt constructionDate!!!");
			return false;
		}
		if(DiagramConditionChecks.hasOwnAttributeOfType(o, "maxNumberOfPassengers", "Integer") && o!=vehicle && o!=horse) {	//only vehicle and horse have max passengers
			return false;
		}
		if(o==car) {
			if(!(DiagramConditionChecks.hasOwnAttributeOfType(o, "hasBoardComputer", "Boolean") && DiagramConditionChecks.hasAttributeOfType(o, "brandName", "String"))) {	//car needs boardComputer and brandName
			return false;
			}
		}
		if(o==train) {
			if(!(DiagramConditionChecks.hasOwnAttributeOfType(o, "usedForPublicTransportation", "Boolean") && DiagramConditionChecks.hasAttributeOfType(o, "trainCategory", "String"))) { //train needs these attributes
			return false;
			}
		}
		if(o==bus) {
			if(!( DiagramConditionChecks.hasOwnAttributeOfType(o, "lineNumber", "String"))) {	//Bus needs line number
			return false;
			}
		}
		if(o==vehicle) {	//vehicle requires all of these attributes
			if(!(DiagramConditionChecks.hasOwnAttributeOfType(o, "constructionDate", "Date") &&
					DiagramConditionChecks.hasOwnAttributeOfType(o, "maxNumberOfPassengers", "Integer") &&
					DiagramConditionChecks.hasOwnAttributeOfType(o, "numberOfSeats", "Integer") &&
					DiagramConditionChecks.hasOwnAttributeOfType(o, "isElectric", "Boolean"))) {
			return false;
			}
		}
		System.out.println("Attributes for"+ o + " Correct");
		return true;
	}

}
