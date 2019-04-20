package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class ObjectContextMenu extends ContextMenu {

	private DiagramActions actions;

	public ObjectContextMenu(FmmlxObject object, DiagramActions actions) {
		this.actions = actions;
		setAutoHide(true);
		
		MenuItem addInstanceItem = new MenuItem("Add instance");
		addInstanceItem.setOnAction(e -> actions.addInstanceDialog(object.getId()));
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> System.out.println("OCM: Remove object called"));
		MenuItem changeNameItem = new MenuItem("Change name");
		changeNameItem.setOnAction(e -> System.out.println("OCM: Change name called!"));
		MenuItem changeOfItem = new MenuItem("Change of");
		changeOfItem.setOnAction(e -> System.out.println("OCM: Change of called"));
		MenuItem changeParentItem = new MenuItem("Change parent");
		changeParentItem.setOnAction(e -> System.out.println("OCM: Change parent called"));
		MenuItem changeLevelItem = new MenuItem("Change leve");
		changeLevelItem.setOnAction(e -> System.out.println("OCM: Change level called"));

		Menu attributeMenu = createAttributeSubMenu();
		Menu associationMenu = createAssociationSubMenu();
		Menu operationMenu = createOperationSubMenu();
		Menu slotMenu = createSlotSubMenu();
		Menu associationInstanceMenu = createAssociationInstanceSubMenu();

		getItems().addAll(addInstanceItem, removeItem, changeNameItem, changeOfItem, changeParentItem, changeLevelItem,
				attributeMenu, associationMenu, operationMenu, slotMenu, associationInstanceMenu);
	}

	private Menu createAttributeSubMenu() {
		Menu attributeMenu = new Menu("Attribute");

		MenuItem addItem = new MenuItem("Add");
		addItem.setOnAction(e -> actions.addAttributeDialog());

		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeAttributDialog());

		MenuItem changeNameItem = new MenuItem("Change name");
		changeNameItem.setOnAction(e -> System.out.println("OCM: change attribute name called"));

		MenuItem changeOwnerItem = new MenuItem("Change owner");
		changeOwnerItem.setOnAction(e -> System.out.println("OCM: change attribute owner called"));

		MenuItem changeTypeItem = new MenuItem("Change type");
		changeTypeItem.setOnAction(e -> System.out.println("OCM: change attribute type called"));

		MenuItem changeMultiplicityItem = new MenuItem("Change multiplicity");
		changeMultiplicityItem.setOnAction(e -> System.out.println("OCM: change attribute mulitplicity called"));

		MenuItem changeLevelItem = new MenuItem("Change level");
		changeLevelItem.setOnAction(e -> System.out.println("OCM: change attribute level"));

		attributeMenu.getItems().addAll(addItem, removeItem, changeNameItem, changeOwnerItem, changeTypeItem,
				changeMultiplicityItem, changeLevelItem);

		return attributeMenu;
	}

	private Menu createAssociationSubMenu() {
		Menu associationMenu = new Menu("Association");

		return associationMenu;
	}

	private Menu createOperationSubMenu() {
		Menu operatioMenu = new Menu("Operation");

		return operatioMenu;
	}

	private Menu createSlotSubMenu() {
		Menu slotMenu = new Menu("Slot");

		return slotMenu;
	}

	private Menu createAssociationInstanceSubMenu() {
		Menu associationInstanceMenu = new Menu("Association instance");

		return associationInstanceMenu;
	}
}
