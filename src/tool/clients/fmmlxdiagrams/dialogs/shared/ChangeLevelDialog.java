package tool.clients.fmmlxdiagrams.dialogs.shared;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.util.converter.IntegerStringConverter;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.LevelBox;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.Vector;


public class ChangeLevelDialog extends CustomDialog<ChangeLevelDialog.Result> {

	private final PropertyType type;
	private FmmlxObject object;
	private Vector<FmmlxAttribute> attributes;
	private Vector<FmmlxOperation> operations;

	private DialogPane dialogPane;

	private Label objectLabel;
	private Label objectLevelLabel;

	private TextField objectNameTextField;
//	private LevelBox objectLevelTextField;

	//For All
	private Label currentLevelLabel;
	private Label newLevelLabel;
	private LevelBox currentLevelTextField;
	private LevelBox newLevelComboBox;
	private Level currentLevel;
//	private Level newLevel;
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


	public ChangeLevelDialog(FmmlxObject object, PropertyType type) {

		super();
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
				return new Result(object, name, currentLevel, newLevelComboBox.getLevel(), type);
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
			errorLabel.setText(StringValue.ErrorMessage.selectAssociation);
			return false;
		}
		if(newLevelComboBox.getLevel() == null){
			errorLabel.setText(StringValue.ErrorMessage.levelUnparseable);
			return false;			
		}
		if(!newLevelComboBox.getLevel().isFixedLevelClass()) {
			errorLabel.setText(StringValue.ErrorMessage.levelMustNotBeContingent);
			return false;			
		}
		errorLabel.setText("");
		return true;
	}

	private boolean validateOperationLevelChange() {
		if (selectOperationComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValue.ErrorMessage.selectOperation);
			return false;
		}
		if(newLevelComboBox.getLevel() == null){
			errorLabel.setText(StringValue.ErrorMessage.levelUnparseable);
			return false;			
		}
		if(!newLevelComboBox.getLevel().isFixedLevelClass()) {
			errorLabel.setText(StringValue.ErrorMessage.levelMustNotBeContingent);
			return false;			
		}
		errorLabel.setText("");
		return true;
	}

	private boolean validateAttributeLevelChange() {
		if (selectAttributeComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValue.ErrorMessage.selectAttribute);
			return false;
		} 
		if(newLevelComboBox.getLevel() == null){
			errorLabel.setText(StringValue.ErrorMessage.levelUnparseable);
			return false;			
		}
		if(!newLevelComboBox.getLevel().isFixedLevelClass()) {
			errorLabel.setText(StringValue.ErrorMessage.levelMustNotBeContingent);
			return false;			
		}
		errorLabel.setText("");
		return true;
	}

	private boolean validateClassLevelChange() {
		if(newLevelComboBox.getLevel() == null){
			errorLabel.setText(StringValue.ErrorMessage.levelUnparseable);
			return false;			
		}
		errorLabel.setText("");
		return true;
	}

	private void layoutContent(PropertyType type) {
		objectLabel = new Label(StringValue.LabelAndHeaderTitle.selectedObject);
		objectNameTextField = new TextField();
		objectLevelLabel = new Label(StringValue.LabelAndHeaderTitle.currentLevel);
		currentLevelTextField = new LevelBox();
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

		dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeAssociationLevel);

		selectAssociationLabel = new Label(StringValue.LabelAndHeaderTitle.selectAssociation);
		currentLevelLabel = new Label(StringValue.LabelAndHeaderTitle.currentLevel);
		newLevelLabel = new Label(StringValue.LabelAndHeaderTitle.newLevel);

		selectAssociationComboBox = new ComboBox<String>();
		currentLevelTextField = new LevelBox(object.getLevel());
		currentLevelTextField.setDisable(true);
		newLevelComboBox = new LevelBox(object.getLevel());

		selectAssociationComboBox.setPrefWidth(COLUMN_WIDTH);
		newLevelComboBox.setPrefWidth(COLUMN_WIDTH);
