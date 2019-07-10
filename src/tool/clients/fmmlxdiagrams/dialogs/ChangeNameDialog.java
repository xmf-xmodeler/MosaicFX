package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeNameDialogResult;
import tool.clients.fmmlxdiagrams.stringvalue.StringValueDialog;

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
	
	//For Attribute
	private Label selectAttributeLabel;
	private ComboBox<FmmlxAttribute> selectAttributeComboBox;
	
	//For Operation
	private Label selectOperationLabel;
	private ComboBox<FmmlxOperation> selectOperationComboBox;
	
	//For Association
	private Label selectAssociationNameLabel;
	private ComboBox<FmmlxAssociation> selectAssociationBox;
	

	private Vector<FmmlxAttribute> attributes;
	private Vector<FmmlxOperation> operations;
	private Vector<FmmlxAssociation> associations;

	// Used for combobox -> displays strings

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
			//setSelectedProperty();
		}
		dialog.setContent(flow);
		setValidation();
		setResult(type);
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
						return new ChangeNameDialogResult(type, object, selectAttributeComboBox.getSelectionModel().getSelectedItem(), newNameTextField.getText());
					case Operation:
						return new ChangeNameDialogResult(type, object, selectOperationComboBox.getSelectionModel().getSelectedItem(), newNameTextField.getText());
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

	private void changeAssociationName() {
		//insert Association List to Combobox;
		
		classNameTextfield.setText(object.getName());
		classNameTextfield.setDisable(true);

		selectAssociationNameLabel = new Label("Select Association");
		selectAssociationBox = new ComboBox<FmmlxAssociation>();

		selectAssociationBox.setPrefWidth(COLUMN_WIDTH);
		newNameLabel = new Label("New Attribute Name");
		newNameTextField= new TextField();

		grid.add(selectAssociationNameLabel, 0, 1);
		grid.add(selectAssociationBox, 1, 1);
		grid.add(newNameLabel, 0, 2);
		grid.add(newNameTextField, 1, 2);

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
		attributeList =  FXCollections.observableList(attributes);
		
		selectAttributeLabel = new Label("Select Attribute");
		selectAttributeComboBox = initializeAttributeComboBox(attributeList);
		
		newNameLabel = new Label("New Attribute Name");
		newNameTextField = new TextField();
		
		selectAttributeComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(selectAttributeLabel, 0, 1);
		grid.add(selectAttributeComboBox, 1, 1);
		grid.add(newNameLabel, 0, 2);
		grid.add(newNameTextField, 1, 2);
	}

	private void changeOperationName() {
		operations = object.getOwnOperations();
		operations.addAll(object.getOtherOperations());
		
		ObservableList<FmmlxOperation> operationList;
		operationList =  FXCollections.observableList(operations);
		selectOperationLabel = new Label("Select Operation");
		selectOperationComboBox = initializeOperationComboBox(operationList);
		
		newNameLabel = new Label("New Operation Name");
		newNameTextField = new TextField();
		
		selectOperationComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(selectOperationLabel, 0, 1);
		grid.add(selectOperationComboBox, 1, 1);
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
		
		if (selectAssociationBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAssociation);
			return false;
		}
		if (!InputChecker.getInstance().validateName(name)) {	
			errorLabel.setText(StringValueDialog.ErrorMessage.enterValidName);
			return false;
		} else if (!InputChecker.getInstance().associationNameIsAvailable(name, object)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.nameAlreadyUsed);
			return false;
		}
		return true;
	}

	private boolean validateClassName() {
		String name = newNameTextField.getText();

		if (!InputChecker.getInstance().validateName(name)) {	
			errorLabel.setText(StringValueDialog.ErrorMessage.enterValidName);
			return false;
		} else if (!InputChecker.getInstance().classNameIsAvailable(name, diagram)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.nameAlreadyUsed);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private boolean validateOperationName() {
		String name = newNameTextField.getText();
		
		if (selectOperationComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectOperation);
			return false;
		}
		
		if (!InputChecker.getInstance().validateName(name)) {	
			errorLabel.setText(StringValueDialog.ErrorMessage.enterValidName);
			return false;
		} else if (!InputChecker.getInstance().attributeNameIsAvailable(name, object)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.nameAlreadyUsed);
			return false;
		}
		return true;
	}

	private boolean validateAttributeName() {
		String name = newNameTextField.getText();
		
		if (selectAttributeComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAttribute);
			return false;
		}
		if (!InputChecker.getInstance().validateName(name)) {	
			errorLabel.setText(StringValueDialog.ErrorMessage.enterValidName);
			return false;
		} else if (!InputChecker.getInstance().attributeNameIsAvailable(name, object)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.nameAlreadyUsed);
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
