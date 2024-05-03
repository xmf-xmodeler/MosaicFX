package tool.xmodeler.tool_introduction;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.management.RuntimeErrorException;

/**
 * This enum is used in the DiagramViewIntroduction. There different states of
 * the diagramView are needed to help new users to deal with the XModeler. The
 * state defines what the user can see. It is passed through the different
 * frontend parts of the diagramView to create a matching frontend.
 */
public enum DiagramViewState {

	CREATE_CLASS(1),
	ADD_ATTRIBUTES(2),
	CREATE_SECOND_CLASS(3),
	DUMMY(4),
	
	

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

	public String getTaskDescritpion() {
		String taskDescriptionPath = buildTaskDescriptionPath();
		StringBuilder contentBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(taskDescriptionPath, StandardCharsets.UTF_8))) {
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

	private String buildTaskDescriptionPath() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("resources/txt/ToolIntroduction/"); // basic path
		stringBuilder.append(getPrecedence()); // append number of current state
		stringBuilder.append(".txt");
		return stringBuilder.toString();
	}
}