package tool.clients.menus;

import java.io.PrintStream;
//import java.util.Vector;

import javafx.scene.control.ContextMenu;
//import javafx.scene.control.Menu;

//import org.eclipse.swt.SWT;
//import org.eclipse.swt.widgets.Menu;

//import tool.xmodeler.XModeler;

public class PopupMenu extends PopupMenuItemContainer {

  public ContextMenu popup(String id) {
    ContextMenu menu = new ContextMenu();//XModeler.getXModeler(), SWT.POP_UP);
    for (PopupMenuItem child : getChildren())
//      child.popup(menu, id);
       menu.getItems().add(child.popup(id));
    return menu;
  }

  public void writeXML(PrintStream out) {
    out.print("<Popup id='" + getId() + "'>");
    for (PopupMenuItem item : getChildren())
      item.writeXML(out);
    out.print("</Popup>");
  }

}
