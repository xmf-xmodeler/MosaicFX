package tool.clients.fmmlxdiagrams.graphics;

import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.anim.dom.SVGOMTSpanElement;
import org.apache.batik.anim.dom.SVGOMTextElement;
import org.apache.batik.dom.GenericText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeText extends NodeBaseElement{
	
//	Vector<TSpan> spans = new Vector<TSpan>();
	final SVGOMTextElement textRootNode;
	private SVGOMSVGElement root;
	private double x,y;
	
	public NodeText(SVGOMTextElement n, SVGOMSVGElement root) {
		super(SVGReader.readTransform(n), root.getComputedStyle(n, null), null, () -> {});
		this.root = root;
		this.textRootNode = n;
		this.action= ()->{};
		Node transformNode = n.getAttributes().getNamedItem("transform");
		this.myTransform = transformNode==null?new Affine():TransformReader.getTransform(transformNode.getNodeValue());
		try{
			this.x = Double.parseDouble(n.getAttributes().getNamedItem("x").getNodeValue());
			this.y = Double.parseDouble(n.getAttributes().getNamedItem("y").getNodeValue());
		} catch(Exception e) {
			x = 0; y = 0;
		}
		setID(n);
		readStyleAndColor(n);
//		if(bgColor == null) bgColor = Color.BLACK;
//		if(fgColor == null) fgColor = Color.TRANSPARENT;
		
//		for (int i = 0; i < n.getChildNodes().getLength(); i++) {
//
//			Node o = n.getChildNodes().item(i);
//
//			if ("tspan".equals(o.getNodeName())) {
//				TSpan span = new TSpan(o);
//				spans.add(span);
//			}
//		}
	}
	
	@Override
	public void paintOn(View diagramView, boolean objectIsSelected) {
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();

		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		
//		g.setFill(bgColor);
//		g.setStroke(fgColor.deriveColor(0., 1., 1., 0.5));
		
		paintOnLocal(g, textRootNode,this.x,this.y);
		
		
		
//		for(TSpan span : spans) {
//			g.fillText(  span.getText(), span.getX(), span.getY());
//			g.strokeText(span.getText(), span.getX(), span.getY());
//		}				
	}

	private void paintOnLocal(GraphicsContext g, SVGOMElement parentNode, double x, double y) {
		for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {
			Node n = parentNode.getChildNodes().item(i);
			if(n instanceof GenericText) {
				GenericText gt = (GenericText) n;
				
				CSSStyleDeclaration styleDeclaration = root.getComputedStyle(parentNode, null);
				
				String fillColor = styleDeclaration.getPropertyValue("fill");
				g.setFill(Color.web(fillColor));

				String strokeColor = styleDeclaration.getPropertyValue("stroke");
				if("none".equals(strokeColor)) {
					g.setStroke(Color.TRANSPARENT);
				} else {
					g.setStroke(Color.web(strokeColor));
				}
				String strokeWidth = styleDeclaration.getPropertyValue("stroke-width");
				g.setLineWidth(SVGReader.parseLength(strokeWidth, null));
				
				
				g.fillText(  gt.getTextContent(), x, y);
				g.strokeText(gt.getTextContent(), x, y);
			} else if (n instanceof SVGOMTSpanElement) {
				SVGOMTSpanElement span = (SVGOMTSpanElement) n;
				double X = 0; double Y = 0;
				try{
					X = Double.parseDouble(n.getAttributes().getNamedItem("x").getNodeValue()); 
					Y = Double.parseDouble(n.getAttributes().getNamedItem("y").getNodeValue());
				} catch (Exception e) {}
				X+=x; Y+=y;
				paintOnLocal(g, span, X, Y);
			}
			
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
	
	@Override
	public String toString() {
		return "Text"+ (id==null?"":("("+id+")"));
	}
//
//	private class TSpan{
//		
//		private Node n;
//		
//		public TSpan(Node n) {
//		 this.n=n;
//		}
//		
//		private String getText(){
//			Node o = n.getChildNodes().item(0);
//			return o.getNodeValue();
//		}
//		
//		private Double getX(){
//			Node o = n.getAttributes().getNamedItem("x");	
//			return Double.parseDouble(o.getNodeValue());
//		}
//		
//		private Double getY(){
//			Node o = n.getAttributes().getNamedItem("y");	
//			return Double.parseDouble(o.getNodeValue());
//		}
//		
//		private Style getStyle(){
//			Node styleNode = n.getAttributes().getNamedItem("style");
//			Style style = styleNode==null?new Style(""):new Style(styleNode.getNodeValue());
//			return style;
//		}		
//	}
}
