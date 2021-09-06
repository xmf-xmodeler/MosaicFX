package tool.clients.fmmlxdiagrams.graphics;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxProperty;

public abstract class NodeBaseElement implements NodeElement {
	
	public interface Action{
		public void perform();
	}

	public NodeBaseElement(double x, double y, FmmlxProperty actionObject, Action action) {
		this.myTransform = new Affine(1,0,x,0,1,y);
		this.actionObject = actionObject;
		this.action = action;
	}
	
	protected Affine myTransform = new Affine(); // where to be painted if the zoom were 1 and the origin has not moved
	protected FmmlxProperty actionObject;
	protected Action action;
	protected boolean selected = false;
	
	@Override public double getX() {return myTransform.getTx();}
	@Override public double getY() {return myTransform.getTy();}	
	public void setSelected() { selected = true;}
	public void setDeselected() { selected = false;}
	public FmmlxProperty getActionObject() { return actionObject;}
	
	@Override public NodeBaseElement getHitLabel(Point2D mouse, GraphicsContext g, Affine currentTransform) {
//		currentTransform = new Affine(currentTransform); // copy
//		currentTransform.append(myTransform);
		if(isHit(mouse.getX(), mouse.getY(), g, currentTransform))
			return this; return null;
	}
	
	public void performDoubleClickAction() { action.perform();}
	

}
