package tool.clients.diagrams;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class ErrorTool extends TrayTool {

  private static final Color ERROR   = Display.getDefault().getSystemColor(SWT.COLOR_RED);
  private static final Color OK      = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
  private static final Color BLACK   = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

  boolean                    isError = false;

  public void paint(GC gc, int x, int y, int width, int height) {
    Color fg = gc.getForeground();
    Color bg = gc.getBackground();
    gc.setBackground(isError ? ERROR : OK);
    gc.setForeground(BLACK);
    gc.fillOval(x, y, width, height);
    gc.drawOval(x, y, width, height);
    gc.setForeground(fg);
    gc.setBackground(bg);
  }

  public String toolTip() {
    return !isError ? "no errors" : "error: click to view";
  }

  public void click(Diagram diagram) {
    diagram.selectError();
  }

  public void error(Diagram diagram) {
    isError = true;
  }

  public void clear() {
    isError = false;
  }

  public void rightClick(int x, int y) {

  }

  public void setError(boolean isError) {
    this.isError = isError;
  }

}
