package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import tool.clients.serializer.Deserializer;
import tool.clients.serializer.Serializer;
import tool.clients.workbench.WorkbenchClient;
import xos.Value;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class FmmlxDiagramCommunicator {
	public static final String TAG = FmmlxDiagram.class.getSimpleName();


	private int handler;
	int idCounter = 0;
	private HashMap<Integer, Vector<Object>> results = new HashMap<>();
	private static Hashtable<Integer, Tab> tabs = new Hashtable<Integer, Tab>();
	private static Vector<FmmlxDiagram> diagrams = new Vector<FmmlxDiagram>();
	private static final boolean DEBUG = false;
	private static Vector<FmmlxDiagramCommunicator> communicators = new Vector<FmmlxDiagramCommunicator>();
	static TabPane tabPane;
	private String name;
	private Value getNoReturnExpectedMessageID(int diagramID) {return new Value(new Value[] {new Value(diagramID), new Value(-1)});}

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
	
	@SuppressWarnings("deprecation")
	public void newDiagram(int diagramID, String diagramName, String packageName) {
		this.name = diagramName;
		CountDownLatch l = new CountDownLatch(1);
		final String label = diagramName + " " + diagramID;//"getPackageName();";
		Platform.runLater(() -> {
			if (DEBUG) System.err.println("Create FMMLx-Diagram ("+diagramName+") ...");

			FmmlxDiagram diagram = new FmmlxDiagram(this, diagramID, label, packageName);
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
//		throw new RuntimeException("Not implemented yet!");
		diagrams.remove(diagrams.get(handler));
		tabs.remove(handler);
	}

	private Value[] createValueArray(Vector<String> vector) { // todo: make more generic
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
	
/*
 * 	private Value[] createValueArrayEnumElement(Vector<EnumElement> vector) {
		Value[] result = new Value[vector.size()];
		for(int i = 0; i < result.length; i++) {
			result[i] = new Value(vector.get(i).getName());
		}
		return result;
	}*/
	
	private Value[] createValueArrayEnum(Vector<String> vector) {
		Value[] result = new Value[vector.size()];
		for(int i = 0; i < result.length; i++) {
			result[i] = new Value(vector.get(i));
		}
		return result;
	}
	
	public void triggerUpdate() {
		for(FmmlxDiagram diagram : diagrams) {
			diagram.updateDiagram();
		}
	}
	
	
	/**
	 * This operations is called by xmf, usually after a request from java.
	 *
	 * @param msgAsObj contains the data from the xmf, which is supposed to be a vector, where the first element may identify a specific request which may be waiting for a response
	 *          <p>
	 *          If the response has an id != -1, it is put in a list of responses, which is checked regularly by the operation which sent the request.
	 *          Otherwise it is dropped, as no operation is waiting for a response.
	 */
	@SuppressWarnings("unchecked")
	public void sendMessageToJava(Object msgAsObj) {
		if (msgAsObj instanceof java.util.Vector) {
			java.util.Vector<Object> msgAsVec = (java.util.Vector<Object>) msgAsObj;
			java.util.Vector<Object> ids = (java.util.Vector<Object>) msgAsVec.get(0);
//			int diagramID = (Integer) (ids.get(0));
//			if(diagramID != this.diagramID) return; // Ignore completely. Message not for this Diagram
			int requestID = (Integer) (ids.get(1));
			if (DEBUG) System.err.println(name + ": Receiving request " + requestID);
			msgAsVec.remove(0);
			if (requestID == -1) {
				if (DEBUG) System.err.println("v.get(0)= " + msgAsVec.get(0));
				java.util.Vector<Object> err = (java.util.Vector<Object>) msgAsVec.get(0);
				if (err != null && err.size() > 0 && err.get(0) != null) {
					CountDownLatch l = new CountDownLatch(1);
					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.ERROR, err.get(0) + "", ButtonType.CLOSE);
						//alert.showAndWait(); NOPE
						alert.show();
						l.countDown();
					});
					try {
						l.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				results.put(requestID, msgAsVec);
			}
		} else {
			if (DEBUG) System.err.println("o: " + msgAsObj + "(" + msgAsObj.getClass() + ")");
		}
	}

	/**
	 * This operation wraps a request, adds an identifier and waits for the response
	 *
	 * @param targetHandle an int identifying the handler
	 * @param message      the name of the operation in xmf (FmmlxDiagramClient)
	 * @param args         the arguments of that operation
	 * @return
	 */
	private Vector<Object> xmfRequest(int targetHandle, FmmlxDiagram diagram, String message, Value... args) throws TimeOutException {
		Value[] args2 = new Value[args.length + 1];
		int requestID = idCounter++;
		if (DEBUG) System.err.println(name + ": Sending request " + message + "(" + requestID + ") handle" + targetHandle);
		for (int i = 0; i < args.length; i++) {
			args2[i + 1] = args[i];
		}
		args2[0] = new Value(new Value[] {new Value(diagram==null?-1:diagram.getID()), new Value(requestID)});
		boolean waiting = true;
		WorkbenchClient.theClient().send(targetHandle, message, args2);
		int attempts = 0;
		int sleep = 5;
		while (waiting && sleep < 200 * 100) {
			if (DEBUG) System.err.println(attempts + ". attempt");
			attempts++;
			try {
				Thread.sleep(sleep);
				sleep *= 2;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (results.containsKey(requestID)) {
				waiting = false;
			}
		}

		if (waiting)
			throw new TimeOutException();
		return results.remove(requestID);
	}

	private void sendMessage(String command, Value[] message) {
		if (DEBUG) System.err.println(name + ": Sending command " + command);
		WorkbenchClient.theClient().send(handler, command, message);
	}

	/////////////////////////////////////////
	/// Operations asking for information ///
	/////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public Vector<FmmlxObject> getAllObjects(FmmlxDiagram diagram) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, diagram, "getAllObjects", new Value[]{});
		Vector<Object> responseContent = (Vector<Object>) (response.get(0));
		Vector<FmmlxObject> result = new Vector<>();
		for (Object responseObject : responseContent) {
			Vector<Object> responseObjectList = (Vector<Object>) (responseObject);

			Vector<Object> parentListO = (Vector<Object>) responseObjectList.get(4);
			Vector<Integer> parentListI = new Vector<Integer>();
			for (Object o : parentListO) {
				parentListI.add((Integer) o);
			}

			FmmlxObject object = new FmmlxObject(
					(Integer) responseObjectList.get(0), // id
					(String)  responseObjectList.get(1), // name
					(Integer) responseObjectList.get(2), // level
					(Integer) responseObjectList.get(3), // of
					parentListI, // parents
					(Boolean) responseObjectList.get(5),
					(Integer) responseObjectList.get(6), // x-Position
					(Integer) responseObjectList.get(7), // y-Position 
//					(Integer) responseObjectList.get(8), // delegatesTo
//					(Integer) responseObjectList.get(9), // roleFiller
					diagram);
			result.add(object);

			sendCurrentPosition(diagram, object); // make sure to store position if newly created
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Vector<Edge> getAllInheritanceEdges(FmmlxDiagram diagram) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, diagram, "getAllInheritanceEdges", new Value[]{});
		Vector<Object> responseContent = (Vector<Object>) (response.get(0));
		Vector<Edge> result = new Vector<>();

		for (Object edgeInfo : responseContent) {
			Vector<Object> edgeInfoAsList = (Vector<Object>) (edgeInfo);

			Vector<Point2D> listOfPoints = null;
			Vector<Object> pointsListO = (Vector<Object>) edgeInfoAsList.get(3);
			PortRegion startRegion = null;
			PortRegion endRegion = null;
			if(pointsListO != null && pointsListO.size()>=2) {
				listOfPoints = new Vector<Point2D>();
				if("startNode".equals(((Vector<Object>)(pointsListO.firstElement())).get(0)))
					startRegion = PortRegion.valueOf((String)(((Vector<Object>)(pointsListO.firstElement())).get(1)));
				if("endNode".equals(((Vector<Object>)(pointsListO.lastElement())).get(0)))
					endRegion = PortRegion.valueOf((String)(((Vector<Object>)(pointsListO.lastElement())).get(1)));
				for (Object pointO : pointsListO) {
					Vector<Object> pointV = (Vector<Object>) pointO;
					if("defaultPoint".equals(pointV.get(0))) {
						Point2D pointP = new Point2D((float) pointV.get(1), (float) pointV.get(2)); 
						listOfPoints.addElement(pointP);
					}
				}
			}

			Edge object = new InheritanceEdge(
					(Integer) edgeInfoAsList.get(0), // id
					(Integer) edgeInfoAsList.get(1), // startId
					(Integer) edgeInfoAsList.get(2), // endId
					listOfPoints, // points
					startRegion, endRegion,
					diagram);
			result.add(object);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Vector<Edge> getAllDelegationEdges(FmmlxDiagram diagram) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, diagram, "getAllDelegationEdges", new Value[]{});
		Vector<Object> responseContent = (Vector<Object>) (response.get(0));
		Vector<Edge> result = new Vector<>();

		for (Object edgeInfo : responseContent) {
			Vector<Object> edgeInfoAsList = (Vector<Object>) (edgeInfo);

			Vector<Point2D> listOfPoints = null;
			Vector<Object> pointsListO = (Vector<Object>) edgeInfoAsList.get(3);
			PortRegion startRegion = null;
			PortRegion endRegion = null;
			if(pointsListO != null && pointsListO.size()>=2) {
				listOfPoints = new Vector<Point2D>();
				if("startNode".equals(((Vector<Object>)(pointsListO.firstElement())).get(0)))
					startRegion = PortRegion.valueOf((String)(((Vector<Object>)(pointsListO.firstElement())).get(1)));
				if("endNode".equals(((Vector<Object>)(pointsListO.lastElement())).get(0)))
					endRegion = PortRegion.valueOf((String)(((Vector<Object>)(pointsListO.lastElement())).get(1)));
				for (Object pointO : pointsListO) {
					Vector<Object> pointV = (Vector<Object>) pointO;
					if("defaultPoint".equals(pointV.get(0))) {
						Point2D pointP = new Point2D((float) pointV.get(1), (float) pointV.get(2)); 
						listOfPoints.addElement(pointP);
					}
				}
			}

			Edge object = new DelegationEdge(
					(Integer) edgeInfoAsList.get(0), // id
					(Integer) edgeInfoAsList.get(1), // startId
					(Integer) edgeInfoAsList.get(2), // endId
					listOfPoints, // points
					startRegion, endRegion,
					diagram);
			result.add(object);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Vector<Edge> getAllRoleFillerEdges(FmmlxDiagram diagram) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, diagram, "getAllRoleFillerEdges", new Value[]{});
		Vector<Object> responseContent = (Vector<Object>) (response.get(0));
		Vector<Edge> result = new Vector<>();

		for (Object edgeInfo : responseContent) {
			Vector<Object> edgeInfoAsList = (Vector<Object>) (edgeInfo);

			Vector<Point2D> listOfPoints = null;
			Vector<Object> pointsListO = (Vector<Object>) edgeInfoAsList.get(3);
			PortRegion startRegion = null;
			PortRegion endRegion = null;
			if(pointsListO != null && pointsListO.size()>=2) {
				listOfPoints = new Vector<Point2D>();
				if("startNode".equals(((Vector<Object>)(pointsListO.firstElement())).get(0)))
					startRegion = PortRegion.valueOf((String)(((Vector<Object>)(pointsListO.firstElement())).get(1)));
				if("endNode".equals(((Vector<Object>)(pointsListO.lastElement())).get(0)))
					endRegion = PortRegion.valueOf((String)(((Vector<Object>)(pointsListO.lastElement())).get(1)));
				for (Object pointO : pointsListO) {
					Vector<Object> pointV = (Vector<Object>) pointO;
					if("defaultPoint".equals(pointV.get(0))) {
						Point2D pointP = new Point2D((float) pointV.get(1), (float) pointV.get(2)); 
						listOfPoints.addElement(pointP);
					}
				}
			}

			Edge object = new RoleFillerEdge(
					(Integer) edgeInfoAsList.get(0), // id
					(Integer) edgeInfoAsList.get(1), // startId
					(Integer) edgeInfoAsList.get(2), // endId
					listOfPoints, // points
					startRegion, endRegion,
					diagram);
			result.add(object);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Vector<Edge> getAllAssociations(FmmlxDiagram diagram) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, diagram, "getAllAssociations", new Value[]{});
		Vector<Object> responseContent = (Vector<Object>) (response.get(0));
		Vector<Edge> result = new Vector<>();

		for (Object edgeInfo : responseContent) {
			Vector<Object> edgeInfoAsList = (Vector<Object>) (edgeInfo);

			Vector<Point2D> listOfPoints = null;
			Vector<Object> pointsListO = (Vector<Object>) edgeInfoAsList.get(4);
			PortRegion startRegion = null;
			PortRegion endRegion = null;
			if(pointsListO != null && pointsListO.size()>=2) {
				listOfPoints = new Vector<Point2D>();
				if("startNode".equals(((Vector<Object>)(pointsListO.firstElement())).get(0)))
					startRegion = PortRegion.valueOf((String)(((Vector<Object>)(pointsListO.firstElement())).get(1)));
				if("endNode".equals(((Vector<Object>)(pointsListO.lastElement())).get(0)))
					endRegion = PortRegion.valueOf((String)(((Vector<Object>)(pointsListO.lastElement())).get(1)));
				for (Object pointO : pointsListO) {
					Vector<Object> pointV = (Vector<Object>) pointO;
					if("defaultPoint".equals(pointV.get(0))) {
						Point2D pointP = new Point2D((float) pointV.get(1), (float) pointV.get(2)); 
						listOfPoints.addElement(pointP);
					}
				}
			}
			
			Vector<Object> labelPositions = (Vector<Object>) edgeInfoAsList.get(13);
			
			Edge object = new FmmlxAssociation(
					(Integer) edgeInfoAsList.get(0), // id
					(Integer) edgeInfoAsList.get(1), // startId
					(Integer) edgeInfoAsList.get(2), // endId
					(Integer) edgeInfoAsList.get(3), // parentId
					listOfPoints, // points
					startRegion, endRegion,
					(String) edgeInfoAsList.get(5), // name 1
					(String) edgeInfoAsList.get(6), // name 2
					(String) edgeInfoAsList.get(7), // name source->target
					(String) edgeInfoAsList.get(8), // name target->source
					(Integer) edgeInfoAsList.get(9), // level source->target
					(Integer) edgeInfoAsList.get(10), // level target->source
					Multiplicity.parseMultiplicity((Vector<Object>) edgeInfoAsList.get(11)), //mul source->target
					Multiplicity.parseMultiplicity((Vector<Object>) edgeInfoAsList.get(12)), //mul target->source
					(Boolean) edgeInfoAsList.get(14), // visibility target->source
					(Boolean) edgeInfoAsList.get(15), // visibility source->target
					(Boolean) edgeInfoAsList.get(16), // symmetric
					(Boolean) edgeInfoAsList.get(17), // transitive
					labelPositions,
					diagram 
					//,(Integer) edgeInfoAsList.get(13), // sourceHead
					//(Integer) edgeInfoAsList.get(14) // targetHead
			);
			result.add(object);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Vector<Edge> getAllAssociationsInstances(FmmlxDiagram diagram) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, diagram, "getAllAssociationInstances", new Value[]{});
		Vector<Object> responseContent = (Vector<Object>) (response.get(0));
		Vector<Edge> result = new Vector<>();

		for (Object edgeInfo : responseContent) {
			Vector<Object> edgeInfoAsList = (Vector<Object>) (edgeInfo);

			Vector<Point2D> listOfPoints = null;
			Vector<Object> pointsListO = (Vector<Object>) edgeInfoAsList.get(4);
			PortRegion startRegion = null;
			PortRegion endRegion = null;
			if(pointsListO != null && pointsListO.size()>=2) {
				listOfPoints = new Vector<Point2D>();
				if("startNode".equals(((Vector<Object>)(pointsListO.firstElement())).get(0)))
					startRegion = PortRegion.valueOf((String)(((Vector<Object>)(pointsListO.firstElement())).get(1)));
				if("endNode".equals(((Vector<Object>)(pointsListO.lastElement())).get(0)))
					endRegion = PortRegion.valueOf((String)(((Vector<Object>)(pointsListO.lastElement())).get(1)));
				for (Object pointO : pointsListO) {
					Vector<Object> pointV = (Vector<Object>) pointO;
					if("defaultPoint".equals(pointV.get(0))) {
						Point2D pointP = new Point2D((float) pointV.get(1), (float) pointV.get(2)); 
						listOfPoints.addElement(pointP);
					}
				}
			}

			Vector<Object> labelPositions = (Vector<Object>) edgeInfoAsList.get(5);
			
			Edge object = new FmmlxLink(
					(Integer) edgeInfoAsList.get(0), // id
					(Integer) edgeInfoAsList.get(1), // startId
					(Integer) edgeInfoAsList.get(2), // endId
					(Integer) edgeInfoAsList.get(3), // ofId
					listOfPoints, // points
					startRegion, endRegion,
					labelPositions,
					diagram);
			result.add(object);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Vector<Vector<FmmlxAttribute>> fetchAttributes(FmmlxDiagram diagram, String className) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, diagram, "getAllAttributes", new Value[]{new Value(className)});
		Vector<Object> twoLists = (Vector<Object>) (response.get(0));
		Vector<FmmlxAttribute> resultOwn = new Vector<>();
		Vector<FmmlxAttribute> resultOther = new Vector<>();

		Vector<Object> ownAttList = (Vector<Object>) twoLists.get(0);
		Vector<Object> otherAttList = (Vector<Object>) twoLists.get(1);
		for (Object o : ownAttList) {
			Vector<Object> attInfo = (Vector<Object>) o;
			FmmlxAttribute object = new FmmlxAttribute(
					(String) attInfo.get(0),
					(Integer) attInfo.get(2),
					(String) attInfo.get(1),
					(Integer) attInfo.get(4),
					Multiplicity.parseMultiplicity((Vector<Object>) attInfo.get(3)));
			resultOwn.add(object);
		}
		for (Object o : otherAttList) {
			Vector<Object> attInfo = (Vector<Object>) o;
			FmmlxAttribute object = new FmmlxAttribute(
					(String) attInfo.get(0),
					(Integer) attInfo.get(2),
					(String) attInfo.get(1),
					(Integer) attInfo.get(4),
					Multiplicity.parseMultiplicity((Vector<Object>) attInfo.get(3)));
			resultOther.add(object);
		}
		Vector<Vector<FmmlxAttribute>> result = new Vector<>();
		result.addElement(resultOwn);
		result.addElement(resultOther);
		return result;
	}

	@SuppressWarnings("unchecked")
	public Vector<FmmlxOperation> fetchOperations(FmmlxDiagram diagram, String className) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, diagram, "getOwnOperations", new Value[]{new Value(className)});
		Vector<Object> response0 = (Vector<Object>) (response.get(0));
		Vector<FmmlxOperation> result = new Vector<>();
		for (Object o : response0) {
			Vector<Object> opInfo = (Vector<Object>) o;

			Vector<Object> paramNamesO = (Vector<Object>) opInfo.get(1);
			Vector<String> paramNamesS = new Vector<String>();
			for (Object O : paramNamesO) {
				paramNamesS.add((String) O);
			}
			
			Vector<Object> paramTypesO = (Vector<Object>) opInfo.get(2);
			Vector<String> paramTypesS = new Vector<String>();
			for (Object O : paramTypesO) {
				paramTypesS.add((String) O);
			}
		
			FmmlxOperation op =
				new FmmlxOperation(
					(String) opInfo.get(0), // name
					paramNamesS, // paramNames
					paramTypesS, // paramTypes
					(Integer) opInfo.get(3), // level
					(String) opInfo.get(4), // type
					(String) opInfo.get(5), // body
					(Integer) opInfo.get(6), // owner
					null, // multiplicity
					(Boolean) opInfo.get(8) // isMonitored
				);
			result.add(op);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Vector<FmmlxSlot> fetchSlots(FmmlxDiagram diagram, FmmlxObject owner, Vector<String> slotNames) throws TimeOutException {
		Value[] slotNameArray = createValueArrayString(slotNames);
		Vector<Object> response = xmfRequest(handler, diagram, "getSlots", new Value[]{new Value(owner.getName()), new Value(slotNameArray)});
		Vector<Object> slotList = (Vector<Object>) (response.get(0));
		Vector<FmmlxSlot> result = new Vector<>();
		for (Object slotO : slotList) {
			Vector<Object> slot = (Vector<Object>) (slotO);
			String name = (String) (slot.get(0));
			String value = (String) (slot.get(1));
			result.add(new FmmlxSlot(name, value, owner));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Vector<FmmlxOperationValue> fetchOperationValues(FmmlxDiagram diagram, String objectName, Vector<String> monitoredOperationsNames) throws TimeOutException {
		Value[] monitoredOperationsNameArray = createValueArrayString(monitoredOperationsNames);
		Vector<Object> response = xmfRequest(handler, diagram, "getOperationValues", new Value[]{new Value(objectName), new Value(monitoredOperationsNameArray)});
		Vector<Object> returnValuesList = (Vector<Object>) (response.get(0));
		Vector<FmmlxOperationValue> result = new Vector<>();
		for (Object returnValueO : returnValuesList) {
			Vector<Object> returnValue = (Vector<Object>) (returnValueO);
			String name = (String) (returnValue.get(0));
			String value = returnValue.get(1) == null?"null":(returnValue.get(1)).toString();
			Boolean hasRange = (Boolean) returnValue.get(2);
			Boolean isInRange = (Boolean) returnValue.get(3);
			result.add(new FmmlxOperationValue(name, value, hasRange, isInRange));
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

	@SuppressWarnings("unchecked")
	public Vector<FmmlxEnum> fetchAllEnums(FmmlxDiagram diagram) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, diagram, "getAllEnums");
		Vector<Object> enumList = (Vector<Object>) (response.get(0));
		Vector<FmmlxEnum> result = new Vector<FmmlxEnum>();
		for (Object enumO : enumList) {
			Vector<Object> enumV = (Vector<Object>) enumO;
			String           name = (String)         (enumV.get(0));
			Vector<Object> itemsV = (Vector<Object>) (enumV.get(1));
			Vector<String> items = new Vector<String>();
			for(Object itemO : itemsV) {
				String itemName = (String) itemO;
				items.add(itemName);
			}
			result.add(new FmmlxEnum(name, items));
		}
		return result;
	}
	
	////////////////////////////////////////////////
	/// Operations storing graphical info to xmf ///
	////////////////////////////////////////////////

	public void sendCurrentPosition(FmmlxDiagram diagram, FmmlxObject o) {
//		Vector<Object> response = xmfRequest(handler, "sendNewPosition",
//				new Value[]{});
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(o.id),
				new Value((int)(o.getX())), 
				new Value((int)(o.getY()))};
		sendMessage("sendNewPosition", message);
	}

	public void sendCurrentPositions(FmmlxDiagram diagram, Edge e) {
		Vector<Point2D> points = e.getIntermediatePoints();
		
		if(points.size() < 2) System.err.println("Suspicious edge alignment");
		for(Point2D p : points) {
			if(!Double.isFinite(p.getX())) {
				System.err.println("Suspicious X coordinate");
			}
			if(!Double.isFinite(p.getY())) {
				System.err.println("Suspicious Y coordinate");
			}
		}

		Value[] listOfPoints = new Value[points.size() + 2];
		{
			Value[] pointS = new Value[3];
			pointS[0] = new Value("startNode");
			pointS[1] = new Value(e.sourceNode.getDirectionForEdge(e.sourceEnd, true).toString());
			pointS[2] = new Value(0);
			listOfPoints[0] = new Value(pointS);
		}
		for (int i = 0; i < points.size(); i++) {
			Value[] point = new Value[3];
			point[0] = new Value("defaultPoint");
			point[1] = new Value((float) (points.get(i).getX()));
			point[2] = new Value((float) (points.get(i).getY()));
			listOfPoints[i+1] = new Value(point);
		}
		{
			Value[] pointE = new Value[3];
			pointE[0] = new Value("endNode");
			pointE[1] = new Value(e.targetNode.getDirectionForEdge(e.targetEnd, false).toString());
			pointE[2] = new Value(0);
			listOfPoints[listOfPoints.length-1] = new Value(pointE);
		}
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(e.id), 
				new Value(listOfPoints)};
		sendMessage("sendNewPositions", message);
	}

	////////////////////////////////////////////////////
	/// Operations requesting data to be manipulated ///
	////////////////////////////////////////////////////

	public void addMetaClass(FmmlxDiagram diagram, String name, int level, Vector<String> parents, boolean isAbstract, int x, int y) {
		Value[] parentsArray = createValueArray(parents);

		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(name),
				new Value(level),
				new Value(parentsArray),
				new Value(isAbstract),
				new Value(x), new Value(y)};
		sendMessage("addMetaClass", message);
	}

	public void addNewInstance(FmmlxDiagram diagram, String className, String name, int level, Vector<String> parents, boolean isAbstract, int x,
							   int y) {
		Value[] parentsArray = createValueArray(parents);

		Value[] message = new Value[]{getNoReturnExpectedMessageID(diagram.getID()), new Value(className), new Value(name),
				new Value(parentsArray), new Value(isAbstract), new Value(x), new Value(y), new Value(new Value[] {})};
		sendMessage("addInstance", message);
	}
	

	public void addNewInstanceWithSlots(
			FmmlxDiagram diagram, 
			String className,
			String instanceName,
			Vector<String> parents,
			HashMap<FmmlxAttribute, String> slotValues, 
			int x, int y) {
		Value[] parentsArray = createValueArray(parents);
		
		Value[] slotList = new Value[slotValues.size()];
		
		int i = 0;
		for(FmmlxAttribute att : slotValues.keySet()) {
			Value name = new Value(att.name);
			Value value = new Value(slotValues.get(att));
			Value pair = new Value(new Value[] {name, value});
			slotList[i] = pair;
			i++;
		}
		
		Value[] message = new Value[]{getNoReturnExpectedMessageID(diagram.getID()), new Value(className), new Value(instanceName),
				new Value(parentsArray), new Value(false), new Value(x), new Value(y), new Value(slotList)};
		sendMessage("addInstance", message);
	}

	public void removeClass(FmmlxDiagram diagram, String className, int strategy) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(className),
				new Value(strategy)};
		sendMessage("removeClass", message);
	}

	public void removeAssociation(FmmlxDiagram diagram, int assocId, int strategy) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(assocId)};
		sendMessage("removeAssociation", message);
	}

	public void setAssociationEndVisibility(FmmlxDiagram diagram, int assocId, boolean targetEnd, boolean newVisbility) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(assocId),
				new Value(targetEnd),
				new Value(newVisbility)};
		sendMessage("setAssociationEndVisibility", message);
	}

	public void addAttribute(FmmlxDiagram diagram, String className, String name, int level, String type, Multiplicity multi) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(className),
				new Value(name),
				new Value(level),
				new Value(type),
				new Value(multi.toValue())};
		sendMessage("addAttribute", message);
	}

	public void changeAttributeName(FmmlxDiagram diagram, String className, String oldName, String newName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(className),
				new Value(oldName),
				new Value(newName)};
		sendMessage("changeAttributeName", message);
	}

	public void changeAttributeLevel(FmmlxDiagram diagram, String objectName, String attName, int oldLevel, int newLevel) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(objectName),
				new Value(attName),
				new Value(oldLevel),
				new Value(newLevel)};
		sendMessage("changeAttributeLevel", message);
	}

	public void changeAttributeMultiplicity(FmmlxDiagram diagram, String objectName, String name, Multiplicity oldMul,
											Multiplicity newMul) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(objectName),
				new Value(name),
				new Value(oldMul.toValue()),
				new Value(newMul.toValue())};
		sendMessage("changeAttributeMultiplicity", message);

	}

	public void changeAttributeOwner(FmmlxDiagram diagram, String objectName, String name, Integer newOwnerID) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(objectName),
				new Value(name),
				new Value(newOwnerID)};
		sendMessage("changeAttributeOwner", message);
	}

	public void changeAttributeType(FmmlxDiagram diagram, String objectName, String attributeName, String oldType, String newType) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(objectName),
				new Value(attributeName),
				new Value(oldType),
				new Value(newType)};
		sendMessage("changeAttributeType", message);

	}

	public void removeAttribute(FmmlxDiagram diagram, String className, String name, int strategy) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(className),
				new Value(name),
				new Value(strategy)};
		sendMessage("removeAttribute", message);
	}

