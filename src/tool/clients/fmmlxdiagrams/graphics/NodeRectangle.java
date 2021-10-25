package tool.clients.fmmlxdiagrams.graphics;

import org.apache.batik.anim.dom.SVGOMRectElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeRectangle extends NodeBaseElement	 {
	
	double x,y,width,height,rx=0,ry=0; 
	//The x and y coordinates refer to the left and top edges of the rectangle. The width and height properties define the overall width and height of the rectangle.
	//For rounded rectangles, the computed values of the rx and ry properties define the x- and y-axis radii of elliptical arcs used to round off the corners of the rectangle.
	
	private NodeRectangle(Affine myTransform, CSSStyleDeclaration styleDeclaration, FmmlxProperty actionObject, Action action) {
		super(myTransform, styleDeclaration, actionObject, action);
	}
	
	public static NodeRectangle rectangle(SVGOMRectElement n, SVGOMSVGElement rootNode) {
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
		Affine myTransform= SVGReader.readTransform(n);
		
		NodeRectangle nR = new NodeRectangle(myTransform, rootNode.getComputedStyle(n, null), null, ()->{});
		nR.x = x;
		nR.y = y;
		nR.rx = rx;
		nR.ry = ry;
		nR.width = width;
		nR.height = height;
		return nR;
	}
	
	@Override
	public void paintOn(View diagramView, boolean objectIsSelected) {
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();
		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		g.beginPath();
		g.appendSVGPath(getPath());
		
		String fillColor = styleDeclaration.getPropertyValue("fill");
		if("none".equals(fillColor)) {
			g.setFill(Color.TRANSPARENT);
		} else {
			g.setFill(Color.web(fillColor));
		}
		setStrokeStyle(g, styleDeclaration);
		
		g.fill();
		g.stroke();
		g.closePath();
		
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, DiagramViewPane diagramView) {
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();
		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		g.beginPath();
		g.appendSVGPath(getPath());
		boolean result = g.isPointInPath(mouseX, mouseY);
		g.closePath();
		return result;
	}

	@Override
	void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element parentGroup) {
		
	}

	@Override
	public Bounds getBounds() {
		return bounds;
	}
	
	public String getPath() {
		String stringPath="M " + x + " " + y;
		stringPath=stringPath + " m " + rx + " " + 0;
		stringPath=stringPath + " l " + (width - 2*rx)+ " " +0;
		if(rx>0 || ry >0) stringPath=stringPath + " a " + rx + " " + ry + " 0 0 1 " + rx + " " + ry;
		stringPath=stringPath + " l " + 0 + " " + (height -2*ry);
		if(rx>0 || ry >0) stringPath=stringPath + " a " + rx + " " + ry + " 0 0 1 " + " "+ (-rx) + " " + ry;
		stringPath=stringPath + " l " + " " + (-(width - 2*rx))+ " " +0;
		if(rx>0 || ry >0) stringPath=stringPath + " a " + rx + " " + ry + " 0 0 1 " + " " + (-rx) + " " + (-ry);
		stringPath=stringPath + " l " + 0 + " " + (-(height -2*ry));
		if(rx>0 || ry >0) stringPath=stringPath + " a " + rx + " " + ry + " 0 0 1 " + " " + rx + " " + (-ry);
		return stringPath;
	}

	@Override
	public void updateBounds() {
		updateBoundsFromPath(getPath());
	}

}
