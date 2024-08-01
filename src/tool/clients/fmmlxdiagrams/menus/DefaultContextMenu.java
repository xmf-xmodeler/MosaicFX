package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram.DiagramCanvas;
import tool.helper.auxilaryFX.JavaFxMenuAuxiliary;
import tool.xmodeler.XModeler;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.tool_intro.ToolIntroductionTasks;

public class DefaultContextMenu extends ContextMenu {

	public DefaultContextMenu(DiagramCanvas view) {
		FmmlxDiagram diagram = view.getDiagram();
		DiagramActions actions = diagram.getActions();
		setAutoHide(true);

		Menu addMenu = buildAddMenu(view, diagram, actions);

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

		MenuItem unhideItem = new MenuItem("Hide/Unhide Elements...");
		unhideItem.setOnAction(e -> actions.showUnhideElementsDialog());		
		
		Menu enumerationMenu = new Menu("Enumerations");
		MenuItem createEnumeration = new MenuItem("Create Enumeration...");
		createEnumeration.setOnAction(e -> actions.addEnumerationDialog());
		MenuItem editEnumeration = new MenuItem("Edit Enumeration...");
		editEnumeration.setOnAction(e -> actions.editEnumerationDialog("edit_element",""));
		MenuItem deleteEnumeration = new MenuItem("Delete Enumeration...");
		deleteEnumeration.setOnAction(e -> actions.deleteEnumerationDialog());
		//MenuItem packageListView = new MenuItem("Class Browser (BETA)...");
		//packageListView.setOnAction(e -> actions.openClassBrowserStage(false));
		enumerationMenu.getItems().addAll(createEnumeration, editEnumeration, deleteEnumeration);

		MenuItem addAssocType = new MenuItem("Add Association Type...");
		addAssocType.setOnAction(e -> actions.associationTypeDialog(null));
			
		addMenues(diagram, addMenu, searchMenu, unhideItem, enumerationMenu, addAssocType);
	}

	private Menu buildAddMenu(DiagramCanvas view, FmmlxDiagram diagram, DiagramActions actions) {
		Menu addMenu = new Menu("Add");
		JavaFxMenuAuxiliary.addMenuItem(addMenu, "Class...", e -> actions.addMetaClassDialog(view));
		if (diagram.getRootPane().isInToolIntroductionMode()) {
			if (ToolIntroductionTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName()) > 3) {
				JavaFxMenuAuxiliary.addMenuItem(addMenu, "Association...", e -> actions.addAssociationDialog(null, null));
			}			
		} else {
			JavaFxMenuAuxiliary.addMenuItem(addMenu, "Association...", e -> actions.addAssociationDialog(null, null));
		}
		JavaFxMenuAuxiliary.addMenuItem(addMenu, "Note...", e -> diagram.activateNoteCreationMode());
		return addMenu;
	}

	private void addMenues(FmmlxDiagram diagram, Menu addMenu, Menu searchMenu, MenuItem unhideItem,
			Menu enumerationMenu, MenuItem addAssocType) {
		getItems().addAll(addMenu, searchMenu, unhideItem);
		if (diagram.getRootPane().isInToolIntroductionMode()) {
			if (ToolIntroductionTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName()) > 6) {
				getItems().addAll(enumerationMenu);			
			}			
		} else {
			getItems().addAll(enumerationMenu);
		}
		if (XModeler.isAlphaMode()) {
			getItems().addAll(new SeparatorMenuItem(), addAssocType);
		}
	}
}