package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public interface NodeElement {

	public void paintOn(GraphicsContext g, double xOffset, double yOffset, FmmlxDiagram diagram, boolean objectIsSelected);

	public boolean isHit(double mouseX, double mouseY);

	public double getX();

	public double getY();

	public NodeLabel getHitLabel(Point2D pointRelativeToParent);

}
