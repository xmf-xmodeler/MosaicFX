package tool.clients.serializer;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Deserializer {
	

    public void loadState(String path, FmmlxDiagramCommunicator fmmlxDiagramCommunicator) {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager(path);
        String projectName = projectXmlManager.getProjectName();
        fmmlxDiagramCommunicator.loadProjectNameFromXml(projectName);
        loadAllDiagram();
    }

    private void loadAllDiagram(){
        DiagramXmlManager diagramXmlManager = new DiagramXmlManager();
        List<String> diagramList = diagramXmlManager.getAllDiagrams();
    }

    public void getAllDiagramElement(FmmlxDiagram fmmlxDiagram){
        LogXmlManager logXmlManager = new LogXmlManager(fmmlxDiagram);
        logXmlManager.reproduceFromLog(fmmlxDiagram.getDiagramLabel());
        fmmlxDiagram.updateDiagram();
    }

    public void alignCoordinate(FmmlxDiagram fmmlxDiagram) {
    	if(checkFileExist()) {
    		ObjectXmlManager objectXmlManager = new ObjectXmlManager();
            objectXmlManager.alignObjects(fmmlxDiagram);
            EdgeXmlManager edgeXmlManager = new EdgeXmlManager();
            edgeXmlManager.alignEdges(fmmlxDiagram);
            //LabelXmlManager labelXmlManager = new LabelXmlManager();
            //labelXmlManager.alignLabel(fmmlxDiagram);
    	}
    }
    
    public boolean checkFileExist(){
        return Files.exists(Paths.get(XmlCreator.path));
    }
}
