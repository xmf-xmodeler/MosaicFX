package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.transform.Affine;
import tool.clients.xmlManipulator.XmlHandler;

public interface CanvasElement {

	@Deprecated void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram);
	void paintOn(GraphicsContext g, Affine currentAffine, FmmlxDiagram fmmlxDiagram);

	ContextMenu getContextMenu(FmmlxDiagram fmmlxDiagram, Point2D absolutePoint);

	void moveTo(double d, double e, FmmlxDiagram diagram);
	
	boolean isHit(double x, double y, GraphicsContext g,  Affine currentTransform, FmmlxDiagram diagram);

	void highlightElementAt(Point2D p);
	void unHighlight();

	void setOffsetAndStoreLastValidPosition(Point2D p);
	
	double getMouseMoveOffsetX();
	double getMouseMoveOffsetY();

    void paintToSvg(XmlHandler xmlHandler, int xOffset, int yOffset, FmmlxDiagram diagram);
}