//	public void addOperation(int objectId, String operationName, int level, String operationType, String body) {
//		Value[] message = new Value[]{
//				new Value(-1),
//				new Value(objectId),
//				new Value(operationName),
//				new Value(level),
//				new Value(operationType),
//				new Value(body)
//		};
//		sendMessage("addOperation", message);
//	}

	public void addOperation2(FmmlxDiagram diagram, String objectName, int level, String body) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(objectName),
				new Value(level),
				new Value(body)
		};
		sendMessage("addOperation2", message);
	}

	public void changeOperationName(FmmlxDiagram diagram, String objectName, String oldName, String newName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(objectName),
				new Value(oldName),
				new Value(newName)};
		sendMessage("changeOperationName", message);
	}

	public void changeOperationLevel(FmmlxDiagram diagram, String objectName, String opName, int oldLevel, int newLevel) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(objectName),
				new Value(opName),
				new Value(oldLevel),
				new Value(newLevel)};
		sendMessage("changeOperationLevel", message);
	}

	public void changeOperationOwner(FmmlxDiagram diagram, String objectName, String name, Integer newOwnerID) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(objectName),
				new Value(name),
				new Value(newOwnerID)};
		sendMessage("changeOperationOwner", message);
	}

	public void changeOperationType(FmmlxDiagram diagram, String objectName, String operationName, String oldType, String newType) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(objectName),
				new Value(operationName),
