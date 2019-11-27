package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;
import java.util.Vector;

public abstract class Edge implements CanvasElement {

	final public int id;
	protected Vector<Point2D> intermediatePoints = new Vector<>();
	protected FmmlxObject startNode;
	protected FmmlxObject endNode;
	protected FmmlxDiagram diagram;
	protected final Double DEFAULT_TOLERANCE = 6.;
	protected boolean layoutingFinishedSuccesfully;
	private Vector<Object> labelPositions;

	public Edge(int id, FmmlxObject startNode, FmmlxObject endNode, Vector<Point2D> intermediatePoints, Vector<Object> labelPositions, FmmlxDiagram diagram) {
		layoutingFinishedSuccesfully = false;
		this.labelPositions = labelPositions;
		this.id = id;
		this.diagram = diagram;
		this.startNode = startNode;
		this.endNode = endNode;
		if (intermediatePoints == null || intermediatePoints.size() < 1) {
//			this.points.add(new Point2D(startNode.getX() + startNode.getWidth() / 2, startNode.getY() + startNode.getHeight() / 2));
//			this.points.add(new Point2D(endNode.getX() + endNode.getWidth() / 2, endNode.getY() + endNode.getHeight() / 2));
		} else {
			this.intermediatePoints.addAll(intermediatePoints);
		}
		storeLatestValidPointConfiguration();
		startNode.addEdgeStart(this, FmmlxObjectPort.EAST);
		endNode.addEdgeEnd(this, FmmlxObjectPort.WEST);
	}

