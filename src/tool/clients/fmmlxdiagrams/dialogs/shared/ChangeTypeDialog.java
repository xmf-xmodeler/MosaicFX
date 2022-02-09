package tool.clients.fmmlxdiagrams.dialogs.shared;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ChangeTypeDialog<Property
	extends FmmlxProperty>
	extends CustomDialog<ChangeTypeDialog<Property>.Result> {

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

	//For Each
	private Label selectPropertyLabel;
	private ComboBox<Property> selectPropertyComboBox;
	private Vector<Property> propertyItems;
	private Property selectedItem;
	

	public ChangeTypeDialog(FmmlxObject object, PropertyType type, Vector<Property> propertyItems, Property selectedItem) {
		this.object = object;
		this.type = type;
		this.selectedItem = selectedItem;
		this.propertyItems = propertyItems;
		
		System.err.println("Object: " + object + " Type: " + type + " Selected Item: " + selectedItem + " PropertyItems: " + propertyItems);

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
				return new Result(type, 
								  object, 
								  selectPropertyComboBox.getSelectionModel().getSelectedItem(), 
								  currentTypeTextField.getText(), 
								  newTypeComboBox.getSelectionModel().getSelectedItem());
				
//				switch (type) {
//
//					case Attribute:
//						return setResultChangeTypeAttribute(type);
//					case Operation:
//						return setResultChangeTypeOperation(type);
//					case Association:
//						return setResultChangeTypeAssociation(type);
//					default:
//						System.err.println("AddDialogResult: No matching content type!");
//				}
			}
			return null;
		});
	}

//	private Result setResultChangeTypeAssociation(PropertyType type) {
//		return new ChangeTypeDialogResult(
//				type, 
//				object, 
//				selectAssociationComboBox.getSelectionModel().getSelectedItem(),
//				currentTypeTextField.getText(), 
//				newTypeComboBox.getSelectionModel().getSelectedItem());
//	}
//
//
//	private Result setResultChangeTypeOperation(PropertyType type) {
//		return new ChangeTypeDialogResult(
//				type, 
//				object, 
//				selectOperationComboBox.getSelectionModel().getSelectedItem(),
//				currentTypeTextField.getText(), 
//				newTypeComboBox.getSelectionModel().getSelectedItem());
//	}
//
//	private Result setResultChangeTypeAttribute(PropertyType type) {
//		return new ChangeTypeDialogResult(
//				type, 
//				object, 
//				selectAttributeComboBox.getSelectionModel().getSelectedItem(),
//				currentTypeTextField.getText(), 
//				newTypeComboBox.getSelectionModel().getSelectedItem());
//	}

	private boolean validateUserInput() {
//		switch (type) {
//			case Attribute:
//				return validateChangeTypeAttribute();
//			case Operation:
//				return validateChangeTypeOperation();
//			case Association:
//				return validateChangeTypeAssociation();
//			default:
//				System.err.println("ChangeTypeDialog: No matching content type!");
//		}
//		return false;
//	}
//
//	private boolean validateChangeTypeAssociation() {
		if (selectPropertyComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValue.ErrorMessage.selectDifferentType);
			return false;
		} else if (newTypeComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValue.ErrorMessage.selectNewType);
			return false;
		} else if (newTypeComboBox.getSelectionModel().getSelectedItem().equals(currentTypeTextField.getText())) {
			errorLabel.setText(StringValue.ErrorMessage.selectAnotherType);
		}
		return true;
	}
