package tool.clients.fmmlxdiagrams.graphics;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeEllipse extends NodeBaseElement {

	double rx, ry, cx, cy; // c=center , r=radius
	Bounds bounds = new BoundingBox(0, 0, 0, 0);

	public static NodeEllipse circle(Node n) {
		NodeEllipse nE = new NodeEllipse();
		nE.cx = Double.parseDouble(n.getAttributes().getNamedItem("cx").getNodeValue());
		nE.cy = Double.parseDouble(n.getAttributes().getNamedItem("cy").getNodeValue());
		nE.rx = Double.parseDouble(n.getAttributes().getNamedItem("r").getNodeValue());
		nE.ry = nE.rx;
		Node bgColorNode = n.getAttributes().getNamedItem("fill");
		if (bgColorNode != null) {
			nE.bgColor = Color.web(bgColorNode.getNodeValue());
		} else {
			nE.bgColor = Color.BLACK;
		}
//		Node fgColorNode = n.getAttributes().getNamedItem("style");
//		if(fgColorNode!=null) {
//			nE.fgColor = Color.web(fgColorNode.getNodeValue());
//		} else {
		nE.fgColor = Color.TRANSPARENT;
//		}
		nE.myTransform= SVGReader.readTransform(n);
		//nE.updateBounds();
		return nE;
	}

	@Override
	public void paintOn(DiagramViewPane diagramView, boolean objectIsSelected) {
		
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();

		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		g.beginPath();
		
		g.appendSVGPath(getPath());
		g.setFill(bgColor);
		g.fill();
		g.setStroke(fgColor);
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
		Affine a = getTotalTransform(new Affine());
		SVGPath p = new SVGPath(); 
		p.setContent(getPath());
		p.getTransforms().add(Transform.affine(
				a.getMxx(), a.getMyx(),
				a.getMxy(), a.getMyy(), 
				a.getTx(), a.getTy()));
		this.bounds = p.getBoundsInParent();
		System.err.println("Bounds updated (Ellipse): " + bounds);
	}

	@Override
	public Bounds getBounds() {
		return bounds;
	}

	public static NodeEllipse ellipse(Node n) {
		NodeEllipse nE = new NodeEllipse();
		nE.cx = Double.parseDouble(n.getAttributes().getNamedItem("cx").getNodeValue());
		nE.cy = Double.parseDouble(n.getAttributes().getNamedItem("cy").getNodeValue());
		nE.rx = Double.parseDouble(n.getAttributes().getNamedItem("rx").getNodeValue());
		nE.ry = Double.parseDouble(n.getAttributes().getNamedItem("ry").getNodeValue());
		Node bgColorNode = n.getAttributes().getNamedItem("fill");
		if (bgColorNode != null) {
			nE.bgColor = Color.web(bgColorNode.getNodeValue());
		} else {
			nE.bgColor = Color.BLACK;
		}
//		Node fgColorNode = n.getAttributes().getNamedItem("style");
//		if(fgColorNode!=null) {
//			nE.fgColor = Color.web(fgColorNode.getNodeValue());
//		} else {
		nE.fgColor = Color.TRANSPARENT;
//		}
		nE.myTransform= SVGReader.readTransform(n);
		//nE.updateBounds();
		return nE;
	}

}
