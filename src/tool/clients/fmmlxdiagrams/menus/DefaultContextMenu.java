package tool.clients.fmmlxdiagrams.menus;

import java.util.Optional;
import java.util.Vector;

import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import tool.clients.fmmlxdiagrams.DiagramActions;

public class DefaultContextMenu extends ContextMenu {

	public DefaultContextMenu(DiagramActions actions) {
		setAutoHide(true);

		MenuItem addClassItem = new MenuItem("Add Class");
		addClassItem.setOnAction(e -> actions.addMetaClassDialog());
		MenuItem addInstanceItem = new MenuItem("Add Instance");
		addInstanceItem.setOnAction(e -> actions.addInstanceDialog());
		
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
		packageListView_LOCAL.setOnAction(e -> actions.classBrowserStage(false));
		MenuItem packageListView = new MenuItem("Package ListView");
		packageListView.setOnAction(e -> actions.classBrowserStage(true));
		
		enumeration.getItems().addAll(createEnumeration, editEnumeration, deleteEnumeration);
		
		getItems().addAll(addClassItem, addInstanceItem, addAssociationItem, levelMenu, enumeration, packageListView, packageListView_LOCAL);

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
			
			getItems().addAll(testEvalList);

			MenuItem testSave = new MenuItem("test Save");
			testSave.setOnAction(a -> actions.save());
			getItems().addAll(testSave);
			
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
