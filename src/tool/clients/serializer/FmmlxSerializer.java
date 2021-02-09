package tool.clients.serializer;

import javafx.util.Pair;
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
    public void save(String diagramPath, String filePath, String label, Integer id, FmmlxDiagramCommunicator communicator)  {
        if(filePath!=null && filePath.length()>0 && checkFileExist(xmlHandler.getSourcePath())){
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

    public void saveDiagram(String label, String diagramPath, Integer id) throws TransformerException, TimeOutException {
        if(checkFileExist(xmlHandler.getSourcePath())) {
            DiagramXmlManager diagramXmlManager = new DiagramXmlManager(this.xmlHandler);
            Element diagramsElement = diagramXmlManager.getDiagramsElement();
            Element diagramElement = diagramXmlManager.createDiagramElement(label, diagramPath);
            if (diagramXmlManager.isExist(label)) {
                diagramXmlManager.remove(label);
            }
            saveComponentsIntoDiagram(diagramElement, diagramPath, id);
            diagramXmlManager.add(diagramsElement, diagramElement);
        }
    }

    private void saveComponentsIntoDiagram(Element diagramElement, String diagramPath, Integer id) {
        saveObjectsIntoDiagram(id, diagramElement);
        saveEdgesIntoDiagram(id, diagramPath, diagramElement);
        saveLabels(id, diagramElement);
    }

    private void saveLabels(Integer id, Element diagramElement) {
        LabelXmlManager labelXmlManager = new LabelXmlManager(this.xmlHandler);

        HashMap<String, HashMap<String, Object>> result = FmmlxDiagramCommunicator.getCommunicator().getAllLabelPositions(id);

        for (String key : result.keySet()){
            Element labelElement = labelXmlManager.createLabelElement(key,
                    (float) result.get(key).get("x"),
                    (float) result.get(key).get("y"));
            labelXmlManager.add(diagramElement, labelElement);
        }
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

    public void saveEdgesIntoDiagram(int id, String diagramPath, Element diagramElement)  {
        EdgeXmlManager edgeXmlManager = new EdgeXmlManager(this.xmlHandler);
        HashMap<String, HashMap<String, Object>> edgesInfo = FmmlxDiagramCommunicator.getCommunicator().getAllEdgePositions(id);

        for (String key : edgesInfo.keySet()) {
            Element edgeElement;
            Pair<String, String> typeAndRef = getTypeAndRefFromKey(key);
            switch (typeAndRef.getKey()) {
                case XmlConstant.EdgeType.ASSOCIATION:
                    edgeElement = edgeXmlManager.createAssociationXmlElement(typeAndRef.getValue(), diagramPath, edgesInfo.get(key));
                    break;
                case XmlConstant.EdgeType.LINK:
                    edgeElement = edgeXmlManager.createLinkXmlElement(typeAndRef.getValue(), edgesInfo.get(key));
                    break;
                case XmlConstant.EdgeType.INHERITANCE:
                    edgeElement = edgeXmlManager.createInheritanceXmlElement(typeAndRef.getValue(), edgesInfo.get(key));
                    break;
                case XmlConstant.EdgeType.ROLEFILLEREDGE:
                    edgeElement = edgeXmlManager.createRoleFillerEdgeXmlElement(typeAndRef.getValue(), edgesInfo.get(key));
                    break;
                case XmlConstant.EdgeType.DELEGATION:
                    edgeElement = edgeXmlManager.createDelegationXmlElement(typeAndRef.getValue(), edgesInfo.get(key));
                    break;
                default:
                    edgeElement = null;
                    break;
            }
            if(edgeElement != null) {
                edgeXmlManager.add(diagramElement, edgeElement);
            }
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

    private Pair<String, String> getTypeAndRefFromKey(String key) {
        String[] keyPair = key.split(" ");
        String type = keyPair[0];
        switch (type) {
            case "DelegationMapping:":
                return new Pair<>(XmlConstant.EdgeType.DELEGATION, keyPair[1]);
            case "InheritanceMapping:":
                return new Pair<>(XmlConstant.EdgeType.INHERITANCE, keyPair[1]);
            case "AssociationLinkMapping:":
                return new Pair<>(XmlConstant.EdgeType.LINK, keyPair[1]);
            case "RoleFillerMapping:":
                return new Pair<>(XmlConstant.EdgeType.ROLEFILLEREDGE, keyPair[1]);
        }
        return new Pair<>(XmlConstant.EdgeType.ASSOCIATION, key);
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