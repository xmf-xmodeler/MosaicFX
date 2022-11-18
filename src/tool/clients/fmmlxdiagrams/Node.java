package tool.clients.fmmlxdiagrams;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import org.w3c.dom.Element;

import tool.clients.fmmlxdiagrams.graphics.NodeGroup;
import tool.clients.fmmlxdiagrams.graphics.SvgConstant;
import tool.clients.xmlManipulator.XmlHandler;

public abstract class Node implements CanvasElement {
	
	protected boolean hidden;
	protected double x;
	protected double y;
	protected transient Point2D lastClick = null;
	private FmmlxObjectPort port;
	
	transient boolean requiresReLayout;
	NodeGroup rootNodeElement = null;	
	
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

	public abstract String getName();

	public Affine getOwnAndDragTransform() {
		Affine a = new Affine(rootNodeElement.getMyTransform());
		a.append(rootNodeElement.getDragAffine());		
		return a;
	}
}
