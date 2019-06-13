package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeNameDialogResult;

import java.util.ArrayList;
import java.util.Vector;

public class ChangeNameDialog extends CustomDialog<ChangeNameDialogResult> {

	private final DialogType type;
	private final FmmlxDiagram diagram;
	private FmmlxObject object;

	private TextField classNameTextfield;
	private ComboBox<String> comboBox;
	private TextField objectNameTextfield;

	private Vector<FmmlxAttribute> attributes;
	private Vector<FmmlxOperation> operations;

	// Used for combobox -> displays strings
	private ArrayList<String> list;

	public ChangeNameDialog(final FmmlxDiagram diagram, FmmlxObject object, DialogType type) {
		super();
		this.diagram = diagram;
		this.type = type;
		this.object = object;

		DialogPane dialog = getDialogPane();
		dialog.setHeaderText("Change name");

		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent();
		dialog.setContent(flow);

		setValidation();
		setResult();
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				if (type.equals("class")) {
					return new ChangeNameDialogResult(type, object, classNameTextfield.getText());
				} else if (type.equals("attribute") || type.equals("operation")) {
					return new ChangeNameDialogResult(type, object, comboBox.getSelectionModel().getSelectedItem(), objectNameTextfield.getText());
				}
			}
			return null;
		});
	}

	private void setValidation() {
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateInput()) {
				e.consume();
			}
		});
	}

	private void layoutContent() {
		Label classLabel = new Label("Class");
		classNameTextfield = new TextField();
		grid.add(classLabel, 0, 0);
		grid.add(classNameTextfield, 1, 0);
		list = new ArrayList<String>();

		switch (type) {
			case Class:
				changeClass();
				break;
			case Attribute:
				changeAttribute();
				break;
			case Operation:
				changeOperation();
				break;
			default:
				System.err.println("ChangeNameDialog: No matching content type!");
		}
	}

	private void changeClass() {
		classNameTextfield.setText(object.getName());
	}

	private void changeAttribute() {
		// TODO: Add association

		attributes = object.getOwnAttributes();
		attributes.addAll(object.getOtherAttributes());
		for (FmmlxAttribute att : attributes) {
			list.add(att.getName());
		}
		layoutComboBox(list);
	}

	private void changeOperation() {
		operations = object.getOwnOperations();
		for (FmmlxOperation op : operations) {
			list.add(op.getName());
		}
		layoutComboBox(list);
	}

	private void layoutComboBox(ArrayList<String> list) {
		classNameTextfield.setText(object.getName());
		classNameTextfield.setDisable(true);
		Label objectLabel = new Label("Select");
		comboBox = new ComboBox<>();
		comboBox.setPrefWidth(COLUMN_WIDTH);
		comboBox.getItems().setAll(list);
		Label nameLabel = new Label("Name");
		objectNameTextfield = new TextField();

		comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			objectNameTextfield.setText(newValue);
		});

		grid.add(objectLabel, 0, 1);
		grid.add(comboBox, 1, 1);
		grid.add(nameLabel, 0, 2);
		grid.add(objectNameTextfield, 1, 2);
	}

	/*
	 * Validation of user input -> check if new name is already used
	 */

	private boolean validateInput() {
		switch (type) {
			case Class:
				return validateClassName();
			case Attribute:
				return validateAttributeName();
			case Operation:
				return validateOperationName();
		}
		return true;
	}

	private boolean validateClassName() {
		String newName = classNameTextfield.getText();
		for (FmmlxObject object : diagram.getObjects()) {
			if (object.getName().equals(newName)) {
				showNameUsedError();
				return false;
			}
		}
		return true;
	}

	private boolean validateOperationName() {
		String newName = objectNameTextfield.getText();
		for (FmmlxOperation operation : operations) {
			if (operation.getName().equals(newName)) {
				showNameUsedError();
				return false;
			}
		}
		return true;
	}

	private boolean validateAttributeName() {
		String newName = objectNameTextfield.getText();
		for (FmmlxAttribute attribute : attributes) {
			if (attribute.getName().equals(newName)) {
				showNameUsedError();
				return false;
			}
		}
		return true;
	}

	private void showNameUsedError() {
		errorLabel.setText("Name already used");
	}
}
