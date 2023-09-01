package tool.clients.fmmlxdiagrams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Vector;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import tool.clients.fmmlxdiagrams.graphics.View;

public abstract class AbstractPackageViewer {
	
	protected Vector<FmmlxObject> objects = new Vector<>();
	protected Vector<FmmlxEnum>   enums = new Vector<>();
	protected Vector<String>      auxTypes = new Vector<>();
	protected Vector<Edge<?>>     edges = new Vector<>();
	protected Vector<Issue>       issues = new Vector<>();
	protected final int diagramID;
	protected final FmmlxDiagramCommunicator comm;
	protected DiagramActions actions;
	protected final String packagePath;
	protected transient boolean fetchingData;
	protected boolean justLoaded = false;
	protected boolean umlMode;

	public static enum ViewerStatus { CLEAN, DIRTY, LOADING }

	protected AbstractPackageViewer(FmmlxDiagramCommunicator comm, int diagramID, String packagePath) {
		this.diagramID = diagramID;
		this.packagePath=packagePath;
		this.comm = comm;
		actions = new DiagramActions(this);
	}

	public FmmlxDiagramCommunicator getComm() {
		return comm;
	}

	public abstract FmmlxProperty getSelectedProperty();
	public abstract ObservableList<FmmlxObject> getPossibleAssociationEnds();
	
	public int getID() {
		return diagramID;
	}
	
	public Vector<FmmlxObject> getObjectsReadOnly() {
		return new Vector<>(objects);
	}
	
	public Vector<FmmlxObject> getVisibleObjectsReadOnly() {
		Vector<FmmlxObject> allObjects = getObjectsReadOnly();
		Vector<FmmlxObject> allVisibleObjects = new Vector<FmmlxObject>();

		for (FmmlxObject o : allObjects) {
			if (o.hidden == false)
				allVisibleObjects.add(o);
		}
		return allVisibleObjects;
	}
	
	public void updateDiagram() {
		updateDiagram( (ReturnCall<Object>) e -> { } );
	}
		
	public void updateDiagram( ReturnCall<Object> a ) {
		setViewerStatus(ViewerStatus.DIRTY);
		
		Thread t = new Thread( () -> {
			this.fetchDiagramData( a );
		});
		t.start();
	}

	//for test
	public void updateEdges() {
		new Thread(this::sendInitialEdgesPosition).start();
	}

	private void sendInitialEdgesPosition() {
		for(Edge<?> edge : edges){
			getComm().sendCurrentEdgePositions(getID(), edge);
		}
	}
	
	public boolean isUpdating() {
		// this method allows to figure out, if the diagram is currently updating
		// it is required to allow waiting for completion of this update
		return fetchingData;
	}

