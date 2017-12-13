package tool.clients.menus;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Menu;
//import org.eclipse.swt.widgets.MenuItem;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
//import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import tool.clients.Client;
import tool.clients.EventHandler;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class MenuClient extends Client implements javafx.event.EventHandler<ActionEvent>{//implements SelectionListener {

  static MenuClient                   theClient;
  static Hashtable<String, Menu>      menus            = new Hashtable<String, Menu>();
  static Hashtable<String, MenuItem>  items            = new Hashtable<String, MenuItem>();
  static Hashtable<String, PopupMenu> popups           = new Hashtable<String, PopupMenu>();
  static Hashtable<String, PopupMenu> popupAssignments = new Hashtable<String, PopupMenu>();
  static Hashtable<Menu, String>      menuSharingTable = new Hashtable<Menu, String>();

  public MenuClient() {
    super("com.ceteva.menus");
    theClient = this;
  }

  public static MenuClient theClient() {
    return theClient;
  }

  public void writeXML(PrintStream out) {
    menuSharingTable.clear();
    out.print("<Menus>");
    writeMenuBar(out);
    writePopups(out);
    out.print("</Menus>");
  }

  private void writePopups(PrintStream out) {
    for (PopupMenu popup : popups.values())
      popup.writeXML(out);
    for (String id : popupAssignments.keySet()) {
      out.print("<GlobalMenu id='" + id + "' popupId='" + getId(popupAssignments.get(id)) + "'/>");
    }
  }

  private String getId(PopupMenu popupMenu) {
    for (String id : popups.keySet())
      if (popups.get(id) == popupMenu) return id;
    return "?";
  }

  private void writeMenuBar(PrintStream out) {
    out.print("<MenuBar>");
    for (String id : menus.keySet()) {
      if (isRootMenu(menus.get(id))) writeMenu(id, menus.get(id), rootMenuText(menus.get(id)), out);
    }
    out.print("</MenuBar>");
  }

  private String rootMenuText(Menu menu) {
	 return menu.getText(); 
//    for (MenuItem item : XModeler.getMenuBar().getItems())
//      if (item.getMenu() == menu) return item.getText();
//    return "";
  }

  private void writeMenu(String id, Menu menu, String text, PrintStream out) {
    if (menuSharingTable.containsKey(menu)) {
      out.print("<SharedMenu id = '" + id + "' sharedId='" + menuSharingTable.get(menu) + "'/>");
    } else {
      menuSharingTable.put(menu, id);
      out.print("<Menu id='" + id + "' text='" + XModeler.encodeXmlAttribute(text) + "'>");
      for (MenuItem item : menu.getItems())
        writeMenuItem(item, out);
      out.print("</Menu>");
    }
  }

  private void writeMenuItem(MenuItem item, PrintStream out) {
    if (item instanceof Menu){
	//if (item.getMenu() != null) {
      Menu m = (Menu)item;
      writeMenu(getId(m), m, m.getText(), out);
    } else out.print("<MenuItem id='" + getId(item) + "' text='" + XModeler.encodeXmlAttribute(item.getText()) + "'/>");
  }

  private String getId(MenuItem item) {
    for (String id : items.keySet())
      if (items.get(id) == item) return id;
    return "?";
  }

  private String getId(Menu menu) {
    for (String id : menus.keySet())
      if (menus.get(id) == menu) return id;
    return "?";
  }

  private boolean isRootMenu(Menu menu) {
    return XModeler.getMenuBar().getMenus().contains(menu);
//	for (MenuItem item : XModeler.getMenuBar().getItems())
//      if (item.getMenu() == menu) return true;
//    return false;
  }

  public void sendMessage(final Message message) {
    if (message.hasName("newMenu"))
      newMenu(message);
    else if (message.hasName("newGroupMarker"))
      newGroupMarker(message);
    else if (message.hasName("newMenuItem"))
      newMenuItem(message);
    else if (message.hasName("newGlobalMenu"))
      newGlobalMenu(message);
    else if (message.hasName("addMenuItem"))
      addMenuItem(message);
    else if (message.hasName("setGlobalMenu"))
      setGlobalMenu(message);
    else if (message.hasName("delete"))
      delete(message);
    else super.sendMessage(message);
  }

  private void delete(Message message) {
    String id = message.args[0].strValue();
    if (menus.containsKey(id)) {
      final Menu menu = menus.get(id);
      menu.getParentMenu().getItems().remove(menu); //is this enough?
//      if (!menu.isDisposed()) {
//        runOnDisplay(new Runnable() {
//          public void run() {
//            menu.dispose();
//          }
//        });
//      }
      menus.remove(id);
    }
    if (items.containsKey(id)) {
      final MenuItem item = items.get(id);
      item.getParentMenu().getItems().remove(item);
//      if (!item.isDisposed()) {
//        runOnDisplay(new Runnable() {
//          public void run() {
//            item.dispose();
//          }
//        });
//      }
      items.remove(id);
    }
    if (popups.containsKey(id)) {
      popups.remove(id);
      popupAssignments.remove(id);
    }
  }

  private void setGlobalMenu(Message message) {
    Value menuId = message.args[0];
    Value elementId = message.args[1];
    if (popups.containsKey(menuId.strValue())) {
      popupAssignments.put(elementId.strValue(), popups.get(menuId.strValue()));
    } else System.err.println("cannot set global menu for " + menuId);
  }

  private void addMenuItem(Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    String name = message.args[2].strValue();
    String keybinding = message.args[3].strValue();
    boolean supportsMulti = message.args[4].boolValue;
    String handlerPointIdentity = message.args[5].strValue();
    newPopupItem(parentId, id, name, keybinding, supportsMulti, handlerPointIdentity);
  }

  private void newPopupItem(String parentId, String id, String name, String keybinding, boolean supportsMulti, String handlerPointIdentity) {
    PopupMenuItem child = new PopupMenuItem();
    child.setId(id);
    child.setName(name);
    child.setKeyBinding(keybinding);
    child.setSupportsMulti(supportsMulti);
    child.setHandlerPointIdentity(handlerPointIdentity);
    // Broadcast...
    for (PopupMenu menu : popups.values())
      menu.addChild(parentId, child);
  }

  private void newGlobalMenu(Message message) {
    Value id = message.args[0];
    newPopup(id.strValue());
  }

  private void newPopup(String id) {
    PopupMenu menu = new PopupMenu();
    menu.setId(id);
    popups.put(id, menu);
  }

  public void inflateXML(Document doc) {
    NodeList menuClients = doc.getElementsByTagName("Menus");
    if (menuClients.getLength() == 1) {
      Node menuClient = menuClients.item(0);
      for (int i = 0; i < menuClient.getChildNodes().getLength(); i++)
        inflateMenuClientElement(menuClient.getChildNodes().item(i));
    } else System.err.println("expecting exactly 1 menu client got: " + menuClients.getLength());
  }

  private void inflateMenuClientElement(Node item) {
    if (item.getNodeName().equals("MenuBar")) inflateMenuBar(item);
    if (item.getNodeName().equals("Popup")) inflatePopup(item);
    if (item.getNodeName().equals("GlobalMenu")) inflateGlobalMenu(item);
  }

  private void inflateGlobalMenu(Node global) {
    String id = XModeler.attributeValue(global, "id");
    String popupId = XModeler.attributeValue(global, "popupId");
    popupAssignments.put(id, popups.get(popupId));
  }

  private void inflatePopup(Node popup) {
    String id = XModeler.attributeValue(popup, "id");
    newPopup(id);
    NodeList children = popup.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
      inflatePopupItem(id, children.item(i));
  }

  private void inflatePopupItem(String parentId, Node item) {
    String id = XModeler.attributeValue(item, "id");
    String name = XModeler.attributeValue(item, "name");
    String keyBinding = XModeler.attributeValue(item, "keyBinding");
    boolean supportsMulti = XModeler.attributeValue(item, "supportsMulti").equals("true");
    String handlerPointIdentity = XModeler.attributeValue(item, "handlerPointIdentity");
    newPopupItem(parentId, id, name, keyBinding, supportsMulti, handlerPointIdentity);
    NodeList children = item.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
      inflatePopupItem(id, children.item(i));
  }

  private void inflateMenuBar(Node menuBar) {
    NodeList menus = menuBar.getChildNodes();
    for (int i = 0; i < menus.getLength(); i++) {
      if (menus.item(i).getNodeName().equals("Menu")) inflateMenuBarMenu(menus.item(i));
      if (menus.item(i).getNodeName().equals("SharedMenu")) inflateSharedMenuBarMenu(menus.item(i));
    }
  }

  private void inflateSharedMenuBarMenu(Node menu) {
    String id = XModeler.attributeValue(menu, "id");
    String sharedId = XModeler.attributeValue(menu, "sharedId");
    if (menus.containsKey(sharedId)) menus.put(id, menus.get(sharedId));
  }

  private void inflateMenuBarMenu(Node menu) {
    String id = XModeler.attributeValue(menu, "id");
    String text = XModeler.attributeValue(menu, "text");
    newRootMenu(id, text);
    NodeList children = menu.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
      inflateMenuElement(id, children.item(i));
  }

  private void inflateMenuElement(String parentId, Node item) {
    if (item.getNodeName().equals("Menu"))
      inflateMenu(parentId, item);
    else if (item.getNodeName().equals("MenuItem")) inflateItem(parentId, item);
  }

  private void inflateItem(String parentId, Node item) {
    String id = XModeler.attributeValue(item, "id");
    String text = XModeler.attributeValue(item, "text");
    newMenuItem(parentId, id, text);
  }

  private void inflateMenu(String parentId, Node menu) {
    String id = XModeler.attributeValue(menu, "id");
    String text = XModeler.attributeValue(menu, "text");
    newMenu(parentId, id, text);
    NodeList children = menu.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
      inflateMenuElement(id, children.item(i));
  }

  private void newMenuItem(Message message) {
    String parent = message.args[0].strValue();
    String id = message.args[1].strValue();
    String name = message.args[2].strValue();
    newMenuItem(parent, id, name);
  }

  private void newMenuItem(final String parent, final String id, final String name) {
    if (menus.containsKey(parent)) {
    	CountDownLatch l = new CountDownLatch(1);
    	Platform.runLater(
//      Display.getDefault().syncExec(
    			new Runnable() {
    				public void run() {
          Menu menu = menus.get(parent);
          MenuItem item = new MenuItem(name.replace('&', '_')); //new MenuItem(menu, SWT.PUSH);
//          item.setText(name);
          menu.getItems().add(item);
          items.put(id, item);
          item.setOnAction(MenuClient.theClient);
          l.countDown();
          //item.addSelectionListener(MenuClient.this);
          //XModeler.getXModeler().setMenuBar(XModeler.getMenuBar());
    	}
      });
      try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
    } else System.err.println("Cannot find menu " + parent);
  }

  private void newGroupMarker(Message message) {
    Value parent = message.args[0];
    Value id = message.args[1];
    if (menus.containsKey(parent.strValue())) {
      // Assume that the group can share the parent menu.
      // CAREFUL when serializing due to sharing...
      menus.put(id.strValue(), menus.get(parent.strValue()));
    } else System.err.println("Cannot find menu " + parent.strValue());
  }

  private void newMenu(final Message message) {
    String parent = message.args[0].strValue();
    String id = message.args[1].strValue();
    String name = message.args[2].strValue();
    if (parent.equals("root"))
      newRootMenu(id, name);
    else newMenu(parent, id, name);
  }

  private void newMenu(final String parent, final String id, final String name) {
//    Display.getDefault().syncExec(
  	CountDownLatch l = new CountDownLatch(1);
  	Platform.runLater(
	  new Runnable() {
      public void run() {
        if (menus.containsKey(parent)) {
          Menu menu = menus.get(parent);
          Menu subMenu = new Menu(name.replace('&', '_'));//new Menu(XModeler.getXModeler(), SWT.DROP_DOWN);
//          MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
//          menuItem.setMenu(subMenu);
//          menuItem.setText(name);
          menu.getItems().add(subMenu);
          menus.put(id, subMenu);
//          XModeler.getXModeler().setMenuBar(XModeler.getMenuBar());
          l.countDown();
        } else System.err.println("Cannot find menu " + parent);
      }
    });
  	try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }

  private void newRootMenu(final String id, final String name) {
//    runOnDisplay(
  	CountDownLatch l = new CountDownLatch(1);
  	Platform.runLater(
	  new Runnable() {
      public void run() {
        Menu oldMenu = getRootMenuItemNamed(name);
        if (oldMenu != null) {
        	XModeler.getMenuBar().getMenus().remove(oldMenu);
        	String oldId = getId(oldMenu);
        	menus.remove(oldId);
        }
        //Menu menuBar = XModeler.getMenuBar();
        //MenuItem menuItem = new MenuItem(menuBar, SWT.CASCADE);
        Menu menu = new Menu(name.replace('&', '_'));//(XModeler.getXModeler(), SWT.DROP_DOWN);
        
        //menuItem.setMenu(menu);
        //menuItem.setText(name);
        XModeler.getMenuBar().getMenus().add(menu);
//        XModeler.getXModeler().setMenuBar(XModeler.getMenuBar());
        menus.put(id, menu);
        l.countDown();
      }
    });
  	try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }

  private Menu getRootMenuItemNamed(String name) {
	for (Menu menu : XModeler.getMenuBar().getMenus()){
		if(menu.getText().equals(name)) return menu;
	}
//	for (MenuItem item : XModeler.getMenuBar().getItems())
//      if (item.getText().equals(name)) return item;
    return null;
  }

  public boolean processMessage(Message message) {
    return true;
  }

//  public void widgetDefaultSelected(SelectionEvent arg0) {
//  }
//
//  public void widgetSelected(SelectionEvent event) {
//    MenuItem item = (MenuItem) event.widget;
//    for (String id : items.keySet())
//      if (items.get(id) == item) {
//        EventHandler handler = getHandler();
//        Message m = handler.newMessage("menuSelected", 1);
//        Value v1 = new Value(id);
//        m.args[0] = v1;
//        handler.raiseEvent(m);
//      }
//  }

  @Deprecated
  public static void popup(String id, int x, int y) {
//    if (popupAssignments.containsKey(id)) {
//      PopupMenu pmenu = popupAssignments.get(id);
//      Menu menu = pmenu.popup(id);
//      menu.setVisible(true);
//    } else System.err.println("no menu for " + id);
  }

  public static void popup(String id, javafx.scene.Node anchor, int x, int y) {
	    if (popupAssignments.containsKey(id)) {
	      PopupMenu pmenu = popupAssignments.get(id);
	      ContextMenu contextmenu = pmenu.popup(id);
	      contextmenu.setAutoHide(true);
	      contextmenu.show(anchor,x,y);
	    } else System.err.println("no menu for " + id);
	  }
  
  @Override
	public void handle(ActionEvent event) {
	    MenuItem item = (MenuItem) event.getSource();
	    for (String id : items.keySet())
	      if (items.get(id) == item) {
	        EventHandler handler = getHandler();
	        Message m = handler.newMessage("menuSelected", 1);
	        Value v1 = new Value(id);
	        m.args[0] = v1;
	        handler.raiseEvent(m);
	      }
	}
}