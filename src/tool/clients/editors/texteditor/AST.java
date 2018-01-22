package tool.clients.editors.texteditor;

import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import javafx.scene.canvas.GraphicsContext;

public class AST {

//  private static final Color RED      = Display.getDefault().getSystemColor(SWT.COLOR_RED);

//  StyledText                 text;
  String                     tooltip;
  int                        charStart;
  int                        charEnd;
  Vector<AST>                children = new Vector<AST>();

  public AST(Object text, String tooltip, int charStart, int charEnd) {
    super();
//    this.text = text;
    this.tooltip = tooltip;
    this.charStart = charStart;
    this.charEnd = charEnd;
  }

  public boolean contains(AST a) {
    return a.getCharStart() >= charStart && a.getCharEnd() <= charEnd;
  }

  public void add(AST a) {
    for (AST child : children) {
      if (child.contains(a)) {
        child.add(a);
        return;
      }
    }
    children.add(a);
  }

  public AST find(int index) {
    if (index < charStart || index > charEnd) return null;
    AST found = null;
    for (AST child : children) {
      found = child.find(index);
      if (found != null) return found;
    }
    return this;
  }

  public String getTooltip() {
    return tooltip;
  }

  public int getCharStart() {
    return charStart;
  }

  public int getCharEnd() {
    return charEnd;
  }

  public void paint(GraphicsContext gc) {
//    try {
//      Color c = gc.getForeground();
//      gc.setForeground(RED);
//      int height = gc.getFontMetrics().getHeight();
//      int gap = 2;
//      int width = 5;
//      Point pStart = text.getLocationAtOffset(charStart);
//      Point pEnd = text.getLocationAtOffset(charEnd);
//
//      gc.drawLine(pStart.x-gap, pStart.y + height, pStart.x-gap, pStart.y);
//      gc.drawLine(pStart.x-gap, pStart.y + height, pStart.x + width, pStart.y + height);
//      gc.drawLine(pStart.x-gap, pStart.y, pStart.x + width, pStart.y);
//
//      gc.drawLine(pEnd.x+gap, pEnd.y + height, pEnd.x+gap, pEnd.y);
//      gc.drawLine(pEnd.x+gap, pEnd.y + height, pEnd.x - width, pEnd.y + height);
//      gc.drawLine(pEnd.x+gap, pEnd.y, pEnd.x - width, pEnd.y);
//
//      gc.setForeground(c);
//    } catch (Exception e) {
//    }
  }

}
