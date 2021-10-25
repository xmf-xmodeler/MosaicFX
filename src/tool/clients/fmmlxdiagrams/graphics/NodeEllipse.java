package tool.clients.fmmlxdiagrams.graphics;

import org.apache.batik.anim.dom.SVGOMCircleElement;
import org.apache.batik.anim.dom.SVGOMEllipseElement;
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

public class NodeEllipse extends NodeBaseElement {

	double rx, ry, cx, cy; // c=center , r=radius
	final String type;
	
	private NodeEllipse(Affine myTransform, CSSStyleDeclaration styleDeclaration, FmmlxProperty actionObject, Action action, String type) {
		super(myTransform, styleDeclaration, actionObject, action);
		this.type=type;
	}
	
	public static NodeEllipse circle(SVGOMCircleElement n, SVGOMSVGElement rootNode) {
		double rx, ry, cx, cy;
		cx = Double.parseDouble(n.getAttributes().getNamedItem("cx").getNodeValue());
		cy = Double.parseDouble(n.getAttributes().getNamedItem("cy").getNodeValue());
		rx = Double.parseDouble(n.getAttributes().getNamedItem("r").getNodeValue());
		ry = rx;

		NodeEllipse nE = new NodeEllipse(SVGReader.readTransform(n), rootNode.getComputedStyle(n, null), null, () -> {}, "Circle");
		nE.cx = cx; nE.cy = cy; nE.rx = rx; nE.ry = ry;
		nE.setID(n);
		return nE;
	}
	
	public static NodeEllipse ellipse(SVGOMEllipseElement n, SVGOMSVGElement rootNode) {
		double rx, ry, cx, cy;
		cx = Double.parseDouble(n.getAttributes().getNamedItem("cx").getNodeValue());
		cy = Double.parseDouble(n.getAttributes().getNamedItem("cy").getNodeValue());
		rx = Double.parseDouble(n.getAttributes().getNamedItem("rx").getNodeValue());
		ry = Double.parseDouble(n.getAttributes().getNamedItem("ry").getNodeValue());

		NodeEllipse nE = new NodeEllipse(SVGReader.readTransform(n), rootNode.getComputedStyle(n, null), null, () -> {}, "Ellipse");
		nE.cx = cx; nE.cy = cy; nE.rx = rx; nE.ry = ry;
		nE.setID(n);
		return nE;	
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
		
//		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();
//
//		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
//		g.setFill(bgColor);
//		g.fill();
//		g.setStroke(fgColor);
//		g.strokeOval(cx-rx, cy-ry, 2*rx, 2*ry);
//		
		
	}

	private String getPath() {
		String s = "M " + cx +" " + cy + " m -"+rx + " 0 ";
		s = s+" a "+ rx + " " + ry + " 0 1 0 " + rx + " -" + ry ;
		s = s+" a "+ rx + " " + ry + " 0 1 0 " + rx + " " + ry ;
		s = s+" a "+ rx + " " + ry + " 0 1 0 -" + rx + " " + ry ;
		s = s+" a "+ rx + " " + ry + " 0 1 0 -" + rx + " -" + ry ;
		return s;
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
		// TODO Auto-generated method stub

	}
	@Override
	public void setOwner(NodeElement owner) {
		super.setOwner(owner);
		updateBounds();
	}

	@Override public void updateBounds() {
		updateBoundsFromPath(getPath());
	}

	@Override
	public Bounds getBounds() {
		return bounds;
	}
	
	@Override
	public String toString() {
		return type + (id==null?"":("("+id+")"));
	}


}
