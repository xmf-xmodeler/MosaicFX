package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.transform.Affine;
import tool.clients.xmlManipulator.XmlHandler;

public interface CanvasElement {

	void paintOn(GraphicsContext g, Affine currentAffine, FmmlxDiagram.DiagramViewPane fmmlxDiagram);

	ContextMenu getContextMenu(FmmlxDiagram.DiagramViewPane fmmlxDiagram, Point2D absolutePoint);

	void moveTo(double d, double e, FmmlxDiagram.DiagramViewPane diagram);
	
	boolean isHit(double x, double y, GraphicsContext g,  Affine currentTransform, FmmlxDiagram.DiagramViewPane diagram);

	void highlightElementAt(Point2D mouse, Affine canvasTransform);
	void unHighlight();

//	void setOffsetAndStoreLastValidPosition(Point2D p);

    void paintToSvg(XmlHandler xmlHandler, FmmlxDiagram diagram);
}
