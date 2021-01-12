package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public interface NodeElement {

	void paintOn(GraphicsContext g, double xOffset, double yOffset, FmmlxDiagram diagram, boolean objectIsSelected);

	boolean isHit(double mouseX, double mouseY);

	double getX();

	double getY();

	NodeBaseElement getHitLabel(Point2D pointRelativeToParent);

}
