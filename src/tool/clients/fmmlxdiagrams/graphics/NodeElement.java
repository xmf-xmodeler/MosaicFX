package tool.clients.fmmlxdiagrams.graphics;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.SVGPath;

import java.util.Vector;

import org.apache.batik.anim.dom.SVGOMElement;
import org.w3c.dom.Element;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.xmlManipulator.XmlHandler;

public abstract class NodeElement {

	protected Affine myTransform; // where to be painted if the zoom were 1 and the origin has not moved
	protected NodeElement owner;
	Bounds bounds = new BoundingBox(0, 0, 0, 0);
	public Style style;
	protected String id;
	
	/**
	 * Paints this NodeElement and all its children to the diagramView's canvas.
	 * @param diagramView the view the element will be painted on
	 * @param objectIsSelected when the element should be displayed as selected.
	 */
	public abstract void paintOn(View diagramView, boolean objectIsSelected);

	/**
	 * Checks whether this NodeElement has been hit with the mouse
	 * @param mouseX
	 * @param mouseY
	 * @param diagramView
	 * @return whether it has been hit
	 */
	public abstract boolean isHit(double mouseX, double mouseY, FmmlxDiagram.DiagramViewPane diagramView);

	abstract NodeBaseElement getHitLabel(Point2D mouse, GraphicsContext g,  Affine currentTransform, FmmlxDiagram.DiagramViewPane diagram);

	abstract void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element parentGroup);

    /**
     * Returns the element's own transform, relative to its parent
     * @return the element's own transform, relative to its parent
     */
	public final Affine getMyTransform() {	
		if (myTransform==null) {
			System.err.println("myTransform not set: "+ this.getClass());
			myTransform = new Affine();
		}
		return myTransform; 
	}
    
    /**
     * Returns the element's combined transform, with its parent's transforms prepended 
     * recursively and then the canvas's transform prepended. To be used before painting 
     * it to a GraphicsContext or to check whether it is is hit by the mouse
     * @param canvasTransform the transform of the canvas
     * @return the total transform
     */
    abstract Affine getTotalTransform(Affine canvasTransform);

	public abstract void setOwner(NodeElement owner);

	public abstract Bounds getBounds();

	public abstract void updateBounds();
	
	protected void updateBoundsFromPath(String textPath) {
		Affine a = getTotalTransform(new Affine());
		SVGPath p = new SVGPath(); 
		p.setContent(textPath);
		p.getTransforms().add(Transform.affine(
				a.getMxx(), a.getMyx(),
				a.getMxy(), a.getMyy(), 
				a.getTx(), a.getTy()));
		this.bounds = p.getBoundsInParent();
	}

	protected abstract Vector<NodeElement> getChildren();
	
	protected void setID(SVGOMElement node) {
		String id = node.getId();
		this.id = ("".equals(id) || id == null)?null:node.getId();
	}
	
	// Anchor is CENTRE by default.
	// if not, the reference frame is shifted by the difference of the corner/side and the centre.   
	public static enum Anchor {
		NORTHWEST,   NORTH,  NORTHEAST,
             WEST,   CENTRE,      EAST, 
		SOUTHWEST,   SOUTH,  SOUTHEAST;
	}
	
	protected abstract NodeElement createInstance(FmmlxObject object, Vector<Modification> modifications);
	
	public boolean matchID(String svgID, String localID) {
		if(!localID.equals(this.id)) return false;
		return matchParentId(svgID);
	}

	private boolean matchParentId(String parentID) {
		if(parentID.equals(this.id)) return true;
		if(owner == null) return false;
		return owner.matchParentId(parentID);
	}
}
