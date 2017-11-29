package tool.clients.screenGeneration;

import java.io.PrintStream;

import org.w3c.dom.Document;

import tool.clients.SerializableClient;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public abstract class CommandableScreenElement implements SerializableClient {

	private String					id;
	
	public CommandableScreenElement(String id) {
		this.id = id;
	}

	@Override
	public void writeXML(PrintStream out) {
		// TODO Auto-generated method stub
	}

	@Override
	public void inflateXML(Document doc) {
		// TODO Auto-generated method stub
	}

	public void sendMessage(Message message){
		if (message.hasName("testIt"))
    		System.out.println(message);
    	else 
    		System.err.println("send message " + this.getClass().getCanonicalName() + " " + message);
	}
	
	public CommandableScreenElement createNewElement(String message, String id, Value[] values){
		System.err.println("create New Element " + this.getClass().getCanonicalName() + " " + message);
		return null;
	}
	
	public void command(String message, Value[] values){
		System.err.println("command Element " + this.getClass().getCanonicalName() + " " + message);
	}
	
	public void deleteElement(String message, CommandableScreenElement cse, Value[] values){
		System.err.println("delete Element " + this.getClass().getCanonicalName() + " " + message);
	}
	
	public void close(){
		System.err.println("close Element " + this.getClass().getCanonicalName());
	}
	
	public Value callMessage(Message message){
	    throw new Error(this + " call message " + message);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	  public void runOnDisplay(final Runnable r) {
		    XModeler.getXModeler().getDisplay().syncExec(new Runnable() {
		      public void run() {
		        try {
		          r.run();
		        } catch (Throwable t) {
		          t.printStackTrace();
		        }
		      }
		    });
		  }
	  
}
