package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.DialogType;

public class AddDialogResult extends DialogResult{
	
	private final DialogType type;
	private final FmmlxDiagram diagram;
	private FmmlxObject object;
	
	public AddDialogResult(FmmlxDiagram diagram, FmmlxObject object, DialogType type) {
		this.type = type;
		this.diagram = diagram;
		this.object = object; 
	}

	public DialogType getType() {
		// TODO Auto-generated method stub
		return type;
	}
	
	

}
