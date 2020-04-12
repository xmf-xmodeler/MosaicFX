package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator.AttributeGeneratorNormalDistributionDialogResult;


public class AttributeGeneratorNormalDistributionDialog extends CustomDialog<AttributeGeneratorNormalDistributionDialogResult>
		implements AttributeGeneratorDialog {
	
	protected InstanceGeneratorGenerateType type;
	protected String attributeType;
	
	private DialogPane dialogPane;
	
	protected List<Node> labelNode;
	protected List<Node> inputNode;

	public <T> AttributeGeneratorNormalDistributionDialog(InstanceGeneratorGenerateType type, String attributeType,
			List<T> value) {
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
		// TODO insert Value
	}

	public AttributeGeneratorNormalDistributionDialog(InstanceGeneratorGenerateType type, String attributeType) {
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
		// TODO Auto-generated method stub

	}

}
