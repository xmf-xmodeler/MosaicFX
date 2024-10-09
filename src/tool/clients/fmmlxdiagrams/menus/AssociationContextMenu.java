package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.xmodeler.XModeler;

public class AssociationContextMenu extends ContextMenu {

//	private final FmmlxAssociation association;
//	private final DiagramActions actions;

	public AssociationContextMenu(final FmmlxAssociation association, final DiagramActions actions) {
		setAutoHide(true);
//		this.association = association;
//		this.actions = actions;

		MenuItem editItem = new MenuItem("Edit");
		editItem.setOnAction(e -> actions.editAssociationDialog(association));
		
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction(e -> actions.removeAssociation(association));
		
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

		getItems().addAll(editItem, removeItem,
				new SeparatorMenuItem(),
				genSource2TargetGetterItem, genTarget2SourceGetterItem);
		
//		if(XModeler.isAlphaMode()) {
			if(association.isDependent()) {
				MenuItem removeDependencyItem = new MenuItem("Remove Dependency");
				removeDependencyItem.setOnAction(e -> actions.removeAssociationDependency(association));
				getItems().add(removeDependencyItem);
			} else {
				MenuItem addDependencyItem = new MenuItem("Add Dependency");
				addDependencyItem.setOnAction(e -> actions.addAssociationDependency(association));
				getItems().add(addDependencyItem);
			}
//		}
	}
}
