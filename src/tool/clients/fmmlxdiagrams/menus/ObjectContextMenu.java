package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
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
		if(object.getLevel() > 1 && !object.isAbstract()) getItems().add(addInstanceItem);
		
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeDialog(object, PropertyType.Class));
		getItems().add(removeItem);
		
		MenuItem changeNameItem = new MenuItem("Change name");
		changeNameItem.setOnAction(e -> actions.changeNameDialog(object, PropertyType.Class));
		getItems().add(changeNameItem);
		
		MenuItem changeOfItem = new MenuItem("Change of (Metaclass)");
		changeOfItem.setOnAction(e -> actions.changeOfDialog(object));
		changeOfItem.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
		getItems().add(changeOfItem);
		
		MenuItem changeParentItem = new MenuItem("Change parent (Superclass)");
		changeParentItem.setOnAction(e -> actions.changeParentsDialog(object));
		changeParentItem.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
		getItems().add(changeParentItem);
		
//		MenuItem changeLevelItem = new MenuItem("Change level");
//		changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, PropertyType.Class));
//		changeLevelItem.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
//		getItems().add(changeLevelItem);
		
		MenuItem abstractClassItem = new MenuItem(object.isAbstract()?"Make concrete":"Make abstract");
		abstractClassItem.setOnAction(e -> actions.toggleAbstract(object));
		if(object.getLevel() > 0) getItems().add(abstractClassItem);

		Menu attributeMenu = createAttributeSubMenu();
		Menu associationMenu = createAssociationSubMenu();
		Menu operationMenu = createOperationSubMenu();
		Menu slotMenu = createSlotSubMenu();
		Menu associationInstanceMenu = createAssociationInstanceSubMenu();
		Menu showMenu = createShowSubMenu();
		
		Menu levelMenu = new Menu("Levels");
		MenuItem levelRaiseAllItem = new MenuItem("Raise all");
		levelRaiseAllItem.setOnAction(e -> actions.levelRaiseAll());
		MenuItem levelLowerAllItem = new MenuItem("Lower all");
		levelLowerAllItem.setOnAction(e -> actions.levelLowerAll());
		MenuItem levelRaiseHereItem = new MenuItem("Raise related");
		levelRaiseHereItem.setOnAction(e -> actions.levelRaiseAll());
		levelRaiseHereItem.setDisable(true);
		MenuItem levelLowerHereItem = new MenuItem("Lower related");
		levelLowerHereItem.setOnAction(e -> actions.levelLowerAll());
		levelLowerHereItem.setDisable(true);
		MenuItem levelSplitItem = new MenuItem("Split level here");
		levelSplitItem.setOnAction(e -> actions.levelRaiseAll());
		levelSplitItem.setDisable(true);
		MenuItem levelMergeItem = new MenuItem("Merge with Metaclass");
		levelMergeItem.setOnAction(e -> actions.levelLowerAll());
		levelMergeItem.setDisable(true);
		
		levelMenu.getItems().addAll(levelRaiseAllItem, levelLowerAllItem, levelRaiseHereItem, levelLowerHereItem, levelSplitItem, levelMergeItem);

		getItems().addAll(attributeMenu, associationMenu, operationMenu, slotMenu, associationInstanceMenu, levelMenu, showMenu);
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
		changeOwnerItem.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
		MenuItem changeTypeItem = new MenuItem("Change type");
		changeTypeItem.setOnAction(e -> actions.changeTypeDialog(object, PropertyType.Attribute));
		MenuItem changeLevelItem = new MenuItem("Change level");
		changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, PropertyType.Attribute));

		attributeMenu.getItems().addAll(addItem, removeItem, changeNameItem, changeOwnerItem, changeTypeItem,
				changeLevelItem);

		return attributeMenu;
	}

	private Menu createAssociationSubMenu() {
		Menu associationMenu = new Menu("Association");

		MenuItem addItem = new MenuItem("Add");
		addItem.setOnAction(e -> actions.setDrawEdgeMode(object, PropertyType.Association));
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e_ -> actions.removeDialog(object, PropertyType.Association));
//		MenuItem changeTargetItem = new MenuItem("Change target");
//		changeTargetItem.setOnAction(e -> actions.changeTargetDialog(object, PropertyType.Association));
//		MenuItem changeNameItem = new MenuItem("Change name");
//		changeNameItem.setOnAction(e -> actions.changeNameDialog(object, PropertyType.Association));
//		MenuItem changeTypeItem = new MenuItem("Change type");
//		changeTypeItem.setOnAction(e -> actions.changeTypeDialog(object, PropertyType.Association));
//		MenuItem changeMultiplicityItem = new MenuItem("Change multiplicity");
//		changeMultiplicityItem.setOnAction(e -> actions.changeMultiplicityDialog(object, PropertyType.Association));
//		MenuItem changeLevelItem = new MenuItem("Change level");
//		changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, PropertyType.Association));
		MenuItem editAssociation = new MenuItem("Edit Association Properties");
		editAssociation.setOnAction(e -> actions.editAssociationDialog(object, PropertyType.Association));
		editAssociation.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
		MenuItem associationValue = new MenuItem("Edit Association Values");
		associationValue.setOnAction(e -> actions.associationValueDialog(object, PropertyType.Association));

		associationMenu.getItems().addAll(addItem, removeItem, /*changeTargetItem, changeNameItem, changeTypeItem,
				changeMultiplicityItem, changeLevelItem, */editAssociation, associationValue);

		return associationMenu;
	}

	private Menu createOperationSubMenu() {
		Menu operationMenu = new Menu("Operation");
		MenuItem addItem = new MenuItem("Add");
		addItem.setOnAction(e -> actions.addOperationDialog(object));
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeDialog(object, PropertyType.Operation));
		MenuItem changeNameItem = new MenuItem("Change name (use Change body instead)");
		changeNameItem.setOnAction(e -> actions.changeNameDialog(object, PropertyType.Operation));
		changeNameItem.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
		MenuItem changeOwnerItem = new MenuItem("Change owner");
		changeOwnerItem.setOnAction(e -> actions.changeOwnerDialog(object, PropertyType.Operation));
		changeOwnerItem.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
		MenuItem changeTypeItem = new MenuItem("Change type (use Change body instead)");
		changeTypeItem.setOnAction(e -> actions.changeTypeDialog(object, PropertyType.Operation));
		changeTypeItem.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
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
		slotMenu.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);

//		MenuItem addValueItem = new MenuItem("Add value");
//		addValueItem.setOnAction(e -> System.out.println("OCM: add slot value called"));
//		MenuItem removeValueItem = new MenuItem("Remove value");
//		removeValueItem.setOnAction(e -> System.out.println("OCM: remove slot value called"));
		MenuItem changeValueItem = new MenuItem("Change value");
		changeValueItem.setOnAction(e -> System.out.println("OCM: change slot value called"));

		slotMenu.getItems().addAll(changeValueItem);

		return slotMenu;
	}

	private Menu createAssociationInstanceSubMenu() {
		Menu associationInstanceMenu = new Menu("Association instance");

		MenuItem addValueItem = new MenuItem("Add instance");
		addValueItem.setOnAction(e -> actions.setDrawEdgeMode(object, PropertyType.AssociationInstance));
		MenuItem removeValueItem = new MenuItem("Remove instance");
		removeValueItem.setOnAction(e -> System.out.println("OCM: remove association instance value called"));
		removeValueItem.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
		MenuItem changeValueItem = new MenuItem("Change instance");
		changeValueItem.setOnAction(e -> System.out.println("OCM: change association instance value called"));
		changeValueItem.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);

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
