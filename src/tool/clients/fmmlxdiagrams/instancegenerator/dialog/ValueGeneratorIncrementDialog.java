package tool.clients.fmmlxdiagrams.instancegenerator.dialog;

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
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.ValueGeneratorIncrementDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;



public class ValueGeneratorIncrementDialog extends CustomDialog<ValueGeneratorIncrementDialogResult> implements ValueGeneratorDialog {
	
	private final String attributeType;

	private TextField startValueTextField;
	private TextField endValueTextField;
	private TextField incrementValueTextField;
	
	private String startValue;
	private String endValue;
	private String incValue;
	
	public ValueGeneratorIncrementDialog(String valueGeneratorName, String attributeType, List<String> parameter) {

		this.attributeType = attributeType;
		System.out.println();
		DialogPane dialogPane = getDialogPane();
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
		setParameter(parameter);
	}

	@Override
	public void setParameter(List<String> parameter) {
		if (parameter!=null){
			this.startValue = parameter.get(0);
			this.startValueTextField.setText(this.startValue);
			this.endValue = parameter.get(1);
			this.endValueTextField.setText(this.endValue);
			this.incValue = parameter.get(2);
			this.incrementValueTextField.setText(this.incValue);
		}
	}

	public void storeParameter() {
		this.startValue = startValueTextField.getText();
		this.endValue = endValueTextField.getText();
		this.incValue = incrementValueTextField.getText();
	}

	@Override
	public List<String> getParameter() {
		List<String> result = new ArrayList<>();
		result.add(this.startValue);
		result.add(this.endValue);
		result.add(this.incValue);
		return result;
	}

	@Override
	public String getAttributeType() {
		return this.attributeType;
	}

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {		
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				storeParameter();
				return new ValueGeneratorIncrementDialogResult(getParameter(), getAttributeType());
			}
			return null;
		});		
	}

	@Override
	public boolean inputIsValid() {
		return validateIncrement(this.startValueTextField.getText(), this.endValueTextField.getText(), this.incrementValueTextField.getText(), getAttributeType());
	}

	@Override
	public void layoutContent() {
		ArrayList<Node> labelNode = new ArrayList<>();
		ArrayList<Node> inputNode = new ArrayList<>();

		Label startValueLabel = new Label(StringValue.LabelAndHeaderTitle.startValue);
		Label endValueLabel = new Label(StringValue.LabelAndHeaderTitle.endValue);
		Label incrementValueLabel = new Label(StringValue.LabelAndHeaderTitle.incrementValue);

		this.startValueTextField = new TextField();
		this.endValueTextField = new TextField();
		this.incrementValueTextField = new TextField();
		
		labelNode.add(startValueLabel);
		labelNode.add(endValueLabel);
		labelNode.add(incrementValueLabel);
		
		inputNode.add(this.startValueTextField);
		inputNode.add(this.endValueTextField);
		inputNode.add(this.incrementValueTextField);
		
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);
		
	}

    @Override
    public boolean validateLogic(String attributeType) {
		return false;
    }

}
