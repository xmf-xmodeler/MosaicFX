package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class ChangeTypeDialogResult extends DialogResult{
	
	private final PropertyType type;
	private FmmlxObject object;
	private String oldType;
	private String newType;
	
	
	public ChangeTypeDialogResult(FmmlxObject object, PropertyType type, String oldType, String newType) {
		this.type= type;
		this.object = object;
		this.oldType = oldType;
		this.newType = newType;
		
		
	}

	public PropertyType getType() {
		return type;
	}

	public FmmlxObject getObject() {
		return object;
	}


	public String getOldType() {
		return oldType;
	}


	public String getNewType() {
		return newType;
	}

}
