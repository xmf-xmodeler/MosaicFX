package tool.clients.serializer.interfaces;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxObject;

import java.util.Vector;

public interface Deserializer {
    void loadProject(FmmlxDiagramCommunicator fmmlxDiagramCommunicator);

    void getAllDiagramElement(Integer newDiagramID);

    void alignCoordinate(FmmlxDiagram diagram);

    String getProjectName();

    void syncObjectCoordinate(AbstractPackageViewer diagram, Integer id, Vector<FmmlxObject> objects);
}
