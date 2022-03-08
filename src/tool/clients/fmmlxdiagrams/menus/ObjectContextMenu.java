package tool.clients.fmmlxdiagrams.menus;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.graphics.NodeBaseElement;

import java.util.Vector;

public class ObjectContextMenu extends ContextMenu {

	private final FmmlxObject object;
	private final FmmlxDiagram diagram;
	private final DiagramActions actions;
	private final FmmlxProperty activeProperty;

	public ObjectContextMenu(FmmlxObject object, FmmlxDiagram.DiagramViewPane view, Point2D mouse) {
		this.diagram = view.getDiagram();
		this.actions = diagram.getActions();
		this.object = object;
		NodeBaseElement nl = this.object.getHitLabel(mouse, view.getCanvas().getGraphicsContext2D(), view.getCanvasTransform(), view);
		activeProperty = nl==null?null:nl.getActionObject();
		setAutoHide(true);

		MenuItem addInstanceItem = new MenuItem("Add instance");
		addInstanceItem.setOnAction(e -> actions.addInstanceDialog(object, view));
		if((object.getLevel() >= 1 || object.getLevel() == -1) && !object.isAbstract()) getItems().add(addInstanceItem);
		
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeDialog(object, PropertyType.Class));
		getItems().add(removeItem);
		
		MenuItem changeNameItem = new MenuItem("Change name");
		changeNameItem.setOnAction(e -> actions.changeNameDialog(object, PropertyType.Class));
		getItems().add(changeNameItem);
		
//		MenuItem instanceGenerator = new MenuItem("Instance Generator");
//
//		instanceGenerator.setOnAction(e -> actions.runInstanceGenerator(object));
//		if(object.notTraditionalDataTypeExists() || object.getLevel()<=0){
//			instanceGenerator.setDisable(true);
//		}
//		getItems().add(instanceGenerator);
		
		MenuItem changeOfItem = new MenuItem("Change of (Metaclass)");
		changeOfItem.setOnAction(e -> actions.changeOfDialog(object));
		changeOfItem.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
		getItems().add(changeOfItem);
		
		MenuItem changeParentItem = new MenuItem("Change parent (Superclass)");
		changeParentItem.setOnAction(e -> actions.changeParentsDialog(object));
		getItems().add(changeParentItem);
		
		MenuItem browseInstanceItem = new MenuItem("Browse Instances");
		browseInstanceItem.setOnAction(e -> actions.showObjectBrowser(object));
		getItems().add(browseInstanceItem);
		
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
		Menu constraintMenu = createConstraintSubMenu();
		MenuItem slotMenu = new MenuItem("Change Slot Value");
		slotMenu.setOnAction(e -> diagram.getActions().changeSlotValue(object, null));
		Menu associationInstanceMenu = createAssociationInstanceSubMenu();
		Menu showMenu = createShowSubMenu();
		Menu delegationMenu = createDelegationSubMenu();
		
		/*
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
		levelMenu.getItems().addAll(levelRaiseAllItem, levelLowerAllItem, levelRaiseHereItem, levelLowerHereItem, levelSplitItem, levelMergeItem);*/

		MenuItem assignItem = new MenuItem("Assign to Global Variable");
		assignItem.setOnAction(e -> actions.assignToGlobal(object));
		
		getItems().addAll(attributeMenu, associationMenu, operationMenu, constraintMenu, delegationMenu, slotMenu, associationInstanceMenu, showMenu, assignItem);
		
		addRunMenu();
		
