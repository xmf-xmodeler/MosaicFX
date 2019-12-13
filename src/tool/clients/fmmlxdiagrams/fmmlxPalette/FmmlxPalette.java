package tool.clients.fmmlxdiagrams.fmmlxPalette;

import java.util.HashMap;
import java.util.Vector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import tool.clients.diagrams.Group;
import tool.clients.diagrams.Tool;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class FmmlxPalette{
	
	private TreeView<String> tree;
	private HashMap<String, FmmlxGroup> fmmlxGroups = new HashMap<>();
	private TreeItem<String> root;

	
	public FmmlxPalette(FmmlxDiagram diagram) {
		  tree = new TreeView<String>();
		  root = new TreeItem<String>();
		  tree.setRoot(root);
		  root.setExpanded(true);
		  
		  tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {

	        @Override
	        public void changed(
	        		ObservableValue<? extends TreeItem<String>> observable, 
	        		TreeItem<String> oldValue,
	        		TreeItem<String> newValue) {
	        	
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
		
		Vector<String> groupNames = new Vector<String>();
		groupNames.add("Models");
		groupNames.add("Relationsship");
		groupNames.add("Classes/Object");
		
		for (int i=0 ; i<3; i++) {
			diagram.newFmmlxGroup(groupNames.get(i));
			generateToogle(diagram, groupNames.get(i));
		}
	}
	
	private void generateToogle(FmmlxDiagram fmmlxDiagram, String name){
		
		FmmlxGroup fmmlxGroup = getFmmlxGroup(name);
		if(fmmlxGroup != null) {
			if(fmmlxGroup.getName().equals("Models")) {
				newTool(fmmlxDiagram, "Models", "Auxillary Classes", "auxilary", false, "resources/gif/Tools/Inherit.png");
				newTool(fmmlxDiagram, "Models", "getPackageName()", "getPackageName()", false, "resources/gif/Tools/Inherit.png");
			} else if(fmmlxGroup.getName().equals("Relationsship")) {
				newTool(fmmlxDiagram, "Relationsship", "Association", "association", false, "resources/gif/Tools/Inherit.png");
				newTool(fmmlxDiagram, "Relationsship", "Specialization", "spezialization", false, "resources/gif/Tools/Inherit.png");
				newTool(fmmlxDiagram, "Relationsship", "Delegation", "delegation", false, "resources/gif/Tools/Inherit.png");
				
			} else if(fmmlxGroup.getName().equals("Classes/Object")) {
				newTool(fmmlxDiagram, "Classes/Object", "MetaClass", "metaClass", false, "resources/gif/Tools/Inherit.png");
			}
		} else
			System.err.println("cannot find group " + name);
	}
	
	public boolean hasGroup(String name) {
		return getFmmlxGroup(name) != null;
	}
	
	private FmmlxGroup getFmmlxGroup(String name) {
		return fmmlxGroups.get(name);
	}
	
	public void newFmmlxGroup(String name) {
		FmmlxGroup fmmlxGroup;
		if(name.equals("Relationsship")) {
			fmmlxGroup = new FmmlxGroupRelationsship(name);
		} else if (name.equals("Classes/Object")){
			fmmlxGroup = new FmmlxGroupClasses(name);
		} else {
			fmmlxGroup = new FmmlxGroupModel(name);
		}
		if(root.getChildren().size() <= 1 ) {
			fmmlxGroup.setExpanded(true);
		}
		root.getChildren().add(fmmlxGroup);
		fmmlxGroups.put(name, fmmlxGroup);
	}
	
	public void deleteGroup(String name) {
		fmmlxGroups.remove(name);
	}
	
	public void newTool(FmmlxDiagram fmmlxDiagram, String groupName, String label, String toolId, boolean isEdge, String icon) {
		FmmlxGroup fmmlxGroup = getFmmlxGroup(groupName);
		if (fmmlxGroup != null) {
			if(fmmlxGroup instanceof FmmlxGroupRelationsship) {
				((FmmlxGroupRelationsship) fmmlxGroup).newFmmlxTool(fmmlxDiagram, label, toolId, isEdge, icon);
			}
			
		} else
			System.err.println("cannot find group " + groupName);
	}
	
	public void newAction(FmmlxDiagram fmmlxDiagram, String groupName, String label, String toolId, String icon) {
		FmmlxGroup fmmlxGroup = getFmmlxGroup(groupName);
	    if (fmmlxGroup != null) {
	      //fmmlxGroup.newAction(fmmlxDiagram, label, toolId, icon);
	    } else System.err.println("cannot find group " + groupName);
	}
	
	public TreeView<String> getToolBar() {
		return tree;
	}


}
