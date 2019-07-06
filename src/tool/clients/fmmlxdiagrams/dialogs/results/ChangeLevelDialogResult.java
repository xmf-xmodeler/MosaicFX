package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class ChangeLevelDialogResult extends DialogResult {

	private PropertyType type;
	private FmmlxObject object;
	private int currentlevel;
	private int newLevel;
	
	public ChangeLevelDialogResult(FmmlxObject object, Integer currentLevel, Integer newLevel, PropertyType type) {
		this.type = type;
		this.object = object;
		this.currentlevel= currentlevel;
		this.newLevel= newLevel;
	}

	public PropertyType getType() {
		return type;
	}

	public int getObjectId() {
		return object.getId();
	}

	public int getNewLevel() {
		return newLevel;
	}
	
	public int getOldLevel() {
		return currentlevel;
	}
	
	

}
