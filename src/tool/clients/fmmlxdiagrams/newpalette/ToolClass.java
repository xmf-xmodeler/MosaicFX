package tool.clients.fmmlxdiagrams.newpalette;

import tool.clients.fmmlxdiagrams.FmmlxDiagramView;

public class ToolClass extends PaletteTool{
	

	public ToolClass(FmmlxDiagramView fmmlxDiagram, String name, String path, int level, boolean isAbstract, String icon) {
		super(fmmlxDiagram, name, path, level, isAbstract, icon);
	}


	@Override
	public void widgetSelected() {
		getDiagram().setNodeCreationType(getId());
	}
}
