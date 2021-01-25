package tool.clients.fmmlxdiagrams.menus;

import java.util.Vector;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class BrowserObjectContextMenu extends ContextMenu {

	private final FmmlxObject object;
	private final DiagramActions actions;
	
	public BrowserObjectContextMenu(ListView<FmmlxObject> listView, AbstractPackageViewer packageViewer) {
		
		this.actions = packageViewer.getActions();
		this.object = listView.getSelectionModel().getSelectedItem();
		setAutoHide(true);
		
		MenuItem addClassItem = new MenuItem("Add Class");
		addClassItem.setOnAction(e -> actions.addMetaClassDialog((javafx.scene.canvas.Canvas) null));
		getItems().add(addClassItem);
		
		if(object != null && object.getLevel() >= 1 && !object.isAbstract()) {
			MenuItem addInstanceItem = new MenuItem("Add instance of " + object.getName());
			addInstanceItem.setOnAction(e -> actions.addInstanceDialog(object, (javafx.scene.canvas.Canvas) null));
			getItems().add(addInstanceItem);
		}
		
		MenuItem changeNameItem = new MenuItem("Change name");
		changeNameItem.setOnAction(e -> actions.changeNameDialog(object, PropertyType.Class, object));
		getItems().add(changeNameItem);
		
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeDialog(object, PropertyType.Class, object));
		getItems().add(removeItem);
		
		if(object != null && object.getLevel() >= 1 && !object.isAbstract()) {
			MenuItem instanceGenerator = new MenuItem("Instance Generator");
	
			instanceGenerator.setOnAction(e -> actions.runInstanceGenerator(object));
			if(object.notTraditionalDataTypeExists() || object.getLevel()<=0){
				instanceGenerator.setDisable(true);
			}
			instanceGenerator.setDisable(true); // temp
			getItems().add(instanceGenerator);
		}
		
		MenuItem changeOfItem = new MenuItem("Change of (Metaclass)");
		changeOfItem.setOnAction(e -> {new javafx.scene.control.Alert(
				AlertType.INFORMATION, "Really ?", 
				javafx.scene.control.ButtonType.NO, 
				javafx.scene.control.ButtonType.CANCEL).showAndWait();});
		getItems().add(changeOfItem);
		
		if(object != null && object.getLevel() >= 1) { 
			MenuItem changeParentItem = new MenuItem("Change parent (Superclass)");
			changeParentItem.setOnAction(e -> actions.changeParentsDialog(object));
			getItems().add(changeParentItem);		
		
			MenuItem abstractClassItem = new MenuItem(object.isAbstract()?"Make concrete":"Make abstract");
			abstractClassItem.setOnAction(e -> actions.toggleAbstract(object));
			abstractClassItem.setDisable(object.getInstances().size() > 0);
			getItems().add(abstractClassItem);
		}
		addNewMenuItem(this, "Hide", e -> {
			Vector<FmmlxObject> v = new Vector<FmmlxObject>(); 
			v.add(object); 
			actions.hide(v, true);
		}, ALWAYS);
	}


	private interface Enabler {
		boolean isEnabled();
	}
	private static final Enabler ALWAYS = () -> true; 
	
	private void addNewMenuItem(ContextMenu parentMenu, String name, EventHandler<ActionEvent> action, Enabler enabler) {
		MenuItem item = new MenuItem(name);
		item.setOnAction(action);
		item.setDisable(!enabler.isEnabled());
		parentMenu.getItems().add(item);
	}
}
