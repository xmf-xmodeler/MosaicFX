package tool.clients.fmmlxdiagrams.instancegenerator.view;

import java.util.*;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.converter.IntegerStringConverter;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue.LabelAndHeaderTitle;
import tool.clients.fmmlxdiagrams.instancegenerator.InstanceGenerator;
import tool.clients.fmmlxdiagrams.instancegenerator.helper.InstanceGeneratorEditButton;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.IValueGenerator;

public class InstanceGeneratorDialog extends CustomDialog<List<String>> {

	private final InstanceGenerator instanceGenerator;
	private ComboBox<Integer> numberOfInstanceComboBox;
	private List<Node> inputNode;
	private ListView<FmmlxObject> parentListView;

	public InstanceGeneratorDialog(InstanceGenerator instanceGenerator){
		this.instanceGenerator = instanceGenerator;
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(LabelAndHeaderTitle.instanceGenerator);
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

	private void setResult() {

		setResultConverter(button -> {
			if (button != null && button.getButtonData() == ButtonData.OK_DONE) {
				storeValue();
			}
			return null;
		});
	}

	private boolean inputIsValid() {
		if (numberOfInstanceComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText("Please select number of Instance");
			return false;
		}
		int counter = 4;
		for(FmmlxAttribute att : instanceGenerator.getObject().getAllAttributes()) {
			if(att.getLevel()==instanceGenerator.getObject().getLevel()-1){
				Node node = inputNode.get(counter);
				if(node instanceof ComboBox) {
					if( ((ComboBox) node).getSelectionModel().getSelectedItem()!=null){
						IValueGenerator valueGenerator = ((ComboBox<IValueGenerator>) node).getSelectionModel().getSelectedItem();
						if (valueGenerator == null ) {
							errorLabel.setText("Please input all required values");
							return false;
						} else if(valueGenerator.getValueGeneratorName().equals(StringValue.ValueGeneratorName.INCREMENT)){
							valueGenerator.generate(numberOfInstanceComboBox.getSelectionModel().getSelectedItem());
							int possible = valueGenerator.possibleGeneratedInstance();
							if(possible<numberOfInstanceComboBox.getSelectionModel().getSelectedItem()){
								errorLabel.setText(att.getName()+ "  : Maximum number of generated instance is " +possible );
								return false;
							}
						} else if(valueGenerator.possibleGeneratedInstance()<numberOfInstanceComboBox.getSelectionModel().getSelectedItem()) {
							int possible = valueGenerator.possibleGeneratedInstance();
							errorLabel.setText(att.getName()+ "  : Maximum number of generated instance is " +possible );
							return false;
						}
						counter++;
					}
				}
			}
		}
		errorLabel.setText("");
		return true;
	}

	public void storeValue() {
		instanceGenerator.setNumberOfInstance(numberOfInstanceComboBox.getSelectionModel().getSelectedItem());
		instanceGenerator.setSelectedParent(parentListView.getSelectionModel().getSelectedItems());
		HashMap<FmmlxAttribute, IValueGenerator> value= new HashMap<>();
		int counter = 4;
		for(FmmlxAttribute att : instanceGenerator.getObject().getAllAttributes()) {
			if(att.getLevel()==instanceGenerator.getObject().getLevel()-1){
				Node node = this.inputNode.get(counter);
				if(node instanceof ComboBox) {
					((ComboBox<IValueGenerator>) node).getSelectionModel().getSelectedItem().generate(numberOfInstanceComboBox.getSelectionModel().getSelectedItem());
					value.put(att, ((ComboBox<IValueGenerator>) node).getSelectionModel().getSelectedItem());
				} else {
					value.put(att, null);
				}
				counter++;
			}
		}
		instanceGenerator.setValue(value);
	}

	public ObservableList<FmmlxObject> getPossibleParentList() {
		return instanceGenerator.getDiagram().getAllPossibleParents(instanceGenerator.getObject().getLevel() - 1);
	}

	private void layoutContent() {
		Label ofLabel = new Label(LabelAndHeaderTitle.of);
		Label numberOfElementLabel = new Label(LabelAndHeaderTitle.numberOfInstances);
		Label parentLabel = new Label(LabelAndHeaderTitle.parent);

		TextField ofTextField = new TextField();
		ofTextField.setText(instanceGenerator.getObject().getName());
		ofTextField.setEditable(false);
		numberOfInstanceComboBox = new ComboBox<>(AllValueList.generateLevelListToThreshold(1, 5));
		numberOfInstanceComboBox.setConverter(new IntegerStringConverter());
		numberOfInstanceComboBox.setEditable(true);
		parentListView = initializeListView(getPossibleParentList(), SelectionMode.MULTIPLE);

		List<Node> labelNode = new ArrayList<>();
		List<Node> typeLabelNode = new ArrayList<>();
		inputNode = new ArrayList<>();
		List<Node> editButtonNode = new ArrayList<>();
		
		labelNode.add(ofLabel);
		labelNode.add(numberOfElementLabel);
		labelNode.add(new Label());
		labelNode.add(parentLabel);
		
		typeLabelNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		typeLabelNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		typeLabelNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		typeLabelNode.add(new Label());
		
		inputNode.add(ofTextField);
		inputNode.add(numberOfInstanceComboBox);
		inputNode.add(new Label());
		inputNode.add(parentListView);
		
		editButtonNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		editButtonNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		editButtonNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		editButtonNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		
		for (FmmlxAttribute att : instanceGenerator.getObject().getAllAttributes()) {

			if(instanceGenerator.getObject().getLevel()-1 == att.getLevel()){
				labelNode.add(new Label(att.getName()));
				typeLabelNode.add(new Label(": "+att.getType()));

				if(AllValueList.traditionalTypeList.contains(att.getType())) {
					ComboBox<IValueGenerator> comboBox = initializeComboBoxGeneratorList(instanceGenerator.getDiagram(), att);
					inputNode.add(comboBox);
					Button button = new InstanceGeneratorEditButton(StringValue.LabelAndHeaderTitle.EDIT, att);
					button.setOnAction(e -> comboBox.getSelectionModel().getSelectedItem().openDialog(instanceGenerator.getDiagram()));
					editButtonNode.add(button);
				} else {
					inputNode.add(new Label(" "));
					editButtonNode.add(new Label(" "));
				}
			}
		}
		
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(typeLabelNode,1);
		addNodesToGrid(inputNode,2);
		addNodesToGrid(editButtonNode, 3);
	}
	
}
