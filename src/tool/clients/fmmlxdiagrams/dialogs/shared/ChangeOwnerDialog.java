package tool.clients.fmmlxdiagrams.dialogs.shared;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.Vector;

public class ChangeOwnerDialog<Property extends FmmlxProperty> extends CustomDialog<ChangeOwnerDialog<Property>.Result> {

	private DialogPane dialogPane;
	private final PropertyType type;
	private FmmlxObject object;
	private Vector<FmmlxObject> objects;
	
	//For Each
	private Label selectProperty;
	private ComboBox<Property> selectPropertyComboBox;

	//For All
	private Label classLabel;
	private Label newOwnerLabel;

	private TextField classNameTextfield;
	private ComboBox<FmmlxObject> newOwnerComboBox;

	private Vector<Property> propertyItems;

	public ChangeOwnerDialog(AbstractPackageViewer diagram, FmmlxObject object, PropertyType type) {
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
						return new Result(type, object,
								selectPropertyComboBox.getSelectionModel().getSelectedItem(), 
								newOwnerComboBox.getSelectionModel().getSelectedItem());
					case Operation:
						return new Result(type, object,
								selectPropertyComboBox.getSelectionModel().getSelectedItem(), 
								newOwnerComboBox.getSelectionModel().getSelectedItem());
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
		if (selectPropertyComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValue.ErrorMessage.selectOperation);
			return false;
		} else if (newOwnerComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValue.ErrorMessage.selectNewOwner);
			return false;
		} else if (newOwnerComboBox.getSelectionModel().getSelectedItem() == object) {
			errorLabel.setText(StringValue.ErrorMessage.selectAnotherClass);
			return false;
		}
		return true;
	}


	private boolean validateChangeOwnerAttribute() {
		if (selectPropertyComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValue.ErrorMessage.selectAttribute);
			return false;
		} else if (newOwnerComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValue.ErrorMessage.selectNewOwner);
			return false;
		} else if (newOwnerComboBox.getSelectionModel().getSelectedItem() == object) {
			errorLabel.setText(StringValue.ErrorMessage.selectAnotherClass);
			return false;
		}
		return true;
	}


	private void layoutContent() {
		classLabel = new Label(StringValue.LabelAndHeaderTitle.selectedObject);

		newOwnerLabel = new Label(StringValue.LabelAndHeaderTitle.newOwner);

		classNameTextfield = new TextField();
		classNameTextfield.setText(object.getName());
		classNameTextfield.setDisable(true);


//		attributes = object.getOwnAttributes();
//		attributes.addAll(object.getOtherAttributes());


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
				dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeAttributeOwner);
				propertyItems = (Vector<Property>) (object.getOwnAttributes());
				propertyItems.addAll((Vector<Property>)(object.getOtherAttributes()));

				ObservableList<Property> attributeList;
				attributeList = FXCollections.observableList(propertyItems);

				selectProperty = new Label(StringValue.LabelAndHeaderTitle.selectAttribute);
				selectPropertyComboBox = (ComboBox<Property>) initializeComboBox(attributeList);
				selectPropertyComboBox.setPrefWidth(COLUMN_WIDTH);
				grid.add(selectProperty, 0, 1);
				grid.add(selectPropertyComboBox, 1, 1);
				break;
			case Operation:
				dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeOperationOwner);
				propertyItems = (Vector<Property>) (object.getOwnOperations());
				propertyItems.addAll((Vector<Property>)(object.getOtherOperations()));

				ObservableList<Property> operationList;
				operationList = FXCollections.observableList(propertyItems);
				selectProperty = new Label(StringValue.LabelAndHeaderTitle.selectOperation);
				selectPropertyComboBox = (ComboBox<Property>) initializeComboBox(operationList);
				selectPropertyComboBox.setPrefWidth(COLUMN_WIDTH);
				grid.add(selectProperty, 0, 1);
				grid.add(selectPropertyComboBox, 1, 1);
				break;
			default:
				System.err.println("AddDialog: No matching content type!");
		}

	}

	public void setSelected(FmmlxProperty selectedProperty) {
		selectPropertyComboBox.getSelectionModel().select((Property) selectedProperty);
//		if (type == PropertyType.Attribute) {
//			selectAttributeComboBox.getSelectionModel().select((FmmlxAttribute) selectedProperty);
//		} else if (type == PropertyType.Operation) {
//			selectOperationComboBox.getSelectionModel().select((FmmlxOperation) selectedProperty);
//		}
	}
	
	public class Result {
		
		public final PropertyType type;
		public final FmmlxObject object;
		public final Property property;
		public final FmmlxObject newOwner;

		public Result(PropertyType type, FmmlxObject object, Property property, FmmlxObject newOwner) {
			this.type = type;
			this.object = object;
			this.property = property;
			this.newOwner = newOwner;
		}

		public String getNewOwnerName() {
			return newOwner.getName();
		}
	}
}
