package tool.clients.diagrams;

import java.io.PrintStream;
import java.util.Vector;

import org.eclipse.swt.graphics.GC;

import javafx.scene.canvas.GraphicsContext;
import xos.Message;
import xos.Value;

public class Edge {
  public static int                  WAYPOINT_ALIGN  = 20;
  
  private static WaypointStyle waypointStyle = WaypointStyle.FREE;
  
  public enum HeadStyle {NO_ARROW(0), ARROW(1), BLACK_DIAMOND(2), WHITE_DIAMOND(3), BLACK_ARROW(4), WHITE_ARROW(5), BLACK_CIRCLE(6), WHITE_CIRCLE(7); 
	  int id; 
	  private HeadStyle(int id) {this.id = id;}
	  private int getID() {return id;}
	  private static HeadStyle getHeadStyle(int id) {
		  for(HeadStyle headStyle : HeadStyle.values()) if(headStyle.id == id) return headStyle;
		  throw new IllegalArgumentException("HeadStyle id " + id + " not in use!");
	  }
  }
  
  enum WaypointStyle {FREE, SQUARED, CURVED};

  Vector<Waypoint>                   waypoints       = new Vector<Waypoint>();
  Vector<Label>                      labels          = new Vector<Label>();
  boolean                            hidden          = false;
  String                             id;
  Node                               sourceNode;
  Port                               sourcePort;
  Node                               targetNode;
  Port                               targetPort;
//  int                                refx;
//  int                                refy;
  HeadStyle                          sourceHead;
  HeadStyle                          targetHead;
  int                                lineStyle;
  int                                red;
  int                                green;
  int                                blue;
	  
  public int getRed() {
	return red;
}

public int getGreen() {
	return green;
}

public int getBlue() {
	return blue;
}

/*PACKAGE ACCESS*/ static EdgePainter.Point circleIntersect(int centerx, int centery, double radius, int x, int y) {
    double dx = x - centerx;
    double dy = y - centery;
    double lineLength = Math.sqrt((dx * dx) + (dy * dy));
    dx = dx / lineLength;
    dy = dy / lineLength;
    EdgePainter.Point p = new EdgePainter.Point((int) (centerx + (dx * radius)), (int) (centery + (dy * radius)));
    return p;
  }
  
  private static boolean isOnLine(int x, int y, int x1, int y1, int x2, int y2) {
    boolean inX = x1 < x2 ? (x >= x1 && x <= x2) : (x >= x2 && x <= x1);
    boolean inY = y1 < y2 ? (y >= y1 && y <= y2) : (y >= y2 && y <= y1);
    return inX && inY;
  }

  /*PACKAGE ACCESS*/ static double pointToLineDistance(Waypoint A, Waypoint B, int x, int y) {
    double normalLength = Math.sqrt((B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y));
    return Math.abs((x - A.x) * (B.y - A.y) - (y - A.y) * (B.x - A.x)) / normalLength;
  }

  /*PACKAGE ACCESS*/ Edge(String id, 
		  Node sourceNode, Port sourcePort, 
		  int sourceX, int sourceY, 
		  Node targetNode, Port targetPort, 
		  int targetX, int targetY, 
		  int refx, int refy, 
		  int sourceHead, int targetHead, 
		  int lineStyle, 
		  int red, int green, int blue) {
    super();
    this.id = id;
    this.sourceNode = sourceNode;
    this.sourcePort = sourcePort;
    this.targetNode = targetNode;
    this.targetPort = targetPort;
//    this.refx = refx;
//    this.refy = refy;
    this.sourceHead = HeadStyle.getHeadStyle(sourceHead);
    this.targetHead = HeadStyle.getHeadStyle(targetHead);
    this.lineStyle = lineStyle;
    this.red = red;
    this.green = green;
    this.blue = blue;
    sourcePort.addSource(this);
    targetPort.addTarget(this);
    waypoints.add(new Waypoint("start", this, sourceX, sourceY));
    waypoints.add(new Waypoint("end", this, targetX, targetY));
    Diagram.dontSelectNextWaypoint = true;
    if(waypointStyle == WaypointStyle.SQUARED) {
    	 newWaypointEvent(1, sourceX, targetY, false);
    }
  }

