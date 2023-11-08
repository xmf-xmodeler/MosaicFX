package tool.clients.fmmlxdiagrams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;
import tool.clients.dialogs.enquiries.FindSendersOfMessages;
import tool.clients.fmmlxdiagrams.dialogs.CodeBoxPair;
import tool.clients.workbench.WorkbenchClient;
import tool.helper.persistence.XMLInstanceStub;
import tool.logging.RequestLog;
import tool.logging.RequestLogManager;
import xos.Value;

public class FmmlxDiagramCommunicator {
	
	private static final boolean DEBUG = false; // while setting debug-modus you will receive logs, that help with error detection
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(FmmlxDiagramCommunicator.class);
	private final HashMap<Integer, Vector<Object>> results = new HashMap<>(); // old response map (to be removed)
	private final HashMap<Integer, ReturnCall<Vector<Object>>> returnMap = new HashMap<>(); // new response map
	private static final Vector<FmmlxDiagram> diagrams = new Vector<>();
	static TabPane tabPane;
	private static int nonReturningMessageId = -1;
	private static int nextMsgID() {if(nonReturningMessageId < -10000) nonReturningMessageId=-1; nonReturningMessageId-=1; return nonReturningMessageId;}
	public static Value getNoReturnExpectedMessageID(int diagramID) {return new Value(new Value[] {new Value(diagramID), new Value(nextMsgID())});}
	private HashMap<Integer, Long> timeMap = new HashMap<>();
	/*  This class is a singleton. It is created from the xmf
	 * and therefore does not follow the typical singleton pattern exactly */
	private static FmmlxDiagramCommunicator self;
	private int handle; // this is set by xmf and serves as an identifier for the communication
	
	public static boolean isDebug() {
		return DEBUG;
	}
	
	public FmmlxDiagramCommunicator(int handle) { // this is to be called by xmf once
		if(self != null) throw new IllegalStateException("FmmlxDiagramCommunicator must not be instantiated more than once.");
		this.handle = handle;
		self = this;
		for(ReturnCall<FmmlxDiagramCommunicator> request : earlyRequests) {
			request.run(self);
		}
	}
	
	public static FmmlxDiagramCommunicator getCommunicator() { // this should only be called after the singleton has been created by xmf already
		if(self != null) return self;
		// otherwise things have gone wrong and the problem needs to be investigated
		throw new IllegalStateException("FmmlxDiagramCommunicator should have been instantiated. Run initCommunicator() first");		
	}
	
	private static Vector<ReturnCall<FmmlxDiagramCommunicator>> earlyRequests = new Vector<>();
	public static void getCommunicatorWhenReady(ReturnCall<FmmlxDiagramCommunicator> onReady) { // this can be called any time, the return call will be invoked either immediately or once it's ready
		if(self != null) {
			onReady.run(self);	
		} else {
			earlyRequests.add(onReady);
		}
	}
	
	public static void initCommunicator() {
		WorkbenchClient.theClient().startFmmlxClient();
	}
	
	public static void start(TabPane tabPane) {
		FmmlxDiagramCommunicator.tabPane = tabPane;
	}

	/* Setting up new or existing diagrams, as well as closing */
	public void newDiagram(int diagramID, String diagramName, String packagePath, String file, Vector<Vector<Object>> listOfViews, Vector<Vector<Object>> listOfOptions, boolean umlMode) {
//		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			if (DEBUG) System.err.println("Create FMMLx-Diagram ("+diagramName+") ...");
			FmmlxDiagram diagram = new FmmlxDiagram(this, diagramID, diagramName, packagePath, listOfViews, listOfOptions, umlMode);
			if(file != null && file.length()>0){
				diagram.setFilePath(file);
			} else {
				diagram.setFilePath(copyFilePath(packagePath));
			}
			createStage(diagram.getView(), diagramName, packagePath, this.handle, diagram);	
			diagrams.add(diagram);
//			l.countDown();
//			diagram.getDiagramViewToolBarModel().receiveDisplayPropertiesFromXMF();
			/*
//			If you create a new diagram the backend has no ToolBarProperties. If you would save it this way the properties can't be exported to XML.
//			To avoid this we will send the properties right at the initialization of the model to the backend.
//			*/
//			diagram.getDiagramViewToolBarModel().sendDisplayPropertiesToXMF();
		});
//		try {
//			l.await();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

