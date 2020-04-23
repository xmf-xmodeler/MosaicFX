package tool.clients.fmmlxdiagrams.instancegenerator.view;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGeneratorStatic;

public class ValueGeneratorStaticDialog extends CustomDialog<String> implements ValueGeneratorDialog {

	private final ValueGeneratorStatic valueGeneratorStatic;

	private TextField staticValueTextField;
	private ComboBox<String> staticValueComboBox;


	public ValueGeneratorStaticDialog(ValueGeneratorStatic valueGeneratorStatic) {
		this.valueGeneratorStatic = valueGeneratorStatic;
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(valueGeneratorStatic.getValueGeneratorName() + " : "+valueGeneratorStatic.getAttributeType());
		dialogPane.setContent(flow);
		layoutContent();

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {
				e.consume();
			}
		});
		
		setResult();
		setParameter(valueGeneratorStatic.getParameter());
	}

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				storeParameter();
			}
			return null;
		});
	}

	@Override
	public void setParameter(List<String> staticValue) {
		if (staticValue !=null){
			if (valueGeneratorStatic.getAttributeType().equals("Boolean")) {
				staticValueComboBox.setValue(staticValue.get(0));
			} else {
				staticValueTextField.setText(valueGeneratorStatic.getParameter().get(0));
			}
		}
	}


	@Override
	public void storeParameter() {
		List<String> parameter = new ArrayList<>();
		if(valueGeneratorStatic.getAttributeType().equals(StringValue.TraditionalDataType.BOOLEAN)){
			parameter.add(staticValueComboBox.getSelectionModel().getSelectedItem());
			valueGeneratorStatic.setParameter(parameter);
		} else {
			parameter.add(staticValueTextField.getText());
		}
		valueGeneratorStatic.setParameter(parameter);
	}


	@Override
	public boolean inputIsValid() {
		if (valueGeneratorStatic.getAttributeType().equals(StringValue.TraditionalDataType.BOOLEAN)) {
    		if (staticValueComboBox.getSelectionModel().getSelectedItem()!=null) {
    			return true;
    		}
    	}
    	return validateStatic(staticValueTextField.getText());
	}
	
	protected boolean validateStatic(String string) {
		switch(valueGeneratorStatic.getAttributeType()){
        case StringValue.TraditionalDataType.INTEGER:
        	if(!inputChecker.validateInteger(string)) {
        		errorLabel.setText("Please input valid Integer");
        	}
            return inputChecker.validateInteger(string);
        case StringValue.TraditionalDataType.FLOAT:
        	if(!inputChecker.validateFloat(string)) {
        		errorLabel.setText("Please input valid float value");
        	}
        	return inputChecker.validateFloat(string);
        case StringValue.TraditionalDataType.STRING:
        	return validateString(string);
        case StringValue.TraditionalDataType.BOOLEAN:
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
		
		if(valueGeneratorStatic.getAttributeType().equals(StringValue.TraditionalDataType.BOOLEAN)) {
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
	public boolean validateLogic() {
		return false;
	}

}
