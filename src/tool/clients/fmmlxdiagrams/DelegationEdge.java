package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;

public class DelegationEdge extends Edge {

	public DelegationEdge(int id, int childID, int parentID, Vector<Point2D> intermediatePoints,
			PortRegion startPortRegion, PortRegion endPortRegion,
			FmmlxDiagram diagram) {
		super(id, diagram.getObjectById(childID), diagram.getObjectById(parentID), intermediatePoints, startPortRegion, endPortRegion, new Vector<>(),
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
		return Color.DARKBLUE;
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
	
	public boolean isVisible() {
		return visible;
	}
}
