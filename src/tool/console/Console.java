package tool.console;

import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Display;

public class Console {

  static ConsoleView consoleView;
  static String      CONSOLE_LABEL = "XMF Console";

  public static void start(CTabFolder tabFolder) {
    ConsoleClient.theConsole().setDisplay(Display.getDefault());
    CTabItem tabItem = new CTabItem(tabFolder, SWT.BORDER);
    tabItem.setText(CONSOLE_LABEL);
    ConsoleView consoleView = new ConsoleView(tabFolder, tabItem);
    ConsoleClient.theConsole().setView(consoleView);
    setConsoleView(consoleView);
    tabFolder.setSelection(tabItem);
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
