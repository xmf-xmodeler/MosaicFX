package tool.clients.fmmlxdiagrams.instancegenerator.dialog;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.ValueGeneratorListDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

public class ValueGeneratorListDialog extends CustomDialog<ValueGeneratorListDialogResult> implements ValueGeneratorDialog {

	private final String attributeType;
	
	private final DialogPane dialogPane;

	private ListView<String> listValue;

	public ValueGeneratorListDialog(String valueGeneratorName, String attributeType, List<String> value) {
		
		this.attributeType = attributeType;
		System.out.println();
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
		for(int i = 0 ; i<=value.size(); i++ ){
			listValue.getItems().add(value.get(i));
		}
	}

	public ValueGeneratorListDialog(String valueGeneratorName, String attributeType) {
		
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
	}

	@Override
	public void setStaticValue(List<String> staticValue) {

	}

	@Override
	public void storeParameter() {

	}

	@Override
	public List<String> getParameter() {
		return null;
	}

	public String getAttributeType() {
		return attributeType;
	}

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {		
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				//TODO
//				return new AttributeGeneratorDialogResult(startValueTextField.getText(), 
//						endValueTextField.getText(), incrementValueTextField.getText(), attributeType, type);
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
		List<Node> labelNode = new ArrayList<>();
		List<Node> inputNode = new ArrayList<>();

		Label valueListLabel = new Label(StringValue.LabelAndHeaderTitle.valueList);
		listValue = new ListView<>();

		Button addItemButton = new Button("Add");
		Button removeItemButton = new Button("Remove");
		
		labelNode.add(valueListLabel);
		
		inputNode.add(listValue);
		inputNode.add(joinNodeElementInHBox(addItemButton, removeItemButton));

		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);
	}

    @Override
    public boolean validateLogic(String attributeType) {
        return false;
    }

}
