package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.List;

import com.sun.scenario.effect.impl.prism.PrImage;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.InstanceGeneratorDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog.LabelAndHeaderTitle;

public class InstanceGeneratorDialog extends CustomDialog<InstanceGeneratorDialogResult>{

	
	@SuppressWarnings("unused")
	private FmmlxDiagram diagram;
	private FmmlxObject object;
	private Label ofLabel ;
	private Label numberOfElementLabel;
	private TextField ofTextField;
	private ComboBox<Integer> numberOfElementComboBox;
	private List<Node> labelNode;
	private List<Node> inputNode;
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
		inputNode = new ArrayList<Node>();
		
		labelNode.add(ofLabel);
		labelNode.add(numberOfElementLabel);
		labelNode.add(new Label());
		
		inputNode.add(ofTextField);
		inputNode.add(numberOfElementComboBox);
		inputNode.add(new Label());
		for (int i = 0; i < object.getAllAttributes().size(); i++) {
			
			labelNode.add(new Label(object.getAllAttributes().get(i).getName()));
			TextField tmp  = new TextField();
			inputNode.add(tmp);
			
		}
		
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode,1);
		
	}

}
