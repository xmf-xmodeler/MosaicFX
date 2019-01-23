package tool.clients.editors.texteditor;

import javafx.scene.canvas.GraphicsContext;

public class CheckSyntax extends Tool {

//  private static final Color CHECKING     = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
//  private static final Color NOT_CHECKING = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
//  private static final Color BLACK        = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

  TextEditor              editor;

  CheckSyntax(TextEditor editor) {
    this.editor = editor;
  }

  public void paint(GraphicsContext gc, int x, int y, int width, int height) {

//    Color fg = gc.getForeground();
//    Color bg = gc.getBackground();
//    gc.setBackground(editor.isCheckingSyntax() ? CHECKING : NOT_CHECKING);
//    gc.setForeground(BLACK);
//    gc.fillOval(x, y, width, height);
//    gc.drawOval(x, y, width, height);
//    gc.setForeground(fg);
//    gc.setBackground(bg);
  }

  public String toolTip() {
    if (editor.isCheckingSyntax())
      return "checking syntax";
    else return "not checking syntax";
  }

  public void click(TextEditor editor) {
    editor.setCheckingSyntax(!editor.isCheckingSyntax());
  }

  public void rightClick(int x,int y) {
  }

}
