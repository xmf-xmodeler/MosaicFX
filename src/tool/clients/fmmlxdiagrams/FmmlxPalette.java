package tool.clients.fmmlxdiagrams;

import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import tool.clients.diagrams.Group;
import tool.clients.diagrams.Tool;

public class FmmlxPalette {
	
	private TreeView<String> tree;
//	private HashMap<String,TreeItem<String>> gRoups = new HashMap<>();
	private HashMap<String,Group> groups = new HashMap<>();
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
}
