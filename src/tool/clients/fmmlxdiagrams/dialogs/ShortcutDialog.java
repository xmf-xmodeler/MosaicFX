package tool.clients.fmmlxdiagrams.dialogs;

import javafx.scene.control.Alert;

public class ShortcutDialog extends Alert {

	public ShortcutDialog() {
		super(AlertType.INFORMATION);
		setTitle("Shortcuts");
		setHeaderText("List of Shortcuts");
		String content = buildContent();
		setContentText(content);
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
				"Mouse ombinations:\n" + 
				"Mouse + Space or Alt: Move Canvas";
	}
}