package tool.clients.menus;

import java.io.PrintStream;

//import org.eclipse.swt.SWT;
//import org.eclipse.swt.widgets.Event;
//import org.eclipse.swt.widgets.Listener;
//import org.eclipse.swt.widgets.Menu;
//import org.eclipse.swt.widgets.MenuItem;

import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import tool.clients.EventHandler;
//import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class PopupMenuItem extends PopupMenuItemContainer {

  String  name;
  String  keyBinding;
  boolean supportsMulti;
  String  handlerPointIdentity;

  public void writeXML(PrintStream out) {
    out.print("<Item id='" + getId() + "' name='" + getName() + "' keyBinding='" + getKeyBinding() + "' supportsMulti='" + isSupportsMulti() + "' handlerPointIdentity='" + getHandlerPointIdentity() + "'>");
    for (PopupMenuItem item : getChildren())
      item.writeXML(out);
    out.print("</Item>");
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getKeyBinding() {
    return keyBinding;
  }

  public void setKeyBinding(String keyBinding) {
    this.keyBinding = keyBinding;
  }

  public boolean isSupportsMulti() {
    return supportsMulti;
  }

  public void setSupportsMulti(boolean supportsMulti) {
    this.supportsMulti = supportsMulti;
  }

  public String getHandlerPointIdentity() {
    return handlerPointIdentity;
  }

  public void setHandlerPointIdentity(String handlerPointIdentity) {
    this.handlerPointIdentity = handlerPointIdentity;
  }

//  public MenuItem popup(Menu menu, final String id) {
//   
//	  MenuItem item = null;
//	  if (!getChildren().isEmpty()) {
//		  item = new Menu(getName());
//	  }else{
//		  item = new MenuItem(getName());
//	  }
	  //MenuItem item = new MenuItem(menu, SWT.CASCADE);
//    item.setText(getName());
//    item.addListener(SWT.Selection, new Listener() {
//      public void handleEvent(Event e) {
//        // This need to be generalized for multiple selected items...
//        EventHandler handler = MenuClient.theClient().getHandler();
//        Message m = handler.newMessage("rightClickMenuSelected", 1);
//        Value[] pairs = new Value[2];
//        pairs[0] = new Value(getId());
//        pairs[1] = new Value(id);
//        m.args[0] = new Value(pairs);
//        handler.raiseEvent(m);
//      }
//    });
//    if (!getChildren().isEmpty()) {
//      Menu subMenu = new Menu(XModeler.getXModeler(), SWT.DROP_DOWN);
//      item.setMenu(subMenu);
//      for (PopupMenuItem child : getChildren())
//        child.popup(subMenu, id);
//    }
//    return item;
//  }

  public MenuItem popup(final String id) {
	  MenuItem item;
	  if (!getChildren().isEmpty()) {
		  item = new Menu(getName());
	  }else{
		  item = new MenuItem(getName());
	  }
	  //MenuItem item = new MenuItem(menu, SWT.CASCADE);
//    item.setText(getName());
	  item.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			EventHandler handler = MenuClient.theClient().getHandler();
	        Message m = handler.newMessage("rightClickMenuSelected", 1);
	        Value[] pairs = new Value[2];
	        pairs[0] = new Value(getId());
	        pairs[1] = new Value(id);
	        m.args[0] = new Value(pairs);
	        handler.raiseEvent(m);
		}
	});
//    item.addListener(SWT.Selection, new Listener() {
//      public void handleEvent(Event e) {
//        // This need to be generalized for multiple selected items...
//        EventHandler handler = MenuClient.theClient().getHandler();
//        Message m = handler.newMessage("rightClickMenuSelected", 1);
//        Value[] pairs = new Value[2];
//        pairs[0] = new Value(getId());
//        pairs[1] = new Value(id);
//        m.args[0] = new Value(pairs);
//        handler.raiseEvent(m);
//      }
//    });
    if (!getChildren().isEmpty()) {
//      Menu subMenu = new Menu(XModeler.getXModeler(), SWT.DROP_DOWN);
//      item.setMenu(subMenu);
      for (PopupMenuItem child : getChildren()){
    	  Menu menu = (Menu) item;
    	  menu.getItems().add(child.popup(id));
      }
    }
    return item;
  }
}
