package tool.clients.fmmlxdiagrams.graphics;

import java.util.Vector;

import org.apache.batik.anim.dom.SVGOMCircleElement;
import org.apache.batik.anim.dom.SVGOMEllipseElement;
import org.apache.batik.anim.dom.SVGOMLineElement;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.dom.SVGOMPolygonElement;
import org.apache.batik.anim.dom.SVGOMRectElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;

import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.xmlManipulator.XmlHandler;

public class NodePath extends NodeBaseElement{

	@Deprecated Color bgColor;
	@Deprecated Color fgColor;
	Color overrideFillColor = null;
	String textPath;
	final String type;
	
	public NodePath(Affine myTransform, String textPath, FmmlxProperty actionObject, Action action, CSSStyleDeclaration styleDeclaration, String type) {
		super(myTransform, styleDeclaration, actionObject, action);
		this.textPath = textPath;
		this.type = type;
		updateBounds();
	}

	public NodePath(SVGOMPathElement n, SVGOMSVGElement root) {
		super(n.getAttributes().getNamedItem("transform")==null?new Affine():SVGReader.readTransform(n.getAttributes().getNamedItem("transform").getNodeValue()), 
				root.getComputedStyle(n, null), null, null);
		this.type = "Path";
		this.textPath = n.getAttributes().getNamedItem("d").getNodeValue();
		setID(n);		
		updateBounds();
	}

	public static NodePath line(SVGOMLineElement n, SVGOMSVGElement root) {
		CSSStyleDeclaration styleDeclaration = root.getComputedStyle(n, null);
		NodePath newPath = new NodePath(SVGReader.readTransform(n), 
				"M "  + n.getAttributes().getNamedItem("x1").getNodeValue() +
				","   + n.getAttributes().getNamedItem("y1").getNodeValue() + 
				" L " + n.getAttributes().getNamedItem("x2").getNodeValue() +
				","   + n.getAttributes().getNamedItem("y2").getNodeValue()
				, null, null, styleDeclaration, "Line");
		newPath.setID(n);
		return newPath;
	}
	
	public static NodePath polygon(SVGOMPolygonElement n, SVGOMSVGElement root) {
		String completeString = null;
		String points = n.getAttributes().getNamedItem("points").getNodeValue().trim();
		String[] allPoints = points.split("[\\s\\n\\r,]+");
		if(allPoints.length % 2 == 0) for(int i=0; i<allPoints.length/2; i++) {
			if(i == 0) {
				completeString = "M " + allPoints[0] + " "+ allPoints[1];
			} else {
				completeString += " L " + allPoints[i*2] + " " + allPoints[i*2+1];
			}
		}
		completeString += "z";
		
		CSSStyleDeclaration styleDeclaration = root.getComputedStyle(n, null);
		NodePath newPath = new NodePath(SVGReader.readTransform(n), completeString, null, null, styleDeclaration, "Polygon");
		newPath.setID(n);
		return newPath;
	}

	@Override
	public void paintOn(View diagramView, boolean objectIsSelected) {
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();

		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		g.beginPath();
		g.appendSVGPath(textPath);
		//if (styleDeclaration==null) {
		//	g.setFill(bgColor);
		//	g.setStroke(fgColor);
		//} else {
			setFill(g, styleDeclaration);
			setStrokeStyle(g, styleDeclaration);
	//	}
		
		g.fill();
		g.stroke();

		g.closePath();
	}



	private void setFill(GraphicsContext g, CSSStyleDeclaration styleDeclaration) {
		if(overrideFillColor == null) {
			String fillColor = styleDeclaration.getPropertyValue("fill");
			if("none".equals(fillColor)) {
				g.setFill(Color.TRANSPARENT);
			} else if(fillColor.startsWith("url")) {
				g.setFill(Color.MAGENTA);
			} else {
				g.setFill(Color.web(fillColor));
			}		
		} else {
			g.setFill(overrideFillColor);
		}
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
		Canvas c = new Canvas();
		GraphicsContext g = c.getGraphicsContext2D();
		setFill(g, styleDeclaration);
		setStrokeStyle(g, styleDeclaration);
		path.setAttribute(SvgConstant.ATTRIBUTE_D, textPath);
		path.setAttribute(SvgConstant.ATTRIBUTE_FILL, NodeBaseElement.toRGBHexString((Color)g.getFill()));
//		path.setAttribute(SvgConstant.ATTRIBUTE_STROKE, NodeBaseElement.toRGBHexString(fgColor));
//		path.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, lineWidth.getWidth(false)+"");
//		path.setAttribute(SvgConstant.ATTRIBUTE_FILL_OPACITY, bgColor.getOpacity()<.5?"0":"1");
		xmlHandler.addXmlElement(group, path);
		xmlHandler.addXmlElement(parentGroup, group);

	}

