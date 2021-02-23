package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxProperty;

public class RemoveDialogResult<Property extends FmmlxProperty> {
	
	private final FmmlxObject object;
	private final Property property;
	
	public RemoveDialogResult(FmmlxObject object, Property property) {
		this.object=object;
		this.property=property;
	}

	public FmmlxObject getObject() {
		return object;
	}

	public Property getProperty() {
		return property;
	}
	
	

}
