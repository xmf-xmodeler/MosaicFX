package tool.xmodeler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import tool.clients.browser.ModelBrowserClient;
import tool.clients.diagrams.DiagramClient;
import tool.clients.dialogs.DialogsClient;
import tool.clients.editors.EditorClient;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.forms.FormsClient;
import tool.clients.menus.MenuClient;
import tool.clients.oleBridge.OleBridgeClient;
import tool.clients.undo.UndoClient;
import tool.clients.workbench.WorkbenchClient;
import tool.console.Console;
import tool.console.ConsoleClient;
import tool.helper.IconGenerator;
import tool.helper.user_properties.PropertyManager;
import tool.helper.user_properties.UserProperty;
import xos.OperatingSystem;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.burningwave.core.assembler.StaticComponentContainer.Modules;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//import com.ceteva.oleBridge.OleBridgeClient;
//import com.ceteva.undo.UndoClient;

public class XModeler extends Application {

  // XModeler is a tool that controls and is controlled by the XMF VM that runs
  // on the XMF operating system.
	
//  private static Integer DEVICE_ZOOM_PERCENT = null;

  static XModeler singleton = null;
  
  static final String    NAME                = "XModeler";
  static String          busyMessage         = "";
  static OperatingSystem xos                 = new OperatingSystem();
  static String          projDir             = null;
  static String          loadedImagePath     = null;
  static String          version             = "";
  static String          buildDate           = null;
  static String[]        copyOfArgs          = null;
  static boolean         showLoad            = false;
  public static String   textEditorClass     = "tool.clients.editors.TextEditor";
  public static PropertyManager propertyManager 	 = new PropertyManager();

  //JavaFX
  static Stage 			 stage 		 		 = null;
  static Scene			 scene 				 = null;
  static VBox			 containingBox		 = null;           
  static SplitPane       outerSplitPane      = null;
  static SplitPane       rightSplitPane      = null;
  static TabPane 		 browserTab 		 = null;
  static TabPane 		 editorTabs 		 = null;
  public static TabPane  propertyTabs 		 = null;
  static MenuBar		 menuBar			 = null;
  static Pane			 notificationPane 	 = null;
  
  static boolean appIsRunning = false;
  
  public static ControlCenter  controlCenterStage            = null;

  public static String getVersion() {
	return version;
  }

  public static String getBuildDate() {
	return buildDate;
  }

	public static ControlCenter getNewStage() {
        return controlCenterStage;
    }

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

    StringBuilder encoded = new StringBuilder();
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

  @Deprecated
  public static int getDeviceZoomPercent() {
    return 100;
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
          return args[i + 1].equalsIgnoreCase("true");
      }
    }
    return true;
  }

  public static MenuBar getMenuBar() {
    return menuBar;
  }

  private static String setVersion(String[] args) {
      for (String arg : args) {
          if (arg.startsWith("version:")) {
              return arg.replace("version:", "");
          }
      }
    return "";
  }
  
  private static String setBuildDate(String[] args) {
      for (String arg : args) {
          if (arg.startsWith("buildDate:")) {
              return arg.replace("buildDate:", "");
          }
      }
    return "";
  }
  
  public static Stage getStage() {
	  return stage;
	  }
  
  public static PropertyManager getPropertyManager() {
	  return propertyManager;
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
          FmmlxDiagramCommunicator.initCommunicator();        
          ModelBrowserClient.theClient().inflateXML(doc);
          DiagramClient.theClient().inflateXML(doc);
          MenuClient.theClient().inflateXML(doc);
          EditorClient.theClient().inflateXML(doc);
          ConsoleClient.theConsole().inflateXML(doc);
          FormsClient.theClient().inflateXML(doc);
        }
      }
    } catch (Throwable e) {
      e.printStackTrace(System.err);
    }
  }

  private static String inflationPath() {
    if (loadedImagePath != null) {
      String name = loadedImagePath.substring(0, loadedImagePath.lastIndexOf('.'));
      return name + ".xml";
    }
    return null;
  }

