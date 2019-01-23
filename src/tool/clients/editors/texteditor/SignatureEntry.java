package tool.clients.editors.texteditor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javafx.scene.canvas.GraphicsContext;

public class SignatureEntry implements Comparable<SignatureEntry> {

  static int             INDENT     = 5;
//  static final Color     SIG_COLOUR = Display.getDefault().getSystemColor(SWT.COLOR_DARK_MAGENTA);

  int                    start;
  String                 shortLabel;
  String                 longLabel;
  boolean                isOpen     = false;
  boolean                overToggle = false;
  Vector<SignatureEntry> children   = new Vector<SignatureEntry>();

  public SignatureEntry(int start, String shortLabel, String longLabel) {
    super();
    this.start = start;
    this.shortLabel = shortLabel;
    this.longLabel = longLabel;
  }

  public void add(SignatureEntry child) {
    children.add(child);
    Collections.sort(children);
  }

  public int getStart() {
    return start;
  }

  public String getShortLabel() {
    return shortLabel;
  }

  public String getLongLabel() {
    return longLabel;
  }

  public Vector<SignatureEntry> getChildren() {
    return children;
  }

//  public void click(TextEditor textEditor, MouseEvent event) {
//    if (overToggle && !children.isEmpty())
//      toggle();
//    else textEditor.setSelection(start+100);
//  }

  private void toggle() {
    isOpen = !isOpen;
  }

  public String getLabel() {
    if (children.isEmpty())
      return "  " + getShortLabel();
    else {
      String open = isOpen ? "+" : "-";
      return open + " " + getShortLabel();
    }
  }

  public int paint(GraphicsContext gc, int x, int y, int height, int mouseX, int mouseY, Signature signature) {
//    Color c = gc.getForeground();
//    gc.setForeground(SIG_COLOUR);
//    gc.drawString(getLabel(), x, y);
//    overToggle = false;
//    int length = gc.textExtent(getLabel()).x;
//    if (mouseX >= x && mouseX <= x + length && mouseY >= y && mouseY <= y + height) {
//      signature.selectedEntry = this;
//      if (Math.abs(mouseX - x) < 10) overToggle = true;
//      if (overToggle && !children.isEmpty())
//        gc.drawString("*", x, y);
//      else gc.drawLine(x, y + (height - 1), x + length, y + (height - 1));
//    }
//    y = y + height;
//    gc.setForeground(c);
//    if (isOpen)
//      return signature.paint(x + INDENT, y, getChildren(), gc);
//    else 
    	return y;
  }

  public int compareTo(SignatureEntry e) {
    return getShortLabel().compareTo(e.getShortLabel());
  }

}
