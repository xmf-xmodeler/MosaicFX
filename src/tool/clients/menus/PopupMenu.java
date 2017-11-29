package tool.clients.menus;

import java.io.PrintStream;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;

import tool.xmodeler.XModeler;

public class PopupMenu extends PopupMenuItemContainer {

  public Menu popup(String id) {
    Menu menu = new Menu(XModeler.getXModeler(), SWT.POP_UP);
    for (PopupMenuItem child : getChildren())
      child.popup(menu, id);
    return menu;
  }

  public void writeXML(PrintStream out) {
    out.print("<Popup id='" + getId() + "'>");
    for (PopupMenuItem item : getChildren())
      item.writeXML(out);
    out.print("</Popup>");
  }

}
