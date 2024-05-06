package tool.clients.fmmlxdiagrams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;

import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.graphics.GraphicalMappingInfo;
import tool.clients.fmmlxdiagrams.graphics.View;

public abstract class AbstractPackageViewer {
	
	protected HashMap<String, FmmlxObject> objects = new HashMap<>();
	protected Vector<FmmlxEnum>   enums = new Vector<>();
	protected Vector<String>      auxTypes = new Vector<>();
	protected Vector<Edge<?>>     edges = new Vector<>();
	protected Vector<Issue>       issues = new Vector<>();
	protected Vector<AssociationType> associationTypes = new Vector<>();
	protected final int diagramID;
	protected final FmmlxDiagramCommunicator comm;
	protected DiagramActions actions;
	protected final String packagePath;
	protected transient boolean fetchingData;
	protected boolean justLoaded = false;
	protected boolean umlMode;
	protected Vector<String> importedPackages = new Vector<>();
	public boolean extendedConstraintCheck = true;
  
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(FmmlxDiagramCommunicator.class);
	protected final NoteList notes = new NoteList();

	public static enum ViewerStatus { CLEAN, DIRTY, LOADING }

	protected AbstractPackageViewer(FmmlxDiagramCommunicator comm, int diagramID, String packagePath) {
		this.diagramID = diagramID;
		this.packagePath=packagePath;
		this.comm = comm;
		actions = new DiagramActions(this);
	}
	
