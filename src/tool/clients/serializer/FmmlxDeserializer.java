package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
        String projectPath = projectXmlManager.getProjectPath();
        String projectName = projectXmlManager.getProjectName(projectPath);
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

    @Override
    public String getProjectName() {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager(this.xmlHandler);
        String projectPath = projectXmlManager.getProjectPath();
        return projectXmlManager.getProjectName(projectPath);
    }

    @Override
    public void alignCoordinate(String file, FmmlxDiagramCommunicator communicator) {
        if(Files.exists(Paths.get(file))) {
            Node diagrams = getDiagramsElement();
            NodeList diagramList = diagrams.getChildNodes();

            for (int i = 0; i < diagramList.getLength(); i++) {
                if (diagramList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element diagramElement = (Element) diagramList.item(i);
                    String label = diagramElement.getAttribute(XmlConstant.ATTRIBUTE_LABEL);
                    ObjectXmlManager objectXmlManager = new ObjectXmlManager(this.xmlHandler);
                    objectXmlManager.alignObjects(diagramElement, label, communicator);
                    EdgeXmlManager edgeXmlManager = new EdgeXmlManager(this.xmlHandler);
                    edgeXmlManager.alignEdges(diagramElement, label, communicator);
                }
            }
        }
    }

    @Deprecated
    @Override
    public void alignCoordinate2(FmmlxDiagram diagram) {
        Element diagrams = getDiagramsElement();
        NodeList diagramList = diagrams.getChildNodes();

        Element diagramElement = null;
        for (int i = 0 ; i< diagramList.getLength(); i++){
            if(diagramList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element tmp = (Element) diagramList.item(i);
                if (tmp.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(diagram.getDiagramLabel())){
                    diagramElement = tmp;
                }
            }
        }
        if(diagramElement!=null){
            //ObjectXmlManager objectXmlManager = new ObjectXmlManager(this.xmlHandler);
            //objectXmlManager.alignObjects(diagramNode, diagram);
//            EdgeXmlManager edgeXmlManager = new EdgeXmlManager(this.xmlHandler);
//            edgeXmlManager.alignEdges(diagramElement,diagram);
            LabelXmlManager labelXmlManager = new LabelXmlManager(this.xmlHandler);
            labelXmlManager.alignLabel(diagramElement, diagram);
        }
    }

    public Element getDiagramsElement(){
        Element Root = xmlHandler.getRoot();
        return xmlHandler.getChildWithTag(Root, XmlConstant.TAG_NAME_DIAGRAMS);
    }
}
