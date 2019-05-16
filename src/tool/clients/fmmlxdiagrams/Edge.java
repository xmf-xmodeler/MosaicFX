package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.menus.DefaultContextMenu;

public class Edge implements CanvasElement, Selectable {

	private Vector<Point2D> points = new Vector<>();
	private FmmlxObject startNode;
	private FmmlxObject endNode;
	
	private Vector<EdgeLabel> labels = new Vector<>();
	private final Double DEFAULT_TOLERANCE = 3.;
	
	public Edge(FmmlxObject startNode, FmmlxObject endNode) {
		this.startNode = startNode;
		this.endNode = endNode;
//		points.add(new Point2D(startNode.x + startNode.width / 2, startNode.y + startNode.height / 2));
//		points.add(new Point2D(endNode.x + endNode.width / 2, endNode.y + endNode.height / 2));
	}
	
	@Override
	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram) {
		for(EdgeLabel label : labels) label.paintOn(g, xOffset, yOffset, fmmlxDiagram);
		g.setStroke(fmmlxDiagram.isSelected(this)?Color.RED:Color.BLACK);
//		g.setLineDashes(dashes);
		g.setLineWidth(isSelected()?3:1);
		double[] xPoints = new double[points.size()+2];
		double[] yPoints = new double[points.size()+2];
		xPoints[0] = startNode.getX() + startNode.width / 2;
		yPoints[0] = startNode.getY() + startNode.height / 2;
		for(int i = 1; i < points.size()+1; i++) {
			xPoints[i] = points.get(i).getX();
			yPoints[i] = points.get(i).getY();
		}
		xPoints[points.size()+1] = endNode.getX() + endNode.width / 2;
		yPoints[points.size()+1] = endNode.getY() + endNode.height / 2;
		
		g.strokePolyline(xPoints, yPoints, xPoints.length);
	}

	private boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public boolean isHit(double x, double y) {
		return isHit(new Point2D(x, y), 5.);
	}
	public boolean isHit(Point2D p, Double tolerance) {
		for(int i = 0; i < points.size() - 1; i++) {
			if(distance(p, points.get(i), points.get(i+1)) < (tolerance==null?DEFAULT_TOLERANCE :tolerance)) {
				return true;
			}
		}
		return false;
	}
	
	private double distance(Point2D testPoint, Point2D lineStart, Point2D lineEnd) { // some fancy math copied from the old diagram
	    double normalLength = Math.sqrt(
	    		(lineEnd.getX() - lineStart.getX()) * (lineEnd.getX() - lineStart.getX()) 
	    	  + (lineEnd.getY() - lineStart.getY()) * (lineEnd.getY() - lineStart.getY()));
	    
	    return Math.abs((testPoint.getX() - lineStart.getX()) * (lineEnd.getY() - lineStart.getY())
		    	      - (testPoint.getY() - lineStart.getY()) * (lineEnd.getX() - lineStart.getX())) / normalLength;
	}

	public Point2D getAnchorPosition(EdgeLabel.Anchor anchor) {
		return null;
	}

	@Override
	public ContextMenu getContextMenu(DiagramActions actions) {
		return new DefaultContextMenu(actions); //temporary
	}


	

}
