package tool.clients.diagrams;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import tool.clients.diagrams.Edge.HeadStyle;
import tool.clients.diagrams.Edge.Position;

public class EdgePainter {
	
//	@Deprecated
	public static class Point {
		public int x; public int y;
		public Point(int x, int y) {
			this.x=x;
			this.y=y;
		}
	}
	
	// An empty vector to be used where a list of intersections is required but there is no need in that case
	private static final Vector<Point> NO_INTERSECTIONS_PLACEHOLDER = new Vector<Point>();

	public static int                  ARROW_ANGLE     = 35;
	public static int                  ARROW_HEAD      = 10;
	public static int                  LINE_WIDTH      = 1;
	public static int                  CIRCLE_SIZE     = 7;
	private final Edge edge;

	public EdgePainter(Edge edge) {
		this.edge = edge;
	}

	//                                        | intersect         | second/penultimate  |
	private void drawCircle(GraphicsContext gc, int tipx, int tipy, int tailx, int taily, boolean filled, Color fill, Color line) {
		
	    double dy = tipy - taily;
	    double dx = tipx - tailx;
	    double theta = Math.atan2(dy, dx);
	    
//	    // store old values
//	    Color oldFG = gc.getForeground();
//	    Color oldBG = gc.getBackground();	    
//	    int width = gc.getLineWidth();
	    
	    // set new values for temporary use
	    gc.setFill(fill);
	    gc.setStroke(line);
	    
//	    gc.setForeground(line);	    
//	    gc.setBackground(fill);	
	    gc.setLineWidth(1);//LINE_WIDTH + 2);

	    // calculate
	    int x, y;
	    if(edgeIntersectsTopOrBottom(tipx, tipy)) {
	    	if(theta > 0) { // TOP
	    		y = tipy - CIRCLE_SIZE - 1;
	    		x = tipx - (int)(CIRCLE_SIZE/2./Math.tan(theta) + CIRCLE_SIZE/2.);
	    	} else { // BOTTOM
	    		y = tipy;
	    		x = tipx + (int)(CIRCLE_SIZE/2./Math.tan(theta) - CIRCLE_SIZE/2.);
	    	}
	    } else {
	    	if(theta < Math.PI/2 && theta > -Math.PI/2) { // LEFT
	    		x = tipx - CIRCLE_SIZE - 1;
	    		y = tipy - (int)(CIRCLE_SIZE/2.*Math.tan(theta) + CIRCLE_SIZE/2.);
	    	} else { // RIGHT
	    		x = tipx;
	    		y = tipy + (int)(CIRCLE_SIZE/2.*Math.tan(theta) - CIRCLE_SIZE/2.);
	    	}
	    }
	    
	    // draw
	    gc.strokeOval(x, y, CIRCLE_SIZE, CIRCLE_SIZE);
	    gc.fillOval(x+1, y+1, CIRCLE_SIZE-1, CIRCLE_SIZE-1);

	    
//	    //reset old Values
//	    gc.setLineWidth(width);	    
//	    gc.setForeground(oldFG);
//	    gc.setBackground(oldBG);
	}
	
	/* Tries to figure out if the line goes out through the upper or lower side of the box.
	 * We don't know which node, so we try all.
	 **/
	private boolean edgeIntersectsTopOrBottom(int intersectX, int intersectY) {
		if(edge.targetNode.getY() == intersectY && edge.targetNode.getX() <= intersectX && edge.targetNode.maxX() >= intersectX) return true;
		if(edge.targetNode.maxY() == intersectY && edge.targetNode.getX() <= intersectX && edge.targetNode.maxX() >= intersectX) return true;
		if(edge.sourceNode.getY() == intersectY && edge.sourceNode.getX() <= intersectX && edge.sourceNode.maxX() >= intersectX) return true;
		if(edge.sourceNode.maxY() == intersectY && edge.sourceNode.getX() <= intersectX && edge.sourceNode.maxX() >= intersectX) return true;
		return false;
	}