//  private static String lookupArg(String string, String[] args) {
//    for (int i = 0; i < args.length; i++) {
//      if (args[i].equals("-arg") && args[i + 1].startsWith("projects:")) { return args[i + 1].substring(9); }
//    }
//    return null;
//  }

  public static void main(String[] args) {
	  System.setProperty("prism.order", "sw");
	  Locale.setDefault(Locale.ENGLISH);
	// Prevent loggin of external library
	PrintStream out = System.out;
	OutputStream tmp = new OutputStream() {
		@Override
		public void write(int b) throws IOException {
		}
	};
	PrintStream nul = new PrintStream(tmp);
	System.setOut(nul);  
	
	// Allow compatibility of Java 9 or newer with older libraries
	// Open all Modules to each other
	AllModulesToAllModulesExporter.execute();
	System.setOut(out);
	
	  
    copyOfArgs = Arrays.copyOf(args, args.length);
    textEditorClass = args.length > 1 ? args[1] : "tool.clients.editors.TextEditor";
	
    //for testing setups the launch method will lead to thread errors
    String envVariableValue = System.getenv("XMODELER_TEST");
    if (envVariableValue != null && envVariableValue.equals("true")) {
    	return;
	}
    
    launch(args);
  }

  public static void removeBusyInformation() {
    busyMessage = "";
    setToolTitle();
  }

	public static void saveInflator(final String inflationPath) {
		System.err.println("saveInflator(" + inflationPath +")");
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {

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
					out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><XModeler x='" + x + "' y='" + y + "' width='"
							+ width + "' height = '" + height + "'>");
                    System.err.println("start write xml");
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
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
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
    	FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("*.img", "*.img");
    	fileChooser.getExtensionFilters().add(filter);
    	fileChooser.setSelectedExtensionFilter(filter);
        
    	String initalDirectory = PropertyManager.getProperty("fileDialogPath", "");
    	if (!initalDirectory.equals("")) {
    		File dir = new File(initalDirectory);
    		if(dir.exists()) {
    			fileChooser.setInitialDirectory(dir);
    		}
    	}
    	
    	File file = fileChooser.showOpenDialog(stage);
    	
    	if(file != null){
    		selectedImage = file.getAbsolutePath();
//    		PropertyManager.setProperty("fileDialogPath", file.getParent());
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
    projDir = PropertyManager.getProperty(UserProperty.MODELS_DIR.toString());
//	 projDir = lookupArg("projects", args);
//    if (projDir == null) throw new Error("you have not set the project directory in the initialisation arguments:\n" + Arrays.toString(args));
  }

	public static void setToolTitle() {
		String path = loadedImagePath == null ? "NO_IMAGE_SET" : loadedImagePath;
		if (Thread.currentThread().getName().equals("JavaFX Application Thread")) {
			stage.setTitle(NAME + " " + version + " [" + path + "]" + busyMessage);
		} else {
			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
				stage.setTitle(NAME + " " + version + " [" + path + "]" + busyMessage);
				l.countDown();
			});
			try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
    xos.newMessageClient("com.ceteva.forms", new FormsClient()); PropertyManager.setXmfSettings();
    xos.newMessageClient("com.ceteva.undo", new UndoClient());
    xos.newMessageClient("com.ceteva.oleBridge", new OleBridgeClient());
    WorkbenchClient.theClient().startFmmlxClient();
  }
  
  public static void initClients() { // only sets the tab pane
	  ModelBrowserClient.start(browserTab);
	  EditorClient.start(editorTabs);
	  FormsClient.start(propertyTabs);
	  Console.start(PropertyManager.getProperty("consoleVisible", true), PropertyManager.getProperty("consoleColorReverted", false));
	  DiagramClient.start(editorTabs);
	  FmmlxDiagramCommunicator.start(editorTabs);
	  //ClassBrowserClient.start();
  }
    
	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		buildXmodelerStage();
		initClients();

		//needed in the testframework. There the start method is called several times. To avoid concurrency exceptions these methods should not be called twice
		if (!appIsRunning) {
			startXOS(copyOfArgs[0]);
			singleton = this;
			startClients();
			controlCenterStage = new ControlCenter();
			appIsRunning = true;
		}
		int toolX = Integer.valueOf(PropertyManager.getProperty("toolX"));
		controlCenterStage.setX(toolX);
		int toolY = Integer.valueOf(PropertyManager.getProperty("toolY"));
		controlCenterStage.setY(toolY);
		stage = controlCenterStage;
		controlCenterStage.show();
	}
    
    public void buildXmodelerStage() throws Exception {
	  		outerSplitPane = new SplitPane();
			// Tabs for projects
//			browserTab = new TabPane();
			
			rightSplitPane = new SplitPane();
			rightSplitPane.setOrientation(Orientation.VERTICAL);
			rightSplitPane.setDividerPosition(0, 0.68);
			rightSplitPane.setPrefHeight(1000);
			
			editorTabs = new TabPane();// welcomeTab ,new Tab("Diagram", new DiagramPanel()));
			propertyTabs = new TabPane();
			
			rightSplitPane.getItems().addAll(editorTabs, propertyTabs);
			outerSplitPane = rightSplitPane;
//			outerSplitPane.getItems().addAll(browserTab, rightSplitPane);
//			outerSplitPane.setDividerPosition(0, 0.01 );
			
			menuBar = new MenuBar(); //MyMenuBar();
			
			containingBox = new VBox();
			containingBox.getChildren().addAll(menuBar, outerSplitPane);
			
			notificationPane = new Pane();
			
			notificationPane.setMouseTransparent(true);
			// set on top of each other for notifications
			StackPane stackPane = new StackPane(containingBox, notificationPane);
			VBox.setVgrow(outerSplitPane,Priority.ALWAYS);
			scene = new Scene(stackPane);
			scene.getStylesheets().add(getClass().getResource("/stylesheet.css").toExternalForm());
			// Set up Stage
			stage.getIcons().add(IconGenerator.getImage("shell/mosaic32"));
			setToolTitle();
			stage.setScene(scene);
			stage.setOnCloseRequest(  new EventHandler<WindowEvent>() {
				  public void handle(WindowEvent event) {
					  //propertyManager.writeXMLFile();
                     
					  if (PropertyManager.getProperty("IGNORE_SAVE_IMAGE",true)) {
                      System.exit(0);
                      } else {
                          if (loadedImagePath == null) WorkbenchClient.theClient().shutdownEvent();
                          else WorkbenchClient.theClient().shutdownAndSaveEvent(loadedImagePath, inflationPath());
                      }
					  event.consume();
//					  event.doit = false;
				  }
		  });			
  }
  
  public static void loadImage(){
	  Platform.runLater(()->{
          	stage.close();
          	busyMessage = "";
          	xos = new OperatingSystem();
          	loadedImagePath = null;
          	showLoad = true;
          	try {
          		singleton.start(stage);
          	} catch (Exception e) {
          		e.printStackTrace();
          	}
	  });
  }
  
  static void startXOS(String initFile) {
    final String[] args = xos.getInitArgs(initFile);
    // /*QUICKFIX FOR HI_RES*/FormsClient.HIGH_RESOLUTION = checkHiRes(args);
    setProjectDirectory(args);
    version = setVersion(args);
    buildDate = setBuildDate(args);
    
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

  static public Pane getNotificationPane() {
	  return notificationPane;
  }
  
	public static double getVerticalBorderSize() {
		if (scene != null) {
			return scene.getX();
		} else
			return 0;
	}

	/**
	 * 
	 * @param	top		set true for top window border, false for bottom window border
	 * @return			Size of top/bottom window border
	 */

	public static double getHorizontalBorderSize(Boolean top) {
		if (scene != null) 
		{
			if (top)
				return scene.getY();
			else
				return (scene.getWindow().getHeight() - scene.getHeight());
		} 
		else
			return 0;
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

    public static void finishOpenDiagramFromXml() {
	    controlCenterStage.getControlCenterClient().getAllProjects(); // TODO get rid of
    }

    public static void bringControlCenterToFront() {
	    Platform.runLater(() -> controlCenterStage.toFront());
    }

    public static void bringControlCenterToBack() {
        Platform.runLater(() -> controlCenterStage.toBack());
    }
    
    public static boolean isAlphaMode() {return "true".equals(PropertyManager.getProperty("alphaMode"));}
}

@SuppressWarnings("unchecked")
class AllModulesToAllModulesExporter {
    public static void execute() {
    	try {
    		Modules.exportAllToAll();
    		Class<?> bootClassLoaderClass = Class.forName("jdk.internal.loader.ClassLoaders$BootClassLoader");
			Constructor<? extends ClassLoader> constructor = 
    			(Constructor<? extends ClassLoader>)
    				Class.forName("jdk.internal.loader.ClassLoaders$PlatformClassLoader")
    					.getDeclaredConstructor(bootClassLoaderClass);
    		constructor.setAccessible(true);
    		Class<?> classLoadersClass = Class.forName("jdk.internal.loader.ClassLoaders");
    		Method bootClassLoaderRetriever = classLoadersClass.getDeclaredMethod("bootLoader");
    		bootClassLoaderRetriever.setAccessible(true);
    		constructor.newInstance(bootClassLoaderRetriever.invoke(classLoadersClass));
    		// System.out.println(newBuiltinclassLoader + " instantiated");
    	} catch (Exception exc) {
    		System.err.println("WARNING: Exporting modules did not work. ");
    		System.err.println("It is not necessary for older Java versions.");
    		System.err.println("Check File-->Open XML File.");
    		System.err.println("If the editor shows up, there is no problem with the modules.");
//    		exc.printStackTrace();
    	}
    }
    
}