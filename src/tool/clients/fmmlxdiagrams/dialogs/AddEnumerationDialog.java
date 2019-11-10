package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.converter.IntegerStringConverter;
import tool.clients.fmmlxdiagrams.EnumElement;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.dialogs.results.AddEnumerationDialogResult;
import javafx.scene.Node;

public class AddEnumerationDialog extends CustomDialog<AddEnumerationDialogResult>{
	
	private FmmlxDiagram diagram;
	private Label nameLabel;
	private Label numberOfElementLabel;
	private Label inputElementLabel;
	
	private TextField nameTextField;
	private ComboBox<Integer> numberOfElements;
	private ListView<EnumElement> inputElementListview;
	

	public AddEnumerationDialog(FmmlxDiagram diagram) {
		super();
		
		this.diagram=diagram;

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
		// TODO Auto-generated method stub
		
	}

	private boolean validateUserInput() {
		// TODO Auto-generated method stub
		return false;
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
		
		List<Node> labelNode = new ArrayList<Node>();
		List<Node> editorNode = new ArrayList<Node>();

		labelNode.add(nameLabel);
		labelNode.add(numberOfElementLabel);
		
		editorNode.add(nameTextField);
		editorNode.add(numberOfElements);
		
		addNodesToGrid(labelNode,0);
		addNodesToGrid(editorNode, 1);
		
		// TODO Auto-generated method stub
		
	}

}
