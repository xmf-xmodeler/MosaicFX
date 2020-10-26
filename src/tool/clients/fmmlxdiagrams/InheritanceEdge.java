package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;

public class InheritanceEdge extends Edge {

	public InheritanceEdge(int id, int childID, int parentID, Vector<Point2D> intermediatePoints,
			PortRegion startPortRegion, PortRegion endPortRegion,
			FmmlxDiagram diagram) {
		super(id, diagram.getObjectById(childID), diagram.getObjectById(parentID), intermediatePoints, startPortRegion, endPortRegion, new Vector<>(),
				diagram);
	}

	public FmmlxObject getChild(){
		return super.getSourceNode();
	}

	public FmmlxObject getParent(){
		return super.getTargetNode();
	}

	public PortRegion getSourcePort(){
		return super.getSourcePortRegion();
	}

	public PortRegion getTargetPort(){
		return super.getTargetPortRegion();
	}

	protected void checkVisibilityMode() {visible = targetNode.getPointForEdge(targetEnd, false).distance(sourceNode.getPointForEdge(sourceEnd, true))<1000;}
	
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
	public void setIntermediatePoints(Vector<Point2D> intermediatePoints) {
		super.intermediatePoints = intermediatePoints;
	}

	@Override
	public HeadStyle getTargetDecoration() {
		return HeadStyle.FULL_TRIANGLE;
	}

	@Override
	public HeadStyle getSourceDecoration() {
		return HeadStyle.NO_ARROW;
	}
	
	public boolean isVisible() {
		return visible;
	}
}
