package tool.clients.menus;

import java.util.Vector;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class PopupMenuItemContainer {

  String                id;
  Vector<PopupMenuItem> children = new Vector<PopupMenuItem>();

  public Vector<PopupMenuItem> getChildren() {
    return children;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void addChild(String id, PopupMenuItem child) {
    if (id.equals(getId()))
      children.add(child);
    else {
      for (PopupMenuItem p : children)
        p.addChild(id, child);
    }
  }

  public String toString() {
    return getClass().getName() + "(" + getId() + "," + getChildren() + ")";
  }
}
