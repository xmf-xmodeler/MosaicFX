package tool.clients.dialogs;

import java.util.Vector;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreeDialog extends Dialog<TreeElement> {

	private Vector<TreeItem<String>> selectedTreeItems = new Vector<TreeItem<String>>();
	
	private TreeView<String> treeView = null;
	
	public TreeDialog(TreeElement root, String title, Vector<TreeElement> expand, Vector<TreeElement> disable,
			Vector<TreeElement> selected) {
		super();
		
		if(expand == null){
			expand = new Vector<TreeElement>();
		}
		if(disable == null){
			disable = new Vector<TreeElement>();
		}
		if(selected == null){
			selected = new Vector<TreeElement>();
		}
		
		 final DialogPane dialogPane = getDialogPane();
	        
	     setTitle(title);
	     
	     TreeItem<String> rootItem = createTree(null, root, expand, disable, selected);
		 treeView = new TreeView<String>(rootItem);
		 for(TreeItem<String> sti:selectedTreeItems){
			 treeView.getSelectionModel().select(sti);
		 }
	     dialogPane.setContent(treeView);
	     dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
	     
	     setResultConverter((dialogButton) -> {
	           if (dialogButton != null && dialogButton.getButtonData() == ButtonData.OK_DONE){
	        	   TreeElement result = root.find(treeView.getSelectionModel().getSelectedItem().getValue());
	        	   return result;
	           }else{
	        	   return null;
	           }
	        });
	}
	
	public TreeItem<String> createTree(TreeItem<String> parent, TreeElement node,Vector<TreeElement> expand, Vector<TreeElement> disable,
			Vector<TreeElement> selected){
		TreeItem<String> item = new TreeItem<String>(node.toString());
		if(expand.contains(node)){
			item.setExpanded(true);
		}
		if(selected.contains(node)){
			selectedTreeItems.add(item);
		}
		if (parent !=null){
			parent.getChildren().add(item);
		}
		for(TreeElement te : node.getChildren()){
			createTree(item,te,expand, disable, selected);
		}
		return item;
	}
}
