package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;
import tool.clients.dialogs.enquiries.FindSendersOfMessages;
import tool.clients.fmmlxdiagrams.classbrowser.CodeBox;
import tool.clients.fmmlxdiagrams.dialogs.CodeBoxPair;
import tool.clients.fmmlxdiagrams.dialogs.MergePropertyDialog.Result;
import tool.clients.serializer.FmmlxDeserializer;
import tool.clients.serializer.FmmlxSerializer;
import tool.clients.serializer.XmlManager;
import tool.clients.workbench.WorkbenchClient;
import tool.xmodeler.XModeler;
import xos.Value;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class FmmlxDiagramCommunicator {
	public static final String TAG = FmmlxDiagram.class.getSimpleName();

	private static FmmlxDiagramCommunicator self;
	
	private int handler;
	int idCounter = 0;
	private final HashMap<Integer, Vector<Object>> results = new HashMap<>();
	private final HashMap<Integer, ReturnCall<Vector<Object>>> returnMap = new HashMap<>();
	private static final Vector<FmmlxDiagram> diagrams = new Vector<>();
	private static final boolean DEBUG = false;
	static TabPane tabPane;
	public static Value getNoReturnExpectedMessageID(int diagramID) {return new Value(new Value[] {new Value(diagramID), new Value(-1)});}
	private boolean silent;
	
	/* Operations for setting up the Communicator */
	
	public FmmlxDiagramCommunicator() {
		//System.err.println("DiagramCom aufgerufen?!?FmmlxDiagramCommunicator?!?11111111");
		if(self != null) throw new IllegalStateException("FmmlxDiagramCommunicator must not be instantiated more than once.");
		self = this;
	}
	
	public static FmmlxDiagramCommunicator getCommunicator() {
		if(self != null) return self;
		//System.err.println("DiagramCom aufgerufen?getCommunicator");
		throw new IllegalStateException("FmmlxDiagramCommunicator should have been instantiated. Run initCommunicator() first");		
	}
	
	public static void initCommunicator() {
		//System.err.println("initCommunicator!?!?!?!?!?!?!?!?!");
		WorkbenchClient.theClient().startFmmlxClient();
	}

	public static void start(TabPane tabPane) {
		FmmlxDiagramCommunicator.tabPane = tabPane;
	}

	public void setHandle(final int handler) {
		this.handler = handler;
	}
	
	/* Setting up new or existing diagrams, as well as closing */
	
	public void newDiagram(int diagramID, String diagramName, String packagePath, String file, Vector<Vector<Object>> listOfViews, Vector<Vector<Object>> listOfOptions) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			if (DEBUG) System.err.println("Create FMMLx-Diagram ("+diagramName+") ...");
			FmmlxDiagram diagram = new FmmlxDiagram(this, diagramID, diagramName, packagePath, listOfViews, listOfOptions);
			if(file != null && file.length()>0){
				diagram.setFilePath(file);
			} else {
				diagram.setFilePath(copyFilePath(packagePath));
			}
			createStage(diagram.getView(), diagramName, this.handler, diagram);	
			diagrams.add(diagram);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private transient Integer _newDiagramID = null;

    public static enum DiagramType {ClassDiagram, ModelBrowser};
	
	public Integer createDiagram(String packagePath, String diagramName, String file, DiagramType type) {
		//Creates a diagram which is not displayed yet.
		Value[] message = new Value[]{
				new Value(packagePath),
				new Value(diagramName),
				new Value(file),
				new Value(type.toString())
		};
		_newDiagramID = null;
		int timeout = 0;
		
		Task<Void> task = new Task<Void>() { protected Void call() { sendMessage("createDiagramFromJava", message); return null; }};
		new Thread(task).start();
		
		while(_newDiagramID == null && timeout < 10) {
			System.err.println("timeout: " + timeout);
			timeout++;
			try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
			
		}
		if (_newDiagramID == null) throw new RuntimeException();
		return _newDiagramID;
	}
	
	public void openDiagram(String packagePath, String diagramName) {
		Value[] message = new Value[]{
				new Value(packagePath),
				new Value(diagramName)
		};
		sendMessage("showDiagram", message);
	}
	
	public void setNewDiagramId(int i) {
		_newDiagramID = i;
	}

	public void close(AbstractPackageViewer diagram, boolean keepDiagram) {
		diagrams.remove(diagram);
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagram.getID()),
				new Value(keepDiagram)};
			sendMessage("closeDiagram", message);
	}

	private Value[] createValueArray(Vector<String> vector) { // todo: make more generic
		Value[] result = new Value[vector.size()];
		for (int i = 0; i < result.length; i++) {
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
//		System.err.println(returnMap);
		if (msgAsObj instanceof java.util.Vector) {
			java.util.Vector<Object> msgAsVec = (java.util.Vector<Object>) msgAsObj;
			java.util.Vector<Object> ids = (java.util.Vector<Object>) msgAsVec.get(0);
			int diagramID = (Integer) (ids.get(0));
			if(diagramID == -1) return; // Ignore completely for now. Message not for any open diagram
			int requestID = (Integer) (ids.get(1));
			if (DEBUG) System.err.println(": Receiving request " + requestID);
			msgAsVec.remove(0);
			if (requestID == -1) {
				if (DEBUG) System.err.println("v.get(0)= " + msgAsVec.get(0));
				java.util.Vector<Object> err = (java.util.Vector<Object>) msgAsVec.get(0);
				if (err != null && err.size() > 0 && err.get(0) != null ) {
			        if(silent) {
			        	System.err.println("Error:" + err.get(0));
			        } else {
						CountDownLatch l = new CountDownLatch(1);
						Platform.runLater(() -> {
							Alert alert = new Alert(AlertType.ERROR, err.get(0) + "", ButtonType.CLOSE);
							//alert.showAndWait(); NOPE
							alert.show();
							if(err.size() > 1) {
								System.err.println("error handling: " + err.get(1));
								handleErrorMessage((java.util.Vector<Object>) err.get(1), getDiagram(diagramID));
								}								
							l.countDown();
						});
						try {
							l.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				if(returnMap.containsKey(requestID)) {
					Platform.runLater(() -> {returnMap.remove(requestID).run(msgAsVec);});
				} else {
					System.err.println("Old queue still in use");
					results.put(requestID, msgAsVec);
				}
			}
		} else {
			if (DEBUG) System.err.println("o: " + msgAsObj + "(" + msgAsObj.getClass() + ")");
		}
	}

	private void handleErrorMessage(Vector<Object> errorInfo, FmmlxDiagram diagram) {
		Object problem = errorInfo.get(0);
		if("addOperation:failed".equals(problem)) {
//			FmmlxObject o = diagram.getObjectByPath((String)errorInfo.get(1));
//			int level = (Integer) errorInfo.get(2);
//			String code = (String) errorInfo.get(3);
//			
//			diagram.actions.addOperationDialog(o);
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
	private Vector<Object> xmfRequest(int targetHandle, int diagramID, String message, Value... args) throws TimeOutException {
		Value[] args2 = new Value[args.length + 1];
		int requestID = idCounter++;
		if (DEBUG) System.err.println(": Sending request " + message + "(" + requestID + ") handle" + targetHandle);
		System.arraycopy(args, 0, args2, 1, args.length);
		args2[0] = new Value(new Value[] {new Value(diagramID), new Value(requestID)});
		boolean waiting = true;
		WorkbenchClient.theClient().send(targetHandle, message, args2);
		int attempts = 0;
		int sleep = 2;
		long START = System.currentTimeMillis();
		while (waiting && sleep < 200 * 100) {
			if (DEBUG) System.err.println(attempts + ". attempt");
			attempts++;
			try {
				Thread.sleep(sleep);
				sleep = (int) (sleep * 1.5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (results.containsKey(requestID)) {
				waiting = false;
				if (DEBUG) System.err.println("  received after " + (System.currentTimeMillis() - START) + "ms.");
			}
		}

		if (waiting)
			throw new TimeOutException(message + args);
		return results.remove(requestID);
	}
	
	private void xmfRequestAsync(int targetHandle, int diagramID, 
			String message, ReturnCall<Vector<Object>> returnCall, Value... args) {

		Value[] args2 = new Value[args.length + 1];
		int requestID = idCounter++;
		if (DEBUG) System.err.println(": Sending request " + message + "(" + requestID + ") handle" + targetHandle);
		System.arraycopy(args, 0, args2, 1, args.length);
		args2[0] = new Value(new Value[] {new Value(diagramID), new Value(requestID)});
		returnMap.put(requestID, returnCall);
		WorkbenchClient.theClient().send(targetHandle, message, args2);
	}

	private void sendMessage(String command, Value[] message) {
		if (DEBUG) System.err.println(": Sending command " + command);
		WorkbenchClient.theClient().send(handler, command, message);
	}

	/////////////////////////////////////////
	/// Operations asking for information ///
	/////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public void  getAllObjects(AbstractPackageViewer diagram, ReturnCall<Vector<FmmlxObject>> objectsReceivedReturn) {
		
		ReturnCall<Vector<Object>> localReturn = (response) -> {
			Vector<Object> responseContent = (Vector<Object>) (response.get(0));
			Vector<FmmlxObject> result = new Vector<>();
			for (Object responseObject : responseContent) {
				Vector<Object> responseObjectList = (Vector<Object>) (responseObject);
				
				Vector<Object> parentListO2 = (Vector<Object>) responseObjectList.get(12);
				Vector<String> parentListS = new Vector<>();
				for (Object o : parentListO2) {
					parentListS.add((String) o);
				}
				FmmlxObject object = new FmmlxObject(
						(String)  responseObjectList.get(1), // name
						(Integer) responseObjectList.get(2), // level
						(String)  responseObjectList.get(10), // ownPath
						(String)  responseObjectList.get(11), // ofPath
						parentListS,                          // parentsPath
						(Boolean) responseObjectList.get(5),
						(Integer) responseObjectList.get(6), // x-Position
						(Integer) responseObjectList.get(7), // y-Position 
						(Boolean) responseObjectList.get(8), // hidden
						diagram);
				result.add(object);
			}
			objectsReceivedReturn.run(result);
		};
		
		xmfRequestAsync(handler, diagram.getID(), "getAllObjects", localReturn);
		
	}

	@SuppressWarnings("unchecked")
	public void getAllInheritanceEdges(AbstractPackageViewer diagram, ReturnCall<Vector<Edge<?>>> inheritanceEdgeReceivedReturn) {
		ReturnCall<Vector<Object>> localReturn = (response) -> {
			Vector<Object> responseContent = (Vector<Object>) (response.get(0));
			Vector<Edge<?>> result = new Vector<>();
	
			for (Object edgeInfo : responseContent) {
				Vector<Object> edgeInfoAsList = (Vector<Object>) (edgeInfo);
	
				Vector<Point2D> listOfPoints = null;
				Vector<Object> pointsListO = (Vector<Object>) edgeInfoAsList.get(3);
				PortRegion startRegion = null;
				PortRegion endRegion = null;
				if(pointsListO != null && pointsListO.size()>=2) {
					listOfPoints = new Vector<>();
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
	
				InheritanceEdge object = new InheritanceEdge(
						(String) edgeInfoAsList.get(0), // id
						(String) edgeInfoAsList.get(1), //TODO startId
						(String) edgeInfoAsList.get(2), //TODO endId
						listOfPoints, // points
						startRegion, endRegion,
						diagram);
	
				result.add(object);
			}
			inheritanceEdgeReceivedReturn.run(result);
		};
		xmfRequestAsync(handler, diagram.getID(), "getAllInheritanceEdges", localReturn);		
	}
	
	@SuppressWarnings("unchecked")
	public void getAllDelegationEdges(AbstractPackageViewer diagram, ReturnCall<Vector<Edge<?>>> delegationEdgeReceivedReturn) {
		ReturnCall<Vector<Object>> localReturn = (response) -> {
			Vector<Object> responseContent = (Vector<Object>) (response.get(0));
			Vector<Edge<?>> result = new Vector<>();
	
			for (Object edgeInfo : responseContent) {
				Vector<Object> edgeInfoAsList = (Vector<Object>) (edgeInfo);
	
				Vector<Point2D> listOfPoints = null;
				Vector<Object> pointsListO = (Vector<Object>) edgeInfoAsList.get(4);
				PortRegion startRegion = null;
				PortRegion endRegion = null;
				if(pointsListO != null && pointsListO.size()>=2) {
					listOfPoints = new Vector<>();
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
	
				DelegationEdge object = new DelegationEdge(
						(String) edgeInfoAsList.get(0), // id
						(String) edgeInfoAsList.get(1), // startId
						(String) edgeInfoAsList.get(2), // endId
						(Integer) edgeInfoAsList.get(3), // level
						listOfPoints, // points
						startRegion, endRegion,
						diagram);
				result.add(object);
	
			}
			delegationEdgeReceivedReturn.run(result);
		};
		xmfRequestAsync(handler, diagram.getID(), "getAllDelegationEdges", localReturn);
	}
	
	@SuppressWarnings("unchecked")
	public void getAllRoleFillerEdges(AbstractPackageViewer diagram, ReturnCall<Vector<Edge<?>>> roleFillerEdgesReceivedReturn) {
		ReturnCall<Vector<Object>> localReturn = (response) -> {
			Vector<Object> responseContent = (Vector<Object>) (response.get(0));
			Vector<Edge<?>> result = new Vector<>();
	
			for (Object edgeInfo : responseContent) {
				Vector<Object> edgeInfoAsList = (Vector<Object>) (edgeInfo);
	
				Vector<Point2D> listOfPoints = null;
				Vector<Object> pointsListO = (Vector<Object>) edgeInfoAsList.get(3);
				PortRegion startRegion = null;
				PortRegion endRegion = null;
				if(pointsListO != null && pointsListO.size()>=2) {
					listOfPoints = new Vector<>();
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
	
				RoleFillerEdge object = new RoleFillerEdge(
						(String) edgeInfoAsList.get(0), // id
						(String) edgeInfoAsList.get(1), //TODO startId
						(String) edgeInfoAsList.get(2), //TODO endId
						listOfPoints, // points
						startRegion, endRegion,
						diagram);
	
				result.add(object);
			}
			roleFillerEdgesReceivedReturn.run(result);
		};
		xmfRequestAsync(handler, diagram.getID(), "getAllRoleFillerEdges", localReturn);
		
	}

    @SuppressWarnings("unchecked")
	public HashMap<String, HashMap<String, Object>> getAllEdgePositions(Integer diagramID) {
		HashMap<String, HashMap<String, Object>> result = new HashMap<>();

		try {
			Vector<Object> response = xmfRequest(handler, -2, "getAllEdgePositions", new Value(diagramID));
			Vector<Object> responseContent = (Vector<Object>) (response.get(0));

			for(Object contentItem : responseContent) {
				Vector<Object> contentVector = (Vector<Object>) contentItem;
				String key = (String) contentVector.get(0);
				Vector<Object> edgeInfo =  (Vector<Object>) contentVector.get(1);

				Vector<Object> portInfo = new Vector<>();
				if(edgeInfo.size()>0){
					portInfo.add(edgeInfo.get(0));
					portInfo.add(edgeInfo.get(edgeInfo.size()-1));
				}
				Vector<Object> intermediatePoints = new Vector<>();
				for(int i = 1; i< Objects.requireNonNull(edgeInfo).size()-1 ; i++){
					intermediatePoints.add(edgeInfo.get(i));
				}

				HashMap<String, Object> edgeInfoMap = new HashMap<>();
				edgeInfoMap.put("Ports", portInfo);
				edgeInfoMap.put("IntermediatePoints", intermediatePoints);

				result.put(key, edgeInfoMap);
			}
		} catch (TimeOutException e) {
			e.printStackTrace();
		}
        return result;
    }
	
	@SuppressWarnings("unchecked")
	public void getAllAssociations(AbstractPackageViewer diagram, ReturnCall<Vector<Edge<?>>> associationsReceivedReturn) {
		ReturnCall<Vector<Object>> returnCall = response -> {
			Vector<Object> responseContent = (Vector<Object>) (response.get(0));
			Vector<Edge<?>> result = new Vector<>();
	
			for (Object edgeInfo : responseContent) {
				Vector<Object> edgeInfoAsList = (Vector<Object>) (edgeInfo);
	
				Vector<Point2D> listOfPoints = null;
				Vector<Object> pointsListO = (Vector<Object>) edgeInfoAsList.get(4);
				PortRegion startRegion = null;
				PortRegion endRegion = null;
				if(pointsListO != null && pointsListO.size()>=2) {
					listOfPoints = new Vector<>();
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
	
				FmmlxAssociation object = new FmmlxAssociation(
						(String) edgeInfoAsList.get(0), // id
						(String) edgeInfoAsList.get(1), // startId
						(String) edgeInfoAsList.get(2), // endId
						(Integer) edgeInfoAsList.get(3), // parentId
						listOfPoints, // points
						startRegion, endRegion,
						(String) edgeInfoAsList.get(5), // name 1
						(String) edgeInfoAsList.get(6), // name 2
						(String) edgeInfoAsList.get(7), // name source->target
						(String) edgeInfoAsList.get(8), // name target->source
						(Integer) edgeInfoAsList.get(9), // level source
						(Integer) edgeInfoAsList.get(10), // level target
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
			associationsReceivedReturn.run(result);
		};
		
		xmfRequestAsync(handler, diagram.getID(), "getAllAssociations", returnCall);
	
	}

	@SuppressWarnings("unchecked")
	public void getAllAssociationsInstances(AbstractPackageViewer diagram, ReturnCall<Vector<Edge<?>>> linksReceivedReturn) {
		ReturnCall<Vector<Object>> returnCall = response -> {
			Vector<Object> responseContent = (Vector<Object>) (response.get(0));
			Vector<Edge<?>> result = new Vector<>();
	
			for (Object edgeInfo : responseContent) {
				Vector<Object> edgeInfoAsList = (Vector<Object>) (edgeInfo);
	
				Vector<Point2D> listOfPoints = null;
				Vector<Object> pointsListO = (Vector<Object>) edgeInfoAsList.get(4);
				PortRegion startRegion = null;
				PortRegion endRegion = null;
				if(pointsListO != null && pointsListO.size()>=2) {
					listOfPoints = new Vector<>();
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
				
				FmmlxLink object = new FmmlxLink(
						(String) edgeInfoAsList.get(0), // id
						(String) edgeInfoAsList.get(1), // startId //TODO
						(String) edgeInfoAsList.get(2), // endId //TODO
						(String) edgeInfoAsList.get(3), // ofId	//TODO
						listOfPoints, // points
						startRegion, endRegion,
						labelPositions,
						diagram);
	
				result.add(object);
			}
			linksReceivedReturn.run(result);
		};
		
		xmfRequestAsync(handler, diagram.getID(), "getAllAssociationInstances", returnCall);

	}
	
	@SuppressWarnings("unchecked")
	public void fetchAllAttributes(AbstractPackageViewer diagram, Vector<FmmlxObject> objects, ReturnCall<Vector<FmmlxObject>> attributesReceivedReturn) {
		ReturnCall<Vector<Object>> returnCall = response -> {			
			Vector<Object> listOfAllAttributes = (Vector<Object>) (response.get(0));
			
			for(Object attributeListforOneObject : listOfAllAttributes) {
				String objPath = (String) (((Vector<Object>) attributeListforOneObject).get(0));
				for(FmmlxObject o : objects) if (o.getPath().equals(objPath)) {
					Vector<Object> ownAttList = (Vector<Object>) (((Vector<Object>) attributeListforOneObject).get(1));
					Vector<Object> otherAttList = (Vector<Object>) (((Vector<Object>) attributeListforOneObject).get(2));
					Vector<FmmlxAttribute> resultOwn = new Vector<>();
					Vector<FmmlxAttribute> resultOther = new Vector<>();
					
					for (Object a : ownAttList) {
						Vector<Object> attInfo = (Vector<Object>) a;
						FmmlxAttribute object = new FmmlxAttribute(
								(String) attInfo.get(0),
								(Integer) attInfo.get(2),
								(String) attInfo.get(1),
								(String) attInfo.get(4),
								Multiplicity.parseMultiplicity((Vector<Object>) attInfo.get(3)));
						resultOwn.add(object);
					}
					for (Object a : otherAttList) {
						Vector<Object> attInfo = (Vector<Object>) a;
						FmmlxAttribute object = new FmmlxAttribute(
								(String) attInfo.get(0),
								(Integer) attInfo.get(2),
								(String) attInfo.get(1),
								(String) attInfo.get(4),
								Multiplicity.parseMultiplicity((Vector<Object>) attInfo.get(3)));
						resultOther.add(object);
					}
					
					o.setAttributes(resultOwn, resultOther);
				}
			}
			
			attributesReceivedReturn.run(objects);
		};
		xmfRequestAsync(handler, diagram.getID(), "getAllAttributes", returnCall);
		
	}

	@SuppressWarnings("unchecked")
	public void fetchAllOperations(AbstractPackageViewer diagram, Vector<FmmlxObject> objects, ReturnCall<Vector<FmmlxObject>> operationsReceivedReturn) {
		ReturnCall<Vector<Object>> returnCall = response -> {
			Vector<Object> listOfAllOperations = (Vector<Object>) (response.get(0));
			
			for(Object operationListforOneObject : listOfAllOperations) {
			  if(operationListforOneObject != null) {
				String objPath = (String) (((Vector<Object>) operationListforOneObject).get(0));
				for(FmmlxObject obj : objects) if (obj.getPath().equals(objPath)) {
					Vector<Object> ownOpList = (Vector<Object>) (((Vector<Object>) operationListforOneObject).get(1));
					Vector<FmmlxOperation> result = new Vector<>();
					
					for (Object o : ownOpList) {
						Vector<Object> opInfo = (Vector<Object>) o;
	
						Vector<Object> paramNamesO = (Vector<Object>) opInfo.get(1);
						Vector<String> paramNamesS = new Vector<>();
						for (Object O : paramNamesO) {
							paramNamesS.add((String) O);
						}
						
						Vector<Object> paramTypesO = (Vector<Object>) opInfo.get(2);
						Vector<String> paramTypesS = new Vector<>();
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
								(String) opInfo.get(6), // owner
								null, // multiplicity
								(Boolean) opInfo.get(8), // isMonitored
								(Boolean) opInfo.get(9) // delToClass
							);
						result.add(op);
					}
					
					obj.setOperations(result);
				}
			  }
			}
			operationsReceivedReturn.run(objects);
		};
		xmfRequestAsync(handler, diagram.getID(), "getAllOperations", returnCall);
		
	}
	

	
	@SuppressWarnings("unchecked")
	public void fetchAllConstraints(AbstractPackageViewer diagram, Vector<FmmlxObject> objects, ReturnCall<Vector<FmmlxObject>> constraintsReceivedReturn) {
		ReturnCall<Vector<Object>> returnCall = response -> {
			Vector<Object> listOfAllConstraints = (Vector<Object>) (response.get(0));
		
			for(Object constraintListForOneObject : listOfAllConstraints) {
				String objPath = (String) (((Vector<Object>) constraintListForOneObject).get(0));
				for(FmmlxObject obj : objects) if (obj.getPath().equals(objPath)) {
					Vector<Object> constraintList = (Vector<Object>) (((Vector<Object>) constraintListForOneObject).get(1));
					Vector<Constraint> result = new Vector<>();
		
					for (Object o : constraintList) {
						Vector<Object> conInfo = (Vector<Object>) o;
					
						Constraint con =
							new Constraint(
								(String)  conInfo.get(0), // name
								(Integer) conInfo.get(1), // level
								(String)  conInfo.get(2), // body-raw
								(String)  conInfo.get(3), // body-full
								(String)  conInfo.get(4), // reason-raw
								(String)  conInfo.get(5) // reason-full
							);
						result.add(con);
					}
					obj.setConstraints(result);
				}
			}
			constraintsReceivedReturn.run(objects);
		};
		xmfRequestAsync(handler, diagram.getID(), "getAllConstraints", returnCall);
	}
	
    @SuppressWarnings("unchecked")
    public void fetchIssues(AbstractPackageViewer abstractPackageViewer, ReturnCall<Vector<Issue>> issuesReceivedReturn) {
    	ReturnCall<Vector<Object>> returnCall = response -> {
    		Vector<Object> issueList = (Vector<Object>) (response.get(0));

		    Vector<Issue> result = new Vector<>();
		    int issueNumber = 0;
		    for (Object issueO : issueList) {
		        Vector<Object> issueV = (Vector<Object>) issueO;
		        try {
		            Issue issue = Issue.readIssue(issueV);
		            issue.setIssueNumber(issueNumber);
					issueNumber++;
		            result.add(issue);
		        } catch (Issue.IssueNotReadableException e) {
		            e.printStackTrace();
		        }
		    }
		    issuesReceivedReturn.run(result);
    	};        
        xmfRequestAsync(handler, abstractPackageViewer.getID(), "getAllIssues", returnCall);
    }

    @SuppressWarnings("unchecked")
    public void fetchAllAuxTypes(AbstractPackageViewer fmmlxDiagram, ReturnCall<Vector<String>> auxReceivedReturn) {
	    ReturnCall<Vector<Object>> returnCall = response -> {
	    	Vector<Object> auxList = (Vector<Object>) (response.get(0));
	        Vector<String> result = new Vector<>();
	        for (Object auxO : auxList) {
	            Vector<Object> auxV = (Vector<Object>) auxO;
	            String name = (String) (auxV.get(0));
	            result.add(name);
	        }
	        auxReceivedReturn.run(result);
	    };
	    xmfRequestAsync(handler, fmmlxDiagram.getID(), "getAllAuxTypes", returnCall);
    }
    
    @SuppressWarnings("unchecked")
    public void fetchAllSlots(AbstractPackageViewer diagram, HashMap<FmmlxObject, Vector<String>> slotNames, ReturnCall<?> slotsReceivedReturn) {
    	java.util.Set<FmmlxObject> objects = slotNames.keySet();
    	Value[] objectSlotList = new Value[slotNames.size()];
    	int count = 0;
    	for(FmmlxObject o : slotNames.keySet()) {
    		Value[] slotNameArray = createValueArray(slotNames.get(o));
    		Value[] objInfo = new Value[] {new Value(o.getName()), new Value(slotNameArray)};
    		objectSlotList[count] = new Value(objInfo); count++;
    	}
    	
    	ReturnCall<Vector<Object>> returnCall = responseAllObjects -> {
    		Vector<Object> response = (Vector<Object>) (responseAllObjects.get(0));
    		for(Object o : response) {
    			Vector<Object> responseO = (Vector<Object>) o;
    			String objName = (String) (responseO.get(0));
    			Vector<Object> slotList = (Vector<Object>) (responseO.get(1));
    			for(FmmlxObject obj : objects) if (obj.getName().equals(objName)) {
	    			Vector<FmmlxSlot> result = new Vector<>();
	    			for (Object slotO : slotList) {
	    				Vector<Object> slot = (Vector<Object>) (slotO);
	    				String name = (String) (slot.get(0));
	    				String value = (String) (slot.get(1));
	    				result.add(new FmmlxSlot(name, value, obj));
	    				Collections.sort(result);
	    			}   	
	    			obj.slots = result;
    			}
    		}
    		slotsReceivedReturn.run(null);
    	};
    	xmfRequestAsync(handler, diagram.getID(), "getAllSlots", returnCall, new Value(objectSlotList));
    }
    

	public void checkSyntax(AbstractPackageViewer diagram, String operationBody, ReturnCall<CodeBoxPair.OperationException> result) {
		ReturnCall<Vector<Object>> returnCall = syntaxCheckResponse -> {
			Object response = syntaxCheckResponse.get(0);
			if(response == null) {
				result.run(null);
			} else {
				Vector<Object> responseV = (Vector<Object>) response;
				Object message = responseV.get(0);
				CodeBoxPair.OperationException e = new CodeBoxPair.OperationException();
				e.message = ""+message;
				e.lineCount = 0;//(Integer) lineCount;
				e.charCount = 0;//(Integer) charCount;
				result.run(e);
			}
		};
		xmfRequestAsync(handler, diagram.getID(), "checkSyntax", returnCall, new Value(operationBody));
	}
    
    
//	@SuppressWarnings("unchecked")
//	public Vector<FmmlxSlot> fetchSlots(AbstractPackageViewer diagram, FmmlxObject owner, Vector<String> slotNames) throws TimeOutException {
//		Value[] slotNameArray = createValueArray(slotNames);
//		Vector<Object> response = xmfRequest(handler, diagram.getID(), "getSlots", new Value(owner.getName()), new Value(slotNameArray));
//		Vector<Object> slotList = (Vector<Object>) (response.get(0));
//		Vector<FmmlxSlot> result = new Vector<>();
//		for (Object slotO : slotList) {
//			Vector<Object> slot = (Vector<Object>) (slotO);
//			String name = (String) (slot.get(0));
//			String value = (String) (slot.get(1));
//			result.add(new FmmlxSlot(name, value, owner));
//			Collections.sort(result);
//		}
//		return result;
//	}

    @SuppressWarnings("unchecked")
    public void fetchAllOperationValues(AbstractPackageViewer diagram, HashMap<FmmlxObject, Vector<String>> monOpNames, ReturnCall<?> opValReceivedReturn) {
    	java.util.Set<FmmlxObject> objects = monOpNames.keySet();
    	Value[] objectOpValList = new Value[monOpNames.size()];
    	int count = 0;
    	for(FmmlxObject o : monOpNames.keySet()) {
    		Value[] slotNameArray = createValueArray(monOpNames.get(o));
    		Value[] objInfo = new Value[] {new Value(o.getName()), new Value(slotNameArray)};
    		objectOpValList[count] = new Value(objInfo); count++;
    	}
    	
    	ReturnCall<Vector<Object>> returnCall = responseAllObjects -> {
    		Vector<Object> response = (Vector<Object>) (responseAllObjects.get(0));
    		for(Object o : response) {
    			Vector<Object> responseO = (Vector<Object>) o;
    			String objName = (String) (responseO.get(0));
    			Vector<Object> returnValuesList = (Vector<Object>) (responseO.get(1));
    			for(FmmlxObject obj : objects) if (obj.getName().equals(objName)) {
    				Vector<FmmlxOperationValue> result = new Vector<>();
	    			for (Object returnValueO : returnValuesList) {
	    				Vector<Object> returnValue = (Vector<Object>) (returnValueO);
	    				String name = (String) (returnValue.get(0));
	    				String value = returnValue.get(1) == null?"null":(returnValue.get(1)).toString();
	    				Boolean hasRange = (Boolean) returnValue.get(2);
	    				Boolean isInRange = (Boolean) returnValue.get(3);
	    				result.add(new FmmlxOperationValue(name, value, hasRange, isInRange));
	    			}	
	    			obj.operationValues = result;
    			}
    		}
    		opValReceivedReturn.run(null);
    	};
    	xmfRequestAsync(handler, diagram.getID(), "getAllOperationValues", returnCall, new Value(objectOpValList));
    }
    
//    @SuppressWarnings("unchecked")
//	public Vector<FmmlxOperationValue> fetchOperationValues(AbstractPackageViewer diagram, String objectName, Vector<String> monitoredOperationsNames) throws TimeOutException {
//		Value[] monitoredOperationsNameArray = createValueArray(monitoredOperationsNames);
//		Vector<Object> response = xmfRequest(handler, diagram.getID(), "getOperationValues", new Value(objectName), new Value(monitoredOperationsNameArray));
//		Vector<Object> returnValuesList = (Vector<Object>) (response.get(0));
//		Vector<FmmlxOperationValue> result = new Vector<>();
//		for (Object returnValueO : returnValuesList) {
//			Vector<Object> returnValue = (Vector<Object>) (returnValueO);
//			String name = (String) (returnValue.get(0));
//			String value = returnValue.get(1) == null?"null":(returnValue.get(1)).toString();
//			Boolean hasRange = (Boolean) returnValue.get(2);
//			Boolean isInRange = (Boolean) returnValue.get(3);
//			result.add(new FmmlxOperationValue(name, value, hasRange, isInRange));
//		}
//		return result;
//	}

	@SuppressWarnings("unchecked")
	public void fetchAllEnums(AbstractPackageViewer diagram, ReturnCall<Vector<FmmlxEnum>> enumsReceivedReturn) {
		ReturnCall<Vector<Object>> returnCall = response -> {
			Vector<Object> enumList = (Vector<Object>) (response.get(0));
			Vector<FmmlxEnum> result = new Vector<>();
			for (Object enumO : enumList) {
				Vector<Object> enumV = (Vector<Object>) enumO;
				String           name = (String)         (enumV.get(0));
				Vector<Object> itemsV = (Vector<Object>) (enumV.get(1));
				Vector<String> items = new Vector<>();
				for(Object itemO : itemsV) {
					String itemName = (String) itemO;
					items.add(itemName);
				}
				result.add(new FmmlxEnum(name, items));
			}
			enumsReceivedReturn.run(result);
		};		
		xmfRequestAsync(handler, diagram.getID(), "getAllEnums", returnCall);
	}
	
	////////////////////////////////////////////////
	/// Operations storing graphical info to xmf ///
	////////////////////////////////////////////////

	public void sendCurrentPosition(int diagramID, String objectPath, int x, int y, boolean hidden) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(objectPath),
				new Value(x),
				new Value(y),
				new Value(hidden)};
		sendMessage("sendNewPosition", message);
	}

	public void sendCurrentPositions(int diagramID, Edge<?> edge) {
		Vector<Point2D> points = edge.getIntermediatePoints();
		
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
			pointS[1] = new Value(edge.sourceNode.getDirectionForEdge(edge.sourceEnd, true).toString());
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
			pointE[1] = new Value(edge.targetNode.getDirectionForEdge(edge.targetEnd, false).toString());
			pointE[2] = new Value(0);
			listOfPoints[listOfPoints.length-1] = new Value(pointE);
		}
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(edge.path),
				new Value(listOfPoints)};
		sendMessage("sendNewPositions", message);
	}

	public void sendEdgePositionsFromXml(int diagramID, String edgePath, Vector<Point2D> intermediatePoints, String sourcePort, String targetPort) {
		if(intermediatePoints.size() < 2) System.err.println("Suspicious edge alignment");
		for(Point2D p : intermediatePoints) {
			if(!Double.isFinite(p.getX())) {
				System.err.println("Suspicious X coordinate");
			}
			if(!Double.isFinite(p.getY())) {
				System.err.println("Suspicious Y coordinate");
			}
		}

		Value[] listOfPoints = new Value[intermediatePoints.size() + 2];
		{
			Value[] pointS = new Value[3];
			pointS[0] = new Value("startNode");
			pointS[1] = new Value(sourcePort);
			pointS[2] = new Value(0);
			listOfPoints[0] = new Value(pointS);
		}
		for (int i = 0; i < intermediatePoints.size(); i++) {
			Value[] point = new Value[3];
			point[0] = new Value("defaultPoint");
			point[1] = new Value((float) (intermediatePoints.get(i).getX()));
			point[2] = new Value((float) (intermediatePoints.get(i).getY()));
			listOfPoints[i+1] = new Value(point);
		}
		{
			Value[] pointE = new Value[3];
			pointE[0] = new Value("endNode");
			pointE[1] = new Value(targetPort);
			pointE[2] = new Value(0);
			listOfPoints[listOfPoints.length-1] = new Value(pointE);
		}
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(edgePath),
				new Value(listOfPoints)};
		sendMessage("sendNewPositions", message);
	}

	////////////////////////////////////////////////////
	/// Operations requesting data to be manipulated ///
	////////////////////////////////////////////////////

	public void addMetaClass(int diagramID, String name, int level, Vector<String> parents, boolean isAbstract, int x, int y, boolean hidden) {
		Value[] parentsArray = createValueArray(parents);

		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(name),
				new Value(level),
				new Value(parentsArray),
				new Value(isAbstract),
				new Value(x), new Value(y), new Value(hidden)};
		sendMessage("addMetaClass", message);
	}
	
	public void addNewInstance(int diagramID, String className, String name, Integer level, Vector<String> parents, boolean isAbstract, int x,
							   int y, boolean hidden) {
		Value[] parentsArray = createValueArray(parents);

		Value[] message = new Value[]{getNoReturnExpectedMessageID(diagramID), new Value(className), new Value(name), new Value(level),
				new Value(parentsArray), new Value(isAbstract), new Value(x), new Value(y), new Value(hidden), new Value(new Value[] {})};
		sendMessage("addInstance", message);
	}
	

	public void addNewInstanceWithSlots(
			int diagramID, 
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
		
		Value[] message = new Value[]{getNoReturnExpectedMessageID(diagramID), new Value(className), new Value(instanceName),
				new Value(parentsArray), new Value(false), new Value(x), new Value(y), new Value(slotList)};
		sendMessage("addInstance", message);
	}
	
	public void classify(int diagramID, Vector<FmmlxObject> objects, String className) {
		Value[] objectNames = new Value[objects.size()];
		for(int i = 0; i < objectNames.length; i++) {
			objectNames[i] = new Value(objects.get(i).name);
		}
		Value[] message = new Value[]{
			getNoReturnExpectedMessageID(diagramID),
			new Value(objectNames),
			new Value(className)};
		sendMessage("classify", message);
	}

	public void removeClass(int diagramID, String className, int strategy) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(className),
				new Value(strategy)};
		sendMessage("removeClass", message);
	}

	public void removeAssociation(int diagramID, String associationName, int strategy) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(associationName)};
		sendMessage("removeAssociation", message);
	}

	public void setAssociationEndVisibility(int diagramID, String assocName, boolean targetEnd, boolean newVisbility) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(assocName),
				new Value(targetEnd),
				new Value(newVisbility)};
		sendMessage("setAssociationEndVisibility", message);
	}

	public void addAttribute(int diagramID, String className, String name, int level, String type, Multiplicity multi) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(className),
				new Value(name),
				new Value(level),
				new Value(type),
				new Value(multi.toValue())};
		sendMessage("addAttribute", message);
	}

	public void changeAttributeName(int diagramID, String className, String oldName, String newName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(className),
				new Value(oldName),
				new Value(newName)};
		sendMessage("changeAttributeName", message);
	}

	public void changeAttributeLevel(int diagramID, String objectName, String attName, int oldLevel, int newLevel) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(objectName),
				new Value(attName),
				new Value(oldLevel),
				new Value(newLevel)};
		sendMessage("changeAttributeLevel", message);
	}

	public void changeAttributeMultiplicity(int diagramID, String objectName, String name, Multiplicity oldMul,
											Multiplicity newMul) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(objectName),
				new Value(name),
				new Value(oldMul.toValue()),
				new Value(newMul.toValue())};
		sendMessage("changeAttributeMultiplicity", message);

	}

	public void changeAttributeOwner(int diagramID, String oldClassName, String name, String newClassName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(oldClassName),
				new Value(name),
				new Value(newClassName)};
		sendMessage("changeAttributeOwner", message);
	}

	public void changeAttributeType(int diagramID, String objectName, String attributeName, String oldType, String newType) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(objectName),
				new Value(attributeName),
				new Value(oldType),
				new Value(newType)};
		sendMessage("changeAttributeType", message);

	}

	public void removeAttribute(int diagramID, String className, String name, int strategy) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
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

    public void addOperation2(int diagramID, String objectName, int level, String body) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(objectName),
                new Value(level),
                new Value(body)
        };
        sendMessage("addOperation2", message);
    }

    public void changeOperationName(int diagramID, String objectName, String oldName, String newName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(objectName),
                new Value(oldName),
                new Value(newName)};
        sendMessage("changeOperationName", message);
    }

    public void changeOperationLevel(int diagramID, String objectName, String opName, int oldLevel, int newLevel) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(objectName),
                new Value(opName),
                new Value(oldLevel),
                new Value(newLevel)};
        sendMessage("changeOperationLevel", message);
    }

    public void changeOperationOwner(int diagramID, String objectName, String name, String newOwnerName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(objectName),
                new Value(name),
                new Value(newOwnerName)};
        sendMessage("changeOperationOwner", message);
    }

    public void changeOperationType(int diagramID, String objectName, String operationName, String newType) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
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

    public void removeOperation(int diagramID, String objectName, String name, int strategy) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(objectName),
                new Value(name)};
        sendMessage("removeOperation", message);
    }

	public void mergeProperties(FmmlxObject mergeIntoClass, Value[] message) {
		sendMessage("mergeProperties", message);
	}

    public void changeClassName(int diagramID, String className, String newName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(className),
                new Value(newName)};
        sendMessage("changeClassName", message);
    }

    public void changeClassLevel(int diagramID, String objectName, int newLevel) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(objectName),
                new Value(newLevel)};
        sendMessage("changeClassLevel", message);
    }


    public void changeSlotValue(int diagramID, String className, String slotName, String aParsableText) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
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


    public void changeOf(int diagramID, String objectName, String oldOfName, String newOfName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(objectName),
                new Value(oldOfName),
                new Value(newOfName)};
        sendMessage("changeOf", message);
    }

    public void changeParent(int diagramID, String objectName, Vector<String> currentParents, Vector<String> newParents) {
        Value[] parentsArray = createValueArray(currentParents);
        Value[] newParentsArray = createValueArray(newParents);

        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(objectName),
                new Value(parentsArray),
                new Value(newParentsArray)};
        sendMessage("changeParent", message);
    }

    public void addDelegation(int diagramID, String delegateFromName, String delegateToName, Integer delegateToLevel) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(delegateFromName), new Value(delegateToName), new Value(delegateToLevel)};
        sendMessage("addDelegation", message);
    }

    public void removeDelegation(int diagramID, String delegateFromName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(delegateFromName)};
        sendMessage("removeDelegation", message);
    }

    public void setRoleFiller(int diagramID, String delegateFromName, String delegateToName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(delegateFromName), new Value(delegateToName)};
        sendMessage("setRoleFiller", message);
    }

    public void removeRoleFiller(int diagramID, String roleName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(roleName)};
        sendMessage("removeRoleFiller", message);
    }

    public void addAssociation(int diagramID,
                               String classSourceName, String classTargetName,
                               String accessSourceFromTargetName, String accessTargetFromSourceName,
                               String fwName, String reverseName,
                               Multiplicity multTargetToSource, Multiplicity multSourceToTarget,
                               Integer instLevelSource, Integer instLevelTarget, boolean sourceVisible, boolean targetVisible,
                               boolean isSymmetric, boolean isTransitive) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(classSourceName), new Value(classTargetName),
                new Value(accessSourceFromTargetName), new Value(accessTargetFromSourceName),
                new Value(fwName), reverseName == null ? new Value(-1) : new Value(reverseName),
                new Value(multTargetToSource.toValue()),
                new Value(multSourceToTarget.toValue()), // multiplicity,
                new Value(instLevelSource), new Value(instLevelTarget),
                new Value(sourceVisible), new Value(targetVisible), new Value(isSymmetric), new Value(isTransitive)};
        sendMessage("addAssociation", message);
    }

