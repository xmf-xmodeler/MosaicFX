package tool.clients.fmmlxdiagrams.dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.Issue;
import tool.helper.IconGenerator;

public class ExtendedAlert extends Dialog<Object>{
	
	public ExtendedAlert(Issue issue) {
		DialogPane dialogPane = getDialogPane();
		setTitle("Constraint Violation Message");
		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(IconGenerator.getImage("shell/mosaic32"));
		
		dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
		
		dialogPane.setPrefWidth(350);
		dialogPane.setPrefHeight(250);
		
		TextArea textArea = new TextArea(issue.getText());
		textArea.setWrapText(true);
		textArea.setMaxHeight(dialogPane.getPrefHeight()*0.85);
		textArea.setMaxWidth(dialogPane.getPrefWidth()*0.92);
		ScrollPane scrollPane = new ScrollPane(textArea);
		dialogPane.setContent(scrollPane);
	}
	
}
