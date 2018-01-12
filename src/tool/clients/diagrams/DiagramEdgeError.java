package tool.clients.diagrams;

import org.eclipse.swt.graphics.Point;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public class DiagramEdgeError extends DiagramError {

  public static int DISTANCE = 30;

  Edge              edge;

  public DiagramEdgeError(String id, Edge edge, String error) {
    super(id, error);
    this.edge = edge;
  }

  public Point midPoint() {
    int numOfWayPoints = edge.getWaypoints().size();
    if (numOfWayPoints % 2 == 0) {
      // Find the mid point.
      Waypoint w1 = edge.getWaypoints().get((numOfWayPoints / 2-1));
      Waypoint w2 = edge.getWaypoints().get((numOfWayPoints / 2));
      int lowX = w1.x > w2.x ? w2.x : w1.x;
      int lowY = w1.y > w2.y ? w2.y : w1.y;
      int highX = w1.x < w2.x ? w2.x : w1.x;
      int highY = w1.y < w2.y ? w2.y : w1.y;
      return new Point(lowX + ((highX - lowX) / 2), lowY + ((highY - lowY) / 2));
    } else {
      Waypoint w = edge.getWaypoints().get((int) Math.floor(numOfWayPoints / 2));
      return new Point(w.x, w.y);
    }
  }

  public void paint(GraphicsContext gc, Diagram diagram) {
    Point p = midPoint();
    drawErrorBox(gc, p.x + DISTANCE, p.y + DISTANCE);
    Paint c = gc.getStroke();
//    gc.setForeground(Diagram.RED);
    gc.strokeLine(p.x, p.y, p.x + DISTANCE, p.y + DISTANCE);
    gc.setStroke(c);
  }

  public Node selectableNode() {
    return null;
  }

  public Edge selectableEdge() {
    return edge;
  }

}
