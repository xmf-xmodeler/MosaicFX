package tool.clients.fmmlxdiagrams.fmmlxPalette;


import javafx.scene.control.TreeItem;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import xos.Value;

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
	
	public abstract Value asValue(String name);
	
	public abstract void delete();
	
	public abstract void deselect();
	
	public abstract FmmlxPalette getFmmlxPalette();
	
	public abstract FmmlxTool getToolLabelled(String label);

	public abstract void removeTool(String label);
	
	public abstract void removeFmmlxTool(FmmlxTool tool);

	public abstract FmmlxTool getFmmlxTool(String label);

	public abstract void newFmmlxTool(FmmlxDiagram diagram, String label, String toolId, boolean isEdge, String icon);

	public void setFmmlxPalette(FmmlxPalette fmmlxPalette) {
		this.fmmlxPalette = fmmlxPalette;
	}

}
