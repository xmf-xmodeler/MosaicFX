package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.dialogs.results.RemoveDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

import java.util.Vector;

public class RemoveDialog extends CustomDialog<RemoveDialogResult> {

	private DialogPane dialogPane;
	private final PropertyType type;
	private FmmlxObject object;
	private final FmmlxDiagram diagram;

	//For All

	private Label selectObjectLabel;
	private Label selectionForStrategies;
	private TextField selectObjectLabelTextField;
	private ComboBox<String> selectionForStrategiesComboBox;


	//For Association
	private Label selectAssociationLabel;
	private ComboBox<FmmlxAssociation> selectAssociationComboBox;

	//For Attribute
	private Label selectAttribute;
	private ComboBox<FmmlxAttribute> selectAttributeComboBox;

	//For Operation
	private Label selectOperation;
	private ComboBox<FmmlxOperation> selectOperationComboBox;


	private Vector<FmmlxAttribute> attributes;
	private Vector<FmmlxOperation> operations;
	private Vector<FmmlxAssociation> associations;


	public RemoveDialog(final FmmlxDiagram diagram, FmmlxObject object, PropertyType type) {
		super();
		this.type = type;
		this.diagram = diagram;
		this.object = object;
		dialogPane = getDialogPane();

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		addElementToGrid(type);

		dialogPane.setContent(flow);


		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				switch (type) {
					case Class:
						return new RemoveDialogResult(type, object);
					case Attribute:
						return new RemoveDialogResult(type, object, selectAttributeComboBox.getSelectionModel().getSelectedItem());
					case Operation:
						return new RemoveDialogResult(type, object, selectOperationComboBox.getSelectionModel().getSelectedItem());
					default:
						System.err.println("ChangeNameDialog: No matching content type!");
				}
			}
			return null;
		});
	}


	private void addElementToGrid(PropertyType type) {

		switch (type) {
			case Class:
				removeClass();
				break;
			case Attribute:
				removeAttribute();
				break;
			case Association:
				removeAssoiation();
				break;
			case Operation:
				removeOperation();
			default:
				break;
		}
	}

	private void removeAssoiation() {
		//insert Association List to Combobox;

		dialogPane.setHeaderText("Remove Association");
		selectObjectLabel = new Label("Selected Object");
		selectAssociationLabel = new Label("Select Association");
		selectionForStrategies = new Label("Selection for Strategies");

		selectObjectLabelTextField = new TextField();
		selectObjectLabelTextField.setText(object.getName());
		selectObjectLabelTextField.setDisable(true);
		selectAssociationComboBox = new ComboBox<FmmlxAssociation>();
		selectionForStrategiesComboBox = new ComboBox<String>();

		selectAssociationComboBox.setPrefWidth(COLUMN_WIDTH);
		selectionForStrategiesComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(selectObjectLabel, 0, 0);
		grid.add(selectObjectLabelTextField, 1, 0);
		grid.add(selectAssociationLabel, 0, 1);
		grid.add(selectAssociationComboBox, 1, 1);
		grid.add(selectionForStrategies, 0, 2);
		grid.add(selectionForStrategiesComboBox, 1, 2);

	}


	private void removeOperation() {
		dialogPane.setHeaderText("Remove Operation");

		operations = object.getOwnOperations();
		operations.addAll(object.getOtherOperations());

		ObservableList<FmmlxOperation> operationList;
		operationList = FXCollections.observableList(operations);

		selectObjectLabel = new Label("Selected Object");
		selectOperation = new Label("Select Operation");
		selectionForStrategies = new Label("Selection for Strategies");
		selectObjectLabelTextField = new TextField();
		selectOperationComboBox = (ComboBox<FmmlxOperation>) initializeComboBox(operationList);
		selectionForStrategiesComboBox = new ComboBox<>();

		grid.add(selectObjectLabel, 0, 0);
		grid.add(selectObjectLabelTextField, 1, 0);
		grid.add(selectOperation, 0, 1);
		grid.add(selectOperationComboBox, 1, 1);
		grid.add(selectionForStrategies, 0, 2);
		grid.add(selectionForStrategiesComboBox, 1, 2);

		selectionForStrategiesComboBox.setPrefWidth(COLUMN_WIDTH);
		selectOperationComboBox.setPrefWidth(COLUMN_WIDTH);

		operations = object.getOwnOperations();

		selectObjectLabelTextField.setText(object.getName());
		selectObjectLabelTextField.setDisable(true);
	}


	private void removeAttribute() {
		dialogPane.setHeaderText("Remove Attribute");

		attributes = object.getOwnAttributes();
		attributes.addAll(object.getOtherAttributes());

		ObservableList<FmmlxAttribute> attributeList;
		attributeList = FXCollections.observableList(attributes);


		selectObjectLabel = new Label("Selected Object");
		selectAttribute = new Label("Select Attribute");
		selectionForStrategies = new Label("Selection for Strategies");
		selectObjectLabelTextField = new javafx.scene.control.TextField();
		selectAttributeComboBox = (ComboBox<FmmlxAttribute>) initializeComboBox(attributeList);
		selectionForStrategiesComboBox = new ComboBox<>();

		grid.add(selectObjectLabel, 0, 0);
		grid.add(selectObjectLabelTextField, 1, 0);
		grid.add(selectAttribute, 0, 1);
		grid.add(selectAttributeComboBox, 1, 1);
		grid.add(selectionForStrategies, 0, 2);
		grid.add(selectionForStrategiesComboBox, 1, 2);

		selectionForStrategiesComboBox.setPrefWidth(COLUMN_WIDTH);
		selectAttributeComboBox.setPrefWidth(COLUMN_WIDTH);

		attributes = object.getOwnAttributes();
		attributes.addAll(object.getOtherAttributes());

		selectObjectLabelTextField.setText(object.getName());
		selectObjectLabelTextField.setDisable(true);
	}

	private void removeClass() {
		dialogPane.setHeaderText("Remove Class");

		selectObjectLabel = new Label("Selected Object");
		selectionForStrategies = new Label("Selection for Strategies");
		selectObjectLabelTextField = new javafx.scene.control.TextField();
		selectionForStrategiesComboBox = new ComboBox<>();

		grid.add(selectObjectLabel, 0, 0);
		grid.add(selectObjectLabelTextField, 1, 0);
		grid.add(selectionForStrategies, 0, 1);
		grid.add(selectionForStrategiesComboBox, 1, 1);

		selectionForStrategiesComboBox.setPrefWidth(COLUMN_WIDTH);

		selectObjectLabelTextField.setText(object.getName());
		selectObjectLabelTextField.setDisable(true);
	}

	private boolean validateUserInput() {

		switch (type) {
			case Class:
				return validateRemoveClass();
			case Attribute:
				return validateRemoveAttribute();
			case Operation:
				return validateRemoveOperation();
			case Association:
				return validateRemoveAssociation();
			default:
				System.err.println("RemoveDialog: No matching content type!");
				break;
		}
		return true;
	}

	private boolean validateRemoveAssociation() {
		if (selectAssociationComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAssociation);
			return false;
		}
		return true;
	}


	private boolean validateRemoveOperation() {
		if (selectOperationComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectOperation);
			return false;
		}
		return true;
	}


	private boolean validateRemoveAttribute() {
		if (selectAttributeComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAttribute);
			return false;
		}
		return true;
	}


	private boolean validateRemoveClass() {
		//not sure 
		return true;
	}
}
