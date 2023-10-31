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
		@SuppressWarnings("unchecked") Vector<String> newParents = (Vector<String>) sourceNode.getParentsPaths().clone();
		for (int i = 0; i < newParents.size(); i++) {
			if (newParents.get(i).equals(targetNode.getPath())) {
				newParents.remove(i);
			}
		}		
		FmmlxDiagramCommunicator.getCommunicator().changeParent(diagram.getID(), sourceNode.getName() , sourceNode.getParentsPaths(), newParents);
		diagram.updateDiagram();
	}
}