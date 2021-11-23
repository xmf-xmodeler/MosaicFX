package tool.clients.fmmlxdiagrams.graphics;

import java.util.Vector;

import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.anim.dom.SVGOMTSpanElement;
import org.apache.batik.anim.dom.SVGOMTextElement;
import org.apache.batik.dom.GenericText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeText extends NodeBaseElement{

	final SVGOMTextElement textNode;
	final SVGOMSVGElement rootNode;
	private double x,y;
	
	public NodeText(SVGOMTextElement textNode, SVGOMSVGElement rootNode) {
		super(SVGReader.readTransform(textNode), rootNode.getComputedStyle(textNode, null), null, () -> {});
		this.textNode = textNode;
		this.rootNode = rootNode;
		this.action= ()->{};
		Node transformNode = textNode.getAttributes().getNamedItem("transform");
		this.myTransform = transformNode==null?new Affine():SVGReader.readTransform(transformNode.getNodeValue());
		try{
			this.x = Double.parseDouble(textNode.getAttributes().getNamedItem("x").getNodeValue());
			this.y = Double.parseDouble(textNode.getAttributes().getNamedItem("y").getNodeValue());
		} catch(Exception e) {
			x = 0; y = 0;
		}
		setID(textNode);
	}
	
	@Override
	public void paintOn(View diagramView, boolean objectIsSelected) {
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();
		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		paintOnLocal(g, textNode, this.x, this.y);
	}

	private void paintOnLocal(GraphicsContext g, SVGOMElement parentNode, double x, double y) {
		for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {
			Node n = parentNode.getChildNodes().item(i);
			if(n instanceof GenericText) {
				GenericText gt = (GenericText) n;
				
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
	
	@Override
	protected NodeElement createInstance(FmmlxObject object, Vector<Modification> modifications) {
		return new NodeText(textNode, rootNode);
	}
}