	private void drawArrow(GraphicsContext gc, int tipx, int tipy, int tailx, int taily, boolean filled, Color fill, Color line) {
	    double phi = Math.toRadians(ARROW_ANGLE);
	    double dy = tipy - taily;
	    double dx = tipx - tailx;
	    double theta = Math.atan2(dy, dx);
	    double x, y, rho = theta + phi;
	    
//	    Color oldFG = gc.getForeground();
//	    Color oldBG = gc.getBackground();
	    
	    gc.setStroke(line);
	    if (!filled) {
	      for (int j = 0; j < 2; j++) {
	        x = tipx - ARROW_HEAD * Math.cos(rho);
	        y = tipy - ARROW_HEAD * Math.sin(rho);
	        gc.strokeLine(tipx, tipy, (int) x, (int) y);
	        rho = theta - phi;
	      }
	    } else {
//	      gc.setLineCap(SWT.CAP_ROUND);
//	      int width = gc.getLineWidth();
	      gc.setLineWidth(LINE_WIDTH + 2);
	      double[] xpoints = new double[3];
	      double[] ypoints = new double[3];
	      xpoints[0] = tipx;
	      ypoints[0] = tipy;
	      for (int j = 0; j < 2; j++) {
	        xpoints[j+1] = (int) (tipx - ARROW_HEAD * Math.cos(rho));
	        ypoints[j+1] = (int) (tipy - ARROW_HEAD * Math.sin(rho));
	        gc.strokeLine(tipx, tipy, xpoints[j+1], ypoints[j+1]);
	        rho = theta - phi;
	      }
	      gc.strokeLine(xpoints[1], ypoints[1], xpoints[2], ypoints[2]);
	      gc.setFill(fill);
	      gc.fillPolygon(xpoints, ypoints, 3);
//	      gc.setLineWidth(width);
	    }
	    
//	    gc.setForeground(oldFG);
//	    gc.setBackground(oldBG);
	  }

	  private void drawSourceDecoration(GraphicsContext gc, Color color, int x, int y, int x2, int y2) {
	    switch (edge.sourceHead) {
	    case NO_ARROW:
	      break;
	    case ARROW:
	      drawArrow(gc, x, y, x2, y2, false, null, color);
	      break;
	    case WHITE_ARROW:
	      drawArrow(gc, x, y, x2, y2, true, Color.WHITE, color);
	      break;
	    case WHITE_CIRCLE:
	      drawCircle(gc, x, y, x2, y2, true, Color.WHITE, color);
	      break;
	    default:
	      System.err.println("unknown type of source decoration: " + edge.sourceHead);
	    }
	  }

	  private void drawTargetDecoration(GraphicsContext gc, Color color, int x, int y, int x2, int y2) {
	    switch (edge.targetHead) {
	    case NO_ARROW:
	      break;
	    case ARROW:
	      drawArrow(gc, x, y, x2, y2, false, null, color);
	      break;
	    case WHITE_ARROW:
	      drawArrow(gc, x, y, x2, y2, true, Color.WHITE, color);
	      break;
	    case WHITE_CIRCLE:
	      drawCircle(gc, x, y, x2, y2, true, Color.WHITE, color);
	      break;
	    default:
	      System.err.println("unknown type of target decoration: " + edge.targetHead);
	    }
	  }  
	  
