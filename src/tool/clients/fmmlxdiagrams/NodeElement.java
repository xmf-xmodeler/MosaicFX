package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import org.w3c.dom.Element;
import tool.clients.xmlManipulator.XmlHandler;

public interface NodeElement {

	void paintOn(GraphicsContext g, double xOffset, double yOffset, FmmlxDiagram diagram, boolean objectIsSelected);

	boolean isHit(double mouseX, double mouseY);

	double getX();

	double getY();

	NodeBaseElement getHitLabel(Point2D pointRelativeToParent);

    void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element group, double xOffset, double yOffset, boolean objectIsSelected);
}
