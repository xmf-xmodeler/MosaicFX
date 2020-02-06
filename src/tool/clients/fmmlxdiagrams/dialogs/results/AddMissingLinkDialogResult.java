package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxObject;

public class AddMissingLinkDialogResult {
	
	public final boolean createNew;
	public final FmmlxObject selection;

	public AddMissingLinkDialogResult(boolean createNew, FmmlxObject selection) {
		this.createNew = createNew; this.selection = selection;
	}

}
