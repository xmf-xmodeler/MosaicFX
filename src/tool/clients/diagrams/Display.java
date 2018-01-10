package tool.clients.diagrams;

import java.io.PrintStream;

import org.eclipse.swt.graphics.GC;
import javafx.scene.canvas.GraphicsContext;

public interface Display {

  String getId();

  void paint(GraphicsContext gc, int x, int y);
  
  void paintHover(GraphicsContext gc, int x, int y, int dx, int dy);
  
  void doubleClick(GraphicsContext gc, Diagram diagram, int dx, int dy, int mouseX, int mouseY);

  void newText(String parentId, String id, String text, int x, int y, boolean editable, boolean underline, boolean italicise, int red, int green, int blue);

  void newBox(String parentId, String id, int x, int y, int width, int height, int curve, boolean top, boolean right, boolean bottom, boolean left, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue);
  
  void newNestedDiagram(String parentId, String id, int x, int y, int width, int height, javafx.scene.canvas.Canvas canvas);
  
  void resize(String id, int width, int height);

  void editText(String id);

  void setText(String id, String text);

  void move(String id, int x, int y);

  void remove(String id);

  void writeXML(PrintStream out);

  void newMultilineText(String parentId, String id, String text, int x, int y, int width, int height, boolean editable, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue, String font);

  void setFillColor(String id, int red, int green, int blue);

  void italicise(String id, boolean italics);

  void newEllipse(String parentId, String id, int x, int y, int width, int height, boolean showOutline, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue);
  
  void newShape(String parentId, String id, int x, int y, int width, int height, boolean showOutline, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue, int[] points);

  void newImage(String parentId, String id, String fileName, int x, int y, int width, int height);

  void setFont(String id, String fontData);

  void setEditable(String id, boolean editable);
  
  void showEdges(String id, boolean top,boolean bottom,boolean left, boolean right);
}