	@Override
	public void setOwner(NodeGroup owner) {
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
		return type + (id==null?"":("("+id+")"));
	}
	
	public static NodePath circle(SVGOMCircleElement n, SVGOMSVGElement rootNode) {
		double cx, cy, rx;
		cx = Double.parseDouble(n.getAttributes().getNamedItem("cx").getNodeValue());
		cy = Double.parseDouble(n.getAttributes().getNamedItem("cy").getNodeValue());
		rx = Double.parseDouble(n.getAttributes().getNamedItem("r").getNodeValue());
		
		String path = getEllipsePath(cx, cy, rx, rx);

		NodePath nE = new NodePath(SVGReader.readTransform(n), path, null, null, rootNode.getComputedStyle(n, null), "Circle");
		nE.setID(n);
		return nE;
	}
	
	public static NodePath ellipse(SVGOMEllipseElement n, SVGOMSVGElement rootNode) {
		double cx, cy, rx, ry;
		cx = Double.parseDouble(n.getAttributes().getNamedItem("cx").getNodeValue());
		cy = Double.parseDouble(n.getAttributes().getNamedItem("cy").getNodeValue());
		rx = Double.parseDouble(n.getAttributes().getNamedItem("rx").getNodeValue());
		ry = Double.parseDouble(n.getAttributes().getNamedItem("ry").getNodeValue());

		String path = getEllipsePath(cx, cy, rx, ry);
				
		NodePath nE = new NodePath(SVGReader.readTransform(n), path,  null, null, rootNode.getComputedStyle(n, null),"Ellipse");
		nE.setID(n);
		return nE;	
	}
	
	private static String getEllipsePath(double cx, double cy, double rx, double ry) {
		String s = "M " + cx +" " + cy + " m -"+rx + " 0 ";
		s = s+" a "+ rx + " " + ry + " 0 1 0 " + rx + " -" + ry ;
		s = s+" a "+ rx + " " + ry + " 0 1 0 " + rx + " " + ry ;
		s = s+" a "+ rx + " " + ry + " 0 1 0 -" + rx + " " + ry ;
		s = s+" a "+ rx + " " + ry + " 0 1 0 -" + rx + " -" + ry ;
		return s;
	}
	
	public static NodePath rectangle(SVGOMRectElement n, SVGOMSVGElement rootNode) {
		double x, y, rx = 0, ry = 0, width, height;
		x = Double.parseDouble(n.getAttributes().getNamedItem("x").getNodeValue());
		y = Double.parseDouble(n.getAttributes().getNamedItem("y").getNodeValue());
		if (n.getAttributes().getNamedItem("rx")!=null) {
			rx = Double.parseDouble(n.getAttributes().getNamedItem("rx").getNodeValue());
		} 
		if (n.getAttributes().getNamedItem("rx")!=null) {
			ry = Double.parseDouble(n.getAttributes().getNamedItem("ry").getNodeValue());
		}
		width = Double.parseDouble(n.getAttributes().getNamedItem("width").getNodeValue());
		height = Double.parseDouble(n.getAttributes().getNamedItem("height").getNodeValue());		
		
		String path="M " + x + " " + y;
		path=path + " m " + rx + " " + 0;
		path=path + " l " + (width - 2*rx)+ " " +0;
		if(rx>0 || ry >0) path=path + " a " + rx + " " + ry + " 0 0 1 " + rx + " " + ry;
		path=path + " l " + 0 + " " + (height -2*ry);
		if(rx>0 || ry >0) path=path + " a " + rx + " " + ry + " 0 0 1 " + " "+ (-rx) + " " + ry;
		path=path + " l " + " " + (-(width - 2*rx))+ " " +0;
		if(rx>0 || ry >0) path=path + " a " + rx + " " + ry + " 0 0 1 " + " " + (-rx) + " " + (-ry);
		path=path + " l " + 0 + " " + (-(height -2*ry));
		if(rx>0 || ry >0) path=path + " a " + rx + " " + ry + " 0 0 1 " + " " + rx + " " + (-ry);

		
		NodePath nR = new NodePath(SVGReader.readTransform(n), path,  null, null, rootNode.getComputedStyle(n, null), "Rectangle");
		nR.setID(n);
		return nR;
	}

	@Override
	protected NodePath createInstance(FmmlxObject object, Vector<Modification> modifications, Vector<ActionInfo> actions, FmmlxDiagram diagram) {
		NodePath n = new NodePath(new Affine(myTransform), textPath, actionObject, action, styleDeclaration, type);
		return n;
	}

	public void setColor(String colorString) {
		try{overrideFillColor = Color.web(colorString);}
		catch(Exception e) {e.printStackTrace();}
	}
	
	
	
}