//				new Value(oldType),
				new Value(newType)};
		sendMessage("changeOperationType", message);

	}

	public void checkOperationBody(String body) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(-1),
				new Value(body)
		};
		sendMessage("checkOperationBody", message);
	}

	public void removeOperation(FmmlxDiagram diagram, String objectName, String name, int strategy) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(objectName),
				new Value(name)};
		sendMessage("removeOperation", message);
	}


	public void changeClassName(FmmlxDiagram diagram, String className, String newName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(className),
				new Value(newName)};
		sendMessage("changeClassName", message);
	}

	public void changeClassLevel(FmmlxDiagram diagram, String objectName, int oldLevel, int newLevel) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(objectName),
				new Value(newLevel)};
		sendMessage("changeClassLevel", message);
	}

	
	public void changeSlotValue(FmmlxDiagram diagram, String className, String slotName, String aParsableText) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(className),
				new Value(slotName),
				new Value(aParsableText)};
		sendMessage("changeSlotValue", message);
	}

//	public void changeAssociationLevel(int objectId, int oldLevel, int newLevel) {
//		Value[] message = new Value[]{
//				new Value(-1),
//				new Value(objectId),
//				new Value(oldLevel),
//				new Value(newLevel)};
//		sendMessage("changeAssociationLevel", message);
//	}


	public void changeOf(FmmlxDiagram diagram, int objectId, int oldOfId, int newOfId) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(objectId),
				new Value(oldOfId),
				new Value(newOfId)};
		sendMessage("changeOf", message);
	}

	public void changeParent(FmmlxDiagram diagram, String objectName, Vector<String> currentParents, Vector<String> newParents) {
		Value[] parentsArray = createValueArray(currentParents);
		Value[] newParentsArray = createValueArray(newParents);

		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(objectName),
				new Value(parentsArray),
				new Value(newParentsArray)};
		sendMessage("changeParent", message);

	}
	
	public void addDelegation(FmmlxDiagram diagram, String delegateFromName, String deledgateToName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(delegateFromName), new Value(deledgateToName)};
		sendMessage("addDelegation", message);
	}	
	
	public void setRoleFiller(FmmlxDiagram diagram, String delegateFromName, String delegateToName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(delegateFromName), new Value(delegateToName)};
		sendMessage("setRoleFiller", message);
	}

	public void addAssociation(FmmlxDiagram diagram, 
			String class1Name, String class2Name,
			String accessSourceFromTargetName, String accessTargetFromSourceName,
			String fwName, String reverseName,
			Multiplicity mul1, Multiplicity mul2,
			Integer instLevel1, Integer instLevel2, boolean sourceVisible, boolean targetVisible,
			boolean isSymmetric, boolean isTransitive) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(class1Name), new Value(class2Name),
				new Value(accessSourceFromTargetName), new Value(accessTargetFromSourceName),
				new Value(fwName), reverseName == null ? new Value(-1) : new Value(reverseName),
				new Value(mul1.toValue()),
				new Value(mul2.toValue()), // multiplicity,
				new Value(instLevel1), new Value(instLevel2),
				new Value(sourceVisible), new Value(targetVisible), new Value(isSymmetric), new Value(isTransitive) };
		sendMessage("addAssociation", message);
	}