	public boolean isUMLMode() {
		return umlMode;
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
		return new Vector<>(objects.values());
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

	/**
	 * Used to update diagram to backenddata
	 */
	public abstract void  updateDiagram();
			
	/**
	 * Used to update diagram to backenddata
	 * @param onDiagramUpdated defines an action that is performed after the diagram is updated
	 */
	public abstract void updateDiagram(ReturnCall<Object> onDiagramUpdated);
	
	/**
	 * This function defines the update logic for every diagram. The gui of a diagram will consume all upcoming events. 
	 * For debug purposes all events are logged.
	 * @param node main note for which all events will be consumed
	 * @param onDiagramUpdated action performed after diagram is updated
	 */
	public void updateDiagram(javafx.scene.Node node, ReturnCall<Object> onDiagramUpdated) {
		setViewerStatus(ViewerStatus.DIRTY);
		
		List<Event> eventList = new ArrayList<>();
		//Every event is consumed and the event is added to an event list
		EventHandler<Event> actionHandler = new EventHandler<Event>() {
	            @Override
	            public void handle(Event event) {
	            	eventList.add(event);
	            	event.consume();
	            }
	        };
//	    if(!fetchingData) {
//	    System.err.println("add filter");
//		node.addEventFilter(Event.ANY, actionHandler); }
		
		Thread t = new Thread(() -> {
			this.fetchDiagramData(r -> {
				onDiagramUpdated.run(null);
				//after the update execution the EventFilter is removed from the node
//				node.removeEventFilter(Event.ANY, actionHandler);
				//all consumed events are printed to the log file
				logger.debug("Block events while updating {}", eventList);
//			    System.err.println("remove filter");
			});
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

	public void fetchDiagramData( ReturnCall<Object> onDataFetched ) {
		final boolean TIMER = false;
		final long START = System.currentTimeMillis();
		
		if (fetchingData) {
			System.err.println("\talready fetching diagram data");
			return;
		}
		fetchingData = true;
		setViewerStatus(ViewerStatus.LOADING);
		if (objects.size() == 0) {
			justLoaded = true;
		}
		this.clearDiagram();
		ReturnCall<Vector<String>> opValReturn = x3 -> {
			if (TIMER) {
				System.err.println("Operation values loaded after      " + (System.currentTimeMillis() - START) + " ms.");
			}
				fetchDiagramDataSpecific();
			if (TIMER) {
				System.err.println("Other stuff loaded after        " + (System.currentTimeMillis() - START) + " ms.");
			}
				fetchingData = false;
				fetchDiagramDataSpecific2();
				setViewerStatus(ViewerStatus.CLEAN);
				onDataFetched.run(null);
		};
		
		ReturnCall<Vector<String>> slotsReturn = x2 -> {
			if(TIMER) System.err.println("Slot values loaded after      " + (System.currentTimeMillis() - START) + " ms.");
			HashMap<FmmlxObject, Vector<String>> opValNames = new HashMap<>();
			for(FmmlxObject o : objects.values()) {
				opValNames.put(o, o.getMonitoredOperationsNames());
			}
			comm.fetchAllOperationValues(this, opValNames, opValReturn);
		};
		
		ReturnCall<Vector<String>> auxTypeReturn = fetchedAuxTypes -> {
			auxTypes = fetchedAuxTypes;
			HashMap<FmmlxObject, Vector<String>> slotNames = new HashMap<>();
			for(FmmlxObject o : objects.values()) {
				slotNames.put(o, o.getSlotNames());
			}
			comm.fetchAllSlots(this, slotsReturn);
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
			if(TIMER) System.err.println((extendedConstraintCheck?"Extended":"User Defined")+" Issues loaded after             " + (System.currentTimeMillis() - START) + " ms.");
			comm.getAllAssociations(this, allAssociationsReturn);
		};
				
		ReturnCall<Vector<FmmlxObject>> allConstraintsReturn = x1 -> {
			if(TIMER) System.err.println("Constraints loaded after " + (System.currentTimeMillis() - START) + " ms.");
			comm.fetchIssues(this, extendedConstraintCheck, allIssuesReturn);
		};
		
		ReturnCall<Vector<FmmlxObject>> allOperationsReturn = visibleObjects -> {
			if(TIMER) System.err.println("Operations loaded after " + (System.currentTimeMillis() - START) + " ms.");
			comm.fetchAllConstraints(this, visibleObjects, allConstraintsReturn);	
		};

		ReturnCall<Vector<FmmlxObject>> allAttributesReturn = visibleObjects -> {
			if(TIMER) System.err.println("Attributes loaded after " + (System.currentTimeMillis() - START) + " ms.");
			comm.fetchAllOperations(this, visibleObjects, allOperationsReturn);	
		};
		
		ReturnCall<Vector<FmmlxObject>> allObjectsReturn = fetchedObjects -> {
			objects.clear();
			for(FmmlxObject o : fetchedObjects) objects.put(o.ownPath, o);
			
			if(TIMER) System.err.println("\nObjects loaded after            " + (System.currentTimeMillis() - START) + " ms.");
					
			Vector<FmmlxObject> visibleObjects = new Vector<>();
			if (loadOnlyVisibleObjects()) {
				for(FmmlxObject o : objects.values())
					if(!o.hidden) visibleObjects.add(o); }
				else visibleObjects = new Vector<>(objects.values());
			
			comm.fetchAllAttributes(this, visibleObjects, allAttributesReturn);
		};
		
		ReturnCall<Vector<AssociationType>> associationTypesReceivedReturn = associationTypes -> {
			this.associationTypes = associationTypes;
			Collections.sort(this.associationTypes);
			if(TIMER) System.err.println("\nRequesting Objects after            " + (System.currentTimeMillis() - START) + " ms.");
			comm.getAllObjects(this, allObjectsReturn);
		};
		
		ReturnCall<Vector<String>> importedPackagesReturn = imports -> {
			this.importedPackages = imports;
			comm.getAssociationTypes(this, associationTypesReceivedReturn);
		};
		
		comm.getImportedPackages(this.diagramID, importedPackagesReturn);
		fetchNotes();
	}
	
	/**
	 * This function asks the backend for all information about diagramNotes. For the most fetching operations the call order does play a huge role. 
	 * If YOu will not ask in the right order errors will be raised due to missing backend information and interdependencies. For the notes this does not play 
	 * any role because the loading process is not dependend on other data
	 */
	private void fetchNotes() {
		ReturnCall<Vector<Note>> notesReturned = returnedNotes -> {
			//reset notesList. Everytime the diagram is loaded all CanvasElements will be fully new loaded from xmf.
			this.notes.clear();
			addNewNotesToNoteList(returnedNotes);
			setNotePositionsFromXMF();
		};
		Note.getAllNotes(diagramID, notesReturned);
	}

	/**
	 * If you create a note in the frontend all data about the note is send to the backend. Afterward the diagram is updated, what means, that on Java-side there is no more information about the new note. 
	 * This function checks if the current instance of the diagram holds a reference to all notes that are in the returnedNotes-list. If the note is already contained the loop continues otherwise the reference is added to the diagramNotes-list.
	 * @param returnedNotes list of all notes associated to the diagram in the backend
	 */
	private void addNewNotesToNoteList(Vector<Note> returnedNotes) {
		for (Note note : returnedNotes) {
			if (notes.contain(note.getId())) {
				continue;
			}
			this.notes.add(note);
		}
	}

	/**
	 * Beside the data about the note like color and content the backend manages a mapping to the diagram. This mapping contains the information xPosition, yPosition and if the note is hidden.
	 * This information must be asked separately because it can be updated by movement of the note on the canvas. It is important, that before this function is called the notes-list is updated. Therefore this function 
	 * is called from a ReturnCall of getAllNotes.
	 * 
	 * !!Prerequisite: Get all notes from XMF 
	 */
	private void setNotePositionsFromXMF() {
		ReturnCall<Vector<GraphicalMappingInfo>> noteMappingRetturned = noteMappings -> {
			for (GraphicalMappingInfo mappingInfo : noteMappings) {
				//here the reference from the diagram is initialized. If you would have not updates the note list, an exception would occure.
				try {
					Note note = notes.getNote(mappingInfo.getNoteIdFromMappingKey());					
					note.setDiagramMapping(mappingInfo);
				} catch (NullPointerException e) {
					System.err.println("Pleas update notes first");
					e.printStackTrace();
				}	
			}
		};
		Note.getNotesMappings(diagramID, noteMappingRetturned);	
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
	
	public final String getProjectName() {
		return getPackagePath().substring(6);
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
		for (FmmlxObject o : objects.values()) if (o.getName().equals(t)) return false;
		return true;
	}
	
	@SuppressWarnings("serial")
	public static class PathNotFoundException extends RuntimeException {

		public PathNotFoundException(String message) {
			super(message);
		}		
	}
	
	public final FmmlxObject getObjectByPath(String path) throws PathNotFoundException{
		if(objects.containsKey(path))
			return objects.get(path);
		else
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
			for (FmmlxObject object : objects.values()) {
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
		for(FmmlxObject obj : objects.values()){
			if(!result.contains(obj.getLevel())){
				result.add(obj.getLevel().getMinLevel());
			}
		}
		Collections.sort(result);
		Collections.reverse(result);
		return result;
	}

	public View getActiveDiagramViewPane() {return null;}

	public Vector<AssociationType> getAssociationTypes() {
		return new Vector<>(associationTypes);
	}
	
	public NoteList getNotes() {
		return notes;
	}

	public Vector<String> getImportedPackages() {
		return new Vector<>(importedPackages);
	}

	
	/**
	 * Returns a list of all nodes, that are contained in the canvas. That are all FmmlxObjects plus all nodes.
	 * @return list of all node elements
	 */
	public ArrayList<Node> getAllNodes() {
		ArrayList<Node> nodes = new ArrayList<>();
		nodes.addAll(objects.values());
		nodes.addAll(notes);
		return nodes;
	}
	
	/**
	 * Searches in edges for edges of the type FmmlxAssociation
	 * @return all FmmlxAssociations associates with this AbstractpackageViewer
	 */
	public Vector<FmmlxAssociation> getFmmlxAssociations() {
		Vector<FmmlxAssociation> assocs = new Vector<>();
		for (Edge<?> edge : edges) {
			if (edge instanceof FmmlxAssociation) {
				assocs.add((FmmlxAssociation) edge);
			}
		}
		return assocs;
	}
	
	public FmmlxObject getObjectByName(String name) {
		String objPath = getPackagePath() + "::" + name;
		try {
			return getObjectByPath(objPath);
		} catch (Exception e) {
			return null;
		}
	}
}