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
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.ReturnCall;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.dialogs.CodeBoxPair.OperationException;

public class ChangeBodyDialog extends CustomDialog<ChangeBodyDialog.Result>{
	
	private DialogPane dialogPane;
	private AbstractPackageViewer diagram;
	private FmmlxObject object;
	
	private Label classLabel;
	private Label selectOperationLabel;
	private Label bodyLabel;
	
	private TextField classTextField;
	private ComboBox<FmmlxOperation> selectOperationComboBox;
	private CodeBoxPair codeBoxPair;
	
	private Vector<FmmlxOperation> operations;
	
	private ButtonType defaultOperationButtonType;
	private Button resetBodyButton;

	public ChangeBodyDialog(AbstractPackageViewer diagram, FmmlxObject object, final FmmlxOperation initiallySelectedOperation) {
		super();
		this.diagram=diagram;
		this.object=object;
		setResizable(true);
		
		dialogPane = getDialogPane();

		defaultOperationButtonType = new ButtonType(StringValue.LabelAndHeaderTitle.defaultOperation);
		resetBodyButton = new Button("Reset Body");
		resetBodyButton.setOnAction(e -> {		
			resetOperationBody();
			e.consume();	
		});
		
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent();
		dialogPane.setContent(flow);
		
		
		//final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		//okButton.addEventFilter(ActionEvent.ACTION, e -> {
		//	if (!validateUserInput()) {
		//		e.consume();
		//	}
		//});
		
//		final Button defaultOperationButton = (Button) getDialogPane().lookupButton(defaultOperationButtonType);
//		defaultOperationButton.addEventFilter(ActionEvent.ACTION, e -> {
//				ChangeBodyDialog.this.resetOperationBody();
//				e.consume();	
//		});
		
//		final Button checkSyntaxButton = (Button) getDialogPane().lookupButton(checkSyntaxButtonType);
//		checkSyntaxButton.addEventFilter(ActionEvent.ACTION, e -> {		
//			ChangeBodyDialog.this.checkBodySyntax();
//			e.consume();	
//		});
		
		setResultConverter();
		
		setResizable(true);
		dialogPane.setMinSize(630, 400);

		if(initiallySelectedOperation != null) selectOperationComboBox.getSelectionModel().select(initiallySelectedOperation);
	}
	
	private void setResultConverter() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new ChangeBodyDialog.Result(object, selectOperationComboBox.getSelectionModel().getSelectedItem(), 
						codeBoxPair.getBodyText());
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
		
		codeBoxPair = new CodeBoxPair(diagram,e->{getDialogPane().lookupButton(ButtonType.OK).setDisable(
					!codeBoxPair.getCheckPassed()||
					selectOperationComboBox.getSelectionModel().getSelectedItem()==null||
					selectOperationComboBox.getSelectionModel().getSelectedItem().getName().startsWith("set")||
					selectOperationComboBox.getSelectionModel().getSelectedItem().getName().startsWith("get"));
		}, false);
		
		dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeOperationsBody);
		
		classLabel = new Label("Class");
		selectOperationLabel = new Label("Select operation");
		bodyLabel = new Label("Body");
		
		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		classTextField.setMinWidth(COLUMN_WIDTH);
		classTextField.isResizable();
		codeBoxPair.getBodyScrollPane().setMinWidth(COLUMN_WIDTH*2);
		codeBoxPair.getBodyScrollPane().setMaxWidth(COLUMN_WIDTH*4);
		//codeBoxPair.getBodyScrollPane().resize(COLUMN_WIDTH, grid.getHeight());
		codeBoxPair.getErrorTextArea().setMinWidth(COLUMN_WIDTH*2);
		codeBoxPair.getErrorTextArea().setMaxWidth(COLUMN_WIDTH*4);
		codeBoxPair.getErrorTextArea().setMaxHeight(100);
		//codeBoxPair.getErrorTextArea().resize(COLUMN_WIDTH, grid.getHeight());
		selectOperationComboBox = (ComboBox<FmmlxOperation>)initializeComboBox(operationsList);
		selectOperationComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				codeBoxPair.setBodyText(newValue.getBody());
			}
		});
		
		selectOperationComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(classLabel, 0, 0);
		grid.add(classTextField, 1, 0);
		grid.add(selectOperationLabel, 0, 1);
		grid.add(selectOperationComboBox, 1, 1);
		grid.add(resetBodyButton, 1, 2);
		grid.add(bodyLabel, 0, 2);
		grid.add(codeBoxPair.getBodyScrollPane(), 0, 3);
		grid.add(new Label("Parse Result"), 0, 4);
		grid.add(codeBoxPair.getErrorTextArea(), 0, 5);
	}
	
	private void checkBodySyntax() {
		ReturnCall<OperationException> returnCall = opException -> {
			System.err.println("opException:" + opException);
		};
		
		diagram.getComm().checkSyntax(diagram, codeBoxPair.getBodyText(), returnCall);
	}
	
	private void resetOperationBody() {
		codeBoxPair.setBodyText(StringValue.OperationStringValues.emptyOperation);
	}
	
	public class Result {
		
		public final FmmlxObject object;
		public final FmmlxOperation selectedItem;
		public final String body;

		public Result(FmmlxObject object, FmmlxOperation selectedItem, String text) {
			this.object = object;
			this.selectedItem = selectedItem;
			this.body = text;
		}
	}

}
