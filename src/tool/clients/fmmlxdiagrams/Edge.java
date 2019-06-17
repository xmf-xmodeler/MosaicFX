package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.menus.DefaultContextMenu;

import java.util.Vector;

public class Edge implements CanvasElement, Selectable {

	private Vector<Point2D> points = new Vector<>();
	private FmmlxObject startNode;
	private FmmlxObject endNode;
	
	private Vector<EdgeLabel> labels = new Vector<>();
	private final Double DEFAULT_TOLERANCE = 3.;
	
	public Edge(FmmlxObject startNode, FmmlxObject endNode) {
		this.startNode = startNode;
		this.endNode = endNode;
		points.add(new Point2D(startNode.getX() + startNode.width / 2, startNode.getY() + startNode.height / 2));
		points.add(new Point2D(endNode.getX() + endNode.width / 2, endNode.getY() + endNode.height / 2));
	}
	
	@Override
	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram) {
		for(EdgeLabel label : labels) label.paintOn(g, xOffset, yOffset, fmmlxDiagram);
		g.setStroke(fmmlxDiagram.isSelected(this)?Color.RED:Color.BLACK);
		g.setLineWidth(isSelected()?3:1);
		double[] xPoints = new double[points.size()];//+2];
		double[] yPoints = new double[points.size()];//+2];
		xPoints[0] = startNode.getX() + startNode.width / 2;
		yPoints[0] = startNode.getY() + startNode.height / 2;
		for(int i = 0; i < points.size(); i++) {
			xPoints[i] = points.get(i).getX();
			yPoints[i] = points.get(i).getY();
		}
//		xPoints[points.size()+1] = endNode.getX() + endNode.width / 2;
//		yPoints[points.size()+1] = endNode.getY() + endNode.height / 2;
		
		g.strokePolyline(xPoints, yPoints, xPoints.length);
		
		if(pointToBeMoved != -1) {
			final double R = 1.5;
			g.fillOval(points.get(pointToBeMoved).getX()-R, 
					   points.get(pointToBeMoved).getY()-R, 
					   2 * R, 
					   2 * R);
		}
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
			System.err.println("distance=" + distance(p, points.get(i), points.get(i+1)));
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

	@Override
	public void moveTo(double x, double y, FmmlxDiagram diagram) {
		if(pointToBeMoved != -1) {
//			Point2D oldPoint = points.get(pointToBeMoved);
			points.setElementAt(new Point2D(x, y), pointToBeMoved);
		}
	}

	public boolean isStartNode(FmmlxObject fmmlxObject) {
		return startNode == fmmlxObject;
	}
	
	public boolean isEndNode(FmmlxObject fmmlxObject) {
		return endNode == fmmlxObject;
	}

	public void moveStartPoint() {
		Point2D startPoint = new Point2D(startNode.getX() + startNode.width / 2, startNode.getY() + startNode.height / 2);
		points.setElementAt(startPoint, 0);
	}
	
	public void moveEndPoint() {
		Point2D endPoint = new Point2D(endNode.getX() + endNode.width / 2, endNode.getY() + endNode.height / 2);
		points.setElementAt(endPoint, points.size()-1);
	}

	private transient int pointToBeMoved = -1;
	
	public void setPointAtToBeMoved(Point2D mousePoint) {
		// An edge has been dragged on at Point p.
		
		// if a point is already found
		if(pointToBeMoved != -1) return;
		
		// If there is a Point at p, then that is to be moved
		// otherwise one is created on the spot.
		// that point's index is stored temporarily while the mouse is dragged
		pointToBeMoved = -1;
		for(int i = 1; i < points.size() - 1; i++) { // can't drag first nor last
			Point2D edgePoint = points.get(i);
			if(distance(mousePoint, edgePoint) < DEFAULT_TOLERANCE) {
				pointToBeMoved = i;
			}
		}
		if(pointToBeMoved == -1) { // not pressed on an existing node
			for(int i = 0; i < points.size() - 1; i++) {
//				System.err.println("distance=" + distance(mousePoint, points.get(i), points.get(i+1)));
				if(distance(mousePoint, points.get(i), points.get(i+1)) < DEFAULT_TOLERANCE) {
					pointToBeMoved = i+1; // i is the point before the node. We will insert a new one, which will be number i+1
				}
			}
			if(pointToBeMoved != -1) { // make sure we found one
				Point2D newPoint = new Point2D(mousePoint.getX(), mousePoint.getY());
				points.insertElementAt(newPoint, pointToBeMoved);
			}
		}
		
	}

	private Double distance(Point2D A, Point2D B) {
		return Math.sqrt(Math.pow(A.getX() - B.getX(),2) + Math.pow(A.getY() - B.getY(),2));
	}

	public void dropPoint() {
		if(pointToBeMoved != -1) {
		// if point very close to other point, remove it.
			if(distance(points.get(pointToBeMoved), points.get(pointToBeMoved + 1)) < DEFAULT_TOLERANCE 
		    || distance(points.get(pointToBeMoved), points.get(pointToBeMoved - 1)) < DEFAULT_TOLERANCE) {
				points.remove(pointToBeMoved);
		}}
		// in any case no point to be moved anymore
		pointToBeMoved = -1;
	}

}
