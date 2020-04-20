package tool.clients.fmmlxdiagrams.instancegenerator.dialog;


import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.ValueGeneratorRandomDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

public class ValueGeneratorRandomDialog extends CustomDialog<ValueGeneratorRandomDialogResult> implements ValueGeneratorDialog {

	private final String attributeType;

	private ComboBox<String> scenarioComboBox;
	private TextField rangeMinTextField, rangeMaxTextField;

	private String scenario;
	private String rangeMin;
	private String rangeMax;

	public ValueGeneratorRandomDialog(String valueGeneratorName, String attributeType, String scenario, List<String> parameter) {

		this.attributeType = attributeType;
		this.scenario =scenario;
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
		setScenario(scenario);
		setParameter(parameter);

	}

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				storeScenario();
				storeParameter();
				return new ValueGeneratorRandomDialogResult(getAttributeType(), getScenario(), getParameter());
			}
			return null;
		});
	}

	private void storeScenario() {
		this.scenario = scenarioComboBox.getSelectionModel().getSelectedItem();
	}

	@Override
	public void setParameter(List<String> staticValue) {
		if(getScenario()!=null) {
			if (!getScenario().equals("Free")) {
				this.rangeMin = staticValue.get(0);
				this.rangeMinTextField.setText(rangeMin);
				this.rangeMax = staticValue.get(1);
				this.rangeMaxTextField.setText(rangeMax);
			}
		}
	}

	public void setScenario(String scenario) {
		if (scenario!=null){
			this.scenario = scenario;
			this.scenarioComboBox.setValue(getScenario());
		}
	}

	private String getScenario() {
		return this.scenario;
	}

	public void storeParameter() {
		this.rangeMin = this.rangeMinTextField.getText();
		this.rangeMax = this.rangeMaxTextField.getText();
	}

	@Override
	public String getAttributeType() {
		return this.attributeType;
	}

	public List<String> getParameter() {
		List<String> result = new ArrayList<>();
		result.add(this.rangeMin);
		result.add(this.rangeMax);
		return result;
	}

	private boolean validateFloat(TextField rangeMinTextField, TextField rangeMaxTextField) {
		if(!inputChecker.validateFloat(rangeMinTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidFloatValue +" : Range (Min)");
			return false;
		} else if(!inputChecker.validateFloat(rangeMaxTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidFloatValue +" : Range (Max)");
			return false;
		} else if(!validateLogic(getAttributeType())){
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	private boolean validateInteger(TextField rangeMinTextField, TextField rangeMaxTextField) {
		if(!inputChecker.validateInteger(rangeMinTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidIntegerValue +" : Range (Min)");
			return false;
		} else if(!inputChecker.validateInteger(rangeMaxTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidIntegerValue +" : Range (Max)");
			return false;
		} else if(!validateLogic(attributeType)){
			return false;
		}

		rangeMinTextField.setText(Integer.parseInt(rangeMinTextField.getText())+"");
		rangeMaxTextField.setText(Integer.parseInt(rangeMaxTextField.getText())+"");
		errorLabel.setText("");
		return true;
	}

	public boolean validateLogic(String attributeType) {
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

	@Override
	public boolean inputIsValid() {
			if(this.scenarioComboBox.getSelectionModel().getSelectedItem()== null){
				errorLabel.setText("Please select scenario");
				return false;
			} if(this.scenarioComboBox.getSelectionModel().getSelectedItem().equals("Range")){
				switch (getAttributeType()) {
					case "Integer":
						return validateInteger(this.rangeMinTextField, this.rangeMaxTextField);
					case "Float":
						return validateFloat(this.rangeMinTextField, this.rangeMaxTextField);
					default:
						return false;
				}
			}
			errorLabel.setText("");
			return true;
	}

	@Override
	public void layoutContent() {
		List<Node> labelNode = new ArrayList<>();
		List<Node> inputNode = new ArrayList<>();

		Label scenarioLabel = new Label(StringValue.LabelAndHeaderTitle.scenario);
		Label rangeLabel = new Label(StringValue.LabelAndHeaderTitle.range);

		this.rangeMinTextField = new TextField();
		this.rangeMinTextField.setDisable(true);
		this.rangeMaxTextField = new TextField();
		this.rangeMaxTextField.setDisable(true);

		VBox rangeVBox = getVBoxControl().joinNodeInVBox(rangeMinTextField, new Label("  -"), rangeMaxTextField);

		ObservableList<String> typeList = FXCollections.observableArrayList("Range", "Free");
		this.scenarioComboBox = new ComboBox<>(typeList);
		this.scenarioComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue.equals(oldValue)){
				if(newValue.equals("Free")){
					this.rangeMinTextField.setText("");
					this.rangeMaxTextField.setText("");
					this.rangeMin=null;
					this.rangeMinTextField.setDisable(true);
					this.rangeMaxTextField.setDisable(true);
					this.rangeMax=null;
				} else {
					this.rangeMinTextField.setDisable(false);
					this.rangeMaxTextField.setDisable(false);
				}
			}
		});

		labelNode.add(scenarioLabel);
		labelNode.add(rangeLabel);
		inputNode.add(this.scenarioComboBox);
		inputNode.add(rangeVBox);
		
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);

	}
}
