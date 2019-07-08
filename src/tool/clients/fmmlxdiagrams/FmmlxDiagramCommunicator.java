package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
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
	private static final boolean DEBUG = true;
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

	/**
	 * This operations is called by xmf, usually after a request from java.
	 *
	 * @param o contains the data from the xmf, which is supposed to be a vector, where the first element may identify a specific request which may be waiting for a response
	 *          <p>
	 *          If the response has an id != -1, it is put in a list of responses, which is checked regularly by the operation which sent the request.
	 *          Otherwise it is dropped, as no operation is waiting for a response.
	 */
	@SuppressWarnings("unchecked")
	public void sendMessageToJava(Object o) {
		if (o instanceof java.util.Vector) {
			java.util.Vector<Object> v = (java.util.Vector<Object>) o;
			int requestID = (Integer) (v.get(0));
			if (DEBUG) System.err.println("Receiving request " + requestID);
			v.remove(0);
			if (requestID == -1) {
				System.err.println("v.get(0)= " + v.get(0));
				java.util.Vector<Object> err = (java.util.Vector<Object>) v.get(0);
				if (err != null && err.size() > 0 && err.get(0) != null) {
					CountDownLatch l = new CountDownLatch(1);
					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.ERROR, err.get(0) + "", ButtonType.CLOSE);
						alert.showAndWait();
						l.countDown();
					});
					try {
						l.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				results.put(requestID, v);
			}
		}
