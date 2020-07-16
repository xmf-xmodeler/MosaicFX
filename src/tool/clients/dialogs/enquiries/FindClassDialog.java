package tool.clients.dialogs.enquiries;

import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;

	public class FindClassDialog extends CustomDialog<Object>{
	private Label classNameLabel = new Label("Class name:");
	private Label levelLabel = new Label("Level");
	private Label includesAttributeLabel = new Label("includes Attribute");
	private Label modelsLabel = new Label("Models");
	private Label classesLabel = new Label("Class(es)");
	private TextField classNameTextfield = new TextField();
	private TextField levelTextfield = new TextField();
	private TextField includesAttributeTextfield = new TextField();
	ListView<String> modelsListView = new ListView<String>();
	ListView<String> classesListView = new ListView<String>();

	public FindClassDialog() {
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialogPane.setHeaderText("Search for Class");

		layout();

		dialogPane.setContent(flow);
	}

	private void layout() {
		
		grid.add(classNameLabel, 0, 1);
		grid.add(levelLabel, 0, 2);
		grid.add(includesAttributeLabel, 0, 3);
		
		grid.add(classNameTextfield, 1, 1);
		grid.add(levelTextfield, 1, 2);
		grid.add(includesAttributeTextfield, 1, 3);
		
		grid.add(modelsLabel, 2, 0);
		grid.add(modelsListView, 2, 1, 1, 4);
		
		grid.add(classesLabel, 3, 0);
		grid.add(classesListView, 3, 1, 1, 4);
		
		classNameTextfield.textProperty().addListener( (e, oldText, newText) -> {keyTyped();});
		levelTextfield.textProperty().addListener( (e, oldText, newText) -> {keyTyped();});
		includesAttributeTextfield.textProperty().addListener( (e, oldText, newText) -> {keyTyped();});
	}

	
	
	private void keyTyped() {
		System.err.println("The user wants to know more about " + classNameTextfield.getText());
		System.err.println("The user wants to know more about " + levelTextfield.getText());
		System.err.println("The user wants to know more about " + includesAttributeTextfield.getText());
		
	}
	
}
