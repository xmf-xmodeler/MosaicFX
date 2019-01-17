package tool.clients.editors.texteditor;

import javafx.scene.canvas.GraphicsContext;

public class ShowSignature extends Tool {

//  private static final Color SHOWING     = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
//  private static final Color NOT_SHOWING = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
//  private static final Color BLACK       = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

  TextEditor                 editor;

  ShowSignature(TextEditor editor) {
    this.editor = editor;
  }

  public void paint(GraphicsContext gc, int x, int y, int width, int height) {
//    Color fg = gc.getForeground();
//    Color bg = gc.getBackground();
//    gc.setBackground(editor.isShowingSignature() ? SHOWING : NOT_SHOWING);
//    gc.setForeground(BLACK);
//    gc.fillOval(x, y, width, height);
//    gc.drawOval(x, y, width, height);
//    gc.setForeground(fg);
//    gc.setBackground(bg);
  }

  public String toolTip() {
    if (editor.isShowingSignature())
      return "showing signature";
    else return "not showing signature";
  }

  public void click(TextEditor editor) {
    editor.setShowingSignature(!editor.isShowingSignature());
  }

  public void rightClick(int x, int y) {
  }

}
