package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.xmlManipulator.XmlHandler;

public interface CanvasElement {

	void paintOn(GraphicsContext g, Affine currentAffine, FmmlxDiagram.DiagramCanvas fmmlxDiagram);

	ContextMenu getContextMenu(FmmlxDiagram.DiagramCanvas fmmlxDiagram, Point2D absolutePoint);

	boolean isHit(double x, double y, GraphicsContext g,  Affine currentTransform, FmmlxDiagram.DiagramCanvas diagram);

	void highlightElementAt(Point2D mouse, Affine canvasTransform);
	void unHighlight();

    void paintToSvg(XmlHandler xmlHandler, FmmlxDiagram diagram);

	boolean isHidden();

	Double getLeftX();

	Double getRightX();

	Double getTopY();

	Double getBottomY();
}
