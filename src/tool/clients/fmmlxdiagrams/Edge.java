package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import org.w3c.dom.Element;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.xmlManipulator.XmlHandler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

public abstract class Edge<ConcreteNode extends Node> implements CanvasElement {

	final public String path;
	private Vector<Point2D> intermediatePoints = new Vector<>();
	final protected ConcreteNode sourceNode;
	final protected ConcreteNode targetNode;
	protected final Double DEFAULT_TOLERANCE = 6.;
	protected boolean layoutingFinishedSuccesfully;
	protected AbstractPackageViewer diagram;
	
	public final Edge<ConcreteNode>.End sourceEnd = new Source(this);
	public final Edge<ConcreteNode>.End targetEnd = new Target(this);

	private transient MoveMode moveMode;
	private transient PortRegion newSourcePortRegion;
	private transient PortRegion newTargetPortRegion;

	protected transient PortRegion sourcePortRegion;
	protected transient PortRegion targetPortRegion;
	private transient Point2D lastMousePositionRaw;

	public abstract class End {public final Edge<ConcreteNode> edge; private End(Edge<ConcreteNode> edge) {this.edge = edge;} public abstract ConcreteNode getNode();}
	public class Source extends End{private Source(Edge<ConcreteNode> edge) {super(edge);} public ConcreteNode getNode() {return edge.sourceNode;}}
	public class Target extends End{private Target(Edge<ConcreteNode> edge) {super(edge);} public ConcreteNode getNode() {return edge.targetNode;}}

	protected boolean visible;

	private HashMap<Integer, Point2D> labelPositions;

	protected enum HeadStyle {
		NO_ARROW, ARROW, FULL_TRIANGLE, CIRCLE
	}

	private transient Vector<Point2D> latestValidPointConfiguration = new Vector<>();
	private transient int pointToBeMoved = -1;
	private transient boolean movementDirectionHorizontal;
	private Integer firstHoverPointIndex;

	private enum MoveMode {
		normal, moveSourcePortArea, moveTargetPortArea
	}
	
	public enum Anchor {CENTRE_MOVABLE, SOURCE_LEVEL, TARGET_LEVEL, SOURCE_MULTI, TARGET_MULTI,CENTRE_SELFASSOCIATION}

	public Edge(String path, 
			ConcreteNode startNode, ConcreteNode endNode, 
			Vector<Point2D> intermediatePoints,
			PortRegion sourcePortRegion, PortRegion targetPortRegion,
			Vector<Object> labelPositions, AbstractPackageViewer diagram) {
		layoutingFinishedSuccesfully = false;
		initLabelPositionMap(labelPositions);
		this.path = path;
		this.sourceNode = startNode;
		this.targetNode = endNode;
		this.diagram = diagram;
		if (intermediatePoints == null || intermediatePoints.size() < 1) {} else {
			this.intermediatePoints.addAll(intermediatePoints);
		}
		storeLatestValidPointConfiguration();
		
		if(sourcePortRegion==null) {
			if(this instanceof InheritanceEdge) sourcePortRegion = PortRegion.NORTH;
			else if(startNode == endNode) sourcePortRegion = PortRegion.EAST; 
			else
			sourcePortRegion = determineInitialPort(startNode,
				this.intermediatePoints.size() < 1 ? null : this.intermediatePoints.firstElement(),
				startNode.getCenterX() < endNode.getCenterX() ? PortRegion.EAST : PortRegion.WEST);
			this.sourcePortRegion = sourcePortRegion;
		}
		if(targetPortRegion==null) {
			if(this instanceof InheritanceEdge) targetPortRegion = PortRegion.SOUTH;
			else if(startNode == endNode) targetPortRegion = PortRegion.NORTH; 
			else
			targetPortRegion = determineInitialPort(endNode,
				this.intermediatePoints.size() < 1 ? null : this.intermediatePoints.lastElement(),
				startNode.getCenterX() < endNode.getCenterX() ? PortRegion.WEST : PortRegion.EAST);
			this.targetPortRegion = targetPortRegion;
		}
        startNode.addEdgeEnd(this.sourceEnd, sourcePortRegion);
        endNode.addEdgeEnd(this.targetEnd, targetPortRegion);

	}

	public ConcreteNode getSourceNode() {
		return sourceNode;
	}

	public ConcreteNode getTargetNode() {
		return targetNode;
	}

	public void setIntermediatePoints(Vector<Point2D> intermediatePoints) {
		this.intermediatePoints = intermediatePoints;
	}

	public PortRegion getSourcePortRegion() {
		return sourceNode.getDirectionForEdge(this.sourceEnd, true);
	}

	public PortRegion getTargetPortRegion() {
		return targetNode.getDirectionForEdge(this.targetEnd, false);
	}

