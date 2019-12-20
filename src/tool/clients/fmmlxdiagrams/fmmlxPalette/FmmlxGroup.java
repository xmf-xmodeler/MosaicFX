package tool.clients.fmmlxdiagrams.fmmlxPalette;

import javafx.scene.control.TreeItem;

public abstract class FmmlxGroup extends TreeItem<String> {
	public FmmlxPalette fmmlxPalette;
	public String name;

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
