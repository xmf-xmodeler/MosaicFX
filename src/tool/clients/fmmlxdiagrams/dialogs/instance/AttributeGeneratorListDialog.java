package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.AttributeGeneratorDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

public class AttributeGeneratorListDialog extends CustomDialog<AttributeGeneratorDialogResult> implements AttributeGeneratorDialog {
	protected InstanceGeneratorGenerateType type;
	protected String attributeType;
	
	private DialogPane dialogPane;
	
	protected List<Node> labelNode;
	protected List<Node> inputNode;
	
	private Label valueListLabel;
	private ListView<String> listValue;
	private Button addItemButton;
	private Button removeItemButton;

	public <T> AttributeGeneratorListDialog(InstanceGeneratorGenerateType type, String attributeType, List<T> value) {
		this.type=type;
		this.attributeType = attributeType;
		System.out.println();
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(type.toString());
		layoutContent();
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);
		dialogPane.setContent(flow);
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {
				e.consume();
			}
		});
		
		setResult();
		//TODO insert value
	}

	public AttributeGeneratorListDialog(InstanceGeneratorGenerateType type, String attributeType) {
		this.type=type;
		this.attributeType = attributeType;
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(type.toString() + " : "+attributeType);
		layoutContent();
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);
		dialogPane.setContent(flow);
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {		
				e.consume();
			}
		});
		setResult();
		
	}

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {		
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				//TODO
//				return new AttributeGeneratorDialogResult(startValueTextField.getText(), 
//						endValueTextField.getText(), incrementValueTextField.getText(), attributeType, type);
			}
			return null;
		});	

	}

	@Override
	public boolean inputIsValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void layoutContent() {
		labelNode = new ArrayList<Node>();
		inputNode = new ArrayList<Node>();
		
		valueListLabel = new Label(StringValue.LabelAndHeaderTitle.valueList);
		listValue = new ListView<String>();
		
		addItemButton = new Button("Add");
		removeItemButton = new Button("Remove");
		
		labelNode.add(valueListLabel);
		
		inputNode.add(listValue);
		inputNode.add(joinNodeElementInHBox(addItemButton, removeItemButton));		
	}

}
