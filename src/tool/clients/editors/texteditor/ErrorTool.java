package tool.clients.editors.texteditor;

import org.eclipse.swt.widgets.Display;

import javafx.scene.canvas.GraphicsContext;

public class ErrorTool extends Tool implements ErrorListener {

//  private static final Color ERROR   = Display.getDefault().getSystemColor(SWT.COLOR_RED);
//  private static final Color OK      = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
//  private static final Color BLACK   = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

  boolean                    isError = false;

  public void paint(GraphicsContext gc, int x, int y, int width, int height) {
//    Color fg = gc.getForeground();
//    Color bg = gc.getBackground();
//    gc.setBackground(isError ? ERROR : OK);
//    gc.setForeground(BLACK);
//    gc.fillOval(x, y, width, height);
//    gc.drawOval(x, y, width, height);
//    gc.setForeground(fg);
//    gc.setBackground(bg);
  }

  public String toolTip() {
    return !isError ? "no errors" : "error: click to view";
  }

  public void click(TextEditor editor) {
//    if (isError) editor.scrollToError();
  }

  public void error(TextEditor editor) {
    isError = true;
  }

  public void clear() {
    isError = false;
  }

  public void rightClick(int x,int y) {
    
  }

}
