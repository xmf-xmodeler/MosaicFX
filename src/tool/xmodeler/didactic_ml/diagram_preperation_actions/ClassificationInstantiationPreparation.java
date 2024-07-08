package tool.xmodeler.didactic_ml.diagram_preperation_actions;

import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.xmodeler.didactic_ml.learning_unit_tasks.LearningUnitTasks;

public class ClassificationInstantiationPreparation extends DiagramPreparationActions {

	@Override
	public void prepair(FmmlxDiagram diagram) {
		switch (LearningUnitTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName())) {
		case 2:
			createMetaClass(diagram, 1, "dkdkd", new int[]{400, 0});

		default:
			return;
		}
		
	}

}
