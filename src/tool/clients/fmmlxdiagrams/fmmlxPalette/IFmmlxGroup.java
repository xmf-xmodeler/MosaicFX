package tool.clients.fmmlxdiagrams.fmmlxPalette;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import xos.Value;

public interface IFmmlxGroup {

	
	public Value asValue(String name);
	
	public void delete();
	
	public void deselect();
	
	public FmmlxPalette getFmmlxPalette();
	
	public FmmlxTool getToolLabelled(String label);

	public void removeTool(String label);
	
	public void removeFmmlxTool(FmmlxTool tool);

	public FmmlxTool getFmmlxTool(String label);

	public void newFmmlxTool(FmmlxDiagram diagram, String label, String toolId, boolean isEdge, String icon);
}
