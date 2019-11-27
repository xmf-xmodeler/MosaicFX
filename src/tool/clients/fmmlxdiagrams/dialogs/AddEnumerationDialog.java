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
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import tool.clients.fmmlxdiagrams.FmmlxEnum;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.EnumElement;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.dialogs.results.AddEnumerationDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;
import javafx.scene.Node;

public class AddEnumerationDialog extends CustomDialog<AddEnumerationDialogResult>{
	
	private FmmlxDiagram diagram;
	private DiagramActions actions;
	
	private Label nameLabel;
	private Label inputElementLabel;
	
	private TextField nameTextField;
	private ListView<String> inputElementListview;
	
	private ButtonType create;
	
	private Button addElementButton;
	private Button removeElementButton;
	

	public AddEnumerationDialog(FmmlxDiagram diagram) {
		super();
		this.diagram = diagram;
		
		actions = new DiagramActions(diagram);
		
		
		DialogPane dialogPane = getDialogPane();

		
		
		dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
		dialogPane.setHeaderText("Create Enumeration");

		addElementToGrid();

		dialogPane.setContent(flow);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		
		
		 okButton.addEventFilter(ActionEvent.ACTION, e -> {
			 if (!validateUserInput())
			 	{ e.consume(); 
			 } 
		});
		 

		setResult();
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				Vector<EnumElement> elements = new Vector<EnumElement>();		
				return new AddEnumerationDialogResult(new FmmlxEnum(nameTextField.getText(),elements));
			}
			return null;
		});
		
	}

	private boolean validateUserInput() {
		if (!validateName()) {
			
			return false;
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
		inputElementLabel = new Label("Input Element");
		
		nameTextField = new TextField();
		nameTextField.isEditable();
		
		
		/*
		 * inputElementListview = initializeListView(0);
		 * inputElementListview.setEditable(true);
		 * inputElementListview.setCellFactory(TextFieldListCell.forListView());
		 * inputElementListview.getSelectionModel().setSelectionMode(SelectionMode.
		 * MULTIPLE);
		 * 
		 * addElementButton = new Button("Add Element"); removeElementButton = new
		 * Button("Remove Element");
		 */
		
		
		List<Node> labelNode = new ArrayList<Node>();
		List<Node> editorNode = new ArrayList<Node>();
		
		
		/*
		 * addElementButton.setOnAction(e -> addElement());
		 * removeElementButton.setOnAction(e ->
		 * removeElement(inputElementListview.getSelectionModel().getSelectedItems()));
		 */

		labelNode.add(nameLabel);
		
		//labelNode.add(inputElementLabel);
		
		editorNode.add(nameTextField);
		
		/*
		 * editorNode.add(inputElementListview);
		 * editorNode.add(createAddAndRemoveButton(addElementButton,
		 * removeElementButton));
		 */
		
		addNodesToGrid(labelNode,0);
		addNodesToGrid(editorNode, 1);
	}

	private void removeElement(ObservableList<String> observableList) {
		inputElementListview.getItems().removeAll(observableList);;
	}

	private void addElement() {
		int elementNumber=inputElementListview.getItems().size()+1;
		inputElementListview.getItems().add("Element"+elementNumber);
	}

}
