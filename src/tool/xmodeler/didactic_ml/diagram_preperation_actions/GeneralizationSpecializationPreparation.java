package tool.xmodeler.didactic_ml.diagram_preperation_actions;

import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;

public class GeneralizationSpecializationPreparation extends DiagramPreparationActions {

	@Override
	public void prepair(FmmlxDiagram diagram) {
		switch (SelfAssessmentTestTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName())) {
		case 1:
			addClasses(diagram);
			return;
		default:return;	
	}
}
	
	private static void addClasses(FmmlxDiagram diagram) {
		String busClass = "Bus";
		String trainClass = "Train";
		String carClass = "Car";
		String horseClass = "Horse";
		
		createMetaClass(diagram, 1, busClass, new int[]{400, 0});
		createAttributeOnLevelNull(diagram, busClass, "lineNumber", "String");
		createAttributeOnLevelNull(diagram, busClass, "maxNumberOfPassengers", "Integer");
		createAttributeOnLevelNull(diagram, busClass, "constructionDate", "Date");
		addGetAgeFunction(diagram, busClass);
		
		createMetaClass(diagram, 1, trainClass, new int[]{0, 0});
		createAttributeOnLevelNull(diagram, trainClass, "trainCategory", "String");
		createAttributeOnLevelNull(diagram, trainClass, "usedForPublicTransportation", "Boolean");
		createAttributeOnLevelNull(diagram, trainClass, "maxNumberOfPassengers", "Integer");
		createAttributeOnLevelNull(diagram, trainClass, "constructionDate", "Date");
		addGetAgeFunction(diagram, trainClass);
		
		createMetaClass(diagram, 1, carClass, new int[]{200, 0});
		createAttributeOnLevelNull(diagram, carClass, "brandName", "String");
		createAttributeOnLevelNull(diagram, carClass, "hasBoardComputer", "Boolean");
		createAttributeOnLevelNull(diagram, carClass, "maxNumberOfPassengers", "Integer");
		createAttributeOnLevelNull(diagram, carClass, "constructionDate", "Date");
		addGetAgeFunction(diagram, carClass);
		
		createMetaClass(diagram, 1, horseClass, new int[]{600, 0});
		createAttributeOnLevelNull(diagram, horseClass, "maxNumberOfPassengers", "Integer");
		createAttributeOnLevelNull(diagram, horseClass, "dateOfBirth", "Date");
		createAttributeOnLevelNull(diagram, horseClass, "typeOfBreed", "String");
		addGetAgeFunction(diagram, horseClass);
	}
	
	private static void addGetAgeFunction(FmmlxDiagram diagram, String className) {
		String dateName="constructionDate";
		if(className=="Horse") {
			dateName="dateOfBirth";
		}
		String funBody = "@Operation getAge[monitor=true]():XCore::Integer if self."+dateName+" <> null then self.dateOfBirth.age() else \"No date of birth entered\" end end";
		diagram.getComm().addOperation(diagram.getID(), diagram.getClassPath(className), 0, funBody);
	}
}
