package tool.xmodeler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

//import com.ceteva.oleBridge.OleBridgeClient;
//import com.ceteva.undo.UndoClient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tool.clients.browser.ModelBrowserClient;
import tool.clients.diagrams.DiagramClient;
import tool.clients.dialogs.DialogsClient;
import tool.clients.editors.EditorClient;
import tool.clients.forms.FormsClient;
import tool.clients.menus.MenuClient;
import tool.clients.oleBridge.OleBridgeClient;
import tool.clients.undo.UndoClient;
import tool.clients.workbench.WorkbenchClient;
import tool.console.Console;
import tool.console.ConsoleClient;
import xos.OperatingSystem;

public class XModeler extends Application {

  // XModeler is a tool that controls and is controlled by the XMF VM that runs
  // on the XMF operating system.
	
  private static Integer DEVICE_ZOOM_PERCENT = null;

  static XModeler singelton = null;
  
  static final String    NAME                = "XModeler";
  static String          busyMessage         = "";
  static int             TOOL_X              = 100;
  static int             TOOL_Y              = 100;
  static int             TOOL_WIDTH          = 1200;
  static int             TOOL_HEIGHT         = 900;
  static OperatingSystem xos                 = new OperatingSystem();
  static String          projDir             = null;
  static String          loadedImagePath     = null;
  static String          version             = null;
  static String[]        copyOfArgs          = null;
  static boolean         showLoad            = false;
  public static String   textEditorClass     = "tool.clients.editors.TextEditor";

  //JavaFX
  static Stage 			 stage 		 		 = null;
  static Scene			 scene 				 = null;
  static VBox			 containingBox		 = null;           
  static SplitPane       outerSplitPane      = null;
  static SplitPane       rightSplitPane      = null;
  static TabPane 		 browserTab 		 = null;
  static TabPane 		 editorTabs 		 = null;
  static TabPane 		 propertyTabs 		 = null;
  static MenuBar		 menuBar				 = null;
  
