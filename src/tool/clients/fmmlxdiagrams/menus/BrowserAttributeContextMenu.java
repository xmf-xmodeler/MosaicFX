package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class BrowserAttributeContextMenu extends ContextMenu {

	private final FmmlxObject object;
	private final FmmlxAttribute attribute;
	private final DiagramActions actions;

	public BrowserAttributeContextMenu(ListView<FmmlxObject> oListView, 
			                           ListView<FmmlxAttribute> aListView, 
			                           AbstractPackageViewer packageViewer) {
		
		this.actions = packageViewer.getActions();
		this.object = oListView.getSelectionModel().getSelectedItem();
		this.attribute = aListView.getSelectionModel().getSelectedItem();
		setAutoHide(true);
		
		if(object != null && object.getLevel() >= 1) {
			MenuItem addItem = new MenuItem("Add Attribute");
			addItem.setOnAction(e -> actions.addAttributeDialog(object));
			getItems().add(addItem);
		}
		
		if(attribute != null) {
			boolean disable = !object.getOwnAttributes().contains(attribute);
			
			MenuItem removeItem = new MenuItem("Remove Attribute");
			removeItem.setOnAction(e -> actions.removeDialog(object, PropertyType.Attribute, attribute));
			removeItem.setDisable(disable);
			getItems().add(removeItem);
			
			MenuItem changeNameItem = new MenuItem("Change name");
			changeNameItem.setOnAction(e -> actions.changeNameDialog(object, PropertyType.Attribute, attribute));
			changeNameItem.setDisable(disable);
			getItems().add(changeNameItem);
						
			MenuItem changeTypeItem = new MenuItem("Change type");
			changeTypeItem.setOnAction(e -> actions.changeTypeDialog(object, PropertyType.Attribute, attribute));
			changeTypeItem.setDisable(disable);
			getItems().add(changeTypeItem);
			
			MenuItem changeLevelItem = new MenuItem("Change level");
			changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, PropertyType.Attribute, attribute));
			changeLevelItem.setDisable(disable);
			getItems().add(changeLevelItem);
			
			MenuItem changeMulItem = new MenuItem("Change multiplicity");
			changeMulItem.setOnAction(e -> actions.changeMultiplicityDialog(object, PropertyType.Attribute, attribute));
			changeMulItem.setDisable(disable);
			getItems().add(changeMulItem);
		}		
	}
}
