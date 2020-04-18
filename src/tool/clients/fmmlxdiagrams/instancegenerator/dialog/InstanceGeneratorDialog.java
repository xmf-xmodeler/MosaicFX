package tool.clients.fmmlxdiagrams.instancegenerator.dialog;

import java.awt.*;
import java.util.*;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.converter.IntegerStringConverter;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.InstanceGeneratorDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue.LabelAndHeaderTitle;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGenerator;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGeneratorIncrement;

public class InstanceGeneratorDialog extends CustomDialog<InstanceGeneratorDialogResult>{


	private FmmlxObject object;
	private Label ofLabel ;
	private Label numberOfElementLabel;
	private TextField ofTextField;
	private ComboBox<Integer> numberOfElementComboBox;
	private List<Node> labelNode;
	private List<Node> typeLabelNode;
	private List<Node> inputNode;
	private List<Node> editButtonNode;
	private DialogPane dialogPane;
	
	private HashMap<FmmlxAttribute, ValueGenerator> value;

	public InstanceGeneratorDialog(FmmlxObject object) {
		this.object= object;
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText("Instance Generator");
		layoutContent();
		dialogPane.setContent(flow);
		
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {
				e.consume();
			}
		});
		setResult();
	}


	private void setResult() {
		setResultConverter(dlgBtn -> {		
		if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
			this.value = new HashMap<>();
			int counter = 3;
			for(FmmlxAttribute att : object.getAllAttributes()) {
				Node node = inputNode.get(counter);
				if(node instanceof ComboBox) {
					((ComboBox<ValueGenerator>) node).getSelectionModel().getSelectedItem().generate(numberOfElementComboBox.getSelectionModel().getSelectedItem());
					value.put(att, ((ComboBox<ValueGenerator>) node).getSelectionModel().getSelectedItem());
				} else {
					value.put(att, null);
				}
				counter++;
			}
			return new InstanceGeneratorDialogResult(object, numberOfElementComboBox.getSelectionModel().getSelectedItem(), value);
		}
		return null;
	});	
	}

	private boolean inputIsValid() {
		return checkComboBoxesAndLogic();
	}

	public HashMap<FmmlxAttribute, ValueGenerator> getValue() {
		return value;
	}

	private boolean checkLogic(int generatedInstance) {
		int counter = 3;
		for(FmmlxAttribute att : object.getAllAttributes()) {
			Node node = inputNode.get(counter);
			if(node instanceof ComboBox) {
				if(((ComboBox<ValueGenerator>) node).getSelectionModel().getSelectedItem().getValueGeneratorName().equals("INCREMENT")){
					((ComboBox<ValueGenerator>) node).getSelectionModel().getSelectedItem().generate(numberOfElementComboBox.getSelectionModel().getSelectedItem());
					int possible = ((ComboBox<ValueGenerator>) node).getSelectionModel().getSelectedItem().possibleGeneratedInstance();
					if(possible<generatedInstance){
						errorLabel.setText(att.getName()+ "  : Maximum number of generated instance is " +possible );
						return false;
					}
				}
			}
			counter++;
		}

		errorLabel.setText("");
		return true;
	}


	private boolean checkComboBoxesAndLogic() {
		if (numberOfElementComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText("Please select number of Instance");
			return false;
		}
		for(Node node : inputNode) {
			if (node instanceof ComboBox) {
				if (((ComboBox<ValueGenerator>) node).getSelectionModel().getSelectedItem() == null ) {
					errorLabel.setText("Please input all required values");
					return false;
				} else if (!checkLogic(numberOfElementComboBox.getSelectionModel().getSelectedItem())){
					return false;
				}
			}
		}
		return true;
	}

	private void layoutContent() {
		ofLabel = new Label(LabelAndHeaderTitle.of);
		numberOfElementLabel = new Label(LabelAndHeaderTitle.numberOfInstances);
		
		ofTextField = new TextField();
		ofTextField.setText(object.getName());
		ofTextField.setEditable(false);
		numberOfElementComboBox = new ComboBox<>(AllValueList.generateLevelListToThreshold(1, 5));
		numberOfElementComboBox.setConverter(new IntegerStringConverter());
		numberOfElementComboBox.setEditable(true);
		
		labelNode = new ArrayList<>();
		typeLabelNode = new ArrayList<>();
		inputNode = new ArrayList<>();
		editButtonNode = new ArrayList<>();
		
		labelNode.add(ofLabel);
		labelNode.add(numberOfElementLabel);
		labelNode.add(new Label());
		
		typeLabelNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		typeLabelNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		typeLabelNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		
		inputNode.add(ofTextField);
		inputNode.add(numberOfElementComboBox);
		inputNode.add(new Label());
		
		editButtonNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		editButtonNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		editButtonNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		
		for (FmmlxAttribute att : object.getAllAttributes()) {			
			labelNode.add(new Label(att.getName()));	
			typeLabelNode.add(new Label(": "+att.getType()));
			
			if(AllValueList.traditionalTypeList.contains(att.getType())) {
				ComboBox<ValueGenerator> comboBox = initializeComboBoxGeneratorList(att);
				inputNode.add(comboBox);
				Button button = new InstanceGeneratorEditButton(StringValue.LabelAndHeaderTitle.EDIT, att);
				button.setOnAction(e -> comboBox.getSelectionModel().getSelectedItem().openDialog());
				editButtonNode.add(button);
			} else {
				inputNode.add(new Label(" "));
				editButtonNode.add(new Label(" "));
			}
		}
		
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(typeLabelNode,1);
		addNodesToGrid(inputNode,2);
		addNodesToGrid(editButtonNode, 3);
	}
	
}