	protected void fetchDiagramData( ReturnCall<Object> a ) {
		
		final boolean TIMER = false;
		final long START = System.currentTimeMillis();
		
		if(fetchingData) {
			System.err.println("\talready fetching diagram data");
			return;
		}
		fetchingData = true;
		setViewerStatus(ViewerStatus.LOADING);
			if(objects.size()==0){
				justLoaded = true;
			}
		this.clearDiagram();
				
		ReturnCall<Vector<String>> opValReturn = x3 -> {

			if(TIMER) System.err.println("Operation values loaded after      " + (System.currentTimeMillis() - START) + " ms.");
			
			fetchDiagramDataSpecific();
			
			if(TIMER) System.err.println("Other stuff loaded after        " + (System.currentTimeMillis() - START) + " ms.");
	
			fetchingData = false;
			
			fetchDiagramDataSpecific2();
//			Platform.runLater(()-> {
//				
//			});
			
			setViewerStatus(ViewerStatus.CLEAN);
			
			a.run(null);
		};
		
		ReturnCall<Vector<String>> slotsReturn = x2 -> {
			if(TIMER) System.err.println("Slot values loaded after      " + (System.currentTimeMillis() - START) + " ms.");
			HashMap<FmmlxObject, Vector<String>> opValNames = new HashMap<>();
			for(FmmlxObject o : objects) {
				opValNames.put(o, o.getMonitoredOperationsNames());
			}
			comm.fetchAllOperationValues(this, opValNames, opValReturn);
		};
		
		ReturnCall<Vector<String>> auxTypeReturn = fetchedAuxTypes -> {
			auxTypes = fetchedAuxTypes;
			HashMap<FmmlxObject, Vector<String>> slotNames = new HashMap<>();
			for(FmmlxObject o : objects) {
				slotNames.put(o, o.getSlotNames());
			}
			comm.fetchAllSlots(this, slotNames, slotsReturn);
		};

		ReturnCall<Vector<FmmlxEnum>> enumsReturn = fetchedEnums -> {
			enums = fetchedEnums;
			comm.fetchAllAuxTypes(this, auxTypeReturn);
		};
		
		ReturnCall<Vector<Edge<?>>> allRoleFillerEdgesReturn = fetchedRoleFillerEdges -> {
			edges.addAll(fetchedRoleFillerEdges);
			if(TIMER) System.err.println("Edges loaded after              " + (System.currentTimeMillis() - START) + " ms.");
			comm.fetchAllEnums(this, enumsReturn);
		};
		
		ReturnCall<Vector<Edge<?>>> allDelegationEdgesReturn = fetchedDelegationEdges -> {
			edges.addAll(fetchedDelegationEdges);
			comm.getAllRoleFillerEdges(this, allRoleFillerEdgesReturn);
		};

		ReturnCall<Vector<Edge<?>>> allInheritanceEdgesReturn = fetchedInheritanceEdges -> {
			edges.addAll(fetchedInheritanceEdges);
			comm.getAllDelegationEdges(this, allDelegationEdgesReturn);
		};
		
		ReturnCall<Vector<Edge<?>>> allAssociationsInstancesReturn = fetchedLinks -> {
			edges.addAll(fetchedLinks);
			comm.getAllInheritanceEdges(this, allInheritanceEdgesReturn);
		};
		
		ReturnCall<Vector<Edge<?>>> allAssociationsReturn = fetchedAssociations -> {
			edges.addAll(fetchedAssociations);
			comm.getAllAssociationsInstances(this, allAssociationsInstancesReturn);
		};
		
		ReturnCall<Vector<Issue>> allIssuesReturn = fetchedIssues -> {	
			issues.addAll(fetchedIssues);
			Collections.sort(issues);
			if(TIMER) System.err.println("Issues loaded after             " + (System.currentTimeMillis() - START) + " ms.");
			comm.getAllAssociations(this, allAssociationsReturn);
		};
				
		ReturnCall<Vector<FmmlxObject>> allConstraintsReturn = x1 -> {
			if(TIMER) System.err.println("Object definitions loaded after " + (System.currentTimeMillis() - START) + " ms.");
			comm.fetchIssues(this, allIssuesReturn);
		};
		
		ReturnCall<Vector<FmmlxObject>> allOperationsReturn = visibleObjects -> {
			comm.fetchAllConstraints(this, visibleObjects, allConstraintsReturn);	
		};

		ReturnCall<Vector<FmmlxObject>> allAttributesReturn = visibleObjects -> {
			comm.fetchAllOperations(this, visibleObjects, allOperationsReturn);	
		};
		
		ReturnCall<Vector<FmmlxObject>> allObjectsReturn = fetchedObjects -> {
			objects.addAll(fetchedObjects);

			if(TIMER) System.err.println("\nObjects loaded after            " + (System.currentTimeMillis() - START) + " ms.");
					
			Vector<FmmlxObject> visibleObjects = new Vector<>();
			if (loadOnlyVisibleObjects()) {
				for(FmmlxObject o : objects)
					if(!o.hidden) visibleObjects.add(o); }
				else visibleObjects = objects;
			
			comm.fetchAllAttributes(this, visibleObjects, allAttributesReturn);
		};
		
		if(TIMER) System.err.println("\nRequesting Objects after            " + (System.currentTimeMillis() - START) + " ms.");
		comm.getAllObjects(this, allObjectsReturn);

	}
	