	private PortRegion determineInitialPort(ConcreteNode node, Point2D nextPoint, PortRegion defaultRegion) {
		if (nextPoint == null) {
			return defaultRegion;
		}
		if (node.getX() < nextPoint.getX() && nextPoint.getX() < node.getRightX()) {
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
	public final void paintOn(GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane view) {
		if(!isVisible()) return;
		if(!layoutingFinishedSuccesfully) {
			layoutLabels(view.getDiagram()); 
			view.getDiagram().redraw();
		} else {
			Vector<Point2D> points = getAllPoints();
			/* SHOW ANGLE 
            g.setFill(new Color(.8, .8, .9, 1.));
			g.fillText(
					new DecimalFormat("0.00").format(Math.atan2(-targetNode.getCenterY() + sourceNode.getCenterY(),
							targetNode.getCenterX() - sourceNode.getCenterX()) / Math.PI) + "\u03C0",
					.5 * (sourceNode.getCenterX() + targetNode.getCenterX()),
					.5 * (sourceNode.getCenterY() + targetNode.getCenterY()) - 12); */

			// hover
			if (firstHoverPointIndex != null) {
				g.setStroke(new Color(1., .8, .2, 1.));
				g.setLineWidth(5);
				g.strokeLine(points.get(firstHoverPointIndex).getX(), points.get(firstHoverPointIndex).getY(),
						points.get(firstHoverPointIndex + 1).getX(), points.get(firstHoverPointIndex + 1).getY());
			}

			// normal
			g.setStroke(view.getDiagram().isSelected(this) ? Color.RED : getPrimaryColor());
			g.setLineWidth(isSelected() ? 3 : 1);
			g.setLineDashes(getLineDashes());

			for (int i = 0; i < points.size() - 1; i++) {
//				if(i!=0) try {
//					g.setFill(Color.PURPLE);
//					Point2D hoverRaw = g.getTransform().inverseTransform(points.get(i));
//					g.fillText(""+hoverRaw, points.get(i).getX(), points.get(i).getY()+15);
//				} catch (NonInvertibleTransformException e) {}
				
				Vector<Point2D> intersections = view.getDiagram().findEdgeIntersections(points.get(i), points.get(i + 1));

				if (intersections.size() == 0) {
					g.strokeLine(points.get(i).getX(), points.get(i).getY(), points.get(i + 1).getX(),
							points.get(i + 1).getY());
				} else {

					Point2D first = points.get(i);
					Point2D last = points.get(i + 1);

					Point2D now = first.getX() < last.getX() ? first : last;
					Point2D endOfLine = first.getX() < last.getX() ? last : first;

					intersections.sort(Comparator.comparingDouble(Point2D::getX));

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
						lastMousePositionRaw.getX(),
						newSourcePortRegion == PortRegion.WEST ? sourceNode.getX()
								: newSourcePortRegion == PortRegion.EAST ? sourceNode.getRightX()
										: sourceNode.getCenterX() };
				double[] yPoints2 = new double[] { (points.get(0).getY() + points.get(1).getY()) / 2,
						lastMousePositionRaw.getY(),
						newSourcePortRegion == PortRegion.NORTH ? sourceNode.getY()
								: newSourcePortRegion == PortRegion.SOUTH ? sourceNode.getBottomY()
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
						lastMousePositionRaw.getX(),
						newSourcePortRegion == PortRegion.WEST ? targetNode.getX()
								: newTargetPortRegion == PortRegion.EAST ? targetNode.getRightX()
										: targetNode.getCenterX() };
				double[] yPoints2 = new double[] {
						(points.get(points.size() - 1).getY() + points.get(points.size() - 2).getY()) / 2,
						lastMousePositionRaw.getY(),
						newSourcePortRegion == PortRegion.NORTH ? targetNode.getY()
								: newTargetPortRegion == PortRegion.SOUTH ? targetNode.getBottomY()
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

		Affine old;
		Affine local;
		
		old = g.getTransform();
		local = new Affine(old);
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
			double size = 14;
			g.setFill(Color.WHITE);
			g.setLineWidth(2);
			g.fillOval(pointForEdge.getX() - size / 2, pointForEdge.getY() + 2, size, size);
			g.strokeOval(pointForEdge.getX() - size / 2, pointForEdge.getY() + 2, size, size);
			g.setLineWidth(1);
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
		Vector<Point2D> allPoints = new Vector<>();
		allPoints.add(sourceNode.getPointForEdge(sourceEnd, true));
		allPoints.addAll(intermediatePoints);
		allPoints.add(targetNode.getPointForEdge(targetEnd, false));
		return allPoints;
	}

	protected abstract void layoutLabels(FmmlxDiagram diagram);

	public void align() {
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
				intermediatePoints.add(new Point2D(sourceNode.getRightX() + 40, sourceNode.getCenterY())); 
				intermediatePoints.add(new Point2D(sourceNode.getRightX() + 40, targetNode.getY() - 40)); 
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

	protected String getSvgDashes() {
		return "0";
	}

	protected String getSvgStrokeWidth() {
		return "1";
	}

	private boolean isSelected() {
		return false;
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, GraphicsContext g,  Affine currentTransform, FmmlxDiagram.DiagramViewPane view) {
		if(!isVisible()) return false;
		return null != isHit(new Point2D(mouseX, mouseY), 2.5, view.getCanvasTransform());
	}

	public Integer isHit(Point2D mouse, Double tolerance, Affine canvasTransform) {
		if (mouse == null)
			return null;
		Point2D p;
		try {
			p = canvasTransform.inverseTransform(mouse);
//			System.err.println(getName() + ": " + p);
			Vector<Point2D> points = getAllPoints();
			for (int i = 0; i < points.size() - 1; i++) {
				if (distance(p, points.get(i),
						points.get(i + 1)) < 0.2/* (tolerance == null ? DEFAULT_TOLERANCE : tolerance) */) {
					return i;
				}
			}
			return null;
		} catch (NonInvertibleTransformException e) {
			return null;
		}
	}

	private double distance(Point2D p, Point2D a, Point2D b) { // assume lines to be aligned

		double angleAP = Math.atan2(a.getY() - p.getY(), p.getX() - a.getX());
		double anglePB = Math.atan2(p.getY() - b.getY(), b.getX() - p.getX());
		double angleAB = (5 * Math.PI + angleAP - anglePB) % (2 * Math.PI) - Math.PI;
		return Math.abs(angleAB);
	}

	@Override
	public final ContextMenu getContextMenu(FmmlxDiagram.DiagramViewPane diagram, Point2D absolutePoint) {
		ContextMenu localMenu = getContextMenuLocal(diagram.getDiagram().actions);
		if(localMenu.getItems().size()>0) localMenu.getItems().add(new SeparatorMenuItem());
		MenuItem repairItem = new MenuItem("Repair Edge Alignment");
		repairItem.setOnAction(e -> ensure90DegreeAngles());
		localMenu.getItems().add(repairItem);
		return localMenu;
	}

	public abstract ContextMenu getContextMenuLocal(DiagramActions actions);
	
	@Override
	public void moveTo(double mouseX, double mouseY, FmmlxDiagram.DiagramViewPane view) {
	  try {
		Point2D mouse = new Point2D(mouseX, mouseY);
		Point2D raw = view.getCanvasTransform().inverseTransform(mouse);
        lastMousePositionRaw = raw;
		double x = raw.getX();
		double y = raw.getY();
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
			this.sourcePortRegion = newSourcePortRegion;
		} else if (moveMode == MoveMode.moveTargetPortArea) {
			newTargetPortRegion = findBestRegion(targetNode, x, y);
			this.targetPortRegion = newTargetPortRegion;
		}
      } catch (NonInvertibleTransformException e) {}
	}

	private PortRegion findBestRegion(Node node, double x, double y) {
		double angleMouse = Math.atan2(y - node.getCenterY(), x - node.getCenterX());
		double diffAngleNW = (4 * Math.PI + angleMouse
				- Math.atan2(node.getY() - node.getCenterY(), node.getX() - node.getCenterX())) % (2 * Math.PI);
		double diffAngleNE = (4 * Math.PI + angleMouse
				- Math.atan2(node.getY() - node.getCenterY(), node.getRightX() - node.getCenterX())) % (2 * Math.PI);
		double diffAngleSE = (4 * Math.PI + angleMouse
				- Math.atan2(node.getBottomY() - node.getCenterY(), node.getRightX() - node.getCenterX()))
				% (2 * Math.PI);
		double diffAngleSW = (4 * Math.PI + angleMouse
				- Math.atan2(node.getBottomY() - node.getCenterY(), node.getX() - node.getCenterX())) % (2 * Math.PI);

		if (diffAngleNW < diffAngleNE && diffAngleNW < diffAngleSE && diffAngleNW < diffAngleSW)
			return PortRegion.NORTH;
		if (diffAngleNE < diffAngleSE && diffAngleNE < diffAngleSW)
			return PortRegion.EAST;
		if (diffAngleSE < diffAngleSW)
			return PortRegion.SOUTH;

		return PortRegion.WEST;
	}

	public boolean isSourceNode(FmmlxObject fmmlxObject) {
		return sourceNode == fmmlxObject;
	}

	public boolean isTargetNode(FmmlxObject fmmlxObject) {
		return targetNode == fmmlxObject;
	}

	private void storeLatestValidPointConfiguration() {
		latestValidPointConfiguration.clear();
		latestValidPointConfiguration.addAll(intermediatePoints);
	}



	public void setPointAtToBeMoved(Point2D mousePoint, Affine canvasTransform) {
		Vector<Point2D> points = getAllPoints();
		// An edge has been dragged on at Point p.
		// if a point is already found
		if (pointToBeMoved != -1)
			return;

		Integer hitLine = isHit(mousePoint, 0.2, canvasTransform);
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

	public void dropPoint(FmmlxDiagram diagram) {
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
							.insertElementAt(new Point2D(0 /* does not matter */, sourceNode.getBottomY() + 30), 0);
					intermediatePoints.setElementAt(
							new Point2D(intermediatePoints.get(1).getX(), sourceNode.getBottomY() + 30), 1);
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
							.insertElementAt(new Point2D(sourceNode.getRightX() + 30, 0 /* does not matter */), 0);
					intermediatePoints.setElementAt(
							new Point2D(sourceNode.getRightX() + 30, intermediatePoints.get(1).getY()), 1);
				}
				sourceNode.setDirectionForEdge(sourceEnd, true, newSourcePortRegion);
			}
		}

		if (newTargetPortRegion != null && newTargetPortRegion != targetNode.getDirectionForEdge(targetEnd, false)) {
			if (newTargetPortRegion.isOpposite(targetNode.getDirectionForEdge(targetEnd, false))) {
				targetNode.setDirectionForEdge(targetEnd, false, newTargetPortRegion);
			} else { // requires a new point
				if (newTargetPortRegion == PortRegion.SOUTH) {
					intermediatePoints.add(new Point2D(0 /* does not matter */, targetNode.getBottomY() + 30));
					intermediatePoints
							.setElementAt(new Point2D(intermediatePoints.get(intermediatePoints.size() - 2).getX(),
									targetNode.getBottomY() + 30), intermediatePoints.size() - 2);
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
					intermediatePoints.add(new Point2D(targetNode.getRightX() + 30, 0 /* does not matter */));
					intermediatePoints.setElementAt(
							new Point2D(targetNode.getRightX() + 30,
									intermediatePoints.get(intermediatePoints.size() - 2).getY()),
							intermediatePoints.size() - 2);
				}
				targetNode.setDirectionForEdge(targetEnd, false, newTargetPortRegion);
			}
		}

		storeLatestValidPointConfiguration();

		if(pointToBeMoved != -1 || newSourcePortRegion!= null || newTargetPortRegion != null) {
				diagram.getComm().sendCurrentPositions(diagram.getID(), this);
		}
		 
		pointToBeMoved = -1;
		newSourcePortRegion = null;
		newTargetPortRegion = null;
		moveMode = MoveMode.normal;

	}

	public Vector<Point2D> getIntermediatePoints() {
		return new Vector<>(intermediatePoints);
	}

	protected Point2D getCentreAnchor() {
		Vector<Point2D> points = getAllPoints();
		int n = points.size() / 2;
		return new Point2D((points.get(n).getX() + points.get(n - 1).getX()) / 2,
				(points.get(n).getY() + points.get(n - 1).getY()) / 2);
	}

	public String getPath() {
		return path;
	}

	@Override
	public void highlightElementAt(Point2D mouse, Affine canvasTransform) {
		firstHoverPointIndex = isHit(mouse, .2, canvasTransform);
	}

	@Override
	public void setOffsetAndStoreLastValidPosition(Point2D p) {
		storeLatestValidPointConfiguration();
	}

	protected Point2D getLabelPosition(int localId) {
		return labelPositions.get(localId);
	}

	private void initLabelPositionMap(Vector<Object> labelPositions2) {
		labelPositions = new HashMap<>();
		for (Object labelPositionO : labelPositions2) {
			Vector<Object> labelPosition = (Vector<Object>) labelPositionO;
			int theirLocalId = (Integer) labelPosition.get(1);
			float x = (Float) labelPosition.get(2);
			float y = (Float) labelPosition.get(3);
			Point2D p = new Point2D(x, y);
			labelPositions.put(theirLocalId, p);
		}
	}

	@Override
	public void unHighlight() {
		firstHoverPointIndex = null;
	}

	public void removeRedundantPoints() {
		pointToBeMoved = -1;
		firstHoverPointIndex = null;
		final double TOLERANCE = 3;

		if (intermediatePoints.size() <= 3)
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

	public final boolean isVisible() {
		return visible && !sourceNode.isHidden() && !targetNode.isHidden();
	}
	
	public void updatePosition(DiagramEdgeLabel<?> del) {
		labelPositions.put(del.localID, new Point2D(del.getRelativeX(), del.getRelativeY()));
	}

	public abstract String getName();
	
	protected void createLabel(String value, int localId, Anchor anchor, Runnable action, Color textColor, Color bgColor, FmmlxDiagram diagram) {
		double w = FmmlxDiagram.calculateTextWidth(value);
		double h = FmmlxDiagram.calculateTextHeight();
		
		if(Anchor.CENTRE_MOVABLE == anchor) {
			Point2D storedPostion = getLabelPosition(localId);
			Vector<ConcreteNode> anchors = new Vector<>();
			anchors.add(getSourceNode());
			anchors.add(getTargetNode());
			if(storedPostion != null) {
				diagram.addLabel(new DiagramEdgeLabel<>(this, localId, action, null, anchors, value, storedPostion.getX(), storedPostion.getY(), w, h, textColor, bgColor));
			} else {
				diagram.addLabel(new DiagramEdgeLabel<>(this, localId, action, null, anchors, value, 0, -h*1.5, w, h, textColor, bgColor));
			}
		} else if (Anchor.CENTRE_SELFASSOCIATION==anchor) {
			Point2D storedPosition = getLabelPosition(localId);
			Vector<ConcreteNode> anchors = new Vector<>();
			anchors.add(getSourceNode());
			anchors.add(getTargetNode());
			if(storedPosition != null) {
				diagram.addLabel(new DiagramEdgeLabel<>(this, localId, action, null, anchors, value, storedPosition.getX(), storedPosition.getY(), w, h, textColor, bgColor));
			} else {
				diagram.addLabel(new DiagramEdgeLabel<>(this, localId, action, null, anchors, value, sourceNode.getWidth()/2, -4*h-0.5*sourceNode.getHeight(), w, h, textColor, bgColor));
			}
		} else {
			Vector<ConcreteNode> anchors = new Vector<>();
			double x,y;
			Point2D p;
			PortRegion dir;
			if(anchor == Anchor.SOURCE_LEVEL || anchor == Anchor.SOURCE_MULTI) {
				p = getSourceNode().getPointForEdge(sourceEnd, true);
				dir = getSourceNode().getDirectionForEdge(sourceEnd, true);
				anchors.add(getSourceNode());
			} else {
				p = getTargetNode().getPointForEdge(targetEnd, false);
				dir = getTargetNode().getDirectionForEdge(targetEnd, false); 
				anchors.add(getTargetNode());
			}
			ConcreteNode node = anchors.firstElement();

			final double TEXT_X_DIFF = 10;
			final double TEXT_Y_DIFF = 10;
			switch(anchor) {
			case SOURCE_LEVEL:
			case TARGET_LEVEL: {
				if(dir == PortRegion.SOUTH) {
					y = TEXT_Y_DIFF;
				} else {
					y = -TEXT_Y_DIFF-h;
				}
				if(dir == PortRegion.EAST) {
					x = TEXT_X_DIFF;
				} else {
					x = -TEXT_X_DIFF - w;
				}
				break;}
			case SOURCE_MULTI: 
			case TARGET_MULTI: {
				if(dir == PortRegion.NORTH) {
					y = -TEXT_Y_DIFF-h;
				} else {
					y = TEXT_Y_DIFF;
				}
				if(dir == PortRegion.WEST) {
					x = -TEXT_X_DIFF - w;
				} else {
					x = TEXT_X_DIFF;
				}
				break;}
			default: {x=0;y=0;break;}
			}
			diagram.addLabel(new DiagramEdgeLabel<>(this, localId, action, null, anchors, value, 
					p.getX() - node.getCenterX() + x, 
					p.getY() - node.getCenterY() + y, 
					w, h, textColor, bgColor));
		}
	}

	public double getMaxX() {
		double i = Double.NEGATIVE_INFINITY;
		for(Point2D p : intermediatePoints) {
			i = Math.max(i, p.getX());
		}
		return i;
	}

	public double getMaxY() {
		double i = Double.NEGATIVE_INFINITY;
		for(Point2D p : intermediatePoints) {
			i = Math.max(i, p.getY());
		}
		return i;
	}

	@Override
	public void paintToSvg(XmlHandler xmlHandler, FmmlxDiagram diagram) {
		if(!isVisible()) return;

		Color color = diagram.isSelected(this) ? Color.RED : getPrimaryColor();
		String strokeColor = color.toString().split("x")[1].substring(0,6);

		Element group = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_GROUP);
		group.setAttribute(SvgConstant.ATTRIBUTE_GROUP_TYPE, "edge");

		Vector<Point2D> points = getAllPoints();
		for (int i = 0; i < points.size() - 1; i++) {
			Vector<Point2D> intersections = diagram.findEdgeIntersections(points.get(i), points.get(i + 1));

			if (intersections.size() == 0) {

				Element path = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
				path.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
				path.setAttribute(SvgConstant.ATTRIBUTE_STROKE_DASHARRAY, getSvgDashes()+"");
				path.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
				String pathString = "M" + points.get(i).getX() + " " + points.get(i).getY() +
						" L" + points.get(i + 1).getX() + " " + points.get(i + 1).getY();
				path.setAttribute(SvgConstant.ATTRIBUTE_D, pathString);
				path.setAttribute(SvgConstant.ATTRIBUTE_FILL, "none");
				xmlHandler.addXmlElement(group, path);

			} else {

				Point2D first = points.get(i);
				Point2D last = points.get(i + 1);

				Point2D now = first.getX() < last.getX() ? first : last;
				Point2D endOfLine = first.getX() < last.getX() ? last : first;

				intersections.sort(Comparator.comparingDouble(Point2D::getX));

				boolean tunnelMode = false;
				final int R = 5;

				while (intersections.size() > 0) {
					Point2D next = intersections.remove(0);
					if (tunnelMode) {
						if (next.getX() - 2 * R > now.getX()) { // enough space to next
							Element path = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
							path.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
							path.setAttribute(SvgConstant.ATTRIBUTE_STROKE_DASHARRAY, getSvgDashes()+"");
							path.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
							String pathString = "M" + now.getX() + " " + (now.getY() - R) +
									"A" +
									" 5 5" + // radiusX radiusY
									" 0" + // rotation
									" 0" +
									" 1" + // Clockwise
									" " + (now.getX() + R) +// X-Endpoint
									" " + now.getY();// Y-Endpoint
							path.setAttribute(SvgConstant.ATTRIBUTE_D, pathString);
							path.setAttribute(SvgConstant.ATTRIBUTE_FILL, "none");
							xmlHandler.addXmlElement(group, path);

							Element path1 = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
							path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
							path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE_DASHARRAY, getSvgDashes()+"");
							path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
							String pathString1 = "M" + (now.getX() + R) + " " + now.getY() +
									" L" + (next.getX() - R) + " " + next.getY();
							path1.setAttribute(SvgConstant.ATTRIBUTE_D, pathString1);
							path1.setAttribute(SvgConstant.ATTRIBUTE_FILL, "none");
							xmlHandler.addXmlElement(group, path1);

							Element path2 = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
							path2.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
							path2.setAttribute(SvgConstant.ATTRIBUTE_STROKE_DASHARRAY, getSvgDashes()+"");
							path2.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
							String pathString2 = "M" + (next.getX() - R) + " " + next.getY() +
									"A" +
									" 5 5" + // radiusX radiusY
									" 0" + // rotation
									" 0" +
									" 1" + // Clockwise
									" " + next.getX() +// X-Endpoint
									" " + (next.getY() - R);// Y-Endpoint
							path2.setAttribute(SvgConstant.ATTRIBUTE_D, pathString2);
							path2.setAttribute(SvgConstant.ATTRIBUTE_FILL, "none");
							xmlHandler.addXmlElement(group, path2);


						} else { // not enough space -> just line to next
							Element path = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
							path.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
							path.setAttribute(SvgConstant.ATTRIBUTE_STROKE_DASHARRAY, getSvgDashes()+"");
							path.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
							String pathString = "M" + now.getX() + " " + (now.getY() - R) +
									" L" + next.getX() + " " + (next.getY() - R);
							path.setAttribute(SvgConstant.ATTRIBUTE_D, pathString);
							path.setAttribute(SvgConstant.ATTRIBUTE_FILL, "none");
							xmlHandler.addXmlElement(group, path);
						}
					} else {
						if (next.getX() - R > now.getX()) {
							Element path1 = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
							path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
							path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE_DASHARRAY, getSvgDashes()+"");
							path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
							String pathString1 = "M" + now.getX() + " " + now.getY() +
									" L" + (next.getX() - R) + " " + next.getY();
							path1.setAttribute(SvgConstant.ATTRIBUTE_D, pathString1);
							path1.setAttribute(SvgConstant.ATTRIBUTE_FILL, "none");
							xmlHandler.addXmlElement(group, path1);

							Element path2 = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
							path2.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
							path2.setAttribute(SvgConstant.ATTRIBUTE_STROKE_DASHARRAY, getSvgDashes()+"");
							path2.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
							String pathString2 = "M" + (next.getX() - R) + " " + next.getY() +
									"A" +
									" 5 5" + // radiusX radiusY
									" 0" + // rotation
									" 0" + //
									" 1" + // Clockwise
									" " + next.getX() +// X-Endpoint
									" " + (next.getY() - R);// Y-Endpoint
							path2.setAttribute(SvgConstant.ATTRIBUTE_D, pathString2);
							path2.setAttribute(SvgConstant.ATTRIBUTE_FILL, "none");
							xmlHandler.addXmlElement(group, path2);

						} else {
							Element path1 = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
							path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
							path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE_DASHARRAY, getSvgDashes()+"");
							path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
							String pathString1 = "M" + now.getX() + " " + now.getY() +
									" L" + now.getX() + " " + (next.getY() - R);
							path1.setAttribute(SvgConstant.ATTRIBUTE_D, pathString1);
							path1.setAttribute(SvgConstant.ATTRIBUTE_FILL, "none");
							xmlHandler.addXmlElement(group, path1);

							Element path2 = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
							path2.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
							path2.setAttribute(SvgConstant.ATTRIBUTE_STROKE_DASHARRAY, getSvgDashes()+"");
							path2.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
							String pathString2 = "M" + now.getX() + " " + (now.getY() - R) +
									" L" + next.getX() + " " + (next.getY() - R);
							path2.setAttribute(SvgConstant.ATTRIBUTE_D, pathString2);
							path2.setAttribute(SvgConstant.ATTRIBUTE_FILL, "none");
							xmlHandler.addXmlElement(group, path2);
						}
						tunnelMode = true;
					}
					now = next;
				}
				// last intersection to end of line

				if (endOfLine.getX() - R > now.getX()) {

					Element path = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
					path.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
					path.setAttribute(SvgConstant.ATTRIBUTE_STROKE_DASHARRAY, getSvgDashes()+"");
					path.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
					String pathString = "M" + now.getX() + " " + (now.getY() - R) +
							"A" +
							" 5 5" + // radiusX radiusY
							" 0" + // rotation
							" 0" +
							" 1" + // Clockwise
							" " + (now.getX() + R) +// X-Endpoint
							" " + now.getY();// Y-Endpoint
					path.setAttribute(SvgConstant.ATTRIBUTE_D, pathString);
					path.setAttribute(SvgConstant.ATTRIBUTE_FILL, "none");
					xmlHandler.addXmlElement(group, path);


					Element path1 = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
					path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
					path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE_DASHARRAY, getSvgDashes()+"");
					path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
					String pathString1 = "M" + (now.getX() + R) + " " + now.getY() +
							" L" + endOfLine.getX() + " " + endOfLine.getY();
					path1.setAttribute(SvgConstant.ATTRIBUTE_D, pathString1);
					path1.setAttribute(SvgConstant.ATTRIBUTE_FILL, "none");
					xmlHandler.addXmlElement(group, path1);

				} else {

					Element path = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
					path.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
					path.setAttribute(SvgConstant.ATTRIBUTE_STROKE_DASHARRAY, getSvgDashes()+"");
					path.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
					String pathString = "M" + now.getX() + " " + (now.getY() - R) +
							" L" + endOfLine.getX() + " " + (endOfLine.getY() - R);
					path.setAttribute(SvgConstant.ATTRIBUTE_D, pathString);
					path.setAttribute(SvgConstant.ATTRIBUTE_FILL, "none");
					xmlHandler.addXmlElement(group, path);

					Element path1 = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
					path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
					path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE_DASHARRAY, getSvgDashes()+"");
					path1.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
					String pathString1 = "M" + endOfLine.getX() + " " + (now.getY() - R) +
							" L" + endOfLine.getX() + " " + endOfLine.getY();
					path1.setAttribute(SvgConstant.ATTRIBUTE_D, pathString1);
					path1.setAttribute(SvgConstant.ATTRIBUTE_FILL, "none");
					xmlHandler.addXmlElement(group, path1);
				}
			}
		}

		drawEdgeSvgDecoration(xmlHandler, group, getTargetDecoration(), targetNode.getDirectionForEdge(targetEnd, false),
				targetNode.getPointForEdge(targetEnd, false), strokeColor);
		drawEdgeSvgDecoration(xmlHandler, group, getSourceDecoration(), sourceNode.getDirectionForEdge(sourceEnd, true),
				sourceNode.getPointForEdge(sourceEnd, true), strokeColor);

		xmlHandler.addXmlElement(xmlHandler.getRoot(), group);
	}

	protected void drawEdgeSvgDecoration(XmlHandler xmlHandler, Element group, HeadStyle decoration, PortRegion directionForEdge,
										 Point2D pointForEdge, String strokeColor){
		Element decor;

		switch (decoration) {
			case NO_ARROW: {
				final double size = 3;
				decor =  xmlHandler.createXmlElement(SvgConstant.TAG_NAME_CIRCLE);
				decor.setAttribute(SvgConstant.ATTRIBUTE_CX, (pointForEdge.getX())+"");
				decor.setAttribute(SvgConstant.ATTRIBUTE_CY, (pointForEdge.getY())+"");
				decor.setAttribute(SvgConstant.ATTRIBUTE_R, size+"");
				xmlHandler.addXmlElement(group, decor);
			}
			break;

			case ARROW: {
				final double size = 16;
				decor = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
				StringBuilder pathString = new StringBuilder("M");
				StringBuilder transform = new StringBuilder("rotate");
				if(PortRegion.WEST.equals(directionForEdge)){
					transform.append("(90,").append(pointForEdge.getX() - size / 2).append(", ").append(pointForEdge.getY()).append(")");
					pathString.append(pointForEdge.getX() - size).append(" ").append(pointForEdge.getY() + size/2);
					pathString.append(" L").append(pointForEdge.getX()- size/2).append(" ").append(pointForEdge.getY()-size/2);
					pathString.append(" L").append(pointForEdge.getX()).append(" ").append(pointForEdge.getY() + size/2);
				} else if (PortRegion.EAST.equals(directionForEdge)){
					transform.append("(270,").append(pointForEdge.getX() + size / 2).append(", ").append(pointForEdge.getY()).append(")");
					pathString.append(pointForEdge.getX()).append(" ").append(pointForEdge.getY() + size/2);
					pathString.append(" L").append(pointForEdge.getX()+ size/2).append(" ").append(pointForEdge.getY()-size/2);
					pathString.append(" L").append(pointForEdge.getX() +size).append(" ").append(pointForEdge.getY() + size/2);
				} else if (PortRegion.SOUTH.equals(directionForEdge)){
					transform.append("(0,").append(pointForEdge.getX()).append(", ").append(pointForEdge.getY()).append(")");
					pathString.append(pointForEdge.getX() - size/2).append(" ").append(pointForEdge.getY() + size);
					pathString.append(" L").append(pointForEdge.getX()).append(" ").append(pointForEdge.getY());
					pathString.append(" L").append(pointForEdge.getX() + size/2).append(" ").append(pointForEdge.getY() + size);
				} else {
					transform.append("(180,").append(pointForEdge.getX()).append(", ").append(pointForEdge.getY() - size / 2).append(")");
					pathString.append(pointForEdge.getX() - size/2).append(" ").append(pointForEdge.getY());
					pathString.append(" L").append(pointForEdge.getX()).append(" ").append(pointForEdge.getY()-size);
					pathString.append(" L").append(pointForEdge.getX() + size/2).append(" ").append(pointForEdge.getY());
				}
				decor.setAttribute(SvgConstant.ATTRIBUTE_D, pathString.toString());
				decor.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
				decor.setAttribute(SvgConstant.ATTRIBUTE_FILL, "none");
				decor.setAttribute(SvgConstant.ATTRIBUTE_TRANSFORM, transform.toString());
				decor.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
				xmlHandler.addXmlElement(group, decor);
			}
			break;

			case FULL_TRIANGLE: {
				final double size = 16;
				decor = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_PATH);
				StringBuilder pathString = new StringBuilder("M");
				StringBuilder transform = new StringBuilder("rotate");
				if(PortRegion.WEST.equals(directionForEdge)){
					transform.append("(90,").append(pointForEdge.getX() - size / 2).append(", ").append(pointForEdge.getY()).append(")");
					pathString.append(pointForEdge.getX() - size).append(" ").append(pointForEdge.getY() + size/2);
					pathString.append(" L").append(pointForEdge.getX()- size/2).append(" ").append(pointForEdge.getY()-size/2);
					pathString.append(" L").append(pointForEdge.getX()).append(" ").append(pointForEdge.getY() + size/2).append(" z");
				} else if (PortRegion.EAST.equals(directionForEdge)){
					transform.append("(270,").append(pointForEdge.getX() + size / 2).append(", ").append(pointForEdge.getY()).append(")");
					pathString.append(pointForEdge.getX()).append(" ").append(pointForEdge.getY() + size/2);
					pathString.append(" L").append(pointForEdge.getX()+ size/2).append(" ").append(pointForEdge.getY()-size/2);
					pathString.append(" L").append(pointForEdge.getX() +size).append(" ").append(pointForEdge.getY() + size/2).append(" z");
				} else if (PortRegion.SOUTH.equals(directionForEdge)){
					transform.append("(0,").append(pointForEdge.getX()).append(", ").append(pointForEdge.getY()).append(")");
					pathString.append(pointForEdge.getX() - size/2).append(" ").append(pointForEdge.getY() + size);
					pathString.append(" L").append(pointForEdge.getX()).append(" ").append(pointForEdge.getY());
					pathString.append(" L").append(pointForEdge.getX() + size/2).append(" ").append(pointForEdge.getY() + size).append(" z");
				} else {
					transform.append("(180,").append(pointForEdge.getX()).append(", ").append(pointForEdge.getY() - size / 2).append(")");
					pathString.append(pointForEdge.getX() - size/2).append(" ").append(pointForEdge.getY());
					pathString.append(" L").append(pointForEdge.getX()).append(" ").append(pointForEdge.getY()-size);
					pathString.append(" L").append(pointForEdge.getX() + size/2).append(" ").append(pointForEdge.getY()).append(" z");
				}
				decor.setAttribute(SvgConstant.ATTRIBUTE_D, pathString.toString());
				decor.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
				decor.setAttribute(SvgConstant.ATTRIBUTE_FILL, "white");
				decor.setAttribute(SvgConstant.ATTRIBUTE_TRANSFORM, transform.toString());
				decor.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, getSvgStrokeWidth());
				xmlHandler.addXmlElement(group, decor);
			}break;

			case CIRCLE: {
				double size = 7;
				String color = Color.WHITE.toString().split("x")[1].substring(0,6);
				decor =  xmlHandler.createXmlElement(SvgConstant.TAG_NAME_CIRCLE);
				if(PortRegion.EAST.equals(directionForEdge)){
					decor.setAttribute(SvgConstant.ATTRIBUTE_CX, (pointForEdge.getX()+size)+"");
					decor.setAttribute(SvgConstant.ATTRIBUTE_CY, (pointForEdge.getY())+"");
				} else if (PortRegion.WEST.equals(directionForEdge)){
					decor.setAttribute(SvgConstant.ATTRIBUTE_CX, (pointForEdge.getX()-size)+"");
					decor.setAttribute(SvgConstant.ATTRIBUTE_CY, (pointForEdge.getY())+"");
				} else if (PortRegion.NORTH.equals(directionForEdge)){
					decor.setAttribute(SvgConstant.ATTRIBUTE_CX, (pointForEdge.getX())+"");
					decor.setAttribute(SvgConstant.ATTRIBUTE_CY, (pointForEdge.getY()-size)+"");
				} else {
					decor.setAttribute(SvgConstant.ATTRIBUTE_CX, (pointForEdge.getX())+"");
					decor.setAttribute(SvgConstant.ATTRIBUTE_CY, (pointForEdge.getY()+size)+"");
				}
				decor.setAttribute(SvgConstant.ATTRIBUTE_R, size+"");
				decor.setAttribute(SvgConstant.ATTRIBUTE_FILL, "#"+color);
				decor.setAttribute(SvgConstant.ATTRIBUTE_STROKE, "#"+strokeColor);
				decor.setAttribute(SvgConstant.ATTRIBUTE_STROKE_WIDTH, "1");
				xmlHandler.addXmlElement(group, decor);
			}
			break;
			default:
				break;

		}

	}
}
