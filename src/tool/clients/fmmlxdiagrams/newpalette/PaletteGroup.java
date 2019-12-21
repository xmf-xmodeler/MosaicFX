package tool.clients.fmmlxdiagrams.newpalette;

import javafx.scene.control.TreeItem;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public abstract class PaletteGroup extends TreeItem<PaletteTool> implements IPaletteGroup{

	public PaletteGroup(PaletteTool value) {
		super(value);
		
	}
	
	public abstract void populate(FmmlxDiagram diagram);

	public abstract void clearTreeItem();

	public abstract void clearTool();
	
}
