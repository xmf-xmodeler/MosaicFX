package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.menus.InheritanceEdgeContextMenu;

public class InheritanceEdge extends Edge<FmmlxObject> {

	public InheritanceEdge(String path, String childPath, String parentPath, Vector<Point2D> intermediatePoints,
			PortRegion startPortRegion, PortRegion endPortRegion,
			AbstractPackageViewer diagram) {
		super(path, diagram.getObjectByPath(childPath), diagram.getObjectByPath(parentPath), intermediatePoints, startPortRegion, endPortRegion, new Vector<>(),
				diagram);
	}

	protected void checkVisibilityMode() {
//		System.err.println("POINT target: "+targetNode.getPointForEdge(targetEnd, false).toString());
//		System.err.println("POINT source: "+sourceNode.getPointForEdge(sourceEnd, true).toString());
		visible = targetNode.getPointForEdge(targetEnd, false).distance(sourceNode.getPointForEdge(sourceEnd, true))<1000;}
	
	@Override
	public void layoutLabels(FmmlxDiagram diagram) {
		layoutingFinishedSuccesfully = true;
	} // NONE

	@Override
	public ContextMenu getContextMenuLocal(DiagramActions actions) {
		return new InheritanceEdgeContextMenu(diagram, sourceNode, targetNode);
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

	@Override
	public String getName() {
		return "doesNotMatter";
	}
}
