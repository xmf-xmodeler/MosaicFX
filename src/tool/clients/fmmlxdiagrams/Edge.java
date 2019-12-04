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
	protected boolean SourceVisible;
	protected boolean TargetVisible;
	private Vector<Object> labelPositions;

	public Edge(int id, 
			FmmlxObject startNode, FmmlxObject endNode, 
			Vector<Point2D> intermediatePoints, 
			Vector<Object> labelPositions, 
			FmmlxDiagram diagram) {
		layoutingFinishedSuccesfully = false;
		this.labelPositions = labelPositions;
		this.id = id;
		this.diagram = diagram;
		this.startNode = startNode;
		this.endNode = endNode;
		if (intermediatePoints == null || intermediatePoints.size() < 1) {
		} else {
			this.intermediatePoints.addAll(intermediatePoints);
		}
		storeLatestValidPointConfiguration();
		PortRegion startPortRegion = determinePort(startNode, intermediatePoints.size()<1?null:intermediatePoints.firstElement(), PortRegion.EAST);
		PortRegion endPortRegion   = determinePort(endNode,   intermediatePoints.size()<1?null:intermediatePoints.lastElement(),  PortRegion.WEST);
		
//		System.err.println(startPortRegion + "-->" + endPortRegion);
		
		startNode.addEdgeStart(this, startPortRegion);
		endNode.addEdgeEnd(this, endPortRegion);
	}

	private PortRegion determinePort(FmmlxObject node, Point2D nextPoint, PortRegion defaultRegion) {
		if(nextPoint == null) {
			return defaultRegion;
		}
//		System.err.println(node.getX() + "-->" + nextPoint.getX() + "-->" +  node.getMaxRight());
		if(node.getX() < nextPoint.getX() && nextPoint.getX() < node.getMaxRight()) {
			// N or S
			if(node.getY() > nextPoint.getY()) return PortRegion.NORTH;
			return PortRegion.SOUTH;
		} else {
			// E or W
			if(node.getX() > nextPoint.getX()) return PortRegion.WEST;
			 return PortRegion.EAST;
		}
	}

	@Override
	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram) {
		if(!layoutingFinishedSuccesfully) {
			layoutLabels(); diagram.redraw();
		} else {
		    Vector<Point2D> points = getAllPoints();
//			for (EdgeLabel label : labels) label.paintOn(g, xOffset, yOffset, fmmlxDiagram);
		    g.setFill(Color.RED);
		    g.fillText(
		    		new DecimalFormat("0.00").format(Math.atan2(-endNode.getCenterY() + startNode.getCenterY(), endNode.getCenterX() - startNode.getCenterX())/Math.PI)+"\u03C0", 
		    		.5*(startNode.getCenterX() + endNode.getCenterX()), 
		    		.5*(startNode.getCenterY() + endNode.getCenterY())-12);
		    
		    // hover
		    if(firstHoverPointIndex != null) {
		    	g.setStroke(new Color(1., .8, .2, 1.));
		    	g.setLineWidth(5);
		    	g.strokeLine(
		    			points.get(firstHoverPointIndex).getX(), 
		    			points.get(firstHoverPointIndex).getY(), 
		    			points.get(firstHoverPointIndex+1).getX(), 
		    			points.get(firstHoverPointIndex+1).getY());
		    }

		    // normal
			g.setStroke(fmmlxDiagram.isSelected(this) ? Color.RED : getPrimaryColor());
			g.setLineWidth(isSelected() ? 3 : 1);
			g.setLineDashes(getLineDashes());
			
			for(int i = 0; i < points.size()-1; i++) {
				g.strokeLine(
						points.get(i).getX(), 
						points.get(i).getY(), 
						points.get(i+1).getX(),
						points.get(i+1).getY());}
			
//			// prepare line segments
//			double[] xPoints = new double[points.size()];
//			double[] yPoints = new double[points.size()];
//			for (int i = 0; i < points.size(); i++) {
//				xPoints[i] = points.get(i).getX();
//				yPoints[i] = points.get(i).getY();
//			}
//			
//			g.strokePolyline(xPoints, yPoints, xPoints.length);
						
			if(newSourcePortRegion != null) {
				double[] xPoints2 = new double[] {
					(points.get(0).getX() + points.get(1).getX())/2,
					lastMousePosition.getX(),
					newSourcePortRegion == PortRegion.WEST?startNode.getX():newSourcePortRegion == PortRegion.EAST?startNode.getMaxRight():startNode.getCenterX()
				};
				double[] yPoints2 = new double[] {
					(points.get(0).getY() + points.get(1).getY())/2,
					lastMousePosition.getY(),
					newSourcePortRegion == PortRegion.NORTH?startNode.getY():newSourcePortRegion == PortRegion.SOUTH?startNode.getMaxBottom():startNode.getCenterY()
				};

		    	g.setStroke(new Color(1., .8, .2, 1.));
				g.strokePolyline(xPoints2, yPoints2, xPoints2.length);
		    	g.setStroke(new Color(0., .8, .2, 1.));
				g.setLineDashes(3, 4);
				g.strokePolyline(xPoints2, yPoints2, xPoints2.length);
			}
			
			if(newTargetPortRegion != null) {
				double[] xPoints2 = new double[] {
					(points.get(points.size()-1).getX() + points.get(points.size()-2).getX())/2,
					lastMousePosition.getX(),
					newSourcePortRegion == PortRegion.WEST?endNode.getX():newTargetPortRegion == PortRegion.EAST?endNode.getMaxRight():endNode.getCenterX()
				};
				double[] yPoints2 = new double[] {
					(points.get(points.size()-1).getY() + points.get(points.size()-2).getY())/2,
					lastMousePosition.getY(),
					newSourcePortRegion == PortRegion.NORTH?endNode.getY():newTargetPortRegion == PortRegion.SOUTH?endNode.getMaxBottom():endNode.getCenterY()
				};

		    	g.setStroke(new Color(1., .8, .2, 1.));
				g.strokePolyline(xPoints2, yPoints2, xPoints2.length);
		    	g.setStroke(new Color(0., .8, .2, 1.));
				g.setLineDashes(3, 4);
				g.strokePolyline(xPoints2, yPoints2, xPoints2.length);
			}	
			/*if (pointToBeMoved != -1) {
				final double R = 1.5;
				g.fillOval(points.get(pointToBeMoved).getX() - R,
						points.get(pointToBeMoved).getY() - R,
						2 * R,
						2 * R);
			}*/
	
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

	protected abstract void layoutLabels();
	
	protected void align() {
		if(intermediatePoints.size() < 2) {
			if(startNode.getDirectionForEdge(this, true).isHorizontal())
				intermediatePoints.add(new Point2D((startNode.getCenterX() + endNode.getCenterX())/2, startNode.getCenterY())); 
			else
				intermediatePoints.add(new Point2D(startNode.getCenterX(), (startNode.getCenterY() + endNode.getCenterY())/2));
			
			if(endNode.getDirectionForEdge(this, false).isHorizontal())
				intermediatePoints.add(new Point2D((startNode.getCenterX() + endNode.getCenterX())/2, endNode.getCenterY())); 
			else
				intermediatePoints.add(new Point2D(endNode.getCenterX(), (startNode.getCenterY() + endNode.getCenterY())/2));
		};
		
		Point2D first = startNode.getPointForEdge(this, true);
		Point2D second = intermediatePoints.get(0);
		PortRegion sourceDirection = startNode.getDirectionForEdge(this, true);
		
		Point2D newSecond = sourceDirection.isHorizontal()?new Point2D(second.getX(), first.getY()):new Point2D(first.getX(), second.getY());
		intermediatePoints.setElementAt(newSecond, 0);
		
		Point2D penultimate = intermediatePoints.get(intermediatePoints.size()-1);
		Point2D ultimate = endNode.getPointForEdge(this, false);
		PortRegion targetDirection = endNode.getDirectionForEdge(this, false);
		
		Point2D newPenultimate = targetDirection.isHorizontal()?new Point2D(penultimate.getX(), ultimate.getY()):new Point2D(ultimate.getX(), penultimate.getY());
		intermediatePoints.setElementAt(newPenultimate, intermediatePoints.size()-1);

	}
	
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
		return null != isHit(new Point2D(x, y), 2.5);
	}

	public Integer isHit(Point2D p, Double tolerance) {
		if(p == null) return null;
	    Vector<Point2D> points = getAllPoints();
		for (int i = 0; i < points.size() - 1; i++) {
			if (distance(p, points.get(i), points.get(i + 1)) < 0.2/*(tolerance == null ? DEFAULT_TOLERANCE : tolerance)*/) {
				return i;
			}
		}
		return null;
	}
	
	private double distance(Point2D p, Point2D a, Point2D b) { // assume lines to be aligned
		
		double angleAP = Math.atan2(
				a.getY() - p.getY(), 
				p.getX() - a.getX());
		double anglePB = Math.atan2(
				p.getY() - b.getY(), 
				b.getX() - p.getX());
		double angleAB = (5 * Math.PI + angleAP - anglePB) % (2 * Math.PI) - Math.PI;
		return Math.abs(angleAB);
	}

	private double distance_OLD(Point2D testPoint, Point2D lineStart, Point2D lineEnd) { // some fancy math copied from the old diagram
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
		lastMousePosition = new Point2D(x, y);
//		System.err.println("move point " + pointToBeMoved + " to " + x + "," + y + (movementDirectionHorizontal?"H":"V"));
		if(pointToBeMoved != -1 && moveMode == MoveMode.normal) {
			if(movementDirectionHorizontal) {
				intermediatePoints.setElementAt(new Point2D(x, intermediatePoints.get(pointToBeMoved-1).getY()), pointToBeMoved-1);
				intermediatePoints.setElementAt(new Point2D(x, intermediatePoints.get(pointToBeMoved).getY()), pointToBeMoved);				
			} else {
				intermediatePoints.setElementAt(new Point2D(intermediatePoints.get(pointToBeMoved-1).getX(),y), pointToBeMoved-1);
				intermediatePoints.setElementAt(new Point2D(intermediatePoints.get(pointToBeMoved).getX(),y), pointToBeMoved);				
			}
		}
		else if (moveMode == MoveMode.moveSourcePortArea) {
			newSourcePortRegion = findBestRegion(startNode, x, y);
		} 
		else if (moveMode == MoveMode.moveTargetPortArea) {
			newTargetPortRegion = findBestRegion(endNode, x, y);
		}
	}

	private PortRegion findBestRegion(FmmlxObject node, double x, double y) {
		double angleMouse = Math.atan2(y - node.getCenterY(), x - node.getCenterX());
		double diffAngleNW = (4 * Math.PI + angleMouse - Math.atan2(node.getY() - node.getCenterY(), node.getX() - node.getCenterX())) % (2 * Math.PI);
		double diffAngleNE = (4 * Math.PI + angleMouse - Math.atan2(node.getY() - node.getCenterY(), node.getMaxRight() - node.getCenterX())) % (2 * Math.PI);
		double diffAngleSE = (4 * Math.PI + angleMouse - Math.atan2(node.getMaxBottom() - node.getCenterY(), node.getMaxRight() - node.getCenterX())) % (2 * Math.PI);
		double diffAngleSW = (4 * Math.PI + angleMouse - Math.atan2(node.getMaxBottom() - node.getCenterY(), node.getX() - node.getCenterX())) % (2 * Math.PI);
		
		if(diffAngleNW < diffAngleNE && diffAngleNW < diffAngleSE && diffAngleNW < diffAngleSW) return PortRegion.NORTH;
		if(diffAngleNE < diffAngleSE && diffAngleNE < diffAngleSW) return PortRegion.EAST;
		if(diffAngleSE < diffAngleSW) return PortRegion.SOUTH;
			
		return PortRegion.WEST;
	}

	public boolean isStartNode(FmmlxObject fmmlxObject) {
		return startNode == fmmlxObject;
	}

	public boolean isEndNode(FmmlxObject fmmlxObject) {
		return endNode == fmmlxObject;
	}
	
	private void storeLatestValidPointConfiguration() {
		latestValidPointConfiguration.clear();
		latestValidPointConfiguration.addAll(intermediatePoints);
	}

	private transient Vector<Point2D> latestValidPointConfiguration = new Vector<>();
	private transient int pointToBeMoved = -1;
	private transient boolean movementDirectionHorizontal;
	private Integer firstHoverPointIndex;
	private enum MoveMode {normal, moveSourcePortArea, moveTargetPortArea}
	private transient MoveMode moveMode;
	private transient PortRegion newSourcePortRegion;
	private transient PortRegion newTargetPortRegion;
	private transient Point2D lastMousePosition;

	public void setPointAtToBeMoved(Point2D mousePoint) {
	    Vector<Point2D> points = getAllPoints();
		// An edge has been dragged on at Point p.
		// if a point is already found
		if (pointToBeMoved != -1) return;
		
		Integer hitLine = isHit(mousePoint, 0.2);
		if(hitLine != null) { 
			if(hitLine > 0 && hitLine < points.size() - 2) {
			pointToBeMoved = hitLine;
			movementDirectionHorizontal = points.get(pointToBeMoved).getX() == points.get(pointToBeMoved+1).getX();
			}
			else if(hitLine == 0) {
				moveMode = MoveMode.moveSourcePortArea;
			}
			else if(hitLine == points.size() - 2) {
				moveMode = MoveMode.moveTargetPortArea;
			}
				
		}
		
		

/*		// If there is a Point at p, then that is to be moved
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
			//	System.err.println("distance=" + distance(mousePoint, points.get(i), points.get(i+1)));
				if (distance_OLD(mousePoint, points.get(i), points.get(i + 1)) < DEFAULT_TOLERANCE) {
					pointToBeMoved = i + 1; // i is the point before the node. We will insert a new one, which will be number i+1
				}
			}
			if (pointToBeMoved != -1) { // make sure we found one
				Point2D newPoint = new Point2D(mousePoint.getX(), mousePoint.getY());
				intermediatePoints.insertElementAt(newPoint, pointToBeMoved-1);
			}
		}*/
	}

	private Double distance(Point2D A, Point2D B) {
		return Math.sqrt(Math.pow(A.getX() - B.getX(), 2) + Math.pow(A.getY() - B.getY(), 2));
	}

	public void dropPoint() {
	    /*Vector<Point2D> points = getAllPoints();
		if (pointToBeMoved != -1) {
			// if point very close to other point, remove it.
			if (distance(points.get(pointToBeMoved), points.get(pointToBeMoved + 1)) < DEFAULT_TOLERANCE
					|| distance(points.get(pointToBeMoved), points.get(pointToBeMoved - 1)) < DEFAULT_TOLERANCE) {
				points.remove(pointToBeMoved);
			}
		}*/
		// in any case no point to be moved anymore
		
		if(newSourcePortRegion != null && newSourcePortRegion != startNode.getDirectionForEdge(this, true)) {
			if(newSourcePortRegion.isOpposite(startNode.getDirectionForEdge(this, true))) {
				startNode.setDirectionForEdge(this, true, newSourcePortRegion);
			} else { // requires a new point
				if(newSourcePortRegion == PortRegion.SOUTH) {
					intermediatePoints.insertElementAt(new Point2D(
							0 /* does not matter */, 
							startNode.getMaxBottom() + 30), 0);
					intermediatePoints.setElementAt(new Point2D(
							intermediatePoints.get(1).getX(), 
							startNode.getMaxBottom() + 30), 1);
				} else 
					if(newSourcePortRegion == PortRegion.NORTH) {
						intermediatePoints.insertElementAt(new Point2D(
								0 /* does not matter */, 
								startNode.getY() - 30), 0);
						intermediatePoints.setElementAt(new Point2D(
								intermediatePoints.get(1).getX(), 
								startNode.getY() - 30), 1);
				} else 
					if(newSourcePortRegion == PortRegion.WEST) {
						intermediatePoints.insertElementAt(new Point2D(
								startNode.getX() - 30, 
								0 /* does not matter */), 0);
						intermediatePoints.setElementAt(new Point2D(
								startNode.getX() - 30, 
								intermediatePoints.get(1).getY()), 1);
				} else 
					if(newSourcePortRegion == PortRegion.EAST) {
						intermediatePoints.insertElementAt(new Point2D(
								startNode.getMaxRight() + 30, 
								0 /* does not matter */), 0);
						intermediatePoints.setElementAt(new Point2D(
								startNode.getMaxRight() + 30, 
								intermediatePoints.get(1).getY()), 1);
				} 			
				startNode.setDirectionForEdge(this, true, newSourcePortRegion);
			}
			align();
		}

		if(newTargetPortRegion != null && newTargetPortRegion != endNode.getDirectionForEdge(this, false)) {
			if(newTargetPortRegion.isOpposite(endNode.getDirectionForEdge(this, false))) {
				endNode.setDirectionForEdge(this, false, newTargetPortRegion);
			} else { // requires a new point
				if(newTargetPortRegion == PortRegion.SOUTH) {
					intermediatePoints.add(new Point2D(
							0 /* does not matter */, 
							endNode.getMaxBottom() + 30));
					intermediatePoints.setElementAt(new Point2D(
							intermediatePoints.get(intermediatePoints.size()-2).getX(), 
							endNode.getMaxBottom() + 30), 1);
				} else 
					if(newTargetPortRegion == PortRegion.NORTH) {
						intermediatePoints.add(new Point2D(
								0 /* does not matter */, 
								endNode.getY() - 30));
						intermediatePoints.setElementAt(new Point2D(
								intermediatePoints.get(intermediatePoints.size()-2).getX(), 
								endNode.getY() - 30), intermediatePoints.size()-2);
				} else 
					if(newTargetPortRegion == PortRegion.WEST) {
						intermediatePoints.add(new Point2D(
								endNode.getX() - 30, 
								0 /* does not matter */));
						intermediatePoints.setElementAt(new Point2D(
								endNode.getX() - 30, 
								intermediatePoints.get(intermediatePoints.size()-2).getY()), intermediatePoints.size()-2);
				} else 
					if(newTargetPortRegion == PortRegion.EAST) {
						intermediatePoints.add(new Point2D(
								endNode.getMaxRight() + 30, 
								0 /* does not matter */));
						intermediatePoints.setElementAt(new Point2D(
								endNode.getMaxRight() + 30, 
								intermediatePoints.get(intermediatePoints.size()-2).getY()), intermediatePoints.size()-2);
				} 			
				endNode.setDirectionForEdge(this, false, newTargetPortRegion);
			}
			align();
		}
		
		pointToBeMoved = -1;
		newSourcePortRegion = null;
		newTargetPortRegion = null;
	    moveMode = MoveMode.normal;
		
		storeLatestValidPointConfiguration();
		
		diagram.getComm().sendCurrentPositions(diagram, this);
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
	public void highlightElementAt(Point2D p) {
		firstHoverPointIndex = isHit(p, .2);
	}

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
	
	@Override
	public void unHighlight() {	firstHoverPointIndex = null;}
}
