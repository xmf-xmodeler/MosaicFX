package tool.clients.serializer;

import org.w3c.dom.Node;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.serializer.interfaces.ISerializer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Vector;


public class Serializer implements ISerializer {
    public static final String TAG = Serializer.class.getSimpleName();

    private static Serializer instance;

    public synchronized static Serializer getInstance() {
        if(instance==null){
            instance = new Serializer();
        }
        return instance;
    }

    public Serializer() {
    }

    @Override
    public void saveState(FmmlxDiagram diagram) throws TransformerException, ParserConfigurationException {
        initUserXMLFile();
        saveProject(diagram);
        saveDiagram(diagram);
        saveLog(diagram);
    }

    private void initUserXMLFile() throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        xmlCreator.create();
    }

    private void saveLog(FmmlxDiagram diagram) {
        try {
            LogXmlManager logXmlManager = new LogXmlManager();
            logXmlManager.clearLog();
            FaXML protocol = diagram.getComm().getDiagramData(diagram);

            Vector<FaXML> logs = protocol.getChildren();
            for (FaXML log : logs){
                Node newLogNode = logXmlManager.createNewLogFromFaXML(log);
                logXmlManager.add(newLogNode);
            }
        } catch (TimeOutException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private void saveEdges(FmmlxDiagram diagram) throws TransformerException {

        Vector<Edge> edges = diagram.getEdges();
        EdgeXmlManager edgeXmlManager = new EdgeXmlManager();
        Node edgeNode;

        for(Edge edge : edges){
            if(edge instanceof FmmlxAssociation){
                edgeNode = edgeXmlManager.createAssociationXmlNode(diagram, (FmmlxAssociation) edge);
            } else if(edge instanceof DelegationEdge){
                edgeNode = edgeXmlManager.createDelegationXmlNode(diagram, (DelegationEdge) edge);
            } else if(edge instanceof FmmlxLink){
                edgeNode = edgeXmlManager.createLinkXmlNode(diagram, (FmmlxLink) edge);
            } else if(edge instanceof InheritanceEdge){
                edgeNode = edgeXmlManager.createInheritanceXmlNode(diagram, (InheritanceEdge) edge);
            } else {
                edgeNode = null;
            }

            if(edgeNode!= null){
                edgeXmlManager.add(edgeNode);
            }
        }
    }

    private void saveObjects(FmmlxDiagram diagram) throws TransformerException {
        ObjectXmlManager objectXmlManager = new ObjectXmlManager();

        Vector<FmmlxObject> objects = diagram.getObjects();

        for (FmmlxObject object : objects){
            Node objectNode = objectXmlManager.createObject(diagram, object);

//            Vector<FmmlxOperation> operations = object.getOwnOperations();
//            for(FmmlxOperation operation : operations){
//                Node operationNode = objectXmlManager.createOperationXmlNode(diagram, object, operation);
//                objectXmlManager.addOperation(objectNode, operationNode);
//            }
//
//            Vector<FmmlxAttribute> attributes = object.getOwnAttributes();
//            for(FmmlxAttribute attribute : attributes){
//                Node attributeNode = objectXmlManager.createAttributeXmlNode(diagram, object, attribute);
//                objectXmlManager.addAttribute(objectNode, attributeNode);
//            }

            objectXmlManager.add(objectNode);
        }
    }

    private void saveDiagram(FmmlxDiagram diagram) throws TransformerException {
        DiagramXmlManager diagramXmlManager = new DiagramXmlManager();
        Node diagramNode = diagramXmlManager.createDiagramNode(diagram);

        if (diagramXmlManager.isExist(diagram)) {
            diagramXmlManager.remove(diagram);
        }
        diagramXmlManager.add(diagramNode);
        saveObjects(diagram);
        saveEdges(diagram);
    }

    private void saveProject(FmmlxDiagram diagram) throws TransformerException {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager();
        Node projectNode =  projectXmlManager.createProjectNode(diagram);

        if(projectXmlManager.isExist()) {
            projectXmlManager.remove();
        }

        projectXmlManager.add(projectNode);
    }

    @Override
    public void loadState(String path, FmmlxDiagramCommunicator fmmlxDiagramCommunicator) {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager(path);
        String projectName = projectXmlManager.getProjectName();
        fmmlxDiagramCommunicator.loadProjectFromXml(projectName);
        //TODO
    }
}
