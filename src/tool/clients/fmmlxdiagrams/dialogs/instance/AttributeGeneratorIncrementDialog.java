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
	
	private String attributeType;
	
	private DialogPane dialogPane;
	
	private List<Node> labelNode;
	private List<Node> inputNode;

	private Label startValueLabel;
	private Label endValueLabel;
	private Label incrementValueLabel;
	
	private TextField startValueTextField;
	private TextField endValueTextField;
	private TextField incrementValueTextField;
	
	private String startValue;
	private String endValue;
	private String incValue;
	
	public AttributeGeneratorIncrementDialog(String valueGeneratorName, String attributeType) {
		
		this.attributeType = attributeType;
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(valueGeneratorName + " : "+attributeType);
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
	
	public AttributeGeneratorIncrementDialog(String valueGeneratorName, String attributeType, List<String> parameter) {

		this.attributeType = attributeType;
		System.out.println();
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(valueGeneratorName + " : "+ attributeType);
		layoutContent();
		
		dialogPane.setContent(flow);
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {
				e.consume();
			}
		});
		
		setResult();
		this.startValue = parameter.get(0).toString();
    	this.startValueTextField.setText(startValue);
    	this.endValue = parameter.get(1).toString();
    	this.endValueTextField.setText(endValue);
    	this.incValue = parameter.get(2).toString();
    	this.incrementValueTextField.setText(incValue);
	}

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {		
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {

				return new AttributeGeneratorIncrementDialogResult(startValueTextField.getText(),
						endValueTextField.getText(), incrementValueTextField.getText(), attributeType);
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
		labelNode = new ArrayList();
		inputNode = new ArrayList();
		
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