  public static String attributeValue(Node node, String name) {
    NamedNodeMap attrs = node.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      Attr attribute = (Attr) attrs.item(i);
      if (attribute.getName().equals(name)) return attribute.getValue();
    }
    return null;
  }

  public static String attributeValue(Node node, String name, String defaultValue) {
    String value = attributeValue(node, name);
    if (value == null)
      return defaultValue;
    else return value;
  }

  public static String encodeXmlAttribute(String str) {
    if (str == null) return null;

    int len = str.length();
    if (len == 0) return str;

    StringBuffer encoded = new StringBuffer();
    for (int i = 0; i < len; i++) {
      char c = str.charAt(i);
      char cc = (i + 1) < (len - 1) ? str.charAt(i + 1) : 0;
      if (c == '<')
        encoded.append("&lt;");
      else if (c == '\n') {
        encoded.append("&#13;");
        if (cc == '\r') i++;
      } else if (c == '\r') {
        encoded.append("&#10;");
        if (cc == '\n') i++;
      } else if (c == '\"')
        encoded.append("&quot;");
      else if (c == '>')
        encoded.append("&gt;");
      else if (c == '\'')
        encoded.append("&apos;");
      else if (c == '&')
        encoded.append("&amp;");
      else encoded.append(c);
    }

    return encoded.toString();
  }

  public static int getDeviceZoomPercent() {
    if (DEVICE_ZOOM_PERCENT == null) DEVICE_ZOOM_PERCENT = 100;
    return DEVICE_ZOOM_PERCENT;
    // return(display.getDPI().x*100/96);
  }

  private static String getImage(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-image")) return args[i + 1];
    }
    return null;
  }

  private static boolean getImageDialog(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-imagedialog")) {
        if (args[i + 1].equalsIgnoreCase("true")) {
          return true;
        } else {
          return false;
        }
      }
    }
    return true;
  }

  public static MenuBar getMenuBar() {
    return menuBar;
  }

  private static String getVersion(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("version:")) { return args[i].replace("version:", ""); }
    }
    return "";
  }
  
  public static Stage getStage() {
	    return stage;
	  }

  @Deprecated 
  public static Shell getXModeler() {
    return null;
  }

  private static String img2xml(String imgString) {
    File img = new File(imgString);
    File path = img.getParentFile();
    String imgFile = img.getName();
    String xmlFile = imgFile.substring(0, imgFile.lastIndexOf(".")) + ".xml";
    File xml = new File(path, xmlFile);
    return xml.getAbsolutePath();
  }

  public static void inflate(String inflationPath) {
    inflationPath = img2xml(loadedImagePath);
    // System.err.println("loadedImagePath: " + loadedImagePath);
    // System.err.println("inflationPath: " + inflationPath);
    // new RuntimeException("XML").printStackTrace();
    try {
      File fXmlFile = new File(inflationPath);
      if (fXmlFile.exists()) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        String root = doc.getDocumentElement().getNodeName();
        Node node = doc.getDocumentElement();
        if (root.equals("XModeler")) {
          final int x = Integer.parseInt(attributeValue(node, "x"));
          final int y = Integer.parseInt(attributeValue(node, "y"));
          final int width = Integer.parseInt(attributeValue(node, "width"));
          final int height = Integer.parseInt(attributeValue(node, "height"));
          stage.setX(x);
          stage.setY(y);
          stage.setHeight(height);
          stage.setWidth(width);
          ModelBrowserClient.theClient().inflateXML(doc);
          DiagramClient.theClient().inflateXML(doc);
          MenuClient.theClient().inflateXML(doc);
          EditorClient.theClient().inflateXML(doc);
          ConsoleClient.theConsole().inflateXML(doc);
          FormsClient.theClient().inflateXML(doc);
        }
      }
    } catch (IOException e) {
      e.printStackTrace(System.err);
    } catch (ParserConfigurationException e) {
      e.printStackTrace(System.err);
    } catch (SAXException e) {
      e.printStackTrace(System.err);
    } catch (Throwable t) {
      t.printStackTrace(System.err);
    }
  }

  private static String inflationPath() {
    if (loadedImagePath != null) {
      String name = loadedImagePath.substring(0, loadedImagePath.lastIndexOf('.'));
      return name + ".xml";
    }
    return null;
  }

  private static String lookupArg(String string, String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-arg") && args[i + 1].startsWith("projects:")) { return args[i + 1].substring(9); }
    }
    return null;
  }

  public static void main(String[] args) {
    copyOfArgs = Arrays.copyOf(args, args.length);
    textEditorClass = args.length > 1 ? args[1] : "tool.clients.editors.TextEditor";
    launch(args);
  }

  public static void removeBusyInformation() {
    busyMessage = "";
    setToolTitle();
  }

  public static void saveInflator(final String inflationPath) {
        try {
          if (inflationPath != null) {
            loadedImagePath = inflationPath.substring(0, inflationPath.lastIndexOf('.')) + ".img";
            setToolTitle();
            File file = new File(inflationPath);
            // FileOutputStream fout = new FileOutputStream(file);
            int x = (new Double(stage.getX())).intValue();
            int y = (new Double(stage.getY())).intValue();
            int width = (new Double(stage.getWidth())).intValue();
            int height = (new Double(stage.getHeight())).intValue();
            // PrintStream out = new PrintStream(fout);
            PrintStream out = new PrintStream(file, "UTF-8");
            out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><XModeler x='" + x + "' y='" + y + "' width='" + width + "' height = '" + height + "'>");
            ModelBrowserClient.theClient().writeXML(out);
            DiagramClient.theClient().writeXML(out);
            MenuClient.theClient().writeXML(out);
            EditorClient.theClient().writeXML(out);
            ConsoleClient.theConsole().writeXML(out);
            FormsClient.theClient().writeXML(out);
            out.print("</XModeler>");
            out.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
  }

  private static void setImage(String[] args) {
    String defaultImage = getImage(args);
    if (defaultImage == null) throw new Error("you have not supplied an image in the initialisation args:\n" + Arrays.toString(args));
    if (!new File(defaultImage).exists()) throw new Error("the default image file must exist: " + defaultImage);
    boolean imageDialog = getImageDialog(args);
    String selectedImage = null;
    if (imageDialog || showLoad) {
    	
    	final FileChooser fileChooser = new FileChooser();
    	
    	fileChooser.setTitle("Select the image file");
    	fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("*.img", "*.img"));
        
        fileChooser.setInitialFileName(defaultImage);    
    	
    	File file = fileChooser.showOpenDialog(stage);
    	
    	if(file != null){
    		selectedImage = file.getAbsolutePath();
    	}
    	
    }
    if (selectedImage != null && !selectedImage.equals(defaultImage)) {
      loadedImagePath = selectedImage;
      setToolTitle();
      for (int i = 0; i < args.length; i++) {
        if (args[i].equals("-image")) args[i + 1] = loadedImagePath;
      }
    }
  }

  private static void setProjectDirectory(String[] args) {
    projDir = lookupArg("projects", args);
    if (projDir == null) throw new Error("you have not set the project directory in the initialisation arguments:\n" + Arrays.toString(args));
  }

  public static void setToolTitle() {
	    String path = loadedImagePath == null ? "NO_IMAGE_SET" : loadedImagePath;
	    stage.setTitle(NAME + " " + version + " [" + path + "]" + busyMessage);
 }
  
  public static void showBusyInformation(String info) {
    busyMessage = info;
    setToolTitle();
  }

  public static void showMessage(String title, String message) {
	 //TODO implement Notifier Dialog
    //NotifierDialog.notify(title, message, NotificationType.values()[2]);
	  CountDownLatch l = new CountDownLatch(1);
	   Platform.runLater(()->{
		   Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.OK);
		   alert.setTitle(title);
		   alert.showAndWait();
		   l.countDown();
	   });
	   try {
		   l.await();
	   } catch (InterruptedException e) {
		   e.printStackTrace();
	   }
  }

  public static void shutdown() {
	 Platform.runLater(new Runnable() {
	      public void run() {
	  stage.close();
	  System.exit(0);
	      }});
  }


  public static void startClients() {
    xos.newMessageClient("com.ceteva.text", new EditorClient());
    xos.newMessageClient("com.ceteva.mosaic", new WorkbenchClient());
    xos.newMessageClient("com.ceteva.menus", new MenuClient());
    xos.newMessageClient("com.ceteva.modelBrowser", new ModelBrowserClient());
    xos.newMessageClient("com.ceteva.diagram", new DiagramClient());
    xos.newMessageClient("com.ceteva.dialogs", new DialogsClient());
    xos.newMessageClient("com.ceteva.forms", new FormsClient());
    xos.newMessageClient("com.ceteva.undo", new UndoClient());
    xos.newMessageClient("com.ceteva.oleBridge", new OleBridgeClient());
//    xos.newMessageClient("screenGeneration", new ScreenGenerationClient()); // BB
  }
  
  public static void initClients() { // only sets the tab pane
	  ModelBrowserClient.start(browserTab);
	  EditorClient.start(editorTabs);
	  FormsClient.start(propertyTabs);
	  Console.start(propertyTabs); // only one which does more
	  DiagramClient.start(editorTabs);
  }
  
  
  public static void openXModeler() {
		stage.show();		
  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
	  singelton = this;
	  stage = primaryStage;
	  startXOS(copyOfArgs[0]);
	  createXmodeler();
	  initClients();
      startClients();
	  openXModeler();
  }	  
	
  public void createXmodeler() throws Exception {
	  		outerSplitPane = new SplitPane();
			
			// Tabs for proejcts
			browserTab = new TabPane();//new Tab("MyProjects",projectTree),new Tab("Project0",new TreeView<String>()));
			
			rightSplitPane = new SplitPane();
			rightSplitPane.setOrientation(Orientation.VERTICAL);
			rightSplitPane.setDividerPosition(0, 0.66);
			
			editorTabs = new TabPane();// welcomeTab ,new Tab("Diagram", new DiagramPanel()));
			propertyTabs = new TabPane();
			
			rightSplitPane.getItems().addAll(editorTabs, propertyTabs);
			
			outerSplitPane.getItems().addAll(browserTab, rightSplitPane);
			outerSplitPane.setDividerPosition(0, 0.2 );
						
			menuBar = new MenuBar(); //MyMenuBar();
			
			containingBox = new VBox();
			containingBox.getChildren().addAll(menuBar, outerSplitPane);
			VBox.setVgrow(outerSplitPane,Priority.ALWAYS);
			
			scene = new Scene(containingBox,TOOL_WIDTH,TOOL_HEIGHT);
			
			// Set up Stage
			stage.getIcons().add(new Image("file:icons/shell/mosaic32.gif"));
			setToolTitle();
			
			stage.setX(TOOL_X);
			stage.setY(TOOL_Y);			
			stage.setScene(scene);
			stage.setOnCloseRequest(  new EventHandler<WindowEvent>() {
				  public void handle(WindowEvent event) {
					  if (loadedImagePath == null) {
						  WorkbenchClient.theClient().shutdownEvent();
					  } else {
						  WorkbenchClient.theClient().shutdownAndSaveEvent(loadedImagePath, inflationPath());
					  }
					  event.consume();
//					  event.doit = false;
				  }
		  });
			
//TODO Why timer? Can we intergrate it properly?
//			XModeler.getDisplay().timerExec(3000, 
//					new Runnable() {
//			      public void run() {
			
			/*MenuItem itemVMPanic = new MenuItem("VM Panic");
			itemVMPanic.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	Machine.interrupt = true;
	            }
	        }); 

			Menu menuDebug = new Menu("Debug");
			menuDebug.getItems().add(itemVMPanic);
			menuBar.getMenus().add(menuDebug);
			
			 */

//			      }});
			
  }
  
  public static void loadImage(){
	  Platform.runLater(()->{
          	stage.close();
          	busyMessage = "";
          	TOOL_X = 100;
          	TOOL_Y = 100;
          	TOOL_WIDTH = 1200;
          	TOOL_HEIGHT = 900;
          	xos = new OperatingSystem();
          	loadedImagePath = null;
          	showLoad = true;
          	try {
          		singelton.start(stage);
          	} catch (Exception e) {
          		e.printStackTrace();
          	}
	  });
  }
  
  static void startXOS(String initFile) {
    final String[] args = xos.getInitArgs(initFile);
    // /*QUICKFIX FOR HI_RES*/FormsClient.HIGH_RESOLUTION = checkHiRes(args);
    setProjectDirectory(args);
    version = getVersion(args);
    setImage(args);
    Thread t = new Thread() {
      public void run() {
        try {
          xos.init(args);
        } catch (Throwable t) {
          System.out.println(t);
          t.printStackTrace();
        }
      }
    };
    t.start();
  }

//  private static boolean runLaterDebug = true;
//  public static void runLater(Runnable runnable, String debugName) {
//		CountDownLatch l = new CountDownLatch(1);
//		Platform.runLater(() -> {
//			if(runLaterDebug) System.err.println("Runnable " + debugName + " started...");
////			runnable.run();
//	        l.countDown();
//	        if(runLaterDebug) System.err.println("Runnable " + debugName + " done");
//		});
//		try {
//			l.await();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//  }
}

