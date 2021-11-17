package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.util.converter.IntegerStringConverter;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.MetaClassDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;

public class CreateMetaClassDialog extends CustomDialog<MetaClassDialogResult> {

	private AbstractPackageViewer diagram;
	private ObservableList<FmmlxObject> possibleParents;

	private Label nameLabel;
	private Label levelLabel;
	private Label abstractLabel;
	private Label parentLabel;
	private TextField nameTextField;
	private ComboBox<Integer> levelComboBox;
	private ListView<FmmlxObject> parentListView;
	private CheckBox abstractCheckbox;

	public CreateMetaClassDialog(AbstractPackageViewer diagram) {
		super();
		this.diagram = diagram;

		DialogPane dialog = getDialogPane();
		dialog.setHeaderText("New MetaClass");

		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		layoutContent();

		dialog.setContent(flow);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResult();
	}

	private void layoutContent() {
		nameLabel = new Label("Name");
		levelLabel = new Label("Level");
		abstractLabel = new Label("Abstract");
		parentLabel = new Label("Parent");

		nameTextField = new TextField();
		parentListView = initializeListView(possibleParents, SelectionMode.MULTIPLE);
		levelComboBox = new ComboBox<>(AllValueList.levelList);
		levelComboBox.setConverter(new IntegerStringConverter());
		levelComboBox.setEditable(true);
		levelComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				possibleParents = diagram.getAllPossibleParents(newValue);
				parentListView.setItems(possibleParents);
				parentListView.setDisable(false);
				if (possibleParents.size() == 0) {
					parentListView.setDisable(true);
				}
			}
		});
		abstractCheckbox = new CheckBox();

		levelComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(nameLabel, 0, 0);
		grid.add(nameTextField, 1, 0);
		grid.add(levelLabel, 0, 1);
		grid.add(levelComboBox, 1, 1);
		grid.add(abstractLabel, 0, 2);
		grid.add(abstractCheckbox, 1, 2);
		grid.add(parentLabel, 0, 3);
		grid.add(parentListView, 1, 3);
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new MetaClassDialogResult(nameTextField.getText(),
						getComboBoxIntegerValue(levelComboBox), abstractCheckbox.isSelected(), parentListView.getSelectionModel().getSelectedItems());
			}
			return null;
		});
	}

	private boolean validateUserInput() {
		String name = nameTextField.getText();

		Label errorLabel = getErrorLabel();
		
		if (!InputChecker.validateName(name)) {	
			errorLabel.setText("Enter valid name!");
			return false;
		} else if (!InputChecker.getInstance().classNameIsAvailable(name, diagram)) {
			errorLabel.setText("Name already used");
			return false;
		} else if (getComboBoxIntegerValue(levelComboBox) == null) {
			errorLabel.setText("Enter level as integer!");
			return false;
		}
		return true;
	}
}
