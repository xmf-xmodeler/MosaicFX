package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;

public class InheritanceEdge extends Edge {

	public InheritanceEdge(String path, String childPath, String parentPath, Vector<Point2D> intermediatePoints,
			PortRegion startPortRegion, PortRegion endPortRegion,
			AbstractPackageViewer diagram) {
		super(path, diagram.getObjectByPath(childPath), diagram.getObjectByPath(parentPath), intermediatePoints, startPortRegion, endPortRegion, new Vector<>(),
				diagram);
	}

	protected void checkVisibilityMode() {visible = targetNode.getPointForEdge(targetEnd, false).distance(sourceNode.getPointForEdge(sourceEnd, true))<1000;}
	
	@Override
	protected void layoutLabels(FmmlxDiagram diagram) {
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

	@Override
	public String getName() {
		return "doesNotMatter";
	}
}
