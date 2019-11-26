package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxLink;

public class AssociationInstanceContextMenu extends ContextMenu {

	private final FmmlxLink instance;
	private final DiagramActions actions;

	public AssociationInstanceContextMenu(FmmlxLink instance, DiagramActions actions) {
		setAutoHide(true);
		this.instance = instance;
		this.actions = actions;

		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeAssociationInstance(instance));

		getItems().addAll(removeItem);
	}
}
