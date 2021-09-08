package tool.clients.fmmlxdiagrams.graphics;

import org.w3c.dom.Element;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.xmlManipulator.XmlHandler;

public class NodePath extends NodeBaseElement{
	
	String textPath;
	Color bgColor;
	Color fgColor;
	private transient Affine lastTransform;
	private Affine selfTransform;
	
	public NodePath(Affine selfTransform, String textPath, Color bgColor, Color fgColor, FmmlxProperty actionObject, Action action) {
		super(selfTransform.getTx(), selfTransform.getTy(), actionObject, action);
		this.selfTransform = selfTransform;
		this.bgColor = bgColor;
		this.fgColor = fgColor;
		this.textPath = textPath;
	}

	@Override
	public void paintOn(GraphicsContext g, FmmlxDiagram.DiagramViewPane diagram,
			boolean objectIsSelected) {//this.g = g;
//		Affine oldTransform = g.getTransform();
//		Affine newtransform = currentTransform.clone();
//		newtransform.appendTranslation(transform.getTx(), transform.getTy());
//		newtransform.prepend(transform);
//		newtransform.prepend(selfTransform);
//		lastTransform = newtransform;
		g.setTransform(getTotalTransform(diagram.getCanvasTransform()));
		g.beginPath();
		g.appendSVGPath(textPath);
		g.setFill(bgColor);
		g.fill();
		g.setStroke(fgColor);
		g.stroke();
		g.closePath();
//		g.setTransform(oldTransform);
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, GraphicsContext g, FmmlxDiagram.DiagramViewPane diagram) {
		g.setTransform(getTotalTransform(diagram.getCanvasTransform()));
		g.beginPath();
		g.appendSVGPath(textPath);
		boolean result = g.isPointInPath(mouseX, mouseY);
		g.closePath();
		return result;
		/*if (g == null || lastTransform == null) return false;
		Affine transform = g.getTransform();
		Affine newtransform = transform.clone();
		newtransform.append(selfTransform);
		g.setTransform(newtransform);
		g.beginPath();
		g.appendSVGPath(textPath);
		boolean result = g.isPointInPath(mouseX, mouseY);
		g.closePath();
		g.setTransform(transform);
		return result;*/
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
