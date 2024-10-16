package tool.clients.editors;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tool.clients.Client;
import tool.clients.EventHandler;
import tool.clients.diagrams.DiagramClient;
import tool.xmodeler.PropertyManager;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class EditorClient extends Client {

  public static final Color             LINE_HIGHLIGHT = Color.rgb(192, 192, 192);
  public static final Color             RED            = Color.rgb(255, 0, 0);
  //public static final Color             GREY           = Color.rgb(192, 192, 192);
  public static final Color             WHITE          = Color.rgb(255, 255, 255);
  public static final Color             GREEN          = Color.rgb(0, 170, 0);
  public static final Color             BLACK          = Color.rgb(0, 0, 0);

  static EditorClient                   theClient;
  static TabPane   						tabPane;
 
  
  

  public static Hashtable<String, Tab>    tabs           = new Hashtable<>(); //TODO: rewrite completely and introduce Browser object
  public static Hashtable<String, ITextEditor> editors        = new Hashtable<>(); //unorthodox TODO:
  public static Hashtable<String, WebBrowser> browsers        = new Hashtable<>(); // unorthodox TODO:
  
  public static void start(TabPane tabPane) {
    EditorClient.tabPane = tabPane;
  }
  
//  public static void start(Stage stage) {
////	 TabPane tabPane = new TabPane();
////	 //EditorClient.tabPane = tabPane;
////	 Scene scene = new Scene(tabPane, 800, 500);
////	 stage.setScene(scene);
////	 stage.setTitle("Editor");
////	 stage.show();
//  }

  public static EditorClient theClient() {
    return theClient;
  }

  public EditorClient() {
    super("com.ceteva.text");
    theClient = this;
  }

  private void addLineHighlight(Message message) {
    String id = message.args[0].strValue();
    int line = message.args[1].intValue;
    addLineHighlight(id, line);
  }

  private void addLineHighlight(final String id, final int line) {
	  CountDownLatch l = new CountDownLatch(1);  
	  Platform.runLater(()->{
//	  runOnDisplay(new Runnable() {
//      public void run() {
        if (editors.containsKey(id))
          editors.get(id).addLineHighlight(line);
        else System.err.println("cannot find editor: " + id);
        l.countDown();
//      }
    });
	  try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
  }

  private void addMultilineRule(Message message) {
    String id = message.args[0].strValue();
    String start = message.args[1].strValue();
    String end = message.args[2].strValue();
    String color = message.args[3].strValue();
    switch (color) {
      case "red":
        addMultilineRule(id, start, end, 255, 0, 0);
        break;
      case "green":
        addMultilineRule(id, start, end, 0, 153, 0);
        break;
      case "blue":
        addMultilineRule(id, start, end, 50, 50, 255);
        break;
      case "gray":
        addMultilineRule(id, start, end, 120, 120, 120);
        break;
      default:
        String[] colours = color.split(",");
        if (colours.length == 3) {
          int red = Integer.parseInt(colours[0]);
          int blue = Integer.parseInt(colours[1]);
          int green = Integer.parseInt(colours[2]);
          addMultilineRule(id, start, end, red, blue, green);
        } else System.err.println("unknown color: " + Arrays.toString(colours));
        break;
    }
  }

  private void addMultilineRule(String id, String start, String end, int red, int green, int blue) {
    for (ITextEditor editor : editors.values())
      editor.addMultilineRule(id, start, end, red, green, blue);
  }

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
    switch (color) {
      case "red":
        addWordRuleColor(id, text, 255, 0, 0);
        break;
      case "green":
        addWordRuleColor(id, text, 0, 153, 0);
        break;
      case "blue":
        addWordRuleColor(id, text, 50, 50, 255);
        break;
      case "torquoise":
        addWordRuleColor(id, text, 0, 120, 120);
        break;
      default:
        String[] colours = color.split(",");
        if (colours.length == 3) {
          int red = Integer.parseInt(colours[0]);
          int blue = Integer.parseInt(colours[1]);
          int green = Integer.parseInt(colours[2]);
          addWordRuleColor(id, text, red, blue, green);
        } else System.err.println("unknown color: " + Arrays.toString(colours));
        break;
    }
  }

  public Value callMessage(Message message) {
    if (message.hasName("getWelcomePage")) {
      // help page if it is available.
      URL location = EditorClient.class.getProtectionDomain().getCodeSource().getLocation();
      String path = location.toString();
      path = path.substring(0, path.length() - 4); // delete "/bin" from string
      path += "resources/webroot/index.html";
      // System.err.println("getWelcomePage: >" + path + "<");
      return new Value(path);
    }
    if (message.hasName("getText")) { return getText(message); }
    return super.callMessage(message);
  }

