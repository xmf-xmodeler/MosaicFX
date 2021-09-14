package tool.clients.fmmlxdiagrams.graphics;

import org.w3c.dom.Element;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.xmlManipulator.XmlHandler;

public class NodePath extends NodeBaseElement{
	
	String textPath;
	Color bgColor;
	Color fgColor;
	
	public NodePath(Affine myTransform, String textPath, Color bgColor, Color fgColor, FmmlxProperty actionObject, Action action) {
		super(myTransform, actionObject, action);
		this.bgColor = bgColor;
		this.fgColor = fgColor;
		this.textPath = textPath;
	}

	@Override
	public void paintOn(FmmlxDiagram.DiagramViewPane diagramView, boolean objectIsSelected) {
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();
		
		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		g.beginPath();
		g.appendSVGPath(textPath);
		g.setFill(bgColor);
		g.fill();
		g.setStroke(fgColor);
		g.stroke();
		g.closePath();
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, FmmlxDiagram.DiagramViewPane diagramView) {
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();
		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		g.beginPath();
		g.appendSVGPath(textPath);
		boolean result = g.isPointInPath(mouseX, mouseY);
		g.closePath();
		return result;
	}

	@Override
	public void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element parentGroup) {
		Element group = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_GROUP);
		group.setAttribute(SvgConstant.ATTRIBUTE_TRANSFORM, getMatrix4svg());
		group.setAttribute("XModeler", "NodePath");
		Element path = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
		path.setAttribute(SvgConstant.ATTRIBUTE_D, textPath);
		path.setAttribute(SvgConstant.ATTRIBUTE_FILL, NodeBaseElement.toRGBHexString(bgColor));
//		path.setAttribute(SvgConstant.ATTRIBUTE_STROKE, NodeBaseElement.toRGBHexString(fgColor));
//		path.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, lineWidth.getWidth(false)+"");
//		path.setAttribute(SvgConstant.ATTRIBUTE_FILL_OPACITY, bgColor.getOpacity()<.5?"0":"1");
		xmlHandler.addXmlElement(group, path);
		xmlHandler.addXmlElement(parentGroup, group);

	}

}
