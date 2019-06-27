package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.DialogType;

public class ChangeLevelDialogResult extends DialogResult{
	
	private final DialogType type;
	private FmmlxObject object;
	private int currentlevel;
	private int newLevel;
	
	public ChangeLevelDialogResult(FmmlxObject object, Integer currentLevel, Integer newLevel, DialogType type) {
		// TODO Auto-generated constructor stub
		this.type = type;
		this.object = object;
		this.currentlevel= currentlevel;
		this.newLevel= newLevel;
	}

	public DialogType getType() {
		// TODO Auto-generated method stub
		return type;
	}

}
