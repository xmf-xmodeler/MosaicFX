package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.Edge.HeadStyle;

public class InheritanceEdge extends Edge {

	public InheritanceEdge(int id, int childID, int parentID, Vector<Point2D> intermediatePoints,
			FmmlxDiagram diagram) {
		super(id, diagram.getObjectById(childID), diagram.getObjectById(parentID), intermediatePoints, new Vector<>(),
				diagram);
	}

	protected void checkVisibilityMode() {visible = endNode.getPointForEdge(this, false).distance(startNode.getPointForEdge(this, true))<1000;}
	
	@Override
	protected void layoutLabels() {
		layoutingFinishedSuccesfully = true;
	} // NONE

	@Override
	public ContextMenu getContextMenuLocal(DiagramActions actions) {
		return new ContextMenu();
	}

	protected Color getPrimaryColor() {
		return Color.BLACK;
	}

	@Override
	public HeadStyle getTargetDecoration() {
		return HeadStyle.FULL_TRIANGLE;
	}

	@Override
	public HeadStyle getSourceDecoration() {
		return HeadStyle.NO_ARROW;
	}
}