  /*PACKAGE ACCESS*/ void addLabel(String id, String text, String pos, int x, int y, boolean editable, boolean underline, boolean condense, int red, int green, int blue, boolean border,  int borderRed, int borderGreen, int borderBlue, String font, int arrow, boolean hidden, boolean fill) {
//    System.err.println("addLabel: x= " + x + " y=" + y + " text = >" + text + "<");
	  labels.add(new Label(this, id, text, pos, x, y, editable, underline, condense, red, green, blue, border, borderRed, borderGreen, borderBlue, font, arrow, hidden, fill));
  }

  /** aligns the start and end waypoint to 90° lines if possible 
  /*PACKAGE ACCESS*/ void align() {
    // Called when there is some jiggling to be done.
//    if (waypoints.size() == 2) {
//      alignStart(end());
//      alignEnd(start());
//    } else {
      alignStart(waypoints.elementAt(1));
      alignEnd(waypoints.elementAt(waypoints.size() - 2));
//    }
  }

  /**
   * When a (visible) waypoint is moved, the start and end waypoints
   * will be moved as possible to have straight (90°) lines.
   */
  /*PACKAGE ACCESS*/ void movedBy(Waypoint justMoved) {
    if (justMoved != start() && justMoved != end()) {
      int index = waypoints.indexOf(justMoved);
      Waypoint w1 = waypoints.elementAt(index - 1);
      Waypoint w2 = waypoints.elementAt(index + 1);
      if (w1 == start()) alignStart(justMoved);
      if (w2 == end()) alignEnd(justMoved);
    }
  }
  
  /**
   * Aligns the last waypoint (hidden under the source node) 
   * so that it connects to the next waypoint in a horizontal 
   * or vertical way if that is possible. The other coordinate 
   * is not changed. No changes at all are made otherwise 
   * (i.e. the next waypoint's x/y cordinates are both outside the box's). 
   */
  private void alignEnd(Waypoint w) {
    int portx = targetNode.getX() + targetPort.getX();
    int porty = targetNode.getY() + targetPort.getY();
    int width = targetPort.getWidth();
    int height = targetPort.getHeight();
    if (w.getX() >= portx && w.getX() <= portx + width) end().setX(w.getX());
    if (w.getY() >= porty && w.getY() <= porty + height) end().setY(w.getY());
  }

  /** Same as <code>alignEnd(Waypoint w)</code>, 
   * but for the last waypoint (hidden under the target node)*/
  private void alignStart(Waypoint w) {
    int portx = sourceNode.getX() + sourcePort.getX();
    int porty = sourceNode.getY() + sourcePort.getY();
    int width = sourcePort.getWidth();
    int height = sourcePort.getHeight();
    if (w.getX() >= portx && w.getX() <= portx + width) start().setX(w.getX());
    if (w.getY() >= porty && w.getY() <= porty + height) start().setY(w.getY());
  }

  /**
   * Removes the justMoved waypoint if it is too close to another one.
   */
  /*PACKAGE ACCESS*/ void checkWaypointsForRedundancy(Waypoint justMoved) {
    if (getWaypoints().size() > 2 && justMoved != start() && justMoved != end()) {
      int index = waypoints.indexOf(justMoved);
      Waypoint w1 = waypoints.elementAt(index - 1);
      Waypoint w2 = waypoints.elementAt(index + 1);
      if (pointToLineDistance(w1, w2, justMoved.x, justMoved.y) < 5) {
        Message message = DiagramClient.theClient().getHandler().newMessage("deleteWaypoint", 1);
        message.args[0] = new Value(justMoved.getId());
        DiagramClient.theClient().getHandler().raiseEvent(message);
      }
    }
  }

  /**
   * Adds a new waypoint if the edge is squared and it becomes necessary
   */
    void checkWaypointsForNewRequiredPoints() {
		System.err.println("checkWaypointsForNewRequiredPoints");
		if (waypointStyle != WaypointStyle.SQUARED)
			return;
		Waypoint first = start();
		Waypoint second = second();
		Waypoint third = waypoints.size() >= 3 ? waypoints.get(2) : null;
		if (first.x != second.x && first.y != second.y) {
			// Now the points are connected by a diagonal line. A new Waypoint is necessary
			// if second and third have the same x then second and the new one have the same y
			if (third == null || second.x == third.x) {
				newWaypointEvent(1, first.x, second.y, false);
			} else {
				newWaypointEvent(1, first.y, second.x, false);
			}
		}
  }
  
