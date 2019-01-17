package tool.clients.diagrams;

import java.util.Vector;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class DiagramError {

  private static final int LINE_WIDTH = 1;
  private static final int PAD        = 2;

  public static String getLongestString(String[] array) {
    int maxLength = 0;
    String longestString = null;
    for (String s : array) {
      if (s.length() > maxLength) {
        maxLength = s.length();
        longestString = s;
      }
    }
    return longestString;
  }

  String         id;
  Vector<String> errors = new Vector<String>();

  public DiagramError(String id, String error) {
    this.id = id;
    errors.add(error);
  }

  public static int getLineWidth() {
    return LINE_WIDTH;
  }

  public String getId() {
    return id;
  }

  public void paint(GraphicsContext gc, Diagram diagram) {
//    org.eclipse.swt.graphics.Rectangle r = diagram.scroller.getClientArea();
    drawErrorBox(gc, PAD, (int)diagram.scroller.getWidth() - PAD);
  }

  protected int getWidth(GraphicsContext gc) {
    return 200;//gc.stringExtent(getLongestString(getLines())).x + (2 * LINE_WIDTH);
  }

  private String[] getLines() {
    String s = "Diagram Error:-------------:";
    for (int i = 0; i < errors.size(); i++) {
      String[] ss = errors.get(i).split(":");
      s = s + ":(" + (i + 1) + ") " + ss[0];
      for (int j = 1; j < ss.length; j++) {
        s = s + ":    " + ss[j];
      }
    }
    return s.split(":");
  }

  protected void drawErrorBox(GraphicsContext gc, int baseX, int baseY) {
    String[] lines = getLines();
    Paint bg = gc.getFill();
    Paint fg = gc.getStroke();
//    int alpha = gc.getAlpha();
//    int lineWidth = gc.getLineWidth();
//    int lineJoin = gc.getLineJoin();
//    gc.setAlpha(200);
    gc.setFill(new Color(1.,.7,.7,.7));
//    int height = gc.getFontMetrics().getHeight();
//    int boxWidth = getWidth(gc);
//    int boxHeight = height * lines.length;
    int boxWidth = 250;
    int boxHeight = 100;
    int height = 20;
    int x = baseX;
    int y = baseY - boxHeight;
    gc.fillRect(x, y, boxWidth, boxHeight);
    for (String line : lines) {
      gc.setFill(Color.RED);
      gc.fillText(line, x + LINE_WIDTH, y);
      x = baseX;
      y += height;
    }
//    gc.setForeground(Color.RED);
//    gc.setLineWidth(LINE_WIDTH);
//    gc.setLineJoin(SWT.JOIN_ROUND);
    gc.strokeRect(baseX, baseY - boxHeight, boxWidth, boxHeight);
    gc.setStroke(fg);
    gc.setFill(bg);
//    gc.setLineWidth(lineWidth);
//    gc.setLineJoin(lineJoin);
//    gc.setAlpha(alpha);
  }

  public void addError(String e) {
    errors.add(e);
  }

  public Node selectableNode() {
    return null;
  }

  public Edge selectableEdge() {
    return null;
  }

}
