package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.menus.DefaultContextMenu;

import java.util.Vector;

public class Edge implements CanvasElement, Selectable {

	final public int id;
	protected Vector<Point2D> points = new Vector<>();
	protected FmmlxObject startNode;
	protected FmmlxObject endNode;
	protected FmmlxDiagram diagram;
	protected Vector<EdgeLabel> labels = new Vector<>();
	protected final Double DEFAULT_TOLERANCE = 6.;

	public Edge(int id, FmmlxObject startNode, FmmlxObject endNode, Vector<Point2D> points, FmmlxDiagram diagram) {
		this.id = id;
		this.diagram = diagram;
		this.startNode = startNode;
		this.endNode = endNode;
		if (points == null || points.size() < 1) {
			this.points.add(new Point2D(startNode.getX() + startNode.getWidth() / 2, startNode.getY() + startNode.getHeight() / 2));
			this.points.add(new Point2D(endNode.getX() + endNode.getWidth() / 2, endNode.getY() + endNode.getHeight() / 2));
		} else {
			this.points.addAll(points);
		}
		storeLatestValidPointConfiguration();
	}

	@Override
	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram) {
		for (EdgeLabel label : labels) label.paintOn(g, xOffset, yOffset, fmmlxDiagram);
		g.setStroke(fmmlxDiagram.isSelected(this) ? Color.RED : getPrimaryColor());
		g.setLineWidth(isSelected() ? 3 : 1);
		g.setLineDashes(getLineDashes());
		double[] xPoints = new double[points.size()];//+2];
		double[] yPoints = new double[points.size()];//+2];
//		xPoints[0] = startNode.getX() + startNode.width / 2;
//		yPoints[0] = startNode.getY() + startNode.height / 2;
		for (int i = 0; i < points.size(); i++) {
			xPoints[i] = points.get(i).getX();
			yPoints[i] = points.get(i).getY();
		}
//		xPoints[points.size()+1] = endNode.getX() + endNode.width / 2;
//		yPoints[points.size()+1] = endNode.getY() + endNode.height / 2;

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

	protected Color getPrimaryColor() {
		return Color.BLACK;
	}

	protected Double getLineDashes() {
		return (double) 0;
	}

	private boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean isHit(double x, double y) {
		return isHit(new Point2D(x, y), 5.);
	}

	public boolean isHit(Point2D p, Double tolerance) {
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

	public Point2D getAnchorPosition(EdgeLabel.Anchor anchor) {
		return null;
	}

	@Override
	public ContextMenu getContextMenu(DiagramActions actions) {
		System.err.println("getContextMenu " + id);
		return new DefaultContextMenu(actions); //temporary
	}

	@Override
	public void moveTo(double x, double y, FmmlxDiagram diagram) {
		if (pointToBeMoved != -1) {
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
	
	public void movePoints(double newStartX, double newStartY, double newEndX, double newEndY) {
		Point2D oldStartPoint = latestValidPointConfiguration.firstElement();
		double oldStartX = oldStartPoint.getX();
		double oldStartY = oldStartPoint.getY();
		Point2D oldEndPoint = latestValidPointConfiguration.lastElement();
		double oldEndX = oldEndPoint.getX();
		double oldEndY = oldEndPoint.getY();
//		double oldDist = Math.sqrt((oldEndX-oldStartX)*(oldEndX-oldStartX)+(oldEndY-oldStartY)*(oldEndY-oldStartY));
//		double oldDist = polarPoints.lastElement().radius;
//		double newDist = Math.sqrt((newEndX-newStartX)*(newEndX-newStartX)+(newEndY-newStartY)*(newEndY-newStartY));
//		
		Vector<Point2D> newPoints = new Vector<>();
//		
		newPoints.add(new Point2D(newStartX, newStartY));
		for(int i = 1; i < latestValidPointConfiguration.size()-1; i++) {
			Point2D oldPoint = latestValidPointConfiguration.get(i);
			double newX = (oldPoint.getX() - oldStartX) / (oldEndX - oldStartX) * (newEndX - newStartX) + newStartX;
			double newY = (oldPoint.getY() - oldStartY) / (oldEndY - oldStartY) * (newEndY - newStartY) + newStartY;
//			Polar p = polarPoints.get(i);
//			double[] scaledRelativePosition = p.getScaledXY(newDist / oldDist);
//			newPoints.add(new Point2D(newStartX + scaledRelativePosition[0], newStartY + scaledRelativePosition[1]));
			if(Double.isFinite(newX) && Double.isFinite(newY))
			newPoints.add(new Point2D(newX, newY));
		};
		newPoints.add(new Point2D(newEndX, newEndY));
//		
		points.clear();
		points.addAll(newPoints);
	}

	public void moveStartPoint(double newStartX, double newStartY) {
		movePoints(newStartX, newStartY, points.lastElement().getX(), points.lastElement().getY());
//		Point2D startPoint = new Point2D(startNode.getX() + startNode.getWidth() / 2, startNode.getY() + startNode.getHeight() / 2);
//		points.setElementAt(startPoint, 0);
	}

	public void moveEndPoint(double newEndX, double newEndY) {
		movePoints(points.firstElement().getX(), points.firstElement().getY(), newEndX, newEndY);
//		Point2D endPoint = new Point2D(endNode.getX() + endNode.getWidth() / 2, endNode.getY() + endNode.getHeight() / 2);
//		points.setElementAt(endPoint, points.size() - 1);
	}
	
//	private static class Polar{
//		final double radius; final double angle;
//		private Polar (double radius, double angle) {this.radius = radius; this.angle = angle;}
//		private double[] getScaledXY(double scale) {return new double[] {radius * scale * Math.cos(angle), radius * scale * Math.sin(angle)};}
//	}
//    private static Polar getPolar(double x, double y) {return new Polar(Math.sqrt(x*x+y*y), Math.atan2(y, x));}
	
	private void storeLatestValidPointConfiguration() {
		latestValidPointConfiguration.clear();
		latestValidPointConfiguration.addAll(points);
//		Point2D startPoint = points.firstElement();
//		Point2D endPoint = points.lastElement();
//		double xDiff = startPoint.getX() - endPoint.getX();
//		double yDiff = startPoint.getY() - endPoint.getY();
//		double distance = Math.sqrt(xDiff*xDiff+yDiff*yDiff);
//		System.err.println("Distance: " + distance);
//		if(distance < 10) return; // too small -> too big rounding errors
//		polarPoints.clear();
//		for(Point2D p : points) {
//			polarPoints.add(getPolar(p.getX() - startPoint.getX(), p.getY() - startPoint.getY()));
//		}
	}
	private transient Vector<Point2D> latestValidPointConfiguration = new Vector<>();
	private transient int pointToBeMoved = -1;

	public void setPointAtToBeMoved(Point2D mousePoint) {
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
				points.insertElementAt(newPoint, pointToBeMoved);
			}
		}

	}

	private Double distance(Point2D A, Point2D B) {
		return Math.sqrt(Math.pow(A.getX() - B.getX(), 2) + Math.pow(A.getY() - B.getY(), 2));
	}

	public void dropPoint() {
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

	public Vector<Point2D> getPoints() {
		return new Vector<Point2D>(points);
	}

	protected Point2D getCentreAnchor() {
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
}
