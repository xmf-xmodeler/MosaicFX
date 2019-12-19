package tool.clients.fmmlxdiagrams.fmmlxPalette;

import java.util.Vector;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import xos.Value;

public class FmmlxGroupClasses extends FmmlxGroup implements IFmmlxGroup{
	
	private Vector<FmmlxTool> tools = new Vector<FmmlxTool>();

	public FmmlxGroupClasses(String name) {
		super(name);
	}

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	public Value asValue(String name) {
		Value[] bs = new Value[tools.size() + 1];
	    bs[0] = new Value(name);
	    int i = 1;
	    for (FmmlxTool tool : tools) {
	      bs[i++] = new Value(new Value[] { new Value(tool.getId()), new Value(tool.getType()) });
	    }
	    return new Value(bs);
	}

	@Override
	public void delete() {
		for (FmmlxTool tool : tools) {
	    	tool.delete();
	    }
	}

	@Override
	public void deselect() {
		for (FmmlxTool tool : tools) {
			tool.reset();
		}
	}

	@Override
	public FmmlxPalette getFmmlxPalette() {
		return getFmmlxPalette();
	}

	@Override
	public FmmlxTool getToolLabelled(String label) {
		for (FmmlxTool tool : tools)
			if (tool.getLabel().equals(label)) return tool;
		return null;
	}

	@Override
	public void removeTool(String label) {
		FmmlxTool tool = getFmmlxTool(label);
	    removeFmmlxTool(tool);
	}

	@Override
	public void removeFmmlxTool(FmmlxTool tool) {
		if (tool != null) {
		      tools.remove(tool);
		      tool.delete();
		    }
	}

	@Override
	public FmmlxTool getFmmlxTool(String label) {
		for (FmmlxTool tool : tools)
		      if (tool.getLabel().equals(label)) return tool;
		    return null;
	}

	public void newFmmlxMetaclassTool(FmmlxDiagram diagram, String label, String toolId, boolean isEdge, String icon) throws InterruptedException {
		for(FmmlxTool tool : tools) { 
			if(tool.label.equals(label)) return;
		}	
	    FmmlxTool node = new NodeCreationFmmlxTool(diagram, label, toolId, icon);
	    tools.add(node);
	    getChildren().add(node.getButton());
	}

	public void newFmmlxNodeTool(FmmlxDiagram diagram, String label, String toolId, int level, boolean isEdge, String icon) throws InterruptedException {
		
		for(FmmlxTool tool : tools) { 
			if(tool.label.equals(label)) return;
		}

	    FmmlxTool node = new NodeCreationFmmlxTool(diagram, label, toolId, level, icon);
	    tools.add(node);
	    getChildren().add(node.getButton());
		
	}

	@Override
	public void clearTreeItem() {
		getChildren().clear();		
	}

	@Override
	public void clearTool() {
		tools.clear();
		
	}
	
	

}
