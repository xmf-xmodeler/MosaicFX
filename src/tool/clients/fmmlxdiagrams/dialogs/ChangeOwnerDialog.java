package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeOwnerDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

import java.util.Vector;

public class ChangeOwnerDialog extends CustomDialog<ChangeOwnerDialogResult> {

	private DialogPane dialogPane;
	private final PropertyType type;
	private FmmlxObject object;
	private Vector<FmmlxObject> objects;

	//For Attribute
	private Label selectAttribute;
	private ComboBox<FmmlxAttribute> selectAttributeComboBox;

	//For Operation
	private Label selectOperation;
	private ComboBox<FmmlxOperation> selectOperationComboBox;

	//For All
	private Label classLabel;
	private Label newOwnerLabel;

	private TextField classNameTextfield;
	private ComboBox<FmmlxObject> newOwnerComboBox;

	private Vector<FmmlxAttribute> attributes;
	private Vector<FmmlxOperation> operations;


	public ChangeOwnerDialog(FmmlxDiagram diagram, FmmlxObject object, PropertyType type) {
		super();
		this.object = object;
		this.type = type;
		this.objects = diagram.getObjects();

		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent();
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
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				switch (type) {
					case Attribute:
						return new ChangeOwnerDialogResult(type, object,
								selectAttributeComboBox.getSelectionModel().getSelectedItem(), newOwnerComboBox.getSelectionModel().getSelectedItem());
					case Operation:
						return new ChangeOwnerDialogResult(type, object,
								selectOperationComboBox.getSelectionModel().getSelectedItem(), newOwnerComboBox.getSelectionModel().getSelectedItem());
					default:
						System.err.println("ChangeOwnerDialog: No matching content type!");
				}
			}
			return null;
		});
	}

	private boolean validateUserInput() {
		switch (type) {
			case Attribute:
				return validateChangeOwnerAttribute();
			case Operation:
				return validateChangeOwnerOperation();
			default:
				System.err.println("ChangeOwnerDialog: No matching content type!");
		}
		return false;
	}


	private boolean validateChangeOwnerOperation() {
		if (selectOperationComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectOperation);
			return false;
		} else if (newOwnerComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectNewOwner);
			return false;
		} else if (newOwnerComboBox.getSelectionModel().getSelectedItem() == object) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAnotherClass);
			return false;
		}
		return true;
	}


	private boolean validateChangeOwnerAttribute() {
		if (selectAttributeComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAttribute);
			return false;
		} else if (newOwnerComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectNewOwner);
			return false;
		} else if (newOwnerComboBox.getSelectionModel().getSelectedItem() == object) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAnotherClass);
			return false;
		}
		return true;
	}


	private void layoutContent() {
		classLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectedObject);

		newOwnerLabel = new Label(StringValueDialog.LabelAndHeaderTitle.newOwner);

		classNameTextfield = new TextField();
		classNameTextfield.setText(object.getName());
		classNameTextfield.setDisable(true);


		attributes = object.getOwnAttributes();
		attributes.addAll(object.getOtherAttributes());


		ObservableList<FmmlxObject> objectList = FXCollections.observableList(objects);
		objectList.remove(object);
		newOwnerComboBox = (ComboBox<FmmlxObject>) initializeComboBox(objectList);

		newOwnerComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(classLabel, 0, 0);
		grid.add(classNameTextfield, 1, 0);

		grid.add(newOwnerLabel, 0, 3);
		grid.add(newOwnerComboBox, 1, 3);
		switch (type) {
			case Attribute:
				dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.changeAttributeOwner);
				attributes = object.getOwnAttributes();
				attributes.addAll(object.getOtherAttributes());

				ObservableList<FmmlxAttribute> attributeList;
				attributeList = FXCollections.observableList(attributes);

				selectAttribute = new Label(StringValueDialog.LabelAndHeaderTitle.selectAttribute);
				selectAttributeComboBox = (ComboBox<FmmlxAttribute>) initializeComboBox(attributeList);
				selectAttributeComboBox.setPrefWidth(COLUMN_WIDTH);
				grid.add(selectAttribute, 0, 1);
				grid.add(selectAttributeComboBox, 1, 1);
				break;
			case Operation:
				dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.changeOperationOwner);
				operations = object.getOwnOperations();
				operations.addAll(object.getOtherOperations());

				ObservableList<FmmlxOperation> operationList;
				operationList = FXCollections.observableList(operations);
				selectOperation = new Label(StringValueDialog.LabelAndHeaderTitle.selectOperation);
				selectOperationComboBox = (ComboBox<FmmlxOperation>) initializeComboBox(operationList);
				selectOperationComboBox.setPrefWidth(COLUMN_WIDTH);
				grid.add(selectOperation, 0, 1);
				grid.add(selectOperationComboBox, 1, 1);
				break;
			default:
				System.err.println("AddDialog: No matching content type!");
		}

	}


	public void setSelected(FmmlxProperty selectedProperty) {
		if (type == PropertyType.Attribute) {
			selectAttributeComboBox.getSelectionModel().select((FmmlxAttribute) selectedProperty);
		} else if (type == PropertyType.Operation) {
			selectOperationComboBox.getSelectionModel().select((FmmlxOperation) selectedProperty);
		}
	}
}
