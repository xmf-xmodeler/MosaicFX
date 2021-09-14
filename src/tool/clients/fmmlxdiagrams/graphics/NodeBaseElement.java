package tool.clients.fmmlxdiagrams.graphics;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxProperty;

public abstract class NodeBaseElement implements NodeElement {
	
	public interface Action{
		public void perform();
	}

//	public NodeBaseElement(double x, double y, FmmlxProperty actionObject, Action action) {
//		this.myTransform = new Affine(1,0,x,0,1,y);
//		this.actionObject = actionObject;
//		this.action = action;
//	}
	
	public NodeBaseElement(Affine myTransform, FmmlxProperty actionObject, Action action) {
		this.myTransform = myTransform;
		this.actionObject = actionObject;
		this.action = action;
	}
	
	protected Affine myTransform = new Affine(); // where to be painted if the zoom were 1 and the origin has not moved
	protected FmmlxProperty actionObject;
	protected Action action;
	protected boolean selected = false;
	private NodeElement owner;
		
	public void setSelected() { selected = true;}
	public void setDeselected() { selected = false;}
	public FmmlxProperty getActionObject() { return actionObject;}
	
	@Override public NodeBaseElement getHitLabel(Point2D mouse, GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane diagramView) {
		if(isHit(mouse.getX(), mouse.getY(), diagramView))
			return this; return null;
	}
	
	public void performDoubleClickAction() { action.perform();}
	
	public final Affine getMyTransform() {	return myTransform; }
	
	public Affine getTotalTransform(Affine canvasTransform) {
		Affine a = new Affine(owner == null?canvasTransform:owner.getTotalTransform(canvasTransform));
		a.append(myTransform);
		return a;
	}

	public void setOwner(NodeElement owner) {
		this.owner = owner;
	}
	
	private static String format(double val) {
	    String in = Integer.toHexString((int) Math.round(val * 255));
	    return in.length() == 1 ? "0" + in : in;
	}

	public static String toRGBHexString(Color value) {
	    return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()))
	            .toUpperCase();
	}

	protected String getMatrix4svg() {
		return "matrix(" + getMyTransform().getMxx() + "," + getMyTransform().getMyx() + "," 
	                     + getMyTransform().getMxy() + "," + getMyTransform().getMyy() + ","
	                     + getMyTransform().getTx() + "," + getMyTransform().getTy() + ")";
	}
}
