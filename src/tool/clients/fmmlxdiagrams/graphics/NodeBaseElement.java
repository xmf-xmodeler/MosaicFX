package tool.clients.fmmlxdiagrams.graphics;

import java.util.Vector;

import org.w3c.dom.css.CSSStyleDeclaration;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxProperty;

public abstract class NodeBaseElement extends NodeElement {
	
	final CSSStyleDeclaration styleDeclaration;
	
	protected NodeBaseElement(Affine myTransform, CSSStyleDeclaration styleDeclaration, FmmlxProperty actionObject, Action action) {
		this.myTransform = myTransform;
		this.actionObject = actionObject;
		this.action = action;
		this.styleDeclaration = styleDeclaration;
	}	
	
	@Override public final NodeBaseElement getHitElement(Point2D mouse, GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane diagramView) {
		if(isHit(mouse.getX(), mouse.getY(), diagramView))
			return this; return null;
	}
	
	@Override public final Action getAction(Point2D mouse, GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane diagramView) {
		if(isHit(mouse.getX(), mouse.getY(), diagramView))
			return this.action;
		return null;
	}	
	
	public final Affine getTotalTransform(Affine canvasTransform) {
		Affine a = new Affine(owner == null?canvasTransform:owner.getTotalTransform(canvasTransform));
		a.append(myTransform);
		return a;
	}
	
	public void setOwner(NodeElement owner) { // final?
		this.owner = owner;
	}
	
	private static String format(double val) {
	    String in = Integer.toHexString((int) Math.round(val * 255));
	    return in.length() == 1 ? "0" + in : in;
	}

	public static String toRGBHexString(Color value) {
	    return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue())).toUpperCase();
	}

	protected final String getMatrix4svg() {
		return "matrix(" + getMyTransform().getMxx() + "," + getMyTransform().getMyx() + "," 
	                     + getMyTransform().getMxy() + "," + getMyTransform().getMyy() + ","
	                     + getMyTransform().getTx() + "," + getMyTransform().getTy() + ")";
	}
	
	protected final void setStrokeStyle(GraphicsContext g, CSSStyleDeclaration styleDeclaration) {
		String strokeColor = styleDeclaration.getPropertyValue("stroke");
		if("none".equals(strokeColor)) {
			g.setStroke(Color.TRANSPARENT);
		} else {
			g.setStroke(Color.web(strokeColor));
		}
		String strokeWidth = styleDeclaration.getPropertyValue("stroke-width");
		g.setLineWidth(SVGReader.parseLength(strokeWidth, null));
		String strokeLinecap = styleDeclaration.getPropertyValue("stroke-linecap");
		try{
			g.setLineCap(StrokeLineCap.valueOf(strokeLinecap.toUpperCase()));
		} catch(Exception e) {System.err.println("strokeLinecap: " + strokeLinecap);}
		String strokeLinejoin = styleDeclaration.getPropertyValue("stroke-linejoin");
		try{
			g.setLineJoin(StrokeLineJoin.valueOf(strokeLinejoin.toUpperCase()));
		} catch(Exception e) {System.err.println("strokeLinejoin: " + strokeLinejoin);}
	}
	
	@Override
	protected final Vector<NodeElement> getChildren() {
		return new Vector<>();
	}
	
}
