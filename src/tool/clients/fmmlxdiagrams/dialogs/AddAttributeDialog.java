package tool.clients.fmmlxdiagrams.dialogs;

import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class AddAttributeDialog extends CustomDialog<MetaClassDialogResult> {
	
	private GridPane gridPane = new GridPane();
	
	private Label nameLabel ;
	private Label classLabel;
	private Label levelLabel;
	private Label typeLabel;
	private Label multiplicityLabel;
	
	private TextField nameTextField;
	private ComboBox<String> classCombobox; 
	private ComboBox<String> levelComboBox; 
	private ComboBox<String> typeComboBox; 
	private ComboBox<String> multiplicityComboBox; 
	
	
	public AddAttributeDialog() {
		super();
		
		DialogPane dialogPane = getDialogPane();
		dialogPane.setHeaderText("Add Attribute");
		
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		gridPane = initializeGrid();
		addElementToGrid();

		dialogPane.setContent(grid);
		
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				//TODO
			}
			return null;
		});
		
		
	}
	private boolean validateUserInput() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	private void addElementToGrid() {
		nameLabel = new Label("Name");
		classLabel = new Label("Class");
		levelLabel = new Label("Level");
		typeLabel = new Label("Type");
		multiplicityLabel = new Label("Multiplicity");
		
		nameTextField = new TextField();
		classCombobox = new ComboBox<>();
		levelComboBox = new ComboBox<>();
		typeComboBox = new ComboBox<>();
		multiplicityComboBox = new ComboBox<>();
		
		classCombobox.setPrefWidth(COLUMN_WIDTH);
		levelComboBox.setPrefWidth(COLUMN_WIDTH);
		typeComboBox.setPrefWidth(COLUMN_WIDTH);
		multiplicityComboBox.setPrefWidth(COLUMN_WIDTH);
		
		gridPane.add(nameLabel, 0, 0);
		gridPane.add(nameTextField, 1, 0);
		gridPane.add(classLabel, 0, 1);
		gridPane.add(classCombobox, 1, 1);
		gridPane.add(levelLabel, 0, 2);
		gridPane.add(levelComboBox, 1, 2);
		gridPane.add(typeLabel, 0, 3);
		gridPane.add(typeComboBox, 1, 3);
		gridPane.add(multiplicityLabel, 0, 4);
		gridPane.add(multiplicityComboBox, 1, 4);
		
	}

}