	  public void paint(GraphicsContext gc, Color color, boolean showWaypoints, Vector<Point> intersections, int xOffset, int yOffset) {
	    if (!edge.hidden) {
	      int x = edge.waypoints.elementAt(0).getX();
	      int y = edge.waypoints.elementAt(0).getY();
//	      int width = gc.getLineWidth();
	      gc.setLineWidth(LINE_WIDTH);
//	      Color c = gc.getBackground();
	      gc.setFill(color);
	      for (int i = 1; i < edge.waypoints.size(); i++) {
	        final Waypoint wp0 = edge.waypoints.elementAt(i - 1);
	        final Waypoint wp1 = edge.waypoints.elementAt(i);
	        Vector<Point> p = getIntersection(wp0, wp1, intersections);
	        Collections.sort(p, new Comparator<Point>() {
	          public int compare(Point o1, Point o2) {
	            Point p1 = (Point) o1;
	            Point p2 = (Point) o2;
	            boolean cmpx = wp0.x <= wp1.x ? p1.x <= p2.x : p2.x <= p1.x;
	            boolean cmpy = wp0.y <= wp1.y ? p1.y <= p2.y : p2.y <= p1.y;
	            return cmpx && cmpy ? -1 : 1;
	          }
	        });
	        paintLine(gc, x + xOffset, y + yOffset, wp1.getX() + xOffset, wp1.getY() + yOffset, color, p);
	        if (showWaypoints && i < edge.waypoints.size() - 1) gc.fillOval(wp1.getX() - 3 + xOffset, wp1.getY() - 3 + yOffset, 6, 6);
	        x = wp1.getX();
	        y = wp1.getY();
	      }
//	      gc.setBackground(c);
	      for (Label label : edge.labels)
	        label.paint(gc, xOffset, yOffset);
	      paintDecorations(gc, color, xOffset, yOffset);
//	      gc.setLineWidth(width);
	    }
	  }

	  public void paintAligned(GraphicsContext gc) {
	    if (!edge.hidden) {
	      int x = edge.waypoints.elementAt(0).getX();
	      int y = edge.waypoints.elementAt(0).getY();
//	      int width = gc.getLineWidth();
	      gc.setLineWidth(LINE_WIDTH + 1);
	      for (int i = 1; i < edge.waypoints.size(); i++) {
	        Waypoint wp = edge.waypoints.elementAt(i);
	        paintLine(gc, x, y, wp.getX(), wp.getY(), Color.RED, NO_INTERSECTIONS_PLACEHOLDER);
	        if (i < edge.waypoints.size() - 1) gc.fillOval(wp.getX() - 3, wp.getY() - 3, 6, 6);
	        x = wp.getX();
	        y = wp.getY();
	      }
//	      gc.setLineWidth(width);
	    }
	  }

	  private void paintDecorations(GraphicsContext gc, Color color, int xOffset, int yOffset) { 
	    if (isSelfEdge())
	      paintHomogeneousEdgeDecorations(gc, color, xOffset, yOffset);
	    else paintHeterogeneousEdgeDecorations(gc, color, xOffset, yOffset);
	  }

	  private boolean isSelfEdge() {
	    return edge.sourceNode == edge.targetNode;
	  }

