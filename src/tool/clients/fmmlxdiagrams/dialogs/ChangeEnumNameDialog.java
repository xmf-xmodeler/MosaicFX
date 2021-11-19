package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxEnum;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

public class ChangeEnumNameDialog extends CustomDialog<ChangeEnumNameDialog.Result>{
	
	private AbstractPackageViewer diagram;
	private FmmlxEnum selectedEnum;
	private Label currentNameLabel;
	private Label newNameLabel;
	
	private TextField currentNameTextField;
	private TextField newNameTextField;
	

	public ChangeEnumNameDialog(AbstractPackageViewer diagram, FmmlxEnum selectedItem) {
		super();
		this.selectedEnum=selectedItem;
		this.diagram=diagram;
		
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		dialogPane.setHeaderText("Change Enum Name");

		addElementToGrid();

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
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {				
				return new Result(selectedEnum.getName(),newNameTextField.getText());
			}
			return null;
		});
		
	}

	private boolean validateUserInput() {
		if (!validateName()) {	
			return false;
		} 
		
		for (FmmlxEnum tmp : diagram.getEnums()) {
			if (tmp.getName().equals(newNameTextField.getText())) {
				errorLabel.setText(StringValue.ErrorMessage.enumAlreadyExist);
				return false;
			}
		}
		errorLabel.setText("");
		return true;
	}
	
	private boolean validateName() {
		String name = newNameTextField.getText();

		if (!InputChecker.isValidIdentifier(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidName);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private void addElementToGrid() {
		currentNameLabel = new Label("Current Name");
		newNameLabel = new Label ("New Name");
		
		currentNameTextField = new TextField();
		currentNameTextField.setText(selectedEnum.getName());
		currentNameTextField.setDisable(true);
		currentNameTextField.setEditable(false);
		
		newNameTextField= new TextField();
		
		List<Node> labelNode = new ArrayList<Node>(); 
		List<Node> editorNode = new ArrayList<Node>();
		
		labelNode.add(currentNameLabel);
		labelNode.add(newNameLabel);
		
		editorNode.add(currentNameTextField);
		editorNode.add(newNameTextField);
		
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(editorNode, 1);
	}

	public class Result {

		public final String oldName;
		public final String newName;	
		
		public Result(String oldName, String newName) {
			this.oldName = oldName;
			this.newName = newName;
		}
	}
}
