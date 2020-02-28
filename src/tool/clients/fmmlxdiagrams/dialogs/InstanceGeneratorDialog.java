package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.InstanceGeneratorDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValue;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValue.LabelAndHeaderTitle;

public class InstanceGeneratorDialog extends CustomDialog<InstanceGeneratorDialogResult>{

	
	@SuppressWarnings("unused")
	private FmmlxDiagram diagram;
	private FmmlxObject object;
	private Label ofLabel ;
	private Label numberOfElementLabel;
	private TextField ofTextField;
	private ComboBox<Integer> numberOfElementComboBox;
	private List<Node> labelNode;
	private List<Node> typeLabel;
	private List<Node> inputNode;
	private List<Node> editNode;
	private List<Node> editButtonNode;
	private DialogPane dialogPane;
	
	

	public InstanceGeneratorDialog(FmmlxDiagram diagram, FmmlxObject object) {
		this.diagram= diagram;
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
		numberOfElementComboBox = new ComboBox<>(LevelList.generateLevelListToThreshold(1, 5));
		
		labelNode = new ArrayList<Node>();
		
		typeLabel = new ArrayList<Node>();
		inputNode = new ArrayList<Node>();
		editNode = new ArrayList<Node>();
		editButtonNode = new ArrayList<Node>();
		
		labelNode.add(ofLabel);
		labelNode.add(numberOfElementLabel);
		labelNode.add(new Label());
		
		typeLabel.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		typeLabel.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		typeLabel.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		
		inputNode.add(ofTextField);
		inputNode.add(numberOfElementComboBox);
		inputNode.add(new Label());
		
		editButtonNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		editButtonNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		editButtonNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		
		for (FmmlxAttribute tmp : object.getAllAttributes()) {			
			labelNode.add(new Label(tmp.getName()));	
			typeLabel.add(new Label(tmp.getType()));
			ComboBox<InstanceGeneratorGenerateType> comboBox = initializeComboBoxGeneratorList(getGenerateTypeList(tmp.getType()), tmp);
			inputNode.add(comboBox);
			Button button = new Button("edit");
			editButtonNode.add(button);
		}
		
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(typeLabel,1);
		addNodesToGrid(inputNode,2);
		addNodesToGrid(editNode,3);
		addNodesToGrid(editButtonNode, 5);
	}

}