//	public void changeMultiplicityAttribute(int objectId, String attributeName, Multiplicity multi) {
//		Value[] message = new Value[]{new Value(-1),
//				new Value(objectId),
//				new Value(attributeName),
//				new Value(multi.toValue())};
//		sendMessage("changeMultiplicity", message);
//	}

    public void changeOperationBody(int diagramID, String objectName, String operationName, String body) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(objectName),
                new Value(operationName),
                new Value(body)};
        sendMessage("changeOperationBody", message);
    }

    public void changeAssociationTarget(int diagramID, String associationName, String oldTargetName, String newTargetName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
//				new Value(objectName),
                new Value(associationName),
                new Value(oldTargetName),
                new Value(newTargetName)};
        sendMessage("changeAssociationTarget", message);
    }

    // to be discussed how this may work anyway
    @Deprecated
    public void editAssociation(int diagramID, int associationId, FmmlxObject source, FmmlxObject target, int newInstLevelSource,
                                int newInstLevelTarget, String newDisplayNameSource, String newDisplayNameTarget,
                                String newIdentifierSource, String newIdentifierTarget, Multiplicity multiSource,
                                Multiplicity multiTarget) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(associationId),
                new Value(source.getPath()),
                new Value(target.getPath()),
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
    
    public void addAssociationInstance(int diagramID, String object1Name, String object2Name, String associationName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(object1Name),
                new Value(object2Name),
                new Value(associationName)};
        sendMessage("addAssociationInstance", message);
    }

    public void removeAssociationInstance(int diagramID, String assocName, String sourceName, String targetName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(assocName),
                new Value(sourceName),
                new Value(targetName)
        };
        sendMessage("removeAssociationInstance", message);
    }

    public void updateAssociationInstance(int diagramID, String associationInstanceId, String startObjectPath, String endObjectPath) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(associationInstanceId),
                //TODO new Value(startObjectId),
                new Value(startObjectPath),
                //new Value(endObjectId)};
                new Value(endObjectPath)};
        sendMessage("updateAssociationInstance", message);
    }

    public void storeLabelInfo(FmmlxDiagram diagram, DiagramEdgeLabel<?> l) {
        sendMessage("storeLabelInfo", l.getInfo4XMF());
        //xmfRequest(handler, "storeLabelInfo",l.getInfo4XMF());
    }

    public void storeLabelInfoFromXml(int diagramId, double relativeX, double relativeY) {
        Value[] message = new Value[]{
                new Value(new Value[]{new Value(diagramId), new Value(-1)}),
                new Value("ownerPath"),
                new Value("localID"), //TODO ???
                new Value((float) relativeX),
                new Value((float) relativeY)};
        sendMessage("storeLabelInfo", message);
    }

    public void changeAssociationForwardName(int diagramID, String associationName, String newFwName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(associationName),
                new Value(newFwName)};
        sendMessage("changeAssociationForwardName", message);
    }

    public void changeDelegationLevel(int diagramID, String sourceName, Integer newLevel) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(sourceName),
                new Value(newLevel)};
        sendMessage("changeDelegationLevel", message);
    }
    
    public void changeAssociationStart2EndLevel(int diagramID, String associationName, Integer newLevel) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(associationName),
                new Value(newLevel)};
        sendMessage("changeAssociationStart2EndLevel", message);
    }

    public void changeAssociationEnd2StartLevel(int diagramID, String associationName, Integer newLevel) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(associationName),
                new Value(newLevel)};
        sendMessage("changeAssociationEnd2StartLevel", message);
    }

    public void changeAssociationStart2EndAccessName(int diagramID, String associationName, String newName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(associationName),
                new Value(newName)};
        sendMessage("changeAssociationStart2EndAccessName", message);
    }

    public void changeAssociationEnd2StartAccessName(int diagramID, String associationName, String newName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(associationName),
                new Value(newName)};
        sendMessage("changeAssociationEnd2StartAccessName", message);
    }

    public void changeAssociationStart2EndMultiplicity(int diagramID, String associationName, Multiplicity newMultiplicity) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(associationName),
                new Value(newMultiplicity.toValue())};
        sendMessage("changeAssociationStart2EndMultiplicity", message);
    }

    public void changeAssociationEnd2StartMultiplicity(int diagramID, String associationName, Multiplicity newMultiplicity) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(associationName),
                new Value(newMultiplicity.toValue())};
        sendMessage("changeAssociationEnd2StartMultiplicity", message);
    }

    public void setClassAbstract(int diagramID, String className, boolean b) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(className),
                new Value(b)};
        sendMessage("setClassAbstract", message);
    }

    public void levelRaiseAll(int diagramID) {
        Value[] message = new Value[]{getNoReturnExpectedMessageID(diagramID), new Value(1)};
        sendMessage("levelRaiseAll", message);
    }

    public void levelLowerAll(int diagramID) {
        Value[] message = new Value[]{getNoReturnExpectedMessageID(diagramID), new Value(-1)};
        sendMessage("levelRaiseAll", message);
    }

    public void printProtocol(int diagramID) {
        Value[] message = new Value[]{getNoReturnExpectedMessageID(diagramID)};
        sendMessage("printProtocol", message);
    }

    public void addEnumeration(int diagramID, String newEnumName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(newEnumName)};
        sendMessage("addEnumeration", message);
    }

    public void changeEnumerationName(int diagramID, String oldEnumName, String newEnumName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(oldEnumName),
                new Value(newEnumName)};
        sendMessage("changeEnumerationName", message);
    }

    public void removeEnumeration(int diagramID, String enumName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(enumName)};
        sendMessage("removeEnumeration", message);
    }

    public void addEnumerationItem(int diagramID, String enumName, String newEnumValueName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(enumName),
                new Value(newEnumValueName)};
        sendMessage("addEnumerationValue", message);
    }
    
    public void changeEnumerationItemName(int diagramID, String enumName, String oldEnumValueName, String newEnumValueName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(enumName),
                new Value(oldEnumValueName),
                new Value(newEnumValueName)};
        sendMessage("changeEnumerationValueName", message);
    }
    
    public void removeEnumerationItem(int diagramID, String enumName, String enumValueName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(enumName),
                new Value(enumValueName)};
        sendMessage("removeEnumerationValue", message);
    }

    public void editEnumeration(int diagramID, String enumName, Vector<String> elements) {
        Value[] elementArray = createValueArray(elements);
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(enumName),
                new Value(elementArray)};
        sendMessage("editEnum", message);
    }    
    
	public void addConstraint(int diagramID, String path, String constName, Integer instLevel, String body, String reason) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
                new Value(path),
                new Value(constName),
                new Value(instLevel),
                new Value(body),
                new Value(reason)
		};
        sendMessage("addConstraint", message);
	}
	
	public void editConstraint(int diagramID, String oldPath, String path,String oldConstName, String constName,Integer oldInstLevel, Integer instLevel,String oldBody, String body,String oldReason, String reason) {
		if (!constName.equals(oldConstName)) {
			Value [] message = new Value[] {
					getNoReturnExpectedMessageID(diagramID),
					new Value(path),
					new Value(oldConstName),
					new Value(constName)
			};
			sendMessage("changeConstraintName", message);	
		}

		if (!instLevel.equals(oldInstLevel)) {
			Value[] message = new Value[]{
					getNoReturnExpectedMessageID(diagramID),
	                new Value(path),
	                new Value(constName),
	                new Value(instLevel)
			};
	        sendMessage("changeConstraintLevel", message);
		}
		
		if((!body.equals(oldBody)) || (!reason.equals(oldReason))) {
			Value[] message = new Value[]{
					getNoReturnExpectedMessageID(diagramID),
	                new Value(path),
	                new Value(constName),
	                new Value(body),
	                new Value(reason)
			};
	        sendMessage("changeConstraintBodyAndReason", message);
		}
		
	}
	
	public void changeConstraintName(int diagramID, String path, String oldName, String newName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
                new Value(path),
                new Value(oldName),
                new Value(newName)
		};
        sendMessage("changeConstraintName", message);
	}
	
	public void changeConstraintLevel(int diagramID, String path, String name, String level) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
                new Value(path),
                new Value(name),
                new Value(level)
		};
        sendMessage("changeConstraintLevel", message);
	}
	
	public void changeConstraintBodyAndReason(int diagramID, String path, String name, String body, String reason) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
                new Value(path),
                new Value(name),
                new Value(body),
                new Value(reason)
		};
        sendMessage("changeConstraintBodyAndReason", message);
	}	
