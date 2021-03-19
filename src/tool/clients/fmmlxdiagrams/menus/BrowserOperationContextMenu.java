package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class BrowserOperationContextMenu extends ContextMenu {

	private final DiagramActions actions;

	public BrowserOperationContextMenu(final FmmlxObject object, 
									   final FmmlxOperation operation, 
			                           AbstractPackageViewer packageViewer) {
		
		this.actions = packageViewer.getActions();
		setAutoHide(true);
		
		if(object != null && object.getLevel() >= 1) {
			MenuItem addItem = new MenuItem("Add Operation");
			addItem.setOnAction(e -> actions.addOperationDialog(object));
			getItems().add(addItem);
		}
		
		if(operation != null) {
			getItems().add(new SeparatorMenuItem());
		
			if(object.getOwnOperations().contains(operation)) {
				MenuItem changeNameItem = new MenuItem("Change name");
				changeNameItem.setOnAction(e -> actions.changeNameDialog(object, PropertyType.Operation, operation));
				getItems().add(changeNameItem);
							
				MenuItem changeTypeItem = new MenuItem("Change type");
				changeTypeItem.setOnAction(e -> actions.changeTypeDialog(object, PropertyType.Operation, operation));
				getItems().add(changeTypeItem);
				
				MenuItem changeLevelItem = new MenuItem("Change level");
				changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, PropertyType.Operation, operation));
				getItems().add(changeLevelItem);
	
				MenuItem changeMulItem = new MenuItem("Change multiplicity");
				changeMulItem.setOnAction(e -> actions.changeMultiplicityDialog(object, PropertyType.Operation, operation));
				getItems().add(changeMulItem);
	
				getItems().add(new SeparatorMenuItem());
				
				MenuItem removeItem = new MenuItem("Remove Attribute");
				removeItem.setOnAction(e -> actions.removeDialog(object, PropertyType.Attribute, operation));
				getItems().add(removeItem);
			} else {
				MenuItem selectDefiningClassItem = new MenuItem("Select Defining Class");
				selectDefiningClassItem.setOnAction(e -> {
					FmmlxObject o = packageViewer.getObjectByPath(operation.getOwner());
					FmmlxOperation a = o.getOperationByName(operation.getName()); // to get original op definition					
				    packageViewer.setSelectedObjectAndProperty(o,a);
				});
				getItems().add(selectDefiningClassItem);
			}
		}
	}
}