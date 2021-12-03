package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxLink;

public class AssociationInstanceContextMenu extends ContextMenu {

	public AssociationInstanceContextMenu(final FmmlxLink instance, final DiagramActions actions) {
		setAutoHide(true);

		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeAssociationInstance(instance));

		getItems().addAll(removeItem);
	}
}
