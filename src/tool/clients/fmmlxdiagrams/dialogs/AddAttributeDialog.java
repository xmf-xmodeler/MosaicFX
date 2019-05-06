package tool.clients.fmmlxdiagrams.dialogs;

import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.AddAttributeDialogResult;

public class AddAttributeDialog extends CustomDialog<AddAttributeDialogResult> {
	
	private Label nameLabel ;
	private Label classLabel;
	private Label levelLabel;
	private Label typeLabel;
	private Label multiplicityLabel;
	
	private TextField nameTextField;
	private ComboBox<String> classCombobox; 
	private ComboBox<Integer> levelComboBox; 
	private ComboBox<String> typeComboBox; 

	private Vector<FmmlxObject> objects;

	private Button multiplicityButton; 
	ObservableList<String> classList;
	List<String> typesArray;


	public AddAttributeDialog(final FmmlxDiagram diagram) {
		super();
		
		DialogPane dialogPane = getDialogPane();
		this.objects = diagram.getObjects();
		
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
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
				int classId = 0;
				for (FmmlxObject object : objects) {
					if(classCombobox.getSelectionModel().getSelectedItem().equals(object.getName())) {
						classId = object.getId();
					}
				}
				
				//TODO AddAttributeDialogResult and Multiplicity Result
				/*return new AddAttributeDialogResult(
						classId,
						nameTextField.getText(), 
						levelComboBox.getSelectionModel().getSelectedItem(),
						typeComboBox.getSelectionModel().getSelectedItem(), multiplicityResult);*/
			}
			return null;
		});
		
		

	}
	private ObservableList<String> getAllClassList() {
		ArrayList<String> resultStrings = new ArrayList<String>();
	
		if (!objects.isEmpty()) {
			for (FmmlxObject object :objects) {
				resultStrings.add(object.getName());
			}
		}
		ObservableList<String> result = FXCollections.observableArrayList( resultStrings);
		return result;
	}
	
	
	private boolean validateUserInput() {
		if (!classChoosen()) {
			return false;
		}
		if (!validateName()) {
			return false;
		}
		if (!validateLevel()) {
			return false;
		}
		if (!validateType()) {
			return false;
		}
		
		return true;
	}
	
	
	private boolean validateType() {
		Label errorLabel = getErrorLabel();
		
		if (typeComboBox.getSelectionModel().getSelectedIndex()==-1) {
			errorLabel.setText("Select Type!");
			return false;
		}
		errorLabel.setText("");
		return true;
	}
	
	private boolean classChoosen() {
		Label errorLabel = getErrorLabel();
		
		if (classCombobox.getSelectionModel().getSelectedIndex()==-1) {
			errorLabel.setText("Select Class!");
			return false;
		}
		errorLabel.setText("");
		return true;
	}
	
	private boolean validateLevel() {
		Label errorLabel = getErrorLabel();
		
		if (classCombobox.getSelectionModel().getSelectedIndex()==-1) {
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
		if (!objects.isEmpty()) {
			for (FmmlxObject object :objects) {
				if(classCombobox.getSelectionModel().getSelectedItem().equals(object.getName())) {
					/*for (FmmlxAttribute attributes : object.getAttributes()) {
						if(nameTextField.getText().equals(attribute.getName())) {
						return true;
						}
					}
					break;*/
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
		
		String[] types = new String[] { "Integer", "String", "Boolean","Double","Float"};
		typesArray = Arrays.asList(types);
		ObservableList<String> typeList= FXCollections.observableArrayList(typesArray);
		
		nameTextField = new TextField();
		classCombobox = new ComboBox<>(classList);
		levelComboBox = new ComboBox<>(LevelList.levelList);
		typeComboBox = new ComboBox<>(typeList);
		multiplicityButton = new Button();
		multiplicityButton.setText("Add / Edit Multiplicity");
		multiplicityButton.setOnAction(e -> new MultiplicityDialog().showAndWait());
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
