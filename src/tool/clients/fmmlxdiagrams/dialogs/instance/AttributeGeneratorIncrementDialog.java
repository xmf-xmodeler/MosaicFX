package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator.AttributeGeneratorIncrementDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;



public class AttributeGeneratorIncrementDialog extends CustomDialog<AttributeGeneratorIncrementDialogResult> implements AttributeGeneratorDialog {
	protected InstanceGeneratorGenerateType type;
	protected String attributeType;
	
	private DialogPane dialogPane;
	
	protected List<Node> labelNode;
	protected List<Node> inputNode;

	private Label startValueLabel;
	private Label endValueLabel;
	private Label incrementValueLabel;
	
	private TextField startValueTextField;
	private TextField endValueTextField;
	private TextField incrementValueTextField;
	
	public String startValue;
	public String endValue;
	protected String incValue;
	
	public AttributeGeneratorIncrementDialog(InstanceGeneratorGenerateType type, String attributeType) {
		this.type=type;
		this.attributeType = attributeType;
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(type.toString() + " : "+attributeType);
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
	
	public <T> AttributeGeneratorIncrementDialog(InstanceGeneratorGenerateType type, String attributeType, List<T> value) {
		this.type=type;
		this.attributeType = attributeType;
		System.out.println();
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(type.toString());
		layoutContent();
		
		dialogPane.setContent(flow);
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {
				e.consume();
			}
		});
		
		setResult();
		this.startValue = value.get(0).toString();
    	this.startValueTextField.setText(startValue);
    	this.endValue = value.get(1).toString();
    	this.endValueTextField.setText(endValue);
    	this.incValue = value.get(2).toString();
    	this.incrementValueTextField.setText(incValue);
	}

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {		
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				
				return new AttributeGeneratorIncrementDialogResult(startValueTextField.getText(), endValueTextField.getText(), incrementValueTextField.getText(), attributeType);		        
			}
			return null;
		});		
	}

	@Override
	public boolean inputIsValid() {
		
		return validateIncrement(startValueTextField.getText(), endValueTextField.getText(), incrementValueTextField.getText(), attributeType); 
	}

	@Override
	public void layoutContent() {
		labelNode = new ArrayList<Node>();
		inputNode = new ArrayList<Node>();
		
		startValueLabel = new Label(StringValue.LabelAndHeaderTitle.startValue);
		endValueLabel = new Label(StringValue.LabelAndHeaderTitle.endValue);
		incrementValueLabel = new Label(StringValue.LabelAndHeaderTitle.incrementValue);
		
		startValueTextField = new TextField();
		endValueTextField = new TextField();
		incrementValueTextField = new TextField();
		
		labelNode.add(startValueLabel);
		labelNode.add(endValueLabel);
		labelNode.add(incrementValueLabel);
		
		inputNode.add(startValueTextField);
		inputNode.add(endValueTextField);
		inputNode.add(incrementValueTextField);
		
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);
		
	}

}
