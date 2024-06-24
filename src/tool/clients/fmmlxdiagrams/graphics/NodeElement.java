package tool.clients.fmmlxdiagrams.graphics;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.util.Vector;

import org.apache.batik.anim.dom.SVGOMElement;
import org.w3c.dom.Element;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.xmlManipulator.XmlHandler;

public abstract class NodeElement {

	protected FmmlxProperty actionObject;
	protected boolean selected = false;
		
	protected Affine myTransform; // where to be painted if the zoom were 1 and the origin has not moved
	protected NodeGroup owner;
	Bounds bounds = new BoundingBox(0, 0, 0, 0);
	public Style style;
	protected String id;
	protected Action action;
	
	public interface Action{
		public void perform();
	}
	
	abstract NodeElement getHitElement(Point2D mouse, GraphicsContext g,  Affine currentTransform, FmmlxDiagram.DiagramCanvas diagram);
	abstract Action getAction(Point2D mouse, GraphicsContext g,  Affine currentTransform, FmmlxDiagram.DiagramCanvas diagram);
	abstract void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element parentGroup);
	public final void setSelected() { selected = true;}
	public final void setDeselected() { selected = false;}
	public final FmmlxProperty getActionObject() { return actionObject;}
//	public final void performDoubleClickAction(View view) { if(action!=null) action.perform();}
	
	public NodeElement() {
		this.dragAffine = new Affine();
	}
	
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
	public abstract boolean isHit(double mouseX, double mouseY, FmmlxDiagram.DiagramCanvas diagramView);	
    
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
	public abstract Affine getTotalTransform(Affine canvasTransform);

	public abstract void setOwner(NodeGroup owner);
	public NodeGroup getOwner() {return owner;}

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

	public abstract Vector<NodeElement> getChildren();
	
	protected void setID(SVGOMElement node) {
		String id = node.getId();
		this.id = ("".equals(id) || id == null)?null:node.getId();
	}
	
	public void setID(String id) {
		this.id = id;
	}
	
	// Anchor is CENTRE by default.
	// if not, the reference frame is shifted by the difference of the corner/side and the centre.   
	public static enum Anchor {
		NORTHWEST,   NORTH,  NORTHEAST,
             WEST,   CENTRE,      EAST, 
		SOUTHWEST,   SOUTH,  SOUTHEAST;
	}
	
	protected abstract NodeElement createInstance(FmmlxObject object, Vector<Modification> modifications, Vector<ActionInfo> actions, FmmlxDiagram diagram);
	
	protected void saveTransformation(Element myElement) {
		myElement.setAttribute("xx", myTransform.getMxx()+"");
		myElement.setAttribute("yy", myTransform.getMyy()+"");
		myElement.setAttribute("xy", myTransform.getMxy()+"");
		myElement.setAttribute("yx", myTransform.getMyx()+"");
		myElement.setAttribute("tx", myTransform.getTx()+"");
		myElement.setAttribute("ty", myTransform.getTy()+"");
	}
	
	public boolean matchID(String svgID, String localID) {
		if(!localID.equals(this.id)) return false;
		return true; //matchParentId(svgID);
	}

//	protected boolean matchParentId(String parentID) {
//		if(parentID.equals(this.id)) return true;
//		if(owner == null) return false;
//		return owner.matchParentId(parentID);
//	}
	
	public NodeGroup getRoot() {
		NodeGroup owner = getOwner();
		return owner == null?null:owner.getRoot();
	}
	
	public void setMyTransform(Affine affine) {
		this.myTransform = affine;		
	}
	
	public Affine getZoomViewTransform(Canvas canvas) {
		updateBounds();
		if(getBounds() == null) return new Affine();
		double minX = getBounds().getMinX();
		double minY = getBounds().getMinY();
		double maxX = getBounds().getMaxX();
		double maxY = getBounds().getMaxY();

		double xZoom = canvas.getWidth() / (maxX - minX); 
		double yZoom = canvas.getHeight() / (maxY - minY);
		double zoom = Math.min(xZoom, yZoom) * 0.7;
		

		return new Affine(zoom,    0, -zoom*(minX + maxX)/2 + canvas.getWidth()/2,
	                         0, zoom, -zoom*(minY + maxY)/2 + canvas.getHeight()/2);
	}
	
	public Affine getZoomViewTransform(int w, int h) {
		updateBounds();
		if(getBounds() == null) return new Affine();
		double minX = getBounds().getMinX();
		double minY = getBounds().getMinY();
		double maxX = getBounds().getMaxX();
		double maxY = getBounds().getMaxY();

		double xZoom = w / (maxX - minX); 
		double yZoom = h / (maxY - minY);
		double zoom = Math.min(xZoom, yZoom) * 0.7;

		return new Affine(zoom,    0, -zoom*(minX + maxX)/2 + w/2,
	                         0, zoom, -zoom*(minY + maxY)/2 + h/2);
	}
	
	public static String color2Web(Color c) {
		String r = Integer.toHexString((int)(c.getRed()*255));
		String g = Integer.toHexString((int)(c.getGreen()*255));
		String b = Integer.toHexString((int)(c.getBlue()*255));
		String a = Integer.toHexString((int)(c.getOpacity()*255));
		if(r.length() == 1) r = "0"+r;
		if(g.length() == 1) g = "0"+g;
		if(b.length() == 1) b = "0"+b;
		if(a.length() == 1) a = "0"+a;
		return "0x"+r+g+b+a;
	}
	
	public String getID() {
		return id;
	}	

	public NodeElement getElement(String id) {
		if(id.equals(this.id)) return this;
		for(NodeElement child : getChildren()) {
			NodeElement foundElement = child.getElement(id);
			if(foundElement != null) return foundElement;
		}
		return null;
	}
	
	public boolean isInsideSVG() {
		boolean insideSVG = false;
		NodeElement svgRoot = this;
		while(!insideSVG && svgRoot != null) {
			insideSVG |= (svgRoot != this) && (svgRoot instanceof SVGGroup);
			svgRoot = svgRoot.getOwner();
		}
		return insideSVG;
	}
	
	private transient Affine dragAffine;

	public void dragTo(Affine dragAffine) {
		this.dragAffine = dragAffine;		
	}

	public void drop() {
		myTransform.append(dragAffine);
		dragAffine = new Affine();		
	}

	public Transform getDragAffine() {
		if(dragAffine == null) return new Affine(); // HACK
		return dragAffine;
	}
  
	protected void setActionObject(FmmlxProperty actionObject) {
		this.actionObject = actionObject;
  }
	
	public void setPosition(double x, double y) {
		myTransform = new Affine(1,0,x,0,1,y);
  }

	protected void setAction(Action action) {
		this.action = action;
	}
}