//	private transient Integer _newDiagramID = null;
	private HashMap<String, ReturnCall<Integer>> newlyCreatedDiagrams = new HashMap<>();


    public static enum DiagramType {ClassDiagram, ModelBrowser};
	
    public void createFmmlxModelBrowser(String packagePath, 
			String diagramName, 
			String file,  
			ReturnCall<Integer> onDiagramCreated){
    	createFmmlxDiagram(packagePath, diagramName, file,DiagramType.ModelBrowser, onDiagramCreated);
	}
    
    public void createFmmlxClassDiagram(String packagePath, 
			String diagramName, 
			String file,   
			ReturnCall<Integer> onDiagramCreated){
    	createFmmlxDiagram(packagePath, diagramName, file,DiagramType.ClassDiagram, onDiagramCreated);
	}
    
    public void createFmmlxDiagram(String packagePath, 
			String diagramName, 
			String file, 
			DiagramType type,  
			ReturnCall<Integer> onDiagramCreated){
		createDiagram(packagePath, diagramName, file, type, false, onDiagramCreated);
	}
    
	public void createUMLDiagram(String packagePath, 
			String diagramName, 
			String file, 
			DiagramType type,  
			ReturnCall<Integer> onDiagramCreated){
		createDiagram(packagePath, diagramName, file, type, true, onDiagramCreated);
	}
   
    public void createDiagram(
			String packagePath, 
			String diagramName, 
			String file, 
			DiagramType type, 
			boolean umlMode, 
			ReturnCall<Integer> onDiagramCreated) {
		//Creates a diagram which is not displayed yet.
		Value[] message = new Value[]{
				new Value(packagePath),
				new Value(diagramName),
				new Value(file),
				new Value(type.toString()),
				new Value(umlMode)
		};
		newlyCreatedDiagrams.put(packagePath+":::"+diagramName, onDiagramCreated);
		Task<Void> task = new Task<Void>() { protected Void call() { sendMessage("createDiagramFromJava", message); return null; }};
		new Thread(task).start();
	}
    
    public int createDiagramAsync(String packagePath, String diagramName, DiagramType type) {
		Value[] message = new Value[]{
				new Value(packagePath),
				new Value(diagramName),
				// 2029-08-29 TS i think this is not used
				new Value(""),
				new Value(type.toString()),
				new Value(false)
		};
		//TODO TS why can i not pass variables here?
		List<Integer> list = new ArrayList<Integer>();
		ReturnCall<Vector<Object>> onDiagramCreated = (fmmlxDiagramId) -> {
			list.add((int) fmmlxDiagramId.get(0));
		};
		xmfRequestAsync(handle, -2, "createDiagramFromJavaUsingAsynchCall", onDiagramCreated, message);
		waitForNextRequestReturn();
		return list.get(0);
	}
	
	public void notifyDiagramCreated(String packagePath, String diagramName, Integer newID) {
		String key = packagePath+":::"+diagramName;
		if(newlyCreatedDiagrams.containsKey(key)) {
			newlyCreatedDiagrams.remove(key).run(newID);
		} else {
			System.err.println("New diagram notification for "+key+" does not match any request.");
		}
	}
	
	// this is called from the command center
	public void openDiagram(String packagePath, String diagramName) {
		Value[] message = new Value[]{
				new Value(packagePath),
				new Value(diagramName)
		};
		sendMessage("showDiagram", message);
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
	
	//////////// INCOMING MESSAGES ////////////
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
			// First the message is unwrapped.
			java.util.Vector<Object> msgAsVec = (java.util.Vector<Object>) msgAsObj;
			java.util.Vector<Object> ids = (java.util.Vector<Object>) msgAsVec.get(0);
			// the diagramID is found in .0.0
			int diagramID = (Integer) (ids.get(0));
			// when does this happen?
			if(diagramID == -1) return; // Ignore completely for now. Message not for any open diagram
			// in .0.1 the request id is found.
			int requestID = (Integer) (ids.get(1));
			if (DEBUG) System.err.println(": Receiving request " + requestID);
			// The header has been read now, so it can be discarded
			msgAsVec.remove(0);
			if (requestID <= -1) {
				// if the requestID is negative (should be always -1)
				// then it's not a message anyone has been waiting for
				// actually for now, it is assumed that it is 
				// some kind of a notification in the for of a 
				// String to be displayed
				if (DEBUG) System.err.println("v.get(0)= " + msgAsVec.get(0));
				try{
					if (DEBUG) System.err.println("\tmessage" + requestID + " returned after " + (System.currentTimeMillis() - timeMap.get(requestID)) + "ms");
				} catch(Exception e) {System.err.println("message" + requestID + " returned anyway");}
				java.util.Vector<Object> err = (java.util.Vector<Object>) msgAsVec.get(0);
				if (err != null && err.size() > 0 && err.get(0) != null ) {
					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.ERROR, err.get(0) + "", ButtonType.CLOSE);
						// alert.showAndWait(); NOPE!!!
						// Leave this comment here as a warning
						alert.show();
					});
				}
			} else {
				// if the requestID is not negative, then there should be something waiting 
				// for the response
				// HISTORICALLY that was a Thread on pause checking regularly 
				// for the presence of the result. As such, the response is put into a 
				// response list and the work is done here. (still in use yet)
				// NOW (most) requests 
				//  1) send their question,
				//  2) put their action on answer (i.e. a ReturnCall) into a list and 
				//  3) terminate.
				// Therefore here it is checked whether such a ReturnCall is present for the 
				// given id. If so, the ReturnCall is invoked with the answer as the single argument
				// and removed from the queue
				// Otherwise it is assumed the old method is used and put into the response list
				//
				// If a responds fails to appear or if a response does not match any request
				// this should run through here unharmed, but ultimately either list will 
				// accumulate failed communications
				if (returnMap.containsKey(requestID)) {
					Runnable r = () -> returnMap.remove(requestID).run(msgAsVec);
					Thread t = new Thread(r);
					t.setName("RunRequest" + requestID);
					t.start();
					RequestLogManager.getInstance().setLogReturned(requestID, msgAsVec);					
					if (DEBUG) {
						System.err.println("Start Thread with name: " + t.getName());						
					}					
				} else {
					System.err.println("Old queue still in use:" + requestID + " -> " + msgAsVec);
					results.put(requestID, msgAsVec);
				}
			}
		} else {
			if (DEBUG) System.err.println("o: " + msgAsObj + "(" + msgAsObj.getClass() + ")");
		}
	}
			
	//////////// OUTGOING MESSAGES ////////////
	private int currentRequestID = 0;
	
	public int getcurrentRequestID() {
		return currentRequestID;
	}
	
	public void setNewRequestID() {
		// the idCounter could in theory cycle through all int. But as -1 is reserved the cycle has to be cut short.
		if(currentRequestID > 10000) currentRequestID = 0;
		currentRequestID++;
	}
	
	/**
	 * This operation wraps a request, adds an identifier and waits for the response
	 *
	 * @param targetHandle an int identifying the handler
	 * @param xmfFunctionName      the name of the operation in xmf (FmmlxDiagramClient)
	 * @param parameterList         the arguments of that operation
	 * @return
	 */
	private Vector<Object> xmfRequest(int targetHandle, int diagramID, String xmfFunctionName, Value... parameterList) throws TimeOutException {
		//create new Value-Array with original length + 1
		Value[] newParameterList = new Value[parameterList.length + 1];
		setNewRequestID();
		if (DEBUG) System.err.println(": Sending synchron request " + xmfFunctionName + "(" + currentRequestID + ") handle " + targetHandle);
		RequestLog log = new  RequestLog(currentRequestID, true, System.currentTimeMillis(), xmfFunctionName, targetHandle, newParameterList);
		RequestLogManager.getInstance().addLog(log);
		logger.debug("Send synchron request {}", log);
		//copy all elements starting by parameterList[0] to new parameterList[1] 
		System.arraycopy(parameterList, 0, newParameterList, 1, parameterList.length);
		//add at position [0] of new parameterList a combined value of diagramID and requestID
		newParameterList[0] = new Value(new Value[] {new Value(diagramID), new Value(currentRequestID)});
		boolean waiting = true;
		WorkbenchClient.theClient().send(targetHandle, xmfFunctionName, newParameterList);
		int attempts = 0;
		int sleep = 2;
		while (waiting && sleep < 10000) {
			try {
				Thread.sleep(sleep);
				sleep = (int) (sleep * 1.5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (results.containsKey(currentRequestID)) {
				waiting = false;
			}
		}

		if (waiting)
			throw new TimeOutException(xmfFunctionName + parameterList);
		Vector<Object> functionReturnVector = results.remove(currentRequestID);
		RequestLogManager.getInstance().setLogReturned(currentRequestID, functionReturnVector);
		return functionReturnVector;
	}
	
	private void xmfRequestAsync(int targetHandle, int diagramID, String message, ReturnCall<Vector<Object>> returnCall, Value... args) {
		setNewRequestID();
		Value[] args2 = new Value[args.length + 1];
		if (DEBUG) System.err.println(": Sending request " + message + "(" + currentRequestID + ") handle" + targetHandle);
		System.arraycopy(args, 0, args2, 1, args.length);
		args2[0] = new Value(new Value[] {new Value(diagramID), new Value(currentRequestID)});
		returnMap.put(currentRequestID, returnCall);
		RequestLog log = new RequestLog(currentRequestID, false, System.currentTimeMillis(), message, targetHandle, args2);
		RequestLogManager.getInstance().addLog(log);
		logger.debug("Start asynchron request {}", log);
		WorkbenchClient.theClient().send(targetHandle, message, args2);
	}
	
	void sendMessage(String command, Value[] message) {
		if (DEBUG) {
			try {
				int n = message[0].values[1].intValue;
				System.err.println(": Sending command" + n + ": " + command);
				timeMap.put(n, System.currentTimeMillis());
			} catch (Exception e) {
				
			}
		}
		WorkbenchClient.theClient().send(handle, command, message);
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
				String type = "FMMLX";
//				Boolean isSingleton = false;
				try{ type = (String) responseObjectList.get(0); } catch(Exception e) {System.err.println("Warning: Pull new XMF version.");}
				Integer maxLevel = (Integer) responseObjectList.get(3);
				if(maxLevel == -1) maxLevel = null;
				FmmlxObject object = new FmmlxObject(
						(String)  responseObjectList.get(1), // name
						(Integer) responseObjectList.get(2), // level-min
						maxLevel, // level-max						
						(String)  responseObjectList.get(10),// ownPath
						(String)  responseObjectList.get(11),// ofPath
						parentListS,                         // parentsPath
						(Boolean) responseObjectList.get(5), // isAbstract
						(Boolean) responseObjectList.get(4), // isSingleton
						(Integer) responseObjectList.get(6), // x-Position
						(Integer) responseObjectList.get(7), // y-Position 
						(Boolean) responseObjectList.get(8), // hidden
						diagram);
				object.type = type;
				result.add(object);
			}
			objectsReceivedReturn.run(result);
		};
		
		xmfRequestAsync(handle, diagram.getID(), "getAllObjects", localReturn);
		
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
		xmfRequestAsync(handle, diagram.getID(), "getAllInheritanceEdges", localReturn);		
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
		xmfRequestAsync(handle, diagram.getID(), "getAllDelegationEdges", localReturn);
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
		xmfRequestAsync(handle, diagram.getID(), "getAllRoleFillerEdges", localReturn);
		
	}

    @SuppressWarnings("unchecked")
	public void getAllEdgePositions(Integer diagramID, ReturnCall<HashMap<String, HashMap<String, Object>>> onAllEdgePositionsReceived) {
		HashMap<String, HashMap<String, Object>> result = new HashMap<>();
		ReturnCall<Vector<Object>> localReturn = response -> {
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
			onAllEdgePositionsReceived.run(result);
		};        
        
		xmfRequestAsync(handle, -2, "getAllEdgePositions", localReturn, new Value(diagramID));
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
		
		xmfRequestAsync(handle, diagram.getID(), "getAllAssociations", returnCall);
	
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
		
		xmfRequestAsync(handle, diagram.getID(), "getAllLinks", returnCall);

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
//						int minLevel = (Integer) attInfo.get(2);
//						int maxLevel = (Integer) attInfo.get(3);
						FmmlxAttribute object = new FmmlxAttribute(
								(String) attInfo.get(0),
								(Integer) attInfo.get(2),
								(String) attInfo.get(1),
								(String) attInfo.get(7),
								Multiplicity.parseMultiplicity((Vector<Object>) attInfo.get(4)));
						resultOwn.add(object);
					}
					for (Object a : otherAttList) {
						Vector<Object> attInfo = (Vector<Object>) a;
						FmmlxAttribute object = new FmmlxAttribute(
								(String) attInfo.get(0),
								(Integer) attInfo.get(2),
								(String) attInfo.get(1),
								(String) attInfo.get(7),
								Multiplicity.parseMultiplicity((Vector<Object>) attInfo.get(4)));
						resultOther.add(object);
					}
					
					o.setAttributes(resultOwn, resultOther);
				}
			}
			
			attributesReceivedReturn.run(objects);
		};
		xmfRequestAsync(handle, diagram.getID(), "getAllAttributes", returnCall);
		
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
						if(opInfo==null) {
							System.err.println("NULL"); 
						} else {
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
								(Integer) opInfo.get(3), // minLevel
								(String) opInfo.get(5), // type
								(String) opInfo.get(6), // body
								(String) opInfo.get(7), // owner
								null, // multiplicity
								(Boolean) opInfo.get(9), // isMonitored
								(Boolean) opInfo.get(10) // delToClass
							);
						result.add(op);
						}
					}
					
					obj.setOperations(result);
				}
			  }
			}
			operationsReceivedReturn.run(objects);
		};
		xmfRequestAsync(handle, diagram.getID(), "getAllOperations", returnCall);
		
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
		xmfRequestAsync(handle, diagram.getID(), "getAllConstraints", returnCall);
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
        xmfRequestAsync(handle, abstractPackageViewer.getID(), "getAllIssues", returnCall);
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
	    xmfRequestAsync(handle, fmmlxDiagram.getID(), "getAllAuxTypes", returnCall);
    }
    
    @SuppressWarnings("unchecked")
    public void fetchAllSlots(AbstractPackageViewer diagram, HashMap<FmmlxObject, Vector<String>> slotNames, ReturnCall<?> slotsReceivedReturn) {
    	java.util.Set<FmmlxObject> objects = slotNames.keySet();
    	Value[] objectSlotList = new Value[slotNames.size()];
    	int count = 0;
    	for(FmmlxObject o : slotNames.keySet()) {
    		//System.err.println("getSlots for " + o.name + ": " + slotNames.get(o));
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
    	xmfRequestAsync(handle, diagram.getID(), "getAllSlots", returnCall, new Value(objectSlotList));
    }
    

	@SuppressWarnings("unchecked")
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
		xmfRequestAsync(handle, diagram.getID(), "checkSyntax", returnCall, new Value(operationBody));
	}
	
	@SuppressWarnings("unchecked")
	public void evalString(AbstractPackageViewer diagram, String text, ReturnCall<Vector<Object>> result) {
		ReturnCall<Vector<Object>> returnCall = syntaxCheckResponse -> {
			Object response = syntaxCheckResponse.get(0);
			if(response == null) {
				result.run(null);
			} else {
				Vector<Object> responseV = (Vector<Object>) response;
				result.run(responseV);
			}
		};
		xmfRequestAsync(handle, diagram.getID(), "evalString", returnCall, new Value(text));
	}

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
    	xmfRequestAsync(handle, diagram.getID(), "getAllOperationValues", returnCall, new Value(objectOpValList));
    }

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
		xmfRequestAsync(handle, diagram.getID(), "getAllEnums", returnCall);
	}
	
	////////////////////////////////////////////////
	/// Operations storing graphical info to xmf ///
	////////////////////////////////////////////////
	//TODO extract as XMLCommunicator
	@Deprecated //use function below
	public void sendObjectInformation(int diagramID, String objectPath, int x, int y, boolean hidden) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(objectPath),
				new Value(x),
				new Value(y),
				new Value(hidden)};
		sendMessage("sendNewPosition", message);
	}
	
	public void sendObjectInformation(int diagramID, XMLInstanceStub stub) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(stub.getRef()),
				new Value(stub.getxCoordinate()),
				new Value(stub.getyCoordinate()),
				new Value(stub.isHidden())};
		sendMessage("sendNewPosition", message);
	}

	public void sendCurrentEdgePositions(int diagramID, Edge<?> edge) {
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
//	@Deprecated
//	public void addMetaClass(int diagramID, String name, int level, Vector<String> parents, boolean isAbstract, int x, int y, boolean hidden) {
//		addMetaClass(diagramID, name, new Level(level), parents, isAbstract, x, y, hidden);
//	}
	public void addMetaClass(int diagramID, String name, Level level, Vector<String> parents, boolean isAbstract, boolean isSingleton, int x, int y, boolean hidden) {
		Value[] parentsArray = createValueArray(parents);

		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(name),
				new Value(level.getMinLevel()),
				new Value(level.getMaxLevel()),
				new Value(parentsArray),
				new Value(isAbstract),
				new Value(isSingleton),
				new Value(x), new Value(y), new Value(hidden)};
		sendMessage("addMetaClass", message);
	}
	
