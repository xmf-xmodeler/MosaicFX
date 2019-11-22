package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import tool.clients.fmmlxdiagrams.dialogs.results.EditEnumerationDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;
import tool.clients.fmmlxdiagrams.EnumElement;
import tool.clients.fmmlxdiagrams.FmmlxEnum;

public class EditEnumerationDialog extends CustomDialog<EditEnumerationDialogResult>{
	
	private Label chooseEnumLabel;
	private Label newNameLabel;
	private Label inputElementLabel;
	
	private ComboBox<FmmlxEnum> chooseEnumComboBox;
	private TextField newNameTextField;
	private ListView<String> inputElementListview;

	private Button addElementButton;
	private Button removeElementButton;
	private Vector<EnumElement> enumElement;
	public EditEnumerationDialog() {
		super();
		
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText("Edit Enumeration");

		addElementToGrid();

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
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				String newName;
				enumElement = null;
				
				for (String tmp : inputElementListview.getItems()) {
					enumElement.add(new EnumElement(tmp));
				}
				if(chooseEnumComboBox.getSelectionModel().getSelectedItem()!=null) {
					if (newNameTextField.getText()!=chooseEnumComboBox.getSelectionModel().getSelectedItem().getName()||newNameTextField.getText().length()>0) {
						newName=newNameTextField.getText();
						
					} else {
						newName = chooseEnumComboBox.getSelectionModel().getSelectedItem().getName();
					}
					return new EditEnumerationDialogResult(chooseEnumComboBox.getSelectionModel().getSelectedItem().getName(), new FmmlxEnum(newName, enumElement));
				}
			}
			return null;
		});
	}

	private boolean validateUserInput() {
		
		if(chooseEnumComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectEnumeration);
			return false;
		}
		else if(newNameTextField.getText().length()>0) {
			if (!validateName()) {
				return false;
			} 
		} 
		
		Set<String> set = new HashSet<String>(inputElementListview.getItems());
		if(set.size() < inputElementListview.getItems().size()){
			errorLabel.setText(StringValueDialog.ErrorMessage.thereAreDuplicates);
			return false;
		}
		
		for (String tmp : inputElementListview.getItems()) {
			if (!InputChecker.getInstance().validateName(tmp)) {
				errorLabel.setText("\""+tmp+"\""+ " is not valid name for enumeration's element");
				return false;
			} 
		}
		
		errorLabel.setText("");
		return true;
	}

	private boolean validateName() {
		String name = newNameTextField.getText();

		if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.enterValidName);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}


	private void addElementToGrid() {
		chooseEnumLabel = new Label("Choose Enumeration");
		newNameLabel = new Label("New Name");
		inputElementLabel = new Label("Elements");
		
		newNameTextField = new TextField();
		newNameTextField.isEditable();
		inputElementListview = initializeListView(0);
		inputElementListview.setEditable(true);
		inputElementListview.setCellFactory(TextFieldListCell.forListView());
		chooseEnumComboBox = (ComboBox<FmmlxEnum>) initializeComboBoxEnum(getEnumList());
		chooseEnumComboBox.valueProperty().addListener((observable, oldValue, newValue1) -> {
			if (newValue1 != null) {
				newNameTextField.setText(newValue1.getName());
				for(EnumElement tmp: chooseEnumComboBox.getSelectionModel().getSelectedItem().getElements()) {
					inputElementListview.getItems().add(tmp.getName());
				}
			}
		});
		
		addElementButton = new Button("Add Element");
		removeElementButton = new Button("Remove Element");
		
		List<Node> labelNode = new ArrayList<Node>();
		List<Node> editorNode = new ArrayList<Node>();
		

		labelNode.add(chooseEnumLabel);
		labelNode.add(newNameLabel);
		labelNode.add(inputElementLabel);
		
		editorNode.add(chooseEnumComboBox);
		editorNode.add(newNameTextField);
		editorNode.add(inputElementListview);
		editorNode.add(createAddAndRemoveButton(addElementButton, removeElementButton));
		
		addElementButton.setOnAction(e -> addElement());
		removeElementButton.setOnAction(e -> removeElement(inputElementListview.getSelectionModel().getSelectedItems()));

		
		addNodesToGrid(labelNode,0);
		addNodesToGrid(editorNode, 1);
	} 

	private ObservableList<FmmlxEnum> getEnumList() {
		
		
		// TODO Auto-generated method stub
		
		
		return null;
	}
	
	private void removeElement(ObservableList<String> observableList) {
		if(chooseEnumComboBox.getSelectionModel().getSelectedItem()!=null) {
			inputElementListview.getItems().removeAll(observableList);
		} else {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectEnumeration);
		}
	}

	private void addElement() {
		if(chooseEnumComboBox.getSelectionModel().getSelectedItem()!=null) {
			int elementNumber=inputElementListview.getItems().size()+1;
			inputElementListview.getItems().add("Element"+elementNumber);
		} else {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectEnumeration);
		}
		
	}

}
