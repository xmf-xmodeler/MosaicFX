package tool.xmodeler.didactic_ml.diagram_preperation_actions;

import java.util.List;

import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;

public class ClassificationInstantiationPreparation extends DiagramPreparationActions {

	@Override
	public String[][] prepair(FmmlxDiagram diagram) {
		switch (SelfAssessmentTestTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName())) {
		case 2:
			createMetaClass(diagram, 1, "dkdkd", new int[]{400, 0});

		default:
			return null;
		}
		
	}

}
