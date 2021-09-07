package tool.clients.fmmlxdiagrams.graphics;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import org.w3c.dom.Element;
import javafx.scene.transform.Affine;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.xmlManipulator.XmlHandler;

public interface NodeElement {

	public void paintOn(GraphicsContext g, FmmlxDiagram.DiagramViewPane diagram, boolean objectIsSelected);

	public boolean isHit(double mouseX, double mouseY, GraphicsContext g, FmmlxDiagram.DiagramViewPane diagram);

	double getX();

	double getY();

	NodeBaseElement getHitLabel(Point2D mouse, GraphicsContext g,  Affine currentTransform, FmmlxDiagram.DiagramViewPane diagram);

    void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element group, double xOffset, double yOffset, boolean objectIsSelected);

    Affine getMyTransform();
    Affine getTotalTransform(Affine canvasTransform);

	public void setOwner(NodeElement owner);

}
