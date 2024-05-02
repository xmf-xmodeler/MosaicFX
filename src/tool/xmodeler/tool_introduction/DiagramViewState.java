package tool.xmodeler.tool_introduction;

/**
 * This enum is used in the DiagramViewIntroduction. There different states of
 * the diagramView are needed to help new users to deal with the XModeler. The
 * state defines what the user can see. It is passed through the different
 * frontend parts of the diagramView to create a matching frontend.
 */
public enum DiagramViewState {

	CREATE_CLASS(1, "FirstTask"),
	DUMMY(2, "SecondTask"),
	
	FULL_GUI(100, ""),
	// could be used for testing new feat as "Feat-Flag"
	FEAT_GUI(101, ""),;

	/**
	 * The gui is build consecutive. So the next gui needs all elements of the gui
	 * before. This is used for not replicate code. The full gui is defined for the
	 * value 100. In between all states can be inserted.
	 */
	private int precedence;
	private String taskDescritpion;



	public String getTaskDescritpion() {
		return taskDescritpion;
	}

	private DiagramViewState(int precedence, String taskDescritpion) {
		this.precedence = precedence;
		this.taskDescritpion = taskDescritpion;
	}

	public int getPrecedence() {
		return precedence;
	}

	public DiagramViewState getNextState() {
		int nextPrecedence = getPrecedence() + 1;
		return getViewStatusFromPrecedence(nextPrecedence);
	}

	DiagramViewState getViewStatusFromPrecedence(int precedence) {
		for (DiagramViewState state : values()) {
			if (state.getPrecedence() == precedence) {
				return state;
			}
		}
		throw new IllegalArgumentException("No DiagramViewState for the precedenceValue" + precedence);
	}
}