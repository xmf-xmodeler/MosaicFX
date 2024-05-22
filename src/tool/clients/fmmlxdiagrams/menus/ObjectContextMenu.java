package tool.clients.fmmlxdiagrams.menus;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.graphics.NodeElement;
import tool.clients.fmmlxdiagrams.graphics.wizard.ConcreteSyntaxWizard;

import java.util.Optional;
import java.util.Vector;

public class ObjectContextMenu extends ContextMenu {

	private final FmmlxObject object;
	private final FmmlxDiagram diagram;
	private final DiagramActions actions;
	private final FmmlxProperty activeProperty;

	public ObjectContextMenu(FmmlxObject object, FmmlxDiagram.DiagramCanvas view, Point2D mouse) {
		this.diagram = view.getDiagram();
		this.actions = diagram.getActions();
		this.object = object;
		NodeElement nl = this.object.getHitElement(mouse, view.getCanvas().getGraphicsContext2D(), view.getCanvasTransform(), view);
		activeProperty = nl==null?null:nl.getActionObject();
		setAutoHide(true);
		constructor(object,view,mouse);
	}
	
	private void constructor(FmmlxObject object, FmmlxDiagram.DiagramCanvas view, Point2D mouse){

		// LM, 07.04.2023, Add new menu item for executing customer user interfaces
		MenuItem execUI = new MenuItem("Execute UI");
		execUI.setOnAction( e -> actions.executeUI(object) );
		if( object.getMetaClassName().equals("UserInterface")) getItems().add(execUI);
		// End custom UI
				
		MenuItem addInstanceItem = new MenuItem("Add instance");
		addInstanceItem.setOnAction(e -> actions.addInstanceDialog(object, view));
		if((object.isClass()) && !object.isAbstract()) getItems().add(addInstanceItem);
		
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeDialog(object, PropertyType.Class));
		getItems().add(removeItem);
		
		MenuItem changeNameItem = new MenuItem("Change name");
		changeNameItem.setOnAction(e -> actions.changeNameDialog(object, PropertyType.Class));
		getItems().add(changeNameItem);
		getItems().add(new SeparatorMenuItem());
				
		if(diagram.getSelectedObjects().size() > 1) {
			boolean classifyPossible = true;
			Vector<FmmlxObject> objs = new Vector<>();
			for(CanvasElement ce : diagram.getSelectedObjects()) {
				if(! (ce instanceof FmmlxObject))  classifyPossible = false;
				else {
					FmmlxObject o = (FmmlxObject) ce;
					objs.add(o);
					if(!("Root::FMML::MetaClass".equals(o.getOfPath())))  classifyPossible = false;
				}
			}
			if(classifyPossible) {
				MenuItem classify = new MenuItem("Classify");
				getItems().add(classify);
				classify.setOnAction(e -> actions.classify(objs));
			}
		}
					
