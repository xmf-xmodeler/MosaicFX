package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import tool.clients.fmmlxdiagrams.DiagramActions;

public class DefaultContextMenu extends ContextMenu {

	public DefaultContextMenu(DiagramActions actions) {
		setAutoHide(true);
		
		MenuItem addClassItem = new MenuItem("Add Class");
		addClassItem.setOnAction(e -> actions.addMetaClassDialog());
		
		MenuItem addInstanceItem = new MenuItem("Add Instance");
		addInstanceItem.setOnAction(e -> actions.addInstanceDialog(0));
		
		// Submenu for association
		Menu associationMenu = new Menu("Association");
		MenuItem addAssociationItem = new MenuItem("Add Association");
		// TODO: add DiagramAction
		addAssociationItem.setOnAction(e -> System.out.println("Add association called!"));
		associationMenu.getItems().add(addAssociationItem);
		
		getItems().addAll(addClassItem, addInstanceItem, associationMenu);
	}
}
