package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.dialogs.results.AddInstanceDialogResult;

public class AddInstanceDialog extends CustomDialog<AddInstanceDialogResult> {

	// TODO:
	// set "of" correct

	private TextField nameTextField;
	private ComboBox<Integer> levelComboBox;
	private ListView<String> parentListView;
	private ComboBox<String> ofComboBox;
	private CheckBox abstractCheckBox;

	// For testing:
	ObservableList<String> parentList = FXCollections.observableArrayList("Comparable", "Cloneable", "Readable",
			"Callable", "Joinable");

	public AddInstanceDialog(String of) {
		super();

		DialogPane dialog = getDialogPane();
		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		layoutContent();

		dialog.setContent(flow);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new AddInstanceDialogResult(nameTextField.getText(),
						levelComboBox.getSelectionModel().getSelectedItem(),
						parentListView.getSelectionModel().getSelectedItems(),
						ofComboBox.getSelectionModel().getSelectedItem(), abstractCheckBox.isSelected());
			}
			return null;
		});

	}

	private void layoutContent() {
		nameTextField = new TextField();
		levelComboBox = new ComboBox<>(LevelList.levelList);
		ofComboBox = new ComboBox<>();
		abstractCheckBox = new CheckBox();

		initializeListView();

		levelComboBox.setPrefWidth(COLUMN_WIDTH);
		ofComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(new Label("Name"), 0, 0);
		grid.add(nameTextField, 1, 0);
		grid.add(new Label("Of"), 0, 1);
		grid.add(ofComboBox, 1, 1);
		grid.add(new Label("Level"), 0, 2);
		grid.add(levelComboBox, 1, 2);
		grid.add(new Label("Abstract"), 0, 3);
		grid.add(abstractCheckBox, 1, 3);
		grid.add(new Label("Parent"), 0, 4);
		grid.add(parentListView, 1, 4);
	}

	private boolean validateUserInput() {
		// TODO: Validate input
		return true;
	}

	private void initializeListView() {
		parentListView = new ListView<>(parentList);

		parentListView.setPrefHeight(75);
		parentListView.setPrefWidth(COLUMN_WIDTH);

		parentListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}
}
