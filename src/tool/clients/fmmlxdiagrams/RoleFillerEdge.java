package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;

public class RoleFillerEdge extends Edge<FmmlxObject> {

	public RoleFillerEdge(String path, String childPath, String parentID, Vector<Point2D> intermediatePoints,
			PortRegion startPortRegion, PortRegion endPortRegion,
			AbstractPackageViewer diagram) {
		super(path, diagram.getObjectByPath(childPath), diagram.getObjectByPath(parentID), intermediatePoints, startPortRegion, endPortRegion, new Vector<>(),
				diagram);
	}

	protected void checkVisibilityMode() {visible = true;}
	
	@Override
	protected void layoutLabels(FmmlxDiagram diagram) {
		layoutingFinishedSuccesfully = true;
	} // NONE

	@Override
	public ContextMenu getContextMenuLocal(DiagramActions actions) {
		MenuItem deleteDelegationItem = new MenuItem("delete");
		ContextMenu menu = new ContextMenu();
		menu.getItems().add(deleteDelegationItem);
		deleteDelegationItem.setOnAction(e -> {
			diagram.getComm().removeRoleFiller(diagram.getID(), sourceEnd.getNode().getName());
		    diagram.updateDiagram();
		});
		return menu;
	}

	protected Color getPrimaryColor() {
		return Color.CORNFLOWERBLUE;
	}

	@Override
	public HeadStyle getTargetDecoration() {
		return HeadStyle.NO_ARROW;
	}

	@Override
	public HeadStyle getSourceDecoration() {
		return HeadStyle.CIRCLE;
	}
	
	@Override
	public String getName() {
		return "doesNotMatter";
	}
}
