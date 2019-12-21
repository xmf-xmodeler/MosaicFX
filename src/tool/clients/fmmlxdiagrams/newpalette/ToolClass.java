package tool.clients.fmmlxdiagrams.newpalette;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class ToolClass extends PaletteTool{
	

	public ToolClass(FmmlxDiagram fmmlxDiagram, String label, String id, int level, String icon) {
		super(fmmlxDiagram, label, id, level, icon);
		
	}


	public int getLevel() {
		return level;
	}


	@Override
	public void widgetSelected() {
		diagram.deselectPalette();
		diagram.setNodeCreationType(getId());		
	}



}
