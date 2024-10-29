package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class BrowserAssociationContextMenu extends ContextMenu {

	private final FmmlxObject object;
	private final FmmlxAssociation association;
	private final DiagramActions actions;
	
	public BrowserAssociationContextMenu(ListView<FmmlxObject> oListView, ListView<FmmlxAssociation> aListView, AbstractPackageViewer packageViewer){
		this.actions=packageViewer.getActions();
		this.object=oListView.getSelectionModel().getSelectedItem();
		this.association=aListView.getSelectionModel().getSelectedItem();
		setAutoHide(true);
		
		if(object != null && object.isClass()) {
			MenuItem addItem = new MenuItem("Add Association");
			addItem.setOnAction(e -> actions.addAssociationDialog(object,null,null));
			
			MenuItem editItem = new MenuItem("Edit Association");
			editItem.setOnAction(e-> actions.editAssociationDialog(association));
			
			MenuItem removeItem = new MenuItem("Remove Association");
			removeItem.setOnAction(e -> actions.removeAssociation(association));
			
			
			if(association == null) {
				getItems().addAll(addItem);}
			else {			
				MenuItem genSource2TargetGetterItem = new MenuItem("Generate Getter Source->Target");
				genSource2TargetGetterItem.setOnAction(e -> actions.generateAssocGetter(
					association.sourceEnd.getNode(),
					association.targetEnd.getNode(),
					association.getAccessNameStartToEnd(),
					association.getLevelSource(),
					association.getMultiplicityStartToEnd()));
				
				MenuItem genTarget2SourceGetterItem = new MenuItem("Generate Getter Target->Source");
				genTarget2SourceGetterItem.setOnAction(e -> actions.generateAssocGetter(
					association.targetEnd.getNode(),
					association.sourceEnd.getNode(),
					association.getAccessNameEndToStart(),
					association.getLevelTarget(),
					association.getMultiplicityEndToStart()));
				genTarget2SourceGetterItem.setDisable(!association.isSourceVisible());
				
				getItems().addAll(addItem, editItem, removeItem, 
						new SeparatorMenuItem(),
						genSource2TargetGetterItem, genTarget2SourceGetterItem);}
		}
		
	}
}
