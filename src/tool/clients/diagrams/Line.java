package tool.clients.diagrams;

import java.io.PrintStream;

import org.eclipse.swt.graphics.GC;

public class Line implements Display {

  public static final int SOLID_LINE        = 1;
  public static final int DASH_LINE         = 2;
  public static final int DOTTED_LINE       = 3;
  public static final int DASH_DOTTED_LINE  = 4;
  public static final int DASH_DOT_DOT_LINE = 5;

  @Override
  public String getId() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void paint(javafx.scene.canvas.GraphicsContext gc, int x, int y) {
	  
  }
  
  @Override @Deprecated
  public void paint(GC gc, int x, int y) {
    // TODO Auto-generated method stub

  }

  @Override
  public void newText(String parentId, String id, String text, int x, int y, boolean editable, boolean underline, boolean italicise, int red, int green, int blue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void newBox(String parentId, String id, int x, int y, int width, int height, int curve, boolean top, boolean right, boolean bottom, boolean left, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void resize(String id, int width, int height) {
    // TODO Auto-generated method stub

  }

  @Override
  public void editText(String id) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setText(String id, String text) {
    // TODO Auto-generated method stub

  }

  @Override
  public void move(String id, int x, int y) {
    // TODO Auto-generated method stub

  }

  @Override
  public void paintHover(GC gc, int x, int y, int dx, int dy) {
    // TODO Auto-generated method stub

  }

  @Override
  public void remove(String id) {
    // TODO Auto-generated method stub

  }

  @Override
  public void doubleClick(GC gc, Diagram diagram, int dx, int dy, int mouseX, int mouseY) {
    // TODO Auto-generated method stub

  }

  @Override
  public void writeXML(PrintStream out) {
    // TODO Auto-generated method stub

  }

  @Override
  public void newMultilineText(String parentId, String id, String text, int x, int y, int width, int height, boolean editable, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue, String font) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setFillColor(String id, int red, int green, int blue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void italicise(String id, boolean italics) {
    // TODO Auto-generated method stub

  }

  public void newEllipse(String parentId, String id, int x, int y, int width, int height, boolean showOutline, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue) {

  }

  public void newImage(String parentId, String id, String fileName, int x, int y, int width, int height) {

  }

  public void setFont(String id, String fontData) {
  }

@Override
public void setEditable(String id, boolean editable) {
	// TODO Auto-generated method stub
	
}

@Override
public void showEdges(String id, boolean top, boolean bottom, boolean left,
		boolean right) {
	// TODO Auto-generated method stub
	
}
public void newNestedDiagram(String parentId, String id, int x, int y, int width, int height, javafx.scene.canvas.Canvas canvas) {}

@Override
public void newShape(String parentId, String id, int x, int y, int width, int height, boolean showOutline, int lineRed,
		int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue, int[] points) {
	// TODO Auto-generated method stub
	
}

}
