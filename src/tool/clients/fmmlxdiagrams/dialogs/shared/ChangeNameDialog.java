package tool.clients.fmmlxdiagrams.dialogs.shared;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.InputChecker;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.Vector;

public class ChangeNameDialog<Property extends FmmlxProperty> extends CustomDialog<ChangeNameDialog<Property>.Result> {

	private final PropertyType type;
	private final AbstractPackageViewer diagram;
	private FmmlxObject object;
	private Property selectedProperty;
	private DialogPane dialog;

	//For All
	private Label classLabel;
	private TextField classNameTextfield;
	private Label newNameLabel;
	private TextField newNameTextField = new TextField();

	//ComboBox used for attribute, operation & association
	private ComboBox<Property> comboBox;

	//For Attribute
	private Label selectAttributeLabel;

	//For Operation
	private Label selectOperationLabel;

	//For Association
	private Label selectAssociationNameLabel;

	public ChangeNameDialog(final AbstractPackageViewer diagram, FmmlxObject object, PropertyType type, Property selectedProperty) {
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
						return new Result(type, object, object.getName(), newNameTextField.getText());
					case Attribute:
						return new Result(type, object, ((FmmlxAttribute) comboBox.getSelectionModel().getSelectedItem()).getName(), newNameTextField.getText());
					case Operation:
						return new Result(type, object, ((FmmlxOperation) comboBox.getSelectionModel().getSelectedItem()).getName(), newNameTextField.getText());
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
		Vector<FmmlxAttribute> attributes = object.getAllAttributes();

		ObservableList<Property> attributeList;
		attributeList = FXCollections.observableList((Vector<Property>) attributes);

		selectAttributeLabel = new Label("Select Attribute");
		comboBox = initializeComboBox(attributeList);

		newNameLabel = new Label("New Attribute Name");
		newNameTextField = new TextField();


		grid.add(selectAttributeLabel, 0, 1);
		grid.add(comboBox, 1, 1);
		grid.add(newNameLabel, 0, 2);
		grid.add(newNameTextField, 1, 2);
	}
	
	private void changeAssociationName() {
		//insert Association List to Combobox;
		Vector<FmmlxAssociation> associations = object.getAllRelatedAssociations();
		
		ObservableList<Property> associationsList;
		associationsList = FXCollections.observableList((Vector<Property>) associations);

		classNameTextfield.setText(object.getName());
		classNameTextfield.setDisable(true);

		selectAssociationNameLabel = new Label("Select Association");
		comboBox = initializeComboBox(associationsList);
		
		comboBox.setPrefWidth(COLUMN_WIDTH);
		newNameLabel = new Label("New Attribute Name");
		newNameTextField= new TextField();

		grid.add(selectAssociationNameLabel, 0, 1);
		grid.add(comboBox, 1, 1);
		grid.add(newNameLabel, 0, 2);
		grid.add(newNameTextField, 1, 2);
	}

	private void changeOperationName() {
		Vector<FmmlxOperation> operations = object.getAllOperations();

		ObservableList<Property> operationList;
		operationList = FXCollections.observableList((Vector<Property>) operations);
		selectOperationLabel = new Label("Select Operation");
		comboBox = initializeComboBox(operationList);

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
		} else if (!InputChecker.validateName(name)) {
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

		if (!InputChecker.validateName(name)) {
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

		if (!InputChecker.validateName(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidName);
			return false;
//		} else if (!InputChecker.getInstance().attributeNameIsAvailable(name, object)) {
//			errorLabel.setText(StringValue.ErrorMessage.nameAlreadyUsed);
//			return false;
		}
		return true;
	}

	private boolean validateAttributeName() {
		String name = newNameTextField.getText();

		if (comboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValue.ErrorMessage.selectAttribute);
			return false;
		}
		if (!InputChecker.validateName(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidName);
			return false;
//		} else if (!InputChecker.getInstance().attributeNameIsAvailable(name, object)) {
//			errorLabel.setText(StringValue.ErrorMessage.nameAlreadyUsed);
//			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}
	
	public class Result {

		public final PropertyType type;
		public final FmmlxObject object;
		public final String oldName;
		public final String newName;

		public Result(PropertyType type, FmmlxObject object, String oldName, String newName) {
			this.type = type;
			this.object = object;
			this.oldName = oldName;
			this.newName = newName;
		}
		
		public String getObjectName() {
			return object.getName();
		}
	}
}
