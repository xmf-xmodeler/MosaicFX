package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.collections.ObservableList;

public abstract class AbstractPackageViewer {
	
	protected final FmmlxDiagramCommunicator comm;
	protected DiagramActions actions;
	
	protected AbstractPackageViewer(FmmlxDiagramCommunicator comm) {
		this.comm = comm;
		actions = new DiagramActions(this);
	}

	public FmmlxDiagramCommunicator getComm() {
		return comm;
	}

	public abstract void updateDiagram();
	public abstract int getID();
	public abstract Vector<String> getAvailableTypes();
	public abstract Vector<FmmlxEnum> getEnums();	
	public abstract void updateEnums();
	public abstract FmmlxProperty getSelectedProperty();
	public abstract ObservableList<FmmlxObject> getAllPossibleParents(Integer newValue);
	public abstract Vector<FmmlxObject> getObjects();
	public abstract FmmlxObject getObjectByPath(String name);
	public abstract boolean isEnum(String type);
	public abstract Vector<String> getEnumItems(String type);
	public abstract ObservableList<FmmlxObject> getAllPossibleParentList();
	public abstract Vector<FmmlxAssociation> findAssociations(FmmlxObject source, FmmlxObject target);
	public abstract Vector<FmmlxAssociation> getAssociations();
	public abstract FmmlxAssociation getAssociationByPath(String path);
	public abstract boolean isNameAvailable(String instanceName);





}
