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
import tool.clients.fmmlxdiagrams.dialogs.results.AddEnumElementDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

public class AddEnumElement extends CustomDialog<AddEnumElementDialogResult>{
	private Label inputElementNameLabel;
	private TextField inputElementNameTextField;

	public AddEnumElement() {
		super();

		
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

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
				return new AddEnumElementDialogResult(inputElementNameTextField.getText());
			}
			return null;
		});
	}

	private boolean validateUserInput() {
		String name = inputElementNameTextField.getText();

		if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.enterValidName);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private void addElementToGrid() {
		
		inputElementNameLabel = new Label ("Type Element Name!");
		inputElementNameTextField = new TextField();
		
		List<Node> mainNode = new ArrayList<Node>(); 
		
		mainNode.add(inputElementNameLabel);
		mainNode.add(inputElementNameTextField);
		
		addNodesToGrid(mainNode);
		
	}

	
}
