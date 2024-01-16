package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class BrowserAttributeContextMenu extends ContextMenu {


	private final DiagramActions actions;

	public BrowserAttributeContextMenu(final FmmlxObject object, 
									   final FmmlxAttribute attribute, 
			                           AbstractPackageViewer packageViewer) {
		
		this.actions = packageViewer.getActions();
		setAutoHide(true);
		
		if(object != null && object.getLevel().isClass()) {
			MenuItem addItem = new MenuItem("Add Attribute");
			addItem.setOnAction(e -> actions.addAttributeDialog(object));
			getItems().add(addItem);
		}
		
		if(attribute != null) {
			getItems().add(new SeparatorMenuItem());
		
			if(object.getOwnAttributes().contains(attribute)) {
				MenuItem changeNameItem = new MenuItem("Change name");
				changeNameItem.setOnAction(e -> actions.changeNameDialog(object, PropertyType.Attribute, attribute));
				getItems().add(changeNameItem);
							
				MenuItem changeTypeItem = new MenuItem("Change type");
				changeTypeItem.setOnAction(e -> actions.changeTypeDialog(object, PropertyType.Attribute, attribute, object.getOwnAttributes()));
				getItems().add(changeTypeItem);
				
				MenuItem changeLevelItem = new MenuItem("Change level");
				changeLevelItem.setOnAction(e -> actions.changeLevelDialog(object, PropertyType.Attribute, attribute));
				getItems().add(changeLevelItem);
	
				MenuItem changeMulItem = new MenuItem("Change multiplicity");
				changeMulItem.setOnAction(e -> actions.changeMultiplicityDialog(object, PropertyType.Attribute, attribute));
				getItems().add(changeMulItem);
	
				getItems().add(new SeparatorMenuItem());

				MenuItem genGetterItem = new MenuItem("Generate Getter");
				genGetterItem.setOnAction(e -> actions.generateGetter(object, attribute));
				genGetterItem.setDisable(attribute == null);
				getItems().add(genGetterItem);
				
				MenuItem genSetterItem = new MenuItem("Generate Setter");
				genSetterItem.setOnAction(e -> actions.generateSetter(object, attribute));
				genSetterItem.setDisable(attribute == null);
				getItems().add(genSetterItem);
	
				getItems().add(new SeparatorMenuItem());
				
				MenuItem removeItem = new MenuItem("Remove Attribute");
				removeItem.setOnAction(e -> actions.removeDialog(object, PropertyType.Attribute, attribute));
				getItems().add(removeItem);
			} else {
				MenuItem selectDefiningClassItem = new MenuItem("Select Defining Class");
				selectDefiningClassItem.setOnAction(e -> {
					FmmlxObject o = packageViewer.getObjectByPath(attribute.getOwnerPath());
					FmmlxAttribute a = o.getAttributeByName(attribute.getName()); // to get original att definition					
				    packageViewer.setSelectedObjectAndProperty(o,a);
				});
				getItems().add(selectDefiningClassItem);
			}
		}
	}
}
