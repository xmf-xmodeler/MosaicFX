package tool.clients.fmmlxdiagrams.instancegenerator.view;

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
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGeneratorIncrement;


public class ValueGeneratorIncrementDialog extends CustomDialog<List<String>> implements ValueGeneratorDialog {

	private final ValueGeneratorIncrement valueGenerator;
	private TextField startValueTextField;
	private TextField endValueTextField;
	private TextField incrementValueTextField;

	public ValueGeneratorIncrementDialog(ValueGeneratorIncrement valueGenerator) {
		this.valueGenerator =valueGenerator;

		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(valueGenerator.getValueGeneratorName() + " : "+ valueGenerator.getAttributeType());
		layoutContent();
		
		dialogPane.setContent(flow);
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {
				e.consume();
			}
		});
		
		setResult();
		setParameter(valueGenerator.getParameter());
	}

	@Override
	public void setParameter(List<String> parameter) {
		if (parameter!=null){
			this.startValueTextField.setText(valueGenerator.getParameter().get(0));
			this.endValueTextField.setText(valueGenerator.getParameter().get(1));
			this.incrementValueTextField.setText(valueGenerator.getParameter().get(2));
		}
	}

	@Override
	public void storeParameter() {
		List<String> parameter = new ArrayList<>();
		parameter.add(startValueTextField.getText());
		parameter.add(endValueTextField.getText());
		parameter.add(incrementValueTextField.getText());
		valueGenerator.setParameter(parameter);
	}

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {		
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				storeParameter();
			}
			return null;
		});		
	}


	@Override
	public boolean inputIsValid() {
		return validateIncrement(this.startValueTextField.getText(), this.endValueTextField.getText(), this.incrementValueTextField.getText(), valueGenerator.getAttributeType());
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
    public boolean validateLogic() {
		return false;
    }

}