//		newLevelComboBox.valueProperty().addListener(new ChangeListener<Integer>() {
//
//			@Override
//			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
//				newLevel = newValue;
//			}
//
//		});

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

		dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeOperationLevel);

		selectOperationLabel = new Label(StringValue.LabelAndHeaderTitle.selectOperation);
		currentLevelLabel = new Label(StringValue.LabelAndHeaderTitle.currentLevel);
		newLevelLabel = new Label(StringValue.LabelAndHeaderTitle.selectNewLevel);

		selectOperationComboBox = (ComboBox<FmmlxOperation>) initializeComboBox(operationList);
		selectOperationComboBox.valueProperty().addListener(new ChangeListener<FmmlxOperation>() {
			@Override
			public void changed(ObservableValue<? extends FmmlxOperation> observable, FmmlxOperation oldValue,
								FmmlxOperation newValue) {
				currentLevelTextField.setLevel(new Level(newValue.getLevel()));
				newLevelComboBox.setLevel(new Level(newValue.getLevel()));
				currentLevel = new Level(newValue.getLevel());
				name = selectOperationComboBox.getSelectionModel().getSelectedItem().getName();
			}
		});
		currentLevelTextField = new LevelBox();
		currentLevelTextField.setDisable(true);
		newLevelComboBox = new LevelBox();


		selectOperationComboBox.setPrefWidth(COLUMN_WIDTH);
		newLevelComboBox.setPrefWidth(COLUMN_WIDTH);
//		newLevelComboBox.valueProperty().addListener(new ChangeListener<Integer>() {
//
//			@Override
//			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
//				newLevel = newValue;
//			}
//
//		});

		grid.add(selectOperationLabel, 0, 1);
		grid.add(selectOperationComboBox, 1, 1);
		grid.add(currentLevelLabel, 0, 2);
		grid.add(currentLevelTextField, 1, 2);
		grid.add(newLevelLabel, 0, 3);
		grid.add(newLevelComboBox, 1, 3);
	}

	private void changeAttributeLevel() {
		dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeAttributeLevel);

		attributes = object.getOwnAttributes();
		attributes.addAll(object.getOtherAttributes());

		ObservableList<FmmlxAttribute> attributeList;
		attributeList = FXCollections.observableList(attributes);

		selectAttributeLabel = new Label(StringValue.LabelAndHeaderTitle.selectAttribute);
		currentLevelLabel = new Label(StringValue.LabelAndHeaderTitle.currentLevel);
		newLevelLabel = new Label(StringValue.LabelAndHeaderTitle.selectNewLevel);

		currentLevelTextField = new LevelBox();
		currentLevelTextField.setDisable(true);

		selectAttributeComboBox = (ComboBox<FmmlxAttribute>) initializeComboBox(attributeList);
		selectAttributeComboBox.valueProperty().addListener(new ChangeListener<FmmlxAttribute>() {

			@Override
			public void changed(ObservableValue<? extends FmmlxAttribute> observable, FmmlxAttribute oldValue,
								FmmlxAttribute newValue) {
				currentLevelTextField.setLevel(new Level(newValue.getLevel()));
				newLevelComboBox.setLevel(new Level(newValue.getLevel()));
				currentLevel = new Level(newValue.getLevel());
				name = selectAttributeComboBox.getSelectionModel().getSelectedItem().getName();
			}
		});

		newLevelComboBox = new LevelBox();
//		newLevelComboBox.valueProperty().addListener(new ChangeListener<Integer>() {
//
//			@Override
//			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
//				newLevel = newValue;
//			}
//
//		});

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
		dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeClassLevel);
		currentLevel = object.getLevel();
		currentLevelTextField.setLevel(currentLevel);
		currentLevelTextField.setDisable(true);

		newLevelLabel = new Label(StringValue.LabelAndHeaderTitle.selectNewLevel);

		newLevelComboBox = new LevelBox(currentLevel);
//		newLevelComboBox.setEditable(true);
//		newLevelComboBox.setConverter(new IntegerStringConverter());

		newLevelComboBox.setPrefWidth(COLUMN_WIDTH);
//		newLevelComboBox.valueProperty().addListener(new ChangeListener<Integer>() {
//
//			@Override
//			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
//				newLevel = newValue;
//			}
//
//		});

		grid.add(objectLevelLabel, 0, 1);
		grid.add(currentLevelTextField, 1, 1);
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
	
	public class Result {

		public final PropertyType type;
		public final  FmmlxObject object;
		public final  Level oldLevel;
		public final  Level newLevel;
		public final  String name;
		
		public Result(FmmlxObject object, String name, Level currentLevel, Level newLevel, PropertyType type) {
			this.type = type;
			this.object = object;
			this.oldLevel = currentLevel;
			this.newLevel= newLevel;
			this.name= name;
		}		

		public String getObjectPath() {
			return object.getPath();
		}
	}

}
