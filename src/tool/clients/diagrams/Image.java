package tool.clients.diagrams;

import java.io.PrintStream;

import org.eclipse.jface.resource.ImageDescriptor;

import javafx.scene.canvas.GraphicsContext;
import tool.xmodeler.XModeler;

public class Image implements Display {

  String                         id;
  String                         fileName;
  int                            x;
  int                            y;
  int                            width;
  int                            height;
  javafx.scene.image.Image imageFX = null;

  public Image(String id, String fileName, int x, int y, int width, int height) {
    super();
    this.id = id;
    this.fileName = fileName;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public String getId() {
    return id;
  }

  public String getFileName() {
    return fileName;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public javafx.scene.image.Image getImage() {
    return imageFX;
  }

  public void ensureImage() {
    if (imageFX == null) {
      imageFX = new javafx.scene.image.Image(fileName);
//    		  ImageDescriptor.createFromFile(null, fileName).createImage();
//      resizeImage();
    }
  }

//  public void resizeImage() {
////	  image = image.
////    ImageData data = image.getImageData().scaledTo(width, height);
//////    image.dispose();
////    image = new org.eclipse.swt.graphics.Image(null, data);
//  }

	@Override
	public void paint(GraphicsContext gc, int x, int y) {
		ensureImage();
		gc.drawImage(imageFX, x + getX(), y + getY());
	}
  
//  @Override @Deprecated
//  public void paint(GC gc, int x, int y) {
//    ensureImage();
//    gc.drawImage(image, x + getX(), y + getY());
//  }

  public void newText(String parentId, String id, String text, int x, int y, boolean editable, boolean underline, boolean italicise, int red, int green, int blue) {

  }

  public void newBox(String parentId, String id, int x, int y, int width, int height, int curve, boolean top, boolean right, boolean bottom, boolean left, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue) {

  }

  public void newNestedDiagram(String parentId, String id, int x, int y, int width, int height, javafx.scene.canvas.Canvas canvas) {}

  public void resize(String id, int width, int height) {
    if (getId().equals(id)) {
      this.width = width;
      this.height = height;
//      resizeImage();
    }
  }

  public void editText(String id) {

  }

  public void setText(String id, String text) {

  }

  public void move(String id, int x, int y) {
    if (getId().equals(id)) {
      this.x = x;
      this.y = y;
    }
  }

  @Override
  public void paintHover(GraphicsContext gc, int x, int y, int dx, int dy) {}

  public void remove(String id) {

  }

  @Override
  public void doubleClick(GraphicsContext gc, Diagram diagram, int dx, int dy, int mouseX, int mouseY) {}

  public void writeXML(PrintStream out) {
    out.print("<Image id='" + id + "'");
    out.print(" fileName='" + XModeler.encodeXmlAttribute(fileName) + "'");
    out.print(" x='" + x + "'");
    out.print(" y='" + y + "'");
    out.print(" width='" + width + "'");
    out.print(" height='" + height + "'/>");
  }

  public void newMultilineText(String parentId, String id, String text, int x, int y, int width, int height, boolean editable, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue, String font) {

  }

  public void setFillColor(String id, int red, int green, int blue) {

  }

  public void italicise(String id, boolean italics) {

  }

  public void newEllipse(String parentId, String id, int x, int y, int width, int height, boolean showOutline, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue) {

  }

  public void newImage(String parentId, String id, String fileName, int x, int y, int width, int height) {

  }

  public void setFont(String id, String fontData) {
  }

public void setEditable(String id, boolean editable) {
	
}

public void showEdges(String id, boolean top, boolean bottom, boolean left,
		boolean right) {
	
}

public void newShape(String parentId, String id, int x, int y, int width, int height, boolean showOutline, int lineRed,
		int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue, int[] points) {
	
}

}
