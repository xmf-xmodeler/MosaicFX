package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;

public class InheritanceEdge extends Edge {

	public InheritanceEdge(
			int id, 
			int childID, 
			int parentID, 
			Vector<Point2D> intermediatePoints,
			FmmlxDiagram diagram) {
		super(id, diagram.getObjectById(childID), diagram.getObjectById(parentID), intermediatePoints, new Vector<>(), diagram);
	}

	@Override
	protected void layoutLabels() {layoutingFinishedSuccesfully = true;} // NONE

	@Override
	public ContextMenu getContextMenu(DiagramActions actions) {
		return null;
	}
	
	protected Color getPrimaryColor() {
		return new Color(.9,.3,.5,1.);
	}
}
