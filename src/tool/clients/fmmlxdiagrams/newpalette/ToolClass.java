package tool.clients.fmmlxdiagrams.newpalette;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class ToolClass extends PaletteTool{
	

	public ToolClass(FmmlxDiagram fmmlxDiagram, String label, String id, int level, boolean isAbstract, String icon) {
		super(fmmlxDiagram, label, id, level, isAbstract, icon);	
	}


	@Override
	public void widgetSelected() {
		getDiagram().setNodeCreationType(getId());		
	}



}
