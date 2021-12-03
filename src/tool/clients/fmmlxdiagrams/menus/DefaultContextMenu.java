package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;

public class DefaultContextMenu extends ContextMenu {

	public DefaultContextMenu(DiagramViewPane view) {
		FmmlxDiagram diagram = view.getDiagram();
		DiagramActions actions = diagram.getActions();
		setAutoHide(true);
		
		Menu addMenu = new Menu("Add");

		MenuItem addClassItem = new MenuItem("Class");
		addClassItem.setOnAction(e -> actions.addMetaClassDialog(view));
		MenuItem addInstanceItem = new MenuItem("Instance");
		addInstanceItem.setOnAction(e -> actions.addInstanceDialog(view));
		MenuItem addAssociationItem = new MenuItem("Association");
		addAssociationItem.setOnAction(e -> actions.addAssociationDialog(null, null));
		addMenu.getItems().addAll(addClassItem, addInstanceItem, addAssociationItem);
		
		Menu levelMenu = new Menu("Levels");
		MenuItem levelRaiseAllItem = new MenuItem("Raise all");
		levelRaiseAllItem.setOnAction(e -> actions.levelRaiseAll());
		MenuItem levelLowerAllItem = new MenuItem("Lower all");
		levelLowerAllItem.setOnAction(e -> actions.levelLowerAll());
		levelMenu.getItems().addAll(levelRaiseAllItem, levelLowerAllItem);
	
		Menu enumerationMenu = new Menu("Enumerations");
		MenuItem createEnumeration = new MenuItem("Create Enumeration");
		createEnumeration.setOnAction(e -> actions.addEnumerationDialog());
		MenuItem editEnumeration = new MenuItem("Edit Enumeration");
		editEnumeration.setOnAction(e -> actions.editEnumerationDialog("edit_element",""));
		MenuItem deleteEnumeration = new MenuItem("Delete Enumeration");
		deleteEnumeration.setOnAction(e -> actions.deleteEnumerationDialog());
		MenuItem packageListView = new MenuItem("Class Browser (BETA)");
		packageListView.setOnAction(e -> actions.openClassBrowserStage(false));
		enumerationMenu.getItems().addAll(createEnumeration, editEnumeration, deleteEnumeration);

		Menu exportMenu = new Menu("Export as");
		MenuItem exportSVG = new MenuItem("SVG");
		exportSVG.setOnAction(e -> actions.exportSvg());
		MenuItem exportPNG = new MenuItem("PNG");
		exportPNG.setOnAction(a -> diagram.savePNG());	
		exportMenu.getItems().add(exportSVG);
		exportMenu.getItems().add(exportPNG);

		MenuItem importDiagram = new MenuItem("Import Package (BETA)");
		importDiagram.setOnAction(e -> actions.importDiagram());

		Menu filterObjectsMenu = new Menu("Filter Objects (BETA)");
		MenuItem showAll = new MenuItem("Show All");
		showAll.setOnAction(e -> actions.showAll());
		MenuItem showCertainLevel = new MenuItem("Filter by Level");
		showCertainLevel.setOnAction(e -> actions.showCertainLevel());
		filterObjectsMenu.getItems().addAll(showAll, showCertainLevel);

		MenuItem unhideItem = new MenuItem("Unhide Elements");
		unhideItem.setOnAction(e -> actions.unhideElementsDialog());
		
		MenuItem saveAs = new MenuItem("Save As...");
		saveAs.setOnAction(a -> diagram.getComm().saveXmlFile2(diagram.getPackagePath(), diagram.getID()));

		Menu searchMenu = new Menu("Search for");
		MenuItem openFindImplementationDialog = new MenuItem("Implementations");
		openFindImplementationDialog.setOnAction(e -> actions.openFindImplementationDialog());
		
		MenuItem openFindClassDialog = new MenuItem("Classes");
		openFindClassDialog.setOnAction(e -> actions.openFindClassDialog());

		MenuItem openFindSendersOfMessages = new MenuItem("Senders");
		openFindSendersOfMessages.setOnAction(e -> actions.openFindSendersDialog());
		searchMenu.getItems().addAll(openFindImplementationDialog, openFindClassDialog, openFindSendersOfMessages);

		getItems().addAll(
			addMenu, 
			levelMenu, 
			enumerationMenu, 
			unhideItem, 
			packageListView, 
			exportMenu, 
			importDiagram, 
			filterObjectsMenu,
			saveAs,
			searchMenu);
	}
}

