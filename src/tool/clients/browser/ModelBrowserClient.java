package tool.clients.browser;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import tool.clients.Client;
import tool.clients.menus.MenuClient;
import tool.helper.IconGenerator;
import tool.xmodeler.PropertyManager;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class ModelBrowserClient extends Client {//implements MouseListener, Listener, KeyListener, CTabFolder2Listener {

  private static TabPane tabPane; 	
	
  static ModelBrowserClient          				theClient;
  static Hashtable<String, Tab> 					tabs               = new Hashtable<String, Tab>();
  static Hashtable<String, TreeView<String>>     	trees              = new Hashtable<String, TreeView<String>>();
  static Hashtable<String, TreeItem<String>> 		items              = new Hashtable<String, TreeItem<String>>();
  static Hashtable<String, String>   				images             = new Hashtable<String, String>();
  static Hashtable<String, String>   				treeChildrenList   = new Hashtable<String, String>(); // child -> tree
  boolean                            				rendering          = true;
  HashSet<TreeItem<String>>                  		deferredExpansions = new HashSet<TreeItem<String>>();
  
  static Font font = null;  

  public static void start(TabPane tabPane){
	  ModelBrowserClient.tabPane = tabPane;
  }

  public static ModelBrowserClient theClient() {
    return theClient;
  }

  public ModelBrowserClient() {
    super("com.ceteva.modelBrowser");
    theClient = this;
  }

  private void addNodeWithIcon(Message message) {
    Value parentId = message.args[0];
    Value nodeId = message.args[1];
    Value text = message.args[2];
    Value editable = message.args[3];
    Value icon = message.args[4];
    int index = -1;
    if (message.arity == 6) index = message.args[5].intValue;
    if (trees.containsKey(parentId.strValue())) {
    	 addRootNodeWithIcon(parentId.strValue(), nodeId.strValue(), text.strValue(), editable.boolValue, false, icon.strValue(), index);
    } else {
    	addNodeWithIcon(parentId.strValue(), nodeId.strValue(), text.strValue(), editable.boolValue, false, icon.strValue(), index);
    }
  }

  private void addNodeWithIcon(final String parentId, final String nodeId, final String text, boolean editable, final boolean expanded, final String icon, final int index) {
	  if (items.containsKey(parentId)) {
		  CountDownLatch l = new CountDownLatch(1);  
		  Platform.runLater(()->{
			  TreeItem<String> parent = items.get(parentId);
			  if (!rendering) {
		            parent.setExpanded(false);
		            // Careful about the dummy nodes added to directories to ensure they
		            // show the handles...
		            if (text.trim().length() != 0) deferredExpansions.add(parent);
		      }
			  
			  ImageView iconView = IconGenerator.getImageView(icon);
//			  ImageView iconView = new ImageView(new Image(getClass().getResourceAsStream("icons/" + icon + ".gif")));
	          TreeItem<String> item = new TreeItem<String>(text, iconView);
	          parent.getChildren().add((index == -1) ? parent.getChildren().size() : index, item);
	          images.put(nodeId, icon);
	          items.put(nodeId, item);
	          item.setExpanded(expanded);
	          
	          treeChildrenList.put(nodeId,treeChildrenList.get(parentId));
	          
	          if (parent.getParent() == null) {
	              if (rendering)
	                parent.setExpanded(true);
	              else {
	                parent.setExpanded(false);
	                // Careful about the dummy nodes added to directories to ensure they
	                // show the handles...
	                if (text.trim().length() != 0) deferredExpansions.add(parent);
	              }
	            }
	          //in -> TreeItemInteractionHandler
//	          for (String id : trees.keySet())
//	              for (TreeItem i : trees.get(id).get )
//	                if (i == item) tabFolder.setSelection(tabs.get(id));
	          l.countDown();
		  });
		  try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		  
    } else System.err.println("ModelBrowserClient.addNodeWithIcon: cannot find node " + parentId);
  }

  private void addRootNodeWithIcon(final String parentId, final String nodeId, final String text, boolean editable, final boolean expanded, final String icon, final int index) {
	  CountDownLatch l = new CountDownLatch(1);
	  Platform.runLater(() -> {
		  TreeView<String> parent = trees.get(parentId);
		  
//		  ImageView iconView = new ImageView(new Image(getClass().getResourceAsStream("icons/" + icon + ".gif")));
		  ImageView iconView = IconGenerator.getImageView(icon);
          TreeItem<String> item = new TreeItem<String>(text, iconView);
          
          parent.setRoot(item);
          images.put(nodeId, icon);
          items.put(nodeId, item);
          
          treeChildrenList.put(nodeId, parentId);
          
          item.setExpanded(expanded);
          
          l.countDown();
	  });
	  try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }

	private void addTree(final String id, final String name) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			TreeView<String> tv = new TreeView<String>();
			trees.put(id, tv);
			tv.setCellFactory((p) -> {
				return new TreeItemInteractionHandler();
			});

			if (PropertyManager.getProperty("treeBrowsersSeparately", true)) {
				createStage(tv, name, id);
			} else {
				createTab(tv, name, id);
			}

			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void createTab(TreeView<String> tv, String name, String id) {
		Tab tab = new Tab(name);
		tab.setTooltip(new Tooltip(name));
		tab.setContent(tv);
		tab.setClosable(true);
		tabs.put(id, tab);
		tab.setOnCloseRequest((e) -> closeTab(tab, e, id, name, tv));
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);
	}

	private void closeTab(Tab item, Event wevent, String id, String name, TreeView<String> tv) {

		Alert alert = new Alert(AlertType.CONFIRMATION);

		ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
		ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
		alert.setTitle("Open tab in separate window instead?");
		alert.setHeaderText(null);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get().getButtonData() == ButtonData.YES) {
			tabs.remove(id);
			PropertyManager.setProperty("treeBrowsersSeparately", "true");
			createStage(tv, name, id);
		} else if (result.get().getButtonData() == ButtonData.CANCEL_CLOSE) {
			wevent.consume();
		} else {
			Message message = getHandler().newMessage("modelBrowserClosed", 1);
			message.args[0] = new Value(id);
			getHandler().raiseEvent(message);
			trees.remove(id);
			tabs.remove(id);
		}

	}

	private void createStage(TreeView<String> tv, String name, String id) {

		Stage stage = new Stage();
		BorderPane border = new BorderPane();
		border.setCenter(tv);
		Scene scene = new Scene(border, 300, 605);
		stage.setScene(scene);
		stage.setTitle(name);
		stage.show();
		stage.setOnCloseRequest((e) -> closeScene(stage, e, id, name, tv));
	}

	private void closeScene(Stage stage, Event wevent, String id, String name, TreeView<String> tv) {

		Alert alert = new Alert(AlertType.CONFIRMATION);

		ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
		ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
		alert.setTitle("Open stage as tab in tree pane instead?");
		alert.setHeaderText(null);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get().getButtonData() == ButtonData.YES) {
			PropertyManager.setProperty("treeBrowsersSeparately", "false");
			createTab(tv, name, id);
		} else if (result.get().getButtonData() == ButtonData.CANCEL_CLOSE) {
			wevent.consume();
		} else {
			Message message = getHandler().newMessage("modelBrowserClosed", 1);
			message.args[0] = new Value(id);
			getHandler().raiseEvent(message);
			trees.remove(id);
		}
	}

	private void expand(final String id) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			items.get(id).setExpanded(true);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

  public void sendMessageExpanded(TreeItem<String> item) {
  		  Message m = getHandler().newMessage("expanded", 1);
	      Value v1 = null;
	      for (String id : items.keySet())
	        if (items.get(id) == item) v1 = new Value(id);
	      if( v1!= null){
	    	  m.args[0] = v1;
	    	  getHandler().raiseEvent(m);
	      }
	  }
  
  private void inflateTree(Node treeNode) {
    final String id = XModeler.attributeValue(treeNode, "id");
    boolean selected = XModeler.attributeValue(treeNode, "selected").equals("true");
    NodeList roots = treeNode.getChildNodes();
    if (roots.getLength() == 1) {
      Node root = roots.item(0);
      String rootId = XModeler.attributeValue(root, "id");
      String text = XModeler.attributeValue(root, "text");
      String image = XModeler.attributeValue(root, "image");
      boolean expanded = XModeler.attributeValue(root, "expanded").equals("true");
      addTree(id, text);
      addRootNodeWithIcon(id, rootId, text, true, expanded, image, 0);
      inflateTreeItem(root);
      if (selected) select(id);
      if (expanded) expand(rootId);
    } else System.err.println("expecting to inflate a tree with 1 root node.");
  }

  private void inflateTreeItem(Node node) {
    String id = XModeler.attributeValue(node, "id");
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      String childId = XModeler.attributeValue(child, "id");
      String text = XModeler.attributeValue(child, "text");
      String image = XModeler.attributeValue(child, "image");
      boolean expanded = XModeler.attributeValue(child, "expanded").equals("true");
      addNodeWithIcon(id, childId, text, true, expanded, image, i);
      inflateTreeItem(child);
      if (expanded) expand(childId);
    }
  }

  public void inflateXML(Document doc) {
    NodeList modelBrowsers = doc.getElementsByTagName("ModelBrowser");
    if (modelBrowsers.getLength() == 1) {
      Node modelBrowser = modelBrowsers.item(0);
      NodeList treeNodes = modelBrowser.getChildNodes();
      for (int i = 0; i < treeNodes.getLength(); i++) {
        Node treeNode = treeNodes.item(i);
        inflateTree(treeNode);   
      }
    } else System.err.println("expecting exactly 1 model browser got: " + modelBrowsers.getLength());
  }

  public String itemId(TreeItem<String> item) {
    for (String id : items.keySet())
      if (items.get(id) == item) return id;
    return null;
  }

  public void keyPressed(KeyEvent e) {
  }

  public void keyReleased(KeyEvent event) {
  }

  public void sendMessageMouseDoubleClick(TreeItem<String> item) {
	      String id = itemId(item);
	      Message m = getHandler().newMessage("doubleSelected", 1);
	      m.args[0] = new Value(id);
	      getHandler().raiseEvent(m);
 }
  
