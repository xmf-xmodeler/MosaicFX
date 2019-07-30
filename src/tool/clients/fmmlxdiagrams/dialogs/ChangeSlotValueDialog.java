package tool.clients.fmmlxdiagrams.dialogs;

import javafx.application.Platform;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeSlotValueDialogResult;

public class ChangeSlotValueDialog extends CustomDialog<ChangeSlotValueDialogResult> {

	private FmmlxObject object;
	private FmmlxSlot slot;

	private TextField classNameTextField;
	private TextField slotNameTextField;
	private TextField slotValueTextField;
	private CheckBox isExpressionCheckBox;

	public ChangeSlotValueDialog(FmmlxObject object, FmmlxSlot slot) {
		super();
		this.object = object;
		this.slot = slot;

		DialogPane dialog = getDialogPane();
		dialog.setHeaderText("Change slot value");
		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent();

		dialog.setContent(flow);
		setResult();

		Platform.runLater(() -> slotValueTextField.requestFocus());
	}

	private void layoutContent() {
		classNameTextField = new TextField(object.getName());
		classNameTextField.setDisable(true);
		slotNameTextField = new TextField(slot.getName());
		slotNameTextField.setDisable(true);
		slotValueTextField = new TextField(slot.getValue());
		slotValueTextField.requestFocus();
		isExpressionCheckBox = new CheckBox();

		grid.add(new Label("Class"), 0, 0);
		grid.add(classNameTextField, 1, 0);
		grid.add(new Label("Slot name"), 0, 1);
		grid.add(slotNameTextField, 1, 1);
		grid.add(new Label("Value"), 0, 2);
		grid.add(slotValueTextField, 1, 2);
		grid.add(new Label("is Expression"), 0, 3);
		grid.add(isExpressionCheckBox, 1, 3);


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
