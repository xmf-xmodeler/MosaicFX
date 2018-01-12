package tool.clients.diagrams;

import java.io.PrintStream;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Font;
import tool.clients.dialogs.notifier.NotificationType;
import tool.clients.dialogs.notifier.NotifierDialog;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class Text implements Display {

  String  id;
  String  text;
  int     x;
  int     y;
  boolean editable;
  boolean underline;
  boolean italicise;
  int     red;
  int     green;
  int     blue;
  String  fontData = "";
  Font    font     = null;

  public Text(String id, String text, int x, int y, boolean editable, boolean underline, boolean italicise, int red, int green, int blue) {
    super();
    this.id = id;
    this.text = text;
    this.x = x;
    this.y = y;
    this.editable = editable;
    this.underline = underline;
    this.italicise = italicise;
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  public boolean contains(int x, int y) {
    return x >= getX() && y >= getY() && x <= getX() + getWidth() && y <= getY() + getHeight();
  }

  
	@Override
	public void doubleClick(GraphicsContext gc, Diagram diagram, int dx, int dy, int mouseX, int mouseY) {
		if (editable && contains(mouseX - dx, mouseY - dy)) {
		    System.err.println("current Thread: " + Thread.currentThread() + " (in doubleClick)");
			// TextField inputField = new TextField(this.text);
			final TextInputDialog input = new TextInputDialog(this.text);
			input.initOwner(XModeler.getStage());
			// input.setTitle("");
			input.setContentText("Enter new Value:");
			input.setHeaderText(null);
			Optional<String> result = input.showAndWait();
			if (result.isPresent()) {
//				CountDownLatch l = new CountDownLatch(1);
//				Platform.runLater(() -> {
					textChangedEvent(result.get());
//					l.countDown();
//				});
//				try {
//					l.await();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			}
		}
        
    	
//   System.err.println("Trying to edit Text");
//      final org.eclipse.swt.widgets.Text text = new org.eclipse.swt.widgets.Text(diagram.getCanvas(), SWT.BORDER);
////      text.setFont(DiagramClient.diagramFont);
//	  Font baseFont = italicise ? DiagramClient.diagramItalicFontFX : DiagramClient.diagramFontFX;
//	  FontDescriptor myDescriptor = FontDescriptor.createFrom(baseFont).setHeight(12);// * 100 / XModeler.getDeviceZoomPercent());
//	  Font zoomFont = myDescriptor.createFont(XModeler.getXModeler().getDisplay());
//	  text.setFont(zoomFont);
//      text.setText(this.text);
//      Point p = diagram.scaleinv(dx + getX(), dy + getY());
//      text.setLocation(p.x, p.y);
//      text.setSize(getWidth() + 10, getHeight() + 10);
//      text.setVisible(true);
//      text.selectAll();
//      //text.setFocus(); - done delayed to not loose focus on Linux, see below
//      NotifierDialog.notify("Edit Text", "Type text then RET to update.\nType ESC to cancel.", NotificationType.values()[3]);
//      Listener listener = new Listener() {
//        public void handleEvent(Event event) {
//          org.eclipse.swt.widgets.Text t;
//          switch (event.type) {
//          case SWT.FocusOut:
//			t = (org.eclipse.swt.widgets.Text) event.widget;
//			t.setVisible(false);
//			t.dispose();
//			diagram.redraw();
//            break;
//          case SWT.Verify:
//            t = (org.eclipse.swt.widgets.Text) event.widget;
//            GC gc = new GC(t);
//            Point size = gc.textExtent(t.getText() + event.text);
//            t.setSize(size.x + 10, getHeight() + 10);
//            break;
//          case SWT.Traverse:
//            switch (event.detail) {
//            case SWT.TRAVERSE_RETURN:
//              t = (org.eclipse.swt.widgets.Text) event.widget;
//              textChangedEvent(t.getText());
//              t.setVisible(false);
//              t.dispose();
//              diagram.redraw();
//              event.doit = false;
//              break;
//            case SWT.TRAVERSE_ESCAPE:
//              t = (org.eclipse.swt.widgets.Text) event.widget;
//              t.setVisible(false);
//              t.dispose();
//              diagram.redraw();
//              event.doit = false;
//              break;
//            }
//            break;
//          }
//        }
//      };
//      text.addListener(SWT.FocusOut, listener);
//      text.addListener(SWT.Verify, listener);
//      text.addListener(SWT.Traverse, listener);
//
//      XModeler.getXModeler().getDisplay().timerExec(100, new Runnable() {
//          public void run() {
//          	  text.setFocus();
//          }
//      });
//    }	  
  }
  
//  @Override @Deprecated
//  public void doubleClick(GC gc, final Diagram diagram, int dx, int dy, int mouseX, int mouseY) {
//	  System.err.println("Cannot doubleclick Text yet");
//
//  }

  public void editText(String id) {
    if (id.equals(getId())) editable = true;
  }

  public int getBlue() {
    return blue;
  }

  public int getGreen() {
    return green;
  }

  public Font getFont() {
    if (font == null) {
      if (fontData.equals("")) {
        return DiagramClient.diagramFontFX;
      } else {
        font = new Font(fontData,12);
        return font;
      }
    } else return font;
  }

  public int getHeight() {
//	System.err.println("Calculating font size without font");
	javafx.geometry.Point2D extent = DiagramClient.theClient().textDimension(text, null/*getFont()*/);
    return (int) extent.getY();// * 100 / XModeler.getDeviceZoomPercent();
  }

  public String getId() {
    return id;
  }

  public int getRed() {
    return red;
  }

  public String getText() {
    return text;
  }

  public int getWidth() {
//	System.err.println("Calculating font size without font");
	javafx.geometry.Point2D extent = DiagramClient.theClient().textDimension(text, null/*getFont()*/);
    return (int) extent.getX();// * 100 / XModeler.getDeviceZoomPercent();
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public boolean isEditable() {
    return editable;
  }

  public boolean isItalicise() {
    return italicise;
  }

  public boolean isUnderline() {
    return underline;
  }

  public void italicise(String id, boolean italics) {
    if (id.equals(getId())) italicise = italics;
  }

  public void move(String id, int x, int y) {
    if (getId().equals(id)) {
      this.x = x;
      this.y = y;
    }
  }

  public void newBox(String parentId, String id, int x, int y, int width, int height, int curve, boolean top, boolean right, boolean bottom, boolean left, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue) {
  }

  public void newMultilineText(String parentId, String id, String text, int x, int y, int width, int height, boolean editable, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue, String font) {
  }

  public void newText(String parentId, String id, String text, int x, int y, boolean editable, boolean underline, boolean italicise, int red, int green, int blue) {
  }
  
  public void newNestedDiagram(String parentId, String id, int x, int y, int width, int height, javafx.scene.canvas.Canvas canvas) {}

  @Override
  public void paint(javafx.scene.canvas.GraphicsContext gc, int x, int y) {
//	  System.err.println("paint Text: " + text + "@" + x +","+ y);
//	    Font font = gc.getFont();
//	    Color c = gc.getForeground(); 
//	    gc.setFont(italicise ? DiagramClient.diagramItalicFont : DiagramClient.diagramFont);
	    //Check if a color is set
	    if(getRed() >=0 && getGreen() >= 0 && getBlue() >= 0){ 
	    	gc.setFill(new javafx.scene.paint.Color(getRed()/255., getGreen()/255., getBlue()/255., 1));
	    } else {
	    	gc.setFill(javafx.scene.paint.Color.BLACK);
	    }
	    gc.fillText(text, x + getX(), y + getY() + getHeight());
//	    gc.setFont(font);
//	    gc.setForeground(c); 
  }

  @Override
  public void paintHover(GraphicsContext gc, int x, int y, int dx, int dy) {
	if (editable && contains(x - dx, y - dy)) paintSelectableOutline(gc, dx, dy);
  }

  private void paintSelectableOutline(GraphicsContext gc, int dx, int dy) {
//    Color c = gc.getForeground();
    gc.setStroke(Diagram.GREY);
    gc.strokeRect(dx + getX(), dy + getY(), getWidth(), getHeight());
//    gc.setForeground(c);
  }

  public void remove(String id) {
  }

  public void resize(String id, int width, int height) {
  }

  public void setFillColor(String id, int red, int green, int blue) {
	  //Bj�rn
	  if (id.equals(getId())){
		this.red = red;  
		this.green = green;
		this.blue = blue;
	  }
  }  

  public void setText(String text) {
    this.text = text;
  }

  public void setText(String id, String text) {
    if (id.equals(getId())) this.text = text;
  }

  public void textChangedEvent(String text) {
    Message message = DiagramClient.theClient().getHandler().newMessage("textChanged", 2);
    message.args[0] = new Value(id);
    message.args[1] = new Value(text);
    //System.out.println("textChanged: " + message);
    System.err.println("current Thread: " + Thread.currentThread() + " (in textChangedEvent)");
    DiagramClient.theClient().getHandler().raiseEvent(message);
  }

  public String toString() {
    return "Text(" + id + "," + x + "," + y + "," + text + ")";
  }

  public void writeXML(PrintStream out) {
    out.print("<Text ");
    out.print("id='" + getId() + "' ");
    out.print("text='" + XModeler.encodeXmlAttribute(getText()) + "' ");
    out.print("x='" + getX() + "' ");
    out.print("y='" + getY() + "' ");
    out.print("editable='" + isEditable() + "' ");
    out.print("underline='" + isUnderline() + "' ");
    out.print("italicise='" + isItalicise() + "' ");
    out.print("red='" + getRed() + "' ");
    out.print("green='" + getGreen() + "' ");
    out.print("blue='" + getBlue() + "'/>");
  }

  public void newEllipse(String parentId, String id, int x, int y, int width, int height, boolean showOutline, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue) {

  }

  public void newImage(String parentId, String id, String fileName, int x, int y, int width, int height) {

  }

  public void setFont(String id, String fontData) {
    if (getId().equals(id)) {
      this.fontData = fontData;
      font = null;
    }
  }
  
  public void setEditable(String id, boolean editable){
	  if (getId().equals(id)) {
	      this.editable = editable;
	      
	   }  
  }

@Override
public void showEdges(String id, boolean top, boolean bottom, boolean left,
		boolean right) {
	// TODO Auto-generated method stub
	
}

@Override
public void newShape(String parentId, String id, int x, int y, int width, int height, boolean showOutline, int lineRed,
		int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue, int[] points) {
	// TODO Auto-generated method stub
	
}


}
