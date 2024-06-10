package tool.xmodeler.tool_introduction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * This enum is used in the DiagramViewIntroduction. There different states of
 * the diagramView are needed to help new users to deal with the XModeler. The
 * state defines what the user can see. It is passed through the different
 * frontend parts of the diagramView to create a matching frontend.
 */
public enum DiagramViewState {

	CREATE_CLASS_MOVIE(1),
	ADD_ATTRIBUTES_TO_MOVIE(2),
	CREATE_CLASS_MOVIE_SHOWING(3),
	ADD_ASSOC_BETWEEN_MOVIE_AND_SHOWING(4),
	ADD_LINK_BETWEEN_MOVIEINST_AND_SHOWINGINST(5),
	CHANGE_CARDINALITY_OF_BUYS(6),
	ADD_RATING_ENUM(7),
	CHANGE_DATATYPE_TO_ENUM(8),
	GET_REQUIRED_AGE_FUN_IS_ADDED(9),
	CONSTRAINT_IS_ADD(10),
	
	FULL_GUI(100),
	// could be used for testing new feat as "Feat-Flag"
	FEAT_GUI(101);

	/**
	 * The gui is build consecutive. So the next gui needs all elements of the gui
	 * before. This is used for not replicate code. The full gui is defined for the
	 * value 100. In between all states can be inserted.
	 */
	private int precedence;

	private DiagramViewState(int precedence) {
		this.precedence = precedence;
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

	/**
	 * request the task description needed for the current state. The descriptions are stores in the source folder
	 * @return current task description as String
	 */
	public String getTaskDescritpion() {
		String taskDescriptionPath = buildTaskDescriptionPath();
		StringBuilder contentBuilder = new StringBuilder();
		try (Reader reader = new InputStreamReader(new FileInputStream(taskDescriptionPath), StandardCharsets.UTF_8)) {
			BufferedReader br = new BufferedReader(reader);
			String line;
			while ((line = br.readLine()) != null) {
				contentBuilder.append(line);
			}
			return contentBuilder.toString();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Can not find file for DiagramViewState " + getPrecedence());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * helper function that returns the path of a file where the description
	 * is stored
	 * @return filepath of task description
	 */
	private String buildTaskDescriptionPath() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("resources/txt/ToolIntroduction/"); // basic path
		stringBuilder.append(getPrecedence()); // append number of current state
		stringBuilder.append(".txt");
		return stringBuilder.toString();
	}
}