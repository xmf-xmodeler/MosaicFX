package tool.clients.diagrams;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

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

  public void paint(GC gc, Diagram diagram) {
    org.eclipse.swt.graphics.Rectangle r = diagram.scroller.getClientArea();
    drawErrorBox(gc, PAD, r.height - PAD);
  }

  protected int getWidth(GC gc) {
    return gc.stringExtent(getLongestString(getLines())).x + (2 * LINE_WIDTH);
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

  protected void drawErrorBox(GC gc, int baseX, int baseY) {
    String[] lines = getLines();
    Color bg = gc.getBackground();
    Color fg = gc.getForeground();
    int alpha = gc.getAlpha();
    int lineWidth = gc.getLineWidth();
    int lineJoin = gc.getLineJoin();
    gc.setAlpha(200);
    gc.setForeground(Diagram.RED);
    gc.setBackground(Diagram.WHITE);
    int height = gc.getFontMetrics().getHeight();
    int boxWidth = getWidth(gc);
    int boxHeight = height * lines.length;
    int x = baseX;
    int y = baseY - boxHeight;
    gc.fillRectangle(x, y, boxWidth, boxHeight);
    for (String line : lines) {
      gc.drawString(line, x + LINE_WIDTH, y);
      x = baseX;
      y += height;
    }
    gc.setForeground(Diagram.RED);
    gc.setLineWidth(LINE_WIDTH);
    gc.setLineJoin(SWT.JOIN_ROUND);
    gc.drawRectangle(baseX, baseY - boxHeight, boxWidth, boxHeight);
    gc.setForeground(fg);
    gc.setBackground(bg);
    gc.setLineWidth(lineWidth);
    gc.setLineJoin(lineJoin);
    gc.setAlpha(alpha);
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
