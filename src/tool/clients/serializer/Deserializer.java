package tool.clients.serializer;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;


public class Deserializer {
	

    public void loadProject(String file, FmmlxDiagramCommunicator fmmlxDiagramCommunicator) {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager(file);
        String projectName = projectXmlManager.getProjectName();
        fmmlxDiagramCommunicator.loadProjectNameFromXml(projectName);

    }

    private Vector<String> loadAllDiagrams(String file){
        DiagramXmlManager diagramXmlManager = new DiagramXmlManager(file);
        return diagramXmlManager.getAllDiagrams();
    }

    public void getAllDiagramElement(FmmlxDiagram fmmlxDiagram){
        String file = fmmlxDiagram.getFilePath();
        if(checkFileExist(file)){
            LogXmlManager logXmlManager = new LogXmlManager(fmmlxDiagram, file);
            logXmlManager.reproduceFromLog(fmmlxDiagram.getDiagramLabel());
            fmmlxDiagram.updateDiagram();
        }
    }

    public void alignCoordinate(FmmlxDiagram fmmlxDiagram) {
        String file = fmmlxDiagram.getFilePath();
    	if(checkFileExist(file)) {

    		ObjectXmlManager objectXmlManager = new ObjectXmlManager(file);
            objectXmlManager.alignObjects(fmmlxDiagram);
            EdgeXmlManager edgeXmlManager = new EdgeXmlManager(file);
            edgeXmlManager.alignEdges(fmmlxDiagram);
            LabelXmlManager labelXmlManager = new LabelXmlManager(file);
            labelXmlManager.alignLabel(fmmlxDiagram);
    	}
    }

    public boolean checkFileExist(String file){
        return Files.exists(Paths.get(file));
    }
}
