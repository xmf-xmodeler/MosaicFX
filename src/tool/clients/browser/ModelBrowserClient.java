package tool.clients.browser;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

//import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CTabFolder;
//import org.eclipse.swt.custom.CTabFolder2Adapter;
//import org.eclipse.swt.custom.CTabFolder2Listener;
//import org.eclipse.swt.custom.CTabFolderEvent;
//import org.eclipse.swt.custom.CTabItem;
//import org.eclipse.swt.custom.TreeEditor;
//import org.eclipse.swt.events.KeyEvent;
//import org.eclipse.swt.events.KeyListener;
//import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.events.MouseListener;
//import org.eclipse.swt.graphics.Color;
//import org.eclipse.swt.graphics.Font;
//import org.eclipse.swt.graphics.FontData;
//import org.eclipse.swt.graphics.GC;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.graphics.ImageData;
//import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.graphics.Rectangle;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Event;
//import org.eclipse.swt.widgets.Listener;
//import org.eclipse.swt.widgets.Text;
//import org.eclipse.swt.widgets.Tree;
//import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import tool.clients.Client;
import tool.clients.menus.MenuClient;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class ModelBrowserClient extends Client {//implements MouseListener, Listener, KeyListener, CTabFolder2Listener {

  private static TabPane tabPane; 	
	
  static ModelBrowserClient          theClient;
  static Hashtable<String, Tab> tabs               = new Hashtable<String, Tab>();
  static Hashtable<String, TreeView<String>>     trees              = new Hashtable<String, TreeView<String>>();
  static Hashtable<String, TreeItem<String>> items              = new Hashtable<String, TreeItem<String>>();
  static Hashtable<String, String>   images             = new Hashtable<String, String>();
  
  static Hashtable<String, String>   treeChildrenList   = new Hashtable<String, String>(); // child -> tree
  
  boolean                            rendering          = true;
  HashSet<TreeItem<String>>                  deferredExpansions = new HashSet<TreeItem<String>>();
  
  static Font font = null;//Font.loadFont("file:fonts/DejaVuSans.ttf", 10);
  
//  public static CTabFolder getTabFolder() {
//    return tabFolder;
//  }

  public static void start(TabPane tabPane){
	  ModelBrowserClient.tabPane = tabPane;
  }
  
//  public static void start(CTabFolder tabFolder, int style) {
//    ModelBrowserClient.tabFolder = tabFolder;
//  }

  public static ModelBrowserClient theClient() {
    return theClient;
  }

  // static Font labelFont = new Font(XModeler.getXModeler().getDisplay(), new FontData("Monaco", 12, SWT.NONE));

//  final static int                   RIGHT_BUTTON       = 3;
//  static CTabFolder                  tabFolder;
//  static ModelBrowserClient          theClient;
//  FontData                           fontData;
//  boolean                            rendering          = true;
//  HashSet<TreeItem>                  deferredExpansions = new HashSet<TreeItem>();
//  static Font                        labelFont;
//  static Hashtable<String, TreeItem> items              = new Hashtable<String, TreeItem>();
//  static Hashtable<String, CTabItem> tabs               = new Hashtable<String, CTabItem>();
//  static Hashtable<String, Tree>     trees              = new Hashtable<String, Tree>();
//  static Hashtable<String, String>   images             = new Hashtable<String, String>();
//  static Hashtable<Tree, TreeItem>   selections         = new Hashtable<Tree, TreeItem>();

  public ModelBrowserClient() {
    super("com.ceteva.modelBrowser");
    theClient = this;
//    tabFolder.addCTabFolder2Listener(this);
//    setFont("fonts/DejaVuSans.ttf", "DejaVu Sans");
  }

  private void addNodeWithIcon(Message message) {
    Value parentId = message.args[0];
    Value nodeId = message.args[1];
    Value text = message.args[2];
    Value editable = message.args[3];
    Value icon = message.args[4];
    int index = -1;
    if (message.arity == 6) index = message.args[5].intValue;
    if (trees.containsKey(parentId.strValue()))
      addRootNodeWithIcon(parentId.strValue(), nodeId.strValue(), text.strValue(), editable.boolValue, false, icon.strValue(), index);
    else addNodeWithIcon(parentId.strValue(), nodeId.strValue(), text.strValue(), editable.boolValue, false, icon.strValue(), index);
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
			  
			  ImageView iconView = new ImageView(new Image((new File("src/resources/gif/" + icon + ".gif").toURI().toString())));
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
		  
//      runOnDisplay(new Runnable() {
//        public void run() {
//          TreeItem parent = items.get(parentId);
//          if (!rendering) {
//            parent.setExpanded(false);
//            // Careful about the dummy nodes added to directories to ensure they
//            // show the handles...
//            if (text.trim().length() != 0) deferredExpansions.add(parent);
//          }
//          String iconFile = "icons/" + icon + ".gif";
//          ImageData data = new ImageData(iconFile);
//          Image image = new Image(XModeler.getXModeler().getDisplay(), data);
//          TreeItem item = new TreeItem(parent, SWT.NONE, (index == -1) ? parent.getItemCount() : index);
//          images.put(nodeId, icon);
//          items.put(nodeId, item);
//          item.setText(text);
//          item.setImage(image);
//          item.setExpanded(expanded);
//          item.setFont(labelFont);
          // automatically open root-node, if child-node is created.
//          if (parent.getParentItem() == null) {
//            if (rendering)
//              parent.setExpanded(true);
//            else {
//              parent.setExpanded(false);
//              // Careful about the dummy nodes added to directories to ensure they
//              // show the handles...
//              if (text.trim().length() != 0) deferredExpansions.add(parent);
//            }
//          }
//          for (String id : trees.keySet())
//            for (TreeItem i : trees.get(id).getItems())
//              if (i == item) tabFolder.setSelection(tabs.get(id));
//        }
//      });
    } else System.err.println("ModelBrowserClient.addNodeWithIcon: cannot find node " + parentId);
  }

  private void addRootNodeWithIcon(final String parentId, final String nodeId, final String text, boolean editable, final boolean expanded, final String icon, final int index) {
	  CountDownLatch l = new CountDownLatch(1);
	  Platform.runLater(() -> {
		  TreeView<String> parent = trees.get(parentId);
		  
//		  ImageView iconView = new ImageView(new Image(getClass().getResourceAsStream("icons/" + icon + ".gif")));
		  ImageView iconView = new ImageView(new Image((new File("src/resources/gif/" + icon + ".gif").toURI().toString())));
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
//	  Display.getDefault().syncExec(new Runnable() {
//      public void run() {
//
//        TreeItem item = new TreeItem(tree, SWT.NONE, (index == -1) ? tree.getItemCount() : index);
//        images.put(nodeId, icon);
//        items.put(nodeId, item);
//        item.setText(text);
//        item.setImage(image);
//        item.setExpanded(expanded);
//        item.setFont(labelFont);
//        tabFolder.setSelection(tabs.get(parentId));
//      }
//    });
  }

  private void addTree(final String id, final String name) {
	  CountDownLatch l = new CountDownLatch(1);
	  Platform.runLater(()->{
		  Tab tab = new Tab(name);
		  tabs.put(id,tab);
		  tab.setOnClosed((e)->{
              Message m = getHandler().newMessage("modelBrowserClosed", 1);
              m.args[0] = new Value(id);
              getHandler().raiseEvent(m);
		  });
		  
		  TreeView<String> tv = new TreeView<String>();
		  tab.setContent(tv);
		  trees.put(id, tv);
		  
		  
		  tv.setCellFactory((p)->{
        	 return new TreeItemInteractionHandler();
		  });
		  		 
		  tabPane.getTabs().add(tab);
		  tabPane.getSelectionModel().select(tab);
		  l.countDown();
	  });
	  try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
//	runOnDisplay(new Runnable() {
//      public void run() {
//        final CTabItem tabItem = new CTabItem(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.CLOSE);
//        tabItem.setText(name);
//        tabs.put(id, tabItem);
//        Tree tree = new Tree(tabFolder, SWT.VIRTUAL);
//        tree.addKeyListener(ModelBrowserClient.this);
//        tabItem.setControl(tree);
//        trees.put(id, tree);
//        tree.addMouseListener(ModelBrowserClient.this);
//        tree.addListener(SWT.Expand, ModelBrowserClient.this);
//        tree.addListener(SWT.Selection, ModelBrowserClient.this);
//        tabFolder.setSelection(tabItem);

//        tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
//          public void close(CTabFolderEvent event) {
//            if (event.item.equals(tabItem)) {
//
//              Message m = getHandler().newMessage("modelBrowserClosed", 1);
//              m.args[0] = new Value(id);
//              getHandler().raiseEvent(m);
//            }
//          }
//        });
        // tabItem.getDisplay().addFilter(SWT.KeyDown, new Listener() {
        // public void handleEvent(Event event) {
        // if(event.stateMask == SWT.CTRL) {
        // if((int)(event.character) == 23) { // Ctrl + W
        // tabItem.dispose();
        // Message m = getHandler().newMessage("modelBrowserClosed", 1);
        // m.args[0] = new Value(id);
        // getHandler().raiseEvent(m);
        // }
        // }
        // }
        // }
        // );

//      }
//    });
  }

//  public void editText(final Tree tree, final TreeItem item) {
//
//    // Called to create an editable area over the tree item and
//    // raise a textChanged event if the text is modified...
//
//    final Color black = XModeler.getXModeler().getDisplay().getSystemColor(SWT.COLOR_BLACK);
//    final TreeItem[] lastItem = new TreeItem[1];
//    final TreeEditor editor = new TreeEditor(tree);
//    boolean showBorder = true;
//    final Composite composite = new Composite(tree, SWT.NONE);
//    if (showBorder) composite.setBackground(black);
//    final Text text = new Text(composite, SWT.NONE);
//    final int inset = showBorder ? 1 : 0;
//    composite.addListener(SWT.Resize, new Listener() {
//      public void handleEvent(Event e) {
//        Rectangle rect = composite.getClientArea();
//        text.setBounds(rect.x + inset, rect.y + inset, rect.width - inset * 2, rect.height - inset * 2);
//      }
//    });
//    Listener textListener = new Listener() {
//      public void handleEvent(final Event e) {
//        switch (e.type) {
//          case SWT.FocusOut:
//            updateText(item, text.getText());
//            composite.dispose();
//            break;
//          case SWT.Verify:
//            String newText = text.getText();
//            String leftText = newText.substring(0, e.start);
//            String rightText = newText.substring(e.end, newText.length());
//            GC gc = new GC(text);
//            Point size = gc.textExtent(leftText + e.text + rightText);
//            gc.dispose();
//            size = text.computeSize(size.x, SWT.DEFAULT);
//            editor.horizontalAlignment = SWT.LEFT;
//            Rectangle itemRect = item.getBounds(), rect = tree.getClientArea();
//            editor.minimumWidth = Math.max(size.x, itemRect.width) + inset * 2;
//            int left = itemRect.x, right = rect.x + rect.width;
//            editor.minimumWidth = Math.min(editor.minimumWidth, right - left);
//            editor.minimumHeight = size.y + inset * 2;
//            editor.layout();
//            break;
//          case SWT.Traverse:
//            switch (e.detail) {
//              case SWT.TRAVERSE_RETURN:
//                updateText(item, text.getText());
//                // fall through.
//              case SWT.TRAVERSE_ESCAPE:
//                composite.dispose();
//                e.doit = false;
//            }
//            break;
//        }
//      }
//    };
//    text.addListener(SWT.FocusOut, textListener);
//    text.addListener(SWT.Traverse, textListener);
//    text.addListener(SWT.Verify, textListener);
//    editor.setEditor(composite, item);
//    text.setText(item.getText());
//    text.selectAll();
//    text.setFocus();
//  }

  private void expand(final String id) {
	CountDownLatch l = new CountDownLatch(1);
	Platform.runLater(()->{
		items.get(id).setExpanded(true);
		l.countDown();
	});
	try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}  
//    runOnDisplay(new Runnable() {
//      public void run() {
//        items.get(id).setExpanded(true);
//        tabFolder.redraw();
//      }
//    });
  }

//  private String getId(TreeView<String> tree) {
//    for (String id : trees.keySet())
//      if (trees.get(id) == tree) return id;
//    return null;
//  }
//
//  private String getId(TreeItem<String> item) {
//    for (String id : items.keySet())
//      if (items.get(id) == item) return id;
//    return null;
//  }

//  public void handleEvent(Event event) {
//    if (event.type == SWT.Expand) {
//      Message m = getHandler().newMessage("expanded", 1);
//      TreeItem item = (TreeItem) event.item;
//      Value v1 = null;
//      for (String id : items.keySet())
//        if (items.get(id) == item) v1 = new Value(id);
//      m.args[0] = v1;
//      getHandler().raiseEvent(m);
//    }
//    if (event.type == SWT.Selection) {
//      Tree tree = (Tree) event.widget;
//      TreeItem item = selections.get(tree);
//      if (tree.getSelectionCount() == 1) {
//        if (item != null && tree.getSelection()[0] == item) {
//          editText(tree, item);
//        } else selections.put(tree, tree.getSelection()[0]);
//      }
//    }
//  }

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

//  private boolean isCommand(MouseEvent event) {
//    return (event.stateMask & SWT.COMMAND) != 0;
//  }
//
//  private boolean isRightClick(MouseEvent event) {
//    return event.button == RIGHT_BUTTON;
//  }
//
//  private boolean isCtrl(MouseEvent event) {
//    return (event.stateMask & SWT.CTRL) != 0;
//  }

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
  
//  public void mouseDoubleClick(MouseEvent event) {
//    Tree tree = (Tree) event.widget;
//    if (tree.getSelectionCount() == 1) {
//      TreeItem item = tree.getSelection()[0];
//      String id = itemId(item);
//      Message m = getHandler().newMessage("doubleSelected", 1);
//      m.args[0] = new Value(id);
//      getHandler().raiseEvent(m);
//    }
//  }

//  public void mouseDown(MouseEvent event) {
//    if (isRightClick(event) || isCommand(event) || isCtrl(event)) {
//      Tree tree = (Tree) event.widget;
//      if (tree.getSelectionCount() == 1) {
//        TreeItem item = tree.getSelection()[0];
//        for (String id : items.keySet())
//          if (items.get(id) == item) MenuClient.popup(id, event.x, event.y);
//      }
//    }
//  }
  
  public void openContextMenu(TreeItem<String> item, javafx.scene.Node anchor, int x, int y){
	  for (String id : items.keySet())
          if (items.get(id) == item) MenuClient.popup(id, anchor, x, y);
  }

//  public void mouseUp(MouseEvent event) {
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
//      Display.getDefault().syncExec(new Runnable() {
//        public void run() {
//          TreeItem item = items.get(id.strValue());
//          item.dispose();
//        }
//      });
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
//    runOnDisplay(new Runnable() {
//      public void run() {
//        tabFolder.setSelection(tabs.get(id));
//      }
//    });
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
//    runOnDisplay(new Runnable() {
//      public void run() {
//        for (TreeItem item : deferredExpansions) {
//          item.setExpanded(true);
//        }
//      }
//    });
//    deferredExpansions.clear();
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
//    	Display.getDefault().syncExec(new Runnable() {
//        public void run() {
//          CTabItem tab = tabs.get(id.strValue());
//          tabFolder.setFocus();
//          tabFolder.setSelection(tab);
//        }
//      });
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
//      Display.getDefault().syncExec(new Runnable() {
//        public void run() {
//          TreeItem item = items.get(id.strValue());
//          item.setText(text.strValue());
//        }
//      });
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
//      Display.getDefault().syncExec(new Runnable() {
//        public void run() {
//          CTabItem item = tabs.get(id.strValue());
//          tabFolder.setSelection(item);
//        }
//      });
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
        
//      Tree tree = trees.get(id);
//      CTabItem tab = tabs.get(id);
//      out.print("<Tree id='" + id + "' selected='" + (tabFolder.getSelection() == tab) + "'>");
//      writeXMLTreeItems(tree.getItems(), out);
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

//  public void close(CTabFolderEvent event) {
//  }
//
//  public void maximize(CTabFolderEvent event) {
//
//  }
//
//  public void minimize(CTabFolderEvent event) {
//
//  }
//
//  public void restore(CTabFolderEvent event) {
//
//  }
//
//  public void showList(CTabFolderEvent event) {
//
//  }

//  public final void setFont(String fileName, String name) {
//    // int oldHeight = fontData==null?10:fontData.getHeight();
//    // System.out.println("oldHeight: " + oldHeight + "("+(fontData!=null)+")");
//    FontData[] fontData = Display.getDefault().getSystemFont().getFontData();
//    // oldHeight = fontData==null?10:fontData[0].getHeight();
//    // int oldHeight =
//    // System.out.println("oldHeight: " + oldHeight + "("+(fontData!=null)+")");
//    this.fontData = fontData[0];
//    XModeler.getXModeler().getDisplay().loadFont(fileName);
//    this.fontData.setName(name);
//    // this.fontData.setHeight(oldHeight);
//    labelFont = new Font(XModeler.getXModeler().getDisplay(), fontData);
//  }
}