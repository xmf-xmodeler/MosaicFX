package tool.clients.serializer;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

public class Deserializer {
    public Deserializer() {
    }

    public void loadProject(String file, FmmlxDiagramCommunicator fmmlxDiagramCommunicator) {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager(file);
        String projectName = projectXmlManager.getProjectName();
        DiagramXmlManager diagramXmlManager = new DiagramXmlManager(file);
        Vector<String> diagramNames = diagramXmlManager.getAllDiagrams();
        fmmlxDiagramCommunicator.loadProjectNameFromXml(projectName, diagramNames, file);
    }

    public boolean checkFileExist(String file){
        return Files.exists(Paths.get(file));
    }

    public void getAllDiagramElement(String file, Integer newDiagramID) {
        if(checkFileExist(file)){
            LogXmlManager logXmlManager = new LogXmlManager(file);
            logXmlManager.reproduceFromLog(newDiagramID);

            System.out.println("recreate all objects : finished ");
        }
    }

    public void alignCoordinate(String file, String diagramName, FmmlxDiagramCommunicator communicator) {
        if(checkFileExist(file)) {
            ObjectXmlManager objectXmlManager = new ObjectXmlManager(file);
            objectXmlManager.alignObjects(diagramName, communicator);
            EdgeXmlManager edgeXmlManager = new EdgeXmlManager(file);
            edgeXmlManager.alignEdges(diagramName, communicator);
            LabelXmlManager labelXmlManager = new LabelXmlManager(file);
            labelXmlManager.alignLabel(diagramName, communicator);
        }
    }
}
