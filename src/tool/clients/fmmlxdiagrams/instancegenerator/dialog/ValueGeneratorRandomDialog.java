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
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGeneratorRandom;

public class ValueGeneratorRandomDialog extends CustomDialog implements ValueGeneratorDialog {

	private final ValueGeneratorRandom valueGeneratorRandom;

	private ComboBox<String> scenarioComboBox;
	private TextField rangeMinTextField, rangeMaxTextField;

	public ValueGeneratorRandomDialog(ValueGeneratorRandom valueGeneratorRandom) {
		this.valueGeneratorRandom = valueGeneratorRandom;
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(valueGeneratorRandom.getValueGeneratorName() + " : "+valueGeneratorRandom.getAttributeType());
		dialogPane.setContent(flow);
		layoutContent();

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {
				e.consume();
			}
		});
		
		setResult();
		setScenario(valueGeneratorRandom.getSelectedScenario());
		setParameter(valueGeneratorRandom.getParameter());

	}

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && ((ButtonType)dlgBtn).getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				storeScenario();
				if (!scenarioComboBox.getSelectionModel().getSelectedItem().equals("Free")){
					storeParameter();
				}
			}
			return null;
		});
	}

	private void storeScenario() {
		valueGeneratorRandom.setSelectedScenario(scenarioComboBox.getSelectionModel().getSelectedItem());
	}

	@Override
	public void setParameter(List<String> staticValue) {
		if(valueGeneratorRandom.getSelectedScenario()!=null) {
			if (!valueGeneratorRandom.getSelectedScenario().equals("Free")) {
				this.rangeMinTextField.setText(valueGeneratorRandom.getParameter().get(0));
				this.rangeMaxTextField.setText(valueGeneratorRandom.getParameter().get(1));
			}
		}
	}

	public void setScenario(String scenario) {
		if (scenario!=null){
			this.scenarioComboBox.setValue(valueGeneratorRandom.getSelectedScenario());
		}
	}

	@Override
	public void storeParameter() {
		if(!valueGeneratorRandom.getAttributeType().equals(StringValue.TraditionalDataType.BOOLEAN)){
			List<String> parameter = new ArrayList<>();
			parameter.add(this.rangeMinTextField.getText());
			parameter.add(this.rangeMaxTextField.getText());
			valueGeneratorRandom.setParameter(parameter);
		}
	}

	private boolean validateFloat(TextField rangeMinTextField, TextField rangeMaxTextField) {
		if(!inputChecker.validateFloat(rangeMinTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidFloatValue +" : Range (Min)");
			return false;
		} else if(!inputChecker.validateFloat(rangeMaxTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidFloatValue +" : Range (Max)");
			return false;
		} else if(!validateLogic()){
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
		} else if(!validateLogic()){
			return false;
		}

		rangeMinTextField.setText(Integer.parseInt(rangeMinTextField.getText())+"");
		rangeMaxTextField.setText(Integer.parseInt(rangeMaxTextField.getText())+"");
		errorLabel.setText("");
		return true;
	}

	@Override
	public boolean validateLogic() {
		if(valueGeneratorRandom.getAttributeType().equals("Integer")){
			if(Integer.parseInt(rangeMinTextField.getText())>=Integer.parseInt(rangeMaxTextField.getText())){
				errorLabel.setText("Minimum range-value is bigger or same as maximum range-value");
				return false;
			}
		} else if (valueGeneratorRandom.getAttributeType().equals("Float")){
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
				switch (valueGeneratorRandom.getAttributeType()) {
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
					this.rangeMinTextField.setDisable(true);
					this.rangeMaxTextField.setDisable(true);
					valueGeneratorRandom.setParameter(null);
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
