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

	private FmmlxDiagram diagram;
	
	private TextField nameTextField;
	private ListView<String> parentListView;
	private ComboBox<String> ofComboBox;
	private CheckBox abstractCheckBox;
	private Label abstractLabel;
	private Vector<FmmlxDiagram> diagrams;
	private Vector<FmmlxObject> objects;
	
	ObservableList<String> parentList;
	ObservableList<String> ofList;

	public AddInstanceDialog(final FmmlxDiagram diagram, Integer ofId) {
		super();
		
		this.diagram = diagram;

		DialogPane dialog = getDialogPane();
		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		layoutContent();
		dialog.setContent(flow);
		

		Vector<FmmlxObject> objects = diagram.getObjects();

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});
		
		setResultConverter(dlgBtn -> {
			int level=0;
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				int idSelectedItem = 0;
				for (FmmlxObject object : objects) {
					if (object.getName().equals(ofComboBox.getSelectionModel().getSelectedItem())) {
						idSelectedItem = object.getId();
						level=object.getLevel()-1;
					}
				}
				System.out.println(level+ " level instance");
				return new AddInstanceDialogResult(nameTextField.getText(),
						level, parentListView.getSelectionModel().getSelectedItems(),
						idSelectedItem, abstractCheckBox.isSelected());
			}
			return null;
		});
	}
	
	private void layoutContent() {
		diagrams = FmmlxDiagramCommunicator.getDiagrams();
		objects = diagrams.get(0).getObjects();
		ofList = getAllOfList();
		parentList = getAllParentList();
		nameTextField = new TextField();
		ofComboBox = new ComboBox<>(ofList);
		abstractCheckBox = new CheckBox();
		
		abstractLabel = new Label("Abstract");

		initializeListView();

		ofComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(new Label("Name"), 0, 0);
		grid.add(nameTextField, 1, 0);
		grid.add(new Label("Of"), 0, 1);
		grid.add(ofComboBox, 1, 1);
		grid.add(abstractLabel, 0, 3);
		grid.add(abstractCheckBox, 1, 3);
		grid.add(new Label("Parent"), 0, 4);
		grid.add(parentListView, 1, 4);
	}


	private ObservableList<String> getAllOfList() {
		ArrayList<String> resultStrings = new ArrayList<String>();

		Vector<FmmlxObject> objects = diagram.getObjects();
		
		for (FmmlxObject object :objects) {
			if (object.getLevel()!=0) {
				resultStrings.add(object.getName());
			}
		}
		
		ObservableList<String> result = FXCollections.observableArrayList( resultStrings);
		return result;
	}

	private ObservableList<String> getAllParentList() {
		ArrayList<String> resultStrings = new ArrayList<String>();

		Vector<FmmlxObject> objects = diagram.getObjects();
		
		for (FmmlxObject object :objects) {
			if (object.getLevel()!=0) {
				resultStrings.add(object.getName());
			}
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
		if (!validateCircularDependecies()) {
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

		Vector<FmmlxObject> objects = diagram.getObjects();
		
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


	
	private boolean validateCircularDependecies() {
		// TODO Auto-generated method stub
		return true;
	}

	private void initializeListView() {
		parentListView = new ListView<>(parentList);
		parentListView.setPrefHeight(75);
		parentListView.setPrefWidth(COLUMN_WIDTH);
		parentListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}
}