//	public void changeMultiplicityAttribute(int objectId, String attributeName, Multiplicity multi) {
//		Value[] message = new Value[]{new Value(-1),
//				new Value(objectId),
//				new Value(attributeName),
//				new Value(multi.toValue())};
//		sendMessage("changeMultiplicity", message);
//	}

	public void changeOperationBody(FmmlxDiagram diagram, int objectId, String operationName, String body) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(objectId),
				new Value(operationName),
				new Value(body)};
		sendMessage("changeOperationBody", message);
	}

	public void changeAssociationTarget(FmmlxDiagram diagram, int objectId, String associationName, Integer oldTargetID, Integer newTargetID) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
//				new Value(objectId),
				new Value(associationName),
				new Value(oldTargetID),
				new Value(newTargetID)};
		sendMessage("changeAssociationTarget", message);
	}

	// to be discussed how this may work anyway
	@Deprecated
	public void editAssociation(FmmlxDiagram diagram, int associationId, FmmlxObject source, FmmlxObject target, int newInstLevelSource,
								int newInstLevelTarget, String newDisplayNameSource, String newDisplayNameTarget,
								String newIdentifierSource, String newIdentifierTarget, Multiplicity multiSource,
								Multiplicity multiTarget) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(associationId),
				new Value(source.getId()),
				new Value(target.getId()),
				new Value(newInstLevelSource),
				new Value(newInstLevelTarget),
				new Value(newDisplayNameSource),
				newDisplayNameTarget == null ? new Value(-1) : new Value(newDisplayNameTarget),
				new Value(newIdentifierSource),
				new Value(newIdentifierTarget),
				new Value(multiSource.toValue()),
				new Value(multiTarget.toValue())};
		sendMessage("editAssociation", message);
	}

	public void addAssociationInstance(FmmlxDiagram diagram, String object1Name, String object2Name, int associationID) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(object1Name),
				new Value(object2Name),
				new Value(associationID)};
		sendMessage("addAssociationInstance", message);
	}

	public void removeAssociationInstance(FmmlxDiagram diagram, int assocInstId) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(assocInstId)
		};
		sendMessage("removeAssociationInstance", message);
	}

	public void updateAssociationInstance(FmmlxDiagram diagram, int associationInstanceId, int startObjectId, int endObjectId) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(associationInstanceId),
				new Value(startObjectId),
				new Value(endObjectId)};
		sendMessage("updateAssociationInstance", message);
	}

	public void storeLabelInfo(FmmlxDiagram diagram, DiagramEdgeLabel l) {		
		sendMessage("storeLabelInfo",l.getInfo4XMF());
		//xmfRequest(handler, "storeLabelInfo",l.getInfo4XMF());
	}

	public void changeAssociationForwardName(FmmlxDiagram diagram, int associationId, String newName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(associationId),
				new Value(newName)};
		sendMessage("changeAssociationForwardName", message);
	}

	public void changeAssociationStart2EndLevel(FmmlxDiagram diagram, int associationId, Integer newLevel) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(associationId),
				new Value(newLevel)};
		sendMessage("changeAssociationStart2EndLevel", message);
	}

	public void changeAssociationEnd2StartLevel(FmmlxDiagram diagram, int associationId, Integer newLevel) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(associationId),
				new Value(newLevel)};
		sendMessage("changeAssociationEnd2StartLevel", message);
	}

	public void changeAssociationStart2EndAccessName(FmmlxDiagram diagram, int associationId, String newName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(associationId),
				new Value(newName)};
		sendMessage("changeAssociationStart2EndAccessName", message);
	}

	public void changeAssociationEnd2StartAccessName(FmmlxDiagram diagram, int associationId, String newName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(associationId),
				new Value(newName)};
		sendMessage("changeAssociationEnd2StartAccessName", message);
	}

	public void changeAssociationStart2EndMultiplicity(FmmlxDiagram diagram, int associationId, Multiplicity newMultiplicity) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(associationId),
				new Value(newMultiplicity.toValue())};
		sendMessage("changeAssociationStart2EndMultiplicity", message);
	}

	public void changeAssociationEnd2StartMultiplicity(FmmlxDiagram diagram, int associationId, Multiplicity newMultiplicity) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(associationId),
				new Value(newMultiplicity.toValue())};
		sendMessage("changeAssociationEnd2StartMultiplicity", message);
	}

	public void setClassAbstract(FmmlxDiagram diagram, String className, boolean b) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(className),
				new Value(b)};
		sendMessage("setClassAbstract", message);		
	}

	public void levelRaiseAll(FmmlxDiagram diagram) {
		Value[] message = new Value[]{getNoReturnExpectedMessageID(diagram.getID()), new Value(1)};
		sendMessage("levelRaiseAll", message);		
	}

	public void levelLowerAll(FmmlxDiagram diagram) {
		Value[] message = new Value[]{getNoReturnExpectedMessageID(diagram.getID()), new Value(-1)};
		sendMessage("levelRaiseAll", message);		
	}

	public void printProtocol(FmmlxDiagram diagram) {
		Value[] message = new Value[]{getNoReturnExpectedMessageID(diagram.getID())};
		sendMessage("printProtocol", message);		
	}

	public void addEnumeration(FmmlxDiagram diagram, String newEnumName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(newEnumName)};
		sendMessage("addEnumeration", message);
	}

	public void changeEnumerationName(FmmlxDiagram diagram, String oldEnumName, String newEnumName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(oldEnumName),
				new Value(newEnumName)};
		sendMessage("changeEnumerationName", message);
	}
	
	public void removeEnumeration(FmmlxDiagram diagram, String enumName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(enumName)};
		sendMessage("removeEnumeration", message);
	}
	
	public void addEnumerationItem(FmmlxDiagram diagram, String enumName, String newEnumValueName) throws TimeOutException {
		Vector<Object> result = xmfRequest(handler, diagram, "addEnumerationValue", new Value[]{
				new Value(enumName),
				new Value(newEnumValueName)});
		showErrorMessage(result);
	}

	@SuppressWarnings("unchecked")
	private void showErrorMessage(Vector<Object> msgAsVec) {
		if(msgAsVec.size() <= 0) return;
		java.util.Vector<Object> err = (java.util.Vector<Object>) msgAsVec.get(0);
		if (err != null && err.size() > 0 && err.get(0) != null) {
//			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.ERROR, err.get(0) + "", ButtonType.CLOSE);
				alert.showAndWait();
//				l.countDown();
			});
