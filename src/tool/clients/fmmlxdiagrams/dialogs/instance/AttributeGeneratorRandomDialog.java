package tool.clients.fmmlxdiagrams.dialogs.instance;


import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.AttributeGeneratorDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

public class AttributeGeneratorRandomDialog extends CustomDialog<AttributeGeneratorDialogResult> implements AttributeGeneratorDialog {
	protected InstanceGeneratorGenerateType type;
	protected String attributeType;
	
	private DialogPane dialogPane;
	
	protected List<Node> labelNode;
	protected List<Node> inputNode;
	
	private String randomValue;
	
	private Label randomValueLabel;
	private TextField randomValueTextField;
	private Button generateRandomValueButton;

	public AttributeGeneratorRandomDialog(InstanceGeneratorGenerateType type, String attributeType) {
		this.type=type;
		this.attributeType = attributeType;
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(type.toString() + " : "+attributeType);
		layoutContent();
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);
		dialogPane.setContent(flow);
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {		
				e.consume();
			}
		});
		setResult();
	}
	
	

	public <T> AttributeGeneratorRandomDialog(InstanceGeneratorGenerateType type, String attributeType, List<T> value) {
		this.type=type;
		this.attributeType = attributeType;
		System.out.println();
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(type.toString());
		layoutContent();
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);
		dialogPane.setContent(flow);
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {
				e.consume();
			}
		});
		
		setResult();
		
		this.randomValue =value.get(0).toString();
    	this.randomValueTextField.setText(value.get(0).toString());
	}



	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {		
			return new AttributeGeneratorDialogResult(randomValueTextField.getText(), attributeType, type);
		});	

	}

	@Override
	public boolean inputIsValid() {
		if (!randomValueTextField.getText().equals("")) {
			return true;
		}
		errorLabel.setText("Please generate random value");
		return false;
	}

	@Override
	public void layoutContent() {
		labelNode = new ArrayList<Node>();
		inputNode = new ArrayList<Node>();	
		
		randomValueLabel = new Label(StringValue.LabelAndHeaderTitle.randomValue);
		randomValueTextField = new TextField();
		randomValueTextField.setDisable(true);
		randomValueTextField.setEditable(false);
		generateRandomValueButton = new Button("Generate");
		generateRandomValueButton.setOnAction(e -> generateRandomValue(randomValueTextField, attributeType));
		
		labelNode.add(randomValueLabel);
		inputNode.add(randomValueTextField);
		inputNode.add(generateRandomValueButton);

	}

}
