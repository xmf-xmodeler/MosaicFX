package tool.clients.fmmlxdiagrams.instancegenerator.dialog;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGeneratorList;

public class ValueGeneratorListDialog extends CustomDialog implements ValueGeneratorDialog {
	private final ValueGeneratorList valueGeneratorList;

	private TextField listName;
	private ListView<String> listValue;

	public ValueGeneratorListDialog(ValueGeneratorList valueGeneratorList) {
		this.valueGeneratorList = valueGeneratorList;
		System.out.println();
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(valueGeneratorList.getValueGeneratorName() + " : "+ valueGeneratorList.getAttributeType());
		layoutContent();
		dialogPane.setContent(flow);
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {
				e.consume();
			}
		});

		setResult();

		if(valueGeneratorList.getParameter()!= null){
			setParameter(valueGeneratorList.getParameter());
		}
	}


	@Override
	public void setParameter(List<String> parameter) {
		listName.setText(parameter.get(0));
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
		parameter.add(listName.getText());
		valueGeneratorList.setParameter(parameter);
		valueGeneratorList.setGeneratedValue(listValue.getSelectionModel().getSelectedItems());
	}

	@Override
	public boolean inputIsValid() {
		if (listName.getText().equals("") || listName.getText()==null) {
			errorLabel.setText("Please input name of the list!");
			return false;
		}
		errorLabel.setText("");
		return true;
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
			valueGeneratorList.fetchList(listName);
			listValue.setItems(FXCollections.observableArrayList(valueGeneratorList.getFetchedList()));
		}
	}

	@Override
    public boolean validateLogic() {
        return false;
    }

}
