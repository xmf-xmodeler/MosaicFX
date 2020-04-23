package tool.clients.fmmlxdiagrams.instancegenerator.dialog;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGeneratorNormalDistribution;


public class ValueGeneratorNormalDistributionDialog extends CustomDialog
		implements ValueGeneratorDialog {
	
	private final ValueGeneratorNormalDistribution valueGeneratorNormalDistribution;

	private TextField meanTextField, stdTextField,rangeMinTextField, rangeMaxTextField;


	public ValueGeneratorNormalDistributionDialog(ValueGeneratorNormalDistribution valueGeneratorNormalDistribution) {

		this.valueGeneratorNormalDistribution = valueGeneratorNormalDistribution;
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(valueGeneratorNormalDistribution.getValueGeneratorName() + " : "
				+ valueGeneratorNormalDistribution.getAttributeType());
		layoutContent();
		dialogPane.setContent(flow);
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {
				e.consume();
			}
		});
		
		setResult();

		if(valueGeneratorNormalDistribution.getParameter()!=null){
			setParameter(valueGeneratorNormalDistribution.getParameter());
		}
	}

	@Override
	public void setParameter(List<String> staticValue) {
		meanTextField.setText(valueGeneratorNormalDistribution.getParameter().get(0));
		stdTextField.setText(valueGeneratorNormalDistribution.getParameter().get(1));
		rangeMinTextField.setText(valueGeneratorNormalDistribution.getParameter().get(2));
		rangeMaxTextField.setText(valueGeneratorNormalDistribution.getParameter().get(3));
	}

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {		
			if (dlgBtn != null && ((ButtonType)dlgBtn).getButtonData() == ButtonData.OK_DONE) {
				storeParameter();
			}
			return null;
		});
	}

	@Override
	public void storeParameter() {
		List<String> parameter = new ArrayList<>();
		parameter.add(meanTextField.getText());
		parameter.add(stdTextField.getText());
		parameter.add(rangeMinTextField.getText());
		parameter.add(rangeMaxTextField.getText());
		valueGeneratorNormalDistribution.setParameter(parameter);
	}

	@Override
	public boolean inputIsValid() {
		switch (valueGeneratorNormalDistribution.getAttributeType()) {
			case "Integer":
				return validateInteger(meanTextField, stdTextField, rangeMinTextField, rangeMaxTextField);
			case "Float":
				return validateFloat(meanTextField, stdTextField, rangeMinTextField, rangeMaxTextField);
			default:
				return false;
		}
	}

	private boolean validateInteger(TextField meanTextField, TextField stdTextField, TextField rangeMinTextField, TextField rangeMaxTextField) {
		if(!inputChecker.validateInteger(meanTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidIntegerValue +" : Mean");
			return false;
		} else if(!inputChecker.validateInteger(stdTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidIntegerValue +" : Standard Deviation");
			return false;
		} else if(!inputChecker.validateInteger(rangeMinTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidIntegerValue +" : Range (Min)");
			return false;
		} else if(!inputChecker.validateInteger(rangeMaxTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidIntegerValue +" : Range (Max)");
			return false;
		} else if(!validateLogic()){
			return false;
		}
		meanTextField.setText(Integer.parseInt(meanTextField.getText())+"");
		stdTextField.setText(Integer.parseInt(stdTextField.getText())+"");
		rangeMinTextField.setText(Integer.parseInt(rangeMinTextField.getText())+"");
		rangeMaxTextField.setText(Integer.parseInt(rangeMaxTextField.getText())+"");
		errorLabel.setText("");
		return true;
	}

	@Override
	public boolean validateLogic() {
		if(valueGeneratorNormalDistribution.getAttributeType().equals("Integer")){
			if(Integer.parseInt(rangeMinTextField.getText())>=Integer.parseInt(rangeMaxTextField.getText())){
				errorLabel.setText("Minimum range-value is bigger or same as maximum range-value");
				return false;
			}
		} else if (valueGeneratorNormalDistribution.getAttributeType().equals("Float")){
			if(Float.parseFloat(rangeMinTextField.getText())>=Float.parseFloat(rangeMaxTextField.getText())){
				errorLabel.setText("Minimum range-value is bigger or same as maximum range-value");
				return false;
			}
		}
		return true;
	}

	private boolean validateFloat(TextField meanTextField, TextField stdTextField, TextField rangeMinTextField, TextField rangeMaxTextField) {
		if(!inputChecker.validateFloat(meanTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidFloatValue +" : Mean");
			return false;
		} else if(!inputChecker.validateFloat(stdTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidFloatValue +" : Standard Deviation");
			return false;
		} else if(!inputChecker.validateFloat(rangeMinTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidFloatValue +" : Range (Min)");
			return false;
		} else if(!inputChecker.validateFloat(rangeMaxTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidFloatValue +" : Range (Max)");
			return false;
		} else if(!validateLogic()){
			return false;
		}
		meanTextField.setText(Float.parseFloat(meanTextField.getText())+"");
		stdTextField.setText(Float.parseFloat(stdTextField.getText())+"");
		rangeMinTextField.setText(Float.parseFloat(rangeMinTextField.getText())+"");
		rangeMaxTextField.setText(Float.parseFloat(rangeMaxTextField.getText())+"");
		errorLabel.setText("");
		return true;
	}

	@Override
	public void layoutContent() {
		ArrayList<Node> labelNode = new ArrayList<>();
		ArrayList<Node> inputNode = new ArrayList<>();
		Label meanLabel = new Label(StringValue.LabelAndHeaderTitle.Mean);
		Label standardDeviationLabel = new Label(StringValue.LabelAndHeaderTitle.stdDeviation);
		Label rangeLabel = new Label(StringValue.LabelAndHeaderTitle.Range);

		meanTextField = new TextField();
		stdTextField = new TextField();
		rangeMinTextField = new TextField();
		rangeMaxTextField = new TextField();

		VBox rangeVBox = getVBoxControl().joinNodeInVBox(rangeMinTextField, new Label("  -"), rangeMaxTextField);

		labelNode.add(meanLabel);
		labelNode.add(standardDeviationLabel);
		labelNode.add(rangeLabel);

		inputNode.add(meanTextField);
		inputNode.add(stdTextField);
		inputNode.add(rangeVBox);

		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);
	}
}
