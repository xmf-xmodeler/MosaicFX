package tool.clients.diagrams;

import java.io.PrintStream;
import java.util.Vector;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import tool.xmodeler.XModeler;

public class Box implements Display {
  String          id;
  int             x;
  int             y;
  int             width;
  int             height;
  int             curve;
  boolean         top;
  boolean         right;
  boolean         bottom;
  boolean         left;
  int             lineRed;
  int             lineGreen;
  int             lineBlue;
  int             fillRed;
  int             fillGreen;
  int             fillBlue;
  Vector<Display> displays      = new Vector<Display>();
  Diagram         nestedDiagram = null;

  public Box(String id, int x, int y, int width, int height, int curve, boolean top, boolean right, boolean bottom, boolean left, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue) {
    super();
    this.id = id;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.curve = curve;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
    this.lineRed = lineRed == -1 ? 0 : lineRed % 256;
    this.lineGreen = lineGreen == -1 ? 0 : lineGreen % 256;
    this.lineBlue = lineBlue == -1 ? 0 : lineBlue % 256;
    // this.fillRed = fillRed == -1 ? 255 : fillRed % 256;
    // this.fillGreen = fillGreen == -1 ? 255 : fillGreen % 256;
    // this.fillBlue = fillBlue == -1 ? 255 : fillBlue % 256;
    this.fillRed = fillRed == -1 ? -1 : fillRed % 256;
    this.fillGreen = fillGreen == -1 ? -1 : fillGreen % 256;
    this.fillBlue = fillBlue == -1 ? -1 : fillBlue % 256;
  }

  public void doubleClick(GC gc, Diagram diagram, int dx, int dy, int mouseX, int mouseY) {
    for (Display display : displays) {
      display.doubleClick(gc, diagram, dx + getX(), dy + getY(), mouseX, mouseY);
    }
  }

  public void editText(String id) {
    for (Display display : displays)
      display.editText(id);
  }

  public int getCurve() {
    return curve;
  }

  private Display getDisplay(String id) {
    for (Display d : displays)
      if (d.getId().equals(id)) return d;
    return null;
  }

  public int getFillBlue() {
    return fillBlue;
  }

  public int getFillGreen() {
    return fillGreen;
  }

  public int getFillRed() {
    return fillRed;
  }

  public int getHeight() {
    return height;
  }

  public String getId() {
    return id;
  }

  public int getLineBlue() {
    return lineBlue;
  }

  public int getLineGreen() {
    return lineGreen;
  }

  public int getLineRed() {
    return lineRed;
  }

