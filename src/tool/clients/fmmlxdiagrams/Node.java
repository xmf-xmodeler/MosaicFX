package tool.clients.fmmlxdiagrams;

import org.w3c.dom.Element;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.graphics.GraphicalMappingInfo;

import java.util.Map;

import org.w3c.dom.Element;

import tool.clients.fmmlxdiagrams.graphics.NodeElement;
import tool.clients.fmmlxdiagrams.graphics.NodeGroup;
import tool.clients.fmmlxdiagrams.graphics.SvgConstant;
import tool.clients.xmlManipulator.XmlHandler;

public abstract class Node implements CanvasElement {
	
	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	protected boolean hidden;
	protected double x;
	protected double y;
	protected transient Point2D lastClick = null;
	private FmmlxObjectPort port;
	
	transient boolean requiresReLayout;
	public NodeGroup rootNodeElement = null;	
	
	public void triggerLayout() {
		this.requiresReLayout = true;
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	
	public double getWidth()   { return getBounds().getWidth(); }
	public double getHeight()  { return getBounds().getHeight(); }
	public double getCenterX() { return (getLeftX() + getRightX()) / 2; }
	public double getCenterY() { return (getTopY() + getBottomY()) / 2; }
	public Double getLeftX()   { return getBounds().getMinX(); }
	public Double getRightX()  { return getBounds().getMaxX(); }
	public Double getTopY()    { return getBounds().getMinY(); }
	public Double getBottomY() { return getBounds().getMaxY(); }
	
	private Bounds getBounds() { 
		if(rootNodeElement!=null) {
			Bounds b = rootNodeElement.getBounds();
			if(b != null) return b;
		} // if fail:
		return new BoundingBox(x, y, x, y);
	}
	
	public Node() {
		this.port = new FmmlxObjectPort(this);
	}

	@Override
	public void paintOn(GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane view) {
		
		if(hidden) return;		
		if(requiresReLayout) layout(view.getDiagram());
		boolean selected = view.getDiagram().isSelected(this);
		
		if (rootNodeElement != null) {
			if(selected) {
				g.setFill(Color.web("0xffdddd"));
				Bounds bounds = rootNodeElement.getBounds();
				if(bounds != null) {
					Affine a = new Affine(view.getCanvasTransform());
					a.append(rootNodeElement.getDragAffine());
					g.setTransform(a);
					g.fillRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
				}
			}
			rootNodeElement.paintOn(view, selected);
		}
	}

	@Override
	public void paintToSvg(XmlHandler xmlHandler, FmmlxDiagram diagram) {

		if(hidden) return;

		if(requiresReLayout) layout(diagram);

		Element group = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_GROUP);
		group.setAttribute(SvgConstant.ATTRIBUTE_GROUP_TYPE, "object");
		group.setAttribute("XModeler", "Node");
		if(rootNodeElement != null){
			rootNodeElement.paintToSvg(diagram, xmlHandler, group);
		}
		xmlHandler.addXmlElement(xmlHandler.getRoot(), group);
	}
	
	@Override
	public boolean isHit(double mouseX, double mouseY, GraphicsContext g,  Affine currentTransform, FmmlxDiagram.DiagramViewPane diagram) {
		if(hidden) return false;
		if(rootNodeElement != null){
			if(rootNodeElement.isHit(mouseX, mouseY, diagram)) return true;
		}
		return false;
	}

	protected abstract void layout(FmmlxDiagram diagram) ;
	
	@Override public void highlightElementAt(Point2D p, Affine a) {}
	@Override public void unHighlight() {}

	public boolean isHidden() {
		return hidden;
	}
	
	public Point2D getPointForEdge(Edge<?>.End edge, boolean isStartNode) {
		return port.getPointForEdge(edge, isStartNode);
	}

	public PortRegion getDirectionForEdge(Edge<?>.End edge, boolean isStartNode) {
		return port.getDirectionForEdge(edge, isStartNode);
	}
	
	public void setDirectionForEdge(Edge<?>.End edge, boolean isStartNode, PortRegion newPortRegion) {
		port.setDirectionForEdge(edge, isStartNode, newPortRegion);
	}
	
	public void addEdgeEnd(Edge<?>.End edge, PortRegion direction) {
		port.addNewEdge(edge, direction);
	}

	public void updatePortOrder() {
		port.sortAllPorts();
	}

	public Affine getOwnAndDragTransform() {
		Affine a = new Affine(rootNodeElement.getMyTransform());
		a.append(rootNodeElement.getDragAffine());		
		return a;
	}
  
	public void dragTo(Affine dragAffine) {
		rootNodeElement.dragTo(dragAffine);
	}

	public void drop() {
		rootNodeElement.drop();
		this.x = rootNodeElement.getMyTransform().getTx();
		this.y = rootNodeElement.getMyTransform().getTy();
	}

	/**
	 * Please mind, that the use of this method will not alter backend data!!!
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public void setDiagramMapping(GraphicalMappingInfo mapping){
		setX(mapping.getxPosition());
		setY(mapping.getyPosition());
		setHidden(mapping.isHidden());
	}

	protected abstract void updatePositionInBackend(int diagramID);

	/**
	 * Must include the backend update
	 */
	public abstract void hide(AbstractPackageViewer diagram);

	/**
	 * Must include the backend update
	 */
	public abstract void unhide(AbstractPackageViewer diagram);
	
	public void setPosition(double x, double y) {
		setX(x);
		setY(y);
	}
  
	public void performDoubleClickAction(Point2D p, GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane view) {
		if(p == null) return;
		NodeElement.Action action = null;
		if(rootNodeElement != null) if(action == null) {
			action = rootNodeElement.getAction(p, g, currentTransform, view);
		}
		if(action != null) action.perform();
	}
}
