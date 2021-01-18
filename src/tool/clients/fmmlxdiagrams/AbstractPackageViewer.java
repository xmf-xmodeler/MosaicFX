package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.collections.ObservableList;
import tool.clients.serializer.Deserializer;

public abstract class AbstractPackageViewer {
	
	protected Vector<FmmlxObject> objects = new Vector<>();
	protected Vector<Edge> edges = new Vector<>();
	protected final int diagramID;
	protected final FmmlxDiagramCommunicator comm;
	protected DiagramActions actions;
	protected final String packagePath;
	protected transient boolean fetchingData;
	protected boolean justLoaded = false;
	
	
	protected AbstractPackageViewer(FmmlxDiagramCommunicator comm, int diagramID, String packagePath) {
		this.diagramID = diagramID;
		this.packagePath=packagePath;
		this.comm = comm;
		actions = new DiagramActions(this);
	}

	public FmmlxDiagramCommunicator getComm() {
		return comm;
	}

	public abstract Vector<String> getAvailableTypes();
	public abstract Vector<FmmlxEnum> getEnums();	
	public abstract void updateEnums();
	public abstract FmmlxProperty getSelectedProperty();
	public abstract ObservableList<FmmlxObject> getAllPossibleParents(Integer newValue);
	public abstract boolean isEnum(String type);
	public abstract Vector<String> getEnumItems(String type);
	public abstract ObservableList<FmmlxObject> getAllPossibleParentList();
	
	public int getID() {
		return diagramID;
	}
	
	public Vector<FmmlxObject> getObjects() {
		return new Vector<>(objects); // read-only
	}
	
	public void updateDiagram() {
		new Thread(this::fetchDiagramData).start();
	}
	
	protected void fetchDiagramData() {
		if(fetchingData) {
			System.err.println("\talready fetching diagram data");
			return;
		}
		fetchingData = true;
		try {

			if(objects.size()==0){
				justLoaded = true;
			}
			this.clearDiagram();

			Vector<FmmlxObject> fetchedObjects = comm.getAllObjects(this);
			objects.addAll(fetchedObjects);

			
			for(FmmlxObject o : objects) {
				o.fetchDataDefinitions(comm);
			}
			
			for(FmmlxObject o : objects) {
				o.fetchDataValues(comm);
			}
			
			Vector<Edge> fetchedEdges = comm.getAllAssociations(this);
			fetchedEdges.addAll(comm.getAllAssociationsInstances(this));
	
			edges.addAll(fetchedEdges);
			edges.addAll(comm.getAllInheritanceEdges(this));
			edges.addAll(comm.getAllDelegationEdges(this));
			edges.addAll(comm.getAllRoleFillerEdges(this));
			fetchDiagramDataSpecific();
			
		} catch (TimeOutException e) {
			e.printStackTrace();
		}
		fetchingData = false;
		fetchDiagramDataSpecific2();
	}
	
	protected abstract void fetchDiagramDataSpecific() throws TimeOutException;
	protected abstract void fetchDiagramDataSpecific2();

	public void clearDiagram(){
		
		objects.clear();
		edges.clear();
		clearDiagram_specific();
		
		
	}

	protected abstract void clearDiagram_specific();
	
	public Vector<FmmlxAssociation> getRelatedAssociationByObject(FmmlxObject object) {
		Vector<FmmlxAssociation> result = new Vector<>();
		for (Edge tmp : edges) {
			if (tmp instanceof FmmlxAssociation) {
				if (tmp.sourceNode.getName().equals(object.getName()) || tmp.targetNode.getName().equals(object.getName())) {
					result.add((FmmlxAssociation) tmp);
				}
			}
		}
		return result;
	}
	
	public Vector<Edge> getEdges() {
		return new Vector<>(edges); // read-only
	}

	public Vector<FmmlxAssociation> getAssociations() {
		Vector<FmmlxAssociation> result = new Vector<>();
		for (Edge tmp : edges) {
			if (tmp instanceof FmmlxAssociation) {
				result.add((FmmlxAssociation) tmp);
			}
		}
		return result; // read-only
	}

	public Vector<FmmlxLink> getAssociationInstance(){
		Vector<FmmlxLink> result = new Vector<>();
		for (Edge tmp : edges) {
			if (tmp instanceof FmmlxLink) {
				result.add((FmmlxLink) tmp);
			}
		}
		return result; // read-only
	}
	
	public String getPackagePath() {
		return packagePath;
	}
	
	public Vector<FmmlxAssociation> findAssociations(FmmlxObject source, FmmlxObject target) {
		Vector<FmmlxAssociation> result = new Vector<>();
		for (Edge e : edges)
			if (e instanceof FmmlxAssociation) {
				FmmlxAssociation association = (FmmlxAssociation) e;
				if (association.doObjectsFit(source, target)) result.add(association);
			}
		return result;
	}
	
	public boolean isNameAvailable(String t) {
		for (FmmlxObject o : objects) if (o.getName().equals(t)) return false;
		return true;
	}
	
	public FmmlxObject getObjectByPath(String path) {
		for(FmmlxObject obj : getObjects()) {
			if (obj.getPath().equals(path)){
				return obj;
			}
		}
		return null;
	}
	
	public FmmlxAssociation getAssociationByPath(String path) {
		for(FmmlxAssociation as : getAssociations()) {
			if(as.getPath().equals(path)) {
				return as;
			}
		}
		return null;
	}
	
	public String convertPath2Short(String typePath) {
		String[] prefixes = new String[]{packagePath, "Root::XCore", "Root::Auxiliary", "Root"};
			for(String prefix : prefixes) {
				if(typePath.startsWith(prefix)) {
					return typePath.substring(prefix.length()+2);
				}
			}
		return typePath;
	}



}
