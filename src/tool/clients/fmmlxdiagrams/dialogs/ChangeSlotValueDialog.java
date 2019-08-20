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
	private String type;

	private TextField slotValueTextField;
	private CheckBox isExpressionCheckBox;
	private String multiplicity;

	public ChangeSlotValueDialog(FmmlxDiagram diagram, FmmlxObject object, FmmlxSlot slot) {
		super();
		this.diagram = diagram;
		this.object = object;
		this.slot = slot;

		getSlotType();

		DialogPane dialog = getDialogPane();
		dialog.setHeaderText(LabelAndHeaderTitle.changeSlotValue);
		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent();

		dialog.setContent(flow);

		if (!type.equals("String")) {
			isExpressionCheckBox.setSelected(true);
		}
		setResult();

		Platform.runLater(() -> slotValueTextField.requestFocus());
	}

	private void layoutContent() {
		List<Node> nodes = new ArrayList<>();
		TextField classNameTextField = new TextField(object.getName());
		classNameTextField.setDisable(true);
		TextField slotNameTextField = new TextField(slot.getName());
		slotNameTextField.setDisable(true);
		TextField slotTypeTextfield = new TextField(type);
		slotTypeTextfield.setDisable(true);
		TextField slotMultiplicityTextField = new TextField(multiplicity.toString());
		slotMultiplicityTextField.setDisable(true);
		slotValueTextField = new TextField(slot.getValue());
		isExpressionCheckBox = new CheckBox();

		nodes.add(new Label(LabelAndHeaderTitle.aClass));
		nodes.add(classNameTextField);
		nodes.add(new Label(LabelAndHeaderTitle.name));
		nodes.add(slotNameTextField);
		nodes.add(new Label((LabelAndHeaderTitle.type)));
		nodes.add(slotTypeTextfield);
		nodes.add(new Label(LabelAndHeaderTitle.multiplicity));
		nodes.add(slotMultiplicityTextField);
		nodes.add(new Label(LabelAndHeaderTitle.value));
		nodes.add(slotValueTextField);
		nodes.add(new Label(LabelAndHeaderTitle.expression));
		nodes.add(isExpressionCheckBox);

		addNodesToGrid(nodes);
	}

	private void getSlotType() {

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
				if (attribute.getMultiplicity() != null) {
					this.multiplicity = attribute.getMultiplicity().toString();
				} else {
					this.type = "";
					this.multiplicity = "";
				}
			}
		}
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {

				if (isExpressionCheckBox.isSelected()) {
					return new ChangeSlotValueDialogResult(object, slot, slotValueTextField.getText());
				} else {
					String slotValue = "\"" + slotValueTextField.getText() + "\"";
					return new ChangeSlotValueDialogResult(object, slot, slotValue);
				}
			}
			return null;
		});
	}
}
