package tool.clients.fmmlxdiagrams.dialogs;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.util.StringConverter;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxEnum;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Level;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.AddAttributeDialogDataType.AddAttributeDialogMetaDataType;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.*;

import org.apache.batik.apps.svgbrowser.JSVGViewerFrame.NewWindowAction;

public class AddAttributeDialog extends CustomDialog<AddAttributeDialog.Result> {

	private Label nameLabel;
	private Label classLabel;
	private Label levelLabel;
	private Label typeLabel;
	private Label multiplicityLabel;
	private Label displayMultiplicityLabel;
	private Label isIntrinsicLabel;
	private Label isIncompleteLabel;
	private Label isOptionalLabel;

	private TextField nameTextField;
	private TextField classTextField;
	private LevelBox levelComboBox;
	private ComboBox<AddAttributeDialogDataType> typeComboBox;
	private CheckBox isIntrinsicBox;
	private CheckBox isIncompleteBox;
	private CheckBox isOptionalBox;
	private CheckBox showNonPrimitive;

	private AbstractPackageViewer diagram;
	private FmmlxObject selectedObject;
	private Button multiplicityButton;
	private Multiplicity multiplicity = Multiplicity.MANDATORY;

	private Vector<AddAttributeDialogDataType> types;
	private Vector<AddAttributeDialogDataType> primitiveTypes;
	private Vector<FmmlxObject> diagramObjects;
	private Vector<FmmlxEnum> diagramEnums;

	public AddAttributeDialog(final AbstractPackageViewer diagram) {
		this(diagram, null);
	}

