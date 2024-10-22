package tool.xmodeler.didactic_ml.diagram_preperation_actions;

import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;

public class DeficientAttributesPreparation extends DiagramPreparationActions {

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
			String personClass = "Person";
			
			createMetaClass(diagram, 1, personClass, new int[]{0, 0});
			createAttributeOnLevelNull(diagram, personClass, "numberOfChildren", "String");
			createAttributeOnLevelNull(diagram, personClass, "married", "Boolean");
			createAttributeOnLevelNull(diagram, personClass, "age", "Integer");
			createAttributeOnLevelNull(diagram, personClass, "birthDate", "Date");
			createAttributeOnLevelNull(diagram, personClass, "heightInCm", "Integer");
			createAttributeOnLevelNull(diagram, personClass, "hairColour", "String");
		}

}