//  public void changed(LocationEvent event) {
//  }




  private void clearHighlights(Message message) {
    String id = message.args[0].strValue();
    clearHighlights(id);
  }

  private void clearHighlights(final String id) {
	  CountDownLatch l = new CountDownLatch(1);  
	  Platform.runLater(()->{
//	  runOnDisplay(new Runnable() {
//      public void run() {
        if (editors.containsKey(id))
          editors.get(id).clearHighlights();
        else System.err.println("cannot find editor: " + id);
//      }
	  l.countDown();
	  });
	  try {
		  l.await();
	  } catch (InterruptedException e) {
		  e.printStackTrace();
	  }
  }

  public void close(Tab item, Event wevent) { //TODO: consider reimplementing in javafx
    // Careful because the diagrams and files share the same tab folder...
    //CTabItem item = (CTabItem) item.item;
    String id = getId(item);
    if (id != null && (editors.containsKey(id) || browsers.containsKey(id))) {
      
    	Alert alert = new Alert(AlertType.CONFIRMATION);

    	ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    	ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
    	ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    	alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
		// alert.show();
		alert.setTitle("Open Tab in seperate window instead TEASDWASDADSADSAAASD?");
		alert.setHeaderText(null);
		
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.YES) {
			System.err.println("TEST new Tab Window");
		} else if (result.get() == ButtonType.CANCEL) {
			wevent.consume();
		} else {
	  EventHandler handler = getHandler();
      Message message = handler.newMessage("textClosed", 1);
      message.args[0] = new Value(id);
      handler.raiseEvent(message);
      editors.remove(id);
      browsers.remove(id);
      tabs.remove(id);
		}
		} else {
      DiagramClient.theClient().close(id); //TODO: consider reimplementing in javafx
    }
  }


  public static String getId(Tab item) { // TODO: Undo public & static
    for (String id : tabs.keySet())
      if (tabs.get(id) == item) return id;
    return null;
  }

  private Value getText(final Message message) {
    final String id = message.args[0].strValue();
    final Value[] result = new Value[] { null };
    CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        for (String editorId : editors.keySet()) {
          if (id.equals(editorId)) {
            result[0] = new Value(editors.get(editorId).getString());
          }
        }
//      }
        l.countDown();
    });
    try {
    	l.await();
    } catch (InterruptedException e) {
    	e.printStackTrace();
    }
    if (result[0] == null)
      throw new Error("Cannot find editor with id " + id);
    else return result[0];
  }

  private void inflateEditorElement(Node editor) {
    if (editor.getNodeName().equals("TextEditor")) inflateTextEditor(editor);
    if (editor.getNodeName().equals("NewTextEditor")) inflateNewTextEditor(editor);
    if (editor.getNodeName().equals("Browser")) inflateBrowser(editor);
  }

  private void inflateTextEditor(Node TextEditor) {
    final String id = XModeler.attributeValue(TextEditor, "id");
    String text = XModeler.attributeValue(TextEditor, "text");
    String label = XModeler.attributeValue(TextEditor, "label");
    String toolTip = XModeler.attributeValue(TextEditor, "toolTip");
    final boolean selected = Objects.equals(XModeler.attributeValue(TextEditor, "selected"), "true");
    boolean editable = Objects.equals(XModeler.attributeValue(TextEditor, "editable"), "true");
    boolean lineNumbers = Objects.equals(XModeler.attributeValue(TextEditor, "lineNumbers"), "true");
//    int fontHeight = Integer.parseInt(XModeler.attributeValue(NewTextEditor, "fontHeight"));
    newNewTextEditor(id, label, toolTip, editable, lineNumbers, text);
    final ITextEditor editor = editors.get(id);
    editor.inflate(TextEditor);
    CountDownLatch l = new CountDownLatch(1);
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
//        editor.getText().redraw();
      if (selected) tabPane.getSelectionModel().select(tabs.get(id));
//      }
      l.countDown();
    });
    try {
      l.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void inflateNewTextEditor(Node NewTextEditor) {
    final String id = XModeler.attributeValue(NewTextEditor, "id");
    String text = XModeler.attributeValue(NewTextEditor, "text");
    String label = XModeler.attributeValue(NewTextEditor, "label");
    String toolTip = XModeler.attributeValue(NewTextEditor, "toolTip");
    final boolean selected = Objects.equals(XModeler.attributeValue(NewTextEditor, "selected"), "true");
    boolean editable = Objects.equals(XModeler.attributeValue(NewTextEditor, "editable"), "true");
    boolean lineNumbers = Objects.equals(XModeler.attributeValue(NewTextEditor, "lineNumbers"), "true");
//    int fontHeight = Integer.parseInt(XModeler.attributeValue(NewTextEditor, "fontHeight"));
    newNewTextEditor(id, label, toolTip, editable, lineNumbers, text);
    final ITextEditor editor = editors.get(id);
    editor.inflate(NewTextEditor);
    CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
//        editor.getText().redraw();
        if (selected) tabPane.getSelectionModel().select(tabs.get(id));
//      }
      l.countDown();
    });
    try {
    	l.await();
    } catch (InterruptedException e) {
    	e.printStackTrace();
    }
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
  
  private void newNewTextEditor(Message message) {
    String id = message.args[0].strValue();
    String label = message.args[1].strValue();
    String toolTip = message.args[2].strValue();
    boolean editable = message.args[3].boolValue;
    newNewTextEditor(id, label, toolTip, editable, true, "");
  }

  private void newNewTextEditor(final String id, final String label, final String toolTip, final boolean editable, final boolean lineNumbers, final String text) {
	  CountDownLatch l = new CountDownLatch(1);
	  Platform.runLater(()->{
		
        try {
          Class<?> textEditorClass = Class.forName(XModeler.textEditorClass);
          Constructor<?> cnstr = textEditorClass.getConstructor(new Class<?>[] { String.class, String.class, TabPane.class, Boolean.TYPE, Boolean.TYPE, String.class });
          ITextEditor editor = (ITextEditor) cnstr.newInstance(id, label, tabPane, editable, lineNumbers, text);
         // if(PropertyManager.getProperty("editorsSeparately", true)) {
        	  createStage(editor.getText(), label, id); 
          //} else{
        	//  createTab(editor.getText(), label, id);
         // }
        	  
          editors.put(id, editor);
        } catch (Exception e) {
          e.printStackTrace();
        } 
        l.countDown();
    });
	  try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }

