package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxObject;

public class ChangeOfDialogResult {
	
	private final FmmlxObject object;
	private int oldOfId;
	private FmmlxObject newOf;
	
	public ChangeOfDialogResult(FmmlxObject object, int oldOfId, FmmlxObject newOf) {
		this.object = object;
		this.oldOfId = oldOfId;
		this.newOf = newOf;
	}

	public FmmlxObject getObject() {
		return object;
	}
	
	public FmmlxObject getNewOf() {
		return newOf;
	}

	public int getObjectId() {
		return object.getId();
	}

	public int getNewOfId() {
		return newOf.getId();
	}

	public int getOldOfId() {
		return oldOfId;
	}
	

}
