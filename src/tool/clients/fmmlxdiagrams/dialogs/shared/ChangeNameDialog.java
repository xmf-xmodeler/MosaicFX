package tool.clients.fmmlxdiagrams.dialogs.shared;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.InputChecker;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeNameDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.Vector;

public class ChangeNameDialog extends CustomDialog<ChangeNameDialogResult> {

	private final PropertyType type;
	private final FmmlxDiagram diagram;
	private FmmlxObject object;
	private FmmlxProperty selectedProperty;
	private DialogPane dialog;

	//For All
	private Label classLabel;
	private TextField classNameTextfield;
	private Label newNameLabel;
	private TextField newNameTextField = new TextField();

	//ComboBox used for attribute, operation & association
	private ComboBox<FmmlxProperty> comboBox;

	//For Attribute
	private Label selectAttributeLabel;

	//For Operation
	private Label selectOperationLabel;

	//For Association
	private Label selectAssociationNameLabel;


	private Vector<FmmlxAttribute> attributes;
	private Vector<FmmlxOperation> operations;
	private Vector<FmmlxAssociation> associations;

	public ChangeNameDialog(final FmmlxDiagram diagram, FmmlxObject object, PropertyType type, FmmlxProperty selectedProperty) {
		super();
		this.diagram = diagram;
		this.type = type;
		this.object = object;
		this.selectedProperty = selectedProperty;

		dialog = getDialogPane();

		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent(type);
		if (selectedProperty != null && type != PropertyType.Class) {
			setSelectedProperty();
		}
		dialog.setContent(flow);
		setValidation();
		setResult(type);
	}

	private void setSelectedProperty() {
		if (comboBox != null) {
			comboBox.getSelectionModel().select(selectedProperty);
		}
	}

	public ChangeNameDialog(final FmmlxDiagram diagram, FmmlxObject object, PropertyType type) {
		this(diagram, object, type, null);
	}

	private void setResult(PropertyType type) {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				switch (type) {
					case Class:
						return new ChangeNameDialogResult(object, newNameTextField.getText());
					case Attribute:
						return new ChangeNameDialogResult(type, object, (FmmlxAttribute) comboBox.getSelectionModel().getSelectedItem(), newNameTextField.getText());
					case Operation:
						return new ChangeNameDialogResult(type, object, (FmmlxOperation) comboBox.getSelectionModel().getSelectedItem(), newNameTextField.getText());
					default:
						System.err.println("ChangeNameDialog: No matching content type!");
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

	private void layoutContent(PropertyType type) {
		classLabel = new Label("Class");
		classNameTextfield = new TextField();
		classNameTextfield.setText(object.getName());
		classNameTextfield.setDisable(true);
		grid.add(classLabel, 0, 0);
		grid.add(classNameTextfield, 1, 0);

		switch (type) {
			case Class:
				dialog.setHeaderText("Change Class Name");
				changeClassName();
				break;
			case Attribute:
				dialog.setHeaderText("Change Attribute Name");
				changeAttributeName();
				break;
			case Operation:
				dialog.setHeaderText("Change Operation Name");
				changeOperationName();
				break;
			case Association:
				dialog.setHeaderText("Change Association Name");
				changeAssociationName();
			default:
				System.err.println("ChangeNameDialog: No matching content type!");
		}
	}


	private void changeClassName() {
		classNameTextfield.setText(object.getName());
		classNameTextfield.setDisable(true);

		Label newClassNameLabel = new Label("New Name");
		newNameTextField = new TextField();

		grid.add(newClassNameLabel, 0, 1);
		grid.add(newNameTextField, 1, 1);

	}

	private void changeAttributeName() {
		attributes = object.getOwnAttributes();
		attributes.addAll(object.getOtherAttributes());

		ObservableList<FmmlxAttribute> attributeList;
		attributeList = FXCollections.observableList(attributes);

		selectAttributeLabel = new Label("Select Attribute");
		comboBox = (ComboBox<FmmlxProperty>) initializeComboBox(attributeList);

		newNameLabel = new Label("New Attribute Name");
		newNameTextField = new TextField();


		grid.add(selectAttributeLabel, 0, 1);
		grid.add(comboBox, 1, 1);
		grid.add(newNameLabel, 0, 2);
		grid.add(newNameTextField, 1, 2);
	}
	
	private void changeAssociationName() {
		//insert Association List to Combobox;
		associations = object.getAllRelatedAssociations();
		
		ObservableList<FmmlxAssociation> associationsList;
		associationsList = FXCollections.observableList(associations);

		classNameTextfield.setText(object.getName());
		classNameTextfield.setDisable(true);

		selectAssociationNameLabel = new Label("Select Association");
		comboBox = (ComboBox<FmmlxProperty>) initializeComboBox(associationsList);

		
		comboBox.setPrefWidth(COLUMN_WIDTH);
		newNameLabel = new Label("New Attribute Name");
		newNameTextField= new TextField();

		grid.add(selectAssociationNameLabel, 0, 1);
		grid.add(comboBox, 1, 1);
		grid.add(newNameLabel, 0, 2);
		grid.add(newNameTextField, 1, 2);

	}

	private void changeOperationName() {
		operations = object.getOwnOperations();
		operations.addAll(object.getOtherOperations());

		ObservableList<FmmlxOperation> operationList;
		operationList = FXCollections.observableList(operations);
		selectOperationLabel = new Label("Select Operation");
		comboBox = (ComboBox<FmmlxProperty>) initializeComboBox(operationList);

		newNameLabel = new Label("New Operation Name");
		newNameTextField = new TextField();

		grid.add(selectOperationLabel, 0, 1);
		grid.add(comboBox, 1, 1);
		grid.add(newNameLabel, 0, 2);
		grid.add(newNameTextField, 1, 2);

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
			case Association:
				return validateAssociationName();
			default:
				System.err.println("AddDialog: No matching content type!");
				break;
		}
		return true;
	}

	private boolean validateAssociationName() {
		String name = newNameTextField.getText();

		if (comboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValue.ErrorMessage.selectAssociation);
			return false;
		} else if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidName);
			return false;
		} else if (!InputChecker.getInstance().associationNameIsAvailable(name, object)) {
			errorLabel.setText(StringValue.ErrorMessage.nameAlreadyUsed);
			return false;
		}
		return true;
	}

	private boolean validateClassName() {
		String name = newNameTextField.getText();

		if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidName);
			return false;
		} else if (!InputChecker.getInstance().classNameIsAvailable(name, diagram)) {
			errorLabel.setText(StringValue.ErrorMessage.nameAlreadyUsed);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private boolean validateOperationName() {
		String name = newNameTextField.getText();

		if (comboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValue.ErrorMessage.selectOperation);
			return false;
		}

		if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidName);
			return false;
		} else if (!InputChecker.getInstance().attributeNameIsAvailable(name, object)) {
			errorLabel.setText(StringValue.ErrorMessage.nameAlreadyUsed);
			return false;
		}
		return true;
	}

	private boolean validateAttributeName() {
		String name = newNameTextField.getText();

		if (comboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValue.ErrorMessage.selectAttribute);
			return false;
		}
		if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidName);
			return false;
		} else if (!InputChecker.getInstance().attributeNameIsAvailable(name, object)) {
			errorLabel.setText(StringValue.ErrorMessage.nameAlreadyUsed);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private void showNameUsedError() {
		errorLabel.setText("Name already used");
	}
}
