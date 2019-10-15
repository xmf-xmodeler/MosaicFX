package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class RemoveDialogResult extends DialogResult {
	
	private final PropertyType type;
	private FmmlxObject object;
	private FmmlxAttribute attribute;
	private FmmlxOperation operation;
	private FmmlxAssociation association;
	
	public RemoveDialogResult(PropertyType type, FmmlxObject object) {
		
		this.type=type;
		this.object=object;
	}

	public RemoveDialogResult(PropertyType type, FmmlxObject object, FmmlxAttribute selectedItem) {
		this.type=type;
		this.object=object;
		this.attribute=selectedItem;
		
	}

	public RemoveDialogResult(PropertyType type, FmmlxObject object, FmmlxOperation selectedItem) {
		this.type=type;
		this.object=object;
		this.operation=selectedItem;
	}

	public PropertyType getType() {
		return type;
	}

	public FmmlxObject getObject() {
		return object;
	}

	public FmmlxAttribute getAttribute() {
		return attribute;
	}

	public FmmlxOperation getOperation() {
		return operation;
	}

	public FmmlxAssociation getAssociation() {
		return association;
	}
	
	

}
