package tool.clients.fmmlxdiagrams.dialogs;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.util.converter.IntegerStringConverter;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeLevelDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

import java.util.Vector;


public class ChangeLevelDialog extends CustomDialog<ChangeLevelDialogResult> {

	private final PropertyType type;
	//private final FmmlxDiagram diagram;
	private FmmlxObject object;
	private Vector<FmmlxAttribute> attributes;
	private Vector<FmmlxOperation> operations;

	private DialogPane dialogPane;

	private Label objectLabel;
	private Label objectLevelLabel;

	private TextField objectNameTextField;
	private TextField objectLevelTextField;

	//For All
	private Label currentLevelLabel;
	private Label newLevelLabel;
	private TextField currentLevelTextField;
	private ComboBox<Integer> newLevelComboBox;
	private int currentLevel;
	private int newLevel;
	private String name;

	//For Attribute
	private Label selectAttributeLabel;
	private ComboBox<FmmlxAttribute> selectAttributeComboBox;

	//For Association
	private Label selectAssociationLabel;
	private ComboBox<String> selectAssociationComboBox;

	//For Operation
	private Label selectOperationLabel;
	private ComboBox<FmmlxOperation> selectOperationComboBox;


	public ChangeLevelDialog(FmmlxDiagram diagram, FmmlxObject object, PropertyType type) {

		super();
		//this.diagram = diagram;
		this.type = type;
		this.object = object;

		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent(type);
		dialogPane.setContent(flow);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput(type)) {
				e.consume();
			}
		});

		setResult();
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				return new ChangeLevelDialogResult(object, name, currentLevel, newLevel, type);
			}
			return null;
		});
	}

	private boolean validateUserInput(PropertyType type) {
		switch (type) {
			case Class:
				return validateClassLevelChange();
			case Attribute:
				return validateAttributeLevelChange();
			case Operation:
				return validateOperationLevelChange();
			case Association:
				return validateAssociationLevelChange();
			default:
				System.err.println("ChangeLevelDialog : No matching content type!");
				break;
		}
		return true;
	}

	private boolean validateAssociationLevelChange() {
		if (selectAssociationComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAssociation);
			return false;
		} else if (newLevelComboBox.getSelectionModel().getSelectedIndex() == -1) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectNewLevel);
			return false;
		} else if (newLevelComboBox.getSelectionModel().getSelectedItem().toString().equals(currentLevelTextField.getText())) {
			errorLabel.setText(StringValueDialog.ErrorMessage.pleaseSelectAnotherLevel);
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	private boolean validateOperationLevelChange() {
		if (selectOperationComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectOperation);
			return false;
		} else if (newLevelComboBox.getSelectionModel().getSelectedIndex() == -1) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectNewLevel);
			return false;
		} else if (newLevelComboBox.getSelectionModel().getSelectedItem().toString().equals(currentLevelTextField.getText())) {
			errorLabel.setText(StringValueDialog.ErrorMessage.pleaseSelectAnotherLevel);
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	private boolean validateAttributeLevelChange() {
		if (selectAttributeComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAttribute);
			return false;
		} else if (newLevelComboBox.getSelectionModel().getSelectedIndex() == -1) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectNewLevel);
			return false;
		} else if (newLevelComboBox.getSelectionModel().getSelectedItem().toString().equals(currentLevelTextField.getText())) {
			errorLabel.setText(StringValueDialog.ErrorMessage.pleaseSelectAnotherLevel);
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	private boolean validateClassLevelChange() {
		if (newLevelComboBox.getSelectionModel().getSelectedIndex() == -1) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectNewLevel);
			return false;
		} else if (newLevelComboBox.getSelectionModel().getSelectedItem().toString().equals(objectLevelTextField.getText())) {
			errorLabel.setText(StringValueDialog.ErrorMessage.pleaseSelectAnotherLevel);
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	private void layoutContent(PropertyType type) {
		objectLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectedObject);
		objectNameTextField = new TextField();
		objectLevelLabel = new Label(StringValueDialog.LabelAndHeaderTitle.currentLevel);
		objectLevelTextField = new TextField();
		objectNameTextField.setText(object.getName());
		objectNameTextField.setDisable(true);

		grid.add(objectLabel, 0, 0);
		grid.add(objectNameTextField, 1, 0);


		switch (type) {
			case Class:
				changeClassLevel();
				break;
			case Attribute:
				changeAttributeLevel();
				break;
			case Operation:
				changeOperationLevel();
				break;
			case Association:
				changeAssociationLevel();
			default:
				System.err.println("ChangeNameDialog: No matching content type!");
		}

	}

	private void changeAssociationLevel() {
		//TODO Put list of Association to combobox

		dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.changeAssociationLevel);

		selectAssociationLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectAssociation);
		currentLevelLabel = new Label(StringValueDialog.LabelAndHeaderTitle.currentLevel);
		newLevelLabel = new Label(StringValueDialog.LabelAndHeaderTitle.newLevel);

		selectAssociationComboBox = new ComboBox<String>();
		currentLevelTextField = new TextField();
		currentLevelTextField.setDisable(true);
		newLevelComboBox = new ComboBox<Integer>(LevelList.getLevelInterval(object));

		selectAssociationComboBox.setPrefWidth(COLUMN_WIDTH);
		newLevelComboBox.setPrefWidth(COLUMN_WIDTH);
		newLevelComboBox.valueProperty().addListener(new ChangeListener<Integer>() {

			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				newLevel = newValue;
			}

		});

		grid.add(selectAssociationLabel, 0, 1);
		grid.add(selectAssociationComboBox, 1, 1);
		grid.add(currentLevelLabel, 0, 2);
		grid.add(currentLevelTextField, 1, 2);
		grid.add(newLevelLabel, 0, 3);
		grid.add(newLevelComboBox, 1, 3);

	}

	private void changeOperationLevel() {

		operations = object.getOwnOperations();
		operations.addAll(object.getOtherOperations());

		ObservableList<FmmlxOperation> operationList;
		operationList = FXCollections.observableList(operations);

		dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.changeOperationLevel);

		selectOperationLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectOperation);
		currentLevelLabel = new Label(StringValueDialog.LabelAndHeaderTitle.currentLevel);
		newLevelLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectNewLevel);

		selectOperationComboBox = (ComboBox<FmmlxOperation>) initializeComboBox(operationList);
		selectOperationComboBox.valueProperty().addListener(new ChangeListener<FmmlxOperation>() {
			@Override
			public void changed(ObservableValue<? extends FmmlxOperation> observable, FmmlxOperation oldValue,
								FmmlxOperation newValue) {
				currentLevelTextField.setText(newValue.getLevel() + "");
				currentLevel = newValue.getLevel();
				name = selectOperationComboBox.getSelectionModel().getSelectedItem().getName();
			}
		});
		currentLevelTextField = new TextField();
		currentLevelTextField.setDisable(true);
		newLevelComboBox = new ComboBox<Integer>(LevelList.getLevelInterval(object));


		selectOperationComboBox.setPrefWidth(COLUMN_WIDTH);
		newLevelComboBox.setPrefWidth(COLUMN_WIDTH);
		newLevelComboBox.valueProperty().addListener(new ChangeListener<Integer>() {

			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				newLevel = newValue;
			}

		});

		grid.add(selectOperationLabel, 0, 1);
		grid.add(selectOperationComboBox, 1, 1);
		grid.add(currentLevelLabel, 0, 2);
		grid.add(currentLevelTextField, 1, 2);
		grid.add(newLevelLabel, 0, 3);
		grid.add(newLevelComboBox, 1, 3);
	}

	private void changeAttributeLevel() {
		dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.changeAttributeLevel);

		attributes = object.getOwnAttributes();
		attributes.addAll(object.getOtherAttributes());

		ObservableList<FmmlxAttribute> attributeList;
		attributeList = FXCollections.observableList(attributes);

		selectAttributeLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectAttribute);
		currentLevelLabel = new Label(StringValueDialog.LabelAndHeaderTitle.currentLevel);
		newLevelLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectNewLevel);

		currentLevelTextField = new TextField();
		currentLevelTextField.setDisable(true);

		selectAttributeComboBox = (ComboBox<FmmlxAttribute>) initializeComboBox(attributeList);
		selectAttributeComboBox.valueProperty().addListener(new ChangeListener<FmmlxAttribute>() {

			@Override
			public void changed(ObservableValue<? extends FmmlxAttribute> observable, FmmlxAttribute oldValue,
								FmmlxAttribute newValue) {
				currentLevelTextField.setText(newValue.getLevel() + "");
				currentLevel = newValue.getLevel();
				name = selectAttributeComboBox.getSelectionModel().getSelectedItem().getName();
			}
		});

		newLevelComboBox = new ComboBox<Integer>(LevelList.getLevelInterval(object));
		newLevelComboBox.valueProperty().addListener(new ChangeListener<Integer>() {

			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				newLevel = newValue;
			}

		});

		selectAttributeComboBox.setPrefWidth(COLUMN_WIDTH);
		newLevelComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(selectAttributeLabel, 0, 1);
		grid.add(selectAttributeComboBox, 1, 1);
		grid.add(currentLevelLabel, 0, 2);
		grid.add(currentLevelTextField, 1, 2);
		grid.add(newLevelLabel, 0, 3);
		grid.add(newLevelComboBox, 1, 3);
	}

	private void changeClassLevel() {
		dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.changeClassLevel);
		objectLevelTextField.setText(object.getLevel() + "");
		objectLevelTextField.setDisable(true);

		newLevelLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectNewLevel);
		currentLevel = object.getLevel();

		newLevelComboBox = new ComboBox<Integer>(LevelList.levelList);
		newLevelComboBox.setEditable(true);
		newLevelComboBox.setConverter(new IntegerStringConverter());

		newLevelComboBox.setPrefWidth(COLUMN_WIDTH);
		newLevelComboBox.valueProperty().addListener(new ChangeListener<Integer>() {

			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				newLevel = newValue;
			}

		});

		grid.add(objectLevelLabel, 0, 1);
		grid.add(objectLevelTextField, 1, 1);
		grid.add(newLevelLabel, 0, 2);
		grid.add(newLevelComboBox, 1, 2);
	}

	public void setSelected(FmmlxProperty selectedProperty) {
		if (selectedProperty.getPropertyType() == PropertyType.Attribute) {
			selectAttributeComboBox.getSelectionModel().select((FmmlxAttribute) selectedProperty);
		} else if (selectedProperty.getPropertyType() == PropertyType.Operation) {
			selectOperationComboBox.getSelectionModel().select((FmmlxOperation) selectedProperty);
		}
	}
}
