package tool.clients.diagrams;

import org.eclipse.swt.graphics.GC;

public class DiagramNodeError extends DiagramError {

  public static int LENGTH = 30;

  Node              node;

  public DiagramNodeError(String id, Node node, String error) {
    super(id, error);
    this.node = node;
  }

  public void paint(GC gc, Diagram diagram) {
    int nodeWidth = node.getWidth();
    int boxWidth = getWidth(gc);
    int diff = (boxWidth - nodeWidth) / 2;
    drawErrorBox(gc, node.getX() - diff, node.getY() - LENGTH);
    gc.drawLine(node.getX() + (nodeWidth / 2), node.getY(), node.getX() + (nodeWidth / 2), node.getY() - LENGTH);
  }

  public Node selectableNode() {
    return node;
  }

  public Edge selectableEdge() {
    return null;
  }

}
