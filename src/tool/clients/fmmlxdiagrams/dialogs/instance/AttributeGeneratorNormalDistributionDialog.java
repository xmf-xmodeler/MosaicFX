package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;

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


	public <T> AttributeGeneratorNormalDistributionDialog(String valueGeneratorName, String attributeType,
			List<T> value) {

		this.attributeType = attributeType;
		System.out.println();
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(valueGeneratorName + " : "+ attributeType);
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
		// TODO insert Value
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
//				return new AttributeGeneratorDialogResult(startValueTextField.getText(), endValueTextField.getText(), 
//						incrementValueTextField.getText(), attributeType, type);
			}
			return null;
		});	

	}

	@Override
	public boolean inputIsValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void layoutContent() {
		labelNode = new ArrayList<Node>();
		inputNode = new ArrayList<Node>();
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

}