//	
//	private boolean validateChangeTypeAssociation() {
//		if (selectAssociationComboBox.getSelectionModel().getSelectedItem() == null) {
//			errorLabel.setText(StringValue.ErrorMessage.selectAssociation);
//			return false;
//		} else if (newTypeComboBox.getSelectionModel().getSelectedItem() == null) {
//			errorLabel.setText(StringValue.ErrorMessage.selectNewType);
//			return false;
//		} else if (newTypeComboBox.getSelectionModel().getSelectedItem().equals(currentTypeTextField.getText())) {
//			errorLabel.setText(StringValue.ErrorMessage.selectAnotherType);
//		}
//		return true;
//	}
//
//	private boolean validateChangeTypeOperation() {
//		if (selectOperationComboBox.getSelectionModel().getSelectedItem() == null) {
//			errorLabel.setText(StringValue.ErrorMessage.selectOperation);
//			return false;
//		} else if (newTypeComboBox.getSelectionModel().getSelectedItem() == null) {
//			errorLabel.setText(StringValue.ErrorMessage.selectNewType);
//			return false;
//		} else if (newTypeComboBox.getSelectionModel().getSelectedItem().equals(currentTypeTextField.getText())) {
//			errorLabel.setText(StringValue.ErrorMessage.selectAnotherType);
//		}
//		return true;
//	}
//
//	private boolean validateChangeTypeAttribute() {
//		if (selectAttributeComboBox.getSelectionModel().getSelectedItem() == null) {
//			errorLabel.setText(StringValue.ErrorMessage.selectAttribute);
//			return false;
//		} else if (newTypeComboBox.getSelectionModel().getSelectedItem() == null) {
//			errorLabel.setText(StringValue.ErrorMessage.selectNewType);
//			return false;
//		} else if (newTypeComboBox.getSelectionModel().getSelectedItem().equals(currentTypeTextField.getText())) {
//			errorLabel.setText(StringValue.ErrorMessage.selectAnotherType);
//			return false;
//		}
//		return true;
//	}

	private void layoutContent(PropertyType type) {
		classLabel = new Label("Class");
		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		
		selectPropertyLabel = new Label("Select " + type.name());
		selectPropertyComboBox = new ComboBox<Property>();
		selectPropertyComboBox.getItems().addAll(propertyItems);
		
		currentTypeLabel = new Label("Current Type");
		currentTypeTextField = new TextField();
		currentTypeTextField.setDisable(true);

		String[] types = new String[]{"Integer", "String", "Boolean", "Float"};
		typesArray = Arrays.asList(types);
		ObservableList<String> newTypeList = FXCollections.observableArrayList(typesArray);

		newTypeLabel = new Label("Select New Type");
		newTypeComboBox = new ComboBox<String>(newTypeList);
		newTypeComboBox.setEditable(true);

		newTypeComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(classLabel, 0, 0);
		grid.add(classTextField, 1, 0);
		grid.add(selectPropertyLabel, 0, 1);
		grid.add(selectPropertyComboBox, 1, 1);
		grid.add(currentTypeLabel, 0, 2);
		grid.add(currentTypeTextField, 1, 2);
		grid.add(newTypeLabel, 0, 3);
		grid.add(newTypeComboBox, 1, 3);
		switch (type) {
			case Attribute:
				dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeAttributeType);
				break;
//			case Association:
//				dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeAssociationType);
//				break;
			case Operation:
				dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeOperationType);
			default:
				System.err.println("ChangeTypeDialog: No matching content type!");
				break;
		}
			
		//selectPropertyComboBox = (ComboBox<Property>) initializeComboBox(attributeList);
		selectPropertyComboBox.valueProperty().addListener(new ChangeListener<Property>() {

			@Override
			public void changed(ObservableValue<? extends Property> observable, Property oldValue,
					Property newValue) {
				if(newValue instanceof FmmlxAttribute) {
					currentTypeTextField.setText(((FmmlxAttribute)newValue).getType());
				} else if(newValue instanceof FmmlxOperation) {
					currentTypeTextField.setText(((FmmlxAttribute)newValue).getType());
				} 
			}
		});
		
		if (selectedItem!=null) {
			selectPropertyComboBox.getSelectionModel().select(selectedItem);
		}
	}

	public void setSelected(Property selectedProperty) {
//		if (selectedProperty.getPropertyType() == PropertyType.Attribute) {
			selectPropertyComboBox.getSelectionModel().select(selectedProperty);
//		} else if (selectedProperty.getPropertyType() == PropertyType.Operation) {
//			selectPropertyComboBox.getSelectionModel().select((FmmlxOperation) selectedProperty);
//		}
	}
	
	public class Result {
		public final PropertyType type;
		public final FmmlxObject object;
		public final Property property;
		public final String oldType;
		public final String newType;
		
		public Result(PropertyType type, FmmlxObject object, Property property, String oldType, String newType) {
			this.type = type;
			this.object = object;
			this.property = property;
			this.oldType = oldType;
			this.newType = newType;
		}
	}
}
