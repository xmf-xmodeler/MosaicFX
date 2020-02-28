
package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tool.clients.fmmlxdiagrams.FmmlxEnum;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.dialogs.results.AddEnumerationDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import javafx.scene.Node;

public class AddEnumerationDialog extends CustomDialog<AddEnumerationDialogResult>{
	private Label nameLabel;
	
	private TextField nameTextField;

	

	public AddEnumerationDialog(FmmlxDiagram diagram) {
		super();
		DialogPane dialogPane = getDialogPane();

		
		
		dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
		dialogPane.setHeaderText("Create Enumeration");

		addElementToGrid();

		dialogPane.setContent(flow);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		
		
		 okButton.addEventFilter(ActionEvent.ACTION, e -> {
			 if (!validateUserInput())
			 	{ e.consume(); 
			 } 
		});
		 

		setResult();
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				Vector<String> elements = new Vector<String>();
				return new AddEnumerationDialogResult(new FmmlxEnum(nameTextField.getText(),elements));
			}
			return null;
		});
		
	}

	private boolean validateUserInput() {
		if (!validateName()) {	
			return false;
		} 
		errorLabel.setText("");
		return true;
	}

	private boolean validateName() {
		String name = nameTextField.getText();

		if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidName);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private void addElementToGrid() {
		nameLabel = new Label("Name");
		
		nameTextField = new TextField();
		nameTextField.isEditable();
		
		List<Node> labelNode = new ArrayList<Node>();
		List<Node> editorNode = new ArrayList<Node>();

		labelNode.add(nameLabel);
		editorNode.add(nameTextField);
		
		addNodesToGrid(labelNode,0);
		addNodesToGrid(editorNode, 1);
	}


}

