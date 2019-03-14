package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.Vector;

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
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.AddInstanceDialogResult;

public class AddInstanceDialog extends CustomDialog<AddInstanceDialogResult> {

	private TextField nameTextField;
	private ComboBox<Integer> levelComboBox;
	private ListView<String> parentListView;
	private ComboBox<String> ofComboBox;
	private CheckBox abstractCheckBox;
	
	ObservableList<String> parentList= getAllParentList();
	ObservableList<String> ofList = getAllOfList();

	public AddInstanceDialog(String of) {
		super();

		DialogPane dialog = getDialogPane();
		dialog.setHeaderText("Add Instance");
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
		ofComboBox = new ComboBox<>(ofList);
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


	private ObservableList<String> getAllOfList() {
		ArrayList<String> resultStrings = new ArrayList<String>();
		Vector<FmmlxDiagram> diagrams = FmmlxDiagramCommunicator.getDiagrams();
		Vector<FmmlxObject> objects = diagrams.get(0).getObjects();
		
		for (FmmlxObject object :objects) {
			resultStrings.add(object.getName());
		}
		ObservableList<String> result = FXCollections.observableArrayList( resultStrings);
		return result;
	}

	private ObservableList<String> getAllParentList() {
		ArrayList<String> resultStrings = new ArrayList<String>();
		Vector<FmmlxDiagram> diagrams = FmmlxDiagramCommunicator.getDiagrams();
		Vector<FmmlxObject> objects = diagrams.get(0).getObjects();
		
		for (FmmlxObject object :objects) {
			resultStrings.add(object.getName());
		}
		ObservableList<String> result = FXCollections.observableArrayList( resultStrings);
		return result;
	}

	
	private boolean validateUserInput() {
		if (!validateName()) {
			return false;
		}
		if (!ofSelected()) {
			return false;
		}
		if (!validateLevel()) {
			return false;
		}
		if (!validateCircularDependecies()) {
			return false;
		}
		if (!validateName()) {
			return false;
		}
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
		}else {
			errorLabel.setText("");
			return true;
		}
	}
	
	private boolean nameAlreadyUsed() {
		Vector<FmmlxDiagram> diagrams = FmmlxDiagramCommunicator.getDiagrams();
		Vector<FmmlxObject> objects = diagrams.get(0).getObjects();
		
		for (FmmlxObject object :objects) {
			if(nameTextField.getText().equals(object.getName())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean ofSelected() {
		Label errorLabel = getErrorLabel();
		
		if (ofComboBox.getSelectionModel().getSelectedIndex()==-1) {
			errorLabel.setText("Select Of!");
			return false;
		}
		errorLabel.setText("");
		return true;
	}
	
	private boolean validateLevel() {
		Label errorLabel = getErrorLabel();
		
		Vector<FmmlxDiagram> diagrams = FmmlxDiagramCommunicator.getDiagrams();
		Vector<FmmlxObject> objects = diagrams.get(0).getObjects();
		
		//-----------------------------------------------------------
		
		for(FmmlxObject object : objects) {
			System.out.println("class name : "+object.getName() );
			System.out.println("level :"+object.getLevel() );
			System.out.println("-------------------------");
		}
		//-----------------------------------------------------------
		
		if (levelComboBox.getSelectionModel().isEmpty()) {
			errorLabel.setText("Select Level!");
			return false;
		}else if(levelIsNotValid(1, 1)) { //TODO just for test
			errorLabel.setText("Selected Level is not allowed");
			return false;
		}else {
			errorLabel.setText("");
			return true;
		}
	}

	private boolean levelIsNotValid(int parentLevel, int choosenLevel) {
		//TODO
		if(parentLevel-1!=choosenLevel) {
			return true;
		}
		return false;
	}
	
	private boolean validateCircularDependecies() {
		// TODO Auto-generated method stub
		
		return false;
	}

	private void initializeListView() {
		parentListView = new ListView<>(parentList);
		parentListView.setPrefHeight(75);
		parentListView.setPrefWidth(COLUMN_WIDTH);
		parentListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}
}
