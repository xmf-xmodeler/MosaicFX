package tool.clients.fmmlxdiagrams.dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import tool.clients.fmmlxdiagrams.Issue;

public class ExtendedAlert extends Dialog<Object>{
	
	public ExtendedAlert(Issue issue) {
		DialogPane dialogPane = getDialogPane();
		setTitle("A constraint has failed or has been violated:");
		dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
		
		TextArea textArea = new TextArea(issue.getText());
		textArea.setPrefHeight(300);
		textArea.setPrefWidth(500);
		ScrollPane scrollPane = new ScrollPane(textArea);
		dialogPane.setContent(scrollPane);
	}
	
}
