package tool.clients.dialogs.enquiries;



import java.util.HashMap;

import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.TimeOutException;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;

	public class FindSendersOfMessages extends CustomDialog<Object>{

	private Label messageNameLabel = new Label("Message name:");
	private Label modelLabel = new Label("Model");
	private Label numberOfParamsLabel = new Label("number of Params");
	private Label classesLabel = new Label("Classes");
	private Label operationsLabel = new Label("Operations");
	
	private TextField messageNameTextfield = new TextField();
	private TextField modelTextfield = new TextField();
	private TextField numberOfParamsTextfield = new TextField();
	
	ListView<String> classesListView = new ListView<String>();
	ListView<String> operationsListView = new ListView<String>();
	FmmlxDiagram diagram;
	private final FmmlxDiagramCommunicator fmmlxDiagramCommunicator;
	
	HashMap<String, String>  result = new HashMap<String, String>();

	@Deprecated public FindSendersOfMessages() {this(null, null);}
	
	public FindSendersOfMessages(FmmlxDiagram diagram, FmmlxDiagramCommunicator fmmlxDiagramCommunicator) {
		this.fmmlxDiagramCommunicator = fmmlxDiagramCommunicator;
		this.diagram = diagram;
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialogPane.setHeaderText("Search for Senders");

		layout();

		dialogPane.setContent(flow);
	}

	private void layout() {
		
		grid.add(messageNameLabel, 0, 1);
		grid.add(modelLabel, 0, 2);
		grid.add(numberOfParamsLabel, 0, 3);
		
		grid.add(messageNameTextfield, 1, 1);
		grid.add(modelTextfield, 1, 2);
		grid.add(numberOfParamsTextfield, 1, 3);
		
		grid.add(classesLabel, 2, 0);
		grid.add(classesListView, 2, 1, 1, 4);
		
		grid.add(operationsLabel, 3, 0);
		grid.add(operationsListView, 3, 1, 1, 4);
		
		messageNameTextfield.textProperty().addListener( (e, oldText, newText) -> {keyTyped();});
		modelTextfield.textProperty().addListener( (e, oldText, newText) -> {keyTyped();});
		numberOfParamsTextfield.textProperty().addListener( (e, oldText, newText) -> {keyTyped();});
	}
	
	private void keyTyped() {
		System.err.println("The user wants to know more about " + messageNameTextfield.getText());
		System.err.println("The user wants to know more about " + modelTextfield.getText());
		System.err.println("The user wants to know more about " + numberOfParamsTextfield.getText());
		if(messageNameTextfield.getText().length()>3) {
			try {
				result = fmmlxDiagramCommunicator.findOperationUsage(
						diagram, 
						messageNameTextfield.getText(), 
						modelTextfield.getText());
			System.err.println("result: " + result);
			} catch (TimeOutException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
}
