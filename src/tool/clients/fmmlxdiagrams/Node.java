package tool.clients.fmmlxdiagrams;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import org.w3c.dom.Element;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.fmmlxdiagrams.graphics.NodeGroup;
import tool.clients.xmlManipulator.XmlHandler;

public abstract class Node implements CanvasElement {
	
	protected boolean hidden;
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	protected transient Point2D lastClick = null;
	private FmmlxObjectPort port;
	
	transient boolean requiresReLayout;
	NodeGroup rootNodeElement = null;
	
	
	public void triggerLayout() {
		this.requiresReLayout = true;
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public double getCenterX() { return x + width / 2.; }
	public double getCenterY() { return y + height / 2.; }
	public double getRightX() { return x + width; }
	public double getBottomY() { return y + height; }
	
	public Node() {
		this.port = new FmmlxObjectPort(this);
	}

	@Override
	public void paintOn(GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane view) {
		
		if(hidden) return;		
		if(requiresReLayout) layout(view.getDiagram());
		boolean selected = view.getDiagram().isSelected(this);
		
		if (rootNodeElement != null) {
			Bounds bounds = rootNodeElement.getBounds();
//			if(bounds != null) {
//			g.setFill(selected?Color.web("0xeedddd"):Color.web("0xddddee"));
//			Affine a = new Affine(view.getCanvasTransform());
//			a.append(rootNodeElement.getDragAffine());
//			g.setTransform(a);
//			g.fillRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());}
		
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
	
	@Override
	public void moveTo(double x, double y, FmmlxDiagram.DiagramViewPane diagram) {
		this.x = Math.max(x, 0.0);
		this.y = Math.max(y, 0.0);
	}
	
	@Override // no longer used
	public void setOffsetAndStoreLastValidPosition(Point2D p) {	}
	
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
//		NodeGroup group = (NodeGroup) rootNodeElement;
		Affine a = new Affine(rootNodeElement.getMyTransform());
		a.append(rootNodeElement.getDragAffine());		
		return a;
	}
}
