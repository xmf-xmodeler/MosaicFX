package tool.clients.serializer;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

public class Deserializer {
    private final XmlHandler xmlHandler;

    public Deserializer(XmlHandler xmlHandler) {
        this.xmlHandler = xmlHandler;
    }

    public void loadProject(FmmlxDiagramCommunicator fmmlxDiagramCommunicator) {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager(this.xmlHandler);
        String projectName = projectXmlManager.getProjectName();
        DiagramXmlManager diagramXmlManager = new DiagramXmlManager(this.xmlHandler);
        Vector<String> diagramNames = diagramXmlManager.getAllDiagrams();
        fmmlxDiagramCommunicator.loadProjectNameFromXml(projectName, diagramNames, this.xmlHandler.getSourcePath());
    }

    public boolean checkFileExist(String file){
        return Files.exists(Paths.get(file));
    }

    public void getAllDiagramElement(Integer newDiagramID) {
        if(checkFileExist(xmlHandler.getSourcePath())){
            LogXmlManager logXmlManager = new LogXmlManager(this.xmlHandler);
            logXmlManager.reproduceFromLog(newDiagramID);
            System.out.println("re-create all objects : finished ");
        }
    }

    @Deprecated
    public void alignObjectsCoordinate(String file, String diagramName, FmmlxDiagramCommunicator communicator) {
        if(checkFileExist(file)) {
            ObjectXmlManager objectXmlManager = new ObjectXmlManager(this.xmlHandler);
            //objectXmlManager.alignObjects(diagramName, communicator);
        }
    }

    public void alignCoordinate(FmmlxDiagram diagram) {
        if(diagramInXmlExists(diagram)){
            ObjectXmlManager objectXmlManager = new ObjectXmlManager(this.xmlHandler);
            objectXmlManager.alignObjects(diagram);
            EdgeXmlManager edgeXmlManager = new EdgeXmlManager(this.xmlHandler);
            edgeXmlManager.alignEdges(diagram);
            LabelXmlManager labelXmlManager = new LabelXmlManager(this.xmlHandler);
            labelXmlManager.alignLabel(diagram);
        }
    }

    private boolean diagramInXmlExists(FmmlxDiagram diagram) {
        DiagramXmlManager diagramXmlManager = new DiagramXmlManager(this.xmlHandler);
        Vector<String> diagrams = diagramXmlManager.getAllDiagrams();
        for (String diagramLabel : diagrams) {
            if(diagram.getDiagramLabel().equals(diagramLabel)){
                return true;
            }
        }
        return false;
    }

    public String getProjectName() {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager(this.xmlHandler);
        return projectXmlManager.getProjectName();
    }
}
