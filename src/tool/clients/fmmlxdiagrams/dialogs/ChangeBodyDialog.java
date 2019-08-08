package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeBodyDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

public class ChangeBodyDialog extends CustomDialog<ChangeBodyDialogResult>{
	
	private DialogPane dialogPane;
	private FmmlxDiagram diagram;
	private FmmlxObject object;
	
	private Label classLabel;
	private Label selectOperationLabel;
	private Label bodyLabel;
	
	private TextField classTextField;
	private ComboBox<FmmlxOperation> selectOperationComboBox;
	private TextArea bodyTextArea;
	
	private Vector<FmmlxOperation> operations;

	public ChangeBodyDialog(FmmlxDiagram diagram, FmmlxObject object) {
		super();
		this.diagram=diagram;
		this.object=object;
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent();
		dialogPane.setContent(flow);
		
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResult();

	}
	
	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new ChangeBodyDialogResult(object, selectOperationComboBox.getSelectionModel().getSelectedItem(), 
						bodyTextArea.getText());
			}
			return null;
		});
	}

	private boolean validateUserInput() {
		if(selectOperationComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectOperation);
			return false;
		}
		return true;
	}

	private void layoutContent() {
		operations = object.getOwnOperations();
		operations.addAll(object.getOtherOperations());
		
		ObservableList<FmmlxOperation> operationsList;
		operationsList = FXCollections.observableList(operations);
		
		
		dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.changeOperationsBody);
		
		classLabel = new Label("Class");
		selectOperationLabel = new Label("Select operation");
		bodyLabel = new Label("Body");
		
		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		
		bodyTextArea = new TextArea();
		
		selectOperationComboBox = (ComboBox<FmmlxOperation>)initializeComboBox(operationsList);
		selectOperationComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				//TODO input current body to textArea
			}
		});
		
		Button checkSyntaxButton = new Button(StringValueDialog.LabelAndHeaderTitle.checkSyntax);
		checkSyntaxButton.setOnAction(event -> ChangeBodyDialog.this.checkBodySyntax());
		checkSyntaxButton.setPrefWidth(COLUMN_WIDTH * 0.5);
		Button defaultOperationButton = new Button(StringValueDialog.LabelAndHeaderTitle.defaultOperation);
		defaultOperationButton.setOnAction(event -> ChangeBodyDialog.this.resetOperationBody());
		defaultOperationButton.setPrefWidth(COLUMN_WIDTH * 0.5);
		
		selectOperationComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(classLabel, 0, 0);
		grid.add(classTextField, 1, 0);
		grid.add(selectOperationLabel, 0, 1);
		grid.add(selectOperationComboBox, 1, 1);
		grid.add(bodyLabel, 0, 2);
		grid.add(bodyTextArea, 1, 2, 1, 2);
		grid.add(checkSyntaxButton, 0, 2);
		grid.add(defaultOperationButton, 0, 3);
		
	}
	
	private void checkBodySyntax() {
		if (!isNullOrEmpty(bodyTextArea.getText()) && !bodyTextArea.getText().contentEquals(StringValueDialog.OperationStringValues.emptyOperation)) {
			diagram.checkOperationBody(bodyTextArea.getText());
		}
	}
	
	private void resetOperationBody() {
		bodyTextArea.setText(StringValueDialog.OperationStringValues.emptyOperation);
	}

}
