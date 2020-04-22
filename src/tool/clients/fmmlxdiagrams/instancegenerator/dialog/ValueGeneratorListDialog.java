package tool.clients.fmmlxdiagrams.instancegenerator.dialog;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.ValueGeneratorListDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

public class ValueGeneratorListDialog extends CustomDialog<ValueGeneratorListDialogResult> implements ValueGeneratorDialog {

	private final String attributeType;

	private TextField listName;
	private ListView<String> listValue;

	public ValueGeneratorListDialog(String valueGeneratorName, String attributeType, List<String> parameter) {
		
		this.attributeType = attributeType;
		System.out.println();
		DialogPane dialogPane = getDialogPane();
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

		if(parameter!= null){
			setParameter(parameter);
		}
	}


	@Override
	public void setParameter(List<String> parameter) {
		listName.setText(parameter.get(0));
	}


	@Override
	public List<String> getParameter() {
		List<String> result = new ArrayList<>();
		result.add(listName.getText());
		return result;
	}

	public String getAttributeType() {
		return this.attributeType;
	}

	@Override
	public void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new ValueGeneratorListDialogResult(getAttributeType(), getParameter(), listValue.getSelectionModel().getSelectedItems());
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

		Label listNameLabel = new Label("Name of List");
		Label valueListLabel = new Label(StringValue.LabelAndHeaderTitle.valueList);

		this.listName = new TextField();
		this.listValue = new ListView<>();

		Button fetchButton = new Button("Fetch Elements");
		fetchButton.setOnAction(e -> fetchElement(listName.getText()));

		labelNode.add(listNameLabel);
		labelNode.add(valueListLabel);

		inputNode.add(getVBoxControl().joinNodeInVBox(listName, fetchButton));
		inputNode.add(this.listValue);

		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);
	}

	private void fetchElement(String listName) {
		if(listName!= null || !listName.equals("")){
			//TODO fetch elements of the list
		}
	}

	@Override
    public boolean validateLogic(String attributeType) {
        return false;
    }

}
