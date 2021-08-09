package tool.console;

import java.io.PrintStream;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tool.xmodeler.PropertyManager;
import tool.xmodeler.XModeler;

public class Console {

  static ConsoleView consoleView;
  static String      CONSOLE_LABEL = "XMF Console";
  static boolean separate;
  static boolean visible;
  static boolean colorReverted;
  static Stage stage;
  static Tab tab;
  
  
  
  public static void start(TabPane tabPane, boolean separate, boolean visible, boolean colorReverted) {
	  Console.colorReverted=colorReverted;
	  Console.separate=separate;
	  Console.visible=visible;
	  Console.stage=null;
	  Console.consoleView = new ConsoleView(colorReverted, !separate);
	  if(visible)if(separate) {
		showInStage();
	  } else {
		showInTab(tabPane);
	  }
  }
  
  
  public static void showInTab(TabPane tabPane) {
	
	tab = new Tab(CONSOLE_LABEL);
	tab.setContent(consoleView.getView());
	tabPane.getTabs().add(tab);
	tabPane.toFront();
	separate = false;
	tab.setOnClosed((e)-> PropertyManager.setProperty("consoleVisible", "false"));
  }


  public static void showInStage() {
	  BorderPane border = new BorderPane();
      border.setCenter(consoleView.getView());
	  Scene scene = new Scene(border, 1000, 605);
	  Stage stage = new Stage();
	  separate = true;
	  Console.stage=stage;
	  stage.setScene(scene);
	  stage.setTitle("Console");
	  stage.setOnCloseRequest((e)-> PropertyManager.setProperty("consoleVisible", "false"));
	  stage.show();
  }


private static void start() {
	  Console.consoleView = new ConsoleView(colorReverted, !separate);
  }
  
  
  public static void start(TabPane tabPane) {
    start();
	
    Tab tabItem = new Tab(CONSOLE_LABEL);
    tabItem.setContent(consoleView.getView());
    tabPane.getTabs().add(tabItem);
    tabPane.toFront();
  }
  
  public static void start(Stage stage2) {
	  start();
	  
	  BorderPane border = new BorderPane();
      border.setCenter(consoleView.getView());
	  Scene scene = new Scene(border, 1000, 605);
	  
	  Stage stage = new Stage();
	  stage.setScene(scene);
	  stage.setTitle("Console");
	  stage.show();
  }

  public static ConsoleView getConsoleView() {
    return consoleView;
  }

  //public static void setConsoleView(ConsoleView consoleView) {
  //  Console.consoleView = consoleView;
  //}

  public static void unhide() {
	  PropertyManager.setProperty("consoleVisible", "true");
	  Platform.runLater(()->{
		  if(PropertyManager.getProperty("showConsoleSeparately", false)) {
			  showInStage();
		  } else {
			  showInTab(XModeler.propertyTabs);
		  }
	  });
  }
  
  
  public static void writeHistory(PrintStream out) {
    consoleView.writeHistory(out);
  }

  public static void addCommand(String command) {
    consoleView.addCommand(command);
  }
}
