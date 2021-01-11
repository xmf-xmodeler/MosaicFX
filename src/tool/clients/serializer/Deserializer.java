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

    public void alignObjectsCoordinate(String file, String diagramName, FmmlxDiagramCommunicator communicator) {
        if(checkFileExist(file)) {
            ObjectXmlManager objectXmlManager = new ObjectXmlManager(file);
            objectXmlManager.alignObjects(diagramName, communicator);
        }
    }

    public void alignEdgesAndLabelsCoordinate(FmmlxDiagram diagram) {
        if(diagramInXmlExists(diagram)){
            EdgeXmlManager edgeXmlManager = new EdgeXmlManager(diagram.getFilePath());
            edgeXmlManager.alignEdges(diagram);
            LabelXmlManager labelXmlManager = new LabelXmlManager(diagram.getFilePath());
            labelXmlManager.alignLabel(diagram);
        }
    }

    private boolean diagramInXmlExists(FmmlxDiagram diagram) {
        DiagramXmlManager diagramXmlManager = new DiagramXmlManager(diagram.getFilePath());
        Vector<String> diagrams = diagramXmlManager.getAllDiagrams();
        for (String diagramLabel : diagrams) {
            if(diagram.getDiagramLabel().equals(diagramLabel)){
                return true;
            }
        }
        return false;
    }
}