//  public boolean processMessage(Message message) {
//    return false;
//  }

//  public void restore(CTabFolderEvent event) {
//  }

 

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
    CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        editor.action(name,args,charStart,charEnd);
//      }
        l.countDown();
      });
      try {
      	l.await();
      } catch (InterruptedException e) {
      	e.printStackTrace();
      }
  }

  private void setSignature(Message message) {
    String id = message.args[0].strValue();
    Value[] entries = message.args[1].values;
    final ITextEditor editor = editors.get(id);
    CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        editor.setSignature(entries);
//      }
        l.countDown();
    });
    try {
    	l.await();
    } catch (InterruptedException e) {
    	e.printStackTrace();
    }
  }

  private void terminates(Message message) {
    String id = message.args[0].strValue();
    String end = message.args[1].strValue();
    String start = message.args[2].strValue();
    final ITextEditor editor = editors.get(id);
    CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        editor.terminates(end,start);
//      }
        l.countDown();
      });
      try {
      	l.await();
      } catch (InterruptedException e) {
      	e.printStackTrace();
      }
  }

  private void ast(Message message) {
    String id = message.args[0].strValue();
    String tooltip = message.args[1].strValue();
    int charStart = message.args[2].intValue;
    int charEnd = message.args[3].intValue;
    final ITextEditor editor = editors.get(id);
    CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        editor.ast(tooltip, charStart, charEnd);
//      }
        l.countDown();
    });
    try {
    	l.await();
    } catch (InterruptedException e) {
    	e.printStackTrace();
    }
  }

  private void setTooltip(Message message) {
    String id = message.args[0].strValue();
    String tooltip = message.args[1].strValue();
    int charStart = message.args[2].intValue;
    int charEnd = message.args[3].intValue;
    final ITextEditor editor = editors.get(id);
    CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        editor.setTooltip(tooltip, charStart, charEnd);
//      }
      l.countDown();
    });
    try {
    	l.await();
    } catch (InterruptedException e) {
    	e.printStackTrace();
    }
  }

  private void varDec(Message message) {
    String id = message.args[0].strValue();
    int charStart = message.args[1].intValue;
    int charEnd = message.args[2].intValue;
    int decStart = message.args[3].intValue;
    int decEnd = message.args[4].intValue;
    final ITextEditor editor = editors.get(id);
    CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        editor.varDec(charStart, charEnd, decStart, decEnd);
//      }
        l.countDown();
    });
    try {
    	l.await();
    } catch (InterruptedException e) {
    	e.printStackTrace();
    }
  }

  private void varType(Message message) {
    // Record the type of a variable.
  }

  private void rendering(Message message) {
    String id = message.args[0].strValue();
    boolean state = message.args[1].boolValue;
    final ITextEditor editor = editors.get(id);
    CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        editor.setRendering(state);
//      }
      l.countDown();
    });
    try {
    	l.await();
    } catch (InterruptedException e) {
    	e.printStackTrace();
    }
  }

  private void unboundVar(Message message) {
    String id = message.args[0].strValue();
    String name = message.args[1].strValue();
    int charStart = message.args[2].intValue;
    int charEnd = message.args[3].intValue;
    final ITextEditor editor = editors.get(id);
    CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        editor.unboundVar(name, charStart, charEnd);
//      }
        l.countDown();
    });
    try {
    	l.await();
    } catch (InterruptedException e) {
    	e.printStackTrace();
    }
  }

  private void syntaxError(Message message) {
    String id = message.args[0].strValue();
    int pos = message.args[1].intValue;
    String error = message.args[2].strValue();
    final ITextEditor editor = editors.get(id);
    CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        editor.syntaxError(pos, error);
//      }
        l.countDown();
    });
    try {
    	l.await();
    } catch (InterruptedException e) {
    	e.printStackTrace();
    }
  }

  private void clearErrors(Message message) {
    String id = message.args[0].strValue();
    final ITextEditor editor = editors.get(id);
    CountDownLatch l = new CountDownLatch(1);  
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        editor.clearErrors();
//      }
      l.countDown();
    });
    try {
    	l.await();
    } catch (InterruptedException e) {
    	e.printStackTrace();
    }
  }

  private void setClean(Message message) {
    String id = message.args[0].strValue();
    setClean(id);
  }

  public void setClean(String id) {
    final Tab tab = tabs.get(id);
    final ITextEditor editor = editors.get(id);
    CountDownLatch l = new CountDownLatch(1);
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        editor.setDirty(false);
        if(tab != null ) tab.setText(editor.getLabel());
        l.countDown();
//      }
    });
    try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }

  private void setDirty(Message message) {
    String id = message.args[0].strValue();
    setDirty(id);
  }

  public void setDirty(String id) {
    final Tab tab = tabs.get(id);
    final ITextEditor editor = editors.get(id);
    CountDownLatch l = new CountDownLatch(1);
    Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        editor.setDirty(true);
        if(tab != null) tab.setText("*" + editor.getLabel());
        l.countDown();
//      }
    });
    try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }

  private void setFocus(Message message) {
    String id = message.args[0].strValue();
    setFocus(id);
  }

  private void setFocus(final String id) {
	  CountDownLatch l = new CountDownLatch(1);
	  Platform.runLater(()->{
//	  runOnDisplay(new Runnable() {
//      public void run() {
        if (tabs.containsKey(id))
        	tabPane.getSelectionModel().select(tabs.get(id));  	
//          tabFolder.setSelection(tabs.get(id));
        else System.err.println("cannot set focus to editor: " + id);
//      }
      l.countDown();
    });
    try {
    	l.await();
    } catch (InterruptedException e) {
    	e.printStackTrace();
    }
  }

  private void setName(Message message) {
    String id = message.args[0].strValue();
    String name = message.args[1].strValue();
    setName(id, name);
  }

  private void setName(final String id, final String name) {
	CountDownLatch l = new CountDownLatch(1);
  	Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        for (String tid : tabs.keySet()) {
          if (tid.equals(id)) tabs.get(id).setText(name);
        }
        l.countDown();
//      }
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
    if (editors.containsKey(id.strValue())) {
//      Display.getDefault().syncExec(new Runnable() {
//        public void run() {
    	CountDownLatch l = new CountDownLatch(1);
    	Platform.runLater(()->{
          ITextEditor editor = editors.get(id.strValue());
          editor.setString(text.strValue());
          tabPane.getSelectionModel().select(tabs.get(id.strValue()));
          l.countDown();
//        }
      });
    	try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    } else System.err.println("cannot find text editor " + id);
  }

  private void showLine(Message message) {
    String id = message.args[0].strValue();
    int line = message.args[1].intValue;
    showLine(id, line);
  }

  private void showLine(final String id, final int line) {
	CountDownLatch l = new CountDownLatch(1);
	Platform.runLater(()->{
//    runOnDisplay(new Runnable() {
//      public void run() {
        if (editors.containsKey(id))
          editors.get(id).showLine(line);
        else System.err.println("cannot find editor: " + id);
        l.countDown();
//      }
    });
	try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }

