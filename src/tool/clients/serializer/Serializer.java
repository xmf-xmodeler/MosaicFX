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
    private final XmlHandler xmlHandler;

    public Serializer(String file) {
        try {
            initUserXMLFile(file);
        } catch (TransformerException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        this.xmlHandler = XmlHandler.getInstance(file);
    }

    @Override
    public void saveAsXml(FmmlxDiagram diagram, String file, int saveLogCount) throws TransformerException, ParserConfigurationException {
        diagram.setFilePath(file);
        saveProject(diagram);
        saveDiagram(diagram);
        if(saveLogCount==0){
            saveLog(diagram);
        }
    }

    private void saveDiagram(FmmlxDiagram diagram) throws TransformerException {
        if(checkFileExist(xmlHandler.getSourcePath())) {
            DiagramXmlManager diagramXmlManager = new DiagramXmlManager(this.xmlHandler);
            Element diagramElement = diagramXmlManager.createDiagramElement(diagram);

            if (diagramXmlManager.isExist(diagram)) {
                diagramXmlManager.remove(diagram);
            }
            diagramXmlManager.add(diagramElement);
            saveObjects(diagram);
            saveEdges(diagram);
            saveLabels(diagram);
            saveLog(diagram);
        }
    }


    private void saveLabels(FmmlxDiagram diagram) {
        LabelXmlManager labelXmlManager = new LabelXmlManager(this.xmlHandler);
        Vector<DiagramEdgeLabel> labels = diagram.getLabels();

        for (DiagramEdgeLabel label : labels){
            Element labelElement = labelXmlManager.createLabelElement(diagram, label);
            labelXmlManager.add(labelElement);
        }
    }

    private void saveEdges(FmmlxDiagram diagram) throws TransformerException {

        Vector<Edge> edges = diagram.getEdges();
        EdgeXmlManager edgeXmlManager = new EdgeXmlManager(this.xmlHandler);
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

    private void saveObjects(FmmlxDiagram diagram) {
        ObjectXmlManager objectXmlManager = new ObjectXmlManager(this.xmlHandler);
        Vector<FmmlxObject> objects = diagram.getObjects();

        for (FmmlxObject object : objects){
            Element objectElement = objectXmlManager.createObjectElement(diagram, object);
            objectXmlManager.add(objectElement);
        }
    }

    private void saveProject(FmmlxDiagram diagram) throws TransformerException {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager(this.xmlHandler);
        Element projectElement =  projectXmlManager.createProjectElement(diagram);

        if(!projectXmlManager.projectIsExist(diagram.getPackagePath())) {
            projectXmlManager.add(projectElement);
        }

    }

    private void initUserXMLFile(String file) throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        xmlCreator.create(file);
    }

    private void saveLog(FmmlxDiagram diagram) {
        try {
            LogXmlManager logXmlManager = new LogXmlManager(this.xmlHandler);
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

    public void save(FmmlxDiagram diagram) throws TransformerException {
        if(diagram.getFilePath()!=null && diagram.getFilePath().length()>0){
            saveDiagram(diagram);
            System.out.println(diagram.getDiagramLabel() + " saved");
        } else {
            diagram.getComm().saveXmlFile(diagram);
        }
    }
}