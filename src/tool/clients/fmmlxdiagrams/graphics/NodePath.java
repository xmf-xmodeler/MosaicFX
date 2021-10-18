package tool.clients.fmmlxdiagrams.graphics;

import java.util.Arrays;
import java.util.Vector;

import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;

import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.xmlManipulator.XmlHandler;

public class NodePath extends NodeBaseElement{
	
	String textPath;
	CSSStyleDeclaration styleDeclaration;
	
	public NodePath(Affine myTransform, String textPath, Color bgColor, Color fgColor, FmmlxProperty actionObject, Action action) {
		super(myTransform, actionObject, action);
		this.bgColor = bgColor;
		this.fgColor = fgColor;
		this.textPath = textPath;
		updateBounds();
	}

	public NodePath(SVGOMElement n, SVGOMSVGElement svgOMElement) {
		styleDeclaration = svgOMElement.getComputedStyle(n, null);
		this.action= ()->{};
		this.textPath = n.getAttributes().getNamedItem("d").getNodeValue();
		
		Node transformNode = n.getAttributes().getNamedItem("transform");
		this.myTransform = transformNode==null?new Affine():TransformReader.getTransform(transformNode.getNodeValue());
		
	}
	
	public static NodePath polygon(Node n) {
		String completeString = null;
		String points = n.getAttributes().getNamedItem("points").getNodeValue();
		String[] copiedPoints = points.split(" ");
		//System.err.println(Arrays.toString(copiedPoints));
		for(int i=0; i<copiedPoints.length; i++) {
			String[] commaPoints = copiedPoints[i].split(",");
			if(commaPoints.length==2) {
			if(completeString==null) {
				completeString = "M " + commaPoints[0] + " "+ commaPoints[1];
			} else {
				completeString += " L " + commaPoints[0] + " " + commaPoints[1];
			}
			}
		}
		completeString += "z";
		Node bgColorNode = n.getAttributes().getNamedItem("fill");
		Color bgColor, fgColor;
		if (bgColorNode != null) {
			bgColor = Color.web(bgColorNode.getNodeValue());
		} else {
			bgColor = Color.BLACK;
		}
		fgColor = Color.TRANSPARENT;
		NodePath newPath = new NodePath(new Affine(), completeString, bgColor, fgColor, null, ()->{});
		//System.err.println("Path: " + completeString);	
		return newPath;
	}

	@Override
	public void paintOn(FmmlxDiagram.DiagramViewPane diagramView, boolean objectIsSelected) {
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();

		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		g.beginPath();
		g.appendSVGPath(textPath);
		if (styleDeclaration==null) {
			g.setFill(bgColor);
			g.setStroke(fgColor);
		} else {
			String fillvalue = styleDeclaration.getPropertyValue("fill");
			System.err.println(fillvalue);
			g.setFill(Color.web(fillvalue));
		}
		
		g.fill();
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

	@Override
	public void setOwner(NodeElement owner) {
		super.setOwner(owner);
		updateBounds();
	}

	@Override 
	public void updateBounds() {
		updateBoundsFromPath(textPath);
	}

	@Override
	public Bounds getBounds() {
		return bounds;
	}
	
	
	
	

}