//  public void showList(CTabFolderEvent event) {
//  }

  public void writeXML(PrintStream out) {
    out.print("<Editors>");
    for (String id : editors.keySet()) {
      Tab item = tabs.get(id);
      ITextEditor editor = editors.get(id);
      if(item!=null) {
        editor.writeXML(out, item.isSelected(), item.getText(), item.getTooltip().getText());
      }else {
        editor.writeXML(out, false, editor.getLabel(), editor.getLabel());
      }
    }
    for (String id : browsers.keySet()) {
      Tab tab = tabs.get(id);
      if (tab != null) {
        WebBrowser browser = browsers.get(id);

        if (browser != null) {
          String label = tab.getText();
          String tooltip = tab.getTooltip().getText();
          String url = browser.getUrl();
          String text = browser.getDocument().toString();

          if (url.startsWith("file:") && url.endsWith("/web/index.html")) {
            url = "welcome";
          }

          out.print("<Browser id='" + id + "' label='" + label + "' tooltip='" + tooltip + "' url='" + url + "' text='" + XModeler.encodeXmlAttribute(text) + "'/>");
        }
      }
    }
    out.print("</Editors>");
  }

  //WebBrowser

  //xos
  private void setUrl(Message message) {
    final Value id = message.args[0];
    final String url = message.args[1].strValue();
    setUrl(id.strValue(), url, true);
  }

  private void newBrowser(Message message) {
    String id = message.args[0].strValue();
    String label = message.args[1].strValue();
    String tooltip = message.args[2].strValue();
    String url = message.args[3].strValue();
    addBrowser(id, label, tooltip, url, "");
  }

  //management
  private void addBrowser(final String id, final String label, String tooltip, final String url, final String text) {
    WebBrowser browser = new WebBrowser(id, getHandler());
    browsers.put(id, browser);
    
    //LM, 11.11.21, removed as getBrowserVBox causes a dump under unix.
    System.err.println("Error creating new Browser");
    return;
    
	/*
	 * browser.getBrowserVBox(url, text) .thenAccept(browserVbox ->
	 * Platform.runLater(() -> { HBox hbox = new HBox(); VBox vbox = new VBox(); Tab
	 * tab = createBrowserTab(id, label, tooltip);
	 * 
	 * vbox.getChildren().addAll(hbox, browserVbox); tab.setContent(vbox);
	 * tabs.put(id, tab);
	 * 
	 * tabPane.getTabs().add(tab); tabPane.getSelectionModel().select(tab); }))
	 * .exceptionally(throwable -> {
	 * System.err.println("Error creating new Browser: "+throwable); return null;
	 * });
	 */
  }

  private Tab createBrowserTab(String id, String label, String tooltip) {
    Tab tab = new Tab(label);
    tab.setClosable(true);
    tab.setTooltip(new Tooltip(tooltip));

    tab.setOnCloseRequest((e)->{
      // Careful because the diagrams and files share the same tab folder...
      if (id != null && (editors.containsKey(id) || browsers.containsKey(id))) {
        EventHandler handler = getHandler();
        Message message = handler.newMessage("textClosed", 1);
        message.args[0] = new Value(id);
        handler.raiseEvent(message);
        editors.remove(id);
        browsers.remove(id);
        tabs.remove(id);
      }
    });

    return tab;
  }

  private void setUrl(final String id, final String url, final boolean addToHistory) {
    if (browsers.containsKey(id)) {
      Platform.runLater(()-> tabPane.getSelectionModel().select(tabs.get(id)));
      browsers.get(id).setUrl(url);
    } else {
      System.err.println("cannot find browser " + id);
    }

//      Display.getDefault().syncExec(new Runnable() {
//        private boolean isLikelyToBeHTML(String s) {
//          s = s.trim();
//          s = s.toLowerCase();
//          if (s.startsWith("<html>")) return true;
//          if (s.startsWith("<!doctype html")) return true;
//          return false;
//        }
//
//        public void run() {
//          browserLocked = false;
//          if (addToHistory) {
//            if (browserCurrent.containsKey(browser)) {
//              getBackQueue(browser).push(browserCurrent.get(browser));
//            }
//          }
//          browserCurrent.put(browser, url);
//          if (isLikelyToBeHTML(url))
//            browser.setText(url);
//          else browser.setUrl(url);
//
//          tabFolder.setFocus();
//          tabFolder.setSelection(tabs.get(id));
//        }
//      });
  }

  private void inflateBrowser(Node browser) {
    String id = XModeler.attributeValue(browser, "id");
    String label = XModeler.attributeValue(browser, "label");
    String tooltip = XModeler.attributeValue(browser, "toolTip");
    String url = XModeler.attributeValue(browser, "url");

    assert url != null;
    if (url.equals("welcome")) { //TODO: move into dedicated protocol
      URL location = EditorClient.class.getProtectionDomain().getCodeSource().getLocation();
      url = location.toString();
      url = url.substring(0, url.length() - 4); // delete "/bin" from string
      url += "web/index.html";
    }
    String text = XModeler.attributeValue(browser, "text");
    addBrowser(id, label, tooltip, url, text);
  }

