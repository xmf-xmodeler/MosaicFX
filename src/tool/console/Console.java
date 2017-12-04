package tool.console;

import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Display;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class Console {

  static ConsoleView consoleView;
  static String      CONSOLE_LABEL = "XMF Console";

  public static void start(TabPane tabPane) {
//    ConsoleClient.theConsole().setDisplay(Display.getDefault());
    Tab tabItem = new Tab(CONSOLE_LABEL);
    ConsoleView consoleView = new ConsoleView();
//    ConsoleView consoleView = new ConsoleView(tabFolder, tabItem);
//    ConsoleClient.theConsole().setView(consoleView);
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
