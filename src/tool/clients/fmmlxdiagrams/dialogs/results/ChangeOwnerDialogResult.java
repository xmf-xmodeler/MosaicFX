package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class ChangeOwnerDialogResult extends DialogResult{
	
	private final PropertyType type;
	private FmmlxObject object;
	private FmmlxAttribute attribute;
	private FmmlxOperation operation;
	private FmmlxObject newOwner;

	public ChangeOwnerDialogResult(PropertyType type, FmmlxObject object, FmmlxAttribute fmmlxAttribute, FmmlxObject newOwner) {
		this.type = type;
		this.object = object;
		this.attribute = fmmlxAttribute;
		this.newOwner = newOwner;
	}

	public ChangeOwnerDialogResult(PropertyType type, FmmlxObject object, FmmlxOperation selectedItem,
			FmmlxObject newOwner) {
		this.type = type;
		this.object = object;
		this.operation = selectedItem;
		this.newOwner = newOwner;
	}
	public PropertyType getType() {
		return type;
	}
	public FmmlxObject getObject() {
		return object;
	}
	public Integer getNewOwner() {
		return newOwner.getId();
	}
	public FmmlxAttribute getAttribute() {
		return attribute;
	}
	public FmmlxOperation getOperation() {
		return operation;
	}


	public String getNewOwnerName() {
		return newOwner.getName();
	}
}