//	public void addMetaClassAsync(int diagramID, String name, Level level, Vector<String> parents, boolean isAbstract, int x, int y, boolean hidden) {
//		Value[] parentsArray = createValueArray(parents);
//		Value[] message = new Value[]{
//				new Value(name),
//				new Value(level.getMinLevel()),
//				new Value(level.getMaxLevel()),
//				new Value(parentsArray),
//				new Value(isAbstract),
//				new Value(x), new Value(y), new Value(hidden)};
//		xmfRequestAsync(handle, diagramID, "addMetaClass", (emptyReturn) -> {}, message);
//	}
	
	public void addNewInstance(int diagramID, 
			String className, 
			String name, 
			Level level, 
			Vector<String> parents, 
			boolean isAbstract, 
			boolean isSingleton, 
			int x, int y, boolean hidden) {
		Value[] parentsArray = createValueArray(parents);

		Value[] message = new Value[]{getNoReturnExpectedMessageID(diagramID), new Value(className), new Value(name), new Value(level.getMinLevel()), new Value(level.getMaxLevel()),
				new Value(parentsArray), new Value(isAbstract), new Value(isSingleton), new Value(x), new Value(y), new Value(hidden), new Value(new Value[] {})};
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
	
	public void addGeneratedInstance(
			AbstractPackageViewer diagram,
			FmmlxObject theClass,
			int level,
			String namePrefix, 
			Vector<Vector<String>> slotValues,
			Vector<String> mandatoryConstraints, 
			ReturnCall<Vector> wizardReturn) {

		int i = 0;
		Value[] slotList = new Value[slotValues.size()];
		for(Vector<String> slotItem : slotValues) {
			Value name = new Value(slotItem.get(0));
			Value value = new Value(slotItem.get(1));
			Value pair = new Value(new Value[] {name, value});
			slotList[i] = pair;
			i++;
		}
		
		i = 0;
		Value[] constraintList = new Value[mandatoryConstraints.size()];
		for(String c : mandatoryConstraints) {
			constraintList[i] = new Value(c);
			i++;
		}
		
		ReturnCall<Vector<Object>> localReturn = (response) -> {
			System.err.println("Instance Generator response from XMF: "+ response);
			wizardReturn.run(response);
		};
		
		xmfRequestAsync(handle, diagram.getID(), "addGeneratedInstance", localReturn, 
			new Value(theClass.name),
			new Value(level),
			new Value(namePrefix),
			new Value(slotList),
			new Value(constraintList));		
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

	public void removeAssociation(int diagramID, FmmlxAssociation assoc) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(assoc.sourceNode.ownPath),
				new Value(assoc.getAccessNameStartToEnd())};
		sendMessage("removeAssociation", message);
	}

	public void setAssociationEndVisibility(int diagramID, FmmlxAssociation assoc, boolean targetEnd, boolean newVisbility) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(assoc.sourceNode.ownPath),
				new Value(assoc.getAccessNameStartToEnd()),
				new Value(targetEnd),
				new Value(newVisbility)};
		sendMessage("setAssociationEndVisibility", message);
	}

	public void addAttribute(int diagramID, String className, String name, Level level, String type, Multiplicity multi, boolean isIntrinsic, boolean isIncomplete, boolean isOptional) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(className),
				new Value(name),
				new Value(level.getMinLevel()),
				new Value(level.getMaxLevel()),
				new Value(type),
				new Value(multi.toValue()),
				new Value(isIntrinsic),
				new Value(isIncomplete),
				new Value(isOptional)};
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

	public void changeAttributeLevel(int diagramID, String objectName, String attName, Level oldLevel, Level newLevel) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(objectName),
				new Value(attName),
				new Value(oldLevel.getMinLevel()),
				new Value(newLevel.getMinLevel())};
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

    public void addOperation(int diagramID, String objectName, int level, String body) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(objectName),
                new Value(level),
                new Value(body)
        };
        sendMessage("addOperation", message);
    }

    public void changeOperationName(int diagramID, String objectName, String oldName, String newName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(objectName),
                new Value(oldName),
                new Value(newName)};
        sendMessage("changeOperationName", message);
    }

    public void changeOperationLevel(int diagramID, String objectName, String opName, Level oldLevel, Level newLevel) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(objectName),
                new Value(opName),
                new Value(oldLevel.getMinLevel()),
                new Value(newLevel.getMinLevel())};
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

    public void changeClassLevel(int diagramID, String objectPath, Level newLevel) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(objectPath),
                new Value(newLevel.getMinLevel()),
                new Value(newLevel.getMaxLevel())};
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
        String fwName, String associationType,
        Multiplicity multTargetToSource, Multiplicity multSourceToTarget,
        Integer instLevelSourceMin, Integer instLevelSourceMax, 
        Integer instLevelTargetMin, Integer instLevelTargetMax, 
        boolean sourceVisible, boolean targetVisible,
        boolean isSymmetric, boolean isTransitive,
        String sourceGetterName,
        String sourceSetterName,
        String targetGetterName, 
        String targetSetterName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(classSourceName), new Value(classTargetName),
                new Value(accessSourceFromTargetName), new Value(accessTargetFromSourceName),
                new Value(fwName), 
                associationType == null ? new Value("Associations::DefaultAssociation") : new Value(associationType),
                new Value(multTargetToSource.toValue()),
                new Value(multSourceToTarget.toValue()), // multiplicity,
                new Value(instLevelSourceMin), new Value(instLevelSourceMax), 
                new Value(instLevelTargetMin), new Value(instLevelTargetMax),
                new Value(sourceVisible), new Value(targetVisible), new Value(isSymmetric), new Value(isTransitive),
                (sourceGetterName==null?new Value(-1):new Value(sourceGetterName)), 
                (sourceSetterName==null?new Value(-1):new Value(sourceSetterName)), 
                (targetGetterName==null?new Value(-1):new Value(targetGetterName)), 
                (targetSetterName==null?new Value(-1):new Value(targetSetterName))};
        sendMessage("addAssociation", message);
    }

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
    
    public void addLink(int diagramID, String object1Name, String object2Name, String role2AccessName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(object1Name),
                new Value(object2Name),
                new Value(role2AccessName)};
        sendMessage("addLink", message);
    }

    public void removeAssociationInstance(int diagramID, String role2Name, String sourceName, String targetName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(role2Name),
                new Value(sourceName),
                new Value(targetName)
        };
        sendMessage("removeLink", message);
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

    public void changeAssociationForwardName(int diagramID, FmmlxAssociation assoc, String newFwName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
				new Value(assoc.sourceNode.ownPath),
				new Value(assoc.getAccessNameStartToEnd()),
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
    
    public void changeAssociationStart2EndLevel(int diagramID, FmmlxAssociation assoc, Integer newLevel) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
				new Value(assoc.sourceNode.ownPath),
				new Value(assoc.getAccessNameStartToEnd()),
                new Value(newLevel)};
        sendMessage("changeAssociationStart2EndLevel", message);
    }

    public void changeAssociationEnd2StartLevel(int diagramID, FmmlxAssociation assoc, Integer newLevel) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
				new Value(assoc.sourceNode.ownPath),
				new Value(assoc.getAccessNameStartToEnd()),
                new Value(newLevel)};
        sendMessage("changeAssociationEnd2StartLevel", message);
    }

    public void changeAssociationStart2EndAccessName(int diagramID, FmmlxAssociation assoc, String newName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
				new Value(assoc.sourceNode.ownPath),
				new Value(assoc.getAccessNameStartToEnd()),
                new Value(newName)};
        sendMessage("changeAssociationStart2EndAccessName", message);
    }

    public void changeAssociationEnd2StartAccessName(int diagramID, FmmlxAssociation assoc, String newName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
				new Value(assoc.sourceNode.ownPath),
				new Value(assoc.getAccessNameStartToEnd()),
                new Value(newName)};
        sendMessage("changeAssociationEnd2StartAccessName", message);
    }

    public void changeAssociationStart2EndMultiplicity(int diagramID, FmmlxAssociation assoc, Multiplicity newMultiplicity) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
				new Value(assoc.sourceNode.ownPath),
				new Value(assoc.getAccessNameStartToEnd()),
                new Value(newMultiplicity.toValue())};
        sendMessage("changeAssociationStart2EndMultiplicity", message);
    }

    public void changeAssociationEnd2StartMultiplicity(int diagramID, FmmlxAssociation assoc, Multiplicity newMultiplicity) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
				new Value(assoc.sourceNode.ownPath),
				new Value(assoc.getAccessNameStartToEnd()),
                new Value(newMultiplicity.toValue())};
        sendMessage("changeAssociationEnd2StartMultiplicity", message);
    }

    public void setClassAbstract(int diagramID, String className, boolean isAbstract) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(className),
                new Value(isAbstract)};
        sendMessage("setClassAbstract", message);
    }
    
    public void setClassSingleton(int diagramID, String className, boolean isSingleton) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(className),
                new Value(isSingleton)};
        sendMessage("setClassSingleton", message);
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

    @Deprecated
    public void addEnumeration(int diagramID, String newEnumName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(newEnumName)};
        sendMessage("addEnumeration", message);
    }
    
    public void addEnumerationAsync(int diagramID, String newEnumName) {
        Value[] message = new Value[]{
                new Value(newEnumName)};
        xmfRequestAsync(handle, diagramID, "addEnumeration", (emptyCall) -> {}, message);
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

    @Deprecated
    public void addEnumerationValue(int diagramID, String enumName, String newEnumValueName) {
        Value[] message = new Value[]{
                getNoReturnExpectedMessageID(diagramID),
                new Value(enumName),
                new Value(newEnumValueName)};
        sendMessage("addEnumerationValue", message);
    }
    
    public void addEnumerationValueAsync(int diagramID, String enumName, String newEnumValueName) {
        Value[] message = new Value[]{
                new Value(enumName),
                new Value(newEnumValueName)};
        xmfRequestAsync(handle, diagramID, "addEnumerationValue", (emptyCall) -> {}, message);
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
    
    @Deprecated
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
    
    public void addConstraintAsync(int diagramID, String path, String constName, Integer instLevel, String body, String reason) {
		Value[] message = new Value[]{
                new Value(path),
                new Value(constName),
                new Value(instLevel),
                new Value(body),
                new Value(reason)
		};
		 xmfRequestAsync(handle, diagramID, "addConstraint", (emptyCall) -> {}, message);;
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

    public void createProject(String projectName, String file) {
        Value[] message = new Value[]{
        		getNoReturnExpectedMessageID(-1),
        		new Value(projectName),
                new Value(file)
        };
        sendMessage("loadProjectFromXml", message);
    }
    
    public void openPackageBrowser() {
        WorkbenchClient.theClient().send(handle, "openPackageBrowser()");
    }

    public Vector<String> evalList(AbstractPackageViewer diagram, String text) throws TimeOutException {
        Vector<Object> response = xmfRequest(handle, diagram.getID(), "evalList", new Value(text));
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

    @Deprecated // use async below
    @SuppressWarnings("unchecked")
    public ModelActionsList getDiagramData(Integer diagramID) throws TimeOutException {
		Vector<Object> response = xmfRequest(handle, diagramID, "getDiagramData");
        Vector<Object> responseContent = (Vector<Object>) (response.get(0));
		return new ModelActionsList(responseContent);
    }
    
    @SuppressWarnings("unchecked")
    public void getModelData(Integer diagramID, ReturnCall<ModelActionsList> onModelDataReceived ){
    	ReturnCall<Vector<Object>> localReturn = (response) -> {
    		Vector<Object> responseContent = (Vector<Object>) (response.get(0));
    		onModelDataReceived.run(new ModelActionsList(responseContent));
    	};
    	xmfRequestAsync(handle, diagramID, "getDiagramData", localReturn);
    }
	
	public void findClasses(String className, String level, String attName, ReturnCall<Object> onResultReceived) {
		ReturnCall<Vector<Object>> localReturn = (response) -> {
//			System.err.println("findClasses response from XMF: "+ response);
//			Boolean success = (Boolean) response.get(0);
			onResultReceived.run("onResultReceived: " + response);
		};
		
		xmfRequestAsync(handle, -2, "findClasses", localReturn, 
				 new Value(className),
				 new Value(level),
				 new Value(attName));
	}

    @SuppressWarnings("unchecked")
    public HashMap<String, String> findImplementation(AbstractPackageViewer diagram, Vector<String> names, String model, Integer arity, String returnType) throws TimeOutException {
        Vector<Object> response = xmfRequest(handle, diagram.getID(), "findOperationImplementation", new Value(createValueArray(names)), // opNames
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
		Vector<Object> response = xmfRequest(handle, diagram.getID(), "findAllOperations");
		
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
		Vector<Object> response = xmfRequest(handle, diagram.getID(), "findOperationUsage", new Value(name), // opNames
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
			response = xmfRequest(handle, diagram.getID(), "findOperationUsage", new Value(name),
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
		
	private void createStage(javafx.scene.Node node, String name, String packagePath, int id, final FmmlxDiagram diagram) {
		Stage stage = new Stage();
		stage.setMaximized(true);
		BorderPane border = new BorderPane();
		border.setCenter(node);
		Scene scene = new Scene(border, 1000, 605);
		stage.setScene(scene);
		String title = packagePath.substring(6) + "::" + name;
		stage.setTitle(title);
		
		//LM, 17.11.2021, resize canvas on maximize
		// The update can only be achieved in a parallel thread as the actual size of the stage is
		// not updated at the same time as the attribute "maximized".
		stage.maximizedProperty().addListener( (observer, x, y) -> {
			Thread newThread = new Thread(diagram::redraw);
			newThread.start();
		});
		
		stage.show();
		stage.setOnCloseRequest((e) -> closeScene(stage, e, id, name, node, diagram));
	}

	private void closeScene(Stage stage, Event wevent, int id, String name, javafx.scene.Node node, FmmlxDiagram diagram) {
		close(diagram, true);
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
	
	public static class DiagramInfo {
		public final Integer id;
		public final String diagramName;

		public DiagramInfo(Integer id, String diagramName) {
			super();
			this.id = id;
			this.diagramName = diagramName;
		}

		public Integer getId() {
			return id;
		}

		public String getDiagramName() {
			return diagramName;
		}
	}
	
	@Deprecated // use getAllDiagramInfos
	@SuppressWarnings("unchecked")
	public Vector<DiagramInfo> getAllDiagramIDs(String packagePath) {
		Vector<DiagramInfo> result = new Vector<>();
		try {
			Vector<Object> response = xmfRequest(handle, -2, "getAllDiagrams", new Value(packagePath));
			Vector<Object> responseContent = (Vector<Object>) (response.get(0));
			
			for(Object e : responseContent) {
				Vector<Object> idAndLabel = (Vector<Object>) e;
				Integer id = (Integer) (idAndLabel.get(0));
				String diagramName = (String) (idAndLabel.get(1));
				result.add(new DiagramInfo(id, diagramName));
			}
			return result;
		} catch (TimeOutException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void getAllDiagramInfos(String packagePath, ReturnCall<Vector<DiagramInfo>> onDiagramInfosReceived) {
		ReturnCall<Vector<Object>> returnCall = response -> {
			Vector<DiagramInfo> result = new Vector<>();
//			Vector<Object> response = 
			Vector<Object> responseContent = (Vector<Object>) (response.get(0));
			
			for(Object e : responseContent) {
				Vector<Object> idAndLabel = (Vector<Object>) e;
				Integer id = (Integer) (idAndLabel.get(0));
				String diagramName = (String) (idAndLabel.get(1));
				result.add(new DiagramInfo(id, diagramName));
			}
			onDiagramInfosReceived.run(result);
		};
		xmfRequestAsync(handle, -2, "getAllDiagrams", returnCall, new Value(packagePath));
	}

	@Deprecated // use async below
	@SuppressWarnings("unchecked")

	public HashMap<String, HashMap<String, Object>> getAllObjectPositions(int diagramID) {
		HashMap<String, HashMap<String, Object>> result = new HashMap<>();
		try {
			Vector<Object> response = xmfRequest(handle, -2, "getAllObjectPositions", new Value(diagramID));
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
	public void getObjectsInformation(int diagramID, ReturnCall<HashMap<String, HashMap<String, Object>>> onAllObjectPositionsReceived) {
		ReturnCall<Vector<Object>> returnCall = response -> {
			HashMap<String, HashMap<String, Object>> objectPositions = new HashMap<>();
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
				objectPositions.put(key, objectMap);
			}
			onAllObjectPositionsReceived.run(objectPositions);
		};
		xmfRequestAsync(handle, -2, "getAllObjectPositions", returnCall, new Value(diagramID));
	}

	@Deprecated // use async below
    @SuppressWarnings("unchecked")
	public HashMap<String, HashMap<String, Object>> getAllLabelPositions(int id) {
		HashMap<String, HashMap<String, Object>> result = new HashMap<>();
		Vector<Object> response;
		try {
			response = xmfRequest(handle, -2, "getAllLabelPositions", new Value(id));
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
	public void getAllLabelPositions(int id, ReturnCall<HashMap<String, HashMap<String, Object>>> onAllLabelPositionsReceived) {
		ReturnCall<Vector<Object>> returnCall = response -> {
			HashMap<String, HashMap<String, Object>> result = new HashMap<>();
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
			onAllLabelPositionsReceived.run(result);
		};
		xmfRequestAsync(handle, -2, "getAllLabelPositions", returnCall, new Value(id));
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
		System.err.println("Diagram " + id + "not found.");
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
	
	public void undo(int diagramID) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID)
		};
		sendMessage("undo", message);
	}
	
	public void redo(int diagramID) {
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID)
		};
		sendMessage("redo", message);
	}

	@Deprecated // use function below
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
	
	public void sendViewStatus(Integer diagramID, SortedMap<String, Affine> views) {
		Value[] listOfViews = new Value[views.size()];
		int size = views.size();
		for(int i = 0; i < size; i++) {
			Value[] view = new Value[4];
			//Add view name
			view[0] = new Value(views.firstKey());
			
			Affine affine = views.get(views.firstKey());
			view[1] = new Value((float) affine.getMxx());	
			view[2] = new Value((float) affine.getTx());
			view[3] = new Value((float) affine.getTy());
			listOfViews[i] = new Value(view);
			views.remove(views.firstKey());
		}
		Value[] message = new Value[]{
				getNoReturnExpectedMessageID(diagramID),
				new Value(listOfViews)
		};
		sendMessage("sendViewStatusToModel", message);
	}
	
	

	@Deprecated // use asynch below
	@SuppressWarnings("unchecked")
	public Vector<Vector<Object>> getAllViews(Integer diagramID) {
		try {
			Vector<Object> response = xmfRequest(handle, diagramID, "getAllViews");
			return (Vector<Vector<Object>>) (response.get(0));
		} catch (TimeOutException e) {
			e.printStackTrace();
			Vector<Object> v = new Vector<Object>();
			v.add("Emergency Tab");v.add(1.0);v.add(0.0);v.add(0.0);
			Vector<Vector<Object>> V = new Vector<>(); V.add(v);
			return V;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void getAllViews(Integer diagramID, ReturnCall<Vector<Vector<Object>>> onAllViewsReceived) {
		ReturnCall<Vector<Object>> localReturn = (response) -> {
			onAllViewsReceived.run((Vector<Vector<Object>>) (response.get(0)));
		};
		xmfRequestAsync(handle, diagramID, "getAllViews", localReturn);
	}
	
	public void sendDiagramDisplayOptions(Integer diagramID, HashMap<String, Boolean> map) {
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

	@Deprecated // use asynch below
	@SuppressWarnings("unchecked")
		public HashMap<String, Boolean> getDiagramDisplayPropertiesLegacy(Integer diagramID) {
			try {
				Vector<Object> response = xmfRequest(handle, diagramID, "getViewOptions");
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
		
	public HashMap<DiagramDisplayProperty, Boolean> getDiagramDisplayPropertiesSynchronous(Integer diagramID) {
		HashMap<DiagramDisplayProperty, Boolean> xmfPropertyValues = new HashMap<DiagramDisplayProperty, Boolean>();
		ReturnCall<Vector<Object>> onValuesReturned = (valuesVector) -> {
			@SuppressWarnings("unchecked")
			Vector<Vector<Object>> list = (Vector<Vector<Object>>) valuesVector.get(0);
			for(Vector<Object> item : list) {
				DiagramDisplayProperty property = null;
				try {
					property = DiagramDisplayProperty.valueOf(((String) item.get(0)).toUpperCase());										
				} catch (Exception e) {
					System.err.println("A name of a DiagramDisplayProperty could not be mapped from XML to Java");
					e.printStackTrace();
				}
				boolean propertyValue = (boolean)item.get(1);
				xmfPropertyValues.put(property, propertyValue);
			}
		};
		xmfRequestAsync(handle, diagramID, "getViewOptions", onValuesReturned);
		waitForNextRequestReturn();
		return xmfPropertyValues;
	}
	
	// TODO TS rebuild this should be called before the next request, so it is ensured, that the requires can not be faster then the execution of the wait method
	public void waitForRequestReturnByNumber(int requestID) throws InterruptedException {
		if (DEBUG) {
			System.err.println("Try to wait for request " + requestID);
		}
		logger.debug("Try to wait for request " + requestID);
		long requestTime = System.currentTimeMillis();
		while (!RequestLogManager.getInstance().getLog(requestID).isReturned()) {
			if (requestTime + 2500 < System.currentTimeMillis()) {
				//TODOD TS add logging, maybe throw exception
				System.err.println("While waiting for the request \"" + requestID + "\", there was no answer");
				logger.error("While waiting for the request \"" + requestID + "\", there was no answer");
				return;
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (DEBUG) {
			System.err.println("Request " + requestID + " is returned");
		}
		logger.debug("Try to wait for request " + requestID);
	}
	
	public void waitForNextRequestReturn() {
		try {
			waitForRequestReturnByNumber(currentRequestID);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//TODO delete when new XML parser is introduced
	@SuppressWarnings("unchecked")
	public void getDiagramDisplayProperties(Integer diagramID, ReturnCall<HashMap<String, Boolean>> onDiagramDisplayPropertiesReturn) {
		ReturnCall<Vector<Object>> localReturn = (response) -> {
			HashMap<String, Boolean> result = new HashMap<String, Boolean>();
			Vector<Vector<Object>> list = (Vector<Vector<Object>>) response.get(0);
			for(Vector<Object> item : list) {
				result.put((String) item.get(0), (Boolean) item.get(1));
			}
			onDiagramDisplayPropertiesReturn.run(result);
		};
		xmfRequestAsync(handle, diagramID, "getViewOptions", localReturn);
	}
	
    
    public void runOperation(Integer diagramID, String text) throws TimeOutException {
        sendMessage("runOperation", new Value[]{
			getNoReturnExpectedMessageID(diagramID),
			new Value(text)});
    }
}