//			try {
//				l.await();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		
	}

	public void changeEnumerationItemName(FmmlxDiagram diagram, String enumName, String oldEnumValueName, String newEnumValueName) throws TimeOutException {
		xmfRequest(handler, diagram, "changeEnumerationValueName", new Value[]{
				new Value(enumName),
				new Value(oldEnumValueName),
				new Value(newEnumValueName)});
	}
	
	public void removeEnumerationItem(FmmlxDiagram diagram, String enumName, String enumValueName) throws TimeOutException {
		xmfRequest(handler, diagram, "removeEnumerationValue", new Value[]{
				new Value(enumName),
				new Value(enumValueName)});
	}

	public void editEnumeration(FmmlxDiagram diagram, String enumName, Vector<String> elements) {
		Value[] elementArray = createValueArrayString(elements);
		
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(enumName),
				new Value(elementArray)};
		sendMessage("editEnum", message);
		
	}
	
	@SuppressWarnings("unchecked")
	public Vector<Issue> fetchIssues(FmmlxDiagram fmmlxDiagram) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, fmmlxDiagram, "getAllIssues");
		Vector<Object> issueList = (Vector<Object>) (response.get(0));
		Vector<Issue> result = new Vector<Issue>();
		for (Object issueO : issueList) {
			Vector<Object> issueV = (Vector<Object>) issueO;
			try{
				Issue issue = Issue.readIssue(issueV);
				result.add(issue);
			} catch (Issue.IssueNotReadableException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Vector<String> fetchAllAuxTypes(FmmlxDiagram fmmlxDiagram) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, fmmlxDiagram, "getAllAuxTypes");
		Vector<Object> auxList = (Vector<Object>) (response.get(0));
		Vector<String> result = new Vector<String>();
		for (Object auxO : auxList) {
			Vector<Object> auxV = (Vector<Object>) auxO;
			String name = (String) (auxV.get(0));
			result.add(name);
		}
		return result;
	}

	public void assignToGlobal(FmmlxDiagram fmmlxDiagram, FmmlxObject object, String varName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(fmmlxDiagram.getID()),
				new Value(object.id),
				new Value(varName)};
		sendMessage("assignToGlobal", message);
	}

	public void showBody(FmmlxDiagram fmmlxDiagram, FmmlxObject object, FmmlxOperation operation) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(fmmlxDiagram.getID()),
				new Value(object.id),
				new Value(operation.getName()),
				new Value(-1), // arity
				};
		sendMessage("showBody", message);
	}

	public void loadProjectNameFromXml(String projectName){
		Value[] message = new Value[]{
				new Value(-1),
				new Value(projectName)
		};
		sendMessage("loadProjectFromXml", message);
	}

	public void openXmlFile(String fileName){
		Deserializer deserializer = new Deserializer();
		deserializer.loadState(fileName, this);
	}

	public void openPackageBrowser() {
		WorkbenchClient.theClient().send(handler, "openPackageBrowser()", new Value[] {});
	}

	public Vector<String> evalList(FmmlxDiagram diagram, String text) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, diagram, "evalList", new Value[]{new Value(text)});
		@SuppressWarnings("unchecked")
		Vector<Object> list = (Vector<Object>) (response.get(0));
		Vector<String> result = new Vector<String>();
		for (Object o : list) {
//			Vector<Object> auxV = (Vector<Object>) auxO;
			String listElement = (String) (o);
			result.add(listElement);
		}
		return result;
	}
	
	public FaXML getDiagramData(FmmlxDiagram diagram) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, diagram, "getDiagramData", new Value[]{});
		Vector<Object> responseContent = (Vector<Object>) (response.get(0));

		return new FaXML(responseContent);
	}


}
