package tool.clients.fmmlxdiagrams;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import tool.clients.workbench.WorkbenchClient;
import xos.Value;

public class FmmlxDiagramCommunicator {
	private int handler;
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
	
	public static Vector<FmmlxDiagram> getDiagrams() { //TODO Ask
		return diagrams;
	}
	
	public static void start(TabPane tabPane) {
		FmmlxDiagramCommunicator.tabPane = tabPane;
	}	
	
	public void setHandle(final int handler) {
		this.handler = handler;
	}
	
	@SuppressWarnings("unchecked")
	public void sendMessageToJava(Object o) {
		if(o instanceof java.util.Vector){
			java.util.Vector<Object> v = (java.util.Vector<Object>) o;
			int requestID = (Integer) (v.get(0));
			System.err.println("Receiving request " + requestID);
			v.remove(0);
			results.put(requestID, v);
		}
//		System.err.println("o: " + o + "(" + o.getClass() + ")");
	}	
	
	private Vector<Object> xmfRequest(int targetHandle, String message, Value... args) {
		Value[] args2 = new Value[args.length+1];
		int requestID = idCounter++;
		System.err.println("Sending request " + message + "(" + requestID + ")");
		for(int i = 0; i < args.length; i++) {
			args2[i+1] = args[i];
		}
		args2[0] = new Value(requestID);
		boolean waiting = true;
//		System.err.println("send:" + targetHandle +"-"+ message +"-"+ args2);
		WorkbenchClient.theClient().send(targetHandle, message, args2);
		int attempts = 0;
		int sleep = 10;
		while(waiting && attempts < 20) {
			System.err.println(attempts + ". attempt");
			attempts++;
			try {Thread.sleep(sleep); sleep += 50;
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
		Vector<Object> responseContent = (Vector<Object>) (response.get(0));
		Vector<FmmlxObject> result = new Vector<>();
//		System.err.println(responseContent);
		for(Object responseObject : responseContent) {
			Vector<Object> responseObjectList = (Vector<Object>) (responseObject);
			
//			System.err.println("Class/Object " + o + " found");
			FmmlxObject object = new FmmlxObject(
					(Integer) responseObjectList.get(0),
					(String)  responseObjectList.get(1), 
					(Integer) responseObjectList.get(2), 
					(Integer) responseObjectList.get(3), 
					null, 
					(Integer) responseObjectList.get(5), 
					(Integer) responseObjectList.get(6));
			result.add(object);
			
			sendCurrentPosition(object); // make sure to store position if newly created 
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Vector<FmmlxAttribute> fetchAttributes(String className) {
		Vector<Object> response = xmfRequest(handler, "getOwnAttributes", new Value[]{new Value(className)});
		Vector<Object> response0 = (Vector<Object>) (response.get(0));
		Vector<FmmlxAttribute> result = new Vector<>();
//		System.err.println(response0);
		for(Object o : response0) {
			Vector<Object> attInfo =  (Vector<Object>) o;
//			System.err.println("Attribute " + o + " found");
			FmmlxAttribute object = new FmmlxAttribute(
					(String) attInfo.get(0), 
					(Integer) attInfo.get(2), 
					(String) attInfo.get(1));
			result.add(object);
		}		
		result.add(new FmmlxAttribute("att0", 1, "Integer"));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Vector<FmmlxSlot> fetchSlots(String objectName) {
		Vector<Object> response = xmfRequest(handler, "getSlots", new Value[]{new Value(objectName)});
		Vector<Object> response0 = (Vector<Object>) (response.get(0));
		Vector<FmmlxSlot> result = new Vector<>();
		result.add(new FmmlxSlot()); //Added for test purposes
//		System.err.println("slots: " + response0);
		
		return result;
	}	
	
	public Vector<FmmlxOperation> fetchOperations(String className) {
		Vector<Object> response = xmfRequest(handler, "getOwnOperations", new Value[]{new Value(className)});
		Vector<Object> response0 = (Vector<Object>) (response.get(0));
		Vector<FmmlxOperation> result = new Vector<>();
		result.add(new FmmlxOperation()); //Added for test purposes
//        System.err.println("operations: " + response0);
		return result;
	}
	
	public Vector<FmmlxOperationValue> fetchOperationValues(String objectName) {
		Vector<FmmlxOperationValue> result = new Vector<>();
		result.add(new FmmlxOperationValue()); //Added for test purposes
		return result;
	}	
	
	public Vector<FmmlxObject> fetchParentClasses(String objectName) {
		Vector<FmmlxObject> result = new Vector<>();
		return result;
	}		
	
	public FmmlxObject fetchOf(String objectName) {
		FmmlxObject result = null;
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

	public void sendCurrentPosition(FmmlxObject o) {
		Vector<Object> response = xmfRequest(handler, "sendNewPosition", new Value[]{new Value(o.id), new Value(o.x), new Value(o.y)});
	}

	public void addMetaClass(String name, int level, Vector<Integer> parents, boolean isAbstract, int x, int y) {
		Value[] parentsArray = createValueArray(parents);

		Value[] message = new  Value[]{
				new Value(-1),
				new Value(name),
				new Value(level),
				new Value(parentsArray),
				new Value(isAbstract),
				new Value(x),
				new Value(y)
				};
		WorkbenchClient.theClient().send(handler, "addMetaClass", message);
		
//		Vector<Object> response = xmfRequest(handler, "addNewMetaClass", new Value[]{
//				new Value(name),
//				new Value(level),
//				new Value(parentsArray),
//				new Value(isAbstract),
//				new Value(x),
//				new Value(y)
//				});
//		System.err.println("addNewMetaClassResponse: " + response);
	}
	
	public void addInstance(int testClassId, String name, Vector<Integer> parents, boolean isAbstract, int x, int y) {
		Value[] parentsArray = createValueArray(parents);
		
		Value[] message = new  Value[]{
				new Value(-1),
				new Value(testClassId),
				new Value(name),
				new Value(parentsArray),
				new Value(isAbstract),
				new Value(x),
				new Value(y)
				};
		
		WorkbenchClient.theClient().send(handler, "addInstance", message);
	}

	private Value[] createValueArray(Vector<Integer> vector) { // todo: make more generic
		Value[] result = new Value[vector.size()];
		for(int i = 0; i < result.length; i++) {
			result[i] = new Value(vector.get(i));
		}
		return result;
	}
	
	private Value[] createValueArrayString(Vector<String> vector) {
		Value[] result = new Value[vector.size()];
		for(int i = 0; i<result.length;i++) {
			result[i]= new Value(vector.get(i));
		}
		return result ;	
	}

	public void addNewInstance(int of, String name, int level, Vector<String> parents, boolean isAbstract, int x,
			int y) {
		Value[] parentsArray = createValueArrayString(parents);
		
		Value[] message = new Value[] {
				new Value(-1),
				new Value(name),
				new Value(of),
				new Value(level),
				new Value(parentsArray),
				new Value(isAbstract),
				new Value(x),
				new Value(y),};
		WorkbenchClient.theClient().send(handler, "addInstance", message);
	}
}
 