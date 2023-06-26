package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.*;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;
import tool.clients.fmmlxdiagrams.graphics.wizard.ConcreteSyntaxWizard;

public class DefaultContextMenu extends ContextMenu {

	public DefaultContextMenu(DiagramViewPane view) {
		FmmlxDiagram diagram = view.getDiagram();
		DiagramActions actions = diagram.getActions();
		setAutoHide(true);
		
		Menu addMenu = new Menu("Add");
			MenuItem addClassItem = new MenuItem("Class...");
			addClassItem.setOnAction(e -> actions.addMetaClassDialog(view));
			MenuItem addInstanceItem = new MenuItem("Instance...");
			addInstanceItem.setOnAction(e -> actions.addInstanceDialog(view));
			MenuItem addAssociationItem = new MenuItem("Association...");
			addAssociationItem.setOnAction(e -> actions.addAssociationDialog(null, null));
			addMenu.getItems().addAll(addClassItem, addInstanceItem, addAssociationItem);
		getItems().add(addMenu);
		
		Menu levelMenu = new Menu("Levels");
		MenuItem levelRaiseAllItem = new MenuItem("Raise all");
		levelRaiseAllItem.setOnAction(e -> actions.levelRaiseAll());
		MenuItem levelLowerAllItem = new MenuItem("Lower all");
		levelLowerAllItem.setOnAction(e -> actions.levelLowerAll());
		levelMenu.getItems().addAll(levelRaiseAllItem, levelLowerAllItem);
	
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

		Menu exportMenu = new Menu("Export as");
		MenuItem exportSVG = new MenuItem("SVG...");
		exportSVG.setOnAction(e -> actions.exportSvg());
		MenuItem exportPNG = new MenuItem("PNG...");
		exportPNG.setOnAction(a -> diagram.savePNG());	
		exportMenu.getItems().add(exportSVG);
		exportMenu.getItems().add(exportPNG);

		MenuItem importDiagram = new MenuItem("Import Package (BETA)");
		importDiagram.setOnAction(e -> actions.importDiagram());

		MenuItem generateCustomUI = new MenuItem("Generate Custom UI");
		if (!diagram.getSelectedObjects().isEmpty()){
			generateCustomUI.setOnAction(e -> actions.showGenerateCustomUIDialog());
		}
			
		Menu filterObjectsMenu = new Menu("Filter Objects (BETA)");
		MenuItem showAll = new MenuItem("Show All");
		showAll.setOnAction(e -> actions.showAll());
		MenuItem showCertainLevel = new MenuItem("Filter by Level...");
		showCertainLevel.setOnAction(e -> actions.showCertainLevel());
		filterObjectsMenu.getItems().addAll(showAll, showCertainLevel);

		MenuItem unhideItem = new MenuItem("Hide/Unhide Elements...");
		unhideItem.setOnAction(e -> actions.showUnhideElementsDialog(diagram));
		
		MenuItem centerObject = new MenuItem("Center view on specific Element...");
		centerObject.setOnAction(e -> view.centerObject());
		
		MenuItem saveModel = new MenuItem("Save Model...");
		saveModel.setOnAction(a -> diagram.getComm().saveXmlFile2(diagram.getPackagePath(), diagram.getID()));

		Menu searchMenu = new Menu("Search for");
		MenuItem openFindImplementationDialog = new MenuItem("Implementations...");
		openFindImplementationDialog.setOnAction(e -> actions.openFindImplementationDialog());
		
		MenuItem openFindClassDialog = new MenuItem("Classes...");
		openFindClassDialog.setOnAction(e -> actions.openFindClassDialog());

		MenuItem openFindSendersOfMessages = new MenuItem("Senders...");
		openFindSendersOfMessages.setOnAction(e -> actions.openFindSendersDialog());
		searchMenu.getItems().addAll(openFindImplementationDialog, openFindClassDialog, openFindSendersOfMessages);


		MenuItem editConcreteSyntaxItem = new MenuItem("Edit Concrete Syntaxes");
		editConcreteSyntaxItem.setOnAction(e -> {
			ConcreteSyntaxWizard wizard = new ConcreteSyntaxWizard(diagram, null, null);
			try {
				wizard.start(new Stage());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		getItems().addAll( 
			levelMenu, 
			enumerationMenu, 
			unhideItem,
			generateCustomUI,
			centerObject,
			//packageListView, 
			exportMenu, 
			importDiagram, 
			filterObjectsMenu,
			saveModel,
			searchMenu,
			editConcreteSyntaxItem);
	}
}

