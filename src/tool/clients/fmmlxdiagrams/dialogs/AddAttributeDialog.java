package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.dialogs.results.AddAttributeDialogResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;


public class AddAttributeDialog extends CustomDialog<AddAttributeDialogResult> {

	ObservableList<String> classList;
	List<String> typesArray;
	private Label nameLabel;
	private Label classLabel;
	private Label levelLabel;
	private Label typeLabel;
	private Label multiplicityLabel;
	private TextField nameTextField;
	private ComboBox<String> classCombobox;
	private ComboBox<Integer> levelComboBox;
	private ComboBox<String> typeComboBox;
	private Vector<FmmlxObject> objects;
	private FmmlxObject selectedObject;
	private Button multiplicityButton;
	private Multiplicity multiplicity = Multiplicity.OPTIONAL;

	public AddAttributeDialog(final FmmlxDiagram diagram) {
		this(diagram, null);
	}

	public AddAttributeDialog(final FmmlxDiagram diagram, FmmlxObject selectedObject) {
		super();

		DialogPane dialogPane = getDialogPane();
		this.objects = diagram.getObjects();

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		addElementToGrid();

		if (selectedObject != null) {
			this.selectedObject = selectedObject;
			setSelectedObject();
		}

		dialogPane.setContent(grid);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				int classId = 0;
				for (FmmlxObject object : objects) {
					if (classCombobox.getSelectionModel().getSelectedItem().equals(object.getName())) {
						classId = object.getId();
					}
				}

				//TODO AddAttributeDialogResult and Multiplicity Result
				return new AddAttributeDialogResult(
						classId,
						nameTextField.getText(),
						levelComboBox.getSelectionModel().getSelectedItem(),
						getComboBoxStringValue(typeComboBox),
						new Multiplicity(0, 1, true, false, false));
			}
			return null;
		});


	}

	private void setSelectedObject() {
		classCombobox.getSelectionModel().select(selectedObject.getName());
	}

	private ObservableList<String> getAllClassList() {
		ArrayList<String> resultStrings = new ArrayList<String>();

		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects) {
				resultStrings.add(object.getName());
			}
		}
		ObservableList<String> result = FXCollections.observableArrayList(resultStrings);
		return result;
	}


	private boolean validateUserInput() {
		if (!classChosen()) {
			return false;
		}
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

		if (getComboBoxStringValue(typeComboBox) == null || getComboBoxStringValue(typeComboBox).length() < 1) {
			errorLabel.setText("Select Type!");
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	private boolean classChosen() {
		Label errorLabel = getErrorLabel();

		if (classCombobox.getSelectionModel().getSelectedIndex() == -1) {
			errorLabel.setText("Select Class!");
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	private boolean validateLevel() {
		Label errorLabel = getErrorLabel();

		if (classCombobox.getSelectionModel().getSelectedIndex() == -1) {
			errorLabel.setText("Select Level!");
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	private boolean validateName() {
		Label errorLabel = getErrorLabel();
		String name = nameTextField.getText();

		if (isNullOrEmpty(name)) {
			errorLabel.setText("Enter valid name!");
			return false;
		} else if (nameAlreadyUsed()) {
			errorLabel.setText("Name already used");
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private boolean nameAlreadyUsed() {

		Vector<FmmlxDiagram> diagrams = FmmlxDiagramCommunicator.getDiagrams();
		Vector<FmmlxObject> objects = diagrams.get(0).getObjects();

		for (FmmlxObject object : objects) {
			if (classCombobox.getSelectionModel().getSelectedItem().equals(object.getName())) {
				for (FmmlxAttribute attribute : object.getOwnAttributes()) {
					if (nameTextField.getText().equals(attribute.getName())) return true;
				}
				for (FmmlxAttribute attribute : object.getOtherAttributes()) {
					if (nameTextField.getText().equals(attribute.getName())) return true;

				}
			}
		}
		return false;
	}

	private void addElementToGrid() {
		nameLabel = new Label("Name");
		classLabel = new Label("Class");
		levelLabel = new Label("Level");
		typeLabel = new Label("Type");
		multiplicityLabel = new Label("Multiplicity");
		classList = getAllClassList();

		String[] types = new String[]{"Integer", "String", "Boolean", "Float"};
		typesArray = Arrays.asList(types);
		ObservableList<String> typeList = FXCollections.observableArrayList(typesArray);

		nameTextField = new TextField();
		classCombobox = new ComboBox<>(classList);
		levelComboBox = new ComboBox<>(LevelList.levelList);
		typeComboBox = new ComboBox<>(typeList);
		typeComboBox.setEditable(true);
		multiplicityButton = new Button();
		//multiplicityButton.setText("Add / Edit Multiplicity");
		multiplicityButton.setText(multiplicity.getClass().getSimpleName());
		multiplicityButton.setOnAction(e -> {
			new MultiplicityDialog(multiplicity).showAndWait();
			/*if successful multiplicity = result;
			 *multiplicityButton.setText(multiplicity.toString());*/
		});
		classCombobox.setPrefWidth(COLUMN_WIDTH);
		levelComboBox.setPrefWidth(COLUMN_WIDTH);
		typeComboBox.setPrefWidth(COLUMN_WIDTH);
		multiplicityButton.setPrefWidth(COLUMN_WIDTH);

		grid.add(nameLabel, 0, 1);
		grid.add(nameTextField, 1, 1);
		grid.add(classLabel, 0, 0);
		grid.add(classCombobox, 1, 0);
		grid.add(levelLabel, 0, 2);
		grid.add(levelComboBox, 1, 2);
		grid.add(typeLabel, 0, 3);
		grid.add(typeComboBox, 1, 3);
		grid.add(multiplicityLabel, 0, 4);
		grid.add(multiplicityButton, 1, 4);

	}
}
