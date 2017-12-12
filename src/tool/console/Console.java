package tool.console;

import java.io.PrintStream;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class Console {

  static ConsoleView consoleView;
  static String      CONSOLE_LABEL = "XMF Console";

  public static void start(TabPane tabPane) {
    Tab tabItem = new Tab(CONSOLE_LABEL);
    ConsoleView consoleView = new ConsoleView();
    setConsoleView(consoleView);
    tabItem.setContent(consoleView.getView());
    tabPane.getTabs().add(tabItem);
    tabPane.toFront();
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
