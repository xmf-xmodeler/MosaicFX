package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.collections.ObservableList;

/*
 * FH
 * used for interacting with a gui in the control center
 */
public class ControlCenterGUIView  extends AbstractPackageViewer {

	public ControlCenterGUIView(FmmlxDiagramCommunicator comm, int diagramID, String packagePath) {
		super(comm, diagramID, packagePath);
		// TODO Auto-generated constructor stub
	}

	@Override
	public FmmlxProperty getSelectedProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObservableList<FmmlxObject> getPossibleAssociationEnds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean loadOnlyVisibleObjects() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void updateViewerStatusInGUI(ViewerStatus newStatus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void fetchDiagramDataSpecific() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void fetchDiagramDataSpecific2() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void clearDiagram_specific() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelectedObjectAndProperty(FmmlxObject objectByPath, FmmlxProperty property) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Vector<CanvasElement> getSelectedObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
