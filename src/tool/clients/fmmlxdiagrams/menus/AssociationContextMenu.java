package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;

public class AssociationContextMenu extends ContextMenu {

	private final FmmlxAssociation association;
	private final DiagramActions actions;

	public AssociationContextMenu(FmmlxAssociation association, DiagramActions actions) {
		setAutoHide(true);
		this.association = association;
		this.actions = actions;

		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeAssociation(association));

		getItems().addAll(removeItem);
	}
}
