package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.util.converter.IntegerStringConverter;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.results.AddAttributeDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.MultiplicityDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

import java.util.*;


public class AddAttributeDialog extends CustomDialog<AddAttributeDialogResult> {

	private ObservableList<String> classList;
	private List<String> typesArray;
	private Label nameLabel;
	private Label classLabel;
	private Label levelLabel;
	private Label typeLabel;
	private Label multiplicityLabel;
	private Label displayMultiplicityLabel;

	private TextField nameTextField;
	private TextField classTextField;
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
		this.selectedObject = selectedObject;

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		addElementToGrid();

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
				int classId = selectedObject.getId();

				//TODO AddAttributeDialogResult and Multiplicity Result
				return new AddAttributeDialogResult(
						classId,
						nameTextField.getText(),
						levelComboBox.getSelectionModel().getSelectedItem(),
						getComboBoxStringValue(typeComboBox),
						multiplicity);
			}
			return null;
		});

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
			errorLabel.setText(StringValueDialog.ErrorMessage.selectType);
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	private boolean validateLevel() {
		Label errorLabel = getErrorLabel();

		if (levelComboBox.getSelectionModel().getSelectedIndex() == -1) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectLevel);
			return false;
		}
		errorLabel.setText("");
		return true;
	}


	private boolean validateName() {
		Label errorLabel = getErrorLabel();
		String name = nameTextField.getText();

		if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.enterValidName);
			return false;
		} else if (!InputChecker.getInstance().attributeNameIsAvailable(name, selectedObject)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.nameAlreadyUsed);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private void addElementToGrid() {
		nameLabel = new Label(StringValueDialog.LabelAndHeaderTitle.name);
		classLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectedObject);
		levelLabel = new Label(StringValueDialog.LabelAndHeaderTitle.level);
		typeLabel = new Label(StringValueDialog.LabelAndHeaderTitle.type);
		multiplicityLabel = new Label(StringValueDialog.LabelAndHeaderTitle.Multiplicity);
		classList = getAllClassList();

		String[] types = new String[]{"Integer", "String", "Boolean", "Float"};
		typesArray = Arrays.asList(types);
		ObservableList<String> typeList = FXCollections.observableArrayList(typesArray);

		nameTextField = new TextField();
		classTextField = new TextField();
		classTextField.setText(selectedObject.getName());
		classTextField.setDisable(true);
		levelComboBox = new ComboBox<>(LevelList.getLevelInterval(selectedObject));
		levelComboBox.setConverter(new IntegerStringConverter());
		typeComboBox = new ComboBox<>(typeList);
		typeComboBox.setEditable(true);
		multiplicityButton = new Button();
		//multiplicityButton.setText("Add / Edit Multiplicity");
		multiplicityButton.setText(multiplicity.getClass().getSimpleName());
		multiplicityButton.setOnAction(e -> {
			showMultiplicityDialog();
		});
		displayMultiplicityLabel = new Label(multiplicity.toString());

		classTextField.setPrefWidth(COLUMN_WIDTH);
		levelComboBox.setPrefWidth(COLUMN_WIDTH);
		typeComboBox.setPrefWidth(COLUMN_WIDTH);
		multiplicityButton.setPrefWidth(COLUMN_WIDTH);

		grid.add(nameLabel, 0, 1);
		grid.add(nameTextField, 1, 1);
		grid.add(classLabel, 0, 0);
		grid.add(classTextField, 1, 0);
		grid.add(levelLabel, 0, 2);
		grid.add(levelComboBox, 1, 2);
		grid.add(typeLabel, 0, 3);
		grid.add(typeComboBox, 1, 3);
		grid.add(multiplicityLabel, 0, 4);
		grid.add(multiplicityButton, 1, 4);
		grid.add(displayMultiplicityLabel, 1, 5);

	}

	private void showMultiplicityDialog() {
		MultiplicityDialog dlg = new MultiplicityDialog(multiplicity);
		Optional<MultiplicityDialogResult> opt = dlg.showAndWait();

		if (opt.isPresent()) {
			MultiplicityDialogResult result = opt.get();

			multiplicity = result.convertToMultiplicity();
			displayMultiplicityLabel.setText(multiplicity.toString());
		}
	}
}