	public AddAttributeDialog(final AbstractPackageViewer diagram, FmmlxObject selectedObject) {
		super();
		this.diagram = diagram;
		primitiveTypes = new Vector<AddAttributeDialogDataType>();
		// by this we can differentiate between a type/ name (relevant for XMF) and a
		// display name
		primitiveTypes.add(new AddAttributeDialogDataType("Boolean", AddAttributeDialogMetaDataType.Primitive));
		primitiveTypes.add(new AddAttributeDialogDataType("Integer", AddAttributeDialogMetaDataType.Primitive));
		primitiveTypes.add(new AddAttributeDialogDataType("Float", AddAttributeDialogMetaDataType.Primitive));
		primitiveTypes.add(new AddAttributeDialogDataType("String", AddAttributeDialogMetaDataType.Primitive));
		primitiveTypes.add(new AddAttributeDialogDataType("Date", AddAttributeDialogMetaDataType.Primitive));

		types = new Vector<AddAttributeDialogDataType>(primitiveTypes);
		
		types.add(new AddAttributeDialogDataType("MonetaryValue", AddAttributeDialogMetaDataType.NonPrimitive));
		types.add(new AddAttributeDialogDataType("Currency", AddAttributeDialogMetaDataType.NonPrimitive));

		if(!diagram.isUMLMode()) {
			types.add(new AddAttributeDialogDataType("Complex", AddAttributeDialogMetaDataType.NonPrimitive));
			types.add(new AddAttributeDialogDataType("AuxiliaryClass", AddAttributeDialogMetaDataType.NonPrimitive));
		}

		// add enums to type list
		diagramEnums = diagram.getEnums();
		for (FmmlxEnum e : diagramEnums) {
			types.add(new AddAttributeDialogDataType(e.getName(), AddAttributeDialogMetaDataType.Enum));

		}

		types.add(new AddAttributeDialogDataType("Domain-Specific", AddAttributeDialogMetaDataType.Domainspecific));

		diagramObjects = diagram.getObjectsReadOnly();

		DialogPane dialogPane = getDialogPane();
		this.selectedObject = selectedObject;

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		layout();

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

				if (!diagram.isUMLMode()) {
					return new Result(selectedObject.getPath(), nameTextField.getText(), levelComboBox.getLevel(),
							datatype, multiplicity, isIntrinsicBox.isSelected(), isIncompleteBox.isSelected(),
							isOptionalBox.isSelected());
				} else {
					return new Result(selectedObject.getPath(), nameTextField.getText(), new Level(0, 0), datatype,
							multiplicity, true, false, false);
				}
			}
			return null;
		});

	}

	private boolean validateUserInput() {
		if (!validateName()) {
			return false;
		}
		if (!validateLevel()) {
			return false;
		}
		return validateType();
	}

	private boolean validateType() {

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
	}

	private boolean validateLevel() {
		Label errorLabel = getErrorLabel();

		if (levelComboBox.getLevel() == null) {
			errorLabel.setText(StringValue.ErrorMessage.selectLevel);
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	private boolean validateName() {
		Label errorLabel = getErrorLabel();
		String name = nameTextField.getText();

		if (!InputChecker.isValidIdentifier(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidName);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private void layout() {
		nameLabel = new Label(StringValue.LabelAndHeaderTitle.name);
		classLabel = new Label(StringValue.LabelAndHeaderTitle.selectedObject);
		levelLabel = new Label(StringValue.LabelAndHeaderTitle.level);
		typeLabel = new Label(StringValue.LabelAndHeaderTitle.type);
		multiplicityLabel = new Label(StringValue.LabelAndHeaderTitle.Multiplicity);
		showNonPrimitive = new CheckBox("Show all data types");
		isIntrinsicLabel = new Label("intrinsic");
		isIncompleteLabel = new Label("incomplete");
		isOptionalLabel = new Label("optional");

		ObservableList<AddAttributeDialogDataType> primitiveTypeList = FXCollections
				.observableArrayList(primitiveTypes);
		// add all types
		ObservableList<AddAttributeDialogDataType> typeList = FXCollections.observableArrayList(types);

		nameTextField = new TextField();
		classTextField = new TextField();
		classTextField.setText(selectedObject.getName());
		classTextField.setDisable(true);
		levelComboBox = new LevelBox(new Level(selectedObject.getLevel().getMinLevel() - 1));

		// initial values for the combobox are only primitive types, selecting the
		// checkbox can change that
		typeComboBox = new ComboBox<>(primitiveTypeList);
		typeComboBox.setEditable(true);

		typeComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if (newValue == null) {
				return;
			}
			if (newValue.getName().toString().equals("Domain-Specific")) {

				Platform.runLater(() -> {
					DomainspecificDatatypesDialog dlg = new DomainspecificDatatypesDialog(diagram);
					dlg.setTitle("Select Domain-Specific datatype");
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

		multiplicityButton = new Button();
		multiplicityButton.setText(multiplicity.getClass().getSimpleName());
		multiplicityButton.setOnAction(e -> {
			showMultiplicityDialog();
		});
		displayMultiplicityLabel = new Label(multiplicity.toString());

		classTextField.setPrefWidth(COLUMN_WIDTH);
		showNonPrimitive.setPrefWidth(COLUMN_WIDTH);
		levelComboBox.setPrefWidth(COLUMN_WIDTH);
		typeComboBox.setPrefWidth(COLUMN_WIDTH);
		multiplicityButton.setPrefWidth(COLUMN_WIDTH);

		isIntrinsicBox = new CheckBox();
		isIntrinsicBox.setSelected(true);
		isIncompleteBox = new CheckBox();
		isOptionalBox = new CheckBox();

		grid.add(nameTextField, 1, 0);
		grid.add(nameLabel, 0, 0);
		grid.add(classLabel, 0, 1);
		if (!diagram.isUMLMode()) {
			grid.add(levelLabel, 0, 3);
			grid.add(multiplicityLabel, 0, 5);
			grid.add(isIntrinsicLabel, 0, 7);
			grid.add(isIncompleteLabel, 0, 8);
			grid.add(isOptionalLabel, 0, 9);
			grid.add(levelComboBox, 1, 3);
		}
			grid.add(typeComboBox, 1, 4);
			if (!diagram.isUMLMode()) {
			grid.add(isIntrinsicBox, 1, 7);
			grid.add(isIncompleteBox, 1, 8);
			grid.add(isOptionalBox, 1, 9);
			grid.add(multiplicityButton, 1, 5);
			grid.add(displayMultiplicityLabel, 1, 6);
		}
		grid.add(typeLabel, 0, 4);

		grid.add(classTextField, 1, 1);
		grid.add(showNonPrimitive, 1, 2);

		// Define an event handler for changes in the state of the checkbox
		// showNonPrimitives
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

	}

	private void showMultiplicityDialog() {
		MultiplicityDialog dlg = new MultiplicityDialog(multiplicity);
		Optional<Multiplicity> opt = dlg.showAndWait();

		if (opt.isPresent()) {
			multiplicity = opt.get();

			displayMultiplicityLabel.setText(multiplicity.toString());
		}
	}

	public static class Result {

		public final String name;
		public final String type;
		public final Level level;
		public final String classPath;
		public final Multiplicity multi;
		public final boolean isIntrinsic;
		public final boolean isIncomplete;
		public final boolean isOptional;

		private Result(String classPath, String name, Level level, String type, Multiplicity multi, boolean isIntrinsic,
				boolean isIncomplete, boolean isOptional) {
			this.classPath = classPath;
			this.name = name;
			this.level = level;
			this.type = type;
			this.multi = multi;
			this.isIntrinsic = isIntrinsic;
			this.isIncomplete = isIncomplete;
			this.isOptional = isOptional;

		}
	}
}
