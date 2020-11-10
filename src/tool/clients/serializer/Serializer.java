package tool.clients.serializer;

import org.w3c.dom.Element;
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
                Element newLogElement = logXmlManager.createNewLogFromFaXML(log);
                logXmlManager.add(newLogElement);
            }
        } catch (TimeOutException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public void saveEdges(FmmlxDiagram diagram) throws TransformerException {

        Vector<Edge> edges = diagram.getEdges();
        EdgeXmlManager edgeXmlManager = new EdgeXmlManager();
        Element edgeElement;

        for(Edge edge : edges){
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
                edgeXmlManager.add(edgeElement);
            }
        }
    }

    public void saveObjects(FmmlxDiagram diagram) {
        ObjectXmlManager objectXmlManager = new ObjectXmlManager();

        Vector<FmmlxObject> objects = diagram.getObjects();

        for (FmmlxObject object : objects){
            Element objectElement = objectXmlManager.createObjectElement(diagram, object);
            objectXmlManager.add(objectElement);
        }
    }

    public boolean checkFileExist(){
        return Files.exists(Paths.get(XmlCreator.path));
    }
    
    public void saveDiagram(FmmlxDiagram diagram) throws TransformerException {
    	if(checkFileExist()) {
    		DiagramXmlManager diagramXmlManager = new DiagramXmlManager();
            Element diagramElement = diagramXmlManager.createDiagramElement(diagram);

            if (diagramXmlManager.isExist(diagram)) {
                diagramXmlManager.remove(diagram);
            }
            diagramXmlManager.add(diagramElement);
            saveObjects(diagram);
            saveEdges(diagram);
            saveLabels(diagram);
    	}
    }

    private void saveLabels(FmmlxDiagram diagram) {
		LabelXmlManager labelXmlManager = new LabelXmlManager();
		Vector<DiagramEdgeLabel> labels = diagram.getLabels();
		
		for (DiagramEdgeLabel label : labels){
            Element labelElement = labelXmlManager.createLabelElement(diagram, label);
            labelXmlManager.add(labelElement);
        }
		
	}

	private void saveProject(FmmlxDiagram diagram) throws TransformerException {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager();
        Element projectElement =  projectXmlManager.createProjectElement(diagram);

        if(projectXmlManager.isExist()) {
            projectXmlManager.removeAll();
        }

        projectXmlManager.add(projectElement);
    }
}
