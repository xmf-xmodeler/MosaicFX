package tool.xmodeler.didactic_ml.sucess_conditions;

import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.xmodeler.didactic_ml.frontend.task_description_viewer.TaskDescriptionViewer;

/**
 * Class is used in the context of the ToolIntroduction.
 * Every time the user clicks on check-condition button this class is used to check the successCondition for the current state.
 */
public abstract class SuccessCondition {

	/**
	 * Diagram for that the conditions are checked. The diagram is set via the LearningUnitManager.
	 */
	protected FmmlxDiagram diagram;
		
	/**
	 * Function that defines which condition should be check for the current task. Please reuse structure from example class ToolIntroductionCondition
	 * @return the result of the specific checked condition
	 */
	public abstract boolean checkSuccessCondition(TaskDescriptionViewer taskViewer);

	public void setDiagram(FmmlxDiagram diagram) {
		this.diagram = diagram;
	}
}