package tool.clients.fmmlxdiagrams.menus;

import java.util.Vector;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class InheritanceEdgeContextMenu extends ContextMenu {

	public InheritanceEdgeContextMenu(AbstractPackageViewer diagram, FmmlxObject sourceNode, FmmlxObject targetNode) {
		MenuItem deleteInheritanceRelation = new MenuItem("Delete inheritance relation");
		deleteInheritanceRelation.setOnAction( e -> deleteInheritanceRelation(diagram, sourceNode, targetNode));
		getItems().add(deleteInheritanceRelation);
	}
	
	private void deleteInheritanceRelation(AbstractPackageViewer diagram, FmmlxObject sourceNode, FmmlxObject targetNode) {
		Vector<String> newParentNames = new Vector<>();
		Vector<String> oldParentNames = new Vector<>();
		Vector<String> oldParents = sourceNode.getParentsPaths();
		for (int i = 0; i < oldParents.size(); i++) {
			if (!oldParents.get(i).equals(targetNode.getPath())) {
				newParentNames.add(name(oldParents.get(i)));
			}
			oldParentNames.add(name(oldParents.get(i)));
		}	
		FmmlxDiagramCommunicator.getCommunicator().changeParent(diagram.getID(), sourceNode.getName(), oldParentNames, newParentNames);
		diagram.updateDiagram();
	}

	private String name(String s) {
		int n = s.lastIndexOf("::")+2;
		return s.substring(n);
	}
}