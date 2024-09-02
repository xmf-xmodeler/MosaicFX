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
		String vehicleClass = "Vehicle";
		String trainClass = "Train";
		String carClass = "Car";
		String horseClass = "Horse";
		
		createMetaClass(diagram, 1, vehicleClass, new int[]{400, 0});
		createAttributeOnLevelNull(diagram, vehicleClass, "horsePower", "Integer");
		createAttributeOnLevelNull(diagram, vehicleClass, "numberOfPassengers", "Integer");

		createMetaClass(diagram, 1, trainClass, new int[]{0, 0});
		createAttributeOnLevelNull(diagram, trainClass, "numberOfWheels", "Integer");
		
		createMetaClass(diagram, 1, carClass, new int[]{200, 0});
		createAttributeOnLevelNull(diagram, carClass, "numberOfWheels", "Integer");
		
		createMetaClass(diagram, 1, horseClass, new int[]{600, 0});
		createAttributeOnLevelNull(diagram, horseClass, "horseName", "String");
	}
}
