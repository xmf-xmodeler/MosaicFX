package tool.clients.fmmlxdiagrams.graphics;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import org.w3c.dom.Element;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeBox extends NodeGroup{

	public interface LineWidthGetter {
		double getWidth(boolean selected);
	}

	double width;
	double height;
	Color bgColor;
	Color fgColor;
	LineWidthGetter lineWidth;
	private PropertyType propertyType;

	public NodeBox(double x, double y, double width, double height, Color bgColor, Color fgColor, LineWidthGetter lineWidth, PropertyType propertyType) {
		super(new Affine(1,0,x,0,1,y));
		this.width = width;
		this.height = height;
		this.bgColor = bgColor;
		this.fgColor = fgColor;
		this.lineWidth = lineWidth;
		this.propertyType = propertyType;
	}

	@Override
	public void paintOn(View diagramView, boolean objectIsSelected) {
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();
		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		g.setFill(bgColor);
		g.fillRect(0,0, width, height);
		g.setStroke(fgColor);
		g.setLineWidth(lineWidth.getWidth(objectIsSelected));
		g.strokeRect(0,0, width, height);
		super.paintOn(diagramView, objectIsSelected);
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, FmmlxDiagram.DiagramViewPane diagramView) {
		boolean hit = false;
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();
		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		g.beginPath();
		g.moveTo(0, 0); g.lineTo(0, height); g.lineTo(width, height); g.lineTo(width, 0); g.lineTo(0, 0);
		hit = g.isPointInPath(mouseX, mouseY);
		g.closePath();
		return hit;
	}

	public PropertyType getElementType() {
		return propertyType;
	}

	@Override
	public void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element parentGroup) {
		Element group = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_GROUP);
		group.setAttribute(SvgConstant.ATTRIBUTE_TRANSFORM, "matrix(1,0,0,1,"+getMyTransform().getTx()+","+getMyTransform().getTy()+")");
		group.setAttribute("XModeler", "NodeBox");
		xmlHandler.addXmlElement(parentGroup, group);

		Element rect = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_RECT);
		rect.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, "0");//(x + xOffset)+"");
		rect.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, "0");// (y + yOffset)+"");
		rect.setAttribute(SvgConstant.ATTRIBUTE_HEIGHT, height+"");
		rect.setAttribute(SvgConstant.ATTRIBUTE_WIDTH, width+"");
		rect.setAttribute(SvgConstant.ATTRIBUTE_FILL, NodeBaseElement.toRGBHexString(bgColor));
		rect.setAttribute(SvgConstant.ATTRIBUTE_STROKE, NodeBaseElement.toRGBHexString(fgColor));
		rect.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, lineWidth.getWidth(false)+"");
		rect.setAttribute(SvgConstant.ATTRIBUTE_FILL_OPACITY, bgColor.getOpacity()<.5?"0":"1");
		xmlHandler.addXmlElement(group, rect);
		
		for(NodeElement nodeElement : nodeElements){
			nodeElement.paintToSvg(diagram, xmlHandler, group);
		}
	}
	
	@Override
	public void updateBounds() {
		
		Point2D min = new Point2D(0, 0);
		Point2D max = new Point2D(width, height);
		
		Affine a = getTotalTransform(new Affine());

		min = a.transform(min);
		max = a.transform(max);
		
		bounds = new BoundingBox(min.getX(), min.getY(), 
		    max.getX() - min.getX(), max.getY() - min.getY()); 
	}

}
