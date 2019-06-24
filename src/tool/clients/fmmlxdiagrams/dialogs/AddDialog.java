package tool.clients.fmmlxdiagrams.dialogs;

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
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.results.AddAttributeDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.AddDialogResult;

public class AddDialog extends CustomDialog<AddDialogResult>{
	
	private final DialogType type;
	private FmmlxObject object;
	private Vector<FmmlxObject> objects;
	
	//For add Attribute
	private Label nameLabel ;
	private Label classLabel;
	private Label levelLabel;
	private Label typeLabel;
	private Label multiplicityLabel;
	
	private TextField nameTextField;
	private TextField classTextField; 
	private ComboBox<Integer> levelComboBox; 
	private ComboBox<String> typeComboBox;
	

	private Button multiplicityButton; 
	ObservableList<String> classList;
	List<String> typesArray;

	private Multiplicity multiplicity = Multiplicity.OPTIONAL;

	public AddDialog(FmmlxObject object, DialogType type2) {
		super();
		this.object = object;
		this.type = type2;
		
		DialogPane dialogPane = getDialogPane();
		
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent();
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
				switch (type) {
				case Class:
					setResultAddMetaClass(dlgBtn);
					break;
				case Attribute:
					setResultGenerateLayoutAddAttribute(dlgBtn);
					break;
				case Operation:
					SetResultAddOperation(dlgBtn);
					break;
				case Association:
					setResultAddAssociation(dlgBtn);
					break;
				default:
					System.err.println("AddDialog: No matching content type!");	
				}
			}
			return null;
		});
	}
	

	private void setResultGenerateLayoutAddAttribute(ButtonType dlgBtn) {
		// TODO Auto-generated method stub
		
	}

	private void setResultAddAssociation(ButtonType dlgBtn) {
		// TODO Auto-generated method stub
		
	}

	private void SetResultAddOperation(ButtonType dlgBtn) {
		// TODO Auto-generated method stub
		
	}

	private void setResultAddAttribute(ButtonType dlgBtn) {
		if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {

			
			//TODO AddAttributeDialogResult and Multiplicity Result
			/*return new AddAttributeDialogResult(
					classId,
					nameTextField.getText(), 
					levelComboBox.getSelectionModel().getSelectedItem(),
					typeComboBox.getSelectionModel().getSelectedItem(), multiplicityResult);*/
		}
		
		
	}

	private void setResultAddMetaClass(ButtonType dlgBtn) {
		// TODO Auto-generated method stub
		
	}

	private void layoutContent() {
		switch (type) {
			case Class:
				addMetaClass();
				break;
			case Attribute:
				generateLayoutAddAttribute();
				break;
			case Operation:
				addOperation();
				break;
			case Association:
				addAssociation();
				break;
			default:
				System.err.println("AddDialog: No matching content type!");	
		}

	}
	
	//generate Layout Content

	private void addMetaClass() {
		// TODO Auto-generated method stub
		
	}
	
	private void addAssociation() {
			// TODO Auto-generated method stub
			
	}

	private void addOperation() {
			// TODO Auto-generated method stub
			
	}

	private void generateLayoutAddAttribute() {
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
			classTextField = new TextField();
			classTextField.setText(object.getName());
			classTextField.setDisable(true);
			levelComboBox = new ComboBox<>(LevelList.levelList);
			typeComboBox = new ComboBox<>(typeList);
			multiplicityButton = new Button();
			//multiplicityButton.setText("Add / Edit Multiplicity");
			multiplicityButton.setText(multiplicity.getClass().getSimpleName().toString());
			multiplicityButton.setOnAction(e -> {
				new MultiplicityDialog(multiplicity).showAndWait(); 
				/*if successful multiplicity = result;
				 *multiplicityButton.setText(multiplicity.toString());*/ });
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
		
		switch (type) {
		case Class:
			return validateAddMetaClass();
		case Attribute:
			return validateAddAttribute();
		case Operation:
			return validateAddOperation();
		case Association:
			return validateAddAssociation();
		default:
			System.err.println("AddDialog: No matching content type!");	
		}
		return false;
	}
	
	//Validate user Input to each Add Dialog

	private boolean validateAddAssociation() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean validateAddOperation() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean validateAddAttribute() {
		Label errorLabel = getErrorLabel();
		
		String name = nameTextField.getText();
		
		if (isNullOrEmpty(name)) {
			errorLabel.setText("Enter valid name!");
			return false;
		} else if (nameAlreadyUsed()) {
			errorLabel.setText("Name already used");
			return false;
		}
		errorLabel.setText("");
		if (levelComboBox.getSelectionModel().getSelectedIndex()==-1) {
			errorLabel.setText("Select Level!");
			return false;
		}
		errorLabel.setText("");
		if (typeComboBox.getSelectionModel().getSelectedIndex()==-1) {
			errorLabel.setText("Select Type!");
			return false;
		}
		errorLabel.setText("");
		
		return true;
		
	}
	
	private boolean nameAlreadyUsed() {
		for (FmmlxAttribute attribute : object.getOwnAttributes()) {
			if(nameTextField.getText().equals(attribute.getName())) return true; 
		}
		for (FmmlxAttribute attribute : object.getOtherAttributes()) {
			if(nameTextField.getText().equals(attribute.getName())) return true; 

		}
		return false;
	}
	
	

	private boolean validateAddMetaClass() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	
}