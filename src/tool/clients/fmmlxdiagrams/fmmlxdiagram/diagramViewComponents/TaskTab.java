package tool.clients.fmmlxdiagrams.fmmlxdiagram.diagramViewComponents;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

/**
 * This class is used in the context of the toolIntroduction. On this tab the
 * tasks the user should do to reach the next stage are displayed
 */
public class TaskTab extends ScrollPane {

	TextArea textArea = new TextArea();

	public TaskTab(String firstTask) {
		configScrollPane();
		configTextArea(firstTask);
		setContent(textArea);
	}

	private void configTextArea(String text) {
		textArea.setWrapText(true);
		textArea.maxWidthProperty().bind(widthProperty());
		textArea.prefHeightProperty().bind(heightProperty());
		textArea.setEditable(false);
		textArea.setText(text);
	}

	private void configScrollPane() {
		setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		//value determined by experiments
		setMaxWidth(200);
	}

	/**
	 * function that adds the description of the next Task to the TextArea.
	 * Between the old and the new text a Separator is added.
	 * @param nextTaskDescription
	 */
	public void appendTask(String nextTaskDescription) {
		appendSeparator();
		textArea.appendText(nextTaskDescription);
	}

	public void appendSeparator() {
		final String SEPARATOR = "\n- - - - - - - - - - - - - - - - - -\n";
		textArea.appendText(SEPARATOR);
	}
}