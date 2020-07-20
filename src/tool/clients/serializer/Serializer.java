package tool.clients.serializer;

import org.w3c.dom.Node;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.serializer.interfaces.ISerializer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Vector;


public class Serializer implements ISerializer {
    private final XmlHandler xmlHandler;

    public Serializer() {
        this.xmlHandler = new XmlHandler();
    }

    @Override
    public synchronized void saveState(FmmlxDiagram diagram) throws TransformerException, ParserConfigurationException {
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
            FaXML protocol = diagram.getComm().getDiagramData(diagram);

            Vector<FaXML> logs = protocol.getChildren();
            for (FaXML log : logs){
                Node newLogNode = logXmlManager.createNewLogFromFaXML(log);
                logXmlManager.add(newLogNode);
            }
        } catch (TimeOutException e) {
            e.printStackTrace();
        }
    }

    private void saveEdges(FmmlxDiagram diagram) {

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
        Node diagramNode = diagramXmlManager.createDiagram(diagram);

        if (diagramXmlManager.isExist(diagram)) {
            diagramXmlManager.remove(diagram);
        }
        diagramXmlManager.add(diagramNode);
        saveObjects(diagram);
        saveEdges(diagram);
    }


    @Override
    public synchronized void loadState(int diagramId, String diagramLabel) {
        this.xmlHandler.getLatestSave(diagramId, diagramLabel);

        //TODO bind to Diagram
    }
}