  /*PACKAGE ACCESS*/ Label getLabel(String id) {
    for (Label label : labels)
      if (label.getId().equals(id)) return label;
    return null;
  }

  /*PACKAGE ACCESS*/ Vector<Label> getLabels() {
    return labels;
  }

  /*PACKAGE ACCESS*/ Waypoint getWaypoint(String id) {
    for (Waypoint waypoint : waypoints)
      if (waypoint.getId().equals(id)) return waypoint;
    return null;
  }

  /*PACKAGE ACCESS*/ Vector<Waypoint> getWaypoints() {
    return waypoints;
  }

//  /*PACKAGE ACCESS*/ boolean isHidden() {
//    return hidden;
//  }


  /*PACKAGE ACCESS*/ int maxX() {
    int maxX = 0;
    for (Waypoint w : waypoints)
      maxX = Math.max(maxX, w.getX());
    for (Label label : labels)
      maxX = Math.max(maxX, label.maxX());
    return maxX;
  }

  /*PACKAGE ACCESS*/ int maxY() {
    int maxY = 0;
    for (Waypoint w : waypoints)
      maxY = Math.max(maxY, w.getY());
    for (Label label : labels)
      maxY = Math.max(maxY, label.maxY());
    return maxY;
  }

  /*PACKAGE ACCESS*/ void move(String id, int x, int y) {
    for (Label label : labels) {
      label.move(id, x, y);
    }
    for (Waypoint waypoint : waypoints) {
      waypoint.move(id, x, y);
    }
    if (id.equals(getId())) checkWaypointsForNewRequiredPoints();
  }

  /*PACKAGE ACCESS*/ void moveSourceBy(int dx, int dy) {
    waypoints.elementAt(0).moveBy(dx, dy);
  }

  /*PACKAGE ACCESS*/ void moveTargetBy(int dx, int dy) {
    waypoints.elementAt(waypoints.size() - 1).moveBy(dx, dy);
  }

  /*PACKAGE ACCESS*/ boolean newWaypoint(int x, int y) { // outbound?
//    System.err.println("new Waypoint 1");
	Diagram.dontSelectNextWaypoint = false;
    if(waypointStyle == WaypointStyle.SQUARED) return false;
    for (int i = 0; i < waypoints.size() - 1; i++) {
      Waypoint w1 = waypoints.elementAt(i);
      Waypoint w2 = waypoints.elementAt(i + 1);
      boolean betweenX = x >= Math.min(w1.x, w2.x) && x <= Math.max(w1.x, w2.x);
      boolean betweenY = y >= Math.min(w1.y, w2.y) && y <= Math.max(w1.y, w2.y);
      if (betweenX && betweenY && pointToLineDistance(w1, w2, x, y) < 5) {
        newWaypointEvent(i + 1, x, y, true);
        return true;
      }
    }
    return false;
  }

  /*PACKAGE ACCESS*/ Waypoint newWaypoint(String parentId, String id, int index, int x, int y) { // inbound ?
//	  System.err.println("new Waypoint 2: " + id);
    if (parentId.equals(getId())) {
//   System.err.println("waypoint " + parentId + ":" + index);
      Waypoint w = new Waypoint(id, this, x, y);
      waypoints.insertElementAt(w, index);
//      System.err.println("waypoints:" + waypoints);
      align();
      return w;
    }
    return null;
  }

  private void newWaypointEvent(int index, int x, int y, boolean select) {
    Message message = DiagramClient.theClient().getHandler().newMessage("newWaypoint", 4);
    message.args[0] = new Value(id);
    message.args[1] = new Value(index);
    message.args[2] = new Value(x);
    message.args[3] = new Value(y);
//    message.args[4] = new Value(select);
    
    DiagramClient.theClient().getHandler().raiseEvent(message);
  }
  
