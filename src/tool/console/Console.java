package tool.console;

import java.io.PrintStream;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import tool.xmodeler.XModeler;

public class Console {

  static ConsoleView consoleView;
  static String      CONSOLE_LABEL = "XMF Console";

  public static void start(TabPane tabPane) {
    Tab tabItem = new Tab(CONSOLE_LABEL);
    ConsoleView consoleView = new ConsoleView(XModeler.getStage());
    setConsoleView(consoleView);
    tabItem.setContent(consoleView.getView());
    tabPane.getTabs().add(tabItem);
    tabPane.toFront();
  }
  
  public static void start(Stage stage) {
	  stage = new Stage();
      BorderPane border = new BorderPane();
      ConsoleView consoleView = new ConsoleView(stage);
      setConsoleView(consoleView);
      //HBox hBox = new HBox(consoleView.getView());
	  //HBox.setHgrow(consoleView.getView(), Priority.ALWAYS);
//		GridPane grid = new GridPane();
	  border.setCenter(consoleView.getView());
	  Scene scene = new Scene(border, 1000, 605);
	  stage.setScene(scene);
	  stage.setTitle("Console");
	  stage.show();
	  }

  public static ConsoleView getConsoleView() {
    return consoleView;
  }

  public static void setConsoleView(ConsoleView consoleView) {
    Console.consoleView = consoleView;
  }

  public static void writeHistory(PrintStream out) {
    consoleView.writeHistory(out);
  }

  public static void addCommand(String command) {
    consoleView.addCommand(command);
  }
}
