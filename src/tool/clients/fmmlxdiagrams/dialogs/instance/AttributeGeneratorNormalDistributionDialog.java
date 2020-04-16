package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;
import tool.clients.diagrams.Text;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator.AttributeGeneratorNormalDistributionDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;


public class AttributeGeneratorNormalDistributionDialog extends CustomDialog<AttributeGeneratorNormalDistributionDialogResult>
		implements AttributeGeneratorDialog {
	
	private String attributeType;
	
	private DialogPane dialogPane;
	
	private List<Node> labelNode, inputNode;

	private Label meanLabel, standardDeviationLabel, rangeLabel;

	private TextField meanTextField, stdTextField,rangeMinTextField, rangeMaxTextField;

	private VBox rangeVBox;

	private String meanValue, stdDevValue, rangeMinValue, rangeMaxValue;


	public  AttributeGeneratorNormalDistributionDialog(String valueGeneratorName, String attributeType,
			List<String> value) {

		this.attributeType = attributeType;
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

		this.meanValue = value.get(0);
		meanTextField.setText(meanValue);
		this.stdDevValue = value.get(1);
		stdTextField.setText(stdDevValue);
		this.rangeMinValue = value.get(2);
		rangeMinTextField.setText(rangeMinValue);
		this.rangeMaxValue = value.get(3);
		rangeMaxTextField.setText(rangeMaxValue);
	}

	public AttributeGeneratorNormalDistributionDialog(String valueGeneratorName, String attributeType) {

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

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {		
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {

				meanValue = meanTextField.getText();
				stdDevValue = stdTextField.getText();
				rangeMinValue = rangeMinTextField.getText();
				rangeMaxValue = rangeMaxTextField.getText();

				return new AttributeGeneratorNormalDistributionDialogResult(attributeType, meanValue, stdDevValue, rangeMinValue, rangeMaxValue);
			}
			return null;
		});
	}

	@Override
	public boolean inputIsValid() {
		switch (attributeType) {
			case "Integer":
				validateInteger(meanTextField, stdTextField, rangeMinTextField, rangeMaxTextField);
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
		} else if(!validateLogic(attributeType)){
			return false;
		}
		meanTextField.setText(Integer.parseInt(meanTextField.getText())+"");
		stdTextField.setText(Integer.parseInt(stdTextField.getText())+"");
		rangeMinTextField.setText(Integer.parseInt(rangeMinTextField.getText())+"");
		rangeMaxTextField.setText(Integer.parseInt(rangeMaxTextField.getText())+"");
		errorLabel.setText("");
		return true;
	}

	private boolean validateLogic(String attributeType) {
		if(attributeType.equals("Integer")){
			if(Integer.parseInt(rangeMinTextField.getText())>=Integer.parseInt(rangeMaxTextField.getText())){
				errorLabel.setText("Minimum range-value is bigger or same as maximum range-value");
				return false;
			}
		} else if (attributeType.equals("Float")){
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
		} else if(!validateLogic(attributeType)){
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
		labelNode = new ArrayList<>();
		inputNode = new ArrayList<>();
		meanLabel = new Label(StringValue.LabelAndHeaderTitle.Mean);
		standardDeviationLabel = new Label(StringValue.LabelAndHeaderTitle.stdDeviation);
		rangeLabel = new Label(StringValue.LabelAndHeaderTitle.Range);

		meanTextField = new TextField();
		stdTextField = new TextField();
		rangeMinTextField = new TextField();
		rangeMaxTextField = new TextField();

		rangeVBox = getVBoxControl().joinNodeInVBox(rangeMinTextField, new Label("  -"), rangeMaxTextField);

		labelNode.add(meanLabel);
		labelNode.add(standardDeviationLabel);
		labelNode.add(rangeLabel);

		inputNode.add(meanTextField);
		inputNode.add(stdTextField);
		inputNode.add(rangeVBox);

		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);
	}

	public String generateValue(String attributeType, String mean, String stdDeviation, long rangeMin, long rangeMax){
		Random random = new Random();
		if(attributeType.equals("Integer")){
			Boolean boolValue = false;
			int meanInt = Integer.parseInt(mean);
			int stdDevInt = Integer.parseInt(stdDeviation);
			while(!boolValue){
				long nextGauss = Math.round(random.nextGaussian());
				if(nextGauss<=rangeMax && nextGauss>=rangeMin){
					return ((nextGauss*stdDevInt)+meanInt)+"";
				}
			}
		} else if (attributeType.equals("Float")){
			Boolean boolValue = false;
			float meanFloat = Float.parseFloat(mean);
			float stdDevFloat = Float.parseFloat(stdDeviation);
			while(!boolValue){
				double nextGauss = random.nextGaussian();
				if(nextGauss<=rangeMax && nextGauss>=rangeMin){
					return ((nextGauss*stdDevFloat)+meanFloat)+"";
				}
			}
		}
		return "";
	}
}