	protected abstract boolean loadOnlyVisibleObjects();

	private void setViewerStatus(ViewerStatus newStatus) {
		updateViewerStatusInGUI(newStatus);
	}

	protected abstract void updateViewerStatusInGUI(ViewerStatus newStatus);

	protected abstract void fetchDiagramDataSpecific();
	protected abstract void fetchDiagramDataSpecific2();

	public void clearDiagram(){
		objects.clear();
		edges.clear();
		issues.clear();	
		clearDiagram_specific();
	}

	protected abstract void clearDiagram_specific();
	
	public final Vector<FmmlxAssociation> getRelatedAssociationByObject(FmmlxObject object) {
		Vector<FmmlxAssociation> result = new Vector<>();
		for (Edge<?> tmp : edges) {
			if (tmp instanceof FmmlxAssociation) {
				if (((FmmlxObject) (tmp.sourceNode)).getName().equals(object.getName()) 
				  ||((FmmlxObject) (tmp.targetNode)).getName().equals(object.getName())) {
					result.add((FmmlxAssociation) tmp);
				}
			}
		}
		return result;
	}
	
	public final Vector<FmmlxLink> getRelatedLinksByObject(FmmlxObject object) {
		Vector<FmmlxLink> result = new Vector<>();
		for (Edge<?> tmp : edges) {
			if (tmp instanceof FmmlxLink) {
				if (((FmmlxObject) (tmp.sourceNode)).getName().equals(object.getName()) 
				  ||((FmmlxObject) (tmp.targetNode)).getName().equals(object.getName())) {
					result.add((FmmlxLink) tmp);
				}
			}
		}
		return result;
	}
	
	public final Vector<Edge<?>> getEdges() {
		return new Vector<>(edges); // read-only
	}

	public final Vector<FmmlxAssociation> getAssociations() {
		Vector<FmmlxAssociation> result = new Vector<>();
		for (Edge<?> tmp : edges) {
			if (tmp instanceof FmmlxAssociation) {
				result.add((FmmlxAssociation) tmp);
			}
		}
		return result; // read-only
	}

	public final Vector<FmmlxLink> getAssociationInstance(){
		Vector<FmmlxLink> result = new Vector<>();
		for (Edge<?> tmp : edges) {
			if (tmp instanceof FmmlxLink) {
				result.add((FmmlxLink) tmp);
			}
		}
		return result; // read-only
	}

	public final Vector<DelegationEdge> getDelegations() {
		Vector<DelegationEdge> result = new Vector<>();
		for (Edge<?> tmp : edges) {
			if (tmp instanceof DelegationEdge) {
				result.add((DelegationEdge) tmp);
			}
		}
		return result; // read-only
	}

	public final Vector<RoleFillerEdge> getRoleFillerEdges() {
		Vector<RoleFillerEdge> result = new Vector<>();
		for (Edge<?> tmp : edges) {
			if (tmp instanceof RoleFillerEdge) {
				result.add((RoleFillerEdge) tmp);
			}
		}
		return result; // read-only
	}
	
	public Vector<String> getEnumItems(String enumName) {
		for (FmmlxEnum e : enums) {
			if(e.getName().equals(enumName)) return e.getItems();
		}
		return null;
	}
	
	public final String getPackagePath() {
		return packagePath;
	}
	
	public final Vector<FmmlxAssociation> findAssociations(FmmlxObject source, FmmlxObject target) {
		Vector<FmmlxAssociation> result = new Vector<>();
		for (Edge<?> e : edges)
			if (e instanceof FmmlxAssociation) {
				FmmlxAssociation association = (FmmlxAssociation) e;
				if (association.doObjectsFit(source, target)) result.add(association);
			}
		return result;
	}
	
	public final boolean isNameAvailable(String t) {
		for (FmmlxObject o : objects) if (o.getName().equals(t)) return false;
		return true;
	}
	
	@SuppressWarnings("serial")
	public static class PathNotFoundException extends RuntimeException {