  /*PACKAGE ACCESS*/ void reconnectSource(Node node, Port port) {
    if (this.sourcePort != null) sourcePort.getSources().remove(this);
    this.sourceNode = node; //setSourceNode(node);
    this.sourcePort = port; //setSourcePort(port);
    port.getSources().add(this);
    start().setX(node.getX() + port.getX() + (port.getWidth() / 2));
    start().setY(node.getY() + port.getY() + (port.getHeight() / 2));
  }

  /*PACKAGE ACCESS*/ void reconnectTarget(Node node, Port port) {
    if (this.targetPort != null) targetPort.getTargets().remove(this);
    this.targetNode = node;//setTargetNode(node);
    this.targetPort = port;//setTargetPort(port);
    port.getTargets().add(this);
    end().setX(node.getX() + port.getX() + (port.getWidth() / 2));
    end().setY(node.getY() + port.getY() + (port.getHeight() / 2));
  }

  /*PACKAGE ACCESS*/ Waypoint start() {
    return waypoints.get(0);
  }
  
  /*PACKAGE ACCESS*/ Waypoint second() {
    return waypoints.elementAt(1);
  }
  
  /*PACKAGE ACCESS*/ Waypoint penultimate() {
    return waypoints.elementAt(waypoints.size() - 2);
  }
  
  /*PACKAGE ACCESS*/ Waypoint end() {
    return waypoints.get(waypoints.size() - 1);
  }

  /*PACKAGE ACCESS*/ void setBlue(int blue) {
    this.blue = blue;
  }

  /*PACKAGE ACCESS*/ void setGreen(int green) {
    this.green = green;
  }

  /*PACKAGE ACCESS*/ void setLineStyle(int lineStyle) {
    this.lineStyle = lineStyle;
  }

  /*PACKAGE ACCESS*/ void setRed(int red) {
    this.red = red;
  }

//  /*PACKAGE ACCESS*/ void setRefx(int refx) {
//    this.refx = refx;
//  }
//
//  /*PACKAGE ACCESS*/ void setRefy(int refy) {
//    this.refy = refy;
//  }

  /*PACKAGE ACCESS*/ void setText(String id, String text) {
    for (Label label : labels)
      label.setText(id, text);
  }

  /*PACKAGE ACCESS*/ void setBorder(String id, boolean border) {
	    for (Label label : labels)
	      label.setBorder(id, border);
	  }
  
  /*PACKAGE ACCESS*/ void setFill(String id, boolean fill) {
	    for (Label label : labels)
	      label.setFill(id, fill);
	  }
  
  /*PACKAGE ACCESS*/ boolean sharesSegment(Edge edge) {
    for (int i = 0; i < getWaypoints().size() - 1; i++) {
      Waypoint w11 = getWaypoints().elementAt(i);
      Waypoint w12 = getWaypoints().elementAt(i + 1);
      for (int j = 0; j < edge.getWaypoints().size() - 1; j++) {
        Waypoint w21 = edge.getWaypoints().elementAt(j);
        Waypoint w22 = edge.getWaypoints().elementAt(j + 1);
        if (w11.colocated(w21) && w12.colocated(w22)) return true;
      }
    }
    return false;
  }

  public void straighten() {
    // To straighten edges:
    // (1) Remove all way-points that are not needed.
    // (2) Straighten segments by moving way-points;
    for (Waypoint w : waypoints) {
      if (w != start() && w != end()) {
        checkWaypointsForRedundancy(w);
      }
    }
    straightenFrom(start(), 1, 1);
    straightenFrom(end(), waypoints.size() - 2, -1);
  }

  /*NEW PRIVATE*/private void straightenFrom(Waypoint w1, int start, int inc) {
    for (int i = start; i >= 0 && i < waypoints.size(); i = i + inc) {
      Waypoint w2 = waypoints.elementAt(i);
      int dx = Math.abs(w1.getX() - w2.getX());
      int dy = Math.abs(w1.getY() - w2.getY());
      if (dx < WAYPOINT_ALIGN) {
        w2.move(w1.getX(), w2.getY());
      }
      if (dy < WAYPOINT_ALIGN) {
        w2.move(w2.getX(), w1.getY());
      }
      w1 = w2;
    }
  }

  public String toString() {
    return "Edge(" + waypoints + "," + labels + ")";
  }

