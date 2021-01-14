package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public abstract class Node implements CanvasElement{

	private transient double mouseMoveOffsetX;
	private transient double mouseMoveOffsetY;	
	
	protected boolean hidden;
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	protected int minWidth = 100;
	protected transient Point2D lastClick = null;
	protected FmmlxObjectPort ports;
	
	protected transient boolean requiresReLayout;
	protected Vector<NodeElement> nodeElements = new Vector<>();
	
	
	public void triggerLayout() {
		this.requiresReLayout = true;
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public double getCenterX() { return x + width / 2.; }
	public double getCenterY() { return y + height / 2.; }
	public double getRightX() { return x + width; }
	public double getBottomY() { return y + height; }

	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram diagram) {
		
		if(hidden) return;
		
		if(requiresReLayout) layout(diagram);

		boolean selected = diagram.isSelected(this);

		g.setFont(diagram.getFont());

		for (NodeElement e : nodeElements) {
			e.paintOn(g, x + xOffset, y + yOffset, diagram, selected);
		}
	}
	
	@Override
	public boolean isHit(double mouseX, double mouseY) {
		return
	        !hidden && 
			mouseX > x &&
			mouseY > y &&
			mouseX < x + width &&
			mouseY < y + height;
	}

	protected abstract void layout(FmmlxDiagram diagram) ;
	
	@Override
	public void moveTo(double x, double y, FmmlxDiagram diagram) {
		this.x = Math.max(x, 0.0);
		this.y = Math.max(y, 0.0);
	}
	
	@Override
	public void setOffsetAndStoreLastValidPosition(Point2D p) {
		mouseMoveOffsetX = p.getX() - x;
		mouseMoveOffsetY = p.getY() - y;
	}
	
	@Override public double getMouseMoveOffsetX() {return mouseMoveOffsetX;}
	@Override public double getMouseMoveOffsetY() {return mouseMoveOffsetY;}
	@Override public void highlightElementAt(Point2D p) {}
	@Override public void unHighlight() {}


	public boolean isHidden() {
		return hidden;
	}

}