  public int getWidth() {
    return width;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public boolean isBottom() {
    return bottom;
  }

  public boolean isLeft() {
    return left;
  }

  public boolean isRight() {
    return right;
  }

  public boolean isTop() {
    return top;
  }

  public void italicise(String id, boolean italics) {
    for (Display display : displays)
      display.italicise(id, italics);
  }

  public void move(String id, int x, int y) {
    if (getId().equals(id)) {
      this.x = x;
      this.y = y;
    } else {
      for (Display display : displays)
        display.move(id, x, y);
    }
  }

  public void newBox(String parentId, String id, int x, int y, int width, int height, int curve, boolean top, boolean right, boolean bottom, boolean left, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue) {
    if (getId().equals(parentId)) {
      Box box = new Box(id, x, y, width, height, curve, top, right, bottom, left, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue);
      displays.add(box);
    } else {
      for (Display display : displays) {
        display.newBox(parentId, id, x, y, width, height, curve, top, right, bottom, left, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue);
      }
    }
  }

  public void newNestedDiagram(String parentId, String id, int x, int y, int width, int height, org.eclipse.swt.widgets.Composite canvas) {
    if (getId().equals(parentId)) {
      DiagramClient.theClient().runOnDisplay(new Runnable() {
        public void run() {
          Diagram diagram = new Diagram(id, canvas, Box.this);
          DiagramClient.newlyCreatedDiagrams.add(diagram);
          Box.this.nestedDiagram = diagram;
        }
      });
    } else {
      for (Display display : displays) {
        display.newNestedDiagram(parentId, id, x, y, width, height, canvas);
      }
    }
  }

  public void newEllipse(String parentId, String id, int x, int y, int width, int height, boolean showOutline, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue) {
    if (parentId.equals(getId()))
      displays.add(new Ellipse(id, x, y, width, height, showOutline, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue));
    else {
      for (Display display : displays)
        display.newEllipse(parentId, id, x, y, width, height, showOutline, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue);
    }
  }

  public void newShape(String parentId, String id, int x, int y, int width, int height, boolean showOutline, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue, int[] points) {
    if (parentId.equals(getId()))
      displays.add(new Shape(id, x, y, width, height, showOutline, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue, points));
    else {
      for (Display display : displays)
        display.newShape(parentId, id, x, y, width, height, showOutline, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue, points);
    }
  }

  public void newMultilineText(String parentId, String id, String text, int x, int y, int width, int height, boolean editable, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue, String font) {
    if (getId().equals(parentId)) {
      MultilineText t = new MultilineText(id, text, x, y, width, height, editable, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue, font);
      displays.add(t);
    } else {
      for (Display d : displays)
        d.newMultilineText(parentId, id, text, x, y, width, height, editable, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue, font);
    }
  }

  public void newText(String parentId, String id, String text, int x, int y, boolean editable, boolean underline, boolean italicise, int red, int green, int blue) {
    if (parentId.equals(getId())) {
      Text t = new Text(id, text, x, y, editable, underline, italicise, red, green, blue);
      displays.add(t);
    } else {
      for (Display display : displays)
        display.newText(parentId, id, text, x, y, editable, underline, italicise, red, green, blue);
    }
  }

  public void newImage(String parentId, String id, String fileName, int x, int y, int width, int height) {
    if (parentId.equals(getId())) {
      displays.add(new Image(id, fileName, x, y, width, height));
    } else {
      for (Display display : displays)
        display.newImage(parentId, id, fileName, x, y, width, height);
    }
  }

  public void paint(GC gc, int x, int y) {
    if (width > 0 && height > 0) {
      Color fillColor = gc.getBackground();
      if (getFillRed() != -1 && getFillGreen() != -1 && getFillBlue() != -1) {
        gc.setBackground(new Color(XModeler.getXModeler().getDisplay(), getFillRed(), getFillGreen(), getFillBlue()));
        gc.fillRectangle(x + getX(), y + getY(), width, height);
      }
      gc.setBackground(fillColor);
      for (Display display : displays)
        display.paint(gc, x + getX(), y + getY());
      if (top || bottom || left || right) { // Bj�rn
        Color lineColor = gc.getForeground();
        gc.setForeground(new Color(XModeler.getXModeler().getDisplay(), getLineRed(), getLineGreen(), getLineBlue()));
        gc.drawRectangle(x + getX(), y + getY(), width, height);
        gc.setForeground(lineColor);
      }
    }
    if (nestedDiagram != null) nestedDiagram.paint(gc, x + getX(), y + getY());
  }

  public void paintHover(GC gc, int x, int y, int dx, int dy) {
    for (Display display : displays)
      display.paintHover(gc, x, y, dx + getX(), dy + getY());
  }

  public void remove(String id) {
    Display d = getDisplay(id);
    if (d != null) {
      displays.remove(d);
    } else {
      for (Display display : displays) {
        display.remove(id);
      }
    }
  }

  public void resize(String id, int width, int height) {
    if (id.equals(getId())) {
      this.width = width;
      this.height = height;
    } else {
      for (Display display : displays)
        display.resize(id, width, height);
    }
  }

  public void setFillColor(String id, int red, int green, int blue) {
    if (id.equals(getId())) {
      // fillRed = red == -1 ? 255 : red % 256;
      // fillGreen = green == -1 ? 255 : green % 256;
      // fillBlue = blue == -1 ? 255 : blue % 256;
      fillRed = red == -1 ? -1 : red % 256;
      fillGreen = green == -1 ? -1 : green % 256;
      fillBlue = blue == -1 ? -1 : blue % 256;
    } else for (Display display : displays)
      display.setFillColor(id, red, green, blue);
  }

  public void setText(String id, String text) {
    for (Display display : displays)
      display.setText(id, text);
  }

  public void showEdges(String id, boolean top, boolean bottom, boolean left, boolean right) { // Bj�rn
    if (id.equals(getId())) {
      this.top = top;
      this.bottom = bottom;
      this.left = left;
      this.right = right;
    } else for (Display display : displays)
      display.showEdges(id, top, bottom, right, left);
  }

  public String toString() {
    return "Box(" + id + "," + x + "," + y + "," + width + "," + height + "," + displays + ")";
  }

  public void writeXML(PrintStream out) {
    out.print("<Box ");
    out.print("id='" + getId() + "' ");
    out.print("x='" + getX() + "' ");
    out.print("y='" + getY() + "' ");
    out.print("width='" + getWidth() + "' ");
    out.print("height='" + getHeight() + "' ");
    out.print("curve='" + getCurve() + "' ");
    out.print("top='" + isTop() + "' ");
    out.print("right='" + isRight() + "' ");
    out.print("bottom='" + isBottom() + "' ");
    out.print("left='" + isLeft() + "' ");
    out.print("lineRed='" + getLineRed() + "' ");
    out.print("lineGreen='" + getLineGreen() + "' ");
    out.print("lineBlue='" + getLineBlue() + "' ");
    out.print("fillRed='" + getFillRed() + "' ");
    out.print("fillGreen='" + getFillGreen() + "' ");
    out.print("fillBlue='" + getFillBlue() + "'>");
    for (Display display : displays)
      display.writeXML(out);
    out.print("</Box>");
  }

  public void setFont(String id, String fontData) {
    for (Display display : displays)
      display.setFont(id, fontData);
  }

  public void setEditable(String id, boolean editable) {
    for (Display display : displays)
      display.setEditable(id, editable);

  }

}
