package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;

public class ChangeBodyDialogResult {
	
	private final FmmlxObject object;
	private final FmmlxOperation selectedItem;
	private final String body;

	public ChangeBodyDialogResult(FmmlxObject object, FmmlxOperation selectedItem, String text) {
		this.object = object;
		this.selectedItem = selectedItem;
		this.body = text;
	}

	public FmmlxObject getObject() {
		return object;
	}

	public FmmlxOperation getSelectedItem() {
		return selectedItem;
	}

	public String getBody() {
		return body;
	}
	
	

}
