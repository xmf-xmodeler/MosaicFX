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
	public Vector<NodeElement> nodeElements = new Vector<>();
	private PropertyType propertyType;

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
	public void paintOn(GraphicsContext g, Affine currentTransform, FmmlxDiagram diagram, boolean objectIsSelected) {
		Affine myTransform = new Affine(1, 0, x, 0, 1, y);
		currentTransform = new Affine(currentTransform); // copy
		currentTransform.append(myTransform);
		g.setTransform(currentTransform);
		
		try {
			g.setFill(bgColor);
			g.fillRect(0,0, width, height);
//			g.fillRect(x + transform.getTx(), y + transform.getTy(), width, height);
			g.setStroke(/*objectIsSelected&&System.currentTimeMillis()%2400<500?new Color(1.,.8,0.,1.):*/fgColor);
			g.setLineWidth(lineWidth.getWidth(objectIsSelected));
			g.strokeRect(0,0, width, height);
//			g.strokeRect(x + transform.getTx(), y + transform.getTy(), width, height);
//			Affine newTransform = transform.clone(); newTransform.appendTranslation(x, y);
			for (NodeElement e : nodeElements) {
				e.paintOn(g, currentTransform, diagram, objectIsSelected);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, GraphicsContext g, Affine currentTransform) {
		boolean hit = false;
		Affine myTransform = new Affine(1, 0, x, 0, 1, y);
		currentTransform = new Affine(currentTransform); // copy
		currentTransform.append(myTransform);
		g.setTransform(currentTransform);
		g.beginPath();
		g.moveTo(0, 0); g.lineTo(0, height); g.lineTo(width, height); g.lineTo(width, 0); g.lineTo(0, 0);
		hit = g.isPointInPath(mouseX, mouseY);
		g.closePath();
		return hit;
//		Rectangle rec = new Rectangle(x, y, width, height);
//		return rec.contains(mouseX, mouseY);
	}

	public PropertyType getElementType() {
		return propertyType;
	}

	@Override public double getX() {return x;}
	@Override public double getY() {return y;}
	
	@Override public NodeBaseElement getHitLabel(Point2D mouse, GraphicsContext g, Affine currentTransform) {
		if(isHit(mouse.getX(), mouse.getY(), g, currentTransform)) {
			Affine myTransform = new Affine(1, 0, x, 0, 1, y);
			currentTransform = new Affine(currentTransform); // copy
			currentTransform.append(myTransform);
			NodeBaseElement hitLabel = null;
			for(NodeElement e : nodeElements) if(hitLabel == null) {
				 hitLabel = e.getHitLabel(mouse, g, currentTransform);
			}
			return hitLabel;
		} else {
			return null;
		} 
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
}