  public void writeXML(PrintStream out) {
    out.print("<Edge ");
    out.print("id='" + getId() + "' ");
//    out.print("refx='" + refx + "' ");
//    out.print("refy='" + refy+ "' ");
    out.print("source='" + sourceNode.getId() + "' ");
    out.print("target='" + targetNode.getId() + "' ");
    out.print("sourcePort='" + sourcePort.getId() + "' ");
    out.print("targetPort='" + targetPort.getId() + "' ");
    out.print("sourceHead='" + sourceHead.getID() + "' ");
    out.print("targetHead='" + targetHead.getID() + "' ");
    out.print("sourceX='" + waypoints.firstElement().x + "' ");
    out.print("targetX='" + waypoints.lastElement().x + "' ");
    out.print("sourceY='" + waypoints.firstElement().y + "' ");
    out.print("targetY='" + waypoints.lastElement().y + "' ");
    out.print("lineStyle='" + lineStyle + "' ");
    out.print("hidden='" + hidden + "' ");
    out.print("waypointStyle='" + waypointStyle.name() + "' ");
    out.print("red='" + red + "' ");
    out.print("green='" + green + "' ");
    out.print("blue='" + blue + "'>");
    for (Waypoint waypoint : waypoints)
      if (waypoint != start() && waypoint != end()) waypoint.writeXML(out);
    for (Label label : labels)
      label.writeXML(out);
    out.print("</Edge>");
  }

  public void hide(String id) {
    if (getId().equals(id)) hidden = true;
	for(Label label : labels) {
		label.hide(id);
	}
  }

  public void show(String id) {
    if (getId().equals(id)) hidden = false;
	for(Label label : labels) {
		label.show(id);
	}
  }

  public void doubleClick(GraphicsContext gc, Diagram diagram, int x, int y) {
	for(Label label : labels) {
		label.doubleClick(gc, diagram, x, y);
	}
//	if(lineIsHit(x,y,2)) {
//		
//	}
	}
  
  /////// INTERCEPT //////
 
  /*PACKAGE ACCESS*/ EdgePainter.Point targetIntercept() { // used by Label
    return intercept(targetNode, false);
  }
  
  /*PACKAGE ACCESS*/ EdgePainter.Point sourceIntercept() { // used by Label
    return intercept(sourceNode, true);
  }

  /*PACKAGE ACCESS*/ EdgePainter.Point intercept(Node node, boolean isSourceEnd) {
    EdgePainter.Point p = intercept(node, Position.TOP, isSourceEnd);
    p = p == null ? intercept(node, Position.LEFT, isSourceEnd) : p;
    p = p == null ? intercept(node, Position.RIGHT, isSourceEnd) : p;
    p = p == null ? intercept(node, Position.BOTTOM, isSourceEnd) : p;
    return p;
  }
  
  enum Position{TOP, BOTTOM, LEFT, RIGHT}
  
  /*PACKAGE ACCESS*/ EdgePainter.Point intercept(Node node, Position position, boolean isSourceEnd) { 
	  /* Find the point where the edge crosses the box's boundary.
	     If the edge is between two different nodes, there is only one solution.
	     If the edge is a loop, there are two, distinguishable as source and target
	     It is tried to find a point where the edge meets the border which is guessed by position.
	     The point, if found, or null is returned.
	  */
	  Waypoint firstOrLast = isSourceEnd ? start() : end();
	  Waypoint secondOrPenultimate = isSourceEnd ? second() : penultimate();
	  return intercept(node, firstOrLast, secondOrPenultimate, position);
  }

  /*PACKAGE ACCESS*/ EdgePainter.Point intercept(Node node, Waypoint w1, Waypoint w2, Position position) {
	  	int x2 = node.getX() + (position == Position.RIGHT  ? node.getWidth()  : 0);
	  	int y2 = node.getY() + (position == Position.BOTTOM ? node.getHeight() : 0);
	  	int x3 = node.getX() + (position != Position.LEFT   ? node.getWidth()  : 0);
	  	int y3 = node.getY() + (position != Position.TOP    ? node.getHeight() : 0);
	  	EdgePainter.Point p = intercept(w1.x, w1.y, w2.x, w2.y, x2, y2, x3, y3);
	  	if(position == Position.TOP    && (w2.y >= y2 || p.x <= x2 || p.x >= x3)) return null;
	  	if(position == Position.BOTTOM && (w2.y <= y2 || p.x <= x2 || p.x >= x3)) return null;
	  	if(position == Position.LEFT   && (w2.x > x2  || p.y <= y2 || p.y >= y3)) return null;
	  	if(position == Position.RIGHT  && (w2.x < x2  || p.y <= y2 || p.y >= y3)) return null;
	  	return p;
  }
  