	  private void paintHeterogeneousEdgeDecorations(GraphicsContext gc, Color color, int xOffset, int yOffset) {
	    Point topIntercept = edge.intercept(edge.targetNode, Position.TOP, false);
	    if (topIntercept != null && topIntercept.x >= 0 && topIntercept.y >= 0) drawTargetDecoration(gc, color, topIntercept.x + xOffset, topIntercept.y + yOffset, edge.penultimate().x + xOffset, edge.penultimate().y + yOffset);
	    Point bottomIntercept = edge.intercept(edge.sourceNode, Position.BOTTOM, true);
	    if (bottomIntercept != null && bottomIntercept.x >= 0 && bottomIntercept.y >= 0) drawSourceDecoration(gc, color, bottomIntercept.x + xOffset, bottomIntercept.y + yOffset, edge.second().x + xOffset, edge.second().y + yOffset);
	    topIntercept = edge.intercept(edge.sourceNode, Position.TOP, true);
	    if (topIntercept != null && topIntercept.x >= 0 && topIntercept.y >= 0) drawSourceDecoration(gc, color, topIntercept.x + xOffset, topIntercept.y + yOffset, edge.second().x + xOffset, edge.second().y + yOffset);
	    bottomIntercept = edge.intercept(edge.targetNode, Position.BOTTOM, false);
	    if (bottomIntercept != null && bottomIntercept.x >= 0 && bottomIntercept.y >= 0) drawTargetDecoration(gc, color, bottomIntercept.x + xOffset, bottomIntercept.y + yOffset, edge.penultimate().x + xOffset, edge.penultimate().y + yOffset);
	    Point leftIntercept = edge.intercept(edge.sourceNode, Position.LEFT, true);
	    if (leftIntercept != null && leftIntercept.x >= 0 && leftIntercept.y >= 0) drawSourceDecoration(gc, color, leftIntercept.x + xOffset, leftIntercept.y + yOffset, edge.second().x + xOffset, edge.second().y + yOffset);
	    Point rightIntercept = edge.intercept(edge.targetNode, Position.RIGHT, false);
	    if (rightIntercept != null && rightIntercept.x >= 0 && rightIntercept.y >= 0) drawTargetDecoration(gc, color, rightIntercept.x + xOffset, rightIntercept.y + yOffset, edge.penultimate().x + xOffset, edge.penultimate().y + yOffset);
	    leftIntercept = edge.intercept(edge.targetNode, Position.LEFT, false);
	    if (leftIntercept != null && leftIntercept.x >= 0 && leftIntercept.y >= 0) drawTargetDecoration(gc, color, leftIntercept.x + xOffset, leftIntercept.y + yOffset, edge.penultimate().x + xOffset, edge.penultimate().y + yOffset);
	    rightIntercept = edge.intercept(edge.sourceNode, Position.RIGHT, true);
	    if (rightIntercept != null && rightIntercept.x >= 0 && rightIntercept.y >= 0) drawSourceDecoration(gc, color, rightIntercept.x + xOffset, rightIntercept.y + yOffset, edge.second().x + xOffset, edge.second().y + yOffset);
	  }

	  /*NEW PRIVATE*/private void paintHomogeneousEdgeDecorations(GraphicsContext gc, Color color, int xOffset, int yOffset) {
	    // Ensure that the correct waypoint is used when calculating the intercepts...
	    Point topIntercept = edge.targetHead == HeadStyle.NO_ARROW ? null : edge.intercept(edge.targetNode, edge.end(), edge.penultimate(), Position.TOP);
	    if (topIntercept != null && topIntercept.x >= 0 && topIntercept.y >= 0) drawTargetDecoration(gc, color, topIntercept.x + xOffset, topIntercept.y + yOffset, edge.penultimate().x + xOffset, edge.penultimate().y + yOffset);
	    Point bottomIntercept = edge.sourceHead == HeadStyle.NO_ARROW ? null : edge.intercept(edge.sourceNode, edge.start(), edge.second(), Position.BOTTOM);
	    if (bottomIntercept != null && bottomIntercept.x >= 0 && bottomIntercept.y >= 0) drawSourceDecoration(gc, color, bottomIntercept.x + xOffset, bottomIntercept.y + yOffset, edge.second().x + xOffset, edge.second().y + yOffset);
	    topIntercept = edge.sourceHead == HeadStyle.NO_ARROW ? null : edge.intercept(edge.sourceNode, edge.start(), edge.second(), Position.TOP);
	    if (topIntercept != null && topIntercept.x >= 0 && topIntercept.y >= 0) drawSourceDecoration(gc, color, topIntercept.x + xOffset, topIntercept.y + yOffset, edge.second().x + xOffset, edge.second().y + yOffset);
	    bottomIntercept = edge.targetHead == HeadStyle.NO_ARROW ? null : edge.intercept(edge.targetNode, edge.end(), edge.penultimate(), Position.BOTTOM);
	    if (bottomIntercept != null && bottomIntercept.x >= 0 && bottomIntercept.y >= 0) drawTargetDecoration(gc, color, bottomIntercept.x + xOffset, bottomIntercept.y + yOffset, edge.penultimate().x + xOffset, edge.penultimate().y + yOffset);
	    Point leftIntercept = edge.sourceHead == HeadStyle.NO_ARROW ? null : edge.intercept(edge.sourceNode, edge.start(), edge.second(), Position.LEFT);
	    if (leftIntercept != null && leftIntercept.x >= 0 && leftIntercept.y >= 0) drawSourceDecoration(gc, color, leftIntercept.x + xOffset, leftIntercept.y + yOffset, edge.second().x + xOffset, edge.second().y + yOffset);
	    Point rightIntercept = edge.targetHead == HeadStyle.NO_ARROW ? null : edge.intercept(edge.targetNode, edge.end(), edge.penultimate(), Position.RIGHT);
	    if (rightIntercept != null && rightIntercept.x >= 0 && rightIntercept.y >= 0) drawTargetDecoration(gc, color, rightIntercept.x + xOffset, rightIntercept.y + yOffset, edge.penultimate().x + xOffset, edge.penultimate().y + yOffset);
	    leftIntercept = edge.targetHead == HeadStyle.NO_ARROW ? null : edge.intercept(edge.targetNode, edge.end(), edge.penultimate(), Position.LEFT);
	    if (leftIntercept != null && leftIntercept.x >= 0 && leftIntercept.y >= 0) drawTargetDecoration(gc, color, leftIntercept.x + xOffset, leftIntercept.y + yOffset, edge.penultimate().x + xOffset, edge.penultimate().y + yOffset);
	    rightIntercept = edge.sourceHead == HeadStyle.NO_ARROW ? null : edge.intercept(edge.sourceNode, edge.start(), edge.second(), Position.RIGHT);
	    if (rightIntercept != null && rightIntercept.x >= 0 && rightIntercept.y >= 0) drawSourceDecoration(gc, color, rightIntercept.x + xOffset, rightIntercept.y + yOffset, edge.second().x + xOffset, edge.second().y + yOffset);
	  }

