package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;
import tool.helper.FXAuxilary.JavaFxMenuAuxiliary;

public class DefaultContextMenu extends ContextMenu {

	public DefaultContextMenu(DiagramViewPane view) {
		FmmlxDiagram diagram = view.getDiagram();
		DiagramActions actions = diagram.getActions();
		setAutoHide(true);

		Menu addMenu = new Menu("Add");
		JavaFxMenuAuxiliary.addMenuItem(addMenu, "Class...", e -> actions.addMetaClassDialog(view));
		JavaFxMenuAuxiliary.addMenuItem(addMenu, "Instance...", e -> actions.addInstanceDialog(view));
		JavaFxMenuAuxiliary.addMenuItem(addMenu, "Association...", e -> actions.addAssociationDialog(null, null));

		/*TS 2023-03-29: This code block is commented out because the assumption is, that the functionality is right now not used
		 * 
		 * Menu filterObjectsMenu = new Menu("Filter Objects (BETA)");
		 * JavaFxMenuAuxiliary.addMenuItem(filterObjectsMenu, "Show All", e ->
		 * actions.showAll()); JavaFxMenuAuxiliary.addMenuItem(filterObjectsMenu, "Filter by Level...", e -> actions.showCertainLevel());
		 */
		Menu searchMenu = new Menu("Search for");
		JavaFxMenuAuxiliary.addMenuItem(searchMenu, "Implementations...", e -> actions.openFindImplementationDialog());
		JavaFxMenuAuxiliary.addMenuItem(searchMenu, "Classes...", e -> actions.openFindClassDialog());
		JavaFxMenuAuxiliary.addMenuItem(searchMenu, "Senders...", e -> actions.openFindSendersDialog());

		getItems().addAll(addMenu, searchMenu);
	}
}
