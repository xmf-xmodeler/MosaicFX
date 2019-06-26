package tool.clients.fmmlxdiagrams.dialogs;

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

		// TODO: set Validation

	}

	private void layoutContent() {
		classNameTextField = new TextField(object.getName());
		classNameTextField.setDisable(true);
		slotNameTextField = new TextField(slot.getName());
		slotNameTextField.setDisable(true);
		slotValueTextField = new TextField(slot.getValue());

		grid.add(new Label("Class"), 0, 0);
		grid.add(classNameTextField, 1, 0);
		grid.add(new Label("Slot name"), 0, 1);
		grid.add(slotNameTextField, 1, 1);
		grid.add(new Label("Value"), 0, 2);
		grid.add(slotValueTextField, 1, 2);


	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				return new ChangeSlotValueDialogResult(object, slot, slotValueTextField.getText());
			}
			return null;
		});
	}
}