	  public void paintHover(GraphicsContext gc, int x, int y) {
	    for (Label label : edge.labels)
	      label.paintHover(gc, x, y);
	  }

	  public void paintLine(GraphicsContext gc, int x1, int y1, int x2, int y2, Color lineColor, Vector<Point> intersect) {

	    // Paint the line in the line style.
//	    int style = gc.getLineStyle();
//	    Color c = gc.getForeground();
	    gc.setStroke(lineColor);
	    switch (edge.lineStyle) {
	    case Line.DASH_LINE:
	      gc.setLineDashes(12., 3.);
//	      gc.setLineStyle(SWT.LINE_DASH);
	      break;
	    case Line.DOTTED_LINE:
		  gc.setLineDashes(3., 3.);
//	      gc.setLineStyle(SWT.LINE_DOT);
	      break;
	    case Line.DASH_DOTTED_LINE:
          gc.setLineDashes(12., 3., 3., 3.);
//	      gc.setLineStyle(SWT.LINE_DASHDOT);
	      break;
	    case Line.DASH_DOT_DOT_LINE:
	      gc.setLineDashes(12., 3., 3., 3., 3., 3.);
//	      gc.setLineStyle(SWT.LINE_DASHDOTDOT);
	      break;
	    case Line.SOLID_LINE:
	    default:
	      gc.setLineDashes(null);
//	      gc.setLineStyle(SWT.LINE_SOLID);
	    }
	    for (Point p : intersect) {
	      Point p1 = Edge.circleIntersect(p.x, p.y, 3.0, x1, y1);
	      Point p2 = Edge.circleIntersect(p.x, p.y, 3.0, x2, y2);
	      gc.strokeLine(x1, y1, p1.x, p1.y);
	      x1 = p2.x;
	      y1 = p2.y;
	    }
	    gc.strokeLine(x1, y1, x2, y2);
//	    gc.setLineStyle(style);
//	    gc.setForeground(c);
	  }

