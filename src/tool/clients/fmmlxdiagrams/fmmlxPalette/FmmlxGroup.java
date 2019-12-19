package tool.clients.fmmlxdiagrams.fmmlxPalette;

import javafx.scene.control.TreeItem;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public abstract class FmmlxGroup extends TreeItem {
	public FmmlxPalette fmmlxPalette;
	public String name;

	@SuppressWarnings("unchecked")
	public FmmlxGroup(String name) {
		super(name);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public FmmlxPalette getFmmlxPalette() {
		return getFmmlxPalette();
	}

	public abstract FmmlxTool getToolLabelled(String value);
	
	public abstract void clearTreeItem();
	
	public abstract void clearTool();

}
