package tool.clients.fmmlxdiagrams.dialogs.operation;

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
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeBodyDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

public class ChangeBodyDialog extends CustomDialog<ChangeBodyDialogResult>{
	
	private DialogPane dialogPane;
	private AbstractPackageViewer diagram;
	private FmmlxObject object;
	
	private Label classLabel;
	private Label selectOperationLabel;
	private Label bodyLabel;
	
	private TextField classTextField;
	private ComboBox<FmmlxOperation> selectOperationComboBox;
	private TextArea bodyTextArea;
	
	private Vector<FmmlxOperation> operations;
	
	private ButtonType checkSyntaxButtonType;
	private ButtonType defaultOperationButtonType;

	public ChangeBodyDialog(AbstractPackageViewer diagram, FmmlxObject object, final FmmlxOperation initiallySelectedOperation) {
		super();
		this.diagram=diagram;
		this.object=object;
		setResizable(true);
		
		dialogPane = getDialogPane();

		checkSyntaxButtonType = new ButtonType(StringValue.LabelAndHeaderTitle.checkSyntax);
		defaultOperationButtonType = new ButtonType(StringValue.LabelAndHeaderTitle.defaultOperation);
		
		dialogPane.getButtonTypes().addAll(checkSyntaxButtonType, defaultOperationButtonType, ButtonType.OK, ButtonType.CANCEL);
		layoutContent();
		dialogPane.setContent(flow);
		
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});
		
		final Button defaultOperationButton = (Button) getDialogPane().lookupButton(defaultOperationButtonType);
		defaultOperationButton.addEventFilter(ActionEvent.ACTION, e -> {
				ChangeBodyDialog.this.resetOperationBody();
				e.consume();	
		});
		
		final Button checkSyntaxButton = (Button) getDialogPane().lookupButton(checkSyntaxButtonType);
		checkSyntaxButton.addEventFilter(ActionEvent.ACTION, e -> {		
			ChangeBodyDialog.this.checkBodySyntax();
			e.consume();	
		});
		
		setResultConverter();
		
		setResizable(true);
		dialogPane.setMinSize(800, 500);

		if(initiallySelectedOperation != null) selectOperationComboBox.getSelectionModel().select(initiallySelectedOperation);
	}
	
	private void setResultConverter() {
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
			errorLabel.setText(StringValue.ErrorMessage.selectOperation);
			return false;
		}
		return true;
	}

	private void layoutContent() {
		operations = object.getOwnOperations();
		operations.addAll(object.getOtherOperations());
		ObservableList<FmmlxOperation> operationsList;
		operationsList = FXCollections.observableList(operations);
		
		dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeOperationsBody);
		
		classLabel = new Label("Class");
		selectOperationLabel = new Label("Select operation");
		bodyLabel = new Label("Body");
		
		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		classTextField.setMinWidth(COLUMN_WIDTH);
		classTextField.isResizable();
		
		bodyTextArea = new TextArea();
		bodyTextArea.setMinSize(620, 350);
		bodyTextArea.resize(COLUMN_WIDTH, grid.getHeight());
		selectOperationComboBox = (ComboBox<FmmlxOperation>)initializeComboBox(operationsList);
		selectOperationComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				bodyTextArea.setText(newValue.getBody());
			}
		});
		
		selectOperationComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(classLabel, 0, 0);
		grid.add(classTextField, 1, 0);
		grid.add(selectOperationLabel, 0, 1);
		grid.add(selectOperationComboBox, 1, 1);
		grid.add(bodyLabel, 0, 2);
		grid.add(bodyTextArea, 1, 2, 1, 2);
	}
	
	private void checkBodySyntax() {
		if (!isNullOrEmpty(bodyTextArea.getText()) && !bodyTextArea.getText().contentEquals(StringValue.OperationStringValues.emptyOperation)) {
			diagram.getComm().checkOperationBody(bodyTextArea.getText());
		}
	}
	
	private void resetOperationBody() {
		bodyTextArea.setText(StringValue.OperationStringValues.emptyOperation);
	}

}
