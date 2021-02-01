package tool.clients.serializer;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.serializer.interfaces.Deserializer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

public class FmmlxDeserializer implements Deserializer {
    private final XmlHandler xmlHandler;

    public FmmlxDeserializer(XmlHandler xmlHandler) {
        this.xmlHandler = xmlHandler;
    }

    @Override
    public void loadProject(FmmlxDiagramCommunicator fmmlxDiagramCommunicator) {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager(this.xmlHandler);
        String projectName = projectXmlManager.getProjectName();
        DiagramXmlManager diagramXmlManager = new DiagramXmlManager(this.xmlHandler);
        Vector<String> diagramNames = diagramXmlManager.getAllDiagrams();
        fmmlxDiagramCommunicator.loadProjectNameFromXml(projectName, diagramNames, this.xmlHandler.getSourcePath());
    }
    @Override
    public void getAllDiagramElement(Integer newDiagramID) {
        if(Files.exists(Paths.get(xmlHandler.getSourcePath()))){
            LogXmlManager logXmlManager = new LogXmlManager(this.xmlHandler);
            logXmlManager.reproduceFromLog(newDiagramID);
            System.out.println("re-create all objects : finished ");
        }
    }

    @Deprecated
    private void alignObjectsCoordinate(String file, String diagramName, FmmlxDiagramCommunicator communicator) {
        if(Files.exists(Paths.get(file))) {
            ObjectXmlManager objectXmlManager = new ObjectXmlManager(this.xmlHandler);
            //objectXmlManager.alignObjects(diagramName, communicator);
        }
    }

    @Override
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

    @Override
    public String getProjectName() {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager(this.xmlHandler);
        return projectXmlManager.getProjectName();
    }
}
