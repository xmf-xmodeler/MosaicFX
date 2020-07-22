package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.serializer.interfaces.ISerializer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Vector;


public class Serializer implements ISerializer {

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

    private void saveObjects(FmmlxDiagram diagram) {
        ObjectXmlManager objectXmlManager = new ObjectXmlManager();
        Vector<FmmlxObject> objects = diagram.getObjects();
        for (FmmlxObject object : objects){
            Node objectNode = objectXmlManager.createObject(diagram, object);
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

    @Override
    public void loadState(String path) {
        DiagramXmlManager diagramXmlManager = new DiagramXmlManager(path);



        //TODO

    }
}
