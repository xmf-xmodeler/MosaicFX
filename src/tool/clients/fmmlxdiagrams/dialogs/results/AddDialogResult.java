package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class AddDialogResult extends DialogResult{
	
	private final String type;
	private final FmmlxDiagram diagram;
	private FmmlxObject object;
	
	public AddDialogResult(FmmlxDiagram diagram, FmmlxObject object, String type) {
		this.type = type;
		this.diagram = diagram;
		this.object = object; 
	}

	public String getType() {
		// TODO Auto-generated method stub
		return type;
	}
	
	

}
