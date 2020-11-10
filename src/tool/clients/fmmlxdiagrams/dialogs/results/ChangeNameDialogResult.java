package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class ChangeNameDialogResult {

	private final PropertyType type;
	private FmmlxObject object;
	private String oldName;
	private String newName;

	// Result to change class name 
	public ChangeNameDialogResult(FmmlxObject object, String newName) {
		this.type = PropertyType.Class;
		this.object = object;
		this.newName = newName;
	}

	// Result to change attribute
	public ChangeNameDialogResult(PropertyType type, FmmlxObject object, FmmlxAttribute fmmlxAttribute, String newName) {
		this.type = type;
		this.object = object;
		this.oldName = fmmlxAttribute.getName();
		this.newName = newName;
	}
	
	//Result to change operation
	public ChangeNameDialogResult(PropertyType type2, FmmlxObject object2, FmmlxOperation fmmlxOperation, String newName) {
		this.type = type2;
		this.object = object2;
		this.oldName = fmmlxOperation.getName();
		this.newName = newName;
	}

	public int getObjectId() {
		return object.getId();
	}

	public PropertyType getType() {
		return type;
	}

	public String getNewName() {
		return newName;
	}

	public String getOldName() {
		return oldName;
	}

	public String toString() {
		return ("Type:" + type + " Object: " + object.getName() + " Old: " + oldName + " New: " + newName);
	}

	public String getObjectName() {
		return object.getName();
	}
}
