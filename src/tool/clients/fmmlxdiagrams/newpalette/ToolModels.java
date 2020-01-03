package tool.clients.fmmlxdiagrams.newpalette;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class ToolModels extends PaletteTool{

	public ToolModels(FmmlxDiagram diagram, String label, String id, String icon) {
		super(diagram, label, id, 1000, false, icon);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void widgetSelected() {
		getDiagram().setNodeCreationType(getId());
	}

}