  /*
   * intercept checks where the two lines (infinitely extended) would meet. return -1/-1 if the lines are parallel
   * 
   * intersect checks where the two lines do actually meet. returns -1/-1 if the lines don't meet
   */
  
  /*NEW PRIVATE*/private static EdgePainter.Point intercept(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
	  // 1 is first (or last) Waypoint
	  // 2 is next Waypoint
	  // 3 and 4 are the relevant corners of the node
	    float x12 = x1 - x2;
	    float x34 = x3 - x4; // is negative width of the line between the corners of the node, zero for TOP or BOTTOM
	    float y12 = y1 - y2;
	    float y34 = y3 - y4; // is negative height of the line between the corners of the node, zero for LEFT or RIGHT
	    float c = x12 * y34 - y12 * x34;
	    if (Math.abs(c) < 0.01)
	      // No intersection
	      return new EdgePainter.Point(-1, -1);
	    else {
	      // Intersection
	      float a = x1 * y2 - y1 * x2;
	      float b = x3 * y4 - y3 * x4;
	      float x = (a * x34 - b * x12) / c;
	      float y = (a * y34 - b * y12) / c;
	      return new EdgePainter.Point((int) x, (int) y);
	    }
	  }

	  /*PACKAGE ACCESS*/ static EdgePainter.Point intersect(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
	    float x12 = x1 - x2;
	    float x34 = x3 - x4;
	    float y12 = y1 - y2;
	    float y34 = y3 - y4;
	    float c = x12 * y34 - y12 * x34;
	    if (Math.abs(c) < 0.01)
	      // No intersection
	      return new EdgePainter.Point(-1, -1);
	    else {
	      // Intersection
	      float a = x1 * y2 - y1 * x2;
	      float b = x3 * y4 - y3 * x4;
	      float x = (a * x34 - b * x12) / c;
	      float y = (a * y34 - b * y12) / c;
	      if (isOnLine((int) x, (int) y, x1, y1, x2, y2) && isOnLine((int) x, (int) y, x3, y3, x4, y4))
	        return new EdgePainter.Point((int) x, (int) y);
	      else return new EdgePainter.Point(-1, -1);
	    }
	  }
  
  ///////// NEAR /////////
  
  public boolean near(Node node, int x, int y) {
	  /* We don't know if x/y is near source or target. 
	   * We try to find out and decide then.
	   * Could be both (assoc-self) or none (wrong node)?
	   */
	boolean checkSourceDistance = sourceNode == node;
	boolean checkTargetDistance = targetNode == node;
	if(checkSourceDistance) {
		EdgePainter.Point p = intercept(node, true);
		if(p != null && distance(p.x, p.y, x, y) < 10) return true;
	}
	if(checkTargetDistance) {
		EdgePainter.Point p = intercept(node, false);
		if(p != null && distance(p.x, p.y, x, y) < 10) return true;
	}	
	return false;
  }
  
  private double distance(int x1, int y1, int x2, int y2) {
    int dx = x1 - x2;
    int dy = y1 - y2;
    return Math.sqrt((dx * dx) + (dy * dy));
  }

  public boolean nearEnd(int x, int y) {
    return near(targetNode, x, y);
  }

  public boolean nearStart(int x, int y) {
    return near(sourceNode, x, y);
  }

  ///////// DRAW ////////

  //////// GETTER ///////
  
  public String getId() { return id; }
  
  public Node getSourceNode() { return sourceNode; }
  public Port getSourcePort() { return sourcePort; }
  public Node getTargetNode() { return targetNode; }
  public Port getTargetPort() { return targetPort; }

  private EdgePainter myPainter;
	public EdgePainter getPainter() {
		if(myPainter == null) myPainter = new EdgePainter(this);
		return myPainter;
	}

	public boolean isSquared() {
		return waypointStyle == WaypointStyle.SQUARED;
	}

}
