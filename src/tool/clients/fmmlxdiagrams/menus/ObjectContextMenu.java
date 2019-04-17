package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class ObjectContextMenu extends ContextMenu {

	public ObjectContextMenu(FmmlxObject object, DiagramActions actions) {
		setAutoHide(true);
		MenuItem addInstanceItem = new MenuItem("Add Instance");

		addInstanceItem.setOnAction(e -> actions.addInstanceDialog(object.getId()));

		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeAttributDialog());

		getItems().addAll(addInstanceItem, removeItem);

	}
}