//	
//	public void changeConstraintBody(int diagramID, String path, String name, String body) {
//		Value[] message = new Value[]{
//				getNoReturnExpectedMessageID(diagramID),
//                new Value(path),
//                new Value(name),
//                new Value(body)
//		};
//        sendMessage("changeConstraintBody", message);
//	}
//	
//	public void changeConstraintReason(int diagramID, String path, String name, String reason) {
//		Value[] message = new Value[]{
//				getNoReturnExpectedMessageID(diagramID),
//                new Value(path),
//                new Value(name),
//                new Value(reason)
//		};
//        sendMessage("changeConstraintReason", message);
//	}
	
	public void changeConstraintOwner(int diagramID, String oldOwner, String newOwner, String name) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
                new Value(oldOwner),
                new Value(newOwner),
                new Value(name)
		};
        sendMessage("changeConstraintOwner", message);
	}
	
	public void removeConstraint(int diagramID, String path, String name) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
                new Value(path),
                new Value(name)
		};
        sendMessage("removeConstraint", message);
	}

    public void hideElements(int diagramID, Vector<FmmlxObject> objects, Boolean hide) {
        Value[] vec = new Value[objects.size()];
        for (int i = 0; i < vec.length; i++) {
            vec[i] = new Value(objects.get(i).getPath());
        }
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(vec),
                new Value(hide)};
        sendMessage("hideElements", message);
    }

    public void assignToGlobal(int diagramID, FmmlxObject object, String varName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(object.getPath()),
                new Value(varName)};
        sendMessage("assignToGlobal", message);
    }

    public void showBody(AbstractPackageViewer fmmlxDiagram, FmmlxObject object, FmmlxOperation operation) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(fmmlxDiagram.getID()),
                new Value(object.getPath()),
                new Value(operation.getName()),
                new Value(-1), // arity
        };
        sendMessage("showBody", message);
    }

    public void createProject(String projectName, Vector<String> diagramNames, String file) {
        Value[] diagramNamesValue = createValueArray(diagramNames);
        Value[] message = new Value[]{
                new Value(-1),
                new Value(projectName),
                new Value(diagramNamesValue),
                new Value(file)
        };
        sendMessage("loadProjectFromXml", message);
    }

    public void openXmlFile(String fileName) {
        FmmlxDeserializer fmmlxDeserializer = new FmmlxDeserializer(new XmlManager(fileName));
        new Thread(() -> fmmlxDeserializer.loadProject(this)).start(); // Very important. Otherwise assigning diagramID will get stuck
		XModeler.bringControlCenterToFront();
	}

    public void openPackageBrowser() {
        WorkbenchClient.theClient().send(handler, "openPackageBrowser()");
    }

    public Vector<String> evalList(AbstractPackageViewer diagram, String text) throws TimeOutException {
        Vector<Object> response = xmfRequest(handler, diagram.getID(), "evalList", new Value(text));
        @SuppressWarnings("unchecked")
        Vector<Object> list = (Vector<Object>) (response.get(0));
        Vector<String> result = new Vector<>();
        for (Object o : list) {
//			Vector<Object> auxV = (Vector<Object>) auxO;
            String listElement = (String) (o);
            result.add(listElement);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public FaXML getDiagramData(Integer diagramID) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, diagramID, "getDiagramData");
        Vector<Object> responseContent = (Vector<Object>) (response.get(0));
		return new FaXML(responseContent);
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, String> findImplementation(AbstractPackageViewer diagram, Vector<String> names, String model, Integer arity, String returnType) throws TimeOutException {
        Vector<Object> response = xmfRequest(handler, diagram.getID(), "findOperationImplementation", new Value(createValueArray(names)), // opNames
                new Value(arity),// arity
                new Value(model),// model
                new Value(returnType)// param4
        );

        Vector<Object> responseContent = (Vector<Object>) (response.get(0));

        HashMap<String, String> result = new HashMap<>();
        for (Object resultItemO : responseContent) {
            Vector<Object> resultItem = (Vector<Object>) (resultItemO);
			result.put((String) resultItem.get(0), (String)resultItem.get(6));
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, String> findAllOperations(FmmlxDiagram diagram) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, diagram.getID(), "findAllOperations");
		
		Vector<Object> responseContent = (Vector<Object>) (response.get(0));
		
		HashMap<String, String> result = new HashMap<>();
		for(Object resultItemO : responseContent) {
			Vector<Object> resultItem = (Vector<Object>) (resultItemO);
			result.put((String) resultItem.get(0), (String)resultItem.get(6));
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, String> findOperationUsage(FmmlxDiagram diagram, String name, String model) throws TimeOutException {
		Vector<Object> response = xmfRequest(handler, diagram.getID(), "findOperationUsage", new Value(name), // opNames
				new Value(model)// model
		);
		
		Vector<Object> responseContent = (Vector<Object>) (response.get(0));
		
		HashMap<String, String> result = new HashMap<>();
		for(Object resultItemO : responseContent) {
			Vector<Object> resultItem = (Vector<Object>) (resultItemO);
			result.put((String) resultItem.get(0), (String)resultItem.get(6));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public void findOperationUsage(AbstractPackageViewer diagram, FindSendersOfMessages findSendersOfMessages, String name,
			String model) {
		Vector<Object> response;
		try {
			response = xmfRequest(handler, diagram.getID(), "findOperationUsage", new Value(name),
					new Value(model));

			Vector<Object> responseContent = (Vector<Object>) (response.get(0));
			
			HashMap<String, String> result = new HashMap<>();
			for(Object resultItemO : responseContent) {
				Vector<Object> resultItem = (Vector<Object>) (resultItemO);
				result.put(resultItem.get(0) + "::" + resultItem.get(2), (String)resultItem.get(7));
				
			}Platform.runLater(()->{
				findSendersOfMessages.sendResponse(result);
			});
			
		} catch (TimeOutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ########################## Tab ### Stage #######################

	private void createStage(javafx.scene.Node node, String name, int id, final FmmlxDiagram diagram) {
		Stage stage = new Stage();
		BorderPane border = new BorderPane();
		border.setCenter(node);
		Scene scene = new Scene(border, 1000, 605);
		stage.setScene(scene);
		stage.setTitle(name);
		
		//LM, 17.11.2021, resize canvas on maximize
		// The update can only be achieved in a parallel thread as the actual size of the stage is
		// not updated at the same time as the attribute "maximized".
		stage.maximizedProperty().addListener( (observer, x, y) -> {
			Thread newThread = new Thread(() -> {
				diagram.redraw();
			});
			newThread.start();
		});
		
		stage.show();
		stage.setOnCloseRequest((e) -> closeScene(stage, e, id, name, node, diagram));
	}

	private void closeScene(Stage stage, Event wevent, int id, String name, javafx.scene.Node node, FmmlxDiagram diagram) {
		close(diagram, true);
	}


	public void saveFile(String packageString) {
		String packageName = packageString.substring(1,packageString.length()-1).split(" ")[1];
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() {
				try {
					for(FmmlxDiagram diagram : diagrams){
						String tmp_packageName = diagram.getPackagePath().split("::")[1];
						if(packageName.equals(tmp_packageName)){
							String filePath = diagram.getFilePath();
							FmmlxDiagramCommunicator communicator = diagram.getComm();
							String label = diagram.getDiagramLabel();
							FmmlxSerializer serializer = new FmmlxSerializer(diagram.getFilePath());
							serializer.save(diagram.getPackagePath(), filePath, label, diagram.getID(), communicator);
						}
					}
				} catch (TransformerException | ParserConfigurationException e) {
					if(e instanceof TransformerException){
						saveXmlFile2(diagrams.get(0).getPackagePath(), diagrams.get(0).getID());
					} else {
						e.printStackTrace();
					}
				}
				return null;
			}
		};
		new Thread(task).start();

	}

	public void saveXmlFile(String fileName, String packageString) {
		String packageName = packageString.substring(1,packageString.length()-1).split(" ")[1];
		FmmlxDiagramCommunicator communicator = this;
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws TransformerException, ParserConfigurationException {
				try {
					String diagramPath = null;
					String initLabel = null;
					FmmlxSerializer serializer = new FmmlxSerializer(fileName);
					serializer.clearAllData();
					for(FmmlxDiagram diagram : diagrams){
						String tmp_packageName = diagram.getPackagePath().split("::")[1];
						if(packageName.equals(tmp_packageName)){
							diagram.setFilePath(fileName);
							diagramPath = diagram.packagePath;
							initLabel = diagram.getDiagramLabel();
						}
					}
					serializer.saveAsXml(diagramPath, initLabel, communicator);
				} catch (TimeOutException e) {
					e.printStackTrace();
				}
				return null;
			}

		};

		new Thread(task).start();
	}

	public void saveXmlFile2(String diagramPath, Integer id) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(id),
				new Value(diagramPath),
				new Value(diagramPath.split("::")[1])
		};
		sendMessage("saveAsXml", message);
	}

	private String copyFilePath(String packagePath) {
		for (FmmlxDiagram diagram: diagrams){
			if(diagram.getPackagePath().equals(packagePath)){
				String file = diagram.getFilePath();
				if(file != null && file.length()>0){
					return file;
				}
			}
		}
		return "";
	}

	public void closeDiagram(int diagramID) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID)};
		sendMessage("closeDiagram", message);
	}
	
	@SuppressWarnings("unchecked")
	public Vector<Integer> getAllDiagramIDs(String packagePath) {
		Vector<Integer> result = new Vector<>();
		try {
			Vector<Object> response = xmfRequest(handler, -2, "getAllDiagrams", new Value(packagePath));
			Vector<Object> responseContent = (Vector<Object>) (response.get(0));
			
			for(Object e : responseContent) {
				result.add((Integer) e);
			}
			return result;
		} catch (TimeOutException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, HashMap<String, Object>> getAllObjectPositions(int diagramID) {
		HashMap<String, HashMap<String, Object>> result = new HashMap<>();
		try {
			Vector<Object> response = xmfRequest(handler, -2, "getAllObjectPositions", new Value(diagramID));
			Vector<Object> responseContent = (Vector<Object>) (response.get(0));
			
			for(Object o : responseContent) {
				Vector<Object> objInfo = (Vector<Object>) o;
				String key = (String) objInfo.get(0);
				Integer x = (Integer) objInfo.get(1);
				Integer y = (Integer) objInfo.get(2);
				Boolean hidden = (Boolean) objInfo.get(3);

				HashMap<String, Object> objectMap = new HashMap<>();
				objectMap.put("x", x);
				objectMap.put("y", y);
				objectMap.put("hidden", hidden);
				result.put(key, objectMap);
			}
			return result;
		} catch (TimeOutException e1) {
			throw new RuntimeException(e1);
		}
	}

    @SuppressWarnings("unchecked")
	public HashMap<String, HashMap<String, Object>> getAllLabelPositions(int id) {
		HashMap<String, HashMap<String, Object>> result = new HashMap<>();
		Vector<Object> response;
		try {
			response = xmfRequest(handler, -2, "getAllLabelPositions", new Value(id));
			Vector<Object> responseContent = (Vector<Object>) (response.get(0));

			for (Object o : responseContent) {
				Vector<Object> labelInfo = (Vector<Object>) o;
				String ownerID = (String) labelInfo.get(0);
				Integer localID = (Integer) labelInfo.get(1);
				float x = (float) labelInfo.get(2);
				float y = (float) labelInfo.get(3);

//				if(type == 0){
					HashMap<String, Object> labelMap = new HashMap<>();
					labelMap.put("ownerID", ownerID);
					labelMap.put("localID", localID);
					labelMap.put("x", x);
					labelMap.put("y", y);
					result.put(ownerID + "::" + localID, labelMap); // just some arbitrary stuff for the hashMap
//				}
			}
		} catch (TimeOutException e) {
			e.printStackTrace();
		}

        return result;
    }

	@SuppressWarnings("unchecked")
	public void testGetAllEdgePositions(int id) {
		Vector<Object> response;
		try {
			response = xmfRequest(handler, -2, "getAllEdgePositions", new Value(id));
			Vector<Object> responseContent = (Vector<Object>) (response.get(0));
			System.out.println(responseContent);
		} catch (TimeOutException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public void testGetAllLabelPositions(int id) {
		Vector<Object> response;
		try {
			response = xmfRequest(handler, -2, "getAllLabelPositions", new Value(id));
			Vector<Object> responseContent = (Vector<Object>) (response.get(0));
			System.out.println(responseContent);
		} catch (TimeOutException e) {
			e.printStackTrace();
		}
	}

	private HashMap<Integer, org.w3c.dom.Node> positionInfos = new HashMap<>();
	
	public void preparePositionInfo(Integer diagramId, org.w3c.dom.Node diagramNode) {
		positionInfos.put(diagramId, diagramNode);	
	}

	public org.w3c.dom.Node getPositionInfo(Integer id) {
		org.w3c.dom.Node positionInfos = this.positionInfos.get(id);
		return positionInfos;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	public void fileSaved(String filePath, Integer id) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(id),
				new Value(filePath),
		};
		sendMessage("isSaved", message);
	}

	public static FmmlxDiagram getDiagram(Integer id) {
		for(FmmlxDiagram diagram : diagrams) {
			if(id == diagram.diagramID) {
				return diagram;
			}
		}
		return null;
	}
	// -------------------- merge package ---------------------------- //

	public void mergeMetaClass(int diagramID, String name, int level, Vector<String> parents, boolean isAbstract, int x, int y, boolean hidden) {
		Value[] parentsArray = createValueArray(parents);

		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(name),
				new Value(level),
				new Value(parentsArray),
				new Value(isAbstract),
				new Value(x), new Value(y), new Value(hidden)};
		sendMessage("mergeMetaClass", message);
	}

	public void mergeParent(int diagramID, String className, Vector<String> oldParents, Vector<String> newParents) {
		Value[] parentsArray = createValueArray(oldParents);
		Value[] newParentsArray = createValueArray(newParents);

		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(className),
				new Value(parentsArray),
				new Value(newParentsArray)};
		sendMessage("mergeParent", message);
	}

	public void mergeAttribute(int diagramID, String className, String name, int level, String typeName, Multiplicity multiplicity) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(className),
				new Value(name),
				new Value(level),
				new Value(typeName),
				new Value(multiplicity.toValue())};
		sendMessage("mergeAttribute", message);
	}

	public void mergeNewInstance(int diagramID, String className, String name, Vector<String> parents, boolean isAbstract, int x, int y, boolean hidden) {
		Value[] parentsArray = createValueArray(parents);

		Value[] message = new Value[]{getNoReturnExpectedMessageID(diagramID), new Value(className), new Value(name),
				new Value(parentsArray), new Value(isAbstract), new Value(x), new Value(y), new Value(hidden), new Value(new Value[] {})};
		sendMessage("mergeInstance", message);
	}

	public void mergeEnumeration(int diagramID, String enumName) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(enumName)};
		sendMessage("mergeEnumeration", message);
	}

	public void mergeEnumerationItem(int diagramID, String enumName, String itemName) throws TimeOutException {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(enumName),
				new Value(itemName)
		};
		sendMessage("mergeEnumerationValue",message);

	}

	public void mergeAssociation(int diagramID, String classSourceName, String classTargetName,
								 String accessSourceFromTargetName, String accessTargetFromSourceName,
								 String fwName, String reverseName, Multiplicity multiplicityT2S, Multiplicity multiplicityS2T,
								 int instLevelSource, int instLevelTarget, boolean sourceVisibleFromTarget,
								 boolean targetVisibleFromSource, boolean isSymmetric, boolean isTransitive) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(classSourceName), new Value(classTargetName),
				new Value(accessSourceFromTargetName), new Value(accessTargetFromSourceName),
				new Value(fwName), reverseName == null ? new Value(-1) : new Value(reverseName),
				new Value(multiplicityT2S.toValue()),
				new Value(multiplicityS2T.toValue()),
				new Value(instLevelSource), new Value(instLevelTarget),
				new Value(sourceVisibleFromTarget), new Value(targetVisibleFromSource), new Value(isSymmetric), new Value(isTransitive)};
		sendMessage("mergeAssociation", message);
	}

	public void mergeAssociationInstance(int diagramID, String className1, String className2, String name) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(className1),
				new Value(className2),
				new Value(name)};
		sendMessage("mergeAssociationInstance", message);
	}

	public void mergeConstraint(int diagramID, String classPath, String constName, Integer instLevel, String body, String reason) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(classPath),
				new Value(constName),
				new Value(instLevel),
				new Value(body),
				new Value(reason)
		};
		sendMessage("mergeConstraint", message);
	}

	public void mergeRoleFiller(int diagramID, String role, String roleFiller) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(role), new Value(roleFiller)};
		sendMessage("mergeRoleFiller", message);
	}

	public void mergeDelegation(int diagramID, String delegationFromName, String delegationToName, int delegateToLevel) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(delegationFromName), new Value(delegationToName), new Value(delegateToLevel)};
		sendMessage("mergeDelegation", message);
	}

	public void mergeSlotValue(int diagramID, String className, String slotName, String valueToBeParsed) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(className),
				new Value(slotName),
				new Value(valueToBeParsed)};
		sendMessage("mergeSlotValue", message);
	}

	public void mergeOperation2(int diagramID, String className, int level, String body) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(className),
				new Value(level),
				new Value(body)
		};
		sendMessage("mergeOperation", message);
	}

	public void sendViewStatus(int diagramID, Vector<String> names, Vector<Affine> transformations) {
		if(names.size() != transformations.size()) throw new IllegalArgumentException("list sizes do not match");
		Value[] listOfViews = new Value[names.size()];
		for(int i = 0; i < names.size(); i++) {
			Value[] view = new Value[4];
			view[0] = new Value(names.get(i));
			view[1] = new Value(new Float(transformations.get(i).getMxx()));
			view[2] = new Value(new Float(transformations.get(i).getTx()));
			view[3] = new Value(new Float(transformations.get(i).getTy()));
			listOfViews[i] = new Value(view);
		}
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(listOfViews)
		};
		sendMessage("sendViewStatusToModel", message);
	}

	@SuppressWarnings("unchecked")
	public Vector<Vector<Object>> getAllViews(Integer diagramID) {
		try {
			Vector<Object> response = xmfRequest(handler, diagramID, "getAllViews");
			return (Vector<Vector<Object>>) (response.get(0));
		} catch (TimeOutException e) {
			e.printStackTrace();
			Vector<Object> v = new Vector<Object>();
			v.add("Emergency Tab");v.add(1.0);v.add(0.0);v.add(0.0);
			Vector<Vector<Object>> V = new Vector<>(); V.add(v);
			return V;
		}
	}
	
	public void sendViewOptions(int diagramID) {
		Vector<Value> items = new Vector<>();
		FmmlxDiagram diagram = getDiagram(diagramID);
		items.add(new Value(new Value[] {new Value("showDerivedAttributes"), 	new Value( diagram.isShowDerivedAttributes())}));
		items.add(new Value(new Value[] {new Value("showDerivedOperations"), 	new Value( diagram.isShowDerivedOperations())}));
		items.add(new Value(new Value[] {new Value("showGettersAndSetters"), 	new Value( diagram.isShowGetterAndSetter())}));
		items.add(new Value(new Value[] {new Value("showOperations"), 			new Value( diagram.isShowOperations())}));
		items.add(new Value(new Value[] {new Value("showOperationValues"), 		new Value( diagram.isShowOperationValues())}));
		items.add(new Value(new Value[] {new Value("showSlots"), 				new Value( diagram.isShowSlots())}));
		items.add(new Value(new Value[] {new Value("showMetaClassName"),		new Value( diagram.isMetaClassNameInPalette())}));
		items.add(new Value(new Value[] {new Value("showConstraints"),		    new Value( diagram.isConstraintsInDiagram())}));
		items.add(new Value(new Value[] {new Value("showConstraintReports"),    new Value( diagram.isConstraintReportsInDiagram())}));
		Value[] itemArray = new Value[items.size()];
		for(int i = 0; i < itemArray.length; i++) {
			itemArray[i] = items.get(i);
		}
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(itemArray)
		};
		sendMessage("sendViewOptions", message);
	}


	public void sendViewOptions(Integer diagramID, HashMap<String, Boolean> map) {
		Vector<Value> items = new Vector<>();
		for(String key : map.keySet()) {
			items.add(new Value(new Value[] {new Value(key), new Value(map.get(key))}));
		}
		Value[] itemArray = new Value[items.size()];
		for(int i = 0; i< itemArray.length; i++) {
			itemArray[i] = items.get(i);
		}
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(itemArray)
		};
		sendMessage("sendViewOptions", message);
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Boolean> getViewOptions(Integer diagramID) {
		try {
			Vector<Object> response = xmfRequest(handler, diagramID, "getViewOptions");
			HashMap<String, Boolean> result = new HashMap<String, Boolean>();
			Vector<Vector<Object>> list = (Vector<Vector<Object>>) response.get(0);
			for(Vector<Object> item : list) {
				result.put((String) item.get(0), (Boolean) item.get(1));
			}			
			return result;
		} catch (TimeOutException e) {
			e.printStackTrace();
			return new HashMap<String, Boolean>();
		}
	}
    
    public void runOperation(Integer diagramID, String text) throws TimeOutException {
        sendMessage("runOperation", new Value[]{
			getNoReturnExpectedMessageID(diagramID),
			new Value(text)});
    }




}
