package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class ObjectContextMenu extends ContextMenu {

	private final FmmlxObject object;
	private DiagramActions actions;

	public ObjectContextMenu(FmmlxObject object, DiagramActions actions) {
		this.actions = actions;
		this.object = object;
		setAutoHide(true);

		MenuItem addInstanceItem = new MenuItem("Add instance");
		addInstanceItem.setOnAction(e -> actions.addInstanceDialog(object));
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeDialog(object, PropertyType.Class));
		MenuItem changeNameItem = new MenuItem("Change name");
		changeNameItem.setOnAction(e -> actions.changeNameDialog(object, PropertyType.Class));
		MenuItem changeOfItem = new MenuItem("Change of");
		changeOfItem.setOnAction(e -> actions.changeOfDialog(object));
		MenuItem changeParentItem = new MenuItem("Change parent");
		changeParentItem.setOnAction(e -> actions.changeParentsDialog(object));
		MenuItem changeLevelItem = new MenuItem("Change level");
		changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, PropertyType.Class));
		MenuItem abstractClassItem = new MenuItem("Change abstract");
		abstractClassItem.setOnAction(e -> actions.toggleIsAbstract(object));

		Menu attributeMenu = createAttributeSubMenu();
		Menu associationMenu = createAssociationSubMenu();
		Menu operationMenu = createOperationSubMenu();
		Menu slotMenu = createSlotSubMenu();
		Menu associationInstanceMenu = createAssociationInstanceSubMenu();
		Menu showMenu = createShowSubMenu();

		getItems().addAll(addInstanceItem, removeItem, changeNameItem, changeOfItem, changeParentItem, changeLevelItem, abstractClassItem,
				attributeMenu, associationMenu, operationMenu, slotMenu, associationInstanceMenu, showMenu);
	}

	private Menu createAttributeSubMenu() {
		Menu attributeMenu = new Menu("Attribute");

		MenuItem addItem = new MenuItem("Add");
		addItem.setOnAction(e -> actions.addAttributeDialog(object));
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeDialog(object, PropertyType.Attribute));
		MenuItem changeNameItem = new MenuItem("Change name");

		changeNameItem.setOnAction(e -> actions.changeNameDialog(object, PropertyType.Attribute));
		MenuItem changeOwnerItem = new MenuItem("Change owner");
		changeOwnerItem.setOnAction(e -> actions.changeOwnerDialog(object, PropertyType.Attribute));
		MenuItem changeTypeItem = new MenuItem("Change type");
		changeTypeItem.setOnAction(e -> actions.changeTypeDialog(object, PropertyType.Attribute));
		MenuItem changeMultiplicityItem = new MenuItem("Change multiplicity");
		changeMultiplicityItem.setOnAction(e -> actions.changeMultiplicityDialog(object, PropertyType.Attribute));
		MenuItem changeLevelItem = new MenuItem("Change level");
		changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, PropertyType.Attribute));

		attributeMenu.getItems().addAll(addItem, removeItem, changeNameItem, changeOwnerItem, changeTypeItem,
				changeMultiplicityItem, changeLevelItem);

		return attributeMenu;
	}

	private Menu createAssociationSubMenu() {
		Menu associationMenu = new Menu("Association");

		MenuItem addItem = new MenuItem("Add");
		addItem.setOnAction(e -> actions.setDrawEdgeMode(object, PropertyType.Association));
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e_ -> actions.removeDialog(object, PropertyType.Association));
		MenuItem changeTargetItem = new MenuItem("Change target");
		changeTargetItem.setOnAction(e -> actions.changeTargetDialog(object, PropertyType.Association));
		MenuItem changeNameItem = new MenuItem("Change name");
		changeNameItem.setOnAction(e -> actions.changeNameDialog(object, PropertyType.Association));
		MenuItem changeTypeItem = new MenuItem("Change type");
		changeTypeItem.setOnAction(e -> actions.changeTypeDialog(object, PropertyType.Association));
		MenuItem changeMultiplicityItem = new MenuItem("Change multiplicity");
		changeMultiplicityItem.setOnAction(e -> actions.changeMultiplicityDialog(object, PropertyType.Association));
		MenuItem changeLevelItem = new MenuItem("Change level");
		changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, PropertyType.Association));
		MenuItem editAssociation = new MenuItem("edit Association (test)");
		editAssociation.setOnAction(e -> actions.editAssociationDialog(object, PropertyType.Association));
		MenuItem associationValue = new MenuItem("Association Value (test)");
		associationValue.setOnAction(e -> actions.associationValueDialog(object, PropertyType.Association));

		associationMenu.getItems().addAll(addItem, removeItem, changeTargetItem, changeNameItem, changeTypeItem,
				changeMultiplicityItem, changeLevelItem, editAssociation, associationValue);

		return associationMenu;
	}

	private Menu createOperationSubMenu() {
		Menu operationMenu = new Menu("Operation");
		MenuItem addItem = new MenuItem("Add");
		addItem.setOnAction(e -> actions.addDialog(object, PropertyType.Operation));
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeDialog(object, PropertyType.Operation));
		MenuItem changeNameItem = new MenuItem("Change name");
		changeNameItem.setOnAction(e -> actions.changeNameDialog(object, PropertyType.Operation));
		MenuItem changeOwnerItem = new MenuItem("Change owner");
		changeOwnerItem.setOnAction(e -> actions.changeOwnerDialog(object, PropertyType.Operation));
		MenuItem changeTypeItem = new MenuItem("Change type");
		changeTypeItem.setOnAction(e -> actions.changeTypeDialog(object, PropertyType.Operation));
		MenuItem changeBodyItem = new MenuItem("Change body");
		changeBodyItem.setOnAction(e -> actions.changeBodyDialog(object));
		MenuItem changeLevelItem = new MenuItem("Change level");
		changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, PropertyType.Operation));

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
		addValueItem.setOnAction(e -> actions.setDrawEdgeMode(object, PropertyType.AssociationInstance));
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
		operationsItem.setOnAction(e -> {
			object.toggleShowOperations();
			actions.redrawDiagram();
		});

		showSubMenu.getItems().addAll(operationsItem);
		return showSubMenu;
	}
}
