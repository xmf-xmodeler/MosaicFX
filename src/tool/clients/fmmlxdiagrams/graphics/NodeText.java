package tool.clients.fmmlxdiagrams.graphics;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
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
		
		Node styleNode = n.getAttributes().getNamedItem("style");
		this.style = styleNode==null?new Style(""):new Style(styleNode.getNodeValue());
		
		Node bgColorNode = n.getAttributes().getNamedItem("fill");
		if(bgColorNode!=null) {
			this.bgColor = Color.web(bgColorNode.getNodeValue());
		} else {
			this.bgColor = style.getFill();
		}
		if(bgColor==null) {
			this.bgColor = Color.TRANSPARENT;
		}
		this.fgColor = Color.TRANSPARENT;
		
		for (int i = 0; i < n.getChildNodes().getLength(); i++) {

			Node o = n.getChildNodes().item(i);

			if ("tspan".equals(o.getNodeName())) {
				TSpan span = new TSpan(o);
				span.getText();
				span.getX();
				span.getY();
				span.getStyle();
				spans.add(span);
			}
		}
		
		
	}
	
	@Override
	public void paintOn(DiagramViewPane diagramView, boolean objectIsSelected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, DiagramViewPane diagramView) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element parentGroup) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Bounds getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateBounds() {
		// TODO Auto-generated method stub
		
	}

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
