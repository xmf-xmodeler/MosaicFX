package tool.clients.diagrams;

import java.io.PrintStream;
import java.util.Vector;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Port {

  Vector<Edge> sources = new Vector<Edge>();
  Vector<Edge> targets = new Vector<Edge>();

  String       id;
  int          x;
  int          y;
  int          width;
  int          height;

  public Port(String id, int x, int y, int width, int height) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public String getId() {
    return id;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public boolean contains(int x, int y) {
    return x >= getX() && y >= getY() && x <= (getX() + getWidth()) && y <= (getY() + getHeight());
  }

  public String toString() {
    return "Port(" + x + "," + y + "," + width + "," + height + ")";
  }

  public void addSource(Edge edge) {
    sources.add(edge);
  }

  public void addTarget(Edge edge) {
    targets.add(edge);
  }

  public Vector<Edge> getSources() {
    return sources;
  }

  public Vector<Edge> getTargets() {
    return targets;
  }

  public void ownerMovedBy(int dx, int dy) {
    for (Edge edge : sources)
      edge.moveSourceBy(dx, dy);
    for (Edge edge : targets)
      edge.moveTargetBy(dx, dy);
  }

  public int getMidX() {
    return getX() + (getWidth() / 2);
  }

  public int getMidY() {
    return getY() + (getHeight() / 2);
  }

  public void writeXML(PrintStream out) {
    out.print("<Port id='" + getId() + "' x='" + getX() + "' y='" + getY() + "' width='" + getWidth() + "' height='" + getHeight() + "'/>");
  }

  public void resize(String id, int width, int height) {
    if (getId().equals(id)) {
      this.width = width;
      this.height = height;
    }
  }

  public void paintHover(GraphicsContext gc, int x, int y, int xOffset, int yOffset) {
    Paint c = gc.getStroke();
    gc.setStroke(Color.RED);
    gc.strokeRect(x + getX() + xOffset, y + getY() + yOffset, getWidth(), getHeight());
    gc.setStroke(c);
  }
}
