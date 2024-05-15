package tool.clients.fmmlxdiagrams.newpalette;

import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;

public class ToolClass extends PaletteTool{
	

	public ToolClass(FmmlxDiagram fmmlxDiagram, String name, String path, int level, boolean isAbstract, String icon) {
		super(fmmlxDiagram, name, path, level, isAbstract, icon);
	}


	@Override
	public void widgetSelected() {
		getDiagram().setNodeCreationType(getId());
	}
}
