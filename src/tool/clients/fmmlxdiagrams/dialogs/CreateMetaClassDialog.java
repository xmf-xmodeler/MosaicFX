package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class CreateMetaClassDialog extends Dialog<MetaClassDialogResult> {

	private final int COLUMN_WIDTH = 150;

	private final ObservableList<String> levelsList = FXCollections.observableArrayList("1", "2", "3", "4", "5");
	private final ObservableList<String> parentList = FXCollections.observableArrayList();

	private GridPane grid;

	private Label nameLabel;
	private Label levelLabel;
	private Label abstractLabel;
	private Label parentLabel;

	private TextField nameTextField;
	private ComboBox<String> levelComboBox;
	private ComboBox<String> parentComboBox;
	private CheckBox abstractCheckbox;

	public CreateMetaClassDialog() {
		super();
		DialogPane dialog = getDialogPane();
		dialog.setHeaderText("New MetaClass");

		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		initializeGrid();
		createContent();

		dialog.setContent(grid);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new MetaClassDialogResult(nameTextField.getText(),
						levelComboBox.getSelectionModel().getSelectedItem(), abstractCheckbox.isSelected(), "");
			}
			return null;
		});
	}

	private void initializeGrid() {
		grid = new GridPane();
		grid.setHgap(3);
		grid.setVgap(3);
		grid.setPadding(new Insets(10, 10, 10, 10));

		ColumnConstraints cc;
		for (int i = 0; i < 2; i++) {
			cc = new ColumnConstraints();
			cc.setMaxWidth(COLUMN_WIDTH);
			cc.setMinWidth(COLUMN_WIDTH);
			cc.setFillWidth(true);
			grid.getColumnConstraints().add(cc);
		}

	}

	private void createContent() {
		nameLabel = new Label("Name");
		levelLabel = new Label("Level");
		abstractLabel = new Label("Abstract");
		parentLabel = new Label("Parent");

		nameTextField = new TextField();
		levelComboBox = new ComboBox<>(levelsList);
		parentComboBox = new ComboBox<>(parentList);
		abstractCheckbox = new CheckBox();

		levelComboBox.setPrefWidth(COLUMN_WIDTH);
		parentComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(nameLabel, 0, 0);
		grid.add(nameTextField, 1, 0);
		grid.add(levelLabel, 0, 1);
		grid.add(levelComboBox, 1, 1);
		grid.add(abstractLabel, 0, 2);
		grid.add(abstractCheckbox, 1, 2);
		grid.add(parentLabel, 0, 3);
		grid.add(parentComboBox, 1, 3);
	}

	private boolean validateUserInput() {

		String name = nameTextField.getText();
		String level = levelComboBox.getSelectionModel().getSelectedItem();

		if (!name.isEmpty() && !level.isEmpty()) {
			return true;
		}

		return false;
	}

	public GridPane getGrid() {
		return grid;
	}

	public TextField getNameTextField() {
		return nameTextField;
	}

	public ComboBox<String> getLevelComboBox() {
		return levelComboBox;
	}

	public ComboBox<String> getParentComboBox() {
		return parentComboBox;
	}

	public CheckBox getAbstractCheckbox() {
		return abstractCheckbox;
	}

}
