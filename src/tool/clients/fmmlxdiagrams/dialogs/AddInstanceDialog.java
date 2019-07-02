package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.AddInstanceDialogResult;

import java.util.ArrayList;
import java.util.Vector;

public class AddInstanceDialog extends CustomDialog<AddInstanceDialogResult> {

	private FmmlxDiagram diagram;
	private FmmlxObject selectedObject;

	private TextField nameTextField;
	private ListView<FmmlxObject> parentListView;
	private ComboBox<FmmlxObject> ofComboBox;
	private CheckBox abstractCheckBox;
	private Label abstractLabel;
	private ObservableList<FmmlxObject> parentList;
	private ObservableList<FmmlxObject> ofList;
	private Vector<FmmlxObject> objects;

	public AddInstanceDialog(final FmmlxDiagram diagram, FmmlxObject object) {
		super();

		DialogPane dialog = getDialogPane();
		this.diagram = diagram;
		this.selectedObject = object;
		this.objects = diagram.getObjects();

		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		layoutContent(object);
		dialog.setContent(flow);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResult();
	}

	private void layoutContent(FmmlxObject selectedObject) {

		ofList = getAllOfList();
		nameTextField = new TextField();
		parentListView = initializeListView(parentList, SelectionMode.MULTIPLE);

		ofComboBox = initializeComboBox(ofList);
		ofComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.selectedObject = newValue;
				createAndSetParentList();
			}
		});

		if (selectedObject != null) {
			setOf(selectedObject);
			createAndSetParentList();
			ofComboBox.setDisable(true);
		}

		abstractCheckBox = new CheckBox();
		abstractLabel = new Label("Abstract");


		ofComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(new Label("Name"), 0, 0);
		grid.add(nameTextField, 1, 0);
		grid.add(new Label("Of"), 0, 1);
		grid.add(ofComboBox, 1, 1);
		grid.add(abstractLabel, 0, 3);
		grid.add(abstractCheckBox, 1, 3);
		grid.add(new Label("Parent"), 0, 4);
		grid.add(parentListView, 1, 4);
	}

	private void createAndSetParentList() {
		parentList = diagram.getAllPossibleParents(selectedObject.getLevel());
		parentList.remove(selectedObject);
		parentListView.setItems(parentList);
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			int level = 0;
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new AddInstanceDialogResult(nameTextField.getText(), selectedObject.getLevel() - 1,
						parentListView.getSelectionModel().getSelectedItems(), selectedObject.getId(),
						abstractCheckBox.isSelected());
			}
			return null;
		});
	}

	private ObservableList<FmmlxObject> getAllOfList() {
		ArrayList<FmmlxObject> resultOf = new ArrayList<>();
		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects) {
				if (object.getLevel() != 0) {
					resultOf.add(object);
				}
			}
		}

		ObservableList<FmmlxObject> result = FXCollections.observableArrayList(resultOf);
		return result;
	}


	private boolean validateUserInput() {
		if (!validateName()) {
			return false;
		}
		if (!ofSelected()) {
			return false;
		}
		if (!validateCircularDependecies()) {
			return false;
		}
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
		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects) {
				if (nameTextField.getText().equals(object.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean ofSelected() {
		Label errorLabel = getErrorLabel();

		if (ofComboBox.getSelectionModel().getSelectedIndex() == -1) {
			errorLabel.setText("Select Of!");
			return false;
		}
		errorLabel.setText("");
		return true;
	}


	private void setOf(FmmlxObject selectedObject) {

		ofComboBox.setValue(selectedObject);

		ofComboBox.setEditable(false);
	}


	private boolean validateCircularDependecies() {
		// TODO Auto-generated method stub
		return true;
	}
}
