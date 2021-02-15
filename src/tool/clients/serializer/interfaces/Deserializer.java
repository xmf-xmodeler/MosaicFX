package tool.clients.serializer.interfaces;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;

public interface Deserializer {
    void loadProject(FmmlxDiagramCommunicator fmmlxDiagramCommunicator);

    void getAllDiagramElement(Integer newDiagramID);

    void alignCoordinate2(FmmlxDiagram diagram);

    String getProjectName();

    void alignCoordinate(String file, FmmlxDiagramCommunicator communicator);
}
