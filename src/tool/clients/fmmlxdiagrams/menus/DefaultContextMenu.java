package tool.clients.fmmlxdiagrams.menus;

import java.util.Optional;

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
		Menu associationMenu = new Menu("Association");
		MenuItem addAssociationItem = new MenuItem("Add Association");
		// TODO: add DiagramAction
		addAssociationItem.setOnAction(e -> System.out.println("Add association called!"));
		associationMenu.getItems().add(addAssociationItem);
		

		MenuItem myItem = new MenuItem("ChangeSlotValue");
		myItem.setOnAction(e -> {
			TextInputDialog dialog = new TextInputDialog("null");
			 
			dialog.setTitle("ChangeSlotValue");
			dialog.setHeaderText("Enter Value:");
			dialog.setContentText("ChangeSlotValue");
			 
			Optional<String> result = dialog.showAndWait();
			 
			if(result.isPresent()) actions.changeSlotValue(result.get());
		});

		getItems().addAll(addClassItem, addInstanceItem, associationMenu, myItem);
	}
}