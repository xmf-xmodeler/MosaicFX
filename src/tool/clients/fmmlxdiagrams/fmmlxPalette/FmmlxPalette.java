package tool.clients.fmmlxdiagrams.fmmlxPalette;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;

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
	            	if(parent instanceof FmmlxGroup) {
	            		FmmlxGroup group = (FmmlxGroup) parent;
	            		FmmlxTool tool = group.getToolLabelled(newValue.getValue());
	            		if(tool != null) tool.widgetSelected();
	            	}
	            }
	        }

		  });
	  }
	
	public void init(FmmlxDiagram diagram) {
		
		Vector<String> groupNames = new Vector<String>();
		groupNames.add("Models");
		groupNames.add("Relationship");
		groupNames.add("Classes/Object");
		
		for (int i=0 ; i<3; i++) {
			diagram.newFmmlxGroup(groupNames.get(i));
			generateToggle(diagram, groupNames.get(i));
		}
	}
	
	private void generateToggle(FmmlxDiagram fmmlxDiagram, String name){
		
		FmmlxGroup fmmlxGroup = getFmmlxGroup(name);
		
		
		if(fmmlxGroup != null) {
			if(fmmlxGroup.getName().equals("Models")) {
				newTool(fmmlxDiagram, "Models", "Auxillary Classes", "auxilary", false, "resources/gif/Tools/Inherit.png");
				newTool(fmmlxDiagram, "Models", "getPackageName()", "getPackageName()", false, "resources/gif/Tools/Inherit.png");
			} else if(fmmlxGroup.getName().equals("Relationship")) {
				newTool(fmmlxDiagram, "Relationship", "Association", "association", true, "resources/gif/Association.gif");
				newTool(fmmlxDiagram, "Relationship", "Association Instance", "associationInstance", true, "resources/gif/Association.gif");
				newTool(fmmlxDiagram, "Relationship", "Specialization", "spezialization", true, "resources/gif/Tools/Inherit.gif");
				newTool(fmmlxDiagram, "Relationship", "Delegation", "delegation", true, "resources/gif/XCore/Delegation.png");
				
			} else if(fmmlxGroup.getName().equals("Classes/Object")) {
				newTool(fmmlxDiagram, "Classes/Object", "MetaClass", "metaClass", false, "resources/gif/Tools/Inherit.png");
				int maxLevel = fmmlxDiagram.getMaxLevel();
				for (int i = maxLevel ; i>=0 ; i--) {
					for (FmmlxObject tmp : fmmlxDiagram.getObjects()) {
						if (tmp.getLevel()==i) {
							newNodeTool(fmmlxDiagram, "Classes/Object", tmp.getName(), tmp.getId()+"", tmp.getLevel(), false, null);
						}
					}	
				}
				
			} else {
				System.err.println("cannot find group " + name);
			}
		}
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
			} else if (fmmlxGroup instanceof FmmlxGroupClasses) {
				try {
					((FmmlxGroupClasses) fmmlxGroup).newFmmlxMetaclassTool(fmmlxDiagram, label, toolId, isEdge, icon);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			} else {
				((FmmlxGroupModel) fmmlxGroup).newFmmlxNodeTool(fmmlxDiagram, label, toolId, isEdge, icon);
			}
			
		} else
			System.err.println("cannot find group " + groupName);
	}
	
	
	private void newNodeTool(FmmlxDiagram fmmlxDiagram, String groupName, String label, String toolId, int level, boolean isEdge,
			String icon) {
			FmmlxGroup fmmlxGroup = getFmmlxGroup(groupName);
			if (fmmlxGroup instanceof FmmlxGroupClasses) {
			try {
				((FmmlxGroupClasses) fmmlxGroup).newFmmlxNodeTool(fmmlxDiagram, label, toolId, level, isEdge, icon);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
		
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
	
	public void clearSelection() {
		if (Thread.currentThread().getName().equals("JavaFX Application Thread")) {
			tree.getSelectionModel().clearSelection();

		} else { // create a new Thread
			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
				tree.getSelectionModel().clearSelection();
				l.countDown();
			});
			try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void clearAllGroup() {
		Iterator groupIterator = fmmlxGroups.entrySet().iterator();
		
		while(groupIterator.hasNext()) {
			Map.Entry pair = (Entry) groupIterator.next();
			((FmmlxGroup) pair.getValue()).clearTreeItem();
			((FmmlxGroup) pair.getValue()).clearTool();
		}
	}

	public HashMap<String, FmmlxGroup> getFmmlxGroups() {
		return fmmlxGroups;
	}

	public void deselect() {
		// TODO Auto-generated method stub
		
	}

}
