package tool.clients.forms;

import java.io.PrintStream;
import java.util.Hashtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;

import tool.xmodeler.XModeler;

public class List implements MouseListener {

  String                       id;
  org.eclipse.swt.widgets.List list;
  Hashtable<String, String>    items = new Hashtable<String, String>();

  public List(String id, Composite parent, int x, int y, int width, int height) {
    this.id = id;
    list = new org.eclipse.swt.widgets.List(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    list.setLocation(x, y);
    list.setSize(width, height);
    list.setFont(FormsClient.formTextFieldFont);
    list.addMouseListener(this);
  }

  public void add(String id, String value) {
    list.add(value);
    items.put(id, value);
  }

  public String getId() {
    return id;
  }

  public void writeXML(PrintStream out) {
    out.print("<List id='" + getId() + "'");
    out.print(" x='" + list.getLocation().x + "'");
    out.print(" y='" + list.getLocation().y + "'");
    out.print(" width='" + list.getSize().x + "'");
    out.print(" height='" + list.getSize().y + "'>");
    for (String id : items.keySet())
      out.print("<Item id='" + id + "' value='" + XModeler.encodeXmlAttribute(items.get(id)) + "'/>");
    out.print("</List>");
  }

  public void clear() {
    list.removeAll();
    items.clear();
  }

  public void mouseDoubleClick(MouseEvent arg0) {
    if (list.getSelectionCount() == 1) {
      String id = getId(list.getSelection()[0]);
      FormsClient.theClient().doubleClick(id);
    }
  }

  private String getId(String string) {
    for (String id : items.keySet())
      if (items.get(id).equals(string)) return id;
    return null;
  }

  @Override
  public void mouseDown(MouseEvent arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseUp(MouseEvent arg0) {
    // TODO Auto-generated method stub

  }

}
