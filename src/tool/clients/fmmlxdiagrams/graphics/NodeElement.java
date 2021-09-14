package tool.clients.fmmlxdiagrams.graphics;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import org.w3c.dom.Element;
import javafx.scene.transform.Affine;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.xmlManipulator.XmlHandler;

public interface NodeElement {

	/**
	 * Paints this NodeElement and all its children to the diagramView's canvas.
	 * @param diagramView the view the element will be painted on
	 * @param objectIsSelected when the element should be displayed as selected.
	 */
	public void paintOn(FmmlxDiagram.DiagramViewPane diagramView, boolean objectIsSelected);

	/**
	 * Checks whether this NodeElement has been hit with the mouse
	 * @param mouseX
	 * @param mouseY
	 * @param diagramView
	 * @return whether it has been hit
	 */
	public boolean isHit(double mouseX, double mouseY, FmmlxDiagram.DiagramViewPane diagramView);

	NodeBaseElement getHitLabel(Point2D mouse, GraphicsContext g,  Affine currentTransform, FmmlxDiagram.DiagramViewPane diagram);

    void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element parentGroup);

    /**
     * Returns the element's own transform, relative to its parent
     * @return the element's own transform, relative to its parent
     */
    Affine getMyTransform();
    
    /**
     * Returns the element's combined transform, with its parent's transforms prepended 
     * recursively and then the canvas's transform prepended. To be used before painting 
     * it to a GraphicsContext or to check whether it is is hit by the mouse
     * @param canvasTransform the transform of the canvas
     * @return the total transform
     */
    Affine getTotalTransform(Affine canvasTransform);

	public void setOwner(NodeElement owner);

}
