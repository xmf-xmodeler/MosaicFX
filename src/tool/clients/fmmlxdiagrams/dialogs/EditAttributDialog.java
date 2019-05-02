package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
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
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.MetaClassDialogResult;

public class EditAttributDialog extends CustomDialog<MetaClassDialogResult> {

	
	private Label classLabel;
	private Label selectAttributLabel;
	private Label currentValueLabel;
	private Label newNameLabel;
	private Label newValueLabel;
	private ObservableList<String> classList;
	private ObservableList<String> attributeList;
	private Vector<FmmlxAttribute> attributes;
	private ComboBox<String> classComboBox;
	private ComboBox<String> selectAttributeComboBox;
	private Label currentValue;
	private TextField newNameTextField;
	private TextField newValueTextField;
	private Vector<FmmlxObject> objects;
	
	
	public EditAttributDialog(final FmmlxDiagram diagram) {
		super();
		
		DialogPane dialogPane = getDialogPane();
		dialogPane.setHeaderText("Edit Attribute");
		
		objects = diagram.getObjects();
		
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		addElementToGrid();
		dialogPane.setContent(flow);
		
		
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
	
	private ObservableList<String> getClassAttributeList() {
		// TODO Auto-generated method stub
		if (classComboBox.getSelectionModel().getSelectedIndex() == -1) {
			for (FmmlxObject object : objects) {
				if (classComboBox.getSelectionModel().getSelectedItem().equals(object.getName())) {
					attributeList = object.getAttributes();
				}
			}
			
		}
		
		return null;
	}

	private boolean validateUserInput() {
		// TODO Auto-generated method stub
		return false;
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

	private void addElementToGrid() {
		classList = getAllClassList();
		//attributeList = getClassAttributeList();
		classLabel = new Label("Select Class");
		selectAttributLabel = new Label("Select Attribute");
		currentValueLabel = new Label("Value");
		newNameLabel = new Label("Set new name");
		newValueLabel = new Label("new Value");
		
		classComboBox = new ComboBox<>(classList);
		selectAttributeComboBox = new ComboBox<>(attributeList);
		currentValue = new Label(getCurrentValue());
		newNameTextField = new TextField();
		newValueTextField = new TextField();
		
		classComboBox.setPrefWidth(COLUMN_WIDTH);
		selectAttributeComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(classLabel, 0, 0);
		grid.add(classComboBox, 1, 0);
		grid.add(selectAttributLabel, 0, 1);
		grid.add(selectAttributeComboBox, 1, 1);
		grid.add(currentValueLabel, 0, 2);
		grid.add(currentValue, 1, 2);
		grid.add(newNameLabel, 0, 3);
		grid.add(newNameTextField, 1, 3);
		grid.add(newValueLabel, 0, 4);
		grid.add(newValueTextField, 1, 4);
	}


	


	private String getCurrentValue() {
		return "Current Value";
	}
	
	

}
