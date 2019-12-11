package tool.clients.fmmlxdiagrams.fmmlxPalette;

import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import tool.clients.diagrams.Diagram;
import tool.clients.diagrams.Group;
import tool.clients.diagrams.Tool;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class FmmlxPalette extends ToolBar{
	
	private TreeView<String> tree;
//	private HashMap<String,TreeItem<String>> gRoups = new HashMap<>();
	private HashMap<String, FmmlxGroup> fmmlxGroups = new HashMap<>();
//	private HashMap<String,TreeItem<String>> buttons = new HashMap<>();
	private TreeItem<String> root;
	
	public FmmlxPalette(FmmlxDiagram diagram) {
		  tree = new TreeView<String>();
		  root = new TreeItem<String>("Root");
		  tree.setRoot(root);
		  root.setExpanded(true);
		  
		  tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {

	        @Override
	        public void changed(
	        		ObservableValue<? extends TreeItem<String>> observable, 
	        		TreeItem<String> oldValue,
	        		TreeItem<String> newValue) {
	        	
//	            TreeItem<String> selectedItem = (TreeItem<String>) newValue;
	            // Workaround:
	            // if leaf is selected, 
	            // then it must be part of a group
	        	if(newValue == null) return;
	            if(newValue.getChildren().isEmpty()) {
	            	TreeItem<String> parent = newValue.getParent();
	            	if(parent instanceof Group) {
	            		Group group = (Group) parent;
	            		Tool tool = group.getToolLabelled(newValue.getValue());
	            		if(tool != null) tool.widgetSelected();
	            	}
	            }
	        }

		  });
	  }
	public void init(FmmlxDiagram diagram) {
		diagram.newFmmlxGroup("FmmlxDiagram");
		newFmmlxTool(diagram, "Diagram", "Select", "Select", false, "resources/gif/Select.gif");
	}
	
	private void newFmmlxTool(FmmlxDiagram diagram, String groupName, String label, String toolId, boolean isEdge,
			String icon) {
		FmmlxGroup group = getFmmlxGroup(groupName);
		if (group != null) {
			group.newFmmlxTool(diagram, label, toolId, isEdge, icon);
		} else
			System.err.println("cannot find group " + groupName);
		
	}
	public boolean hasGroup(String name) {
		return getFmmlxGroup(name) != null;
	}
	
	private FmmlxGroup getFmmlxGroup(String name) {
		return fmmlxGroups.get(name);
	}
	
	public void newGroup(String name) {
		FmmlxGroup group = new FmmlxGroup(name);
		if(root.getChildren().size() <= 1 ) {
			group.setExpanded(true);
		}
		root.getChildren().add(group);
		fmmlxGroups.put(name, group);
	}
	
	public void deleteGroup(String name) {
	    FmmlxGroup group = getFmmlxGroup(name);
	    if (group != null) {
	      fmmlxGroups.remove(group);
	      group.delete();
	    }
	  }
	
	 public void deselect() {
	  }
	
	public void newToggle(FmmlxDiagram diagram, String groupName, String label, String toolId, boolean state, String iconTrue, String iconFalse) {
	    FmmlxGroup group = getFmmlxGroup(groupName);
	    if (group != null) {
	      group.newToggle(diagram, label, toolId, state, iconTrue, iconFalse);
	    } else System.err.println("cannot find group " + groupName);
	  }
	public void newAction(FmmlxDiagram fmmlxDiagram, String groupName, String label, String toolId, String icon) {
		FmmlxGroup fmmlxGroup = getFmmlxGroup(groupName);
	    if (fmmlxGroup != null) {
	      fmmlxGroup.newAction(fmmlxDiagram, label, toolId, icon);
	    } else System.err.println("cannot find group " + groupName);
	}


}
