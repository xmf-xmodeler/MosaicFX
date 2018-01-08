package tool.clients.diagrams;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import tool.clients.EventHandler;
import tool.clients.menus.MenuClient;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class Node implements Selectable {

  private static final int SELECTION_GAP = 4;
  private static final int EAR_GAP       = SELECTION_GAP + 2;
  private static final int EAR_LENGTH    = 6;
  String                   id;
  int                      x;
  int                      y;
  int                      width;
  int                      height;
  boolean                  selectable;
  Hashtable<String, Port>  ports         = new Hashtable<String, Port>();
  Vector<Display>          displays      = new Vector<Display>();
  boolean                  hidden        = false;

  public Node(String id, int x, int y, int width, int height, boolean selectable) {
    super();
    this.id = id;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.selectable = selectable;
  }

  public boolean atBottomLeftCorner(int x, int y) {
    return distance(new Point(getX(), getY() + getHeight()), new Point(x, y)) < 5;
  }

  public boolean atBottomRightCorner(int x, int y) {
    return distance(new Point(getX() + getWidth(), getY() + getHeight()), new Point(x, y)) < 5;
  }

  public boolean atCorner(int x, int y) {
    return atBottomLeftCorner(x, y) || atBottomRightCorner(x, y) || atTopLeftCorner(x, y) || atTopRightCorner(x, y);
  }

  public boolean atOrigin() {
    return getX() == 0 && getY() == 0;
  }

  public boolean atTopLeftCorner(int x, int y) {
    return distance(new Point(getX(), getY()), new Point(x, y)) < 5;
  }

  public boolean atTopRightCorner(int x, int y) {
    return distance(new Point(getX() + getWidth(), getY()), new Point(x, y)) < 5;
  }

  public boolean contains(int x, int y) {
    return getX() <= x && getY() <= y && x <= (getX() + getWidth()) && y <= (getY() + getHeight());
  }

  public boolean contains(Waypoint w) {
    return contains(w.x, w.y);
  }

  private double distance(Point p1, Point p2) {
    int dx = p1.x - p2.x;
    int dy = p1.y - p2.y;
    return Math.sqrt((dx * dx) + (dy * dy));
  }

  public void doubleClick(GC gc, Diagram diagram, int x, int y) {
    for (Display display : displays) {
      display.doubleClick(gc, diagram, getX(), getY(), x, y);
    }
  }

  public void editText(String id) {
    for (Display display : displays)
      display.editText(id);
  }

  public void setEditable(String id, boolean editable) {
	    for (Display display : displays)
	      display.setEditable(id, editable);
  }
  
  public void showEdges(String id, boolean top,boolean bottom,boolean left, boolean right){
	    for (Display display : displays)
		      display.showEdges(id, top, bottom, left, right);
  }
  
  public Display getDisplay(String id) {
    for (Display display : displays)
      if (display.getId().equals(id)) return display;
    return null;
  }

  public int getHeight() {
    return height;
  }

  public String getId() {
    return id;
  }

  public Hashtable<String, Port> getPorts() {
    return ports;
  }

  public int getWidth() {
    return width;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public boolean isSelectable() {
    return selectable;
  }

  public void italicise(String id, boolean italics) {
    for (Display display : displays)
      display.italicise(id, italics);
  }

  public int maxX() {
    return getX() + getWidth();
  }

  public int maxY() {
    return getY() + getHeight();
  }

  public void move(int x, int y) {
    int dx = x - getX();
    int dy = y - getY();
    this.x = x;
    this.y = y;
    for (Port port : ports.values()) {
      port.ownerMovedBy(dx, dy);
    }
  }

  public void move(String id, int x, int y) {
    if (getId().equals(id))
      move(x, y);
    else {
      for (Display display : displays)
        display.move(id, x, y);
    }
  }

  public void moveBy(int dx, int dy) {
    x = x + dx;
    y = y + dy;
    for (Port port : ports.values()) {
      port.ownerMovedBy(dx, dy);
    }
  }

  public void moveEvent(int minX, int maxX, int minY, int maxY) {
	  
	int oldX = getX(); // Save the intended but misplaced Position for the ports.
	int oldY = getY();
	
	if(getX() < minX) setX(minX);
	if(getY() < minY) setY(minY);
	if(getX() + getWidth() > maxX) setX(maxX - getWidth());
	if(getY() + getHeight() > maxY) setY(maxY - getHeight());
	
	//The position is now corrected. The ports are moved by the diff to follow
	
    for (Port port : ports.values()) {
        port.ownerMovedBy(getX() - oldX, getY() - oldY);
    }
	
    Message message = DiagramClient.theClient().getHandler().newMessage("move", 3);
    message.args[0] = new Value(id);
    message.args[1] = new Value(getX());
    message.args[2] = new Value(getY());
    DiagramClient.theClient().getHandler().raiseEvent(message);
  }

  public void newBox(String parentId, String id, int x, int y, int width, int height, int curve, boolean top, boolean right, boolean bottom, boolean left, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue) {
    if (getId().equals(parentId)) {
      Box box = new Box(id, x, y, width, height, curve, top, right, bottom, left, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue);
      displays.add(box);
    } else {
      for (Display display : displays) {
        display.newBox(parentId, id, x, y, width, height, curve, top, right, bottom, left, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue);
      }
    }
  }

  public void newEllipse(String parentId, String id, int x, int y, int width, int height, boolean showOutline, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue) {
    if (parentId.equals(getId()))
      displays.add(new Ellipse(id, x, y, width, height, showOutline, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue));
    else {
      for (Display display : displays)
        display.newEllipse(parentId, id, x, y, width, height, showOutline, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue);
    }
  }

  public void newShape(String parentId, String id, int x, int y, int width, int height, boolean showOutline, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue, int[] points) {
	    if (parentId.equals(getId()))
	      displays.add(new Shape(id, x, y, width, height, showOutline, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue,points));
	    else {
	      for (Display display : displays)
	        display.newShape(parentId, id, x, y, width, height, showOutline, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue, points);
	    }
	  }
  
  public void newImage(String parentId, String id, String fileName, int x, int y, int width, int height) {
    if (parentId.equals(getId())) {
      displays.add(new Image(id, fileName, x, y, width, height));
    } else {
      for (Display display : displays)
        display.newImage(parentId, id, fileName, x, y, width, height);
    }
  }

  public void newMultilineText(String parentId, String id, String text, int x, int y, int width, int height, boolean editable, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue, String font) {
    if (getId().equals(parentId)) {
      MultilineText t = new MultilineText(id, text, x, y, width, height, editable, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue, font);
      displays.add(displays.size(), t);
    } else {
      for (Display d : displays)
        d.newMultilineText(parentId, id, text, x, y, width, height, editable, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue, font);
    }
  }

  public void newPort(String id, int x, int y, int width, int height) {
    Port port = new Port(id, x, y, Math.min(width, getWidth()), Math.min(height, getHeight()));
    ports.put(id, port);
  }

  private void newText(String id, String s, int x, int y, boolean editable, boolean underline, boolean italicise, int red, int green, int blue) {
    Text text = new Text(id, s, x, y, editable, underline, italicise, red, green, blue);
    displays.add(text);
  }
  
  public void newNestedDiagram(String parentId, String id, int x, int y, int width, int height, javafx.scene.canvas.Canvas canvas) {
	  for (Display display : displays) {
		  display.newNestedDiagram(parentId, id, x, y, width, height, canvas);
	  }
  }

  public void newText(String parentId, String id, String text, int x, int y, boolean editable, boolean underline, boolean italicise, int red, int green, int blue) {
    if (parentId.equals(getId()))
      newText(id, text, x, y, editable, underline, italicise, red, green, blue);
    else for (Display display : displays)
      display.newText(parentId, id, text, x, y, editable, underline, italicise, red, green, blue);
  }

  public void paint(javafx.scene.canvas.GraphicsContext gc, Diagram diagram, int xOffset, int yOffset) {
    if (!hidden) {
      // Clear the background of the node...
      gc.setFill(javafx.scene.paint.Color.WHEAT);
      gc.fillRect(x+xOffset, y+yOffset, getWidth(), getHeight());
//      gc.setStroke(javafx.scene.paint.Color.BLACK);
//      gc.strokeRect(x+xOffset, y+yOffset, getWidth(), getHeight());
    	
//      Color background = gc.getBackground();
//      gc.setBackground(diagram.getDiagramBackgroundColor());
//      gc.fillRectangle(x+xOffset, y+yOffset, getWidth(), getHeight());
//      gc.setBackground(background);
      for (Display display : displays) {
        display.paint(gc, x+xOffset, y+yOffset);
      }
    }
  }

  public void paintPortHover(GC gc, int x, int y, int xOffset, int yOffset) {
    if (!hidden) {
      for (Port port : ports.values()) {
        if (port.contains(x - getX(), y - getY())) {
          port.paintHover(gc, getX(), getY(), xOffset, yOffset);
        }
      }
    }
  }

  private void paintResizeHover(GC gc, int x, int y, int xOffset, int yOffset) {
    if (!hidden) {
      if (atTopLeftCorner(x, y)) paintResizeTopLeft(gc, xOffset, yOffset);
      if (atTopRightCorner(x, y)) paintResizeTopRight(gc, xOffset, yOffset);
      if (atBottomLeftCorner(x, y)) paintResizeBottomLeft(gc, xOffset, yOffset);
      if (atBottomRightCorner(x, y)) paintResizeBottomRight(gc, xOffset, yOffset);
    }
  }

  private void paintResizeBottomLeft(GC gc, int xOffset, int yOffset) {
    if (!hidden) {
      Color c = gc.getForeground();
      gc.setForeground(Diagram.GREEN);
      gc.drawLine(xOffset + getX() - EAR_GAP, yOffset + getY() + (getHeight() + EAR_GAP), 
    		      xOffset + getX() - EAR_GAP, yOffset + getY() + (getHeight() - EAR_LENGTH));
      gc.drawLine(xOffset + getX() - EAR_GAP, yOffset + getY() + (getHeight() + EAR_GAP), 
    		      xOffset + getX() + EAR_LENGTH, yOffset + getY() + (getHeight() + EAR_GAP));
      gc.setForeground(c);
    }
  }

  private void paintResizeBottomRight(GC gc, int xOffset, int yOffset) {
    if (!hidden) {
      Color c = gc.getForeground();
      gc.setForeground(Diagram.GREEN);
      gc.drawLine(xOffset + getX() + (getWidth() + EAR_GAP), yOffset + getY() + (getHeight() + EAR_GAP), 
    		      xOffset + getX() + (getWidth() + EAR_GAP), yOffset + getY() + (getHeight() - EAR_LENGTH));
      gc.drawLine(xOffset + getX() + (getWidth() + EAR_GAP), yOffset + getY() + (getHeight() + EAR_GAP), 
    		      xOffset + getX() + (getWidth() - EAR_LENGTH), yOffset + getY() + (getHeight() + EAR_GAP));
      gc.setForeground(c);
    }
  }

  private void paintResizeTopLeft(GC gc, int xOffset, int yOffset) {
    if (!hidden) {
      Color c = gc.getForeground();
      gc.setForeground(Diagram.GREEN);
      gc.drawLine(xOffset + getX() - EAR_GAP, yOffset + getY() - EAR_GAP, 
    		      xOffset + getX() + EAR_LENGTH, yOffset + getY() - EAR_GAP);
      gc.drawLine(xOffset + getX() - EAR_GAP, yOffset + getY() - EAR_GAP, 
    		      xOffset + getX() - EAR_GAP, yOffset + getY() + EAR_LENGTH);
      gc.setForeground(c);
    }
  }

  private void paintResizeTopRight(GC gc, int xOffset, int yOffset) {
    if (!hidden) {
      Color c = gc.getForeground();
      gc.setForeground(Diagram.GREEN);
      gc.drawLine(xOffset + getX() + getWidth() + EAR_GAP, yOffset + getY() - EAR_GAP, 
    		      xOffset + getX() + (getWidth() - EAR_LENGTH), yOffset + getY() - EAR_GAP);
      gc.drawLine(xOffset + getX() + getWidth() + EAR_GAP, yOffset + getY() - EAR_GAP, 
    		      xOffset + getX() + getWidth() + EAR_GAP, yOffset + getY() + EAR_LENGTH);
      gc.setForeground(c);
    }
  }

  private void paintSelectableOutline(GC gc, int x, int y) {
    if (!hidden) {
      Color c = gc.getForeground();
      int width = gc.getLineWidth();
      gc.setLineWidth(1);
      gc.setForeground(XModeler.getXModeler().getDisplay().getSystemColor(SWT.COLOR_RED));
      gc.drawRectangle(getX() - SELECTION_GAP + x, getY() - SELECTION_GAP + y, getWidth() + (SELECTION_GAP * 2), getHeight() + (SELECTION_GAP * 2));
      gc.setForeground(c);
      gc.setLineWidth(width);
    }
  }

  public void paintHover(GC gc, int x, int y, int xOffset, int yOffset, boolean selected) {
    if (!hidden && contains(x, y)) {
      paintSelectableOutline(gc, xOffset, yOffset);
      for (Display display : displays)
        display.paintHover(gc, x, y, getX(), getY());
    }
    if (!selected && !contains(x, y) && atCorner(x, y)) paintResizeHover(gc, x, y, xOffset, yOffset);
  }
  
  public void paintSelected(GC gc, int x, int y) { 
    if (!hidden) {
      Color c = gc.getForeground();
      int width = gc.getLineWidth();
      gc.setLineWidth(2);
      gc.setForeground(XModeler.getXModeler().getDisplay().getSystemColor(SWT.COLOR_RED));
      gc.drawRectangle(getX() - SELECTION_GAP + x , getY() - SELECTION_GAP + y, getWidth() + (SELECTION_GAP * 2), getHeight() + (SELECTION_GAP * 2));
      gc.setForeground(c);
      gc.setLineWidth(width);
    }
  }


  public void remove(String id) {
    Display d = getDisplay(id);
    if (d != null) {
      displays.remove(d);
    } else {
      for (Display display : displays) {
        display.remove(id);
      }
    }
  }

  public void resize(String id, int width, int height) {
    if (id.equals(getId())) {
      this.width = width;
      this.height = height;
    } else {
      for (Display display : displays)
        display.resize(id, width, height);
      for (Port port : ports.values())
        port.resize(id, width, height);
    }
  }

  @Override
  public ContextMenu rightClick(javafx.scene.Node anchor, Side side, int x, int y) {
    if (!hidden) {
      return MenuClient.popup(id, anchor, side, x, y);
    }
    return null;
  }

  public boolean sameLocation(Node other) {
    return getX() == other.getX() && getY() == other.getY();
  }

  public void setFillColor(String id, int red, int green, int blue) {
    for (Display display : displays)
    	display.setFillColor(id, red, green, blue);
  }

  public void setText(String id, String text) {
    for (Display display : displays)
      display.setText(id, text);
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public String toString() {
    return "Node(" + id + "," + x + "," + y + "," + width + "," + height + "," + displays + ")";
  }

  public void writeXML(PrintStream out) {
    out.print("<Node id='" + getId() + "' x = '" + getX() + "' y='" + getY() + "' width='" + getWidth() + "' height='" + getHeight() + "' hidden='" + hidden + "' selectable='" + isSelectable() + "'>");
    for (Port port : ports.values())
      port.writeXML(out);
    for (Display display : displays)
      display.writeXML(out);
    out.print("</Node>");

  }

  public void setFont(String id, String fontData) {
    for (Display display : displays)
      display.setFont(id, fontData);
  }

  public void deselect() {
    EventHandler eventHandler = DiagramClient.theClient().getHandler();
    Message message = eventHandler.newMessage("nodeDeselected", 1);
    message.args[0] = new Value(getId());
    eventHandler.raiseEvent(message);
  }

  public void select() {
    EventHandler eventHandler = DiagramClient.theClient().getHandler();
    Message message = eventHandler.newMessage("nodeSelected", 1);
    message.args[0] = new Value(getId());
    eventHandler.raiseEvent(message);
  }

  public void hide(String id) {
    if (getId().equals(id)) hidden = true;
  }

  public void show(String id) {
    if (getId().equals(id)) hidden = false;
  }
}
