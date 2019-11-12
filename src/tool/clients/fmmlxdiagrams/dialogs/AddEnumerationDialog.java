package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.converter.IntegerStringConverter;
import tool.clients.fmmlxdiagrams.Enum;
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
				
				return new AddEnumerationDialogResult(new Enum(nameTextField.getText(), elements));
			}
			return null;
		});
		
	}

	private boolean validateUserInput() {
		if (!validateName()) {
			return false;
		} if (numberOfElements.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.inputNumberOfElement);
			return false;
		}
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
		
		List<Node> labelNode = new ArrayList<Node>();
		List<Node> editorNode = new ArrayList<Node>();
		
		numberOfElements.valueProperty().addListener((observable, oldValue, newValue1) -> {
			if (newValue1 != null) {
				int newValue = newValue1.intValue();
				ListView<String> newInput = initializeListView(newValue);
				newInput.setEditable(true);
				newInput.setCellFactory(TextFieldListCell.forListView());
						
				updateNodeInsideGrid(inputElementLabel, new Label("Input Element"), 0, 2);
				updateNodeInsideGrid(inputElementListview, newInput, 1, 2);
			}
		});

		labelNode.add(nameLabel);
		labelNode.add(numberOfElementLabel);
		labelNode.add(inputElementLabel);
		
		editorNode.add(nameTextField);
		editorNode.add(numberOfElements);
		editorNode.add(inputElementListview);
		
		addNodesToGrid(labelNode,0);
		addNodesToGrid(editorNode, 1);
	}

}
