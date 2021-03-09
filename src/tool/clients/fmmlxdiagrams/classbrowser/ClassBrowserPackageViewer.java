package tool.clients.fmmlxdiagrams.classbrowser;

import java.util.ArrayList;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.fmmlxdiagrams.TimeOutException;

public class ClassBrowserPackageViewer extends AbstractPackageViewer{

	private final ModelBrowser view;
	
	protected ClassBrowserPackageViewer(FmmlxDiagramCommunicator comm, int diagramID, String packagePath, ModelBrowser view) {
		super(comm, diagramID, packagePath);
		this.view = view;
	}

	@Override
	public void updateEnums() {
		throw new RuntimeException();
	}

	@Override
	public FmmlxProperty getSelectedProperty() {
		throw new RuntimeException();
	}

	@Override
	public Vector<String> getEnumItems(String type) {
		throw new RuntimeException();
	}

	@Override
	public ObservableList<FmmlxObject> getPossibleAssociationEnds() {
		ArrayList<FmmlxObject> objectList = new ArrayList<>();

		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects) {
				if (object.getLevel() != 0) {
					objectList.add(object);
				}
			}
		}
		return FXCollections.observableArrayList(objectList);
	}

	@Override
	protected void fetchDiagramDataSpecific() throws TimeOutException {}

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
}
