package tool.clients.fmmlxdiagrams.classbrowser;

import java.util.ArrayList;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.*;

public class ClassBrowserPackageViewer extends AbstractPackageViewer{

	private final ModelBrowser view;
	@Override protected boolean loadOnlyVisibleObjects() { return false; }	
	
	protected ClassBrowserPackageViewer(FmmlxDiagramCommunicator comm, int diagramID, String packagePath, ModelBrowser view) {
		super(comm, diagramID, packagePath);
		this.view = view;
	}

	@Override
	public FmmlxProperty getSelectedProperty() {
		throw new RuntimeException();
	}

	@Override
	public ObservableList<FmmlxObject> getPossibleAssociationEnds() {
		ArrayList<FmmlxObject> objectList = new ArrayList<>();

		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects.values()) {
				if (object.getLevel().isClass()) {
					objectList.add(object);
				}
			}
		}
		return FXCollections.observableArrayList(objectList);
	}

	@Override
	protected void fetchDiagramDataSpecific() {}

	@Override
	protected void fetchDiagramDataSpecific2() {view.notifyModelHasLoaded();}

	@Override
	protected void clearDiagram_specific() {}

	@Override
	public void setSelectedObjectAndProperty(FmmlxObject object, FmmlxProperty property) {view.setSelectedObjectAndProperty(object, property);}

	@Override
	protected void updateViewerStatusInGUI(ViewerStatus newStatus) {
		view.setStatusButton(newStatus);		
	}

	@Override
	protected Vector<CanvasElement> getSelectedObjects() {
		// TODO Auto-generated method stub
		// FH 02.05.2023
		// Method was needed in the interface to access it for FmmlxDiagram
		// in this class it is not needed
		return null;
	}	
}
