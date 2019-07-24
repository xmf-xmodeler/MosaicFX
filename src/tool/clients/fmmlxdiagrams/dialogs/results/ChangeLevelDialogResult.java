package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class ChangeLevelDialogResult extends DialogResult {

	private PropertyType type;
	private FmmlxObject object;
	private int currentlevel;
	private int newLevel;
	private String name;
	
	public ChangeLevelDialogResult(FmmlxObject object, String name, Integer currentLevel, Integer newLevel, PropertyType type) {
		this.type = type;
		this.object = object;
		this.currentlevel = currentLevel;
		this.newLevel= newLevel;
		this.name= name;
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
	
	public String getName() {
		return name;
	}
	
	public int getOldLevel() {
		return currentlevel;
	}
	
	

}
