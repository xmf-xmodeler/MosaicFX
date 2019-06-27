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

	private TextField classNameTextfield;
	private ComboBox<String> comboBox;
	private TextField objectNameTextfield;
	private TextField newClassNameTextField = new TextField();
	
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
		layoutContent();
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

	private void setResult() {
		setResultConverter(dlgBtn -> {
			switch (type) {
			case Class:
				return new ChangeNameDialogResult(type, object, classNameTextfield.getText());
			case Attribute:
				return new ChangeNameDialogResult(type, object, comboBox.getSelectionModel().getSelectedItem(), objectNameTextfield.getText());
			case Operation:
				return new ChangeNameDialogResult(type, object, comboBox.getSelectionModel().getSelectedItem(), objectNameTextfield.getText());
			default:
			System.err.println("ChangeNameDialog: No matching content type!");
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

	private void layoutContent(DialogType type) {
		Label classLabel = new Label("Class");
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
		comboBox.getSelectionModel().select(selectedProperty.getName());
	}

	private void changeClass() {
	private void changeAssociationName() {
		classNameTextfield.setText(object.getName());
		classNameTextfield.setDisable(true);
		
		Label selectAssociationNameLabel = new Label("Select Association");
		Label newAssociationNameLabel = new Label ("New Name");
		
		ComboBox<String> selectAssociationBox = new ComboBox<String>();
		TextField newAssociationNameTextField = new TextField();
		
		selectAssociationBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(selectAssociationNameLabel, 0, 1);
		grid.add(selectAssociationBox, 1, 1);
		grid.add(newAssociationNameLabel, 0, 2);
		grid.add(newAssociationNameTextField, 1, 2);
		
	}

	private void changeClassName() {
		classNameTextfield.setText(object.getName());
		classNameTextfield.setDisable(true);
		
		Label newClassNameLabel = new Label("New Name");
		newClassNameTextField = new TextField();
		
		grid.add(newClassNameLabel, 0, 1);
		grid.add(newClassNameTextField, 1, 1);
		
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
		classNameTextfield.setText(object.getName());
		classNameTextfield.setDisable(true);
		Label objectLabel = new Label("Name");
		comboBox = new ComboBox<>();
		comboBox.setPrefWidth(COLUMN_WIDTH);
		comboBox.getItems().setAll(list);
		Label nameLabel = new Label("New name");
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
			default:System.err.println("AddDialog: No matching content type!");	
			break;
		}
		return true;
	}

	private boolean validateClassName() {
		String newName = newClassNameTextField.getText();
		
		if (isNullOrEmpty(newName) ) {
			errorLabel.setText("Enter new name!");
			return false;
		} 
		
		for (FmmlxObject object : diagram.getObjects()) { 
			if (object.getName().equals(newName)) { 
				showNameUsedError(); return false; 
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
