package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.converter.IntegerStringConverter;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.InstanceGeneratorDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue.LabelAndHeaderTitle;

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
	
	//private Vector<ValueGenerator> currentGenerator =  new Vector<ValueGenerator>();

	public InstanceGeneratorDialog(FmmlxObject object) {

		this.object= object;
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText("Instance Generator");
		layoutContent();
		dialogPane.setContent(flow);
		setValidation();
		setResult();
	}

	private void setResult() {
		// TODO Auto-generated method stub
		
	}

	private void setValidation() {
		// TODO Auto-generated method stub
		
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
		
		labelNode = new ArrayList<Node>();
		typeLabelNode = new ArrayList<Node>();
		inputNode = new ArrayList<Node>();
		editButtonNode = new ArrayList<Node>();
		
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
