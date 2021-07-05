package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxObject;

public class ChangeOfDialogResult {
	
	private final FmmlxObject object;
	private String oldOfName;
	private FmmlxObject newOf;
	
	public ChangeOfDialogResult(FmmlxObject object, String oldOfName, FmmlxObject newOf) {
		this.object = object;
		this.oldOfName = oldOfName;
		this.newOf = newOf;
	}

	public FmmlxObject getObject() {
		return object;
	}
	
	public FmmlxObject getNewOf() {
		return newOf;
	}

	public String getOldOfName() {
		return oldOfName;
	}

}
