package tool.clients.fmmlxdiagrams.graphics;

import org.apache.batik.anim.dom.SVGOMLineElement;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.dom.SVGOMPolygonElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.xmlManipulator.XmlHandler;

public class NodePath extends NodeBaseElement{
	
	String textPath;
	
	@Deprecated
	public NodePath(Affine myTransform, String textPath, Color bgColor, Color fgColor, FmmlxProperty actionObject, Action action, CSSStyleDeclaration styleDeclaration) {
		super(myTransform, styleDeclaration, actionObject, action);
		this.bgColor = bgColor;
		this.fgColor = fgColor;
		this.textPath = textPath;
		updateBounds();
	}
	
	public NodePath(Affine myTransform, String textPath, FmmlxProperty actionObject, Action action, CSSStyleDeclaration styleDeclaration) {
		super(myTransform, styleDeclaration, actionObject, action);
		this.textPath = textPath;
		updateBounds();
	}

	public NodePath(SVGOMPathElement n, SVGOMSVGElement root) {
		super(n.getAttributes().getNamedItem("transform")==null?new Affine():TransformReader.getTransform(n.getAttributes().getNamedItem("transform").getNodeValue()), 
				root.getComputedStyle(n, null), null, ()->{});
		this.action= ()->{};
		this.textPath = n.getAttributes().getNamedItem("d").getNodeValue();
		setID(n);
		
//		Node transformNode = n.getAttributes().getNamedItem("transform");
//		this.myTransform = transformNode==null?new Affine():TransformReader.getTransform(transformNode.getNodeValue());
		updateBounds();
	}

	public static NodePath line(SVGOMLineElement n, SVGOMSVGElement root) {
		CSSStyleDeclaration styleDeclaration = root.getComputedStyle(n, null);
		NodePath newPath = new NodePath(new Affine(), 
				"M "  + n.getAttributes().getNamedItem("x1").getNodeValue() +
				","   + n.getAttributes().getNamedItem("y1").getNodeValue() + 
				" L " + n.getAttributes().getNamedItem("x2").getNodeValue() +
				","   + n.getAttributes().getNamedItem("y2").getNodeValue()
				, null, ()->{}, styleDeclaration);
		return newPath;
	}
	
	public static NodePath polygon(SVGOMPolygonElement n, SVGOMSVGElement root) {
		String completeString = null;
		String points = n.getAttributes().getNamedItem("points").getNodeValue();
		String[] allPoints = points.split("[\\s,]+");
		
		if(allPoints.length % 2 == 0) for(int i=0; i<allPoints.length/2; i++) {
			if(i == 0) {
				completeString = "M " + allPoints[0] + " "+ allPoints[1];
			} else {
				completeString += " L " + allPoints[i*2] + " " + allPoints[i*2+1];
			}
		}
		completeString += "z";
		
		CSSStyleDeclaration styleDeclaration = root.getComputedStyle(n, null);
		NodePath newPath = new NodePath(new Affine(), completeString, null, ()->{}, styleDeclaration);
		return newPath;
	}

	@Override
	public void paintOn(View diagramView, boolean objectIsSelected) {
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();

		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		g.beginPath();
		g.appendSVGPath(textPath);
		if (styleDeclaration==null) {
			g.setFill(bgColor);
			g.setStroke(fgColor);
		} else {
			String fillColor = styleDeclaration.getPropertyValue("fill");
			if("none".equals(fillColor)) {
				g.setFill(Color.TRANSPARENT);
			} else {
				g.setFill(Color.web(fillColor));
			}
			setStrokeStyle(g, styleDeclaration);
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
	
	@Override
	public String toString() {
		return "Path"+ (id==null?"":("("+id+")"));
	}
	
	
	
	

}
