package tool.clients.dialogs.enquiries;



import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import com.sun.media.jfxmediaimpl.platform.Platform;

import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.TimeOutException;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;

	public class FindSendersOfMessages extends CustomDialog<Object>{

	private Label messageNameLabel = new Label("Message name:");
	private Label modelLabel = new Label("Model");
	private Label numberOfParamsLabel = new Label("number of Params");
	private Label classesLabel = new Label("Operations");
	private Label operationsLabel = new Label("Code");
	private Label statusLabel = new Label("");
	
	private TextField messageNameTextfield = new TextField();
	private TextField modelTextfield = new TextField();
	private TextField numberOfParamsTextfield = new TextField();
	
	private enum Status{
		READY, WAITING, DIRTY, 
	}
	
	private Status status = Status.READY;
	
	
	
	
	ListView<String> classesListView = new ListView<String>();
	TextArea operationsTextarea = new TextArea();
	AbstractPackageViewer diagram;
	private final FmmlxDiagramCommunicator fmmlxDiagramCommunicator;
	
	HashMap<String, String>  result = new HashMap<String, String>();
	
	public FindSendersOfMessages(AbstractPackageViewer diagram, FmmlxDiagramCommunicator fmmlxDiagramCommunicator) {
		this.fmmlxDiagramCommunicator = fmmlxDiagramCommunicator;
		this.diagram = diagram;
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialogPane.setHeaderText("Search for Senders");

		layout();

		dialogPane.setContent(flow);
	}

	private void layout() {
		statusLabel.setText(status + "");
		grid.add(messageNameLabel, 0, 1);
		grid.add(modelLabel, 0, 2);
		grid.add(numberOfParamsLabel, 0, 3);
		
		grid.add(messageNameTextfield, 1, 1);
		grid.add(modelTextfield, 1, 2);
		grid.add(numberOfParamsTextfield, 1, 3);
		
		grid.add(classesLabel, 2, 0);
		grid.add(classesListView, 2, 1, 1, 5);
		
		grid.add(operationsLabel, 3, 0);
		grid.add(operationsTextarea, 3, 1, 1, 5);
		
		grid.add(statusLabel, 0, 4, 2, 1);
		
		messageNameTextfield.textProperty().addListener( (e, oldText, newText) -> {keyTyped();});
		modelTextfield.textProperty().addListener( (e, oldText, newText) -> {keyTyped();});
		numberOfParamsTextfield.textProperty().addListener( (e, oldText, newText) -> {keyTyped();});
		classesListView.getSelectionModel().selectedItemProperty().addListener((e, oldText, newText) -> {classSelected(newText);});

	}
	
	private void classSelected(String newText) {
		if(newText != null) {
			operationsTextarea.setText(result.get(newText));
	}}
	
	private void keyTyped() 
	{
		javafx.application.Platform.runLater(()->
		{
		switch (status) 
		{
		case READY:
			
			if(messageNameTextfield.getText().length()>3) 
			{
				status =Status.WAITING;
				statusLabel.setText(status + "");
				sendRequest();
			}
			break;
			
		case WAITING:
			status = Status.DIRTY;
			statusLabel.setText(status + "");
			break;
		
		
		default:
			break;
		}
		});
	}
	

	private void sendRequest() {
		System.err.println("The user wants to know more about " + messageNameTextfield.getText());
		System.err.println("The user wants to know more about " + modelTextfield.getText());
		System.err.println("The user wants to know more about " + numberOfParamsTextfield.getText());
		if(messageNameTextfield.getText().length()>3) {
	//		javafx.application.Platform.runLater(()->{
			Thread thread = new Thread(()->{
			
				fmmlxDiagramCommunicator.findOperationUsage(
						diagram, this,
						messageNameTextfield.getText(), 
						modelTextfield.getText());
			});
			thread.start();
		}	else {
			status = Status.READY;
			statusLabel.setText(status + "");
		}
	}

	public void sendResponse(HashMap<String, String> result2) {
		this.result = result2;
		classesListView.getItems().clear();
		Vector <String> keys = new Vector<>();
		keys.addAll(result.keySet());
		Collections.sort(keys);
		classesListView.getItems().addAll(keys);
		if(status == Status.DIRTY) {
			status = Status.WAITING;
			statusLabel.setText(status + "");
			sendRequest();
		}else {
		status = Status.READY;
		statusLabel.setText(status + "");

		}
	}
	
}