	@Override
	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram) {
		if(!layoutingFinishedSuccesfully) {
			layout(); diagram.redraw();
		} else {
		    Vector<Point2D> points = getAllPoints();
//			for (EdgeLabel label : labels) label.paintOn(g, xOffset, yOffset, fmmlxDiagram);
		    g.setFill(Color.RED);
		    g.fillText(
		    		new DecimalFormat("0.00").format(Math.atan2(-endNode.getCenterY() + startNode.getCenterY(), endNode.getCenterX() - startNode.getCenterX())/Math.PI)+"\u03C0", 
		    		.5*(startNode.getCenterX() + endNode.getCenterX()), 
		    		.5*(startNode.getCenterY() + endNode.getCenterY())-12);
			g.setStroke(fmmlxDiagram.isSelected(this) ? Color.RED : getPrimaryColor());
			g.setLineWidth(isSelected() ? 3 : 1);
			g.setLineDashes(getLineDashes());
			double[] xPoints = new double[points.size()];//+2];
			double[] yPoints = new double[points.size()];//+2];
			for (int i = 0; i < points.size(); i++) {
				xPoints[i] = points.get(i).getX();
				yPoints[i] = points.get(i).getY();
			}
	
			g.strokePolyline(xPoints, yPoints, xPoints.length);
	
			if (pointToBeMoved != -1) {
				final double R = 1.5;
				g.fillOval(points.get(pointToBeMoved).getX() - R,
						points.get(pointToBeMoved).getY() - R,
						2 * R,
						2 * R);
			}
	
			// resetting the graphicsContext
			g.setLineDashes(0);
		}
	}

	protected Vector<Point2D> getAllPoints() {
		Vector<Point2D> allPoints = new Vector<Point2D>();
		allPoints.add(startNode.getPointForEdge(this, true));
		allPoints.addAll(intermediatePoints);
		allPoints.add(endNode.getPointForEdge(this, false));
		return allPoints;
	}

	protected abstract void layout();

	protected Color getPrimaryColor() {
		return Color.BLACK;
	}

	protected Double getLineDashes() {
		return (double) 0;
	}

	private boolean isSelected() {
		return false;
	}


	public boolean isHit(double x, double y) {
		return isHit(new Point2D(x, y), 5.);
	}

	public boolean isHit(Point2D p, Double tolerance) {
	    Vector<Point2D> points = getAllPoints();
		for (int i = 0; i < points.size() - 1; i++) {
//			System.err.println("distance=" + distance(p, points.get(i), points.get(i+1)));
			if (distance(p, points.get(i), points.get(i + 1)) < (tolerance == null ? DEFAULT_TOLERANCE : tolerance)) {
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

	@Override
	public abstract ContextMenu getContextMenu(DiagramActions actions);

	@Override
	public void moveTo(double x, double y, FmmlxDiagram diagram) {
		if(pointToBeMoved != -1) {
			intermediatePoints.setElementAt(new Point2D(x, y), pointToBeMoved-1);
		}
	}

	public boolean isStartNode(FmmlxObject fmmlxObject) {
		return startNode == fmmlxObject;
	}

	public boolean isEndNode(FmmlxObject fmmlxObject) {
		return endNode == fmmlxObject;
	}
	
	public void movePoints(double newStartX, double newStartY, double newEndX, double newEndY) {
//		Point2D oldStartPoint = latestValidPointConfiguration.firstElement();
//		double oldStartX = oldStartPoint.getX();
//		double oldStartY = oldStartPoint.getY();
//		Point2D oldEndPoint = latestValidPointConfiguration.lastElement();
//		double oldEndX = oldEndPoint.getX();
//		double oldEndY = oldEndPoint.getY();
//		
//		Vector<Point2D> newPoints = new Vector<>();
//	
//		newPoints.add(new Point2D(newStartX, newStartY));
//		for(int i = 1; i < latestValidPointConfiguration.size()-1; i++) {
//			Point2D oldPoint = latestValidPointConfiguration.get(i);
//			double newX = (oldPoint.getX() - oldStartX) / (oldEndX - oldStartX) * (newEndX - newStartX) + newStartX;
//			double newY = (oldPoint.getY() - oldStartY) / (oldEndY - oldStartY) * (newEndY - newStartY) + newStartY;
//			if(Double.isFinite(newX) && Double.isFinite(newY))
//			newPoints.add(new Point2D(newX, newY));
//		};
//		newPoints.add(new Point2D(newEndX, newEndY));
//		points.clear();
//		points.addAll(newPoints);
	}

//	public void moveStartPoint(double newStartX, double newStartY) {
//		movePoints(newStartX, newStartY, points.lastElement().getX(), points.lastElement().getY());
//	}
//
//	public void moveEndPoint(double newEndX, double newEndY) {
//		movePoints(points.firstElement().getX(), points.firstElement().getY(), newEndX, newEndY);
//	}
		
	private void storeLatestValidPointConfiguration() {
		latestValidPointConfiguration.clear();
		latestValidPointConfiguration.addAll(intermediatePoints);
	}

	private transient Vector<Point2D> latestValidPointConfiguration = new Vector<>();
	private transient int pointToBeMoved = -1;

	public void setPointAtToBeMoved(Point2D mousePoint) {
	    Vector<Point2D> points = getAllPoints();
		// An edge has been dragged on at Point p.

		// if a point is already found
		if (pointToBeMoved != -1) return;

		// If there is a Point at p, then that is to be moved
		// otherwise one is created on the spot.
		// that point's index is stored temporarily while the mouse is dragged
		pointToBeMoved = -1;
		for (int i = 1; i < points.size() - 1; i++) { // can't drag first nor last
			Point2D edgePoint = points.get(i);
			if (distance(mousePoint, edgePoint) < DEFAULT_TOLERANCE) {
				pointToBeMoved = i;
			}
		}
		if (pointToBeMoved == -1) { // not pressed on an existing node
			for (int i = 0; i < points.size() - 1; i++) {
//				System.err.println("distance=" + distance(mousePoint, points.get(i), points.get(i+1)));
				if (distance(mousePoint, points.get(i), points.get(i + 1)) < DEFAULT_TOLERANCE) {
					pointToBeMoved = i + 1; // i is the point before the node. We will insert a new one, which will be number i+1
				}
			}
			if (pointToBeMoved != -1) { // make sure we found one
				Point2D newPoint = new Point2D(mousePoint.getX(), mousePoint.getY());
				intermediatePoints.insertElementAt(newPoint, pointToBeMoved-1);
			}
		}

	}

	private Double distance(Point2D A, Point2D B) {
		return Math.sqrt(Math.pow(A.getX() - B.getX(), 2) + Math.pow(A.getY() - B.getY(), 2));
	}

	public void dropPoint() {
	    Vector<Point2D> points = getAllPoints();
		if (pointToBeMoved != -1) {
			// if point very close to other point, remove it.
			if (distance(points.get(pointToBeMoved), points.get(pointToBeMoved + 1)) < DEFAULT_TOLERANCE
					|| distance(points.get(pointToBeMoved), points.get(pointToBeMoved - 1)) < DEFAULT_TOLERANCE) {
				points.remove(pointToBeMoved);
			}
		}
		// in any case no point to be moved anymore
		pointToBeMoved = -1;
		
		storeLatestValidPointConfiguration();
	}

	public Vector<Point2D> getIntermediatePoints() {
		return new Vector<Point2D>(intermediatePoints);
	}

	protected Point2D getCentreAnchor() {
	    Vector<Point2D> points = getAllPoints();
		int n = points.size() / 2;
		return new Point2D(
				(points.get(n).getX() + points.get(n - 1).getX()) / 2,
				(points.get(n).getY() + points.get(n - 1).getY()) / 2);
	}

	public int getId() {
		return id;
	}

	@Override
	public void highlightElementAt(Point2D p) {}

	@Override
	public void setOffsetAndStoreLastValidPosition(Point2D p) {
		storeLatestValidPointConfiguration();		
	}
	
	@Override public double getMouseMoveOffsetX() {return 0;}
	@Override public double getMouseMoveOffsetY() {return 0;}
	
	@SuppressWarnings("unchecked")
	protected Point2D getLabelPosition(int localId) {
		for(Object labelPositionO : labelPositions) {
			Vector<Object> labelPosition = (Vector<Object>) labelPositionO;
			int theirLocalId = (Integer) labelPosition.get(1);
			if (theirLocalId == localId) {
				return new Point2D((Float) labelPosition.get(2), (Float) labelPosition.get(3));
			}
		}
		return null;
	}
}
