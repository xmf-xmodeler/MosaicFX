package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import tool.clients.fmmlxdiagrams.dialogs.results.AddEnumElementDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.EditEnumerationDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.MultiplicityDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;
import tool.clients.fmmlxdiagrams.EnumElement;
import tool.clients.fmmlxdiagrams.FmmlxEnum;

public class EditEnumerationDialog extends CustomDialog<EditEnumerationDialogResult>{
	
	private Label chooseEnumLabel;
	private Label newNameLabel;
	private Label inputElementLabel;
	private Label inputEnumNameLabel;
	private Label enumNameLabel;
	
	private ComboBox<FmmlxEnum> chooseEnumComboBox;
	private TextField newNameTextField;
	private TextField enumNameTextField;
	private ListView<String> inputElementListview;

	private Button addElementButton;
	private Button removeElementButton;
	private Vector<EnumElement> enumElement;
	
	private SplitPane splitDoalog;
	private TabPane changeNameTabPane;
	private String type;
	private String enumName;
	
	public EditEnumerationDialog(String string, String enumName) {
		super();
		this.type = string;
		this.enumName = enumName;
		
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		if (string.equals("edit_element")) {
			dialogPane.setHeaderText("Edit Enumeration");
		} else {
			dialogPane.setHeaderText("Input Enum-Element");
		}

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
					return new EditEnumerationDialogResult(chooseEnumComboBox.getSelectionModel().getSelectedItem().getName(), new FmmlxEnum(newName, new Vector<>()));
				}
			}
			return null;
		});
	}

	private boolean validateUserInput() {
		
		System.out.println(type);
		
		if(type.equals("edit_element")) {
			if(chooseEnumComboBox.getSelectionModel().getSelectedItem()==null) {
				errorLabel.setText(StringValueDialog.ErrorMessage.selectEnumeration);
				return false;
			}
		} else {
			if(newNameTextField.getText().length()>0) {
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
		enumNameLabel = new Label ("Enum Name");
		inputElementLabel = new Label("Elements");
		
		newNameTextField = new TextField();
		newNameTextField.isEditable();
		enumNameTextField = new TextField();
		
		inputElementListview = initializeListView(0);
		inputElementListview.setEditable(true);
		inputElementListview.setCellFactory(TextFieldListCell.forListView());
		chooseEnumComboBox = (ComboBox<FmmlxEnum>) initializeComboBoxEnum(getEnumList());
		chooseEnumComboBox.valueProperty().addListener((observable, oldValue, newValue1) -> {
			if (newValue1 != null) {
				newNameTextField.setText(newValue1.getName());
				for(String tmp: chooseEnumComboBox.getSelectionModel().getSelectedItem().getElements()) {
					inputElementListview.getItems().add(tmp);
				}
			}
		});
		
		addElementButton = new Button("Add Element");
		removeElementButton = new Button("Remove Element");
		
		splitDoalog = new SplitPane();
		splitDoalog.setOrientation(Orientation.HORIZONTAL);
		splitDoalog.setDividerPosition(0, 0.6);
		
		List<Node> labelNode = new ArrayList<Node>(); 
		List<Node> editorNode = new ArrayList<Node>();
		
		if (type.equals("edit_element")){
			labelNode.add(chooseEnumLabel);
			labelNode.add(newNameLabel);
			labelNode.add(inputElementLabel);
			
			editorNode.add(chooseEnumComboBox);
			editorNode.add(newNameTextField);
			
		} else {
			enumNameTextField.setText(enumName);
			labelNode.add(enumNameLabel);
			labelNode.add(inputElementLabel);
			
			editorNode.add(enumNameTextField);
		}
		
		  	
		editorNode.add(inputElementListview);
		editorNode.add(createAddAndRemoveButton(addElementButton,
		removeElementButton));
		  
		addElementButton.setOnAction(e -> addElement(inputElementListview));
		removeElementButton.setOnAction(e ->removeElement(inputElementListview.getSelectionModel().getSelectedItems()));
		  
		  
		addNodesToGrid(labelNode,0); addNodesToGrid(editorNode, 1);
		 
		
		grid.add(splitDoalog, 0, 0);
	} 

	private ObservableList<FmmlxEnum> getEnumList() {
		
		
		// TODO Auto-generated method stub
		
		
		return null;
	}
	
	private void removeElement(ObservableList<String> observableList) {
		
		if (type.equals("edit_element")) {
			if(chooseEnumComboBox.getSelectionModel().getSelectedItem()!=null) {
				inputElementListview.getItems().removeAll(observableList);
			} else {
				errorLabel.setText(StringValueDialog.ErrorMessage.selectEnumeration);
			}
		} else {
			inputElementListview.getItems().removeAll(observableList);
		}
		
	}

	private void addElement(ListView<String> list) {
		AddEnumElement dlg = new AddEnumElement();
		Optional<AddEnumElementDialogResult> opt = dlg.showAndWait();

		if (opt.isPresent()) {
			AddEnumElementDialogResult result = opt.get();

			list.getItems().add(result.getName());
		}
		
		
		/*
		 * int elementNumber=inputElementListview.getItems().size()+1;
		 * if(type.equals("edit_element")) {
		 * if(chooseEnumComboBox.getSelectionModel().getSelectedItem()!=null) {
		 * inputElementListview.getItems().add("Element"+elementNumber); } else {
		 * errorLabel.setText(StringValueDialog.ErrorMessage.selectEnumeration); } }
		 * else { inputElementListview.getItems().add("Element"+elementNumber); }
		 */
		
		
	}

}
