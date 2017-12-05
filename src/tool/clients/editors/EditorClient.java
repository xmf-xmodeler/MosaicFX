package tool.clients.editors;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tool.clients.Client;
import tool.clients.EventHandler;
import tool.clients.diagrams.DiagramClient;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class EditorClient extends Client implements LocationListener, CTabFolder2Listener {

  public static final Color             LINE_HIGHLIGHT = new Color(XModeler.getXModeler().getDisplay(), 192, 192, 192);
  public static final Color             RED            = new Color(XModeler.getXModeler().getDisplay(), 255, 0, 0);
  public static final Color             GREY           = new Color(XModeler.getXModeler().getDisplay(), 192, 192, 192);
  public static final Color             WHITE          = new Color(XModeler.getXModeler().getDisplay(), 255, 255, 255);
  public static final Color             GREEN          = new Color(XModeler.getXModeler().getDisplay(), 0, 170, 0);
  public static final Color             BLACK          = new Color(XModeler.getXModeler().getDisplay(), 0, 0, 0);

  static EditorClient                   theClient;
  static CTabFolder                     tabFolder;

  static Hashtable<String, CTabItem>    tabs           = new Hashtable<String, CTabItem>();
  static Hashtable<String, Browser>     browsers       = new Hashtable<String, Browser>();
  static Hashtable<String, ITextEditor> editors        = new Hashtable<String, ITextEditor>();

  boolean browserLocked = true;
  
  Hashtable<Browser, Stack<String>> backQueues     = new Hashtable<Browser, Stack<String>>();
  Hashtable<Browser, String>        browserCurrent = new Hashtable<Browser, String>();
  Hashtable<Browser, Stack<String>> forwardQueues  = new Hashtable<Browser, Stack<String>>();
  
  public static void start(CTabFolder tabFolder, int style) {
    EditorClient.tabFolder = tabFolder;
  }

  public static EditorClient theClient() {
    return theClient;
  }

  

  public EditorClient() {
    super("com.ceteva.text");
    theClient = this;
    tabFolder.addCTabFolder2Listener(this);
  }

  private void addLineHighlight(Message message) {
    String id = message.args[0].strValue();
    int line = message.args[1].intValue;
    addLineHighlight(id, line);
  }

  private void addLineHighlight(final String id, final int line) {
    runOnDisplay(new Runnable() {
      public void run() {
        if (editors.containsKey(id))
          editors.get(id).addLineHighlight(line);
        else System.err.println("cannot find editor: " + id);
      }
    });
  }

  private void addMultilineRule(Message message) {
    String id = message.args[0].strValue();
    String start = message.args[1].strValue();
    String end = message.args[2].strValue();
    String color = message.args[3].strValue();
    if (color.equals("red"))
      addMultilineRule(id, start, end, 255, 0, 0);
    else if (color.equals("green"))
      addMultilineRule(id, start, end, 0, 153, 0);
    else if (color.equals("blue"))
      addMultilineRule(id, start, end, 50, 50, 255);
    else if (color.equals("gray"))
      addMultilineRule(id, start, end, 120, 120, 120);
    else {
      String[] colours = color.split(",");
      if (colours.length == 3) {
        int red = Integer.parseInt(colours[0]);
        int blue = Integer.parseInt(colours[1]);
        int green = Integer.parseInt(colours[2]);
        addMultilineRule(id, start, end, red, blue, green);
      } else System.err.println("unknown color: " + Arrays.toString(colours));
    }
  }

  private void addMultilineRule(String id, String start, String end, int red, int green, int blue) {
    for (ITextEditor editor : editors.values())
      editor.addMultilineRule(id, start, end, red, green, blue);
  }

//  private void addToolBar(CTabFolder parent, Browser browser) {
//    ToolBar toolbar = new ToolBar(parent, SWT.NONE);
//    FormData data = new FormData();
//    data.top = new FormAttachment(0, 5);
//    toolbar.setLayoutData(data);
//    ToolItem itemBack = new ToolItem(toolbar, SWT.PUSH);
//    itemBack.setText(("Back"));
//    ToolItem itemForward = new ToolItem(toolbar, SWT.PUSH);
//    itemForward.setText(("Forward"));
//    final ToolItem itemStop = new ToolItem(toolbar, SWT.PUSH);
//    itemStop.setText(("Stop"));
//    final ToolItem itemRefresh = new ToolItem(toolbar, SWT.PUSH);
//    itemRefresh.setText(("Refresh"));
//    final ToolItem itemGo = new ToolItem(toolbar, SWT.PUSH);
//    itemGo.setText(("Go"));
//
//    itemBack.setEnabled(browser.isBackEnabled());
//    itemForward.setEnabled(browser.isForwardEnabled());
//    // Listener listener = new Listener() {
//    // public void handleEvent(Event event) {
//    // ToolItem item = (ToolItem) event.widget;
//    // if (item == itemBack)
//    // browser.back();
//    // else if (item == itemForward)
//    // browser.forward();
//    // else if (item == itemStop)
//    // browser.stop();
//    // else if (item == itemRefresh)
//    // browser.refresh();
//    // else if (item == itemGo)
//    // browser.setUrl(locationBar.getText());
//    // }
//    // };
//  }

  private void addWordRule(Message message) {
    if (message.arity == 3)
      addWordRuleNamedColor(message);
    else addWordRuleColor(message);
  }

  private void addWordRuleColor(Message message) {
    String id = message.args[0].strValue();
    String text = message.args[1].strValue();
    int red = message.args[2].intValue;
    int green = message.args[3].intValue;
    int blue = message.args[4].intValue;
    addWordRuleColor(id, text, red, green, blue);
  }

  private void addWordRuleColor(String id, String text, int red, int green, int blue) {
    for (ITextEditor editor : editors.values())
      editor.addWordRule(id, text, red, green, blue);
  }

  private void addWordRuleNamedColor(Message message) {
    String id = message.args[0].strValue();
    String text = message.args[1].strValue();
    String color = message.args[2].strValue();
    if (color.equals("red"))
      addWordRuleColor(id, text, 255, 0, 0);
    else if (color.equals("green"))
      addWordRuleColor(id, text, 0, 153, 0);
    else if (color.equals("blue"))
      addWordRuleColor(id, text, 50, 50, 255);
    else if (color.equals("torquoise"))
      addWordRuleColor(id, text, 0, 120, 120);
    else {
      String[] colours = color.split(",");
      if (colours.length == 3) {
        int red = Integer.parseInt(colours[0]);
        int blue = Integer.parseInt(colours[1]);
        int green = Integer.parseInt(colours[2]);
        addWordRuleColor(id, text, red, blue, green);
      } else System.err.println("unknown color: " + Arrays.toString(colours));
    }
  }

  public Value callMessage(Message message) {
    if (message.hasName("getWelcomePage")) {
      // help page if it is available.
      URL location = EditorClient.class.getProtectionDomain().getCodeSource().getLocation();
      String path = location.toString();
      path = path.substring(0, path.length() - 4); // delete "/bin" from string
      path += "web/index.html";
      // System.err.println("getWelcomePage: >" + path + "<");
      return new Value(path);
    }
    if (message.hasName("getText")) { return getText(message); }
    return super.callMessage(message);
  }

  public void changed(LocationEvent event) {
  }

  public void changing(LocationEvent event) {
    if (browserLocked) {
      event.doit = false;
      Browser browser = (Browser) event.widget;
      EventHandler handler = getHandler();
      Message message = handler.newMessage("urlRequest", 2);
      message.args[0] = new Value(getId(browser));
      message.args[1] = new Value(event.location);
      // Can be used to push on an undo stack?
      // System.err.println("FROM: " + browser.getText());
      handler.raiseEvent(message);
    } else {
      browserLocked = true;
    }
  }

  private void clearHighlights(Message message) {
    String id = message.args[0].strValue();
    clearHighlights(id);
  }

  private void clearHighlights(final String id) {
    runOnDisplay(new Runnable() {
      public void run() {
        if (editors.containsKey(id))
          editors.get(id).clearHighlights();
        else System.err.println("cannot find editor: " + id);
      }
    });
  }

  public void close(CTabFolderEvent event) {
    // Careful because the diagrams and files share the same tab folder...
    CTabItem item = (CTabItem) event.item;
    String id = getId(item);
    if (id != null && (editors.containsKey(id) || browsers.containsKey(id))) {
      EventHandler handler = getHandler();
      Message message = handler.newMessage("textClosed", 1);
      message.args[0] = new Value(id);
      handler.raiseEvent(message);
      editors.remove(id);
      browsers.remove(id);
      tabs.remove(id);
    } else {
      DiagramClient.theClient().close(event);
    }
  }

  private String getId(Browser browser) {
    for (String id : browsers.keySet())
      if (browsers.get(id) == browser) return id;
    return null;
  }

  private String getId(CTabItem item) {
    for (String id : tabs.keySet())
      if (tabs.get(id) == item) return id;
    return null;
  }

  private Value getText(final Message message) {
    final String id = message.args[0].strValue();
    final Value[] result = new Value[] { null };
    runOnDisplay(new Runnable() {
      public void run() {
        for (String editorId : editors.keySet()) {
          if (id.equals(editorId)) {
            result[0] = new Value(editors.get(editorId).getString());
          }
        }
      }
    });
    if (result[0] == null)
      throw new Error("Cannot find editor with id " + id);
    else return result[0];
  }

  private void inflateBrowser(Node browser) {
    String id = XModeler.attributeValue(browser, "id");
    String label = XModeler.attributeValue(browser, "label");
    String tooltip = XModeler.attributeValue(browser, "toolTip");
    String url = XModeler.attributeValue(browser, "url");
    if (url.equals("welcome")) {
      URL location = EditorClient.class.getProtectionDomain().getCodeSource().getLocation();
      url = location.toString();
      url = url.substring(0, url.length() - 4); // delete "/bin" from string
      url += "web/index.html";
    }
    String text = XModeler.attributeValue(browser, "text");
    newBrowser(id, label, tooltip, url, text);
  }

  private void inflateEditorElement(Node editor) {
    if (editor.getNodeName().equals("NewTextEditor")) inflateNewTextEditor(editor);
    if (editor.getNodeName().equals("Browser")) inflateBrowser(editor);
  }

  private void inflateNewTextEditor(Node NewTextEditor) {
    final String id = XModeler.attributeValue(NewTextEditor, "id");
    String text = XModeler.attributeValue(NewTextEditor, "text");
    String label = XModeler.attributeValue(NewTextEditor, "label");
    String toolTip = XModeler.attributeValue(NewTextEditor, "toolTip");
    final boolean selected = XModeler.attributeValue(NewTextEditor, "selected").equals("true");
    boolean editable = XModeler.attributeValue(NewTextEditor, "editable").equals("true");
    boolean lineNumbers = XModeler.attributeValue(NewTextEditor, "lineNumbers").equals("true");
    int fontHeight = Integer.parseInt(XModeler.attributeValue(NewTextEditor, "fontHeight"));
    newNewTextEditor(id, label, toolTip, editable, lineNumbers, text);
    final ITextEditor editor = editors.get(id);
    editor.inflate(NewTextEditor);
    runOnDisplay(new Runnable() {
      public void run() {
        editor.getText().redraw();
        if (selected) tabFolder.setSelection(tabs.get(id));
      }
    });
  }

  public void inflateXML(Document doc) {
    NodeList editorClients = doc.getElementsByTagName("Editors");
    if (editorClients.getLength() == 1) {
      Node editorClient = editorClients.item(0);
      NodeList editors = editorClient.getChildNodes();
      for (int i = 0; i < editors.getLength(); i++) {
        Node editor = editors.item(i);
        inflateEditorElement(editor);
      }
    } else System.err.println("expecting exactly 1 editor client got: " + editorClients.getLength());
  }

  private boolean isURL(String url) {
    return url.startsWith("http://") || url.startsWith("file:/");
  }

  public void maximize(CTabFolderEvent event) {
  }

  public void minimize(CTabFolderEvent event) {
  }

  private void newBrowser(Message message) {
    String id = message.args[0].strValue();
    String label = message.args[1].strValue();
    String tooltip = message.args[2].strValue();
    String url = message.args[3].strValue();
    newBrowser(id, label, tooltip, url, "");
  }

  private void newBrowser(final String id, final String label, String tooltip, final String url, final String text) {
    runOnDisplay(new Runnable() {
      public void run() {
        CTabItem tabItem = new CTabItem(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        tabItem.setText(label);
        tabItem.setShowClose(true);
        tabs.put(id, tabItem);
        Composite browserParent = new Composite(tabFolder, SWT.NONE);
        Vector<Object> buttons = new Vector<Object>();
        Button up = new Button(browserParent, SWT.PUSH);
        up.setText("+");
        buttons.add(up);
        Button down = new Button(browserParent, SWT.PUSH);
        down.setText("-");
        buttons.add(down);
        Button b1a = new Button(browserParent, SWT.PUSH);
        b1a.setImage(new Image(tabItem.getDisplay(), new ImageData("icons/User/Arrow4Left.gif")));
        buttons.addElement(b1a);
        Button b1b = new Button(browserParent, SWT.PUSH);
        b1b.setImage(new Image(tabItem.getDisplay(), new ImageData("icons/User/Arrow4Right.gif")));
        buttons.addElement(b1b);
        Label b2 = new Label(browserParent, SWT.NONE);
        b2.setText("URL:");
        buttons.addElement(b2);
        final Text b3 = new Text(browserParent, SWT.BORDER);
        b3.setText("Enter URL here...");
        buttons.addElement(b3);
        final Browser browser = new Browser(browserParent, SWT.BORDER);
        final int defaultZoom = 100;// XModeler.getDeviceZoomPercent();
        final int[] zoom = new int[] { defaultZoom };
        up.addListener(SWT.Selection, new Listener() {
          public void handleEvent(Event arg0) {
            zoom[0] += defaultZoom / 10;
            browser.execute("document.body.style.zoom = \"" + zoom[0] + "%\"");
            browser.redraw();
          }
        });
        down.addListener(SWT.Selection, new Listener() {
          public void handleEvent(Event arg0) {
            if (zoom[0] > defaultZoom / 10) {
              zoom[0] -= defaultZoom / 10;
              browser.execute("document.body.style.zoom = \"" + zoom[0] + "%\"");
              browser.redraw();
            }
          }
        });
        b1a.addListener(SWT.Selection, new Listener() {
          public void handleEvent(Event arg0) {
            // browser.back();
            if (!getBackQueue(browser).isEmpty()) {
              if (browserCurrent.containsKey(browser)) {
                getForwardQueue(browser).push(browserCurrent.get(browser));
              }
              setUrl(id, getBackQueue(browser).pop(), false);
            }
          }
        });
        b1b.addListener(SWT.Selection, new Listener() {
          public void handleEvent(Event arg0) {
            // browser.forward();
            if (!getForwardQueue(browser).isEmpty()) {
              if (browserCurrent.containsKey(browser)) {
                getBackQueue(browser).push(browserCurrent.get(browser));
              }
              setUrl(id, getForwardQueue(browser).pop(), false);
            }
          }
        });
        b3.addListener(SWT.DefaultSelection, new Listener() {
          public void handleEvent(Event e) {
            browser.setUrl(b3.getText());
          }
        });
        browser.addLocationListener(new LocationListener() {
          public void changed(LocationEvent event) {
            if (event.top) b3.setText(event.location);
          }

          public void changing(LocationEvent event) {
          }
        });
        tabItem.setControl(browserParent);
        browser.setText(text);
        browser.setJavascriptEnabled(true);
        int buttonCount = buttons.size();
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = buttonCount;
        browserParent.setLayout(gridLayout);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = buttonCount;
        browser.setLayoutData(gd);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = GridData.FILL;
        b3.setLayoutData(gd);
        browserLocked = false;
        if (isURL(url)) {
          browser.setUrl(url);
        }
        browsers.put(id, browser);
        browser.setVisible(true);
        browser.addLocationListener(EditorClient.this);
        tabFolder.setSelection(tabItem);
      }
    });
  }

  private void newNewTextEditor(Message message) {
    String id = message.args[0].strValue();
    String label = message.args[1].strValue();
    String toolTip = message.args[2].strValue();
    boolean editable = message.args[3].boolValue;
    newNewTextEditor(id, label, toolTip, editable, true, "");
  }

  private void newNewTextEditor(final String id, final String label, final String toolTip, final boolean editable, final boolean lineNumbers, final String text) {
    Display.getDefault().syncExec(new Runnable() {
      public void run() {
        CTabItem tabItem = new CTabItem(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        tabItem.setText(label);
        tabItem.setToolTipText(toolTip);
        tabItem.setShowClose(true);
        tabs.put(id, tabItem);
        try {
          Class<?> textEditorClass = Class.forName(XModeler.textEditorClass);
          Constructor<?> cnstr = textEditorClass.getConstructor(new Class<?>[] { String.class, String.class, CTabFolder.class, Boolean.TYPE, Boolean.TYPE, String.class });
          ITextEditor editor = (ITextEditor) cnstr.newInstance(id, label, tabFolder, editable, lineNumbers, text);
          // ITextEditor editor = new TextEditor(id, label, tabFolder, editable, lineNumbers, text);
          tabItem.setControl(editor.getText());
          editors.put(id, editor);
          tabFolder.setSelection(tabItem);
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        } catch (NoSuchMethodException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (SecurityException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (InstantiationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IllegalArgumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });
  }

  public boolean processMessage(Message message) {
    return false;
  }

  public void restore(CTabFolderEvent event) {
  }

  public void sendMessage(final Message message) {
    if (message.hasName("newBrowser"))
      newBrowser(message);
    else if (message.hasName("setUrl"))
      setUrl(message);
    else if (message.hasName("newTextEditor"))
      newNewTextEditor(message);
    else if (message.hasName("setText"))
      setText(message);
    else if (message.hasName("addWordRule"))
      addWordRule(message);
    else if (message.hasName("addMultilineRule"))
      addMultilineRule(message);
    else if (message.hasName("setDirty"))
      setDirty(message);
    else if (message.hasName("setClean"))
      setClean(message);
    else if (message.hasName("setName"))
      setName(message);
    else if (message.hasName("showLine"))
      showLine(message);
    else if (message.hasName("addLineHighlight"))
      addLineHighlight(message);
    else if (message.hasName("clearHighlights"))
      clearHighlights(message);
    else if (message.hasName("setFocus"))
      setFocus(message);
    else if (message.hasName("syntaxError"))
      syntaxError(message);
    else if (message.hasName("clearErrors"))
      clearErrors(message);
    else if (message.hasName("unboundVar"))
      unboundVar(message);
    else if (message.hasName("rendering"))
      rendering(message);
    else if (message.hasName("varType"))
      varType(message);
    else if (message.hasName("varDec"))
      varDec(message);
    else if (message.hasName("setTooltip"))
      setTooltip(message);
    else if (message.hasName("ast"))
      ast(message);
    else if (message.hasName("addTerminates"))
      terminates(message);
    else if (message.hasName("setSignature"))
      setSignature(message);
    else if (message.hasName("action"))
      action(message);
    else super.sendMessage(message);
  }

  private void action(Message message) {
    String id = message.args[0].strValue();
    String name = message.args[1].strValue();
    Value[] args = message.args[2].values;
    int charStart = message.args[3].intValue;
    int charEnd = message.args[4].intValue;
    final ITextEditor editor = editors.get(id);
    runOnDisplay(new Runnable() {
      public void run() {
        editor.action(name,args,charStart,charEnd);
      }
    });
  }

  private void setSignature(Message message) {
    String id = message.args[0].strValue();
    Value[] entries = message.args[1].values;
    final ITextEditor editor = editors.get(id);
    runOnDisplay(new Runnable() {
      public void run() {
        editor.setSignature(entries);
      }
    });
  }

  private void terminates(Message message) {
    String id = message.args[0].strValue();
    String end = message.args[1].strValue();
    String start = message.args[2].strValue();
    final ITextEditor editor = editors.get(id);
    runOnDisplay(new Runnable() {
      public void run() {
        editor.terminates(end,start);
      }
    });
  }

  private void ast(Message message) {
    String id = message.args[0].strValue();
    String tooltip = message.args[1].strValue();
    int charStart = message.args[2].intValue;
    int charEnd = message.args[3].intValue;
    final ITextEditor editor = editors.get(id);
    runOnDisplay(new Runnable() {
      public void run() {
        editor.ast(tooltip, charStart, charEnd);
      }
    });
  }

  private void setTooltip(Message message) {
    String id = message.args[0].strValue();
    String tooltip = message.args[1].strValue();
    int charStart = message.args[2].intValue;
    int charEnd = message.args[3].intValue;
    final ITextEditor editor = editors.get(id);
    runOnDisplay(new Runnable() {
      public void run() {
        editor.setTooltip(tooltip, charStart, charEnd);
      }
    });
  }

  private void varDec(Message message) {
    String id = message.args[0].strValue();
    int charStart = message.args[1].intValue;
    int charEnd = message.args[2].intValue;
    int decStart = message.args[3].intValue;
    int decEnd = message.args[4].intValue;
    final ITextEditor editor = editors.get(id);
    runOnDisplay(new Runnable() {
      public void run() {
        editor.varDec(charStart, charEnd, decStart, decEnd);
      }
    });
  }

  private void varType(Message message) {
    // Record the type of a variable.
  }

  private void rendering(Message message) {
    String id = message.args[0].strValue();
    boolean state = message.args[1].boolValue;
    final ITextEditor editor = editors.get(id);
    runOnDisplay(new Runnable() {
      public void run() {
        editor.setRendering(state);
      }
    });
  }

  private void unboundVar(Message message) {
    String id = message.args[0].strValue();
    String name = message.args[1].strValue();
    int charStart = message.args[2].intValue;
    int charEnd = message.args[3].intValue;
    final ITextEditor editor = editors.get(id);
    runOnDisplay(new Runnable() {
      public void run() {
        editor.unboundVar(name, charStart, charEnd);
      }
    });
  }

  private void syntaxError(Message message) {
    String id = message.args[0].strValue();
    int pos = message.args[1].intValue;
    String error = message.args[2].strValue();
    final ITextEditor editor = editors.get(id);
    runOnDisplay(new Runnable() {
      public void run() {
        editor.syntaxError(pos, error);
      }
    });
  }

  private void clearErrors(Message message) {
    String id = message.args[0].strValue();
    final ITextEditor editor = editors.get(id);
    runOnDisplay(new Runnable() {
      public void run() {
        editor.clearErrors();
      }
    });
  }

  private void setClean(Message message) {
    String id = message.args[0].strValue();
    setClean(id);
  }

  public void setClean(String id) {
    final CTabItem item = tabs.get(id);
    final ITextEditor editor = editors.get(id);
    runOnDisplay(new Runnable() {
      public void run() {
        editor.setDirty(false);
        item.setText(editor.getLabel());
      }
    });
  }

  private void setDirty(Message message) {
    String id = message.args[0].strValue();
    setDirty(id);
  }

  public void setDirty(String id) {
    final CTabItem item = tabs.get(id);
    final ITextEditor editor = editors.get(id);
    runOnDisplay(new Runnable() {
      public void run() {
        editor.setDirty(true);
        item.setText("*" + editor.getLabel());
      }
    });
  }

  private void setFocus(Message message) {
    String id = message.args[0].strValue();
    setFocus(id);
  }

  private void setFocus(final String id) {
    runOnDisplay(new Runnable() {
      public void run() {
        if (tabs.containsKey(id))
          tabFolder.setSelection(tabs.get(id));
        else System.err.println("cannot set focus to editor: " + id);
      }
    });
  }

  private void setName(Message message) {
    String id = message.args[0].strValue();
    String name = message.args[1].strValue();
    setName(id, name);
  }

  private void setName(final String id, final String name) {
    runOnDisplay(new Runnable() {
      public void run() {
        for (String tid : tabs.keySet()) {
          if (tid.equals(id)) tabs.get(id).setText(name);
        }
      }
    });
  }

  private void setText(Message message) {
    final Value id = message.args[0];
    final Value text = message.args[1];
    if (editors.containsKey(id.strValue())) {
      Display.getDefault().syncExec(new Runnable() {
        public void run() {
          ITextEditor editor = editors.get(id.strValue());
          editor.setString(text.strValue());
          tabFolder.setFocus();
          tabFolder.setSelection(tabs.get(id.strValue()));
        }
      });
    } else System.err.println("cannot find text editor " + id);
  }

    private void setUrl(Message message) {
    final Value id = message.args[0];
    final Value url = message.args[1];
    setUrl(id.strValue(), url.strValue(), true);
  }

  private Stack<String> getBackQueue(Browser browser) {
    if (!backQueues.containsKey(browser)) backQueues.put(browser, new Stack<String>());
    return backQueues.get(browser);
  }

  private Stack<String> getForwardQueue(Browser browser) {
    if (!forwardQueues.containsKey(browser)) forwardQueues.put(browser, new Stack<String>());
    return forwardQueues.get(browser);
  }

  private void setUrl(final String id, final String url, final boolean addToHistory) {
    if (browsers.containsKey(id)) {
      final Browser browser = browsers.get(id);
      Display.getDefault().syncExec(new Runnable() {
        private boolean isLikelyToBeHTML(String s) {
          s = s.trim();
          s = s.toLowerCase();
          if (s.startsWith("<html>")) return true;
          if (s.startsWith("<!doctype html")) return true;
          return false;
        }

        public void run() {
          browserLocked = false;
          if (addToHistory) {
            if (browserCurrent.containsKey(browser)) {
              getBackQueue(browser).push(browserCurrent.get(browser));
            }
          }
          browserCurrent.put(browser, url);
          if (isLikelyToBeHTML(url))
            browser.setText(url);
          else browser.setUrl(url);

          tabFolder.setFocus();
          tabFolder.setSelection(tabs.get(id));
        }

      });
    } else System.err.println("cannot find browser " + id);
  }

  private void showLine(Message message) {
    String id = message.args[0].strValue();
    int line = message.args[1].intValue;
    showLine(id, line);
  }

  private void showLine(final String id, final int line) {
    runOnDisplay(new Runnable() {
      public void run() {
        if (editors.containsKey(id))
          editors.get(id).showLine(line);
        else System.err.println("cannot find editor: " + id);
      }
    });
  }

  public void showList(CTabFolderEvent event) {
  }

  public void writeXML(PrintStream out) {
    out.print("<Editors>");
    for (String id : editors.keySet()) {
      CTabItem item = tabs.get(id);
      ITextEditor editor = editors.get(id);
      editor.writeXML(out, tabFolder.getSelection() == item, item.getText(), item.getToolTipText());
    }
    for (String id : browsers.keySet()) {
      CTabItem tab = tabs.get(id);
      String label = tab.getText();
      String tooltip = tab.getToolTipText();
      Browser browser = browsers.get(id);
      String url = browser.getUrl();
      if (url.startsWith("file:") && url.endsWith("/web/index.html")) {
        url = "welcome";
      }
      String text = browser.getText();
      out.print("<Browser id='" + id + "' label='" + label + "' tooltip='" + tooltip + "' url='" + url + "' text='" + XModeler.encodeXmlAttribute(text) + "'/>");
    }
    out.print("</Editors>");
  }

}