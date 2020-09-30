package tool.clients.serializer;

import org.w3c.dom.Node;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.serializer.interfaces.ISerializer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import java.nio.file.Files;
import java.nio.file.Paths;
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
            LogXmlManager logXmlManager = new LogXmlManager(diagram);
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

    public void saveEdges(FmmlxDiagram diagram) throws TransformerException {

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

    public void saveObjects(FmmlxDiagram diagram) {
        ObjectXmlManager objectXmlManager = new ObjectXmlManager();

        Vector<FmmlxObject> objects = diagram.getObjects();

        for (FmmlxObject object : objects){
            Node objectNode = objectXmlManager.createObject(diagram, object);
            objectXmlManager.add(objectNode);
        }
    }

    public boolean checkFileExist(){
        return Files.exists(Paths.get(XmlCreator.path));
    }
    
    public void saveDiagram(FmmlxDiagram diagram) throws TransformerException {
    	if(checkFileExist()) {
    		DiagramXmlManager diagramXmlManager = new DiagramXmlManager();
            Node diagramNode = diagramXmlManager.createDiagramNode(diagram);

            if (diagramXmlManager.isExist(diagram)) {
                diagramXmlManager.remove(diagram);
            }
            diagramXmlManager.add(diagramNode);
            saveObjects(diagram);
            saveEdges(diagram);
            saveLabels(diagram);
    	}
    }

    private void saveLabels(FmmlxDiagram diagram) throws TransformerException {
		LabelXmlManager labelXmlManager = new LabelXmlManager();
		Vector<DiagramEdgeLabel> labels = diagram.getLabels();
		
		for (DiagramEdgeLabel label : labels){
            Node labelNode = labelXmlManager.createLabel(diagram, label);
            labelXmlManager.add(labelNode);
        }
		
	}

	private void saveProject(FmmlxDiagram diagram) throws TransformerException {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager();
        Node projectNode =  projectXmlManager.createProjectNode(diagram);

        if(projectXmlManager.isExist()) {
            projectXmlManager.removeAll();
        }

        projectXmlManager.add(projectNode);
    }
}