		addNewMenuItem(this, "Hide", e -> {
			Vector<FmmlxObject> v = new Vector<>();
			v.add(object); 
			actions.hide(v, true);
		}, ALWAYS);
	}

	private void addRunMenu() {
		Vector<String> names = object.getAvailableNoArgumentOperationNames();
		if(names.isEmpty()) return;
		Menu run = new Menu("Run");
		for(String opName : names) {
			addNewMenuItem(run, opName, e -> {
				actions.runOperation(object.getPath(), opName);
			}, ALWAYS);
		}
		getItems().add(run);
		
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
		changeTypeItem.setOnAction(e -> actions.changeTypeDialog(object, PropertyType.Attribute, null, object.getOwnAttributes()));
		MenuItem changeLevelItem = new MenuItem("Change level");
		changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, PropertyType.Attribute));
		MenuItem changeMulItem = new MenuItem("Change multiplicity");
		changeMulItem.setOnAction(e -> actions.changeMultiplicityDialog(object, PropertyType.Attribute));

		attributeMenu.getItems().addAll(addItem, removeItem, changeNameItem, changeOwnerItem, changeTypeItem,
				changeLevelItem, changeMulItem);

		return attributeMenu;
	}

	private Menu createAssociationSubMenu() {
		Menu associationMenu = new Menu("Association");

		MenuItem addItem = new MenuItem("Add");
		addItem.setOnAction(e -> diagram.setDrawEdgeMode(object, PropertyType.Association));
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
//		MenuItem editAssociation = new MenuItem("Edit Association Properties");
//		editAssociation.setOnAction(e -> actions.editAssociationDialog(object, PropertyType.Association));
//		editAssociation.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
		MenuItem associationValue = new MenuItem("Edit Association Values");
		associationValue.setOnAction(e -> actions.associationValueDialog(object, PropertyType.Association));

		associationMenu.getItems().addAll(addItem, removeItem, /*changeTargetItem, changeNameItem, changeTypeItem,
				changeMultiplicityItem, changeLevelItem, editAssociation, */associationValue);

		return associationMenu;
	}
	
	private Menu createConstraintSubMenu() {
		System.err.println("activeProperty: " + activeProperty);
		final Constraint activeConstraint = 
				(activeProperty != null && activeProperty instanceof Constraint)
					?(Constraint) activeProperty
					:null;
		
		Menu constraintMenu = new Menu("Constraint");
		
		MenuItem addItem = new MenuItem("Add");
		addItem.setOnAction(e -> actions.addConstraintDialog(object));
		constraintMenu.getItems().add(addItem);
		
		constraintMenu.getItems().add(new SeparatorMenuItem());
		
		MenuItem editConstraint = new MenuItem("Edit Constraint");
		editConstraint.setOnAction(e -> actions.editConstraint(object,activeConstraint));
		constraintMenu.getItems().add(editConstraint);
		
		MenuItem changeNameItem = new MenuItem("Change name");
		changeNameItem.setDisable(true);
		//changeNameItem.setOnAction();
		constraintMenu.getItems().add(changeNameItem);
		
		MenuItem changeLevelItem = new MenuItem("Change level");
		changeLevelItem.setDisable(true);
		constraintMenu.getItems().add(changeLevelItem);
		
		MenuItem changeBodyItem = new MenuItem("Change body");
		changeBodyItem.setDisable(true);
		constraintMenu.getItems().add(changeBodyItem);
		
		MenuItem changeReasonItem = new MenuItem("Change reason");
		changeReasonItem.setDisable(true);
		constraintMenu.getItems().add(changeReasonItem);
		
		MenuItem changeOwnerItem = new MenuItem("Change owner");
		changeOwnerItem.setDisable(true);
		constraintMenu.getItems().add(changeOwnerItem);
		
		constraintMenu.getItems().add(new SeparatorMenuItem());
		
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeDialog(object, PropertyType.Constraint));
		constraintMenu.getItems().add(removeItem);
		
		
		
		return constraintMenu;
	}

	private Menu createOperationSubMenu() {
		final FmmlxOperation activeOperation = 
				(activeProperty != null && activeProperty instanceof FmmlxOperation)
					?(FmmlxOperation) activeProperty
					:null;
				
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
		MenuItem changeTypeItem = new MenuItem("Change type (use Change body instead)");
		changeTypeItem.setOnAction(e -> actions.changeTypeDialog(object, PropertyType.Operation, null, object.getOwnOperations()));
		changeTypeItem.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
		
		/*MenuItem showBodyItem = new MenuItem("Show body in editor");
		if(activeProperty != null && activeProperty instanceof FmmlxOperation) {
			showBodyItem.setOnAction(e -> actions.showBody(object, (FmmlxOperation) activeProperty));
		} else {
			showBodyItem.setDisable(true);
		}*/
		
		MenuItem changeBodyItem = new MenuItem("Change body");
		changeBodyItem.setOnAction(e -> actions.changeBodyDialog(object, activeOperation));
		MenuItem changeLevelItem = new MenuItem("Change level");
		changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, PropertyType.Operation));

		operationMenu.getItems().addAll(addItem, removeItem, changeNameItem, changeOwnerItem, changeTypeItem,
				changeBodyItem, changeLevelItem);

		return operationMenu;
	}

//	private Menu createSlotSubMenu() {
//		Menu slotMenu = new Menu("Slot");
////		slotMenu.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
//
////		MenuItem addValueItem = new MenuItem("Add value");
////		addValueItem.setOnAction(e -> System.out.println("OCM: add slot value called"));
////		MenuItem removeValueItem = new MenuItem("Remove value");
////		removeValueItem.setOnAction(e -> System.out.println("OCM: remove slot value called"));
//		MenuItem changeValueItem = new MenuItem("Change value");
//		changeValueItem.setOnAction(e -> System.out.println("OCM: change slot value called"));
//
//		slotMenu.getItems().addAll(changeValueItem);
//
//		return slotMenu;
//	}

	private Menu createAssociationInstanceSubMenu() {
		Menu associationInstanceMenu = new Menu("Association instance");

		MenuItem addValueItem = new MenuItem("Add instance");
		addValueItem.setOnAction(e -> diagram.setDrawEdgeMode(object, PropertyType.AssociationInstance));
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
			object.setShowOperations(true);
			diagram.redraw();
		});

		showSubMenu.getItems().addAll(operationsItem);
		return showSubMenu;
	}
	
	private Menu createDelegationSubMenu() {
		Menu delegationMenu = new Menu("Delegate");
		addNewMenuItem(delegationMenu, "add Delegate to", e -> diagram.setDrawEdgeMode(object, PropertyType.Delegation), ALWAYS);
		addNewMenuItem(delegationMenu, "remove Delegate to", e -> System.out.println("remove Delegate to not yet implemented."), () -> FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
		addNewMenuItem(delegationMenu, "change Role Filler", e -> diagram.setDrawEdgeMode(object, PropertyType.RoleFiller), ALWAYS);
//		addNewMenuItem(delegationMenu, "remove Rolefiller", e -> System.out.println("remove Rolefiller not yet implemented."), ALWAYS);
		return delegationMenu;
	}

	private interface Enabler {
		boolean isEnabled();
	}
	private static final Enabler ALWAYS = () -> true; 
	
	private void addNewMenuItem(Menu parentMenu, String name, EventHandler<ActionEvent> action, Enabler enabler) {
		MenuItem item = new MenuItem(name);
		item.setOnAction(action);
		item.setDisable(!enabler.isEnabled());
		parentMenu.getItems().add(item);
	}
	
	private void addNewMenuItem(ContextMenu parentMenu, String name, EventHandler<ActionEvent> action, Enabler enabler) {
		MenuItem item = new MenuItem(name);
		item.setOnAction(action);
		item.setDisable(!enabler.isEnabled());
		parentMenu.getItems().add(item);
	}
}
