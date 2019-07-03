package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class ChangeOwnerDialogResult extends DialogResult{
	
	private final PropertyType type;
	private FmmlxObject object;
	private Integer oldOwnerID;
	private Integer newOwnerID;

	public ChangeOwnerDialogResult(FmmlxObject object, PropertyType type, Integer oldOwnerID, Integer newOwnerID) {
		this.type = type;
		this.object = object;
		this.oldOwnerID= oldOwnerID;
		this.newOwnerID= newOwnerID;
	}
	public PropertyType getType() {
		return type;
	}
	public FmmlxObject getObject() {
		return object;
	}
	public Integer getOldOwnerID() {
		return oldOwnerID;
	}
	public Integer getNewOwnerID() {
		return newOwnerID;
	}
	
	

}
