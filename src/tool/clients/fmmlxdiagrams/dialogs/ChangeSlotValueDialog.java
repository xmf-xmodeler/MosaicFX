package tool.clients.fmmlxdiagrams.dialogs;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeSlotValueDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog.LabelAndHeaderTitle;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ChangeSlotValueDialog extends CustomDialog<ChangeSlotValueDialogResult> {

	private final FmmlxDiagram diagram;
	private final FmmlxObject object;
	private final FmmlxSlot slot;
	private final String type;

	private TextField slotValueTextField;
	private ComboBox<String> slotValueComboBox;
	private CheckBox isExpressionCheckBox;
	
	private enum Mode {DEFAULT, STRING, ENUM}
	private final Mode mode;

	public ChangeSlotValueDialog(FmmlxDiagram diagram, FmmlxObject object, FmmlxSlot slot) {
		super();
		this.diagram = diagram;
		this.object = object;
		this.slot = slot;
		this.type = slot.getType(diagram);
		this.mode = "String".equals(this.type)?Mode.STRING:diagram.isEnum(this.type)?Mode.ENUM:Mode.DEFAULT;


		DialogPane dialog = getDialogPane();
		dialog.setHeaderText(LabelAndHeaderTitle.changeSlotValue);
		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent();

		dialog.setContent(flow);

		setResult();

		if (mode != Mode.ENUM) Platform.runLater(() -> slotValueTextField.requestFocus());
	}

	private void layoutContent() {
		List<Node> nodes = new ArrayList<>();
		TextField classNameTextField = new TextField(object.getName());
		classNameTextField.setDisable(true);
		TextField slotNameTextField = new TextField(slot.getName());
		slotNameTextField.setDisable(true);
		TextField slotTypeTextfield = new TextField(type);
		slotTypeTextfield.setDisable(true);
		Node inputItem = null; 
		if(mode != Mode.ENUM) {
			slotValueTextField = new TextField(slot.getValue());
			inputItem = slotValueTextField;
		} else {
			slotValueComboBox = new ComboBox<>();
			slotValueComboBox.getItems().addAll(diagram.getEnumItems(type));
			slotValueComboBox.getSelectionModel().select(slot.getValue());
			inputItem = slotValueComboBox;
		}
		isExpressionCheckBox = new CheckBox();

		nodes.add(new Label(LabelAndHeaderTitle.aClass));
		nodes.add(classNameTextField);
		nodes.add(new Label(LabelAndHeaderTitle.name));
		nodes.add(slotNameTextField);
		nodes.add(new Label((LabelAndHeaderTitle.type)));
		nodes.add(slotTypeTextfield);
		nodes.add(new Label(LabelAndHeaderTitle.value));
		nodes.add(inputItem);
		nodes.add(new Label(LabelAndHeaderTitle.expression));
		nodes.add(isExpressionCheckBox);

		addNodesToGrid(nodes);
		
		if(mode == Mode.DEFAULT) {
			isExpressionCheckBox.setSelected(true);
		}
		if(mode == Mode.ENUM) {
			isExpressionCheckBox.setDisable(true);
		}
	}

	/*private void getSlotType() {
		Vector<FmmlxAttribute> allAttributes = new Vector<>();
		FmmlxObject parent = object;
		while (parent != null) {
			allAttributes.addAll(parent.getOwnAttributes());
			allAttributes.addAll(parent.getOtherAttributes());
			parent = diagram.getObjectById(parent.getOf());
		}

		for (FmmlxAttribute attribute : allAttributes) {
			if (attribute.getName().equals(slot.getName())) {
				this.type = attribute.getType();
			}
		}
	}*/

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {

				if (isExpressionCheckBox.isSelected()) {
					return new ChangeSlotValueDialogResult(object, slot, slotValueTextField.getText());
				} else {
					if(mode != Mode.ENUM) {
						String slotValue = "\"" + slotValueTextField.getText() + "\"";
						return new ChangeSlotValueDialogResult(object, slot, slotValue);
					} else {
						String enumItem = type + "::" + slotValueComboBox.getSelectionModel().getSelectedItem();
						System.err.println("enumItem: "+enumItem);
						return new ChangeSlotValueDialogResult(object, slot, enumItem);
					}
				}
			}
			return null;
		});
	}
}
