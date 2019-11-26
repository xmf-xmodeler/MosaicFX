package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.converter.IntegerStringConverter;
import tool.clients.fmmlxdiagrams.FmmlxEnum;
import tool.clients.fmmlxdiagrams.EnumElement;
import tool.clients.fmmlxdiagrams.dialogs.results.AddEnumerationDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;
import javafx.scene.Node;

public class AddEnumerationDialog extends CustomDialog<AddEnumerationDialogResult>{
	
	private Label nameLabel;
	private Label numberOfElementLabel;
	private Label inputElementLabel;
	
	private TextField nameTextField;
	private ComboBox<Integer> numberOfElements;
	private ListView<String> inputElementListview;
	
	private Button addElementButton;
	private Button removeElementButton;
	

	public AddEnumerationDialog() {
		super();
		
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText("Create Enumeration");

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
				Vector<EnumElement> elements = new Vector<EnumElement>();
				
				for(String tmp : inputElementListview.getItems()) {
					elements.add(new EnumElement(tmp));
				}
				
				return new AddEnumerationDialogResult(new FmmlxEnum(nameTextField.getText(), new Vector<>()));
			}
			return null;
		});
		
	}

	private boolean validateUserInput() {
		if (!validateName()) {
			return false;
		} else if (numberOfElements.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.inputNumberOfElement);
			return false;
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
		String name = nameTextField.getText();

		if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.enterValidName);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private void addElementToGrid() {
		nameLabel = new Label("Name");
		numberOfElementLabel = new Label("Number of Elements");
		inputElementLabel = new Label("Input Element");
		
		nameTextField = new TextField();
		nameTextField.isEditable();
		numberOfElements = new ComboBox<Integer>(LevelList.levelList);
		numberOfElements.setConverter(new IntegerStringConverter());
		numberOfElements.setEditable(true);
		inputElementListview = initializeListView(0);
		inputElementListview.setEditable(true);
		inputElementListview.setCellFactory(TextFieldListCell.forListView());
		inputElementListview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		addElementButton = new Button("Add Element");
		removeElementButton = new Button("Remove Element");
		
		
		List<Node> labelNode = new ArrayList<Node>();
		List<Node> editorNode = new ArrayList<Node>();
		
		numberOfElements.valueProperty().addListener((observable, oldValue, newValue1) -> {
			if (newValue1 != null) {
				int newValue = newValue1.intValue();
				if (inputElementListview.getItems().size()==0) {
					for(int i=inputElementListview.getItems().size(); i<newValue;i++) {
						int elementNumber= i+1;
						inputElementListview.getItems().add("Element"+elementNumber);
					}	
				} else {
					if(newValue==inputElementListview.getItems().size()) {
						
					} else {
						if (newValue<inputElementListview.getItems().size()) {
							for(int i=inputElementListview.getItems().size(); i>newValue; i--) {
								inputElementListview.getItems().remove(i-1);
							}
						}else {
							for(int i=inputElementListview.getItems().size(); i<newValue;i++) {
								int elementNumber= i+1;
								inputElementListview.getItems().add("Element"+elementNumber);
							}				
						}
					}
				}
			}
		});
		
		addElementButton.setOnAction(e -> addElement());
		removeElementButton.setOnAction(e -> removeElement(inputElementListview.getSelectionModel().getSelectedItems()));

		labelNode.add(nameLabel);
		labelNode.add(numberOfElementLabel);
		labelNode.add(inputElementLabel);
		
		editorNode.add(nameTextField);
		editorNode.add(numberOfElements);
		editorNode.add(inputElementListview);
		editorNode.add(createAddAndRemoveButton(addElementButton, removeElementButton));
		
		addNodesToGrid(labelNode,0);
		addNodesToGrid(editorNode, 1);
	}

	private void removeElement(ObservableList<String> observableList) {
		inputElementListview.getItems().removeAll(observableList);
		numberOfElements.setValue(inputElementListview.getItems().size());
	}

	private void addElement() {
		int elementNumber=inputElementListview.getItems().size()+1;
		inputElementListview.getItems().add("Element"+elementNumber);
	}

}
