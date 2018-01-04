package tool.clients;

import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

import org.w3c.dom.Document;

import javafx.application.Platform;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.MessageHandler;
import xos.MessagePacket;
import xos.Value;

public abstract class Client implements MessageHandler, SerializableClient {

  boolean      debug = false;
  String       name;
  EventHandler handler;

  public Client(String name) {
    this.name = name;
  }

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public EventHandler getHandler() {
    return handler;
  }

  public void setHandler(EventHandler handler) {
    this.handler = handler;
  }

  public Value callMessage(Message message) {
    throw new Error(this + " call message " + message);
  }

  public Value processCall(Message message) {
    return null;
  }

  public abstract boolean processMessage(Message message);

  public void registerEventHandler(xos.EventHandler handler) {
    this.handler = new EventHandler(name, handler);
    setHandler(this.handler);
  }

  public void sendMessage(final Message message) {
    System.err.println("send message " + name + " " + message);
  }

  public void sendPacket(final MessagePacket packet) {
    for (int i = 0; i < packet.getMessageCount(); i++)
      sendMessage(packet.getMessage(i));
  }

  @Deprecated
  public void runOnDisplay(final Runnable r) {
	    
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {	  
			System.err.println("runOnDisplay (deprecated) start");
			r.run();
			System.err.println("runOnDisplay (deprecated)       done");
            l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	   
//    XModeler.getXModeler().getDisplay().syncExec(new Runnable() {
//      public void run() {
//        try {
//          r.run();
//        } catch (Throwable t) {
//          t.printStackTrace();
//        }
//      }
//    });
  }

  public void writeXML(PrintStream out) {
    throw new Error("unknown type of client for XML " + this);
  }

  public void inflateXML(Document doc) {
    throw new Error("unknown type of client for XML " + this);
  }
}