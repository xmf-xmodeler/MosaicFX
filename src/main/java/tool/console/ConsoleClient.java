package tool.console;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tool.xmodeler.XModeler;
import xos.Message;

public class ConsoleClient extends Thread {

  ConsoleView          view        = null;
  BufferedReader       in;
  PrintStream          out;
  StringBuffer         queuedInput = new StringBuffer();
  static ConsoleClient theConsole;

  public ConsoleClient(InputStream in, OutputStream out) {
    this.in = new BufferedReader(new InputStreamReader(in));
    this.out = new PrintStream(new BufferedOutputStream(out));
    theConsole = this;
  }

  public void setView(ConsoleView view) {
    this.view = view;
  }

  public static ConsoleClient theConsole() {
    return theConsole;
  }

  @Override
  public void run() {
    char[] buffer = new char[1000];
    while (true) {
      try {
        int size = in.read(buffer);
        if (size > 0) sendInput(new String(buffer).substring(0, size));
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }

  public void debug(String message) {
    System.err.println(java.lang.Thread.currentThread() + ": " + message);
    System.err.flush();
  }

  public boolean tryConnecting() {
    while ((view = Console.getConsoleView()) == null)
      try {
        // We might not have got everything set up just yet...
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    view.setOutput(out);
    view.processInput(queuedInput.toString());
    return true;
  }

  public void sendInput(final String input) {
//	  Platform.runLater( new Runnable() {
//  	    @Override
//  	    public void run() {
  	        if (view != null && view.getOutput() != null)
  	          view.processInput(input);
  	        else if (tryConnecting())
  	          view.processInput(input);
  	        else queueInput(input);
//  	    }
//  	});
  }

  public void queueInput(String input) {
    queuedInput.append(input);
  }

  public void writeXML(PrintStream out) {
    out.print("<Console>");
    Console.writeHistory(out);
    out.print("</Console>");
  }

  public void inflateXML(Document doc) {
    NodeList consoleClients = doc.getElementsByTagName("Console");
    if (consoleClients.getLength() == 1) {
      Node console = consoleClients.item(0);
      NodeList list = console.getChildNodes();
      for (int i = 0; i < list.getLength(); i++) {
        Node item = list.item(i);
        inflateConsoleElement(item);
      }
    } else System.err.println("expecting exactly 1 console client got: " + consoleClients.getLength());
  }

  private void inflateConsoleElement(Node item) {
    if (item.getNodeName().equals("Command")) Console.addCommand(XModeler.attributeValue(item, "text"));
  }

  public void dot(Message message) {
    view.dot(message);
  }

  public void namespace(Message message) {
    view.namespace(message);
  }
}