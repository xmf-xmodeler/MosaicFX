package tool.clients.fmmlxdiagrams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import org.w3c.dom.Element;

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


	public static enum ViewerStatus { CLEAN, DIRTY, LOADING }

	public static final AbstractPackageViewer SIMPLE_VIEWER = new AbstractPackageViewer(FmmlxDiagramCommunicator.getCommunicator(),
			-2, "simple_viewer") {

		@Override public Vector<String> getAvailableTypes() { return null;}
		@Override public Vector<FmmlxEnum> getEnums() { return null;}
		@Override public void updateEnums() { }
		@Override public FmmlxProperty getSelectedProperty() { return null;}
		@Override public ObservableList<FmmlxObject> getAllPossibleParents(Integer newValue) { return null;}
		@Override public boolean isEnum(String type) { return false;}
		@Override public Vector<String> getEnumItems(String type) { return null;}

		@Override public ObservableList<FmmlxObject> getPossibleAssociationEnds() { return null;}

		@Override protected void fetchDiagramDataSpecific() throws TimeOutException { }
		@Override protected void fetchDiagramDataSpecific2() { }
		@Override protected void clearDiagram_specific() { }
		@Override public void setSelectedObjectAndProperty(FmmlxObject objectByPath, FmmlxProperty property) {}
		@Override protected void updateViewerStatusInGUI(ViewerStatus newStatus) {}
		@Override protected boolean loadOnlyVisibleObjects() { return false; }	
	};
	
	protected AbstractPackageViewer(FmmlxDiagramCommunicator comm, int diagramID, String packagePath) {
		this.diagramID = diagramID;
		this.packagePath=packagePath;
		this.comm = comm;
		actions = new DiagramActions(this);
	}

	public FmmlxDiagramCommunicator getComm() {
		return comm;
	}

	public abstract void updateEnums();
	public abstract FmmlxProperty getSelectedProperty();
	public abstract Vector<String> getEnumItems(String type);
	public abstract ObservableList<FmmlxObject> getPossibleAssociationEnds();
	
	public int getID() {
		return diagramID;
	}
	
	public Vector<FmmlxObject> getObjects() {
		return new Vector<>(objects); // read-only
	}
	
	public void updateDiagram() {
		setViewerStatus(ViewerStatus.DIRTY);
		new Thread(this::fetchDiagramData).start();
	}

	//for test
	public void updateEdges() {
		new Thread(this::sendInitialEdgesPosition).start();
	}

	private void sendInitialEdgesPosition() {
		for(Edge<?> edge : edges){
			getComm().sendCurrentPositions(getID(), edge);
		}
	}

	protected void fetchDiagramData() {
		
		final boolean TIMER = false;
		final long START = System.currentTimeMillis();
		
		if(fetchingData) {
			System.err.println("\talready fetching diagram data");
			return;
		}
		fetchingData = true;
		setViewerStatus(ViewerStatus.LOADING);
		try {
			if(objects.size()==0){
				justLoaded = true;
			}
			this.clearDiagram();

			Vector<FmmlxObject> fetchedObjects = comm.getAllObjects(this);
			objects.addAll(fetchedObjects);

			if(TIMER) System.err.println("\nObjects loaded after            " + (System.currentTimeMillis() - START) + " ms.");
			
//			for(FmmlxObject o : objects) {
//				o.fetchDataDefinitions(comm);
//			}
			
			Vector<FmmlxObject> visibleObjects = new Vector<>();
			if (loadOnlyVisibleObjects()) {
				for(FmmlxObject o : objects)
					if(!o.hidden) visibleObjects.add(o); }
				else visibleObjects = objects;

			comm.fetchAllAttributes(this, visibleObjects);	
			comm.fetchAllOperations(this, visibleObjects);	
			comm.fetchAllConstraints(this, visibleObjects);			
			
			
			if(TIMER) System.err.println("Object definitions loaded after " + (System.currentTimeMillis() - START) + " ms.");
			
			issues.addAll(comm.fetchIssues(this));

			if(TIMER) System.err.println("Issues loaded after             " + (System.currentTimeMillis() - START) + " ms.");
			
			Vector<Edge<?>> fetchedEdges = comm.getAllAssociations(this);
			fetchedEdges.addAll(comm.getAllAssociationsInstances(this));
	
			if(TIMER) System.err.println("Edges loaded after              " + (System.currentTimeMillis() - START) + " ms.");

			edges.addAll(fetchedEdges);
			edges.addAll(comm.getAllInheritanceEdges(this));
			edges.addAll(comm.getAllDelegationEdges(this));
			edges.addAll(comm.getAllRoleFillerEdges(this));

			enums = comm.fetchAllEnums(this);
			auxTypes = comm.fetchAllAuxTypes(this);
						
			for(FmmlxObject o : objects) {
				o.fetchDataValues(comm);
			}

			if(TIMER) System.err.println("Object values loaded after      " + (System.currentTimeMillis() - START) + " ms.");
			
			fetchDiagramDataSpecific();
			
			if(TIMER) System.err.println("Other stuff loaded after        " + (System.currentTimeMillis() - START) + " ms.");

		} catch (TimeOutException e) {
			e.printStackTrace();
		}
		fetchingData = false;
		setViewerStatus(ViewerStatus.CLEAN);
		fetchDiagramDataSpecific2();
	}
	
	protected abstract boolean loadOnlyVisibleObjects();

	private void setViewerStatus(ViewerStatus newStatus) {
		updateViewerStatusInGUI(newStatus);
	}

	protected abstract void updateViewerStatusInGUI(ViewerStatus newStatus);

	protected abstract void fetchDiagramDataSpecific() throws TimeOutException;
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
		for(FmmlxObject obj : getObjects()) {
			if (obj.getPath().equals(path)){
				return obj;
			}
		}
		for(FmmlxObject obj : getObjects()) {
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
		String[] prefixes = new String[]{packagePath, "Root::XCore", "Root::Auxiliary", "Root"};
			for(String prefix : prefixes) {
				if(typePath.startsWith(prefix)) {
					return typePath.substring(prefix.length()+2);
				}
			}
		return typePath;
	}

	public final DiagramActions getActions() {
		return actions;
	}

	public ObservableList<FmmlxObject> getAllPossibleParents(Integer level) {
		ArrayList<FmmlxObject> objectList = new ArrayList<>();

		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects) {
				if (level != 0 && object.getLevel() == level) {
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
			for(FmmlxObject o : getObjects()) {
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

	public Canvas getCanvas() {return null;}

	public Vector<Integer> getAllObjectLevel() {
		Vector<Integer> result = new Vector<>();
		for(FmmlxObject obj : objects){
			if(!result.contains(obj.getLevel())){
				result.add(obj.getLevel());
			}
		}
		Collections.sort(result);
		Collections.reverse(result);
		return result;
	}
}