package tool.clients.serializer;

import javafx.util.Pair;
import org.w3c.dom.Element;
import tool.clients.fmmlxdiagrams.FaXML;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.TimeOutException;
import tool.clients.xmlManipulator.XmlCreator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

/*This Class is an abstract layer over XML to allow saving the FmmlxDiagram data into XML file
*This Class associated with another two classes, which is XmlHandler and the xml-filePath
*   XMLHandler is can be considered an interface to manipulate XML-document (document-communicator).
*   while filepath is an address where the XML file will be saved
*And this Class contains the main method to save FmmlxDiagram data into xml-File
*/
public class FmmlxSerializer  {
    private final XmlManager xmlManager;
    private final String filePath;


    public FmmlxSerializer(String filePath) throws TransformerException, ParserConfigurationException {
        this.filePath = initUserXMLFile(filePath);
        this.xmlManager = new XmlManager(this.filePath);
    }

    //This methode is used to make xml files according to the address in parameters
    //and it will be automatically called when FmmlxSerializer-Class created
    public String initUserXMLFile(String file) throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        return xmlCreator.createXml(file);
    }

    //This Method is the main "save as" method
    /*This method is divided into 4 main processes
    *   This method called in xmf after file-path created using file-chooser
    *   and then saving diagram-data process (xml-document manipulation)
    *   and then saving diagram-Log process (xml-document manipulation)
    *   at the end, flush xml-document into the file*/
    public void saveAsXml(String packagePath, String initLabel, FmmlxDiagramCommunicator communicator) throws TimeOutException {
        try{
            this.clearAllData();
            Vector<Integer> diagramIds = FmmlxDiagramCommunicator.getCommunicator().getAllDiagramIDs(packagePath);
            Collections.sort(diagramIds);
            for(Integer id :diagramIds){
                String diagramLabel = communicator.createLabelFromInitLabel(initLabel, id);
                saveProject(packagePath);
                saveDiagram(diagramLabel, packagePath, id);
            }
            saveLog(diagramIds.get(0), communicator);
            this.xmlManager.flushData();
            communicator.fileSaved(filePath, diagramIds.get(0));
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    //Analog to "save as" methode without created file-path using file chooser because the file-path is already exists
    public void save(String packagePath, String filePath, String label, Integer id, FmmlxDiagramCommunicator communicator)  {
        System.out.println(label);
        if(filePath!=null && filePath.length()>0 && checkFileExist(xmlManager.getSourcePath())){
            try {
                Vector<Integer> diagramIds = FmmlxDiagramCommunicator.getCommunicator().getAllDiagramIDs(packagePath);
                Collections.sort(diagramIds);
                for(Integer id_tmp :diagramIds){
                    String diagramLabel = communicator.createLabelFromInitLabel(label, id_tmp);
                    saveProject(packagePath);
                    saveDiagram(diagramLabel, packagePath, id_tmp);
                }
                saveLog(diagramIds.get(0), communicator);
                xmlManager.flushData();
                communicator.fileSaved(this.filePath, diagramIds.get(0));
            } catch (TransformerException | TimeOutException e) {
                e.printStackTrace();
            }
            System.out.println(label + " saved");
        } else {
            communicator.saveXmlFile2(packagePath, id);
        }
    }

    //Part of saving-process
    //This methode save the Diagram-ProjectPath.
    //This method makes XML-node containing ProjectPath-data and then add the node into xml-document
    public void saveProject(String projectPath)  {
        Element projectElement =  xmlManager.createProjectElement(projectPath);

        if(!xmlManager.projectIsExist(projectPath)) {
            xmlManager.addProject(projectElement);
        }
    }

    //Part of saving-process
    //This methode save the Diagram-Data.
    //This method makes the main XML-node containing Diagram-data and then add the node into xml-document
    public void saveDiagram(String label, String diagramPath, Integer id) throws TransformerException, TimeOutException {
        if(checkFileExist(xmlManager.getSourcePath())) {
            Element diagramsElement = xmlManager.getDiagramsElement();
            Element diagramElement = xmlManager.createDiagramElement(label, diagramPath);
            if (xmlManager.isDiagramExist(label)) {
                xmlManager.removeDiagram(label);
            }
            saveComponentsIntoDiagram(diagramElement, diagramPath, id);
            xmlManager.addDiagram(diagramsElement, diagramElement);
        }
    }

    //Part of saveDiagram-process
    //this method contains the steps of saving process in more detail
    //All steps create the XML-Element and add this element as a child into its parent (Diagram Node)
    private void saveComponentsIntoDiagram(Element ParentElement, String diagramPath, Integer id) {
        saveObjectsIntoDiagram(id, ParentElement);
        saveEdgesIntoDiagram(id, diagramPath, ParentElement);
        saveLabels(id, ParentElement);
    }

    private void saveLabels(Integer id, Element diagramElement) {
        HashMap<String, HashMap<String, Object>> result = FmmlxDiagramCommunicator.getCommunicator().getAllLabelPositions(id);

        for (String key : result.keySet()){
            Element labelElement = xmlManager.createLabelElement(key,
                    (float) result.get(key).get("x"),
                    (float) result.get(key).get("y"));
            xmlManager.addLabel(diagramElement, labelElement);
        }
    }

    public void saveObjectsIntoDiagram(int id, Element diagramElement) {
        HashMap<String, HashMap<String, Object>> result = FmmlxDiagramCommunicator.getCommunicator().getAllObjectPositions(id);
        for(String path : result.keySet()) {
            Element objectElement = xmlManager.createObjectElement(path,
                    (Integer) result.get(path).get("x"),
                    (Integer) result.get(path).get("y"),
                    (Boolean) result.get(path).get("hidden"));
            xmlManager.addObject(diagramElement, objectElement);
        }
    }

    public void saveEdgesIntoDiagram(int id, String diagramPath, Element parentElement)  {
        HashMap<String, HashMap<String, Object>> edgesInfo = FmmlxDiagramCommunicator.getCommunicator().getAllEdgePositions(id);

        for (String key : edgesInfo.keySet()) {
            Element edgeElement;
            Pair<String, String> typeAndRef = getTypeAndRefFromKey(key);
            switch (typeAndRef.getKey()) {
                case SerializerConstant.EdgeType.ASSOCIATION:
                    edgeElement = xmlManager.createAssociationXmlElement(typeAndRef.getValue(), diagramPath, edgesInfo.get(key));
                    break;
                case SerializerConstant.EdgeType.LINK:
                    edgeElement = xmlManager.createLinkXmlElement(typeAndRef.getValue(), edgesInfo.get(key));
                    break;
                case SerializerConstant.EdgeType.INHERITANCE:
                    edgeElement = xmlManager.createInheritanceXmlElement(typeAndRef.getValue(), edgesInfo.get(key));
                    break;
                case SerializerConstant.EdgeType.ROLEFILLEREDGE:
                    edgeElement = xmlManager.createRoleFillerEdgeXmlElement(typeAndRef.getValue(), edgesInfo.get(key));
                    break;
                case SerializerConstant.EdgeType.DELEGATION:
                    edgeElement = xmlManager.createDelegationXmlElement(typeAndRef.getValue(), edgesInfo.get(key));
                    break;
                default:
                    edgeElement = null;
                    break;
            }
            if(edgeElement != null) {
                xmlManager.addEdge(parentElement, edgeElement);
            }
        }
    }

    public void saveLog(Integer diagramID, FmmlxDiagramCommunicator communicator) throws TimeOutException {
        xmlManager.clearLog();
        Element logsElement = xmlManager.getLogs();
        FaXML protocol = communicator.getDiagramData(diagramID);
        
        System.err.println("protocol:" + protocol.getChildren().size() + " :protocol");

        Vector<FaXML> logs = protocol.getChildren();
        for (FaXML log : logs){
            Element newLogElement = xmlManager.createNewLogFromFaXML(log);
            xmlManager.addLog(logsElement, newLogElement);
        }
    }

    private Pair<String, String> getTypeAndRefFromKey(String key) {
        String[] keyPair = key.split(" ");
        String type = keyPair[0];
        switch (type) {
            case "DelegationMapping:":
                return new Pair<>(SerializerConstant.EdgeType.DELEGATION, key);
            case "InheritanceMapping:":
                return new Pair<>(SerializerConstant.EdgeType.INHERITANCE, key);
            case "AssociationLinkMapping:":
                return new Pair<>(SerializerConstant.EdgeType.LINK, key);
            case "RoleFillerMapping:":
                return new Pair<>(SerializerConstant.EdgeType.ROLEFILLEREDGE, key);
        }
        return new Pair<>(SerializerConstant.EdgeType.ASSOCIATION, key);
    }

    public boolean checkFileExist(String file) {
        return Files.exists(Paths.get(file));
    }

    public void clearAllData() {
        Element Root = xmlManager.getRoot();
        Element logsElement = xmlManager.getChildWithTag(Root, SerializerConstant.TAG_NAME_LOGS);
        Element projectsElement = xmlManager.getChildWithTag(Root, SerializerConstant.TAG_NAME_PROJECTS);
        Element categoriesElement = xmlManager.getChildWithTag(Root, SerializerConstant.TAG_NAME_CATEGORIES);
        Element diagramsElement = xmlManager.getChildWithTag(Root, SerializerConstant.TAG_NAME_DIAGRAMS);
        xmlManager.removeAllChildren(logsElement);
        xmlManager.removeAllChildren(projectsElement);
        xmlManager.removeAllChildren(categoriesElement);
        xmlManager.removeAllChildren(diagramsElement);
    }
}
