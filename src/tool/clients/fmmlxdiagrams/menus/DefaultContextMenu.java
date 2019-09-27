package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import tool.clients.fmmlxdiagrams.DiagramActions;

public class DefaultContextMenu extends ContextMenu {

	public DefaultContextMenu(DiagramActions actions) {
		setAutoHide(true);

		MenuItem addClassItem = new MenuItem("Add Class");
		addClassItem.setOnAction(e -> actions.addMetaClassDialog());
		MenuItem addInstanceItem = new MenuItem("Add Instance");
		addInstanceItem.setOnAction(e -> actions.addInstanceDialog());
		
		// Submenu for association
//		Menu associationMenu = new Menu("Association");
		MenuItem addAssociationItem = new MenuItem("Add Association");
		addAssociationItem.setOnAction(e -> actions.addAssociationDialog(null, null));
//		associationMenu.getItems().add(addAssociationItem);

		getItems().addAll(addClassItem, addInstanceItem, addAssociationItem);
	}
}