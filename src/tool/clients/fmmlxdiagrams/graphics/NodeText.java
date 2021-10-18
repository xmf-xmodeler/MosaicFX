package tool.clients.fmmlxdiagrams.graphics;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeText extends NodeBaseElement{
	
	Vector<TSpan> spans = new Vector<TSpan>();
	
	public NodeText(Node n) {
		this.action= ()->{};
		Node transformNode = n.getAttributes().getNamedItem("transform");
		this.myTransform = transformNode==null?new Affine():TransformReader.getTransform(transformNode.getNodeValue());
		
		readStyleAndColor(n);
		
		for (int i = 0; i < n.getChildNodes().getLength(); i++) {

			Node o = n.getChildNodes().item(i);

			if ("tspan".equals(o.getNodeName())) {
				TSpan span = new TSpan(o);
				spans.add(span);
			}
		}
	}
	
	@Override
	public void paintOn(DiagramViewPane diagramView, boolean objectIsSelected) {
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();

		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		
		g.setFill(bgColor);
		g.setStroke(fgColor.deriveColor(0., 1., 1., 0.5));
		
		for(TSpan span : spans) {
			g.fillText(  span.getText(), span.getX(), span.getY());
			g.strokeText(span.getText(), span.getX(), span.getY());
		}				
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, DiagramViewPane diagramView) {
		// The text itself is never clickable
		return false;
	}

	@Override
	void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element parentGroup) {
		// TODO Auto-generated method stub
	}

	// Do not count for bounds
	@Override public Bounds getBounds() { return null; }

	// Just ignore it
	@Override public void updateBounds() {}

	private class TSpan{
		
		private Node n;
		
		public TSpan(Node n) {
		 this.n=n;
		}
		
		private String getText(){
			Node o = n.getChildNodes().item(0);
			return o.getNodeValue();
		}
		
		private Double getX(){
			Node o = n.getAttributes().getNamedItem("x");	
			return Double.parseDouble(o.getNodeValue());
		}
		
		private Double getY(){
			Node o = n.getAttributes().getNamedItem("y");	
			return Double.parseDouble(o.getNodeValue());
		}
		
		private Style getStyle(){
			Node styleNode = n.getAttributes().getNamedItem("style");
			Style style = styleNode==null?new Style(""):new Style(styleNode.getNodeValue());
			return style;
		}		
	}
}
