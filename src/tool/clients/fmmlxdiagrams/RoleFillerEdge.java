package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;

public class RoleFillerEdge extends Edge {

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
		return new ContextMenu();
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
