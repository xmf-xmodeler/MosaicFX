package tool.clients.fmmlxdiagrams.graphics;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeRectangle extends NodeBaseElement	 {
	
	double x,y,width,height,rx=0,ry=0; 
	//The x and y coordinates refer to the left and top edges of the rectangle. The width and height properties define the overall width and height of the rectangle.
	//For rounded rectangles, the computed values of the rx and ry properties define the x- and y-axis radii of elliptical arcs used to round off the corners of the rectangle.
	
	public static NodeRectangle rectangle(Node n) {
		NodeRectangle nR = new NodeRectangle();
		nR.x = Double.parseDouble(n.getAttributes().getNamedItem("x").getNodeValue());
		nR.y = Double.parseDouble(n.getAttributes().getNamedItem("y").getNodeValue());
		if (n.getAttributes().getNamedItem("rx")!=null) {
			nR.rx = Double.parseDouble(n.getAttributes().getNamedItem("rx").getNodeValue());
		} 
		if (n.getAttributes().getNamedItem("rx")!=null) {
			nR.ry = Double.parseDouble(n.getAttributes().getNamedItem("ry").getNodeValue());
		}
		nR.width = Double.parseDouble(n.getAttributes().getNamedItem("width").getNodeValue());
		nR.height = Double.parseDouble(n.getAttributes().getNamedItem("height").getNodeValue());
		Node bgColorNode = n.getAttributes().getNamedItem("fill");
		if (bgColorNode != null) {
			nR.bgColor = Color.web(bgColorNode.getNodeValue());
		} else {
			nR.bgColor = Color.BLACK;
		}
		nR.fgColor = Color.TRANSPARENT;
		nR.myTransform= SVGReader.readTransform(n);
		return nR;
	}
	
	@Override
	public void paintOn(DiagramViewPane diagramView, boolean objectIsSelected) {
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();
		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		g.beginPath();
		System.err.println(bgColor+" " + getPath() + "" + g.getTransform());
		g.appendSVGPath(getPath());
		g.setFill(bgColor);
		g.fill();
		g.setStroke(fgColor);
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
		stringPath=stringPath + " a " + rx + " " + ry + " 0 0 1 " + rx + " " + ry;
		stringPath=stringPath + " l " + 0 + " " + (height -2*ry);
		stringPath=stringPath + " a " + rx + " " + ry + " 0 0 1 " + " "+ (-rx) + " " + ry;
		stringPath=stringPath + " l " + " " + (-(width - 2*rx))+ " " +0;
		stringPath=stringPath + " a " + rx + " " + ry + " 0 0 1 " + " " + (-rx) + " " + (-ry);
		stringPath=stringPath + " l " + 0 + " " + (-(height -2*ry));
		stringPath=stringPath + " a " + rx + " " + ry + " 0 0 1 " + " " + rx + " " + (-ry);
		return stringPath;
	}

	@Override
	public void updateBounds() {
		updateBoundsFromPath(getPath());
	}

}
