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

    public Serializer() {
    }

    @Override
    public void saveAsXml(FmmlxDiagram diagram, String file) throws TransformerException, ParserConfigurationException {
        initUserXMLFile(file);
        diagram.setFilePath(file);
        saveProject(diagram, file);
        saveDiagram(diagram, file);
        saveLog(diagram, file);
    }


    private void saveDiagram(FmmlxDiagram diagram, String file) throws TransformerException {
        if(checkFileExist(file)) {
            DiagramXmlManager diagramXmlManager = new DiagramXmlManager(file);
            Element diagramElement = diagramXmlManager.createDiagramElement(diagram);

            if (diagramXmlManager.isExist(diagram)) {
                diagramXmlManager.remove(diagram);
            }
            diagramXmlManager.add(diagramElement);
            saveObjects(diagram, file);
            saveEdges(diagram, file);
            saveLabels(diagram, file);
        }
    }


    private void saveLabels(FmmlxDiagram diagram, String file) {
        LabelXmlManager labelXmlManager = new LabelXmlManager(file);
        Vector<DiagramEdgeLabel> labels = diagram.getLabels();

        for (DiagramEdgeLabel label : labels){
            Element labelElement = labelXmlManager.createLabelElement(diagram, label);
            labelXmlManager.add(labelElement);
        }
    }

    private void saveEdges(FmmlxDiagram diagram, String file) throws TransformerException {

        Vector<Edge> edges = diagram.getEdges();
        EdgeXmlManager edgeXmlManager = new EdgeXmlManager(file);
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

    private void saveObjects(FmmlxDiagram diagram, String file) {
        ObjectXmlManager objectXmlManager = new ObjectXmlManager(file);

        Vector<FmmlxObject> objects = diagram.getObjects();

        for (FmmlxObject object : objects){
            Element objectElement = objectXmlManager.createObjectElement(diagram, object);
            objectXmlManager.add(objectElement);
        }
    }

    private void saveProject(FmmlxDiagram diagram, String file) throws TransformerException {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager(file);
        Element projectElement =  projectXmlManager.createProjectElement(diagram);

        if(projectXmlManager.isExist()) {
            projectXmlManager.removeAll();
        }

        projectXmlManager.add(projectElement);
    }

    private void initUserXMLFile(String file) throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        xmlCreator.create(file);
    }

    private void saveLog(FmmlxDiagram diagram, String file) {
        try {
            LogXmlManager logXmlManager = new LogXmlManager(file);
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

    private boolean checkFileExist(String file) {
        return Files.exists(Paths.get(file));
    }
}
