package tool.clients.fmmlxdiagrams;

import java.util.Collections;
import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;

import org.w3c.dom.Element;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.fmmlxdiagrams.graphics.NodeElement;
import tool.clients.xmlManipulator.XmlHandler;

public abstract class Node implements CanvasElement{

	private transient double mouseMoveOffsetX;
	private transient double mouseMoveOffsetY;	
	
	protected boolean hidden;
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	protected transient Point2D lastClick = null;
	private FmmlxObjectPort port;
	
	transient boolean requiresReLayout;
	Vector<NodeElement> nodeElements = new Vector<>();
	
	
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

	@Override @Deprecated public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram diagram) {
		
		if(hidden) return;
		
		if(requiresReLayout) layout(diagram);

		boolean selected = diagram.isSelected(this);

		Affine transform = new Affine();
		transform.appendTranslation(x + xOffset, y + yOffset);
		
		for (NodeElement e : nodeElements) {
			e.paintOn(g, transform, diagram, selected);
		}
	}
	
	@Override
	public void paintOn(GraphicsContext g, Affine currentTransform, FmmlxDiagram diagram) {
		
		if(hidden) return;		
		if(requiresReLayout) layout(diagram);
		boolean selected = diagram.isSelected(this);
		
//		Affine myTransform = new Affine(1, 0, x, 0, 1, y);
//		currentTransform = new Affine(currentTransform); // copy
//		currentTransform.append(myTransform);

//		Affine newTransform = new Affine();
//		transform.appendTranslation(x + xOffset, y + yOffset);
		
		for (NodeElement e : nodeElements) {
			e.paintOn(g, currentTransform, diagram, selected);
		}
	}

	@Override
	public void paintToSvg(XmlHandler xmlHandler, int xOffset, int yOffset, FmmlxDiagram diagram) {

		if(hidden) return;

		if(requiresReLayout) layout(diagram);

		boolean selected = diagram.isSelected(this);
		Element group = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_GROUP);
		group.setAttribute(SvgConstant.ATTRIBUTE_GROUP_TYPE, "object");

		Vector<NodeElement> nodesTobePainted = nodeElements;
		Collections.reverse(nodesTobePainted);
		for(NodeElement nodeElement : nodesTobePainted){
			nodeElement.paintToSvg(diagram, xmlHandler, group, x+xOffset, y+yOffset, selected);
		}
		xmlHandler.addXmlElement(xmlHandler.getRoot(), group);
	}
	
	@Override
	public boolean isHit(double mouseX, double mouseY, GraphicsContext g,  Affine currentTransform, FmmlxDiagram diagram) {
		if(hidden) return false;
//		Affine myTransform = new Affine(1, 0, x, 0, 1, y);
//		currentTransform = new Affine(currentTransform); // copy
//		currentTransform.append(myTransform);
		for(NodeElement n : nodeElements) {
			if(n.isHit(mouseX, mouseY, g, diagram)) return true;
		}
		return false;
	}

	protected abstract void layout(FmmlxDiagram diagram) ;
	
	@Override
	public void moveTo(double x, double y, FmmlxDiagram diagram) {
		this.x = Math.max(x, 0.0);
		this.y = Math.max(y, 0.0);
	}
	
	@Override
	public void setOffsetAndStoreLastValidPosition(Point2D p) {
		mouseMoveOffsetX = p.getX() - x;
		mouseMoveOffsetY = p.getY() - y;
	}
	
	@Override public double getMouseMoveOffsetX() {return mouseMoveOffsetX;}
	@Override public double getMouseMoveOffsetY() {return mouseMoveOffsetY;}
	@Override public void highlightElementAt(Point2D p) {}
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

	public void updatePortOder() {
		port.sortAllPorts();
	}

	public abstract String getName();

}
