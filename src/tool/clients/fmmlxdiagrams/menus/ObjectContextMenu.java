package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class ObjectContextMenu extends ContextMenu {

	private final FmmlxObject object;
	private DiagramActions actions;

	public ObjectContextMenu(FmmlxObject object, DiagramActions actions) {
		this.actions = actions;
		this.object = object;
		setAutoHide(true);

		MenuItem addInstanceItem = new MenuItem("Add instance");
		addInstanceItem.setOnAction(e -> actions.addInstanceDialog(object.getId()));
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeDialog(object, "class"));
		MenuItem changeNameItem = new MenuItem("Change name");
		changeNameItem.setOnAction(e -> actions.changeNameDialog(object, "class"));
		MenuItem changeOfItem = new MenuItem("Change of");
		changeOfItem.setOnAction(e -> actions.changeOfDialog(object));
		MenuItem changeParentItem = new MenuItem("Change parent");
		changeParentItem.setOnAction(e -> actions.changeParentsDialog(object));
		MenuItem changeLevelItem = new MenuItem("Change level");
		changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, "class"));

		Menu attributeMenu = createAttributeSubMenu();
		Menu associationMenu = createAssociationSubMenu();
		Menu operationMenu = createOperationSubMenu();
		Menu slotMenu = createSlotSubMenu();
		Menu associationInstanceMenu = createAssociationInstanceSubMenu();
		Menu showMenu = createShowSubMenu();

		getItems().addAll(addInstanceItem, removeItem, changeNameItem, changeOfItem, changeParentItem, changeLevelItem,
				attributeMenu, associationMenu, operationMenu, slotMenu, associationInstanceMenu, showMenu);
	}

	private Menu createAttributeSubMenu() {
		Menu attributeMenu = new Menu("Attribute");

		MenuItem addItem = new MenuItem("Add");
		addItem.setOnAction(e -> actions.addAttributeDialog());
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeDialog(object, "attribute"));
		MenuItem changeNameItem = new MenuItem("Change name");

		changeNameItem.setOnAction(e -> actions.changeNameDialog(object, "attribute"));
		MenuItem changeOwnerItem = new MenuItem("Change owner");
		changeOwnerItem.setOnAction(e -> System.out.println("OCM: change attribute owner called"));
		MenuItem changeTypeItem = new MenuItem("Change type");
		changeTypeItem.setOnAction(e -> System.out.println("OCM: change attribute type called"));
		MenuItem changeMultiplicityItem = new MenuItem("Change multiplicity");
		changeMultiplicityItem.setOnAction(e -> System.out.println("OCM: change attribute mulitplicity called"));
		MenuItem changeLevelItem = new MenuItem("Change level");
		changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, "attribute"));

		attributeMenu.getItems().addAll(addItem, removeItem, changeNameItem, changeOwnerItem, changeTypeItem,
				changeMultiplicityItem, changeLevelItem);

		return attributeMenu;
	}

	private Menu createAssociationSubMenu() {
		Menu associationMenu = new Menu("Association");

		MenuItem addItem = new MenuItem("Add");
		addItem.setOnAction(e -> System.out.println("OCM: add association called"));
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e_ -> actions.removeDialog(object, "association"));
		MenuItem changeTargetItem = new MenuItem("Change target");
		changeTargetItem.setOnAction(e -> System.out.println("OCM: change association target called"));
		MenuItem changeNameItem = new MenuItem("Change name");
		changeNameItem.setOnAction(e -> System.out.println("OCM: change association name called"));
		MenuItem changeTypeItem = new MenuItem("Change type");
		changeTypeItem.setOnAction(e -> System.out.println("OCM: change association type called"));
		MenuItem changeMultiplicityItem = new MenuItem("Change multiplicity");
		changeMultiplicityItem.setOnAction(e -> System.out.println("OCM: change association multiplicity called"));
		MenuItem changeLevelItem = new MenuItem("Change level");
		changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, "association"));

		associationMenu.getItems().addAll(addItem, removeItem, changeTargetItem, changeNameItem, changeTypeItem,
				changeMultiplicityItem, changeLevelItem);

		return associationMenu;
	}

	private Menu createOperationSubMenu() {
		Menu operationMenu = new Menu("Operation");

		MenuItem addItem = new MenuItem("Add");
		addItem.setOnAction(e -> System.out.println("OCM: add operation called"));
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeDialog(object, "operation"));
		MenuItem changeNameItem = new MenuItem("Change name");
		changeNameItem.setOnAction(e -> actions.changeNameDialog(object, "operation"));
		MenuItem changeOwnerItem = new MenuItem("Change owner");
		changeOwnerItem.setOnAction(e -> System.out.println("OCM: change operation owner called"));
		MenuItem changeTypeItem = new MenuItem("Change type");
		changeTypeItem.setOnAction(e -> System.out.println("OCM: change operation type called"));
		MenuItem changeBodyItem = new MenuItem("Change body");
		changeBodyItem.setOnAction(e -> System.out.println("OCM: change operation body called"));
		MenuItem changeLevelItem = new MenuItem("Change level");
		changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, "operation"));

		operationMenu.getItems().addAll(addItem, removeItem, changeNameItem, changeOwnerItem, changeTypeItem,
				changeBodyItem, changeLevelItem);

		return operationMenu;
	}

	private Menu createSlotSubMenu() {
		Menu slotMenu = new Menu("Slot");

		MenuItem addValueItem = new MenuItem("Add value");
		addValueItem.setOnAction(e -> System.out.println("OCM: add slot value called"));
		MenuItem removeValueItem = new MenuItem("Remove value");
		removeValueItem.setOnAction(e -> System.out.println("OCM: remove slot value called"));
		MenuItem changeValueItem = new MenuItem("Change value");
		changeValueItem.setOnAction(e -> System.out.println("OCM: change slot value called"));

		slotMenu.getItems().addAll(addValueItem, removeValueItem, changeValueItem);

		return slotMenu;
	}

	private Menu createAssociationInstanceSubMenu() {
		Menu associationInstanceMenu = new Menu("Association instance");

		MenuItem addValueItem = new MenuItem("Add instance");
		addValueItem.setOnAction(e -> System.out.println("OCM: add association instance value called"));
		MenuItem removeValueItem = new MenuItem("Remove instance");
		removeValueItem.setOnAction(e -> System.out.println("OCM: remove association instance value called"));
		MenuItem changeValueItem = new MenuItem("Change instance");
		changeValueItem.setOnAction(e -> System.out.println("OCM: change association instance value called"));

		associationInstanceMenu.getItems().addAll(addValueItem, removeValueItem, changeValueItem);
		return associationInstanceMenu;
	}

	private Menu createShowSubMenu() {
		Menu showSubMenu = new Menu("Show");

		MenuItem operationsItem = new MenuItem("Operations");
		operationsItem.setOnAction(e -> object.toogleShowOperations());

		showSubMenu.getItems().addAll(operationsItem);
		return showSubMenu;
	}
}
