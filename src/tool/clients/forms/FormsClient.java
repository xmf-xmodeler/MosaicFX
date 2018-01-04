package tool.clients.forms;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

//import org.eclipse.draw2d.FigureUtilities;
//import org.eclipse.draw2d.geometry.Dimension;
//import org.eclipse.swt.custom.CTabFolderEvent;
//import org.eclipse.swt.custom.CTabItem;
//import org.eclipse.swt.graphics.Color;
//import org.eclipse.swt.graphics.Font;
//import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import tool.clients.Client;
import tool.clients.EventHandler;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class FormsClient extends Client {
	
	public FormsClient() {
		super("com.ceteva.forms");

	    setDebug(true);
		theClient = this;
	}

	public static void start(TabPane tabFolder) {
		FormsClient.tabFolder = tabFolder;

//		tab.setOnClosed(new javafx.event.EventHandler<Event>() {
//			@Override
//			public void handle(Event t) {
//				close(t);
//				t.consume();
//			}
//		});
	}

//	public static boolean HIGH_RESOLUTION = false;
	public static final int HIGH_RESOLUTION_FACTOR_OLD = 2;
	
//	  private Value getTextDimension(final Message message) {
//
////		  runOnDisplay( )
//		  
////		  XModeler.getXModeler().getDisplay().syncExec(
//		  class calcTextDimension implements Runnable{ 
//			  public Value value = null;
//			  //calcTextDimension(){ 
//	 			public void run() {
//	 				System.err.println("calcTextDimension");
//  					try {
//  						Value[] values = new Value[2];
//		        	Font f = XModeler.getXModeler().getDisplay().getSystemFont();
//		  			Dimension d = FigureUtilities.getTextExtents(message.args[0].strValue(), f);
//		  			
//  						values[0] = new Value(d.width);
//  						values[1] = new Value(d.height);
//  						value = new Value(values);
//  					} catch (Throwable t) {
//  						t.printStackTrace();
//  					}
//  				}
//	  		};
////			  				);
//	  		calcTextDimension r = new calcTextDimension();
//	  		//CountDownLatch l = new CountDownLatch(1);  //TODO
//	  		Platform.runLater(r);
//
//			return r.value;
//	  }
	
  public static int getDeviceZoomPercent() {
	  return XModeler.getDeviceZoomPercent();
  }
	
//  public static Font getFormLabelFont() {
//    return formLabelFont;
//  }
//
//  public static Font getFormTextFieldFont() {
//    return formLabelFont;
//  }

//  public static void select() {
//	for (ToolItem item : toolBar.getItems())
//          item.dispose();
//	CTabItem selectedItem = tabFolder.getSelection();
//	for (String id : tabs.keySet()) {
//      if (tabs.get(id) == selectedItem) {
////        for (ToolItem item : toolBar.getItems())
////          item.dispose();
//    	  FormTools formTools = FormsClient.theClient().getFormTools(id);
//    	  formTools.populateToolBar(toolBar);
//      }
//    }
//	toolBar.pack();
//  }



  public static FormsClient theClient() {
    return theClient;
  }

//  static final Color                  WHITE             = new Color(null, 255, 255, 255);
  static FormsClient                  theClient;
  static TabPane                      tabFolder;
//  static ToolBar                      toolBar;

  static Hashtable<String, Tab>       tabs              = new Hashtable<String, Tab>();
  static Vector<Form>                 forms             = new Vector<Form>();
  static Hashtable<String, FormTools> toolDefs          = new Hashtable<String, FormTools>();
//  static Font                         formLabelFont     = Display.getDefault().getSystemFont();//new Font(Display.getDefault(), new FontData("Monaco", 12, SWT.NO));
//  static Font                         formTextFieldFont = Display.getDefault().getSystemFont();//new Font(Display.getDefault(), new FontData("Monaco", 12, SWT.NO));


  private void addComboItem(Message message) {
    String parentId = message.args[0].strValue();
    String value = message.args[1].strValue();
    addComboItem(parentId, value);
  }

  private void addComboItem(final String parentId, final String value) {
	CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(() ->{
      for (Form form : forms)
        form.addComboItem(parentId, value);
      l.countDown();
    });
    try {
     l.await();
    } catch (InterruptedException e) {
     e.printStackTrace();
    }
  }

  private void addItem(Message message) {
    if (message.arity == 2)
      addComboItem(message);
    else addListItem(message);
  }

  private void addListItem(Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    String value = message.args[2].strValue();
    addListItem(parentId, id, value);
  }

  private void addListItem(final String parentId, final String id, final String value) {
	CountDownLatch l = new CountDownLatch(1);  
	Platform.runLater(() ->{
        for (Form form : forms)
          form.addListItem(parentId, id, value);
        l.countDown();
	});
	try {
	 l.await();
	} catch (InterruptedException e) {
	 e.printStackTrace();
	}
  }

  private void addNodeWithIcon(Message message) {
    String parentId = message.args[0].strValue();
    String nodeId = message.args[1].strValue();
    String text = message.args[2].strValue();
    boolean editable = message.args[3].boolValue;
    String icon = message.args[4].strValue();
    int index = -1;
    if (message.arity == 6) index = message.args[5].intValue;
    addNodeWithIcon(parentId, nodeId, text, editable, icon, index);
  }

  private void addNodeWithIcon(final String parentId, final String nodeId, final String text, final boolean editable, final String icon, final int index) {
	CountDownLatch l = new CountDownLatch(1);  
	Platform.runLater(() ->{
        for (Form form : forms)
          form.newNodeWithIcon(parentId, nodeId, text, editable, icon, index);
        l.countDown();
	});
	try {
	 l.await();
	} catch (InterruptedException e) {
	 e.printStackTrace();
	}
  }

  public Value callMessage(Message message) {
    if (message.hasName("getText"))
      return getText(message);
    else if (message.hasName("getTextDimension"))
    	throw new RuntimeException("The method getTextDimension(message) was removed.");
//        return getTextDimension(message);
    else return super.callMessage(message);
  }

  private Value getText(Message message) {
    final String id = message.args[0].strValue();
    final String[] text = new String[] { "" };
	CountDownLatch l = new CountDownLatch(1);  
	Platform.runLater(() ->{
        for (Form form : forms) {
          String textIn = form.getText(id);
          if (textIn != null) text[0] = textIn;
        }
        l.countDown();
	});
	try {
	 l.await();
	} catch (InterruptedException e) {
	 e.printStackTrace();
	}
    return new Value(text[0]);
  }

  private void clearForm(Message message) {
    String id = message.args[0].strValue();
    final Form form = getForm(id);
    if (form != null) {
    	CountDownLatch l = new CountDownLatch(1);  
    	Platform.runLater(() ->{
          form.clear();
          l.countDown();
    	});
    	try {
    	 l.await();
    	} catch (InterruptedException e) {
    	 e.printStackTrace();
    	}
    } else System.err.println("cannot find form to clear " + id);
  }

  private Form getForm(String id) {
    for (Form form : forms)
      if (form.getId().equals(id)) return form;
    return null;
  }

  private FormTools getFormTools(String id) {
    if (toolDefs.containsKey(id))
      return toolDefs.get(id);
    else {
      FormTools formTools = new FormTools(id);
      toolDefs.put(id, formTools);
      return formTools;
    }
  }

  private void inflateButton(String parentId, Node button) {
    String id = XModeler.attributeValue(button, "id");
    String text = XModeler.attributeValue(button, "text");
    int x = Integer.parseInt(XModeler.attributeValue(button, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(button, "y"));
    int width = Integer.parseInt(XModeler.attributeValue(button, "width"));
    int height = Integer.parseInt(XModeler.attributeValue(button, "height"));
    newButton(parentId, id, text, x, y, width, height);
  }

  private void inflateCheck(String parentId, Node check) {
    String id = XModeler.attributeValue(check, "id");
//    String text = XModeler.attributeValue(check, "text");
    int x = Integer.parseInt(XModeler.attributeValue(check, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(check, "y"));
    boolean checked = XModeler.attributeValue(check, "checked").equals("true");
    newCheckBox(parentId, id, x, y, checked);
  }

  private void inflateCombo(String parentId, Node combo) {
    String id = XModeler.attributeValue(combo, "id");
//    String string = XModeler.attributeValue(combo, "string");
    int x = Integer.parseInt(XModeler.attributeValue(combo, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(combo, "y"));
    int width = Integer.parseInt(XModeler.attributeValue(combo, "width"));
    int height = Integer.parseInt(XModeler.attributeValue(combo, "height"));
//    boolean editable = XModeler.attributeValue(combo, "editable").equals("true");
    newComboBox(parentId, id, x, y, width, height);
    NodeList items = combo.getChildNodes();
    for (int i = 0; i < items.getLength(); i++) {
      Node item = items.item(i);
      addComboItem(id, XModeler.attributeValue(item, "item"));
    }
  }

  private void inflateForm(Node form) {
    String id = XModeler.attributeValue(form, "id");
    String label = XModeler.attributeValue(form, "label");
    boolean selected = XModeler.attributeValue(form, "selected").equals("true");
    newForm(id, label, selected);
    NodeList elements = form.getChildNodes();
    for (int i = 0; i < elements.getLength(); i++)
      inflateFormElement(id, elements.item(i));
  }

  private void inflateFormClientElement(Node element) {
    if (element.getNodeName().equals("Form")) inflateForm(element);
    if (element.getNodeName().equals("FormTools")) inflateFormTools(element);
  }

  private void inflateFormElement(String parentId, Node element) {
    if (element.getNodeName().equals("TextField"))
      inflateTextField(parentId, element);
    else if (element.getNodeName().equals("Label"))
      inflateLabel(parentId, element);
    else if (element.getNodeName().equals("TextBox"))
      inflateTextBox(parentId, element);
    else if (element.getNodeName().equals("Combo"))
      inflateCombo(parentId, element);
    else if (element.getNodeName().equals("Check"))
      inflateCheck(parentId, element);
    else if (element.getNodeName().equals("Button"))
      inflateButton(parentId, element);
    else if (element.getNodeName().equals("Tree"))
      inflateTree(parentId, element);
    else if (element.getNodeName().equals("List"))
      inflateList(parentId, element);
    else System.err.println("Unknown type of form element: " + element.getNodeName());
  }

  private void inflateFormTools(Node formTools) {
    String id = XModeler.attributeValue(formTools, "id");
    FormTools tools = getFormTools(id);
    NodeList children = formTools.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      String toolId = XModeler.attributeValue(node, "id");
      String event = XModeler.attributeValue(node, "event");
//      String icon = XModeler.attributeValue(node, "icon");
      tools.addTool(event, toolId);
    }
  }

  private void inflateLabel(String parentId, Node label) {
    String id = XModeler.attributeValue(label, "id");
    String string = XModeler.attributeValue(label, "string");
    int x = Integer.parseInt(XModeler.attributeValue(label, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(label, "y"));
    newText(parentId, id, string, x, y);
    getForm(parentId).getLabels().get(id).setText(string);
  }

  private void inflateList(String parentId, Node list) {
    String id = XModeler.attributeValue(list, "id");
    int x = Integer.parseInt(XModeler.attributeValue(list, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(list, "y"));
    int width = Integer.parseInt(XModeler.attributeValue(list, "width"));
    int height = Integer.parseInt(XModeler.attributeValue(list, "height"));
    newList(parentId, id, x, y, width, height);
    NodeList items = list.getChildNodes();
    for (int i = 0; i < items.getLength(); i++) {
      String itemId = XModeler.attributeValue(items.item(i), "id");
      String value = XModeler.attributeValue(items.item(i), "value");
      addListItem(parentId, itemId, value);
    }
  }

  private void inflateTextBox(String parentId, Node textBox) {
    String id = XModeler.attributeValue(textBox, "id");
    String string = XModeler.attributeValue(textBox, "string");
    int x = Integer.parseInt(XModeler.attributeValue(textBox, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(textBox, "y"));
    int width = Integer.parseInt(XModeler.attributeValue(textBox, "width"));
    int height = Integer.parseInt(XModeler.attributeValue(textBox, "height"));
    boolean editable = XModeler.attributeValue(textBox, "editable").equals("true");
    newTextBox(parentId, id, x, y, width, height, editable);
    getForm(parentId).getBoxes().get(id).setText(string);
  }

  private void inflateTextField(String parentId, Node textField) {
    String id = XModeler.attributeValue(textField, "id");
    String string = XModeler.attributeValue(textField, "string");
    int x = Integer.parseInt(XModeler.attributeValue(textField, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(textField, "y"));
    int width = Integer.parseInt(XModeler.attributeValue(textField, "width"));
    int height = Integer.parseInt(XModeler.attributeValue(textField, "height"));
    boolean editable = XModeler.attributeValue(textField, "editable").equals("true");
    newTextField(parentId, id, x, y, width, height, editable);
    getForm(parentId).getTextFields().get(id).setText(string);
  }

  private void inflateTree(String parentId, Node tree) {
    String id = XModeler.attributeValue(tree, "id");
    int x = Integer.parseInt(XModeler.attributeValue(tree, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(tree, "y"));
    int width = Integer.parseInt(XModeler.attributeValue(tree, "width"));
    int height = Integer.parseInt(XModeler.attributeValue(tree, "height"));
    newTree(parentId, id, x, y, width, height, true);
    inflateTreeItems(tree);
  }

  private void inflateTreeItems(Node node) {
    String id = XModeler.attributeValue(node, "id");
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      String childId = XModeler.attributeValue(child, "id");
      String text = XModeler.attributeValue(child, "text");
      String image = XModeler.attributeValue(child, "image");
      boolean expanded = XModeler.attributeValue(child, "expanded").equals("true");
      addNodeWithIcon(id, childId, text, expanded, image, i);
      inflateTreeItems(child);
    }
  }

  public void inflateXML(final Document doc) {
	CountDownLatch l = new CountDownLatch(1);  
	Platform.runLater(() ->{
        try {
          NodeList formClients = doc.getElementsByTagName("Forms");
          if (formClients.getLength() == 1) {
            Node formClient = formClients.item(0);
            NodeList forms = formClient.getChildNodes();
            for (int i = 0; i < forms.getLength(); i++) {
              Node element = forms.item(i);
              inflateFormClientElement(element);
            }
          } else System.err.println("expecting exactly 1 editor client got: " + formClients.getLength());
        } catch (Throwable t) {
          t.printStackTrace(System.err);
        }
        l.countDown();
	});
	try {
	 l.await();
	} catch (InterruptedException e) {
	 e.printStackTrace();
	}
  }

  private void newButton(Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    String label = message.args[2].strValue();
    int zoom = getDeviceZoomPercent();
    int x = zoom*message.args[3].intValue/100;
    int y = zoom*message.args[4].intValue/100;
    int width = zoom*message.args[5].intValue/100;
    int height = zoom*message.args[6].intValue/100;
    newButton(parentId, id, label, x, y, width, height);
  }

  private void newButton(final String parentId, final String id, final String label, final int x, final int y, final int width, final int height) {
	CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(() ->{
      for (Form form : forms)
        form.newButton(parentId, id, label, x, y, width, height);
      l.countDown();
    });
    try {
     l.await();
    } catch (InterruptedException e) {
     e.printStackTrace();
    }
  }

  private void newCheckBox(Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    int zoom = getDeviceZoomPercent();
    int x = zoom*message.args[2].intValue/100;
    int y = zoom*message.args[3].intValue/100;
    boolean checked = message.args[4].boolValue;
    newCheckBox(parentId, id, x, y, checked);
  }

  private void newCheckBox(final String parentId, final String id, final int x, final int y, final boolean checked) {
		CountDownLatch l = new CountDownLatch(1);  
		Platform.runLater(() ->{
        for (Form form : forms)
          form.newCheckBox(parentId, id, x, y, checked);
        l.countDown();
		});
		try {
		 l.await();
		} catch (InterruptedException e) {
		 e.printStackTrace();
		}
  }

  private void newComboBox(Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    int zoom = getDeviceZoomPercent();
    int x = zoom*message.args[2].intValue/100;
    int y = zoom*message.args[3].intValue/100;
    int width = zoom*message.args[4].intValue/100;
    int height = zoom*message.args[5].intValue/100;
    newComboBox(parentId, id, x, y, width, height);
  }

  private void newComboBox(final String parentId, final String id, final int x, final int y, final int width, final int height) {
	CountDownLatch l = new CountDownLatch(1);  
	Platform.runLater(() ->{
      for (Form form : forms)
        form.newComboBox(parentId, id, x, y, width, height);
      l.countDown();
	});
	try {
	 l.await();
	} catch (InterruptedException e) {
	 e.printStackTrace();
	}
  }

  private void newForm(Message message) {
    String id = message.args[0].strValue();
//    String type = message.args[1].strValue(); // needed?
    String label = message.args[2].strValue();
	System.err.println("*** NEW FORM *** " + id + " *** " + label + " ***");
    newForm(id, label, true);
	System.err.println("NEW FORM done");
  }

  private void newForm(final String id, final String label, final boolean selected) {
	  CountDownLatch l = new CountDownLatch(1); 
	  
	  Platform.runLater(()->{
		  System.err.println("Platform.runLater START");
    	Tab tabItem = new Tab(label);
    	tabFolder.getTabs().add(tabItem);
//    	tabFolder.toFront();
        tabs.put(id, tabItem);
//        CTabItem tabItem = new CTabItem(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
//        tabItem.setText(label);
//        tabs.put(id, tabItem);
        Form form = new Form(tabItem, id);
        // REMOVED // tabItem.setControl(form.getForm());
        // REMOVED // tabItem.setShowClose(true);
        forms.add(form);
        // REMOVED // if (selected) tabFolder.setSelection(tabItem);
//      }
        System.err.println("Platform.runLater END");
        l.countDown();
	  });
	  try {
	   System.err.println("waiting...");
	   l.await();
	   System.err.println("waited");
	  } catch (InterruptedException e) {
	   e.printStackTrace();
	  }
  }

  private void newList(Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    int zoom = getDeviceZoomPercent();
    int x = zoom*message.args[2].intValue/100;
    int y = zoom*message.args[3].intValue/100;
    int width = zoom*message.args[4].intValue/100;
    int height = zoom*message.args[5].intValue/100;
    newList(parentId, id, x, y, width, height);
  }

  private void newList(String parentId, String id, int x, int y, int width, int height) {
    for (Form form : forms)
      form.newList(parentId, id, x, y, width, height);
  }

  private void newText(Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    String string = message.args[2].strValue();
    int zoom = getDeviceZoomPercent();
    int x = zoom*message.args[3].intValue/100;
    int y = zoom*message.args[4].intValue/100;
    newText(parentId, id, string, x, y);
  }

  private void newText(final String parentId, final String id, final String string, final int x, final int y) {
    final Form form = getForm(parentId);
    if (form != null) {
        CountDownLatch l = new CountDownLatch(1);  
  	    Platform.runLater(() ->{
        form.newText(id, string, x, y);
        l.countDown();
  	  });
  	  try {
  	   l.await();
  	  } catch (InterruptedException e) {
  	   e.printStackTrace();
  	  }
    } else System.err.println("cannot find text parent " + parentId);
  }

  private void newTextBox(Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    int zoom = getDeviceZoomPercent();
    int x = zoom*message.args[2].intValue/100;
    int y = zoom*message.args[3].intValue/100;
    int width = zoom*message.args[4].intValue/100;
    int height = zoom*message.args[5].intValue/100;
    boolean editable = message.args[6].boolValue;
    newTextBox(parentId, id, x, y, width, height, editable);
  }

  private void newTextBox(final String parentId, final String id, final int x, final int y, final int width, final int height, final boolean editable) {
    CountDownLatch l = new CountDownLatch(1);  
	Platform.runLater(() ->{
      for (Form form : forms)
        form.newTextBox(parentId, id, x, y, width, height, editable);
      l.countDown();
	});
	try {
	 l.await();
	} catch (InterruptedException e) {
	 e.printStackTrace();
	}
  }

  private void newTextField(Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    int zoom = getDeviceZoomPercent();
    int x = zoom*message.args[2].intValue/100;
    int y = zoom*message.args[3].intValue/100;
    int width = zoom*message.args[4].intValue/100;
    int height = zoom*message.args[5].intValue/100;
    boolean editable = message.args[6].boolValue;
    newTextField(parentId, id, x, y, width, height, editable);
  }

	private void newTextField(final String parentId, final String id, final int x, final int y, final int width,
			final int height, final boolean editable) {
		final Form form = getForm(parentId);
		if (form != null) {
			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
				form.newTextField(id, x, y, width, height, editable);
				l.countDown();
			});
			try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else
			System.err.println("cannot find text field parent " + parentId);
	}

  private void newTree(Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    int zoom = getDeviceZoomPercent();
    int x = zoom*message.args[2].intValue/100;
    int y = zoom*message.args[3].intValue/100;
    int width = zoom*message.args[4].intValue/100;
    int height = zoom*message.args[5].intValue/100;
    boolean editable = message.args[6].boolValue;
    newTree(parentId, id, x, y, width, height, editable);
  }

  private void newTree(final String parentId, final String id, final int x, final int y, final int width, final int height, final boolean editable) {
	CountDownLatch l = new CountDownLatch(1);  
	Platform.runLater(() ->{
    for (Form form : forms)
      form.newTree(parentId, id, x, y, width, height, editable);
    l.countDown();
	});
	try {
	 l.await();
	} catch (InterruptedException e) {
	 e.printStackTrace();
	}
  }

  public boolean processMessage(Message message) {
    return false;
  }

	private void selectForm(final String id) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			
		 if (tabs.containsKey(id))
		 tabFolder.getSelectionModel().select(tabs.get(id));	
			
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

//		System.err.println("selectForm not implemented yet");
		/* REMOVED */
		// runOnDisplay(new Runnable() {
		// public void run() {
		// if (tabs.containsKey(id))
		// tabFolder.setSelection(tabs.get(id));
		// else System.err.println("cannot find form: " + id);
		//// select();
		// }
		// });
	}
  
  private void removeNode(Message message) {
    final String id = message.args[0].strValue();
	CountDownLatch l = new CountDownLatch(1);  
	Platform.runLater(() ->{
          for (Form form : forms) {
            form.removeItem(id);
          }
          l.countDown();
	});
	try {
	 l.await();
	} catch (InterruptedException e) {
	 e.printStackTrace();
	}  
  }

  private void maximiseToCanvas(Message message) {
	    final String id = message.args[0].strValue();
		CountDownLatch l = new CountDownLatch(1);  
		Platform.runLater(() ->{
	          for (Form form : forms)
	            form.maximiseToCanvas(id);
	          l.countDown();
		});
		try {
		 l.await();
		} catch (InterruptedException e) {
		 e.printStackTrace();
		}
  }

	private void changesMade(Message message) {
		final String id = message.args[0].strValue();
		final boolean made = message.args[1].boolValue;
		CountDownLatch l = new CountDownLatch(1);  
		Platform.runLater(() ->{
				for (Form form : forms)
					form.changesMade(id, made);
				  l.countDown();
		});
		try {
		 l.await();
		} catch (InterruptedException e) {
		 e.printStackTrace();
		}
	}

  public void sendMessage(final Message message) {
//	  System.err.println("MESSAGE: "+message);
    if (message.hasName("newForm"))
      newForm(message);
    else if (message.hasName("setTool"))
      setTool(message);
    else if (message.hasName("newText")) {
      newText(message);
      String string = message.args[2].strValue();
      if(string.length() < 2) {
    	  System.err.println("####### MESSAGE TO FORM CLIENT FOR EMPTY LABEL: "+message);
      }
    }
    else if (message.hasName("setText"))
      setText(message);
    else if (message.hasName("newTextField"))
      newTextField(message);
    else if (message.hasName("clearForm"))
      clearForm(message);
    else if (message.hasName("newList"))
      newList(message);
    else if (message.hasName("addItem"))
      addItem(message);
    else if (message.hasName("newTextBox"))
      newTextBox(message);
    else if (message.hasName("newComboBox"))
      newComboBox(message);
    else if (message.hasName("setSelection"))
      setSelection(message);
    else if (message.hasName("newCheckBox"))
      newCheckBox(message);
    else if (message.hasName("newButton"))
      newButton(message);
    else if (message.hasName("newTree"))
      newTree(message);
    else if (message.hasName("addNodeWithIcon"))
      addNodeWithIcon(message);
    else if (message.hasName("setVisible"))
      setVisible(message);
    else if (message.hasName("clear"))
      clear(message);
    else if (message.hasName("check"))
      check(message);
    else if (message.hasName("uncheck"))
      uncheck(message);
    else if (message.hasName("removeNode"))
      removeNode(message);
    else if (message.hasName("maximiseToCanvas"))
      maximiseToCanvas(message);
    else if (message.hasName("changesMade"))
      changesMade(message);
    else if (message.hasName("move"))
        move(message);
    else if (message.hasName("setSize"))
      setSize(message);
    else if (message.hasName("delete"))
      delete(message);
    else {
//System.out.println("------- UNKNOWN");    	
    	super.sendMessage(message);
    } 
  }
  
  private void move(Message message) {
    Value id = message.args[0];
    Value x = message.args[1];
    Value y = message.args[2];
    for (Form form : forms)
      form.move(id.strValue(), x.intValue, y.intValue);
  }
  
  private void setSize(Message message) {
    final Value id = message.args[0];
    final Value width = message.args[1];
    final Value height = message.args[2];
    for (Form form : forms)
      form.setSize(id.strValue(), width.intValue, height.intValue);
  }
  
  private void delete(Message message) {
    final Value id = message.args[0];
    for (Form form : forms)
      form.delete(id.strValue());
  }

  private void check(Message message) {
    String id = message.args[0].strValue();
    check(id);
  }

  private void check(final String id) {
		CountDownLatch l = new CountDownLatch(1);  
		Platform.runLater(() ->{
        for (Form form : forms)
          form.check(id);
        l.countDown();
		});
		try {
		 l.await();
		} catch (InterruptedException e) {
		 e.printStackTrace();
		}
  }

  private void uncheck(final String id) {
		CountDownLatch l = new CountDownLatch(1);  
		Platform.runLater(() ->{
        for (Form form : forms)
          form.uncheck(id);
        l.countDown();
		});
		try {
		 l.await();
		} catch (InterruptedException e) {
		 e.printStackTrace();
		}
  }

  private void uncheck(Message message) {
    String id = message.args[0].strValue();
    uncheck(id);
  }

  private void clear(Message message) {
    String id = message.args[0].strValue();
    clear(id);
  }

  private void clear(final String id) {
	CountDownLatch l = new CountDownLatch(1);  
	Platform.runLater(() ->{
        for (Form form : forms) {
          form.clear(id);
        }
        l.countDown();
	});
	try {
	 l.await();
	} catch (InterruptedException e) {
	 e.printStackTrace();
	}
  }

  private void setSelection(Message message) {
    String comboId = message.args[0].strValue();
    int index = message.args[1].intValue;
    setSelection(comboId, index);
  }

  private void setSelection(final String comboId, final int index) {
    CountDownLatch l = new CountDownLatch(1);  
	Platform.runLater(() ->{
      for (Form form : forms)
        form.setSelection(comboId, index);
      l.countDown();
	});
	try {
	 l.await();
	} catch (InterruptedException e) {
	 e.printStackTrace();
	}
  }

  private void setText(Message message) {
    final Value id = message.args[0];
    final Value text = message.args[1];
    CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(() ->{
      for (Form form : forms) {
        form.setText(id.strValue(), text.strValue());
      }
      l.countDown();
    });
    try {
     l.await();
    } catch (InterruptedException e) {
     e.printStackTrace();
    }
  }

  private void setTool(Message message) {
	  System.err.println("setTool: "+ message);
    Value id = message.args[0];
    Value name = message.args[1];
    Value enabled = message.args[2];
//    if (enabled.boolValue) {
      if (tabs.containsKey(id.strValue())) {
        FormTools formTools = getFormTools(id.strValue());
//        formTools.addTool(name.strValue(), id.strValue(), enabled.boolValue);
        formTools.setTools(name.strValue(), id.strValue(), enabled.boolValue);
      } else System.err.println("cannot find form " + id);
//    }else{
//    	System.err.println("disabled tool: " + name.strValue());
//    }
  }

  private void setVisible(Message message) {
    String id = message.args[0].strValue();
    selectForm(id);
//    runOnDisplay(new Runnable() {
//        public void run() {
//          select();
//        }
//     });
  }

  public void toolItemEvent(String event, String id, boolean enabled) {
		Message m = null;
		if (event.equals("lockForm")) {
			m = getHandler().newMessage(event, 2);
			Value v = new Value(id);
			m.args[0] = v;
			Value enabled_value = new Value(!enabled);
			m.args[1] = enabled_value;
		} else {
			m = getHandler().newMessage(event, 1);
			Value v = new Value(id);
			m.args[0] = v;
		}
		getHandler().raiseEvent(m);
  }
  
  public void toolItemEvent(String event, String id) {
//    Message m = getHandler().newMessage(event, 1);
//    Value v = new Value(id);
//    m.args[0] = v;
//    getHandler().raiseEvent(m);
	  toolItemEvent(event, id, false);
  }

  public void writeXML(PrintStream out) {
		 /*REMOVED*/
//    out.print("<Forms>");
//    for (Form form : forms)
//      form.writeXML(out, tabFolder.getSelection() == tabs.get(form.getId()), tabs.get(form.getId()).getText());
//    for (FormTools tools : toolDefs.values())
//      tools.writeXML(out);
//    out.print("</Forms>");
  }

//  public void close(CTabFolderEvent event) {
//    CTabItem item = (CTabItem) event.item;
//    String id = getId(item);
//    if (id != null && getForm(id) != null) {
//      EventHandler handler = getHandler();
//      Message message = handler.newMessage("formClosed", 1);
//      message.args[0] = new Value(id);
//      handler.raiseEvent(message);
//      forms.remove(getForm(id));
//      tabs.remove(id);
//    }
//  }
//
//  private String getId(CTabItem item) {
//    for (String id : tabs.keySet())
//      if (tabs.get(id).equals(item)) return id;
//    return null;
//  }

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

  public void doubleClick(String id) {
    EventHandler handler = getHandler();
    Message message = handler.newMessage("doubleSelected", 1);
    message.args[0] = new Value(id);
    handler.raiseEvent(message);
  }
}