package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
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
		Menu associationMenu = new Menu("Association");
		MenuItem addAssociationItem = new MenuItem("Add Association");
		// TODO: add DiagramAction
		addAssociationItem.setOnAction(e -> System.out.println("Add association called!"));
		associationMenu.getItems().add(addAssociationItem);

		MenuItem myMenuItem = new MenuItem("Surprise");
		myMenuItem.setOnAction(e -> actions.surpriseDialog());
		getItems().addAll(addClassItem, addInstanceItem, associationMenu, myMenuItem);
	}
}