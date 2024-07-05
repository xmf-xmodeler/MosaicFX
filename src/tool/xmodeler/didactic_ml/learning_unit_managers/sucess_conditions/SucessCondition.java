package tool.xmodeler.didactic_ml.learning_unit_managers.sucess_conditions;

import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;

/**
 * Class is used in the context of the ToolIntroduction.
 * Every time the user clicks on check-condition button this class is used to check the successCondition for the current state.
 */
public abstract class SucessCondition {

	protected FmmlxDiagram diagram;
	
	protected SucessCondition(FmmlxDiagram diagram) {
		this.diagram = diagram;
	}
	
	public abstract boolean checkSucessCondition();


}