package tool.clients.fmmlxdiagrams.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ShortcutDialog extends Alert {

	public ShortcutDialog() {
		super(AlertType.INFORMATION);
		setTitle("Shortcuts");
		setHeaderText("List of Shortcuts");

		String content = buildContent();

		// Create a TextArea and set the content
		TextArea textArea = new TextArea(content);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		// Configure the TextArea to expand in the dialog
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		// Add the TextArea to the dialog's expandable content
		GridPane contentPane = new GridPane();
		contentPane.add(textArea, 0, 0);

		getDialogPane().setContent(contentPane);
	}

	private String buildContent() {
		return 	"F5: Update Diagram\n" +
				"Strg + S: Save Diagram\n" + 
				"Strg + A: Select all Elements\n" + 
				"Strg + F: Find Objects\n" + 
				"Strg + Z: Undo\n" + 
				"Strg + Y: Redo\n" +
				"\n" +
				"Strg + T: Bring Task Description upfront (Only usable in a Learning Unit)\n" +
				"\n" + 
				"Mouse Combinations:\n" + 
				"Mouse + Space or Alt: Move Canvas";
	}
}
