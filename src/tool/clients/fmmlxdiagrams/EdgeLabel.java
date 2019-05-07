package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class EdgeLabel implements CanvasElement {

	Point2D offset;
	Edge parent;
	Anchor anchor;
	
	@Override
	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram) {
		Point2D anchorPosition = parent.getAnchorPosition(anchor);
		Point2D position = new Point2D(offset.getX() + anchorPosition.getX() + xOffset, offset.getY() + anchorPosition.getY() + yOffset);
	}
	
	public enum Anchor{START, MID, END}
	
}
