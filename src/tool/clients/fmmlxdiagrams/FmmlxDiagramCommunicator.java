package tool.clients.fmmlxdiagrams;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import tool.clients.workbench.WorkbenchClient;
import xos.Value;

public class FmmlxDiagramCommunicator {
	private int handler;
	private JButton button;
	public String test() {return "Test works!";}
	int idCounter = 0;
	private HashMap<Integer, Vector<Object>> results = new HashMap<>();	
	private static Hashtable<Integer, Tab> tabs = new Hashtable<Integer, Tab>();
	private static Vector<FmmlxDiagram> diagrams = new Vector<FmmlxDiagram>();
	private static Vector<FmmlxDiagramCommunicator> communicators = new Vector<FmmlxDiagramCommunicator>();
	static TabPane tabPane;
	FmmlxDiagram diagram;
	
	public FmmlxDiagramCommunicator() {
		communicators.add(this);
	}
	
	public static void start(TabPane tabPane) {
		FmmlxDiagramCommunicator.tabPane = tabPane;
	}	
	
	public void setHandle(final int handler) {
		this.handler = handler;
		System.err.println("handler="+handler);
//		JFrame f = new JFrame("TestFrame");
//		f.setSize(300, 300);
//		f.setLocation(800, 100);
//		JPanel p = new JPanel();
//		button = new JButton("Test");
//		p.add(button);
//		f.setContentPane(p);
//		f.setVisible(true);
//		
//		button.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				buttonvalue++;
////			    WorkbenchClient.theClient().send(handler, "fibo", new Value(buttonvalue));
//				Vector<Object> o = xmfRequest(handler, "fibo", new Value(buttonvalue));
//				Vector<FmmlxObject> o2 = getAllObjects();
//			    Integer i = (Integer) (o.firstElement());
//			    button.setText(i+" "+o2.size());
//			}
//		});	
	}
	
//	int buttonvalue = 0;
	
	@SuppressWarnings("unchecked")
	public void sendPackageToJava(Object o) {
		if(o instanceof java.util.Vector){
			java.util.Vector<Object> v = (java.util.Vector<Object>) o;
			int requestID = (Integer) (v.get(0));
			v.remove(0);
			results.put(requestID, v);
		}
		System.err.println("o: " + o + "(" + o.getClass() + ")");
	}
	
	@SuppressWarnings("unchecked")
	public void sendMessageToJava(Object o) {
		if(o instanceof java.util.Vector){
			java.util.Vector<Object> v = (java.util.Vector<Object>) o;
			int requestID = (Integer) (v.get(0));
			v.remove(0);
			results.put(requestID, v);
		}
		System.err.println("o: " + o + "(" + o.getClass() + ")");
	}	
//	public void sendPackageToJava(Object o) {
//		if(o instanceof java.util.Vector){
//			@SuppressWarnings("rawtypes")
//			java.util.Vector v = (java.util.Vector) o;
//			if(v.get(0).equals("fibo")) {
//				button.setText(v.get(1)+"");
//			}
//		}
//		System.err.println("o: " + o + "(" + o.getClass() + ")");
//	}
	
	private Vector<Object> xmfRequest(int targetHandle, String message, Value... args) {
		Value[] args2 = new Value[args.length+1];
		int requestID = idCounter++;
		for(int i = 0; i < args.length; i++) {
			args2[i+1] = args[i];
		}
		args2[0] = new Value(requestID);
		boolean waiting = true;
		System.err.println("send:" + targetHandle +"-"+ message +"-"+ args2);
		WorkbenchClient.theClient().send(targetHandle, message, args2);
		int attempts = 0;
		while(waiting && attempts < 20) {
			System.err.println(attempts + ". attempt");
			attempts++;
			try {Thread.sleep(20);
			} catch (InterruptedException e) { e.printStackTrace(); }
			if(results.containsKey(requestID)) {
				waiting = false;
			}
		}
		
		if(waiting) throw new RuntimeException("Did not receive answer in time!");
		return results.remove(requestID);
		//throw new RuntimeException("Not yet finished implementing");
	}
	
	
	@SuppressWarnings("unchecked")
	public Vector<FmmlxObject> getAllObjects() {
		Vector<Object> response = xmfRequest(handler, "getAllObjects", new Value[]{});
		Vector<Object> response0 = (Vector<Object>) (response.get(0));
		Vector<FmmlxObject> result = new Vector<>();
		System.err.println(response0);
		for(Object o : response0) {
			System.err.println("Class/Object " + o + " found");
			FmmlxObject object = new FmmlxObject((String) o);
			result.add(object);
		}
		return result;
	}
	
	public void newDiagram() {
		CountDownLatch l = new CountDownLatch(1);
		final String label = "getPackageName();";
		Platform.runLater(() -> {
			System.err.println("Create FMMLx-Diagram...");

			diagram = new FmmlxDiagram(this, label);
			Tab tab = new Tab(label);
			tab.setContent(diagram.getView());
			tab.setClosable(true);
			tabs.put(this.handler, tab);
			diagrams.add(diagram);
			tabPane.getTabs().add(tab);
			tabPane.getSelectionModel().selectLast();
			tab.setOnCloseRequest(new javafx.event.EventHandler<javafx.event.Event>() {
				@Override
				public void handle(javafx.event.Event arg0) {
					close(FmmlxDiagramCommunicator.this.handler);
				}

			});
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void close(int handler) {
		diagrams.remove(diagram);
		tabs.remove(this.handler);
//		throw new RuntimeException("Not yet implemented");		
	}
	


}
 