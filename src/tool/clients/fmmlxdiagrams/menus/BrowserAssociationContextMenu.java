package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class BrowserAssociationContextMenu extends ContextMenu {

	private final FmmlxObject object;
	private final FmmlxAssociation association;
	private final DiagramActions actions;
	
	public BrowserAssociationContextMenu(ListView<FmmlxObject> oListView, ListView<FmmlxAssociation> aListView, AbstractPackageViewer packageViewer){
		this.actions=packageViewer.getActions();
		this.object=oListView.getSelectionModel().getSelectedItem();
		this.association=aListView.getSelectionModel().getSelectedItem();
		setAutoHide(true);
		
		if(object != null && object.getLevel() >= 1) {
			MenuItem addItem = new MenuItem("Add Association");
			addItem.setOnAction(e -> actions.addAssociationDialog(object,null));
			
			MenuItem editItem = new MenuItem("Edit Association");
			editItem.setOnAction(e-> actions.editAssociationDialog(association));
			getItems().addAll(addItem,editItem);
		}
		
	}
}