	  public void paintMovingSourceOrTarget(GraphicsContext gc, int startX, int startY, int endX, int endY, int xOffset, int yOffset) {
	    int x = startX;
	    int y = startY;
//	    int width = gc.getLineWidth();
	    gc.setLineWidth(LINE_WIDTH);
//	    Color c = gc.getBackground();
	    gc.setFill(Color.BLACK);
	    for (int i = 1; i < edge.waypoints.size() - 1; i++) {
	      Waypoint wp = edge.waypoints.elementAt(i);
	      paintLine(gc, x + xOffset, y + yOffset, wp.getX() + xOffset, wp.getY() + yOffset, Color.BLACK, NO_INTERSECTIONS_PLACEHOLDER);
	      if (i < edge.waypoints.size() - 1) gc.fillOval(wp.getX() - 3 + xOffset, wp.getY() - 3 + yOffset, 6, 6);
	      x = wp.getX();
	      y = wp.getY();
	    }
	    paintLine(gc, x + xOffset, y + yOffset, endX + xOffset, endY + yOffset, Color.BLACK, NO_INTERSECTIONS_PLACEHOLDER);
//	    gc.setBackground(c);
	    for (Label label : edge.labels)
	      label.paint(gc, xOffset, yOffset);
	    drawSourceDecoration(gc, Color.BLACK, startX + xOffset, startY + yOffset, edge.waypoints.elementAt(1).getX() + xOffset, edge.waypoints.elementAt(1).getY() + yOffset);
	    drawTargetDecoration(gc, Color.BLACK, endX + xOffset, endY + yOffset, edge.waypoints.elementAt(edge.waypoints.size() - 2).getX() + xOffset, edge.waypoints.elementAt(edge.waypoints.size() - 2).getY() + yOffset);
//	    gc.setLineWidth(width);
	  }

	  public void paintOrthogonal(GraphicsContext gc, Waypoint waypoint) { // Zielscheibe
	    if (waypoint != edge.start() && waypoint != edge.end()) {
	      int index = edge.waypoints.indexOf(waypoint);
	      int length = 30;
	      Waypoint pre = edge.waypoints.elementAt(index - 1);
	      Waypoint post = edge.waypoints.elementAt(index + 1);
//	      Color c = gc.getForeground();
	      gc.setStroke(Color.RED);
	      if (pre.getX() == waypoint.getX() || post.getX() == waypoint.getX()) {
	        gc.strokeOval(waypoint.getX() - length / 2, waypoint.getY() - length / 2, length, length);
	        gc.strokeLine(waypoint.getX(), waypoint.getY() - length, waypoint.getX(), waypoint.getY() + length);
	      }
	      if (pre.getY() == waypoint.getY() || post.getY() == waypoint.getY()) {
	        gc.strokeOval(waypoint.getX() - length / 2, waypoint.getY() - length / 2, length, length);
	        gc.strokeLine(waypoint.getX() - length, waypoint.getY(), waypoint.getX() + length, waypoint.getY());
	      }
//	      gc.setForeground(c);
	    }
	  }

	  public void paintSourceMoving(GraphicsContext gc, int x, int y, int xOffset, int yOffset) {
	    paintMovingSourceOrTarget(gc, x, y, edge.end().getX(), edge.end().getY(), xOffset, yOffset);
	  }

	  public void paintTargetMoving(GraphicsContext gc, int x, int y, int xOffset, int yOffset) {
	    paintMovingSourceOrTarget(gc, edge.start().getX(), edge.start().getY(), x, y, xOffset, yOffset);
	  }
	  
	  private Vector<Point> getIntersection(Waypoint wp0, Waypoint wp1, Vector<Point> intersections) {
	    Vector<Point> i = new Vector<Point>();
	    for (Point p : intersections) {
	      double d = Edge.pointToLineDistance(wp0, wp1, p.x, p.y);
	      if (d < 1) {
	        i.add(p);
	      }
	    }
	    return i;
	  }
}
