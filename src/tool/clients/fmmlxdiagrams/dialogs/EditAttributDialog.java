package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;

public class EditAttributDialog extends CustomDialog<MetaClassDialogResult> {
	
	private GridPane gridPane= new GridPane();
	
	private Label classLabel;
	private Label selectAttributLabel;
	private Label currentValueLabel;
	private Label newNameLabel;
	private Label newValueLabel;
	
	
	private ComboBox<String> classComboBox;
	private ComboBox<String> selectAttributeComboBox;
	private Label currentValue;
	private TextField newNameTextField;
	private TextField newValueTextField;
	
	
	public EditAttributDialog() {
		super();
		
		DialogPane dialogPane = getDialogPane();
		dialogPane.setHeaderText("Edit Attribute");
		
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
		classLabel = new Label("Select Class");
		selectAttributLabel = new Label("Select Attribute");
		currentValueLabel = new Label("Value");
		newNameLabel = new Label("Set new name");
		newValueLabel = new Label("new Value");
		
		classComboBox = new ComboBox<>();
		selectAttributeComboBox = new ComboBox<>();
		currentValue = new Label(getCurrentValue());
		newNameTextField = new TextField();
		newValueTextField = new TextField();
		
		
		classComboBox.setPrefWidth(COLUMN_WIDTH);
		selectAttributeComboBox.setPrefWidth(COLUMN_WIDTH);
		
		
		gridPane.add(classLabel, 0, 0);
		gridPane.add(classComboBox, 1, 0);
		gridPane.add(selectAttributLabel, 0, 1);
		gridPane.add(selectAttributeComboBox, 1, 1);
		gridPane.add(currentValueLabel, 0, 2);
		gridPane.add(currentValue, 1, 2);
		gridPane.add(newNameLabel, 0, 3);
		gridPane.add(newNameTextField, 1, 3);
		gridPane.add(newValueLabel, 0, 4);
		gridPane.add(newValueTextField, 1, 4);
	}


	private String getCurrentValue() {
		return "Current Value";
	}
	
	

}
