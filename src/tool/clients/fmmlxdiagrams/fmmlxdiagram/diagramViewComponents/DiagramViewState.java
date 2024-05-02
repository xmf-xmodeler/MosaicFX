package tool.clients.fmmlxdiagrams.fmmlxdiagram.diagramViewComponents;

/**
 * This enum is used in the DiagramViewIntroduction. There different states of the diagramView are needed 
 * to help new users to deal with the XModeler. The state defines what the user can see. It is passed through the different frontend parts of the diagramView to create a matching frontend. 
 */
public enum DiagramViewState {
	
	CREATE_CLASS(1),
	FULL_GUI(100),
	//could be used for testing new feat as "Feat-Flag"
	FEAT_GUI(101),
	;
	

	/**
	 * The gui is build consecutive. So the next gui needs all elements of the gui before. This is used for not replicate code.
	 * The full gui is defined for the value 100. In between all states can be inserted.
	 */
	private int precedence;
	
	private DiagramViewState(int precedence) {
		this.precedence = precedence;
	}
	
	public int getPrecedence() {
		return precedence;
	}
}
