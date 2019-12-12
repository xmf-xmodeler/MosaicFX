package tool.clients.fmmlxdiagrams.fmmlxPalette;

import java.util.Vector;

import javafx.scene.control.TreeItem;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import xos.Value;

public class FmmlxGroup extends TreeItem {
	private FmmlxPalette fmmlxPalette;
	private String name;
	Vector<FmmlxTool> tools = new Vector<FmmlxTool>();

	@SuppressWarnings("unchecked")
	public FmmlxGroup(String name) {
		super(name);
		this.name = name;
	}
	
	public String getName() {
	    return name;
	}
	
	public Value asValue(String name) {
	    Value[] bs = new Value[tools.size() + 1];
	    bs[0] = new Value(name);
	    int i = 1;
	    for (FmmlxTool tool : tools) {
	      bs[i++] = new Value(new Value[] { new Value(tool.getId()), new Value(tool.getType()) });
	    }
	    return new Value(bs);
	}
	
	public void delete() {
		for (FmmlxTool tool : tools) {
	    	tool.delete();
	    }
	}
	
	public void deselect() {
		for (FmmlxTool tool : tools) {
			tool.reset();
		}
	}
	
	public FmmlxPalette getPalette() {
		return fmmlxPalette;
	}
	
	public FmmlxTool getToolLabelled(String label) {
		for (FmmlxTool tool : tools)
			if (tool.getLabel().equals(label)) return tool;
		return null;
	}

	private void removeTool(String label) {
		FmmlxTool tool = getFmmlxTool(label);
	    removeFmmlxTool(tool);
		
	}
	
	void removeFmmlxTool(FmmlxTool tool) {
	    if (tool != null) {
	      tools.remove(tool);
	      tool.delete();
	    }
	  }

	private FmmlxTool getFmmlxTool(String label) {
		// TODO Auto-generated method stub
		return null;
	}

	public void newFmmlxTool(FmmlxDiagram diagram, String label, String toolId, boolean isEdge, String icon) {
		for(FmmlxTool tool : tools) { // Quickfix for Copenhagen/MULTI2018, prevent repeated creation of tools
			if(tool.label.equals(label)) return;
		}
		  
	    if (isEdge) {
	    	FmmlxTool edge = new EdgeCreationFmmlxTool(diagram, label, toolId, icon);
	        tools.add(edge); 
	        getChildren().add(edge.getButton());
	    } else {
	    	FmmlxTool node = new NodeCreationFmmlxTool(diagram, label, toolId, icon);
	    	tools.add(node);
	        getChildren().add(node.getButton());
	    }		
	}

}
