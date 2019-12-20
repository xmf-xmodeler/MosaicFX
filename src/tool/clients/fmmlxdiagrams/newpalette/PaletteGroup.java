package tool.clients.fmmlxdiagrams.newpalette;

import javafx.scene.control.TreeItem;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public abstract class PaletteGroup extends TreeItem<FmmlxTool> implements IPaletteGroup{

	public PaletteGroup(FmmlxTool value) {
		super(value);
	}
	
	public abstract void populate(FmmlxDiagram diagram);

	public abstract void clearTreeItem();

	public abstract void clearTool();
	
}
