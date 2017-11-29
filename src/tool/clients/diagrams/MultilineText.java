package tool.clients.diagrams;

import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import tool.clients.dialogs.notifier.NotificationType;
import tool.clients.dialogs.notifier.NotifierDialog;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class MultilineText implements Display {

  final static int INDENT = 2;

  public MultilineText(String id, String text, int x, int y, int width, int height, boolean editable, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue, String font) {
    super();
    this.id = id;
    this.text = text;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.editable = editable;
    this.lineRed = lineRed;
    this.lineGreen = lineGreen;
    this.lineBlue = lineBlue;
    this.fillRed = fillRed;
    this.fillGreen = fillGreen;
    this.fillBlue = fillBlue;
    this.font = font;
  }

  String  id;
  String  text;
  int     x;
  int     y;
  int     width;
  int     height;
  boolean editable;
  int     lineRed;
  int     lineGreen;
  int     lineBlue;
  int     fillRed;
  int     fillGreen;
  int     fillBlue;
  String  font;

  public String toString() {
    return "MultilineText(" + x + "," + y + "," + width + "," + height + "," + text + ")";
  }

  public void paint(GC gc, int parentX, int parentY) { 
    //FontData fontData = font.equals("") ? DiagramClient.diagramFont.getFontData()[0] : new FontData(font);
    FontData fontData = DiagramClient.diagramFont.getFontData()[0];
    Font font = gc.getFont();
    gc.setFont(DiagramClient.diagramFont);
    int fontHeight = fontData.getHeight() + 5;
    int x = this.x + parentX + INDENT;
    int y = this.y + parentY + INDENT;
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (x + gc.getCharWidth(c) > this.x + parentX + width || c == '\n' || c == '\r') {
        x = this.x + parentX + INDENT;
        y = y + fontHeight;
      }
      if (!(y + fontHeight > this.y + height + parentY) && (c != '\n' || c == '\r')) {
        gc.drawString(c + "", x, y, true);
        x += gc.getCharWidth(c);
      }
    }
    gc.setFont(font);
  }

  public String getId() {
    return id;
  }

  public String getText() {
    return text;
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

  public boolean isEditable() {
    return editable;
  }

  public int getLineRed() {
    return lineRed;
  }

  public int getLineGreen() {
    return lineGreen;
  }

  public int getLineBlue() {
    return lineBlue;
  }

  public int getFillRed() {
    return fillRed;
  }

  public int getFillGreen() {
    return fillGreen;
  }

  public int getFillBlue() {
    return fillBlue;
  }

  public String getFont() {
    return font;
  }

  public void newText(String parentId, String id, String text, int x, int y, boolean editable, boolean underline, boolean italicise, int red, int green, int blue) {
  }

  public void newBox(String parentId, String id, int x, int y, int width, int height, int curve, boolean top, boolean right, boolean bottom, boolean left, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue) {
  }
  
  public void newNestedDiagram(String parentId, String id, int x, int y, int width, int height, org.eclipse.swt.widgets.Composite canvas) {}

  public void resize(String id, int width, int height) {
    if (id.equals(getId())) {
      this.width = width;
      this.height = height;
    }
  }

  public void editText(String id) {
    if (id.equals(getId())) editable = true;
  }

  public void setText(String id, String text) {
    if (id.equals(getId())) this.text = text;
  }

  public void move(String id, int x, int y) {
    if (getId().equals(id)) {
      this.x = x;
      this.y = y;
    }
  }

  public void paintHover(GC gc, int x, int y, int dx, int dy) {
  }

  public void remove(String id) {
  }

  public void doubleClick(GC gc, final Diagram diagram, int dx, int dy, int mouseX, int mouseY) {
    int x = this.x + dx;
    int y = this.y + dy;
    if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
      final org.eclipse.swt.widgets.Text text = new org.eclipse.swt.widgets.Text(diagram.getCanvas(), SWT.BORDER | SWT.MULTI);
      text.setFont(DiagramClient.diagramFont);
      text.setText(this.text);
      text.setLocation(dx + getX(), dy + getY());
      text.setSize(getWidth() + 10, getHeight() + 10);
      text.setVisible(true);
      text.setFocus();
      NotifierDialog.notify("Edit Text", "Type text then TAB to update.\nType ESC to cancel.", NotificationType.values()[3]);
      Listener listener = new Listener() {
        public void handleEvent(Event event) {
          org.eclipse.swt.widgets.Text t;
          switch (event.type) {
          case SWT.FocusOut:
            t = (org.eclipse.swt.widgets.Text) event.widget;
            t.setVisible(false);
            t.dispose();
            diagram.redraw();
            break;
          case SWT.Verify:
            t = (org.eclipse.swt.widgets.Text) event.widget;
            GC gc = new GC(t);
            Point size = gc.textExtent(t.getText() + event.text);
            t.setSize(size.x + 10, getHeight() + 10);
            break;
          case SWT.Traverse:
            switch (event.detail) {
            case SWT.TRAVERSE_TAB_NEXT:
              t = (org.eclipse.swt.widgets.Text) event.widget;
              textChangedEvent(t.getText());
              t.setVisible(false);
              t.dispose();
              diagram.redraw();
              event.doit = false;
              break;
            case SWT.TRAVERSE_ESCAPE:
              t = (org.eclipse.swt.widgets.Text) event.widget;
              t.setVisible(false);
              t.dispose();
              diagram.redraw();
              event.doit = false;
              break;
            }
            break;
          }
        }
      };
      text.addListener(SWT.FocusOut, listener);
      text.addListener(SWT.Verify, listener);
      text.addListener(SWT.Traverse, listener);
    }
  }

  public void textChangedEvent(String text) {
    Message message = DiagramClient.theClient().getHandler().newMessage("textChanged", 2);
    message.args[0] = new Value(id);
    message.args[1] = new Value(text);
    DiagramClient.theClient().getHandler().raiseEvent(message);
  }

  public void writeXML(PrintStream out) {
    out.print("<MultilineText id='" + getId() + "'");
    out.print(" text='" + XModeler.encodeXmlAttribute(text) + "'");
    out.print(" x='" + x + "'");
    out.print(" y='" + y + "'");
    out.print(" width='" + width + "'");
    out.print(" height='" + height + "'");
    out.print(" editable='" + editable + "'");
    out.print(" lineRed='" + lineRed + "'");
    out.print(" lineGreen='" + lineGreen + "'");
    out.print(" lineBlue='" + lineBlue + "'");
    out.print(" fillRed='" + fillRed + "'");
    out.print(" fillGreen='" + fillGreen + "'");
    out.print(" fillBlue='" + fillBlue + "'");
    out.print(" font='" + font + "'/>");
  }

  public void newMultilineText(String parentId, String id, String text, int x, int y, int width, int height, boolean editable, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue, String font) {
  }

  public void setFillColor(String id, int red, int green, int blue) {
    if (id.equals(getId())) {
      fillRed = red;
      fillGreen = green;
      fillBlue = blue;
    }
  }

  public void italicise(String id, boolean italics) {

  }

  public void newEllipse(String parentId, String id, int x, int y, int width, int height, boolean showOutline, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue) {

  }

  public void newImage(String parentId, String id, String fileName, int x, int y, int width, int height) {

  }

  public void setFont(String id, String fontData) {
    if (getId().equals(id)) font = fontData;
  }

@Override
public void setEditable(String id, boolean editable) {
	// TODO check, if it works
	if (getId().equals(id)) this.editable = editable;
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
