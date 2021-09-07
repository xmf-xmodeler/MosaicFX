package tool.clients.fmmlxdiagrams.graphics;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;

import org.w3c.dom.Element;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.xmlManipulator.XmlHandler;

import java.util.Vector;

public class NodeBox implements NodeElement {

	public interface LineWidthGetter {
		double getWidth(boolean selected);
	}

	double x;
	double y;
	double width;
	double height;
	Paint bgColor;
	Paint fgColor;
	LineWidthGetter lineWidth;
	private Vector<NodeElement> nodeElements = new Vector<>();
	private PropertyType propertyType;
	private NodeElement owner;

	public NodeBox(double x, double y, double width, double height, Paint bgColor, Paint fgColor, LineWidthGetter lineWidth, PropertyType propertyType) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.bgColor = bgColor;
		this.fgColor = fgColor;
		this.lineWidth = lineWidth;
		this.propertyType = propertyType;
	}

	@Override
	public void paintOn(GraphicsContext g, FmmlxDiagram.DiagramViewPane diagram, boolean objectIsSelected) {
		g.setTransform(getTotalTransform(diagram.getCanvasTransform()));
		g.setFill(bgColor);
		g.fillRect(0,0, width, height);
		g.setStroke(fgColor);
		g.setLineWidth(lineWidth.getWidth(objectIsSelected));
		g.strokeRect(0,0, width, height);
		for (NodeElement e : new Vector<>(nodeElements)) {
			e.paintOn(g, diagram, objectIsSelected);
		}
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, GraphicsContext g, FmmlxDiagram.DiagramViewPane diagram) {
		boolean hit = false;
		g.setTransform(getTotalTransform(diagram.getCanvasTransform()));
		g.beginPath();
		g.moveTo(0, 0); g.lineTo(0, height); g.lineTo(width, height); g.lineTo(width, 0); g.lineTo(0, 0);
		hit = g.isPointInPath(mouseX, mouseY);
		g.closePath();
		return hit;
	}

	public PropertyType getElementType() {
		return propertyType;
	}

	@Override public double getX() {return x;}
	@Override public double getY() {return y;}
	
	@Override public NodeBaseElement getHitLabel(Point2D mouse, GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane diagram) {
//		if(isHit(mouse.getX(), mouse.getY(), g, currentTransform, diagram)) {
			Affine myTransform = new Affine(1, 0, x, 0, 1, y);
			currentTransform = new Affine(currentTransform); // copy
			currentTransform.append(myTransform);
			NodeBaseElement hitLabel = null;
			for(NodeElement e : nodeElements) if(hitLabel == null) {
				 hitLabel = e.getHitLabel(mouse, g, currentTransform, diagram);
			}
			return hitLabel;
//		} else {
//			return null;
//		} 
	}

	@Override
	public void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element group, double xOffset, double yOffset, boolean objectIsSelected) {
		String backgroundColor = bgColor.toString().split("x")[1].substring(0,6);
		String foregroundColor = fgColor.toString().split("x")[1].substring(0,6);

		Element rect = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_RECT);
		rect.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, (x + xOffset)+"");
		rect.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, (y + yOffset)+"");
		rect.setAttribute(SvgConstant.ATTRIBUTE_HEIGHT, height+"");
		rect.setAttribute(SvgConstant.ATTRIBUTE_WIDTH, width+"");
		rect.setAttribute(SvgConstant.ATTRIBUTE_FILL, "#"+backgroundColor);
		rect.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+foregroundColor);
		rect.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, lineWidth.getWidth(objectIsSelected)+"");
		rect.setAttribute(SvgConstant.ATTRIBUTE_FILL_OPACITY, 1 +"");
		xmlHandler.addXmlElement(group, rect);
		for(NodeElement e : nodeElements){
			e.paintToSvg(diagram, xmlHandler, group, x+xOffset, y+yOffset, objectIsSelected);
		}

	}

	public void addNodeElement(NodeElement nodeElement) {
		nodeElements.add(nodeElement);
		nodeElement.setOwner(this);		
	}
	
	public final Affine getMyTransform() {	return new Affine(1, 0, x, 0, 1, y); }
	
	public Affine getTotalTransform(Affine canvasTransform) {
		Affine a = new Affine(owner == null?canvasTransform:owner.getTotalTransform(canvasTransform));
		a.append(new Affine(1, 0, x, 0, 1, y));
		return a;
	}

	public void setOwner(NodeElement owner) {
		this.owner = owner;
	}
}