		public PathNotFoundException(String message) {
			super(message);
		}
		
	}
	
	public final FmmlxObject getObjectByPath(String path) throws PathNotFoundException{
		for(FmmlxObject obj : getObjectsReadOnly()) {
			if (obj.getPath().equals(path)){
				return obj;
			}
		}
		for(FmmlxObject obj : getObjectsReadOnly()) {
			if (obj.getName().equals(path)){
				return obj;
			}
		}
		throw new PathNotFoundException("path " + path + " not found");
	}
	
	public final FmmlxAssociation getAssociationByPath(String path) {
		for(FmmlxAssociation as : getAssociations()) {
			if(as.getPath().equals(path)) {
				return as;
			}
		}
		return null;
	}
	
	public final String convertPath2Short(String typePath) {
		String[] prefixes = new String[]{packagePath+"::", "Root::XCore::", "Root::Auxiliary::", "Root::"};
			for(String prefix : prefixes) {
				if(typePath.startsWith(prefix)) {
					return typePath.substring(prefix.length());
				}
			}
		return typePath;
	}

	public final DiagramActions getActions() {
		return actions;
	}
	
	@Deprecated
	public ObservableList<FmmlxObject> getAllPossibleParents(int level) {
		return getAllPossibleParents(new Level(level));
	}
	
	public ObservableList<FmmlxObject> getAllPossibleParents(Level level) {
		ArrayList<FmmlxObject> objectList = new ArrayList<>();

		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects) {
				if (level.getMinLevel() != 0 && object.getLevel().getMinLevel() == level.getMinLevel()) {
					objectList.add(object);
				}
			}
		}
		return FXCollections.observableArrayList(objectList);
	}
	
	public boolean isEnum(String enumName) {
		for (FmmlxEnum e : enums) {
			if(e.getName().equals(enumName)) return true;
		}
		return false;
	}
	
	public Vector<String> getAvailableTypes() {
		Vector<String> types = new Vector<>();
		types.add("Boolean");
		types.add("Integer");
		types.add("Float");
		types.add("String");
		types.addAll(auxTypes);
		for(FmmlxEnum e : enums) {
			types.add(e.getName());
		}
		return types;
	}


	public Vector<FmmlxEnum> getEnums() {
		return enums;
	}
	
	public Vector<Issue> getIssues(FmmlxObject fmmlxObject) {
		Vector<Issue> result = new Vector<>();
		if(issues != null) for(Issue issue : issues) { 
			if(issue.isAffected(fmmlxObject)) {
				result.add(issue);
			}
		}
		return result;
	}

	public abstract void setSelectedObjectAndProperty(FmmlxObject objectByPath, FmmlxProperty property);

	public FmmlxOperation getOperation(FmmlxOperationValue newOpV) {
		try{
			for(FmmlxObject o : getObjectsReadOnly()) {
				if(o.getOperationValues().contains(newOpV)) {
					FmmlxObject oOf = getObjectByPath(o.getOfPath());
					for(FmmlxOperation op : oOf.getAllOperations()) {
						if(op.getName().equals(newOpV.name)) {
							FmmlxObject opOwner = getObjectByPath(op.getOwner());
							for(FmmlxOperation op2 : opOwner.getOwnOperations()) {
								if(op2.getName().equals(newOpV.name)) {
									return op2;
								}
							}; throw new RuntimeException("Operation not found in owner");
						}
					}; throw new RuntimeException("Operation not found in class");
				}
			}; throw new RuntimeException("Object not found for OperationValue");
		} catch (PathNotFoundException pnfe) {throw new RuntimeException("Something went wrong",pnfe);}
	}

	public Vector<Integer> getAllObjectLevel() {
		Vector<Integer> result = new Vector<>();
		for(FmmlxObject obj : objects){
			if(!result.contains(obj.getLevel())){
				result.add(obj.getLevel().getMinLevel());
			}
		}
		Collections.sort(result);
		Collections.reverse(result);
		return result;
	}

	public View getActiveDiagramViewPane() {return null;}
	
}