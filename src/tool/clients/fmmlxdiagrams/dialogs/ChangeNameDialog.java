package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeNameDialogResult;

import java.util.ArrayList;
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
	private ComboBox<String> selectAttributeComboBox;
	
	//For Operation
	private Label selectOperationLabel;
	private ComboBox<String> selectOperationComboBox;
	
	//For Association
	private Label selectAssociationNameLabel;
	private ComboBox<String> selectAssociationBox;
	

	private Vector<FmmlxAttribute> attributes;
	private Vector<FmmlxOperation> operations;

	// Used for combobox -> displays strings
	private ArrayList<String> list;

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
		grid.add(classLabel, 0, 0);
		grid.add(classNameTextfield, 1, 0);
		list = new ArrayList<String>();

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

	private void setSelectedProperty() {
		if (type==PropertyType.Attribute) {
			selectAttributeComboBox.getSelectionModel().select(selectedProperty.getName());
		} else if (type==PropertyType.Operation) {
			selectOperationComboBox.getSelectionModel().select(selectedProperty.getName());
		} else if (type==PropertyType.Association) {
			selectAssociationBox.getSelectionModel().select(selectedProperty.getName());
		}
	}

	private void changeAssociationName() {
		classNameTextfield.setText(object.getName());
		classNameTextfield.setDisable(true);

		selectAssociationNameLabel = new Label("Select Association");
		selectAssociationBox = new ComboBox<String>();

		selectAssociationBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(selectAssociationNameLabel, 0, 1);
		grid.add(selectAssociationBox, 1, 1);
		grid.add(newNameTextField, 0, 2);
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
		// TODO: Add association

		attributes = object.getOwnAttributes();
		attributes.addAll(object.getOtherAttributes());
		for (FmmlxAttribute att : attributes) {
			list.add(att.getName());
		}
		layoutComboBox(list);
	}

	private void changeOperationName() {
		operations = object.getOwnOperations();
		for (FmmlxOperation op : operations) {
			list.add(op.getName());
		}
		layoutComboBox(list);
	}

	private void layoutComboBox(ArrayList<String> list) {
		/*
		 * classNameTextfield.setText(object.getName());
		 * classNameTextfield.setDisable(true); selectAttributeLabel = new
		 * Label("Select Attribute"); comboBox = new ComboBox<>();
		 * comboBox.setPrefWidth(COLUMN_WIDTH); comboBox.getItems().setAll(list); Label
		 * newNameLabel = new Label("New name"); newNameTextField = new TextField();
		 * 
		 * comboBox.getSelectionModel().selectedItemProperty().addListener((observable,
		 * oldValue, newValue) -> { newNameTextField.setText(newValue); });
		 * 
		 * grid.add(selectAttributeLabel, 0, 1); grid.add(comboBox, 1, 1);
		 * grid.add(newNameLabel, 0, 2); grid.add(newNameTextField, 1, 2);
		 */
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
			default:
				System.err.println("AddDialog: No matching content type!");
				break;
		}
		return true;
	}

	private boolean validateClassName() {
		Label errorLabel = getErrorLabel();
		String name = newNameTextField.getText();

		if (!InputChecker.getInstance().validateName(name)) {	
			errorLabel.setText("Enter valid name!");
			return false;
		} else if (!InputChecker.getInstance().classNameIsAvailable(name, diagram)) {
			errorLabel.setText("Name already used");
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private boolean validateOperationName() {
		String newName = newNameTextField.getText();
		for (FmmlxOperation operation : operations) {
			if (operation.getName().equals(newName)) {
				showNameUsedError();
				return false;
			}
		}
		return true;
	}

	private boolean validateAttributeName() {
		Label errorLabel = getErrorLabel();
		String name = newNameTextField.getText();

		if (!InputChecker.getInstance().validateName(name)) {	
			errorLabel.setText("Enter valid name!");
			return false;
		} else if (!InputChecker.getInstance().attributeNameIsAvailable(name, object)) {
			errorLabel.setText("Name already used");
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
