package tool.clients.dialogs.enquiries;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.GridData;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;

public class FindImplementationDialog extends CustomDialog<Object> {
	
	private Label nameLabel = new Label("Operation name");
	private Label modelLabel = new Label("Model");
	private Label numberOfParamsLabel = new Label("number of Params");
	private Label returnTypeLabel = new Label("Return type");
	private Label classesLabel = new Label("Classes");
	private Label codeLabel = new Label("Code");
	private TextField nameTextfield = new TextField();
	private TextField modelTextfield = new TextField();
	private TextField numberOfParamsTextfield = new TextField();
	private TextField returnTypeTextfield = new TextField();
	ListView<String> classesListView = new ListView<String>();
	TextArea codeBox = new TextArea();

	public FindImplementationDialog() {
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialogPane.setHeaderText("Search for Implementations");

		layout();

		dialogPane.setContent(flow);
	}

	private void layout() {
		
		grid.add(nameLabel, 0, 1);
		grid.add(modelLabel, 0, 2);
		grid.add(numberOfParamsLabel, 0, 3);
		grid.add(returnTypeLabel, 0, 4);
		
		grid.add(nameTextfield, 1, 1);
		grid.add(modelTextfield, 1, 2);
		grid.add(numberOfParamsTextfield, 1, 3);
		grid.add(returnTypeTextfield, 1, 4);
		
		grid.add(classesLabel, 2, 0);
		grid.add(classesListView, 2, 1, 1, 5);
		
		grid.add(codeLabel, 3, 0);
		grid.add(codeBox, 3, 1, 1, 5);
		
		nameTextfield.textProperty().addListener( (e, oldText, newText) -> {keyTyped();});
		modelTextfield.textProperty().addListener( (e, oldText, newText) -> {keyTyped();});
		numberOfParamsTextfield.textProperty().addListener( (e, oldText, newText) -> {keyTyped();});
		returnTypeTextfield.textProperty().addListener( (e, oldText, newText) -> {keyTyped();});
	}

	
	
	private void keyTyped() {
		System.err.println("The user wants to know more about " + nameTextfield.getText());
		System.err.println("The user wants to know more about " + modelTextfield.getText());
		System.err.println("The user wants to know more about " + numberOfParamsTextfield.getText());
		System.err.println("The user wants to know more about " + returnTypeTextfield.getText());
		
	}
	
}
