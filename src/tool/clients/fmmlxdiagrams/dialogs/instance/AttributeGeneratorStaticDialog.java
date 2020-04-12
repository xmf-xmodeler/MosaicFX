package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator.AttributeGeneratorStaticDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

public class AttributeGeneratorStaticDialog extends CustomDialog<AttributeGeneratorStaticDialogResult> implements AttributeGeneratorDialog {

	private String attributeType;
	
	private DialogPane dialogPane;
	
	private List<Node> labelNode;
	private List<Node> inputNode;
	private Label staticValueLabel;
	private TextField staticValueTextField;
	private ComboBox<String> staticValueComboBox;
	
	private String staticValue;

	public AttributeGeneratorStaticDialog(String valueGeneratorName, String attributeType) {

		this.attributeType = attributeType;
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(valueGeneratorName + " : "+attributeType);
		dialogPane.setContent(flow);
		layoutContent();
		
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {		
				e.consume();
			}
		});
		setResult();
	}
	
	public <T> AttributeGeneratorStaticDialog(String valueGeneratorName, String attributeType, List<T> value) {

		this.attributeType = attributeType;
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(valueGeneratorName + " : "+attributeType);
		dialogPane.setContent(flow);
		layoutContent();

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {
				e.consume();
			}
		});
		
		setResult();		
		
		if (attributeType.equals("Boolean")) {
    		this.staticValue =  value.get(0).toString();
    		staticValueComboBox.setValue(value.get(0).toString());
    	} else {
    		this.staticValue =  value.get(0).toString();
        	staticValueTextField.setText(staticValue);
    	}
		
	}

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {		
			if (attributeType.equals("Boolean")) {
        		return new AttributeGeneratorStaticDialogResult(staticValueComboBox.getSelectionModel().getSelectedItem(), attributeType);
        	} else {
        		return new AttributeGeneratorStaticDialogResult(staticValueTextField.getText(), attributeType);
        	}
		});	
	}

	@Override
	public boolean inputIsValid() {
		if (attributeType.equals("Boolean")) {
    		if (staticValueComboBox.getSelectionModel().getSelectedItem()!=null) {
    			return true;
    		}
    	}
    	return validateStatic(staticValueTextField.getText());
	}
	
	protected boolean validateStatic(String string) {
		switch(attributeType){
        case "Integer":
        	if(!inputChecker.validateInteger(string)) {
        		errorLabel.setText("Please input valid String");
        	}
            return inputChecker.validateInteger(string);
        case "Float":
        	if(!inputChecker.validateFloat(string)) {
        		errorLabel.setText("Please input valid float value");
        	}
        	return inputChecker.validateFloat(string);
        case "String":
        	return validateString(string);
        case "Boolean":
        	if(!inputChecker.validateBoolean(string)) {
        		errorLabel.setText("Please select boolean value");
        	}
        	return inputChecker.validateBoolean(string);
        default:
            return false;
        }		
	}

	@Override
	public void layoutContent() {
		labelNode = new ArrayList<Node>();
		inputNode = new ArrayList<Node>();
			
		staticValueLabel = new Label(StringValue.LabelAndHeaderTitle.value);
		
		if(attributeType.equals("Boolean")) {
			staticValueComboBox = new ComboBox<String>(AllValueList.booleanList);
			inputNode.add(staticValueComboBox);
		} else {
			staticValueTextField = new TextField();
			inputNode.add(staticValueTextField);
		}		
		labelNode.add(staticValueLabel);
	
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);
	}



}
