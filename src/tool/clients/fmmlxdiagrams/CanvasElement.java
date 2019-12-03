package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;

public interface CanvasElement {

	void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram);

	ContextMenu getContextMenu(DiagramActions actions);

	void moveTo(double d, double e, FmmlxDiagram diagram);
	
	boolean isHit(double x, double y);

	void highlightElementAt(Point2D p);
	void unHighlight();

	void setOffsetAndStoreLastValidPosition(Point2D p);
	
	double getMouseMoveOffsetX();
	double getMouseMoveOffsetY();


}
