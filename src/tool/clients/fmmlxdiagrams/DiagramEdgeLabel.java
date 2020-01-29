package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import xos.Value;

public class DiagramEdgeLabel implements CanvasElement {
	
	final Edge owner;
	final int localID;
	private final Runnable action;
	private final String text;
	private final ContextMenu menu;
//	private double x; 
//	private double y;
	public double relativeX;
	public double relativeY;
	private double width;
	private double height;
	private Vector<FmmlxObject> anchors;
	private transient double mouseMoveOffsetX;
	private transient double mouseMoveOffsetY;
//	private transient double lastValidRelativeX;
//	private transient double lastValidRelativeY;

	private final Color bgColor;
	private final Color fontColor;
	private final static int MARGIN = 1;

	public DiagramEdgeLabel(Edge owner, int localID, Runnable action, ContextMenu menu, Vector<FmmlxObject> anchors, String value, 
			double relativeX, double relativeY, double w, double h,
			Color fontColor, Color bgColor) {
		this.owner = owner;
		this.localID = localID;
		this.action = action;
		this.menu = menu;
		this.text = value;
		this.relativeX = relativeX;
		this.relativeY = relativeY;
		this.width = w + 2 * MARGIN;
		this.height = h + 2 * MARGIN;
		this.anchors = anchors;
		this.fontColor = fontColor;
		this.bgColor = bgColor;
	}

	@Override
	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram) {
		int size=16;
		g.setFill(bgColor);
		g.fillRect(this.getReferenceX() + relativeX, this.getReferenceY() + relativeY, this.width, this.height);
		
		g.setFill(fontColor);
		g.fillText(this.text, this.getReferenceX() + relativeX + MARGIN, this.getReferenceY() + relativeY + height - MARGIN-2);
		if(anchors.size()>=2) {
		if(anchors.firstElement().getCenterX() <= anchors.elementAt(1).getCenterX()) {
			
		
		g.fillPolygon(new double[] { this.getReferenceX()+relativeX+width, this.getReferenceX()+relativeX+width + size, this.getReferenceX()+relativeX+width },
					  new double[] { this.getReferenceY()+relativeY, this.getReferenceY()+relativeY + size /2, this.getReferenceY()+relativeY+ size}, 3);
		}else {
			g.fillPolygon(new double[] { this.getReferenceX()+relativeX, this.getReferenceX()+relativeX - size+5, this.getReferenceX()+relativeX },
					  new double[] { this.getReferenceY()+relativeY, this.getReferenceY()+relativeY + size /2, this.getReferenceY()+relativeY+ size}, 3);
		}
			
		}
	}

	private double getReferenceX() {
		double x = 0;
		for(FmmlxObject o : anchors) {
			x += o.getCenterX();
		}
		x /= anchors.size();
		return x;
	}

	private double getReferenceY() {
		double y = 0;
		for(FmmlxObject o : anchors) {
			y += o.getCenterY();
		}
		y /= anchors.size();
		return y;
	}

	@Override
	public ContextMenu getContextMenu(DiagramActions actions, Point2D absolutePoint) {
		return menu;
	}

	@Override
	public void moveTo(double x, double y, FmmlxDiagram diagram) {
		this.relativeX = x;// - getReferenceX();
		this.relativeY = y;// - getReferenceY();
	}

	@Override
	public void highlightElementAt(Point2D p) {} // do nothing

	@Override
	public boolean isHit(double mouseX, double mouseY) {
		return
			mouseX > getReferenceX() + relativeX &&
			mouseY > getReferenceY() + relativeY &&
			mouseX < getReferenceX() + relativeX + width &&
			mouseY < getReferenceY() + relativeY + height;
	}
	
	@Override
	public void setOffsetAndStoreLastValidPosition(Point2D p) {
		mouseMoveOffsetX = p.getX()/* - getReferenceX() */ - relativeX;
		mouseMoveOffsetY = p.getY()/*  - getReferenceY() */ - relativeY;
//		lastValidRelativeX = - getReferenceX() + relativeX;
//		lastValidRelativeY = - getReferenceY() + relativeY;
	}

	@Override public double getMouseMoveOffsetX() {return mouseMoveOffsetX;}
	@Override public double getMouseMoveOffsetY() {return mouseMoveOffsetY;}

	public void performAction() {
		action.run();
	}

	public Value[] getInfo4XMF() {
		return new Value[]{
			new Value(new Value[] {new Value(owner.diagram.getID()), new Value(-1)}),
			new Value(owner.id),
			new Value(localID),
			new Value((float)relativeX),
			new Value((float)relativeY)};
	}

	@Override
	public void unHighlight() {}
}
