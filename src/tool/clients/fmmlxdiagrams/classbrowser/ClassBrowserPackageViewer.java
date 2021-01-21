package tool.clients.fmmlxdiagrams.classbrowser;

import java.util.Vector;

import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxEnum;
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
	public Vector<String> getAvailableTypes() {
		throw new RuntimeException();
	}

	@Override
	public Vector<FmmlxEnum> getEnums() {
		throw new RuntimeException();
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
	public boolean isEnum(String type) {
		throw new RuntimeException();
	}

	@Override
	public Vector<String> getEnumItems(String type) {
		throw new RuntimeException();
	}

	@Override
	public ObservableList<FmmlxObject> getAllPossibleParentList() {
		throw new RuntimeException();
	}

	@Override
	protected void fetchDiagramDataSpecific() throws TimeOutException {}

	@Override
	protected void fetchDiagramDataSpecific2() {view.notifyModelHasLoaded();}

	@Override
	protected void clearDiagram_specific() {}


	
}
