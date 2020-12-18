package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;

public class RoleFillerEdge extends Edge {

	public RoleFillerEdge(int id, String childPath, String parentID, Vector<Point2D> intermediatePoints,
			PortRegion startPortRegion, PortRegion endPortRegion,
			FmmlxDiagram diagram) {
		super(id, diagram.getObjectByName(childPath), diagram.getObjectByName(parentID), intermediatePoints, startPortRegion, endPortRegion, new Vector<>(),
				diagram);
	}

	protected void checkVisibilityMode() {visible = true;}
	
	@Override
	protected void layoutLabels() {
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
	public void setIntermediatePoints(Vector<Point2D> intermediatePoints) {
		super.intermediatePoints = intermediatePoints;
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