//		System.err.println("o: " + o + "(" + o.getClass() + ")");
	}

	/**
	 * This operation wraps a request, adds an identifier and waits for the response
	 *
	 * @param targetHandle an int identifying the handler
	 * @param message      the name of the operation in xmf (FmmlxDiagramClient)
	 * @param args         the arguments of that operation
	 * @return
	 */
	private Vector<Object> xmfRequest(int targetHandle, String message, Value... args) {
		Value[] args2 = new Value[args.length + 1];
		int requestID = idCounter++;
		if (DEBUG) System.err.println("Sending request " + message + "(" + requestID + ")");
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
			if (DEBUG) System.err.println(attempts + ". attempt");
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
	}

	/////////////////////////////////////////
	/// Operations asking for information ///
	/////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public Vector<FmmlxObject> getAllObjects() {
		Vector<Object> response = xmfRequest(handler, "getAllObjects", new Value[]{});
		Vector<Object> responseContent = (Vector<Object>) (response.get(0));
		Vector<FmmlxObject> result = new Vector<>();
		for (Object responseObject : responseContent) {
			Vector<Object> responseObjectList = (Vector<Object>) (responseObject);

//			System.out.println("Class/Object " + responseObjectList.get(1) + " found" + ": " + "Level : "
//					+ (Integer) responseObjectList.get(2) + " of " + (Integer) responseObjectList.get(3) + " isAbstract: " + (Boolean) responseObjectList.get(5));
			Vector<Object> parentListO = (Vector<Object>) responseObjectList.get(4);
			Vector<Integer> parentListI = new Vector<Integer>();
			for (Object o : parentListO) {
				parentListI.add((Integer) o);
			}

			FmmlxObject object = new FmmlxObject(
					(Integer) responseObjectList.get(0), // id
					(String) responseObjectList.get(1), // name
					(Integer) responseObjectList.get(2), // level
					(Integer) responseObjectList.get(3), // of
					parentListI, // parents
					(Boolean) responseObjectList.get(5),
					(Integer) responseObjectList.get(6), // x-Position
					(Integer) responseObjectList.get(7), // y-Position
					diagram);
			result.add(object);

			sendCurrentPosition(object); // make sure to store position if newly created
		}
		return result;
	}


	@SuppressWarnings("unchecked")
	public Vector<Edge> getAllAssociations() {
		Vector<Object> response = xmfRequest(handler, "getAllAssociations", new Value[]{});
		Vector<Object> responseContent = (Vector<Object>) (response.get(0));
		Vector<Edge> result = new Vector<>();

		for (Object edgeInfo : responseContent) {
			Vector<Object> edgeInfoAsList = (Vector<Object>) (edgeInfo);

			Vector<Point2D> listOfPoints = null;
			Vector<Object> pointsListO = (Vector<Object>) edgeInfoAsList.get(4);
			if (pointsListO != null) {
				listOfPoints = new Vector<Point2D>();
				for (Object pointO : pointsListO) {
					Vector<Object> pointV = (Vector<Object>) pointO;
					Point2D pointP = new Point2D((float) pointV.get(1), (float) pointV.get(2)); // leaving 0 free for future use as tag
					listOfPoints.addElement(pointP);
				}
			}

			Edge object = new FmmlxAssociation(
					(Integer) edgeInfoAsList.get(0), // id
					(Integer) edgeInfoAsList.get(1), // startId
					(Integer) edgeInfoAsList.get(2), // endId
					(Integer) edgeInfoAsList.get(3), // parentId
					listOfPoints, // points
					(String) edgeInfoAsList.get(5), // name 1
					(String) edgeInfoAsList.get(6), // name 2
					(String) edgeInfoAsList.get(7), // name 3
					(String) edgeInfoAsList.get(8), // name 4
					(Integer) edgeInfoAsList.get(9), // level s->e
					(Integer) edgeInfoAsList.get(10), // level e->s
					null, //mul s->e
					null, //mul e->e
					diagram);// y-Position
			result.add(object);
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
			FmmlxAttribute object = new FmmlxAttribute((String) attInfo.get(0), (Integer) attInfo.get(2),
					(String) attInfo.get(1), (Integer) attInfo.get(4), (String) attInfo.get(3));
			resultOwn.add(object);
		}
		for (Object o : otherAttList) {
			Vector<Object> attInfo = (Vector<Object>) o;
			FmmlxAttribute object = new FmmlxAttribute((String) attInfo.get(0), (Integer) attInfo.get(2),
					(String) attInfo.get(1), (Integer) attInfo.get(4), (String) attInfo.get(3));
			resultOther.add(object);
		}
		Vector<Vector<FmmlxAttribute>> result = new Vector<>();
		result.addElement(resultOwn);
		result.addElement(resultOther);
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
							(String) opInfo.get(4), // multiplicity
							(Boolean) opInfo.get(5), // isMonitored
							null // args

					);
			result.add(op);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Vector<FmmlxSlot> fetchSlots(String objectName, Vector<String> slotNames) {
		Value[] slotNameArray = createValueArrayString(slotNames);
		Vector<Object> response = xmfRequest(handler, "getSlots", new Value[]{new Value(objectName), new Value(slotNameArray)});
		Vector<Object> slotList = (Vector<Object>) (response.get(0));
		Vector<FmmlxSlot> result = new Vector<>();
		for (Object slotO : slotList) {
			Vector<Object> slot = (Vector<Object>) (slotO);
			String name = (String) (slot.get(0));
			String value = (String) (slot.get(1));
			result.add(new FmmlxSlot(name, value));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Vector<FmmlxOperationValue> fetchOperationValues(String objectName, Vector<String> monitoredOperationsNames) {
		Value[] monitoredOperationsNameArray = createValueArrayString(monitoredOperationsNames);
		Vector<Object> response = xmfRequest(handler, "getOperationValues", new Value[]{new Value(objectName), new Value(monitoredOperationsNameArray)});
		Vector<Object> returnValuesList = (Vector<Object>) (response.get(0));
		Vector<FmmlxOperationValue> result = new Vector<>();
		for (Object returnValueO : returnValuesList) {
			Vector<Object> returnValue = (Vector<Object>) (returnValueO);
			String name = (String) (returnValue.get(0));
			String value = (String) (returnValue.get(1));
			result.add(new FmmlxOperationValue(name, value));
		}
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

	////////////////////////////////////////////////
	/// Operations storing graphical info to xmf ///
	////////////////////////////////////////////////

	@SuppressWarnings("unused")
	public void sendCurrentPosition(FmmlxObject o) {
		Vector<Object> response = xmfRequest(handler, "sendNewPosition",
				new Value[]{new Value(o.id), new Value(o.getX()), new Value(o.getY())});
	}

	public void sendCurrentPositions(FmmlxAssociation a) {
		Vector<Point2D> points = a.getPoints();

		Value[] listOfPoints = new Value[points.size()];
		for (int i = 0; i < listOfPoints.length; i++) {
			Value[] point = new Value[3];
			point[0] = new Value("defaultPoint");
			point[1] = new Value((float) (points.get(i).getX()));
			point[2] = new Value((float) (points.get(i).getY()));
			listOfPoints[i] = new Value(point);
		}

		xmfRequest(handler, "sendNewPositions",
				new Value[]{new Value(a.id), new Value(listOfPoints)});
	}

	////////////////////////////////////////////////////
	/// Operations requesting data to be manipulated ///
	////////////////////////////////////////////////////

	public void addMetaClass(String name, int level, Vector<Integer> parents, boolean isAbstract, int x, int y) {
		Value[] parentsArray = createValueArray(parents);

		Value[] message = new Value[]{
				new Value(-1),
				new Value(name),
				new Value(level),
				new Value(parentsArray),
				new Value(isAbstract),
				new Value(x), new Value(y)};
		WorkbenchClient.theClient().send(handler, "addMetaClass", message);
	}

	public void addNewInstance(int of, String name, int level, Vector<Integer> parents, boolean isAbstract, int x,
							   int y) {
		Value[] parentsArray = createValueArray(parents);

		Value[] message = new Value[]{new Value(-1), new Value(of), new Value(name),
				// new Value(level),
				new Value(parentsArray), new Value(isAbstract), new Value(x), new Value(y)};
		WorkbenchClient.theClient().send(handler, "addInstance", message);
	}

	public void removeClass(int id, int strategy) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(id)};
		WorkbenchClient.theClient().send(handler, "removeClass", message);
	}
	
	public void removeAttribute(int id, String name, int strategy) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(id),
				new Value(name),
				new Value(strategy)};
		WorkbenchClient.theClient().send(handler, "removeAttribute", message);
	}
	
	public void removeOperation(int id, String name, int strategy) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(id),
				new Value(name),
				new Value(strategy)};
		WorkbenchClient.theClient().send(handler, "removeOperation", message);
	}

	public void removeAssociation(int id, String name, int strategy) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(id),
				new Value(name),
				new Value(strategy)};
		WorkbenchClient.theClient().send(handler, "removeAssociation", message);
	}
	
	public void addAttribute(int classID, String name, int level, String type, Multiplicity multi) {
		Value[] multiplicity = new Value[]{
				new Value(multi.min),
				new Value(multi.max),
				new Value(multi.upperLimit),
				new Value(multi.ordered),
				new Value(multi.duplicates)}; // unused in XMF
		Value[] message = new Value[]{new Value(-1),
				new Value(classID),
				new Value(name),
				new Value(level),
				new Value(type),
				new Value(multiplicity)};
		WorkbenchClient.theClient().send(handler, "addAttribute", message);
	}

	public void changeClassName(int id, String newName) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(id),
				new Value(newName)};
		WorkbenchClient.theClient().send(handler, "changeClassName", message);
	}

	public void changeOperationName(int id, String oldName, String newName) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(id),
				new Value(oldName),
				new Value(newName)};
		WorkbenchClient.theClient().send(handler, "changeOperationName", message);
	}

	public void changeAttributeName(int id, String oldName, String newName) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(id),
				new Value(oldName),
				new Value(newName)};
		WorkbenchClient.theClient().send(handler, "changeAttributeName", message);
	}
	
	public void changeAssociationName(int objectId, String oldName, String newName) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(objectId),
				new Value(oldName),
				new Value(newName)};
		WorkbenchClient.theClient().send(handler, "changeAssociationName", message);
	}

	public void changeSlotValue(int id, String slotName, String aParsableText) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(id),
				new Value(slotName),
				new Value(aParsableText)};
		WorkbenchClient.theClient().send(handler, "changeSlotValue", message);
	}

	public void changeClassLevel(int objectId, int oldLevel, int newLevel) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(objectId),
				new Value(oldLevel),
				new Value(newLevel)};
		WorkbenchClient.theClient().send(handler, "changeClassLevel", message);
		
	}

	public void changeAttributeLevel(int objectId, int oldLevel, int newLevel) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(objectId),
				new Value(oldLevel),
				new Value(newLevel)};
		WorkbenchClient.theClient().send(handler, "changeAttributeLevel", message);
		
	}

	public void changeAssociationLevel(int objectId, int oldLevel, int newLevel) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(objectId),
				new Value(oldLevel),
				new Value(newLevel)};
		WorkbenchClient.theClient().send(handler, "changeAssociationLevel", message);
	}

	public void changeOperationLevel(int objectId, int oldLevel, int newLevel) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(objectId),
				new Value(oldLevel),
				new Value(newLevel)};
		WorkbenchClient.theClient().send(handler, "changeOperationLevel", message);
	}

	public void changeOf(int objectId, int oldOfId, int newOfId) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(objectId),
				new Value(oldOfId),
				new Value(newOfId)};
		WorkbenchClient.theClient().send(handler, "changOf", message);	
	}

	public void changeAttributeOwner(int objectId, Integer newOwnerID) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(objectId),
				new Value(newOwnerID)};
		WorkbenchClient.theClient().send(handler, "changOf", message);	
	}

	public void changeOperationOwner(int objectId, Integer newOwnerID) {
		Value[] message = new Value[]{
				new Value(-1),
				new Value(objectId),
				new Value(newOwnerID)};
		WorkbenchClient.theClient().send(handler, "changOwner", message);	
	}

	public void changeParent(int id, Vector<Integer> currentParents, Vector<Integer> newParents) {
		// TODO Auto-generated method stub
		
	}


	
}