		if(!diagram.isUMLMode()) {
		MenuItem instanceWizardItem = new MenuItem("Instance Wizard...");
		instanceWizardItem.setOnAction(e -> actions.openInstanceWizard(object, view));
		if((object.isClass()) && !object.isAbstract()) getItems().add(instanceWizardItem);
			
		MenuItem changeOfItem = new MenuItem("Change of (Metaclass)");
		changeOfItem.setOnAction(e -> actions.changeOfDialog(object));
		changeOfItem.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
		getItems().add(changeOfItem);
		
		MenuItem changeLevelItem = new MenuItem("Change level");
		changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, PropertyType.Class));
		getItems().add(changeLevelItem);
		
		MenuItem abstractClassItem = new MenuItem(object.isAbstract()?"Make concrete":"Make abstract");
		abstractClassItem.setOnAction(e -> actions.toggleAbstract(object));
		if(object.getLevel().isClass()) getItems().add(abstractClassItem);
		
		MenuItem singletonClassItem = new MenuItem(object.isSingleton()?"Remove Singleton Property":"Make Singleton");
		singletonClassItem.setOnAction(e -> actions.toggleSingleton(object));
		if(object.getLevel().isClass()) getItems().add(singletonClassItem);
		}
		
		MenuItem changeParentItem = new MenuItem("Change parent (Superclass)");
		changeParentItem.setOnAction(e -> actions.changeParentsDialog(object));
		
		MenuItem browseInstanceItem = new MenuItem("Browse Instances");
		browseInstanceItem.setOnAction(e -> actions.showObjectBrowser(object));
		
	
		Menu attributeMenu = createAttributeSubMenu();
		Menu associationMenu = createAssociationSubMenu();
		Menu operationMenu = createOperationSubMenu();
		Menu constraintMenu = createConstraintSubMenu();
		MenuItem slotMenu = new MenuItem("Change Slot Value");
		slotMenu.setOnAction(e -> diagram.getActions().changeSlotValue(object, null));
		Menu associationInstanceMenu = createAssociationInstanceSubMenu();
		Menu delegationMenu = createDelegationSubMenu();
		
		MenuItem editConcreteSyntaxItem = new MenuItem("Edit Concrete Syntax");
		editConcreteSyntaxItem.setOnAction(e -> {
			Vector<Integer> choices = new Vector<>();
			for(Integer i = 0; i < object.getLevel().getMinLevel(); i++) {
				choices.add(i);
			}
			if(choices.size() > 0) {
				ChoiceDialog<Integer> dialog = new ChoiceDialog<Integer>(object.getLevel().getMinLevel()-1, choices);
				dialog.setTitle("Edit Concrete Syntax");
				dialog.setHeaderText("Edit Concrete Syntax for " + object.getName() + " on which level?" );

				Optional<Integer> result = dialog.showAndWait();
				if (result.isPresent()){
					ConcreteSyntaxWizard wizard = new ConcreteSyntaxWizard(diagram, object, result.get());
					wizard.start(new Stage());
				}
			}
		});
		
		//add all items, that are used for all Objects
		//Removed uml if statement since the new implementation seems to not add global var anymore
		addMenus(object, changeParentItem, browseInstanceItem, attributeMenu, associationMenu, operationMenu,
				constraintMenu, delegationMenu,slotMenu, associationInstanceMenu, addInstanceItem, removeItem, changeNameItem);
		
		addNewMenuItem(this, "Hide", e -> {
			Vector<FmmlxObject> v = new Vector<>();
			v.add(object); 
			actions.hide(v, true);
		}, ALWAYS);
	}
	
	private void addMenus(FmmlxObject object, MenuItem changeParentItem, MenuItem browseInstanceItem,
			Menu attributeMenu, Menu associationMenu, Menu operationMenu, Menu constraintMenu, Menu delegationMenu, MenuItem slotMenu, Menu associationInstanceMenu, MenuItem addInstanceItem, MenuItem removeItem, MenuItem changeNameItem) {
		if (diagram.getViewPane().getDiagramViewState().getPrecedence() > 4) {		
			if((object.isClass()) && !object.isAbstract()) getItems().add(addInstanceItem);
		}
		getItems().add(changeNameItem);
		getItems().add(removeItem);
		getItems().add(new SeparatorMenuItem());
		// add items, that are used only for Objects that are not on level 0
		if (object.getLevel() != null && !(object.getLevel().getMinLevel() == 0)) {
			if (diagram.getViewPane().getDiagramViewState().getPrecedence() >= 100) {
				getItems().addAll(changeParentItem, browseInstanceItem, constraintMenu, operationMenu);
			}
			if (diagram.getViewPane().getDiagramViewState().getPrecedence() > 1) {
				getItems().add(attributeMenu);
			}
			if (diagram.getViewPane().getDiagramViewState().getPrecedence() > 3) {
				getItems().add(associationMenu);
			}	
		}
		//add all items, that are used for all Objects		
		if (diagram.getViewPane().getDiagramViewState().getPrecedence() > 4) {
			getItems().addAll(slotMenu, associationInstanceMenu);		
		}	
		addRunMenu();
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

		MenuItem genGetterItem = new MenuItem("Generate Getter");
		genGetterItem.setOnAction(e -> actions.generateGetter(object, activeProperty instanceof FmmlxAttribute ? (FmmlxAttribute) activeProperty : null));
		MenuItem genSetterItem = new MenuItem("Generate Setter");
		genSetterItem.setOnAction(e -> actions.generateSetter(object, activeProperty instanceof FmmlxAttribute ? (FmmlxAttribute) activeProperty : null));


		if(!diagram.isUMLMode()) {
		attributeMenu.getItems().addAll(addItem, removeItem, changeNameItem, changeOwnerItem, changeTypeItem,
				changeLevelItem, changeMulItem,
				new SeparatorMenuItem(),
				genGetterItem, genSetterItem);}
		else {
			attributeMenu.getItems().addAll(addItem, removeItem, changeNameItem, changeOwnerItem, changeTypeItem,
					new SeparatorMenuItem(),
					genGetterItem, genSetterItem);
		}

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
//		System.err.println("activeProperty: " + activeProperty);
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
		Menu associationInstanceMenu = new Menu("Link");

		MenuItem addValueItem = new MenuItem("Add link");
		addValueItem.setOnAction(e -> diagram.setDrawEdgeMode(object, PropertyType.AssociationInstance));
		MenuItem removeValueItem = new MenuItem("Remove link");
		removeValueItem.setOnAction(e -> System.out.println("OCM: remove association instance value called"));
		removeValueItem.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
		MenuItem changeValueItem = new MenuItem("Change link");
		changeValueItem.setOnAction(e -> System.out.println("OCM: change association instance value called"));
		changeValueItem.setDisable(!FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);

		associationInstanceMenu.getItems().addAll(addValueItem, removeValueItem, changeValueItem);
		return associationInstanceMenu;
	}
	
	private Menu createDelegationSubMenu() {
		Menu delegationMenu = new Menu("Delegate");
		if(object.isClass()) {
			addNewMenuItem(delegationMenu, "add Delegate to", e -> diagram.setDrawEdgeMode(object, PropertyType.Delegation), ALWAYS);
			addNewMenuItem(delegationMenu, "remove Delegate to", e -> System.out.println("remove Delegate to not yet implemented."), () -> FmmlxDiagram.SHOW_MENUITEMS_IN_DEVELOPMENT);
		}
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
