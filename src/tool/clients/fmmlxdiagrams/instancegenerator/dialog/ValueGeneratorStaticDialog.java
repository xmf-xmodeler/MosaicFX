package tool.clients.fmmlxdiagrams.instancegenerator.dialog;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.ValueGeneratorStaticDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

public class ValueGeneratorStaticDialog extends CustomDialog<ValueGeneratorStaticDialogResult> implements ValueGeneratorDialog {

	private final String attributeType;

	private TextField staticValueTextField;
	private ComboBox<String> staticValueComboBox;
	
	private String staticValue;
	
	public ValueGeneratorStaticDialog(String valueGeneratorName, String attributeType, List<String> parameter) {

		this.attributeType = attributeType;

		DialogPane dialogPane = getDialogPane();
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
		setParameter(parameter);
	}

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				storeParameter();
				return new ValueGeneratorStaticDialogResult(getParameter(), attributeType);
			}
			return null;
		});
	}

	@Override
	public void setParameter(List<String> staticValue) {
		if (staticValue !=null){
			if (this.attributeType.equals("Boolean")) {
				this.staticValue =  staticValue.get(0);
				staticValueComboBox.setValue(staticValue.get(0));
			} else {
				this.staticValue =  staticValue.get(0);
				staticValueTextField.setText(this.staticValue);
			}
		}
	}

	@Override
	public void storeParameter() {
		if(getAttributeType().equals("Boolean")){
			this.staticValue = staticValueComboBox.getSelectionModel().getSelectedItem();
		} else {
			this.staticValue = staticValueTextField.getText();
		}
	}

	@Override
	public List<String> getParameter() {
		List<String> result = new ArrayList<>();
		result.add(this.staticValue);
		return result;
	}

	@Override
	public String getAttributeType() {
		return attributeType;
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
        		errorLabel.setText("Please input valid Integer");
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
		ArrayList<Node> labelNode = new ArrayList<>();
		ArrayList<Node> inputNode = new ArrayList<>();

		Label staticValueLabel = new Label(StringValue.LabelAndHeaderTitle.value);
		
		if(attributeType.equals("Boolean")) {
			staticValueComboBox = new ComboBox<>(AllValueList.booleanList);
			inputNode.add(staticValueComboBox);
		} else {
			staticValueTextField = new TextField();
			inputNode.add(staticValueTextField);
		}		
		labelNode.add(staticValueLabel);
	
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);
	}

	@Override
	public boolean validateLogic(String attributeType) {
		return false;
	}


}
