package tool.clients.fmmlxdiagrams.dialogs.instance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.InputChecker;
import tool.clients.fmmlxdiagrams.dialogs.results.AddInstanceDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.ArrayList;
import java.util.Vector;

public class AddInstanceDialog extends CustomDialog<AddInstanceDialogResult> {

	private final FmmlxDiagram diagram;
	private FmmlxObject selectedObject;
	private TextField nameTextField;
	private ListView<FmmlxObject> parentListView;
	private ComboBox<FmmlxObject> ofComboBox;
	private CheckBox abstractCheckBox;
	private ObservableList<FmmlxObject> parentList;
	private final Vector<FmmlxObject> objects;

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

	@SuppressWarnings("unchecked")
	private void layoutContent(FmmlxObject selectedObject) {

		ObservableList<FmmlxProperty> ofList = getAllOfList();
		nameTextField = new TextField();
		abstractCheckBox = new CheckBox();
		parentListView = initializeListView(parentList, SelectionMode.MULTIPLE);

		ofComboBox = (ComboBox<FmmlxObject>) initializeComboBox(ofList);
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
			setInstanceName(selectedObject);
		}
		ofComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(new Label(StringValue.LabelAndHeaderTitle.name), 0, 0);
		grid.add(nameTextField, 1, 0);
		grid.add(new Label(StringValue.LabelAndHeaderTitle.of), 0, 1);
		grid.add(ofComboBox, 1, 1);
		grid.add(new Label(StringValue.LabelAndHeaderTitle.abstractBig), 0, 3);
		grid.add(abstractCheckBox, 1, 3);
		grid.add(new Label(StringValue.LabelAndHeaderTitle.parent), 0, 4);
		grid.add(parentListView, 1, 4);
	}

	private void setInstanceName(FmmlxObject c) {
		nameTextField.setText(c.getAvailableInstanceName());
		
	}

	private void createAndSetParentList() {
		if (selectedObject != null) {
			parentList = diagram.getAllPossibleParents(selectedObject.getLevel() - 1);
			parentListView.setItems(parentList);
		}
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new AddInstanceDialogResult(nameTextField.getText(), selectedObject.getLevel() - 1,
						parentListView.getSelectionModel().getSelectedItems(), selectedObject.getName(),
						abstractCheckBox.isSelected());
			}
			return null;
		});
	}

	private ObservableList<FmmlxProperty> getAllOfList() {
		ArrayList<FmmlxObject> resultOf = new ArrayList<>();
		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects) {
				if (object.getLevel() != 0) {
					resultOf.add(object);
				}
			}
		}

		return FXCollections.observableArrayList(resultOf);
	}


	private boolean validateUserInput() {
		if (!validateName()) {
			return false;
		}
		if (!ofSelected()) {
			return false;
		}
		return validateCircularDependecies();
	}

	private boolean validateName() {
		Label errorLabel = getErrorLabel();
		String name = nameTextField.getText();

		if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidName);
			return false;
		} else if (!InputChecker.getInstance().classNameIsAvailable(name, diagram)) {
			errorLabel.setText(StringValue.ErrorMessage.nameAlreadyUsed);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private boolean ofSelected() {
		Label errorLabel = getErrorLabel();

		if (ofComboBox.getSelectionModel().getSelectedIndex() == -1) {
			errorLabel.setText(StringValue.ErrorMessage.selectOf);
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