//  public void openContextMenu(TreeItem<String> item, javafx.scene.Node anchor, int x, int y){
//	  for (String id : items.keySet())
//          if (items.get(id) == item) MenuClient.popup(id, anchor, x, y);
//  }


  private void newModelBrowser(Message message) {
    final Value id = message.args[0];
    Value clientName = message.args[1];
    final Value name = message.args[2];
    if (clientName.strValue().equals("com.ceteva.browser")) {
      addTree(id.strValue(), name.strValue());
    }

  }

  public boolean processMessage(Message message) {
    return false;
  }

  private void removeNode(Message message) {
    final Value id = message.args[0];
    if (items.containsKey(id.strValue())) {
    	CountDownLatch l = new CountDownLatch(1);
    	Platform.runLater(()->{
    		TreeItem<String> item = items.get(id.strValue());
    		TreeItem<String> parent = item.getParent();
    		if(parent != null){
    			parent.getChildren().remove(item);
    		}
    		l.countDown();
    	});
    	try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    } else System.err.println("ModelBrowserClient.removeNode: cannnot remove node with id " + id);
  }

  private void select(final String id) {
	CountDownLatch l = new CountDownLatch(1);  
	Platform.runLater(()->{
		tabPane.getSelectionModel().select(tabs.get(id));
		l.countDown();
	});  
	try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }

  private void selectNode(Message message, boolean select) {
    final Value id = message.args[0];
    Value selected = message.args[1];
    if (selected.boolValue && items.containsKey(id.strValue())) {
    	TreeItem<String> item = items.get(id.strValue());
    	TreeView<String> tv = trees.get(treeChildrenList.get(id.strValue()));
    	
    	CountDownLatch l = new CountDownLatch(1);
    	Platform.runLater(()->{
    		if(select) 
    			tv.getSelectionModel().select(item);
    		else 
    			tv.getSelectionModel().clearSelection();
    		l.countDown();
    	});
    	try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}      
    }
  }

  public void sendMessage(final Message message) {
    if (message.hasName("newModelBrowser"))
      newModelBrowser(message);
    else if (message.hasName("addNodeWithIcon"))
      addNodeWithIcon(message);
    else if (message.hasName("removeNode"))
      removeNode(message);
    else if (message.hasName("setText"))
      setText(message);
    else if (message.hasName("setVisible"))
      setVisible(message);
    else if (message.hasName("selectNode"))
        selectNode(message, true);
    else if (message.hasName("deselectNode"))
        selectNode(message, false);
    else if (message.hasName("setFocus"))
      setFocus(message);
    else if (message.hasName("setToolTipText"))
      setTooltipText(message);
    else if (message.hasName("renderOn"))
      renderOn();
    else if (message.hasName("renderOff"))
      renderOff();
    else super.sendMessage(message);
  }

  private void renderOff() {
    rendering = false;
    deferredExpansions.clear();
  }

  private void renderOn() {
    rendering = true;
    CountDownLatch l = new CountDownLatch(1);
    Platform.runLater(()->{
    	for (TreeItem<String> item : deferredExpansions) {
            item.setExpanded(true);
          }
    	deferredExpansions.clear();
    	l.countDown();
    });
    try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }

  private void setFocus(Message message) {
    final Value id = message.args[0];
    if (tabs.containsKey(id.strValue())) {
       	CountDownLatch l = new CountDownLatch(1);
       	Platform.runLater(()->{
       		tabPane.requestFocus();
       		tabPane.getSelectionModel().select(tabs.get(id.strValue()));
       		l.countDown();
       	});
    	try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    } else System.err.println("cannot find tab " + id);
  }

  private void setText(Message message) {
    final Value id = message.args[0];
    final Value text = message.args[1];
    if (items.containsKey(id.strValue())) {
    	CountDownLatch l = new CountDownLatch(1);
    	Platform.runLater(()->{
    		TreeItem<String> item = items.get(id.strValue());
            item.setValue(text.strValue());
            l.countDown();
    	});
    	try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    } else System.err.println("ModelBrowserClienr.setText: cannot find tree item " + id.strValue());
  }

  private void setTooltipText(Message message) {
    // This is not currently supported.
  }

  private void setVisible(Message message) {
    final Value id = message.args[0];
    if (tabs.containsKey(id.strValue())) {
    	CountDownLatch l = new CountDownLatch(1);
    	Platform.runLater(()->{
    		tabPane.getSelectionModel().select(tabs.get(id.strValue()));
    		l.countDown();
    	});
    	try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    } else System.err.println("cannot select tab " + id);
  }

  public void sendMessageUpdateText(TreeItem<String> item, String text) {

    // Change the text associated with the label and raise the event...
      Message m = getHandler().newMessage("textChanged", 2);
      Value v1 = null;
      for (String id : items.keySet())
        if (items.get(id) == item) v1 = new Value(id);
      m.args[0] = v1;
      Value v2 = new Value(item.getValue());
      m.args[1] = v2;
      getHandler().raiseEvent(m);
      
  }

  public void writeXML(PrintStream out) {
    out.print("<ModelBrowser>");
    for (String id : trees.keySet()) {
    	TreeView<String> tree = trees.get(id);
    	Tab tab = tabs.get(id);
    	out.print("<Tree id='" + id + "' selected='" + (tab.isSelected()) + "'>");
        writeXMLTreeRootItems(tree.getRoot(), out);
        
      out.print("</Tree>");
    }
    out.print("</ModelBrowser>");
  }

  private void writeXMLTreeRootItems(TreeItem<String> item, PrintStream out) {
	      String id = null;
	      for (String itemId : items.keySet()) {
	        if (items.get(itemId) == item) id = itemId;
	      }
	      if (id == null) System.err.println("error: cannot find tree item " + item);
	      String icon = images.get(id);
	      out.print("<Item id='" + id + "' text='" + XModeler.encodeXmlAttribute(item.getValue()) + "' image='" + icon + "' expanded='" + item.isExpanded() + "'>");
	      writeXMLTreeItems(item.getChildren(), out);
	      out.print("</Item>");
  }
  
  private void writeXMLTreeItems(Collection<TreeItem<String>> children, PrintStream out) {
    for (TreeItem<String> item : children) {
      String id = null;
      for (String itemId : items.keySet()) {
        if (items.get(itemId) == item) id = itemId;
      }
      if (id == null) System.err.println("error: cannot find tree item " + item);
      String icon = images.get(id);
      out.print("<Item id='" + id + "' text='" + XModeler.encodeXmlAttribute(item.getValue()) + "' image='" + icon + "' expanded='" + item.isExpanded() + "'>");
      writeXMLTreeItems(item.getChildren(), out);
      out.print("</Item>");
    }
  }
}