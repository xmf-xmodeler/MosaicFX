package tool.clients.fmmlxdiagrams.dialogs.shared;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.util.StringConverter;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxEnum;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.fmmlxdiagrams.dialogs.AddAttributeDialogDataType;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.DomainspecificDatatypesDialog;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.dialogs.AddAttributeDialogDataType.AddAttributeDialogMetaDataType;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.Optional;
import java.util.Vector;

public class ChangeTypeDialog<Property
	extends FmmlxProperty>
	extends CustomDialog<ChangeTypeDialog<Property>.Result> {

	private AbstractPackageViewer diagram;
	private FmmlxObject object;
	private final PropertyType type;
	private DialogPane dialogPane;

	// For all
	private Label classLabel;
	private Label currentTypeLabel;
	private Label newTypeLabel;

	private TextField classTextField;
	private TextField currentTypeTextField;
	private ComboBox<AddAttributeDialogDataType> typeComboBox;
	private CheckBox showNonPrimitive;

	// available types
	private Vector<AddAttributeDialogDataType> types;
	private Vector<AddAttributeDialogDataType> primitiveTypes;
	private Vector<FmmlxObject> diagramObjects;
	private Vector<FmmlxEnum> diagramEnums;

	// For Each
	private Label selectPropertyLabel;
	private ComboBox<Property> selectPropertyComboBox;
	private Vector<Property> propertyItems;
	private Property selectedItem;

	public ChangeTypeDialog(FmmlxObject object, PropertyType type, Vector<Property> propertyItems,
			Property selectedItem, AbstractPackageViewer diagram) {
		this.object = object;
		this.type = type;
		this.selectedItem = selectedItem;
		this.propertyItems = propertyItems;
		this.diagram = diagram;

		// fill types
		primitiveTypes = new Vector<AddAttributeDialogDataType>();
		// by this we can differentiate between a type/ name (relevant for XMF) and a
		// display name
		primitiveTypes.add(new AddAttributeDialogDataType("Boolean", AddAttributeDialogMetaDataType.Primitive));
		primitiveTypes.add(new AddAttributeDialogDataType("Integer", AddAttributeDialogMetaDataType.Primitive));
		primitiveTypes.add(new AddAttributeDialogDataType("Float", AddAttributeDialogMetaDataType.Primitive));
		primitiveTypes.add(new AddAttributeDialogDataType("String", AddAttributeDialogMetaDataType.Primitive));
		primitiveTypes.add(new AddAttributeDialogDataType("Date", AddAttributeDialogMetaDataType.Primitive));
		primitiveTypes.add(new AddAttributeDialogDataType("Monetary Value", AddAttributeDialogMetaDataType.Primitive));

		types = new Vector<AddAttributeDialogDataType>(primitiveTypes);

		types.add(new AddAttributeDialogDataType("Currency", AddAttributeDialogMetaDataType.NonPrimitive));
		types.add(new AddAttributeDialogDataType("Complex", AddAttributeDialogMetaDataType.NonPrimitive));
		types.add(new AddAttributeDialogDataType("AuxiliaryClass", AddAttributeDialogMetaDataType.NonPrimitive));

		// add enums to type list
		diagramEnums = diagram.getEnums();
		for (FmmlxEnum e : diagramEnums) {
			types.add(new AddAttributeDialogDataType(e.getName(), AddAttributeDialogMetaDataType.Enum));

		}

		types.add(new AddAttributeDialogDataType("Domainspecific", AddAttributeDialogMetaDataType.Domainspecific));
		diagramObjects = diagram.getObjectsReadOnly();

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

				String datatype = typeComboBox.getConverter().fromString(typeComboBox.getEditor().getText()).getName();

				return new Result(type, object, selectPropertyComboBox.getSelectionModel().getSelectedItem(),
						currentTypeTextField.getText(), datatype);
			}
			return null;
		});
	}

	private boolean validateUserInput() {
		Label errorLabel = getErrorLabel();
		// used for checking whether the type is correct
		Vector<String> classNames = new Vector<>();
		Vector<String> validTypes = new Vector<>();
		Vector<String> enumNames = new Vector<>();

		String datatype = typeComboBox.getConverter().fromString(typeComboBox.getEditor().getText()).getName();

		// FH check for valid type
		if (!(showNonPrimitive.isSelected())) {
			// if primitive is selected, only those types are valid
			for (AddAttributeDialogDataType type : primitiveTypes) {
				validTypes.add(type.getName());
			}
		} else {
			// if non primitive types are allowed, classnames and all types are valid
			// classnames can be used as types for attributes
			for (FmmlxObject o : diagramObjects) {
				if (o.getLevel().getMinLevel() > 0) {
					classNames.add(o.toString());
				}
			}
			for (FmmlxEnum e : diagramEnums) {
				enumNames.add(e.getName());
			}

			validTypes.addAll(enumNames);
			validTypes.addAll(classNames);

			for (AddAttributeDialogDataType type : types) {
				validTypes.add(type.getName());
			}

		}
		// check whether entered type is valid
		if (!validTypes.contains(datatype)) {
			errorLabel.setText(StringValue.ErrorMessage.selectCorrectType);
			return false;
		}

		if (datatype == null || datatype.length() < 1) {
			errorLabel.setText(StringValue.ErrorMessage.selectType);
			return false;
		}
		errorLabel.setText("");
		return true;

//		if (selectPropertyComboBox.getSelectionModel().getSelectedItem() == null) {
//			errorLabel.setText(StringValue.ErrorMessage.selectDifferentType);
//			return false;
//		} else if (typeComboBox.getSelectionModel().getSelectedItem() == null) {
//			errorLabel.setText(StringValue.ErrorMessage.selectNewType);
//			return false;
//		} else if (typeComboBox.getSelectionModel().getSelectedItem().equals(currentTypeTextField.getText())) {
//			errorLabel.setText(StringValue.ErrorMessage.selectAnotherType);
//		}
//		return true;
	}

	private void layoutContent(PropertyType type) {
		classLabel = new Label("Class");
		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);

		showNonPrimitive = new CheckBox("Show non primitive data types");

		selectPropertyLabel = new Label("Select " + type.name());
		selectPropertyComboBox = new ComboBox<Property>();
		selectPropertyComboBox.getItems().addAll(propertyItems);

		currentTypeLabel = new Label("Current Type");
		currentTypeTextField = new TextField();
		currentTypeTextField.setDisable(true);

		ObservableList<AddAttributeDialogDataType> primitiveTypeList = FXCollections
				.observableArrayList(primitiveTypes);
		// add all types
		ObservableList<AddAttributeDialogDataType> typeList = FXCollections.observableArrayList(types);

		newTypeLabel = new Label("Select New Type");
		typeComboBox = new ComboBox<>(primitiveTypeList);
		typeComboBox.setEditable(true);

		typeComboBox.setPrefWidth(COLUMN_WIDTH);

		typeComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {

			if (newValue == null)
				return;

			if (newValue.getName().toString().equals("Domainspecific")) {

				Platform.runLater(() -> {
					DomainspecificDatatypesDialog dlg = new DomainspecificDatatypesDialog(diagram);
					dlg.setTitle("Select domainspecific datatype");
					Optional<String> opt = dlg.showAndWait();

					if (opt.isPresent()) {
						String result = opt.get();

						typeList.add(
								new AddAttributeDialogDataType(result, AddAttributeDialogMetaDataType.Domainspecific));
						typeComboBox.setItems(typeList);
						typeComboBox.setValue(
								new AddAttributeDialogDataType(result, AddAttributeDialogMetaDataType.Domainspecific));

						dlg.close();
					}
				});
			}
		});

		typeComboBox.setConverter(new StringConverter<AddAttributeDialogDataType>() {

			@Override
			public String toString(AddAttributeDialogDataType object) {
				if (object == null)
					return "";
				return object.getDisplayName();
			}

			@Override
			public AddAttributeDialogDataType fromString(String string) {

				// try to find the type if the display name is inputted
				AddAttributeDialogDataType type = typeComboBox.getItems().stream()
						.filter(dn -> dn.getDisplayName().equals(string)).findFirst().orElse(null);

				// if type is null check the input via the name, e.g. user inouts "Integer"
				if (type == null) {
					type = typeComboBox.getItems().stream().filter(dn -> dn.getName().equals(string)).findFirst()
							.orElse(null);
				}

				// if type is still not found, check if the string starts with it . input "int"
				// -> "integer"
				if (type == null) {
					type = typeComboBox.getItems().stream().filter(dn -> dn.getName().startsWith(string)).findFirst()
							.orElse(null);
				}
				return type;

			}

		});

		grid.add(classLabel, 0, 0);
		grid.add(classTextField, 1, 0);
		grid.add(selectPropertyLabel, 0, 1);
		grid.add(selectPropertyComboBox, 1, 1);
		grid.add(currentTypeLabel, 0, 2);
		grid.add(currentTypeTextField, 1, 2);
		grid.add(showNonPrimitive, 0, 3, 2, 1);
		grid.add(newTypeLabel, 0, 4);
		grid.add(typeComboBox, 1, 4);
		switch (type) {
		case Attribute:
			dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeAttributeType);
			break;
		case Operation:
			dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeOperationType);
			break;
		default:
			System.err.println("ChangeTypeDialog: No matching content type!");
			break;
		}

		selectPropertyComboBox.valueProperty().addListener(new ChangeListener<Property>() {

			@Override
			public void changed(ObservableValue<? extends Property> observable, Property oldValue, Property newValue) {
				if (newValue instanceof FmmlxAttribute) {
					currentTypeTextField.setText(((FmmlxAttribute) newValue).getType());
				} else if (newValue instanceof FmmlxOperation) {
					currentTypeTextField.setText(((FmmlxOperation) newValue).getType());
				}
			}
		});

		// Define an event handler for changes in the state of the checkbox
		EventHandler<ActionEvent> changedCheckboxPrimitiveEvent = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				// switch of the contents of the typecombobox
				if (showNonPrimitive.isSelected()) {
					typeComboBox.setItems(typeList);
				} else {
					typeComboBox.setItems(primitiveTypeList);
				}
			}
		};

		// Attach the event handler to the checkbox
		showNonPrimitive.setOnAction(changedCheckboxPrimitiveEvent);

		if (selectedItem != null) {
			selectPropertyComboBox.getSelectionModel().select(selectedItem);
		}
	}

	public void setSelected(Property selectedProperty) {
		selectPropertyComboBox.getSelectionModel().select(selectedProperty);
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
