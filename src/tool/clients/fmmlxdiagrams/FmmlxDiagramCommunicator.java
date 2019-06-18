package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import tool.clients.workbench.WorkbenchClient;
import xos.Value;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

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

	public static Vector<FmmlxDiagram> getDiagrams() { // TODO Ask
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
		if (o instanceof java.util.Vector) {
			java.util.Vector<Object> v = (java.util.Vector<Object>) o;
			int requestID = (Integer) (v.get(0));
			System.err.println("Receiving request " + requestID);
			v.remove(0);
			results.put(requestID, v);
		}
//		System.err.println("o: " + o + "(" + o.getClass() + ")");
	}

	private Vector<Object> xmfRequest(int targetHandle, String message, Value... args) {
		Value[] args2 = new Value[args.length + 1];
		int requestID = idCounter++;
		System.err.println("Sending request " + message + "(" + requestID + ")");
		for (int i = 0; i < args.length; i++) {
			args2[i + 1] = args[i];
		}
		args2[0] = new Value(requestID);
		boolean waiting = true;
//		System.err.println("send:" + targetHandle +"-"+ message +"-"+ args2);
		WorkbenchClient.theClient().send(targetHandle, message, args2);
		int attempts = 0;
		int sleep = 10;
		while (waiting && attempts < 20) {
			System.err.println(attempts + ". attempt");
			attempts++;
			try {
				Thread.sleep(sleep);
				sleep += 50;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (results.containsKey(requestID)) {
				waiting = false;
			}
		}

		if (waiting)
			throw new RuntimeException("Did not receive answer in time!");
		return results.remove(requestID);
		// throw new RuntimeException("Not yet finished implementing");
	}

	@SuppressWarnings("unchecked")
	public Vector<FmmlxObject> getAllObjects() {
		Vector<Object> response = xmfRequest(handler, "getAllObjects", new Value[]{});
		Vector<Object> responseContent = (Vector<Object>) (response.get(0));
		Vector<FmmlxObject> result = new Vector<>();
//		System.err.println(responseContent);
		for (Object responseObject : responseContent) {
			Vector<Object> responseObjectList = (Vector<Object>) (responseObject);


			System.out.println("Class/Object " + responseObjectList.get(1) + " found" + ": " + "Level : "
					+ (Integer) responseObjectList.get(2) + " of " + (Integer) responseObjectList.get(3) + " isAbstract: " + (Boolean) responseObjectList.get(5));
			Vector<Object> parentListO = (Vector<Object>) responseObjectList.get(4);
			Vector<Integer> parentListI = new Vector<Integer>();
			for(Object o : parentListO) {parentListI.add((Integer) o);}
			
			FmmlxObject object = new FmmlxObject(
					(Integer) responseObjectList.get(0), // id
					(String)  responseObjectList.get(1), // name
					(Integer) responseObjectList.get(2), // level
					(Integer) responseObjectList.get(3), // of
					parentListI, // parents
					(Boolean) responseObjectList.get(5),
					(Integer) responseObjectList.get(6), // x-Position
					(Integer) responseObjectList.get(7),
					diagram);// y-Position
			result.add(object);

			sendCurrentPosition(object); // make sure to store position if newly created
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Vector<Vector<FmmlxAttribute>> fetchAttributes(String className) {
		Vector<Object> response = xmfRequest(handler, "getAllAttributes", new Value[]{new Value(className)});
		Vector<Object> twoLists = (Vector<Object>) (response.get(0));
		Vector<FmmlxAttribute> resultOwn = new Vector<>();
		Vector<FmmlxAttribute> resultOther = new Vector<>();

		Vector<Object> ownAttList = (Vector<Object>) twoLists.get(0);
		Vector<Object> otherAttList = (Vector<Object>) twoLists.get(1);
		for (Object o : ownAttList) {
			Vector<Object> attInfo = (Vector<Object>) o;
//			System.err.println("Attribute " + o + " found");
			FmmlxAttribute object = new FmmlxAttribute((String) attInfo.get(0), (Integer) attInfo.get(2),
					(String) attInfo.get(1), (Integer) attInfo.get(4), (String) attInfo.get(3));
			resultOwn.add(object);
		}
		for (Object o : otherAttList) {
			Vector<Object> attInfo = (Vector<Object>) o;
//			System.err.println("Attribute " + o + " found");
			FmmlxAttribute object = new FmmlxAttribute((String) attInfo.get(0), (Integer) attInfo.get(2),
					(String) attInfo.get(1), (Integer) attInfo.get(4), (String) attInfo.get(3));
			resultOther.add(object);
		}
//		result.add(new FmmlxAttribute("att0", 1, "Integer"));
		Vector<Vector<FmmlxAttribute>> result = new Vector<>();
		result.addElement(resultOwn);
		result.addElement(resultOther);
		return result;
	}

	@SuppressWarnings("unchecked")
	public Vector<FmmlxSlot> fetchSlots(String objectName, Vector<String> slotNames) {
		Value[] slotNameArray = createValueArrayString(slotNames);
		Vector<Object> response = xmfRequest(handler, "getSlots", new Value[]{new Value(objectName), new Value(slotNameArray)});
		Vector<Object> slotList = (Vector<Object>) (response.get(0));
		Vector<FmmlxSlot> result = new Vector<>();
		for(Object slotO : slotList) { 
			Vector<Object> slot = (Vector<Object>) (slotO);
			String name =  (String)(slot.get(0));
			String value = (String)(slot.get(1));
		    result.add(new FmmlxSlot(name, value));
		}
		 // Added for test purposes
//		System.err.println("slots: " + response0);

		return result;
	}

	@SuppressWarnings("unchecked")
	public Vector<FmmlxOperation> fetchOperations(String className) {
		Vector<Object> response = xmfRequest(handler, "getOwnOperations", new Value[]{new Value(className)});
		Vector<Object> response0 = (Vector<Object>) (response.get(0));
		Vector<FmmlxOperation> result = new Vector<>();
		for (Object o : response0) {
			Vector<Object> opInfo = (Vector<Object>) o;
			FmmlxOperation op =
					new FmmlxOperation(
							(String) opInfo.get(0), // name
							(Integer) opInfo.get(1), // level
							(String) opInfo.get(2), // type
							(Integer) opInfo.get(3), // owner
							(String) opInfo.get(4) // multiplicity
					);
			result.add(op);
		}
//		result.add(new FmmlxOperation("test", 0, "Blub", -1, null)); // Added for test purposes
//        System.err.println("operations: " + response0);
		return result;
	}

	public Vector<FmmlxOperationValue> fetchOperationValues(String objectName) {
		Vector<FmmlxOperationValue> result = new Vector<>();
		result.add(new FmmlxOperationValue()); // Added for test purposes
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
		Vector<Object> response = xmfRequest(handler, "sendNewPosition",
				new Value[]{new Value(o.id), new Value(o.getX()), new Value(o.getY())});
	}

	public void addMetaClass(String name, int level, Vector<Integer> parents, boolean isAbstract, int x, int y) {
		Value[] parentsArray = createValueArray(parents);

		Value[] message = new Value[]{new Value(-1), new Value(name), new Value(level), new Value(parentsArray),
				new Value(isAbstract), new Value(x), new Value(y)};
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

		Value[] message = new Value[]{new Value(-1), new Value(testClassId), new Value(name), new Value(parentsArray),
				new Value(isAbstract), new Value(x), new Value(y)};

		WorkbenchClient.theClient().send(handler, "addInstance", message);
	}

	private Value[] createValueArray(Vector<Integer> vector) { // todo: make more generic
		Value[] result = new Value[vector.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = new Value(vector.get(i));
		}
		return result;
	}

	private Value[] createValueArrayString(Vector<String> vector) {
		Value[] result = new Value[vector.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = new Value(vector.get(i));
		}
		return result;
	}

	public void addNewInstance(int of, String name, int level, Vector<String> parents, boolean isAbstract, int x,
							   int y) {
		Value[] parentsArray = createValueArrayString(parents);

		Value[] message = new Value[]{new Value(-1), new Value(of), new Value(name),
				// new Value(level),
				new Value(parentsArray), new Value(isAbstract), new Value(x), new Value(y)};
		WorkbenchClient.theClient().send(handler, "addInstance", message);
	}


	public void addAttribute(int classID, String name, int level, String type, Multiplicity multi) {
		Value[] multiplicity = new Value[]{new Value(multi.min), new Value(multi.max), new Value(multi.upperLimit), new Value(multi.ordered), new Value(multi.duplicates)};
		Value[] message = new Value[]{new Value(classID), new Value(name), new Value(level), new Value(type), new Value(multiplicity)};
		WorkbenchClient.theClient().send(handler, "addAttribute", message);

	}


	public void changeClassName(int id, String newName) {
		Value[] message = new Value[]{new Value(id), new Value(newName)};
		//TODO: Implement in XMF
		//WorkbenchClient.theClient().send(handler, "changeClassName", message);
	}

	public void changeOperationName(int id, String oldName, String newName) {
		Value[] message = new Value[]{new Value(id), new Value(oldName), new Value((newName))};
		//TODO: Implement in XMF
		//WorkbenchClient.theClient().send(handler, "changeOperationName", message);
	}

	public void changeAttributeName(int id, String oldName, String newName) {
		Value[] message = new Value[]{new Value(id), new Value(oldName), new Value((newName))};
		//TODO: Implement in XMF
		//WorkbenchClient.theClient().send(handler, "changeAttributeName", message);

	}
}
