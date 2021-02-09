package tool.clients.serializer;

import org.w3c.dom.Element;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.serializer.interfaces.Serializer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Vector;

public class FmmlxSerializer implements Serializer {
    private final XmlHandler xmlHandler;
    private final String file;

    public FmmlxSerializer(String file) throws TransformerException, ParserConfigurationException {
        this.file = initUserXMLFile(file);
        this.xmlHandler = new XmlHandler(this.file);
    }

    @Override
    public String initUserXMLFile(String file) throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        return xmlCreator.create(file);
    }

    @Override
    public void saveAsXml(String diagramPath, String initLabel, FmmlxDiagramCommunicator communicator) throws TimeOutException, TransformerException {
        this.clearAllData();
        int saveLogCount = 0;

        Vector<Integer> diagramIds = FmmlxDiagramCommunicator.getCommunicator().getAllDiagramIDs(diagramPath);
        for(Integer id :diagramIds){
            String diagramLabel = communicator.createLabelFromInitLabel(initLabel, id);
            saveProject(diagramPath);
            saveDiagram(diagramLabel, diagramPath, id);
            if(saveLogCount==0){
                saveLog(id, communicator);
            }
            saveLogCount++;
        }
        this.xmlHandler.flushData();
    }

    @Override
    public void save(String diagramPath, String label, Integer id, FmmlxDiagramCommunicator communicator)  {
        if(diagramPath!=null && diagramPath.length()>0){
            try {
                Vector<Integer> diagramIds = FmmlxDiagramCommunicator.getCommunicator().getAllDiagramIDs(diagramPath);
                int saveLogCount = 0;
                for(Integer id_tmp :diagramIds){
                    String diagramLabel = communicator.createLabelFromInitLabel(label, id_tmp);
                    saveProject(diagramPath);
                    saveDiagram(diagramLabel, diagramPath, id_tmp);
                    if(saveLogCount==0){
                        saveLog(id_tmp, communicator);
                    }
                    saveLogCount++;
                }
                xmlHandler.flushData();
            } catch (TransformerException | TimeOutException e) {
                e.printStackTrace();
            }
            System.out.println(label + " saved");
        } else {
            communicator.saveXmlFile(diagramPath, id);
        }
    }

    public void saveProject(String projectPath)  {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager(this.xmlHandler);
        Element diagramsElement = projectXmlManager.getDiagramsElement();
        Element projectElement =  projectXmlManager.createProjectElement(projectPath);

        if(!projectXmlManager.projectIsExist(projectPath)) {
            projectXmlManager.add(diagramsElement, projectElement);
        }
    }

    public void saveDiagram(String label, String path, Integer id) throws TransformerException, TimeOutException {
        if(checkFileExist(xmlHandler.getSourcePath())) {
            DiagramXmlManager diagramXmlManager = new DiagramXmlManager(this.xmlHandler);
            Element diagramsElement = diagramXmlManager.getDiagramsElement();
            Element diagramElement = diagramXmlManager.createDiagramElement(label, path);
            if (diagramXmlManager.isExist(label)) {
                diagramXmlManager.remove(label);
            }
            saveComponentsIntoDiagram(diagramElement, id);
            diagramXmlManager.add(diagramsElement, diagramElement);

        }
    }

    private void saveComponentsIntoDiagram(Element diagramElement, Integer id) {
        saveObjectsIntoDiagram(id, diagramElement);
        //saveEdges(diagramElement, diagram);
        //saveLabels(diagramElement, diagram);
    }

    public void saveObjectsIntoDiagram(int id, Element diagramElement) {
        ObjectXmlManager objectXmlManager = new ObjectXmlManager(this.xmlHandler);
        
        HashMap<String, HashMap<String, Object>> result = FmmlxDiagramCommunicator.getCommunicator().getAllObjectPositions(id);
        for(String path : result.keySet()) {
            Element objectElement = objectXmlManager.createObjectElement(path,
                    (Integer) result.get(path).get("x"),
                    (Integer) result.get(path).get("y"),
                    (Boolean) result.get(path).get("hidden"));
            objectXmlManager.add(diagramElement, objectElement);
        }
    }

    public void saveEdges(Element diagramElement, FmmlxDiagram diagram)  {
        Vector<Edge<?>> edges = diagram.getEdges();
        EdgeXmlManager edgeXmlManager = new EdgeXmlManager(this.xmlHandler);
        Element edgeElement;

        for(Edge<?> edge : edges){
            if(edge instanceof FmmlxAssociation){
                edgeElement = edgeXmlManager.createAssociationXmlElement(diagram, (FmmlxAssociation) edge);
            } else if(edge instanceof DelegationEdge){
                edgeElement = edgeXmlManager.createDelegationXmlElement(diagram, (DelegationEdge) edge);
            } else if(edge instanceof FmmlxLink){
                edgeElement = edgeXmlManager.createLinkXmlElement(diagram, (FmmlxLink) edge);
            } else if(edge instanceof InheritanceEdge){
                edgeElement = edgeXmlManager.createInheritanceXmlElement(diagram, (InheritanceEdge) edge);
            } else if(edge instanceof RoleFillerEdge){
                edgeElement = edgeXmlManager.createRoleFillerEdgeXmlElement(diagram, (RoleFillerEdge) edge);
            }
            else {
                edgeElement = null;
            }
            if(edgeElement!= null){
                edgeXmlManager.add(diagramElement, edgeElement);
            }
        }
    }

    public void saveLabels(Element diagramElement, FmmlxDiagram diagram) {
        LabelXmlManager labelXmlManager = new LabelXmlManager(this.xmlHandler);
        Vector<DiagramEdgeLabel> labels = diagram.getLabels();

        for (DiagramEdgeLabel label : labels){
            Element labelElement = labelXmlManager.createLabelElement(diagram, label);
            labelXmlManager.add(diagramElement, labelElement);
        }
    }

    public void saveLog(Integer diagramID, FmmlxDiagramCommunicator communicator) throws TimeOutException {
        LogXmlManager logXmlManager = new LogXmlManager(this.xmlHandler);
        logXmlManager.clearLog();
        Element logsElement = logXmlManager.getLogs();
        FaXML protocol = communicator.getDiagramData(diagramID);

        Vector<FaXML> logs = protocol.getChildren();
        for (FaXML log : logs){
            Element newLogElement = logXmlManager.createNewLogFromFaXML(log);
            logXmlManager.add(logsElement, newLogElement);
        }
    }

    @Override
    public boolean checkFileExist(String file) {
        return Files.exists(Paths.get(file));
    }

    @Override
    public void clearAllData() {
        Element Root = xmlHandler.getRoot();
        Element logsElement = xmlHandler.getChildWithTag(Root, XmlConstant.TAG_NAME_LOGS);
        Element projectsElement = xmlHandler.getChildWithTag(Root, XmlConstant.TAG_NAME_PROJECTS);
        Element categoriesElement = xmlHandler.getChildWithTag(Root, XmlConstant.TAG_NAME_CATEGORIES);
        Element diagramsElement = xmlHandler.getChildWithTag(Root, XmlConstant.TAG_NAME_DIAGRAMS);
        xmlHandler.removeAllChildren(logsElement);
        xmlHandler.removeAllChildren(projectsElement);
        xmlHandler.removeAllChildren(categoriesElement);
        xmlHandler.removeAllChildren(diagramsElement);
    }
}