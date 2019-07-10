package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeTypeDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;


public class ChangeTypeDialog extends CustomDialog<ChangeTypeDialogResult>{
	private FmmlxObject object;
	private final PropertyType type;
	private DialogPane dialogPane;
	private List<String> typesArray;
	
	//For all
	private Label classLabel;
	private Label currentTypeLabel;
	private Label newTypeLabel;
	
	private TextField classTextField;
	private TextField currentTypeTextField;
	private ComboBox<String> newTypeComboBox;
	
	//For Attribute
	private Label selectAttributeLabel;
	private ComboBox<FmmlxAttribute> selectAttributeComboBox;
	
	//For Operation
	private Label selectOperationLabel;
	private ComboBox<FmmlxOperation> selectOperationComboBox;
	
	//For Association
	private Label selectAssociationLabel;
	private ComboBox<FmmlxAssociation> selectAssociationComboBox;
	
	
	private Vector<FmmlxAttribute> attributes;
	private Vector<FmmlxOperation> operations;
	private Vector<FmmlxAssociation> associations;

	public ChangeTypeDialog(FmmlxObject object, PropertyType type) {
		super();
		this.object = object;
		this.type = type;
		
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent(type);
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
					setResultChangeTypeAttribute(type);
					break;
				case Operation:
					setResultChangeTypeOperation(type);
					break;
				case Association:
					setResultChangeTypeAssociation(type);
					break;
				default:
					System.err.println("AddDialogResult: No matching content type!");	
				}
			}
			return null;
		});
	}

	private ChangeTypeDialogResult setResultChangeTypeAssociation(PropertyType type) {
		return new ChangeTypeDialogResult(type, object, selectAssociationComboBox.getSelectionModel().getSelectedItem(), 
				currentTypeTextField.getText(), newTypeComboBox.getSelectionModel().getSelectedItem());
	}

	
	private ChangeTypeDialogResult setResultChangeTypeOperation(PropertyType type) {
		return new ChangeTypeDialogResult(type, object, selectOperationComboBox.getSelectionModel().getSelectedItem(), 
				currentTypeTextField.getText(), newTypeComboBox.getSelectionModel().getSelectedItem());
	}

	private ChangeTypeDialogResult setResultChangeTypeAttribute(PropertyType type) {
		return new ChangeTypeDialogResult(type, object, selectAttributeComboBox.getSelectionModel().getSelectedItem(), 
				currentTypeTextField.getText(), newTypeComboBox.getSelectionModel().getSelectedItem());
	}

	private boolean validateUserInput() {
		switch (type) {
		case Attribute:
			return validateChangeTypeAttribute();
		case Operation:
			return validateChangeTypeOperation();
		case Association:
			return validateChangeTypeAssociation();
		default:
			System.err.println("ChangeTypeDialog: No matching content type!");	
		}
	return false;
	}

	private boolean validateChangeTypeAssociation() {
		if(selectAssociationComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAssociation);
			return false;
		} else if (newTypeComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectNewType);
			return false;
		} else if (newTypeComboBox.getSelectionModel().getSelectedItem().equals(currentTypeTextField.getText())) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAnotherType);	
		}
		return true;
	}

	private boolean validateChangeTypeOperation() {
		if(selectOperationComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectOperation);
			return false;
		} else if (newTypeComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectNewType);
			return false;
		} else if (newTypeComboBox.getSelectionModel().getSelectedItem().equals(currentTypeTextField.getText())) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAnotherType);	
		}
		return true;
	}

	private boolean validateChangeTypeAttribute() {
		if(selectAttributeComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAttribute);
			return false;
		} else if (newTypeComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectNewType);
			return false;
		} else if (newTypeComboBox.getSelectionModel().getSelectedItem().equals(currentTypeTextField.getText())) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAnotherType);	
			return false;
		}
		return true;
	}

	private void layoutContent(PropertyType type) {
		classLabel = new Label("Class");
		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		
		currentTypeLabel = new Label("Current Type");
		currentTypeTextField = new TextField();
		currentTypeTextField.setDisable(true);
		
		String[] types = new String[]{"Integer", "String", "Boolean", "Float"};
		typesArray = Arrays.asList(types);
		ObservableList<String> newTypeList = FXCollections.observableArrayList(typesArray);

		newTypeLabel = new Label("Select New Type!");
		newTypeComboBox = new ComboBox<String>(newTypeList);
		newTypeComboBox.setEditable(true);
		
		newTypeComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(classLabel, 0, 0);
		grid.add(classTextField, 1, 0);
		grid.add(currentTypeLabel, 0, 2);
		grid.add(currentTypeTextField, 1, 2);
		grid.add(newTypeLabel, 0, 3);
		grid.add(newTypeComboBox, 1, 3);
		switch (type) {
		case Attribute:
			dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.changeAttributeType);
			layoutContentChangeTypeAttribute();
			break;
		case Association:
			dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.changeAssociationType);
			layoutContentChangeTypeAssoiation();
			break;
		case Operation:
			dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.changeOperationType);
			layoutContentChangeTypeOperation();
		default:
			System.err.println("ChangeTypeDialog: No matching content type!");	
			break;
		}
		
	}

	private void layoutContentChangeTypeAttribute() {
		attributes = object.getOwnAttributes();
		attributes.addAll(object.getOtherAttributes());
		
		ObservableList<FmmlxAttribute> attributeList;
		attributeList =  FXCollections.observableList(attributes);
		
		selectAttributeLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectAttribute);
		selectAttributeComboBox = initializeAttributeComboBox(attributeList);
		selectAttributeComboBox.valueProperty().addListener(new ChangeListener<FmmlxAttribute>() {

			@Override
			public void changed(ObservableValue<? extends FmmlxAttribute> observable, FmmlxAttribute oldValue,
					FmmlxAttribute newValue) {
				currentTypeTextField.setText(newValue.getType());
				
			}
		});
		
		selectAttributeComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(selectAttributeLabel, 0, 1);
		grid.add(selectAttributeComboBox, 1, 1);
	}

	private void layoutContentChangeTypeAssoiation() {
		//TODO insert Associatio list to combobox
		
		selectAssociationLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectAssociation);
		selectAssociationComboBox = new ComboBox<FmmlxAssociation>();
		selectAssociationComboBox.valueProperty().addListener(new ChangeListener<FmmlxAssociation>() {

			@Override
			public void changed(ObservableValue<? extends FmmlxAssociation> observable, FmmlxAssociation oldValue,
					FmmlxAssociation newValue) {
				// TODO Auto-generated method stub
				
			}
		});
		
		selectAssociationComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(selectAssociationLabel, 0, 1);
		grid.add(selectAssociationComboBox, 1, 1);
		
	}

	private void layoutContentChangeTypeOperation() {
		operations = object.getOwnOperations();
		operations.addAll(object.getOtherOperations());
		
		ObservableList<FmmlxOperation> operationList;
		operationList =  FXCollections.observableList(operations);
		
		selectOperationLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectOperation);
		selectOperationComboBox = initializeOperationComboBox(operationList);
		selectOperationComboBox.valueProperty().addListener(new ChangeListener<FmmlxOperation>() {

			@Override
			public void changed(ObservableValue<? extends FmmlxOperation> observable, FmmlxOperation oldValue,
					FmmlxOperation newValue) {
				currentTypeTextField.setText(newValue.getType());
				
			}

		});
		
		selectOperationComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(selectOperationLabel, 0, 1);
		grid.add(selectOperationComboBox, 1, 1);
		
	}

}
