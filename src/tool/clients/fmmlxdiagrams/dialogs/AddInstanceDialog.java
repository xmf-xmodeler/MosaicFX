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
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;

public class AddInstanceDialog extends CustomDialog<MetaClassDialogResult> {
		
	private GridPane grid;
	
	private TextField nameTextField;
	private ComboBox<String> levelComboBox;
	private ComboBox<String> parentComboBox;
	private ComboBox<String> ofComboBox;
	private CheckBox abstractCheckBox;
	
	public AddInstanceDialog(String of) {
		super();
		
		DialogPane dialog = getDialogPane();
		dialog.setHeaderText("Add Instance");
		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		grid = initializeGrid();
		layoutContent();
		
		dialog.setContent(grid);
		
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});		
		
	}
	
	private void layoutContent() {
		nameTextField = new TextField();
		levelComboBox = new ComboBox<>(LevelList.levelList);
		parentComboBox = new ComboBox<>();
		ofComboBox = new ComboBox<>();
		abstractCheckBox = new CheckBox();
		
		levelComboBox.setPrefWidth(COLUMN_WIDTH);
		parentComboBox.setPrefWidth(COLUMN_WIDTH);
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
		grid.add(parentComboBox, 1, 4);		
	}
	
	private boolean validateUserInput() {
		//TODO: Validate input
		return true;
	}
	

}
