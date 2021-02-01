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
    public void saveAsXml(Vector<FmmlxDiagram> diagrams) throws TimeOutException, TransformerException {
        this.xmlHandler.clearAll();
        int saveLogCount = 0;

        for(FmmlxDiagram diagram : diagrams){
            diagram.setFilePath(this.file);
            saveProject(diagram);
            saveDiagram(diagram);
            if(saveLogCount==0){
                saveLog(diagram);
                saveObjects(diagram.getPackagePath(), file);
            }
            saveLogCount++;
        }
        this.xmlHandler.flushData();
    }

    @Override
    public void save(FmmlxDiagram diagram)  {
        if(diagram.getFilePath()!=null && diagram.getFilePath().length()>0){
            try {
                saveDiagram(diagram);
                xmlHandler.flushData();
            } catch (TransformerException | TimeOutException e) {
                e.printStackTrace();
            }
            System.out.println(diagram.getDiagramLabel() + " saved");
        } else {
            diagram.getComm().saveXmlFile(diagram);
        }
    }

    public void saveProject(FmmlxDiagram diagram)  {
        ProjectXmlManager projectXmlManager = new ProjectXmlManager(this.xmlHandler);
        Element projectElement =  projectXmlManager.createProjectElement(diagram);

        if(!projectXmlManager.projectIsExist(diagram.getPackagePath())) {
            projectXmlManager.add(projectElement);
        }
    }

    public void saveDiagram(FmmlxDiagram diagram) throws TransformerException, TimeOutException {
        if(checkFileExist(xmlHandler.getSourcePath())) {
            DiagramXmlManager diagramXmlManager = new DiagramXmlManager(this.xmlHandler);
            Element diagramElement = diagramXmlManager.createDiagramElement(diagram);

            if (diagramXmlManager.isExist(diagram)) {
                diagramXmlManager.remove(diagram);
            }
            diagramXmlManager.add(diagramElement);
//            saveObjects(diagram);
            saveEdges(diagram);
            saveLabels(diagram);
            saveLog(diagram);
        }
    }

    public void saveObjects(String diagramPath, String file) {
        ObjectXmlManager objectXmlManager = new ObjectXmlManager(this.xmlHandler);
        
        Vector<Integer> diagramIds = FmmlxDiagramCommunicator.getCommunicator().getAllDiagramIDs(diagramPath);
        for(Integer id : diagramIds) {
        	HashMap<String, HashMap<String, Object>> result = FmmlxDiagramCommunicator.getCommunicator().getAllObjectPositions(id);
        	for(String path : result.keySet()) {
        		Element objectElement = objectXmlManager.createObjectElement(path,
        				(Integer) result.get(path).get("x"),
        				(Integer) result.get(path).get("y"),
        				(Boolean) result.get(path).get("hidden"));
              objectXmlManager.add(objectElement);
        	}
        }    	
    }

    public void saveEdges(FmmlxDiagram diagram)  {
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

    public void saveLabels(FmmlxDiagram diagram) {
        LabelXmlManager labelXmlManager = new LabelXmlManager(this.xmlHandler);
        Vector<DiagramEdgeLabel> labels = diagram.getLabels();

        for (DiagramEdgeLabel label : labels){
            Element labelElement = labelXmlManager.createLabelElement(diagram, label);
            labelXmlManager.add(labelElement);
        }
    }

    public void saveLog(FmmlxDiagram diagram) throws TimeOutException {
        LogXmlManager logXmlManager = new LogXmlManager(this.xmlHandler);
        logXmlManager.clearLog();
        FaXML protocol = diagram.getComm().getDiagramData(diagram);

        Vector<FaXML> logs = protocol.getChildren();
        for (FaXML log : logs){
            Element newLogElement = logXmlManager.createNewLogFromFaXML(log);
            logXmlManager.add(newLogElement);
        }
    }

    @Override
    public boolean checkFileExist(String file) {
        return Files.exists(Paths.get(file));
    }

    @Override
    public void clearAllData() {
        xmlHandler.clearAll();
    }
}