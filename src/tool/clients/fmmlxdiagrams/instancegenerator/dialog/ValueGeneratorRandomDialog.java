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
	private DialogPane dialogPane;

	private List<Node> labelNode;
	private List<Node> inputNode;

	private Label scenarioLabel;
	private Label rangeLabel;
	private ComboBox<String> scenarioComboBox;
	private TextField rangeMinTextField, rangeMaxTextField;
	private VBox rangeVBox;

	public ValueGeneratorRandomDialog(String valueGeneratorName, String attributeType, String scenario, List<String> parameter) {

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
		scenarioComboBox.setValue(scenario);
		if(scenario!=null) {
			if (!scenario.equals("Free")) {
				rangeMinTextField.setText(parameter.get(0));
				rangeMaxTextField.setText(parameter.get(1));
			}
		}
	}

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				return new ValueGeneratorRandomDialogResult(attributeType, scenarioComboBox.getSelectionModel().getSelectedItem(), getParameter());
			}
			return null;
		});
	}

	private List<String> getParameter() {
		List<String> result = new ArrayList<>();
		result.add(rangeMinTextField.getText());
		result.add(rangeMaxTextField.getText());
		return result;
	}

	private boolean validateFloat(TextField rangeMinTextField, TextField rangeMaxTextField) {
		if(!inputChecker.validateFloat(rangeMinTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidFloatValue +" : Range (Min)");
			return false;
		} else if(!inputChecker.validateFloat(rangeMaxTextField.getText())){
			errorLabel.setText(StringValue.ErrorMessage.pleaseInputValidFloatValue +" : Range (Max)");
			return false;
		} else if(!validateLogic(attributeType)){
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
			if(scenarioComboBox.getSelectionModel().getSelectedItem()== null){
				errorLabel.setText("Please select scenario");
				return false;
			} if(scenarioComboBox.getSelectionModel().getSelectedItem().equals("Range")){
				switch (attributeType) {
					case "Integer":
						return validateInteger(rangeMinTextField, rangeMaxTextField);
					case "Float":
						return validateFloat(rangeMinTextField, rangeMaxTextField);
					default:
						return false;
				}
			}
			errorLabel.setText("");
			return true;
	}

	@Override
	public void layoutContent() {
		labelNode = new ArrayList<>();
		inputNode = new ArrayList<>();

		scenarioLabel = new Label(StringValue.LabelAndHeaderTitle.scenario);
		rangeLabel = new Label(StringValue.LabelAndHeaderTitle.range);

		rangeMinTextField = new TextField();
		rangeMinTextField.setDisable(true);
		rangeMaxTextField = new TextField();
		rangeMaxTextField.setDisable(true);

		rangeVBox = getVBoxControl().joinNodeInVBox(rangeMinTextField, new Label("  -"), rangeMaxTextField);

		ObservableList<String> typeList = FXCollections.observableArrayList("Range", "Free");
		scenarioComboBox = new ComboBox<>(typeList);
		scenarioComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue.equals(oldValue)){
				if(newValue.equals("Free")){
					rangeMinTextField.setText("");
					rangeMaxTextField.setText("");
					rangeMinTextField.setDisable(true);
					rangeMaxTextField.setDisable(true);
				} else {
					rangeMinTextField.setDisable(false);
					rangeMaxTextField.setDisable(false);
				}
			}
		});

		labelNode.add(scenarioLabel);
		labelNode.add(rangeLabel);
		inputNode.add(scenarioComboBox);
		inputNode.add(rangeVBox);
		
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);

	}
}
