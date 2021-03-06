package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.PortRegion;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public abstract class Edge implements CanvasElement {

	final public int id;
	protected Vector<Point2D> intermediatePoints = new Vector<>();
	protected FmmlxObject sourceNode;
	protected FmmlxObject targetNode;
	protected FmmlxDiagram diagram;
	protected final Double DEFAULT_TOLERANCE = 6.;
	protected boolean layoutingFinishedSuccesfully;
	
	public final Edge.End sourceEnd = new Edge.Source(this);
	public final Edge.End targetEnd = new Edge.Target(this);
	
	public static abstract class End {public final Edge edge; private End(Edge edge) {this.edge = edge;} public abstract FmmlxObject getNode();};
	public static class Source extends End{private Source(Edge edge) {super(edge);} public FmmlxObject getNode() {return edge.sourceNode;}};
	public static class Target extends End{private Target(Edge edge) {super(edge);} public FmmlxObject getNode() {return edge.targetNode;}};

	protected boolean visible;

	private Vector<Object> labelPositions;

	protected enum HeadStyle {
		NO_ARROW(0), ARROW(1), FULL_TRIANGLE(2), CIRCLE(3);

		int id;

		private HeadStyle(int id) {
			this.id = id;
		}

		// private int getID() {return id;}
		private static HeadStyle getHeadStyle(int id) {
			for (HeadStyle headStyle : HeadStyle.values())
				if (headStyle.id == id)
					return headStyle;
			throw new IllegalArgumentException("HeadStyle ID " + id + " not in use!");
		}

	}

	public Edge(int id, 
			FmmlxObject startNode, FmmlxObject endNode, 
			Vector<Point2D> intermediatePoints,
			PortRegion sourcePortRegion, PortRegion targetPortRegion,
			Vector<Object> labelPositions, FmmlxDiagram diagram) {
		layoutingFinishedSuccesfully = false;
		this.labelPositions = labelPositions;
		this.id = id;
		this.diagram = diagram;
		this.sourceNode = startNode;
		this.targetNode = endNode;
		if (intermediatePoints == null || intermediatePoints.size() < 1) {
//			this.points.add(new Point2D(startNode.getX() + startNode.getWidth() / 2, startNode.getY() + startNode.getHeight() / 2));
//			this.points.add(new Point2D(endNode.getX() + endNode.getWidth() / 2, endNode.getY() + endNode.getHeight() / 2));
		} else {
			this.intermediatePoints.addAll(intermediatePoints);
		}
		storeLatestValidPointConfiguration();
		
		if(sourcePortRegion==null) {
			if(startNode == endNode) sourcePortRegion = PortRegion.EAST; else
			sourcePortRegion = determinePort(startNode,
				this.intermediatePoints.size() < 1 ? null : this.intermediatePoints.firstElement(),
				startNode.getCenterX() < endNode.getCenterX() ? PortRegion.EAST : PortRegion.WEST);
		}
		if(targetPortRegion==null) {
			if(startNode == endNode) targetPortRegion = PortRegion.NORTH; else
			targetPortRegion = determinePort(endNode,
				this.intermediatePoints.size() < 1 ? null : this.intermediatePoints.lastElement(),
				startNode.getCenterX() < endNode.getCenterX() ? PortRegion.WEST : PortRegion.EAST);
		}
        startNode.addEdgeEnd(this.sourceEnd, sourcePortRegion);
        endNode.addEdgeEnd(this.targetEnd, targetPortRegion);

	}

	private PortRegion determinePort(FmmlxObject node, Point2D nextPoint, PortRegion defaultRegion) {
		if (nextPoint == null) {
			return defaultRegion;
		}
		if (node.getX() < nextPoint.getX() && nextPoint.getX() < node.getMaxRight()) {
			// N or S
			if (node.getY() > nextPoint.getY())
				return PortRegion.NORTH;
			return PortRegion.SOUTH;
		} else {
			// E or W
			if (node.getX() > nextPoint.getX())
				return PortRegion.WEST;
			return PortRegion.EAST;
		}
	}

	@Override
	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram) {
		if(!visible) return;
		if(!layoutingFinishedSuccesfully) {
			layoutLabels(); diagram.redraw();
		} else {
			Vector<Point2D> points = getAllPoints();
			g.setFill(new Color(.8, .8, .9, 1.));
			g.fillText(
					new DecimalFormat("0.00").format(Math.atan2(-targetNode.getCenterY() + sourceNode.getCenterY(),
							targetNode.getCenterX() - sourceNode.getCenterX()) / Math.PI) + "\u03C0",
					.5 * (sourceNode.getCenterX() + targetNode.getCenterX()),
					.5 * (sourceNode.getCenterY() + targetNode.getCenterY()) - 12);

			// hover
			if (firstHoverPointIndex != null) {
				g.setStroke(new Color(1., .8, .2, 1.));
				g.setLineWidth(5);
				g.strokeLine(points.get(firstHoverPointIndex).getX(), points.get(firstHoverPointIndex).getY(),
						points.get(firstHoverPointIndex + 1).getX(), points.get(firstHoverPointIndex + 1).getY());
			}

			// normal
			g.setStroke(fmmlxDiagram.isSelected(this) ? Color.RED : getPrimaryColor());
			g.setLineWidth(isSelected() ? 3 : 1);
			g.setLineDashes(getLineDashes());

			for (int i = 0; i < points.size() - 1; i++) {
				Vector<Point2D> intersections = diagram.findEdgeIntersections(points.get(i), points.get(i + 1));

				if (intersections.size() == 0) {
					g.strokeLine(points.get(i).getX(), points.get(i).getY(), points.get(i + 1).getX(),
							points.get(i + 1).getY());
				} else {

					Point2D first = points.get(i);
					Point2D last = points.get(i + 1);

					Point2D now = first.getX() < last.getX() ? first : last;
					Point2D endOfLine = first.getX() < last.getX() ? last : first;

					Collections.sort(intersections, new Comparator<Point2D>() {

						@Override
						public int compare(Point2D o1, Point2D o2) {
							return o1.getX() < o2.getX() ? -1 : o1.getX() == o2.getX() ? 0 : 1;
						}
					});

					boolean tunnelMode = false;
					final int R = 5;
					while (intersections.size() > 0) {
						Point2D next = intersections.remove(0);
						if (tunnelMode) {
							if (next.getX() - 2 * R > now.getX()) { // enough space to next
								g.strokeArc(now.getX() - R, now.getY() - R, R * 2, R * 2, 0, 90, ArcType.OPEN);
								g.strokeLine(now.getX() + R, now.getY(), next.getX() - R, next.getY());
								g.strokeArc(next.getX() - R, next.getY() - R, R * 2, R * 2, 90, 90, ArcType.OPEN);
							} else { // not enough space -> just line to next
								g.strokeLine(now.getX(), now.getY() - R, next.getX(), next.getY() - R);
							}
						} else {
							if (next.getX() - R > now.getX()) {
								g.strokeLine(now.getX(), now.getY(), next.getX() - R, next.getY());
								g.strokeArc(next.getX() - R, next.getY() - R, R * 2, R * 2, 90, 90, ArcType.OPEN);
							} else {
								g.strokeLine(now.getX(), now.getY(), now.getX(), next.getY() - R);
								g.strokeLine(now.getX(), now.getY() - R, next.getX(), next.getY() - R);
							}
							tunnelMode = true;
						}
						now = next;
					}

					// last intersection to end of line

					if (endOfLine.getX() - R > now.getX()) {
						g.strokeArc(now.getX() - R, now.getY() - R, R * 2, R * 2, 0, 90, ArcType.OPEN);
						g.strokeLine(now.getX() + R, now.getY(), endOfLine.getX(), endOfLine.getY());
					} else {
						g.strokeLine(now.getX(), now.getY() - R, endOfLine.getX(), endOfLine.getY() - R);
						g.strokeLine(endOfLine.getX(), now.getY() - R, endOfLine.getX(), endOfLine.getY());
					}
				}
			}

			if (newSourcePortRegion != null) {
				double[] xPoints2 = new double[] { (points.get(0).getX() + points.get(1).getX()) / 2,
						lastMousePosition.getX(),
						newSourcePortRegion == PortRegion.WEST ? sourceNode.getX()
								: newSourcePortRegion == PortRegion.EAST ? sourceNode.getMaxRight()
										: sourceNode.getCenterX() };
				double[] yPoints2 = new double[] { (points.get(0).getY() + points.get(1).getY()) / 2,
						lastMousePosition.getY(),
						newSourcePortRegion == PortRegion.NORTH ? sourceNode.getY()
								: newSourcePortRegion == PortRegion.SOUTH ? sourceNode.getMaxBottom()
										: sourceNode.getCenterY() };

				g.setStroke(new Color(1., .8, .2, 1.));
				g.strokePolyline(xPoints2, yPoints2, xPoints2.length);
				g.setStroke(new Color(0., .8, .2, 1.));
				g.setLineDashes(3, 4);
				g.strokePolyline(xPoints2, yPoints2, xPoints2.length);
			}

			if (newTargetPortRegion != null) {
				double[] xPoints2 = new double[] {
						(points.get(points.size() - 1).getX() + points.get(points.size() - 2).getX()) / 2,
						lastMousePosition.getX(),
						newSourcePortRegion == PortRegion.WEST ? targetNode.getX()
								: newTargetPortRegion == PortRegion.EAST ? targetNode.getMaxRight()
										: targetNode.getCenterX() };
				double[] yPoints2 = new double[] {
						(points.get(points.size() - 1).getY() + points.get(points.size() - 2).getY()) / 2,
						lastMousePosition.getY(),
						newSourcePortRegion == PortRegion.NORTH ? targetNode.getY()
								: newTargetPortRegion == PortRegion.SOUTH ? targetNode.getMaxBottom()
										: targetNode.getCenterY() };

				g.setStroke(new Color(1., .8, .2, 1.));
				g.strokePolyline(xPoints2, yPoints2, xPoints2.length);
				g.setStroke(new Color(0., .8, .2, 1.));
				g.setLineDashes(3, 4);
				g.strokePolyline(xPoints2, yPoints2, xPoints2.length);
			}
			/*
			 * if (pointToBeMoved != -1) { final double R = 1.5;
			 * g.fillOval(points.get(pointToBeMoved).getX() - R,
			 * points.get(pointToBeMoved).getY() - R, 2 * R, 2 * R); }
			 */

			// resetting the graphicsContext
			g.setLineDashes(0);

			drawDecoration(g, getTargetDecoration(), targetNode.getDirectionForEdge(targetEnd, false),
					targetNode.getPointForEdge(targetEnd, false));
			drawDecoration(g, getSourceDecoration(), sourceNode.getDirectionForEdge(sourceEnd, true),
					sourceNode.getPointForEdge(sourceEnd, true));

		}
	}

	private void drawDecoration(GraphicsContext g, HeadStyle decoration, PortRegion directionForEdge,
			Point2D pointForEdge) {

		Affine old = g.getTransform();
		Affine local = new Affine(old);
		double angle = (directionForEdge == PortRegion.EAST ? -0.5
				: directionForEdge == PortRegion.WEST ? 0.5 : directionForEdge == PortRegion.NORTH ? 1 : 0) * 180;
		local.appendRotation(angle, pointForEdge.getX(), pointForEdge.getY());
		g.setTransform(local);
		switch (decoration) {

		case NO_ARROW: {
			final double size = 6;
			g.setFill(Color.BLACK);
			g.fillOval(pointForEdge.getX() - size / 2, pointForEdge.getY()-2, size, size);

		}
			break;

		case ARROW: {
			final double size = 16;
			g.strokeLine(pointForEdge.getX() - size / 2, pointForEdge.getY() + size, pointForEdge.getX(),
					pointForEdge.getY());
			g.strokeLine(pointForEdge.getX() + size / 2, pointForEdge.getY() + size, pointForEdge.getX(),
					pointForEdge.getY());
		}
			break;

		case FULL_TRIANGLE: {
			double size = 16;
			g.setFill(Color.WHITE);
			g.fillPolygon(
					new double[] { pointForEdge.getX(), pointForEdge.getX() - size / 2,
							pointForEdge.getX() + size / 2 },
					new double[] { pointForEdge.getY(), pointForEdge.getY() + size, pointForEdge.getY() + size }, 3);
			g.strokePolygon(
					new double[] { pointForEdge.getX(), pointForEdge.getX() - size / 2,
							pointForEdge.getX() + size / 2 },
					new double[] { pointForEdge.getY(), pointForEdge.getY() + size, pointForEdge.getY() + size }, 3);
		}
			break;

		case CIRCLE: {
			double size = 16;
			g.setFill(Color.WHITE);
			g.fillOval(pointForEdge.getX() - size / 2, pointForEdge.getY() + 1, size, size);
			g.strokeOval(pointForEdge.getX() - size / 2, pointForEdge.getY() + 1, size, size);
		}
			break;
		default:
			break;

		}
		g.setTransform(old);
	}
	

	public HeadStyle getTargetDecoration() {

		return HeadStyle.ARROW;
	}

	public HeadStyle getSourceDecoration() {
		return HeadStyle.NO_ARROW;
	}

	protected Vector<Point2D> getAllPoints() {
		Vector<Point2D> allPoints = new Vector<Point2D>();
		allPoints.add(sourceNode.getPointForEdge(sourceEnd, true));
		allPoints.addAll(intermediatePoints);
		allPoints.add(targetNode.getPointForEdge(targetEnd, false));
		return allPoints;
	}

	protected abstract void layoutLabels();

	protected void align() {
		checkVisibilityMode();
		if(intermediatePoints.size() < 2) {
			if(sourceNode.getDirectionForEdge(sourceEnd, true).isHorizontal() == targetNode.getDirectionForEdge(targetEnd, false).isHorizontal()) {
				if(sourceNode.getDirectionForEdge(sourceEnd, true).isHorizontal())
					intermediatePoints.add(new Point2D((sourceNode.getCenterX() + targetNode.getCenterX())/2, sourceNode.getCenterY())); 
				else
					intermediatePoints.add(new Point2D(sourceNode.getCenterX(), (sourceNode.getCenterY() + targetNode.getCenterY()) / 2));
	
				if (targetNode.getDirectionForEdge(targetEnd, false).isHorizontal())
					intermediatePoints.add(new Point2D((sourceNode.getCenterX() + targetNode.getCenterX()) / 2, targetNode.getCenterY()));
				else
					intermediatePoints.add(new Point2D(targetNode.getCenterX(), (sourceNode.getCenterY() + targetNode.getCenterY()) / 2));
			} else if(sourceNode.getDirectionForEdge(sourceEnd, true) == PortRegion.EAST && targetNode.getDirectionForEdge(targetEnd, false) == PortRegion.NORTH) {
				intermediatePoints.add(new Point2D(sourceNode.getMaxRight() + 40, sourceNode.getCenterY())); 
				intermediatePoints.add(new Point2D(sourceNode.getMaxRight() + 40, targetNode.getY() - 40)); 
				intermediatePoints.add(new Point2D(targetNode.getCenterX(), targetNode.getY() - 40)); 
			} else {
				System.err.println("Unexpected initial edge alignment");
			}
			
		}
		
		
		Point2D first = sourceNode.getPointForEdge(sourceEnd, true);
		Point2D second = intermediatePoints.get(0);
		PortRegion sourceDirection = sourceNode.getDirectionForEdge(sourceEnd, true);

		Point2D newSecond = sourceDirection.isHorizontal() ? new Point2D(second.getX(), first.getY())
				: new Point2D(first.getX(), second.getY());
		intermediatePoints.setElementAt(newSecond, 0);

		Point2D penultimate = intermediatePoints.get(intermediatePoints.size() - 1);
		Point2D ultimate = targetNode.getPointForEdge(targetEnd, false);
		PortRegion targetDirection = targetNode.getDirectionForEdge(targetEnd, false);

		Point2D newPenultimate = targetDirection.isHorizontal() ? new Point2D(penultimate.getX(), ultimate.getY())
				: new Point2D(ultimate.getX(), penultimate.getY());
		intermediatePoints.setElementAt(newPenultimate, intermediatePoints.size() - 1);
	}
	
	protected void checkVisibilityMode() {visible = true;}

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
		if(!visible) return false;
		return null != isHit(new Point2D(x, y), 2.5);
	}

	public Integer isHit(Point2D p, Double tolerance) {
		if (p == null)
			return null;
		Vector<Point2D> points = getAllPoints();
		for (int i = 0; i < points.size() - 1; i++) {
			if (distance(p, points.get(i),
					points.get(i + 1)) < 0.2/* (tolerance == null ? DEFAULT_TOLERANCE : tolerance) */) {
				return i;
			}
		}
		return null;
	}

	private double distance(Point2D p, Point2D a, Point2D b) { // assume lines to be aligned

		double angleAP = Math.atan2(a.getY() - p.getY(), p.getX() - a.getX());
		double anglePB = Math.atan2(p.getY() - b.getY(), b.getX() - p.getX());
		double angleAB = (5 * Math.PI + angleAP - anglePB) % (2 * Math.PI) - Math.PI;
		return Math.abs(angleAB);
	}

	/*private double distance_OLD(Point2D testPoint, Point2D lineStart, Point2D lineEnd) { // some fancy math copied from
																							// the old diagram
		double normalLength = Math.sqrt((lineEnd.getX() - lineStart.getX()) * (lineEnd.getX() - lineStart.getX())
				+ (lineEnd.getY() - lineStart.getY()) * (lineEnd.getY() - lineStart.getY()));

		return Math.abs((testPoint.getX() - lineStart.getX()) * (lineEnd.getY() - lineStart.getY())
				- (testPoint.getY() - lineStart.getY()) * (lineEnd.getX() - lineStart.getX())) / normalLength;
	}*/

	@Override
	public final ContextMenu getContextMenu(DiagramActions actions) {
		ContextMenu localMenu = getContextMenuLocal(actions);
		if(localMenu.getItems().size()>0) localMenu.getItems().add(new SeparatorMenuItem());
		MenuItem repairItem = new MenuItem("Repair Edge Alignment");
		repairItem.setOnAction(e -> ensure90DegreeAngles());
		localMenu.getItems().add(repairItem);
		return localMenu;
	}

	public abstract ContextMenu getContextMenuLocal(DiagramActions actions);
	
	@Override
	public void moveTo(double x, double y, FmmlxDiagram diagram) {
		lastMousePosition = new Point2D(x, y);
		//System.err.println("move point " + pointToBeMoved + " to " + x + "," + y + (movementDirectionHorizontal?"H":"V"));
		if (pointToBeMoved != -1 && moveMode == MoveMode.normal) {
			if (movementDirectionHorizontal) {
				intermediatePoints.setElementAt(new Point2D(x, intermediatePoints.get(pointToBeMoved - 1).getY()),
						pointToBeMoved - 1);
				intermediatePoints.setElementAt(new Point2D(x, intermediatePoints.get(pointToBeMoved).getY()),
						pointToBeMoved);
			} else {
				intermediatePoints.setElementAt(new Point2D(intermediatePoints.get(pointToBeMoved - 1).getX(), y),
						pointToBeMoved - 1);
				intermediatePoints.setElementAt(new Point2D(intermediatePoints.get(pointToBeMoved).getX(), y),
						pointToBeMoved);
			}
		} else if (moveMode == MoveMode.moveSourcePortArea) {
			newSourcePortRegion = findBestRegion(sourceNode, x, y);
		} else if (moveMode == MoveMode.moveTargetPortArea) {
			newTargetPortRegion = findBestRegion(targetNode, x, y);
		}
	}

	private PortRegion findBestRegion(FmmlxObject node, double x, double y) {
		double angleMouse = Math.atan2(y - node.getCenterY(), x - node.getCenterX());
		double diffAngleNW = (4 * Math.PI + angleMouse
				- Math.atan2(node.getY() - node.getCenterY(), node.getX() - node.getCenterX())) % (2 * Math.PI);
		double diffAngleNE = (4 * Math.PI + angleMouse
				- Math.atan2(node.getY() - node.getCenterY(), node.getMaxRight() - node.getCenterX())) % (2 * Math.PI);
		double diffAngleSE = (4 * Math.PI + angleMouse
				- Math.atan2(node.getMaxBottom() - node.getCenterY(), node.getMaxRight() - node.getCenterX()))
				% (2 * Math.PI);
		double diffAngleSW = (4 * Math.PI + angleMouse
				- Math.atan2(node.getMaxBottom() - node.getCenterY(), node.getX() - node.getCenterX())) % (2 * Math.PI);

		if (diffAngleNW < diffAngleNE && diffAngleNW < diffAngleSE && diffAngleNW < diffAngleSW)
			return PortRegion.NORTH;
		if (diffAngleNE < diffAngleSE && diffAngleNE < diffAngleSW)
			return PortRegion.EAST;
		if (diffAngleSE < diffAngleSW)
			return PortRegion.SOUTH;

		return PortRegion.WEST;
	}

	public boolean isStartNode(FmmlxObject fmmlxObject) {
		return sourceNode == fmmlxObject;
	}

	public boolean isEndNode(FmmlxObject fmmlxObject) {
		return targetNode == fmmlxObject;
	}

	private void storeLatestValidPointConfiguration() {
		latestValidPointConfiguration.clear();
		latestValidPointConfiguration.addAll(intermediatePoints);
	}

	private transient Vector<Point2D> latestValidPointConfiguration = new Vector<>();
	private transient int pointToBeMoved = -1;
	private transient boolean movementDirectionHorizontal;
	private Integer firstHoverPointIndex;

	private enum MoveMode {
		normal, moveSourcePortArea, moveTargetPortArea
	}

	private transient MoveMode moveMode;
	private transient PortRegion newSourcePortRegion;
	private transient PortRegion newTargetPortRegion;
	private transient Point2D lastMousePosition;

	public void setPointAtToBeMoved(Point2D mousePoint) {
		Vector<Point2D> points = getAllPoints();
		// An edge has been dragged on at Point p.
		// if a point is already found
		if (pointToBeMoved != -1)
			return;

		Integer hitLine = isHit(mousePoint, 0.2);
		if(hitLine != null) { 
			if(hitLine > 0 && hitLine < points.size() - 2) {
			pointToBeMoved = hitLine;
			movementDirectionHorizontal = points.get(pointToBeMoved).getX() == points.get(pointToBeMoved+1).getX();
			}
			else if(hitLine == 0) {
				pointToBeMoved = -2;
				moveMode = MoveMode.moveSourcePortArea;
			}
			else if(hitLine == points.size() - 2) {
				pointToBeMoved = -2;
				moveMode = MoveMode.moveTargetPortArea;
			}		
		}
	}

	public void dropPoint() {
		/*
		 * Vector<Point2D> points = getAllPoints(); if (pointToBeMoved != -1) { // if
		 * point very close to other point, remove it. if
		 * (distance(points.get(pointToBeMoved), points.get(pointToBeMoved + 1)) <
		 * DEFAULT_TOLERANCE || distance(points.get(pointToBeMoved),
		 * points.get(pointToBeMoved - 1)) < DEFAULT_TOLERANCE) {
		 * points.remove(pointToBeMoved); } }
		 */
		// in any case no point to be moved anymore
		
		if (newSourcePortRegion != null && newSourcePortRegion != sourceNode.getDirectionForEdge(sourceEnd, true)) {
			if (newSourcePortRegion.isOpposite(sourceNode.getDirectionForEdge(sourceEnd, true))) {
				sourceNode.setDirectionForEdge(sourceEnd, true, newSourcePortRegion);
			} else { // requires a new point
				if (newSourcePortRegion == PortRegion.SOUTH) {
					intermediatePoints
							.insertElementAt(new Point2D(0 /* does not matter */, sourceNode.getMaxBottom() + 30), 0);
					intermediatePoints.setElementAt(
							new Point2D(intermediatePoints.get(1).getX(), sourceNode.getMaxBottom() + 30), 1);
				} else if (newSourcePortRegion == PortRegion.NORTH) {
					intermediatePoints.insertElementAt(new Point2D(0 /* does not matter */, sourceNode.getY() - 30), 0);
					intermediatePoints
							.setElementAt(new Point2D(intermediatePoints.get(1).getX(), sourceNode.getY() - 30), 1);
				} else if (newSourcePortRegion == PortRegion.WEST) {
					intermediatePoints.insertElementAt(new Point2D(sourceNode.getX() - 30, 0 /* does not matter */), 0);
					intermediatePoints
							.setElementAt(new Point2D(sourceNode.getX() - 30, intermediatePoints.get(1).getY()), 1);
				} else if (newSourcePortRegion == PortRegion.EAST) {
					intermediatePoints
							.insertElementAt(new Point2D(sourceNode.getMaxRight() + 30, 0 /* does not matter */), 0);
					intermediatePoints.setElementAt(
							new Point2D(sourceNode.getMaxRight() + 30, intermediatePoints.get(1).getY()), 1);
				}
				sourceNode.setDirectionForEdge(sourceEnd, true, newSourcePortRegion);
			}
		}

		if (newTargetPortRegion != null && newTargetPortRegion != targetNode.getDirectionForEdge(targetEnd, false)) {
			if (newTargetPortRegion.isOpposite(targetNode.getDirectionForEdge(targetEnd, false))) {
				targetNode.setDirectionForEdge(targetEnd, false, newTargetPortRegion);
			} else { // requires a new point
				if (newTargetPortRegion == PortRegion.SOUTH) {
					intermediatePoints.add(new Point2D(0 /* does not matter */, targetNode.getMaxBottom() + 30));
					intermediatePoints
							.setElementAt(new Point2D(intermediatePoints.get(intermediatePoints.size() - 2).getX(),
									targetNode.getMaxBottom() + 30), intermediatePoints.size() - 2);
				} else if (newTargetPortRegion == PortRegion.NORTH) {
					intermediatePoints.add(new Point2D(0 /* does not matter */, targetNode.getY() - 30));
					intermediatePoints
							.setElementAt(new Point2D(intermediatePoints.get(intermediatePoints.size() - 2).getX(),
									targetNode.getY() - 30), intermediatePoints.size() - 2);
				} else if (newTargetPortRegion == PortRegion.WEST) {
					intermediatePoints.add(new Point2D(targetNode.getX() - 30, 0 /* does not matter */));
					intermediatePoints.setElementAt(
							new Point2D(targetNode.getX() - 30,
									intermediatePoints.get(intermediatePoints.size() - 2).getY()),
							intermediatePoints.size() - 2);
				} else if (newTargetPortRegion == PortRegion.EAST) {
					intermediatePoints.add(new Point2D(targetNode.getMaxRight() + 30, 0 /* does not matter */));
					intermediatePoints.setElementAt(
							new Point2D(targetNode.getMaxRight() + 30,
									intermediatePoints.get(intermediatePoints.size() - 2).getY()),
							intermediatePoints.size() - 2);
				}
				targetNode.setDirectionForEdge(targetEnd, false, newTargetPortRegion);
			}
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
		return new Point2D((points.get(n).getX() + points.get(n - 1).getX()) / 2,
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

	@Override
	public double getMouseMoveOffsetX() {
		return 0;
	}

	@Override
	public double getMouseMoveOffsetY() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	protected Point2D getLabelPosition(int localId) {
		for (Object labelPositionO : labelPositions) {
			Vector<Object> labelPosition = (Vector<Object>) labelPositionO;
			int theirLocalId = (Integer) labelPosition.get(1);
			if (theirLocalId == localId) {
				return new Point2D((Float) labelPosition.get(2), (Float) labelPosition.get(3));
			}
		}
		return null;
	}

	@Override
	public void unHighlight() {
		firstHoverPointIndex = null;
	}

	public void removeRedundantPoints() {
		pointToBeMoved = -1;
		firstHoverPointIndex = null;
		final double TOLERANCE = 3;

		if (intermediatePoints.size() < 3)
			return;
		Integer removeIndex = null;
		for (int i = 0; removeIndex == null && i < intermediatePoints.size() - 1; i++) {
			if (intermediatePoints.get(i).distance(intermediatePoints.get(i + 1)) < TOLERANCE)
				removeIndex = i;
		}

		if (removeIndex != null) {
			intermediatePoints.remove(removeIndex.intValue());
			intermediatePoints.remove(removeIndex.intValue());
			removeRedundantPoints();
			align();
			ensure90DegreeAngles();
		}
	}

	private void ensure90DegreeAngles() {
		boolean horizontal = sourceNode.getDirectionForEdge(sourceEnd, true).isHorizontal();

		for (int countTotal = 1; countTotal <= intermediatePoints.size(); countTotal++) {
			Point2D now = intermediatePoints.get(countTotal - 1);
			Point2D previous = getAllPoints().get(countTotal - 1);
			if (horizontal) {
				now = new Point2D(now.getX(), previous.getY());
			} else {
				now = new Point2D(previous.getX(), now.getY());
			}
			intermediatePoints.setElementAt(now, countTotal - 1);
			horizontal = !horizontal;
		}
	}

	public boolean isVisible() {
		return true;
	}
}
