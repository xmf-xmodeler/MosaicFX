package tool.clients.diagrams;

import java.io.PrintStream;

import javafx.geometry.Side;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import tool.clients.EventHandler;
import tool.clients.menus.MenuClient;
import xos.Message;
import xos.Value;

public class Waypoint implements Selectable {

  private static int       SELECTED_SIZE = 6;

  // Limits on the movement of a way-point...

  private static final int VERTICAL      = 0;
  private static final int HORIZONTAL    = 1;
  private static final int ANY           = 2;

  private String           id;
  private Edge             edge;
  int                      x;
  int                      y;
  private int              limitMovement = ANY;

  public Waypoint(String id, Edge edge, int x, int y) {
    super();
    this.id = id;
    this.edge = edge;
    this.x = x;
    this.y = y;
  }

  private final int MAX_DIFF = 4;
  boolean isApproximatelyLeftOrRightOf( Waypoint that) { return Math.abs(this.y - that.y) < Math.abs(this.x - that.x) && (Math.abs(this.y - that.y) < MAX_DIFF); }
  boolean isExactlyLeftOrRightOf( 		Waypoint that) { return this.x != that.x && this.y == that.y; }
  boolean isApproximatelyAboveOrBelow( 	Waypoint that) { return Math.abs(this.x - that.x) < Math.abs(this.y - that.y) && (Math.abs(this.x - that.x) < MAX_DIFF); }
  boolean isExactlyAboveOrBelow( 		Waypoint that) { return this.y != that.y && this.x == that.x; }
  
  void limitMovementToHorizontal() { limitMovement = HORIZONTAL; }
  void limitMovementToVertical() { limitMovement = VERTICAL; } 
  private void unLimitMovement() { limitMovement = ANY; }
  private boolean canMoveHorizontally() { return limitMovement == ANY || limitMovement == HORIZONTAL; }
  private boolean canMoveVertically() { return limitMovement == ANY || limitMovement == VERTICAL; }

  boolean colocated(Waypoint other) { return x == other.x && y == other.y; }
  
  Edge getEdge() { return edge; }
  String getId() { return id; }
  
  // These getters and setters are pointless as direct access is allowed
  // but too much to do to replace them with direct access
  // Don't add any checks/side effects in them as they will be bypassed anyway.
  /*@Deprecated*/ int getX() { return x; }
  /*@Deprecated*/ int getY() { return y; }
  /*@Deprecated*/ void setX(int x) { this.x = x; }
  /*@Deprecated*/ void setY(int y) { this.y = y; }
  
  // even more (3) methods to set x and y, but this time checking if it is allowed.  
  void move(int x, int y) {
    if (canMoveHorizontally()) this.x = x;
    if (canMoveVertically()) this.y = y;
    if (edge.start() != this && edge.end() != this) moveEvent(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  void move(String id, int x, int y) {
    if (id.equals(getId())) {
      if (canMoveHorizontally()) this.x = x;
      if (canMoveVertically()) this.y = y;
      edge.checkWaypointsForRedundancy(this);
    }
  }

  @Override
  public void moveBy(int dx, int dy) {
    if (canMoveHorizontally()) x = x + dx;
    if (canMoveVertically()) y = y + dy;
    edge.movedBy(this);
  }
  
  boolean isEnd() { return getId().equals("end"); }
  boolean isStart() { return getId().equals("start"); }
  public String toString() { return "W(" + x + "," + y + "(" + id + "))"; }
  
  @Override
  public void deselect() {
    unLimitMovement();
    EventHandler eventHandler = DiagramClient.theClient().getHandler();
    Message message = eventHandler.newMessage("edgeDeselected", 1);
    message.args[0] = new Value(edge.getId());
    eventHandler.raiseEvent(message);
  }

  double distance(Waypoint w) {
    int dx = x - w.x;
    int dy = y - w.y;
    return  Math.sqrt((dx * dx) + (dy * dy));
  }

  boolean nearTo(int x, int y) {
    int dx = this.x - x;
    int dy = this.y - y;
    return Math.sqrt((dx * dx) + (dy * dy)) < 5;
  }
  
  @Override
  public void moveEvent(int minX, int maxX, int minY, int maxY) {

    if(getX() < minX)  setX(minX);
	if(getY() < minY)  setY(minY);
	if(getX()  > maxX) setX(maxX);
	if(getY()  > maxY) setY(maxY);
		
    Message message = DiagramClient.theClient().getHandler().newMessage("move", 3);
    message.args[0] = new Value(id);
    message.args[1] = new Value(x);
    message.args[2] = new Value(y);
    DiagramClient.theClient().getHandler().raiseEvent(message);
  }


  @Override
  public void paintSelected(GraphicsContext gc, int xOffset, int yOffset) { 
//    Color c = gc.getForeground();
    gc.setStroke(Color.RED);
    gc.strokeOval(x - SELECTED_SIZE + xOffset, y - SELECTED_SIZE + yOffset, SELECTED_SIZE * 2, SELECTED_SIZE * 2);
//    gc.setForeground(c);
    edge.getPainter().paintOrthogonal(gc, this); // Zielscheibe
  }

  @Override
  public ContextMenu rightClick(javafx.scene.Node anchor, Side side, int x, int y) {
      return MenuClient.popup(id, anchor, side, x, y);
  }

  @Override
  public void select() {
    EventHandler eventHandler = DiagramClient.theClient().getHandler();
    Message message = eventHandler.newMessage("edgeSelected", 1);
    message.args[0] = new Value(edge.getId());
    eventHandler.raiseEvent(message);
  }

  void writeXML(PrintStream out) {
    out.print("<Waypoint id='" + getId() + "' index='" + edge.getWaypoints().indexOf(this) + "' x='" + x + "' y='" + y + "'/>");
  }
}
