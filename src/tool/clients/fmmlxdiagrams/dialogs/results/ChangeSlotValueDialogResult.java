package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxSlot;

public class ChangeSlotValueDialogResult extends DialogResult {
	private FmmlxObject object;
	private FmmlxSlot slot;
	private String newValue;

	public ChangeSlotValueDialogResult(FmmlxObject object, FmmlxSlot slot, String newValue) {
		this.object = object;
		this.slot = slot;
		this.newValue = newValue;
	}

	public FmmlxObject getObject() {
		return object;
	}

	public FmmlxSlot getSlot() {
		return slot;
	}

	public String getNewValue() {
		return newValue;
	}
}
