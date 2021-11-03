package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;

import java.util.Optional;
import java.util.Vector;

public class DefaultContextMenu extends ContextMenu {

	public DefaultContextMenu(DiagramViewPane view) {
		FmmlxDiagram diagram = view.getDiagram();
		DiagramActions actions = diagram.getActions();
		setAutoHide(true);

		MenuItem addClassItem = new MenuItem("Add Class");
		addClassItem.setOnAction(e -> actions.addMetaClassDialog(view.getCanvas()));
		MenuItem addInstanceItem = new MenuItem("Add Instance");
		addInstanceItem.setOnAction(e -> actions.addInstanceDialog(view.getCanvas()));
		
		// Submenu for association
//		Menu associationMenu = new Menu("Association");
		MenuItem addAssociationItem = new MenuItem("Add Association");
		addAssociationItem.setOnAction(e -> actions.addAssociationDialog(null, null));
//		associationMenu.getItems().add(addAssociationItem);
		
		
		Menu levelMenu = new Menu("Levels");
		MenuItem levelRaiseAllItem = new MenuItem("Raise all");
		levelRaiseAllItem.setOnAction(e -> actions.levelRaiseAll());
		MenuItem levelLowerAllItem = new MenuItem("Lower all");
		levelLowerAllItem.setOnAction(e -> actions.levelLowerAll());
		
		levelMenu.getItems().addAll(levelRaiseAllItem, levelLowerAllItem);
	
		Menu enumeration = new Menu("Enumeration");
		MenuItem createEnumeration = new MenuItem("Create Enumeration");
		createEnumeration.setOnAction(e -> actions.addEnumerationDialog());
		MenuItem editEnumeration = new MenuItem("Edit Enumeration");
		editEnumeration.setOnAction(e -> actions.editEnumerationDialog("edit_element",""));
		MenuItem deleteEnumeration = new MenuItem("Delete Enumeration");
		deleteEnumeration.setOnAction(e -> actions.deleteEnumerationDialog());
		MenuItem packageListView_LOCAL = new MenuItem("Package ListView (Local)");
		packageListView_LOCAL.setOnAction(e -> actions.openClassBrowserStage(false));
		MenuItem packageListView = new MenuItem("Package ListView");
		packageListView.setOnAction(e -> actions.openClassBrowserStage(false));
		enumeration.getItems().addAll(createEnumeration, editEnumeration, deleteEnumeration);

		MenuItem exportSvgDefault = new MenuItem("Export SVG");
		exportSvgDefault.setOnAction(e -> actions.exportSvg());

		MenuItem importDiagram = new MenuItem("Import Package (BETA)");
		importDiagram.setOnAction(e -> actions.importDiagram());

		Menu filterObjects = new Menu("Filter Objects");
		MenuItem showAll = new MenuItem("Show All");
		showAll.setOnAction(e -> actions.showAll());
		MenuItem showCertainLevel = new MenuItem("Show Certain Level (BETA)");
		showCertainLevel.setOnAction(e -> actions.showCertainLevel());
		filterObjects.getItems().addAll(showAll, showCertainLevel);

		MenuItem unhideItem = new MenuItem("Unhide Elements");
		unhideItem.setOnAction(e -> actions.unhideElementsDialog());
		
		getItems().addAll(addClassItem, addInstanceItem, addAssociationItem, levelMenu, enumeration, unhideItem, packageListView, packageListView_LOCAL, exportSvgDefault, importDiagram, filterObjects);

		{ // test
			MenuItem testEvalList = new MenuItem("TEST EVAL LIST");
			testEvalList.setOnAction(e -> {
				TextInputDialog dialog = new TextInputDialog("x");
				dialog.setTitle("evalList Test Dialog");
//				dialog.setHeaderText("Look, a Text Input Dialog");
				dialog.setContentText("Enter a reference:");

				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()){
				    Vector<String> list = actions.testEvalList(result.get());
				    
				    ChoiceDialog<String> dialog2 = new ChoiceDialog<>(null, list);
				    dialog2.setTitle("Result Dialog");
//				    dialog2.setHeaderText("Look, a Choice Dialog");
				    dialog2.setContentText("Found this list:");

				    //Optional<String> result2 = 
				    	dialog2.showAndWait();
				}

			});
			
//			getItems().addAll(testEvalList);

			MenuItem save = new MenuItem("Save");
			save.setOnAction(a -> actions.save());
			getItems().addAll(save);
			save.setDisable(true);

			MenuItem saveAs = new MenuItem("Save As...");
			saveAs.setOnAction(a -> diagram.getComm().saveXmlFile2(diagram.getPackagePath(), diagram.getID()));
			getItems().addAll(saveAs);

			MenuItem pngItem = new MenuItem("Export as PNG...");
			pngItem.setOnAction(a -> {
				diagram.savePNG();
			});	

			getItems().add(pngItem);

			MenuItem openFindImplementationDialog = new MenuItem("Search for Implementation");
			openFindImplementationDialog.setOnAction(e -> actions.openFindImplementationDialog());
			getItems().addAll(openFindImplementationDialog);
	
			MenuItem openFindClassDialog = new MenuItem("Search for Class");
			openFindClassDialog.setOnAction(e -> actions.openFindClassDialog());
			getItems().addAll(openFindClassDialog);

			MenuItem openFindSendersOfMessages = new MenuItem("Search for Senders");
			openFindSendersOfMessages.setOnAction(e -> actions.openFindSendersDialog());
			getItems().addAll(openFindSendersOfMessages);

		}
	}
}
