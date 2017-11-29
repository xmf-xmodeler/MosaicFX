package tool.clients.editors.texteditor;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import tool.xmodeler.XModeler;

public class Signature {

  static Font            FONT          = new Font(XModeler.getXModeler().getDisplay(), new FontData("Monaco", 8, SWT.NONE));
  static final Color     BLACK         = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
  static final Color     WHITE         = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
  static final int       BORDER        = 10;

  Vector<SignatureEntry> entries       = new Vector<SignatureEntry>();
  SignatureEntry         selectedEntry = null;
  boolean                visible       = true;
  int                    mouseX;
  int                    mouseY;
  int                    startX;
  int                    height;

  public Signature() {
  }

  public void add(SignatureEntry entry) {
    entries.add(entry);
    Collections.sort(entries);
  }

  public boolean isVisible() {
    return visible;
  }

  public void setIsVisible(boolean b) {
    visible = b;
  }

  public int maxHeight(GC gc) {
    Font f = gc.getFont();
    gc.setFont(FONT);
    int height = gc.getFontMetrics().getHeight();
    gc.setFont(f);
    return height * totalEntries(entries);
  }

  private int totalEntries(Vector<SignatureEntry> entries) {
    int n = 0;
    for (SignatureEntry entry : entries) {
      n += totalEntries(entry.getChildren()) + 1;
    }
    return n;
  }

  public void paint(int width, GC gc) {
    int maxWidth = maxWidth(entries, gc);
    selectedEntry = null;
    paint(width - (maxWidth + BORDER), 0, entries, gc);
  }

  public int paint(int x, int y, Vector<SignatureEntry> entries, GC gc) {
    startX = x;
    Font f = gc.getFont();
    Color fc = gc.getForeground();
    Color bc = gc.getBackground();
    gc.setFont(FONT);
    gc.setForeground(BLACK);
    gc.setBackground(WHITE);
    int height = gc.getFontMetrics().getHeight();
    for (SignatureEntry entry : entries) {
      y = entry.paint(gc, x, y, height, mouseX, mouseY, this);
    }
    gc.setFont(f);
    gc.setForeground(fc);
    gc.setBackground(bc);
    this.height = y;
    return y;
  }

  private int maxWidth(Vector<SignatureEntry> entries, GC gc) {
    int width = 0;
    Font f = gc.getFont();
    gc.setFont(FONT);
    for (SignatureEntry entry : entries) {
      width = Math.max(width, gc.stringExtent(entry.getShortLabel()).x);
      width = Math.max(width, maxWidth(entry.getChildren(), gc) + SignatureEntry.INDENT);
    }
    gc.setFont(f);
    return width;
  }

  public boolean mouseOver(int x, int y) {
    boolean currentlyOver = mouseX >= startX && mouseY <= height;
    mouseX = x;
    mouseY = y;
    boolean nowOver = mouseX >= startX && mouseY <= height;
    return nowOver || (currentlyOver && !nowOver);
  }

  public void click(TextEditor textEditor, MouseEvent event) {
    if (selectedEntry != null && event.count == 1) {
      selectedEntry.click(textEditor, event);
      textEditor.redraw();
    }
  }

  public void clear() {
    entries.clear();
  }

}
