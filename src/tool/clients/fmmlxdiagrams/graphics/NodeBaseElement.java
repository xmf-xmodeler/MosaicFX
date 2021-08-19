package tool.clients.fmmlxdiagrams.graphics;

import javafx.geometry.Point2D;
import tool.clients.fmmlxdiagrams.FmmlxProperty;

public abstract class NodeBaseElement implements NodeElement {
	
	public interface Action{
		public void perform();
	}

	public NodeBaseElement(double x, double y, FmmlxProperty actionObject, Action action) {
		this.x = x;
		this.y = y;
		this.actionObject = actionObject;
		this.action = action;
	}
	
	protected double x;
	protected double y;
	protected FmmlxProperty actionObject;
	protected Action action;
	protected boolean selected = false;
	
	@Override public double getX() {return x;}
	@Override public double getY() {return y;}	
	public void setSelected() { selected = true;}
	public void setDeselected() { selected = false;}
	public FmmlxProperty getActionObject() { return actionObject;}
	
	@Override public NodeBaseElement getHitLabel(Point2D pointRelativeToParent) {
		if(isHit(pointRelativeToParent.getX(), pointRelativeToParent.getY()))
			return this; return null;
	}
	
	public void performDoubleClickAction() { action.perform();}
	

}