//  private String getId(WebView browser) { TODO:
//    for (String id : browsers.keySet())
//      if (browsers.get(id) == browser) return id;
//    return null;
//  }

	@Override
	public boolean processMessage(Message message) {
		return false;
	}

	// _____________________________________

	private void createStage(javafx.scene.Node node, String name, String id) {
		Stage stage = new Stage();
		BorderPane border = new BorderPane();
		border.setCenter(node);
		Scene scene = new Scene(border, 1000, 605);
		stage.setScene(scene);
		stage.setTitle(name);
		stage.show();
		stage.setOnCloseRequest((e) -> closeScene(stage, e, id, name, node));
	}

//	private void createTab(javafx.scene.Node node, String name, String id) {
//		Tab tab = new Tab(name);
//		tab.setTooltip(new Tooltip(name)); 
//		tab.setContent(node);
//		tab.setClosable(true);
//		EditorClient.tabs.put(id, tab);
//		tabPane.getTabs().add(tab);
//		tabPane.getSelectionModel().selectLast();
//		tab.setOnCloseRequest((e)->closeTab(tab,e, id, name, node));
//	}
	
//	private void closeTab(Tab item, Event wevent, String id, String name, javafx.scene.Node node) { 
//		// Careful because the diagrams and files share the same tab folder...
//		
//		if (id != null && (EditorClient.editors.containsKey(id) || EditorClient.browsers.containsKey(id))) {
//
//			Alert alert = new Alert(AlertType.CONFIRMATION);
//
//			ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
//			ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
//			ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
//			alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
//			alert.setTitle("Open tab in separate window instead?");
//			alert.setHeaderText(null);
//
//			Optional<ButtonType> result = alert.showAndWait();
//			if (result.get().getButtonData() == ButtonData.YES) {
//				EditorClient.tabs.remove(id);
//				PropertyManager.setProperty("editorsSeparately", "true");
//				createStage(node, name, id);
//			} else if (result.get().getButtonData() == ButtonData.CANCEL_CLOSE) {
//				wevent.consume();
//			} else {
//				Message message = getHandler().newMessage("textClosed", 1);
//				message.args[0] = new Value(id);
//				getHandler().raiseEvent(message);
//				EditorClient.editors.remove(id);
//				EditorClient.browsers.remove(id);
//				EditorClient.tabs.remove(id);
//			}
//		} else {
//			DiagramClient.theClient().close(id); // TODO: consider reimplementing in javafx
//		}
//	}
	
	private void closeScene(Stage stage, Event wevent, String id, String name, javafx.scene.Node node) { 
		// Careful because the diagrams and files share the same tab folder...
		
		if (id != null && (EditorClient.editors.containsKey(id) || EditorClient.browsers.containsKey(id))) {

//			Alert alert = new Alert(AlertType.CONFIRMATION);
//
//			ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
//			ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
//			ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
//			alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
//			alert.setTitle("Open stage as tab in editor instead?");
//			alert.setHeaderText(null);
//
//			Optional<ButtonType> result = alert.showAndWait();
//			if (result.get().getButtonData() == ButtonData.YES) {
//				PropertyManager.setProperty("editorsSeparately", "false");
//				createTab(node, name, id);
//			} else if (result.get().getButtonData() == ButtonData.CANCEL_CLOSE) {
//				wevent.consume();
//			} else {
				Message message = getHandler().newMessage("textClosed", 1);
				message.args[0] = new Value(id);
				getHandler().raiseEvent(message);
				EditorClient.editors.remove(id);
				EditorClient.browsers.remove(id);
			//}
		} else {
			DiagramClient.theClient().close(id); // TODO: consider reimplementing in javafx
		}
	}

}