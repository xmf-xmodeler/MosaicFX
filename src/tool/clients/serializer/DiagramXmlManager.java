package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.serializer.interfaces.IXmlManager;

import javax.xml.transform.TransformerException;
import java.util.List;

public class DiagramXmlManager implements IXmlManager {
    private final XmlHandler xmlHandler;

    public DiagramXmlManager(){
        this.xmlHandler = new XmlHandler();
    }
    public DiagramXmlManager(String path){ this.xmlHandler = new XmlHandler(path);}

    public Node createDiagramNode(FmmlxDiagram fmmlxDiagram) {
        String label =fmmlxDiagram.getDiagramLabel();
        String path = fmmlxDiagram.getPackagePath();

        Element diagram = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_DIAGRAM);
        diagram.setAttribute(XmlConstant.ATTRIBUTE_LABEL, label);
        diagram.setAttribute(XmlConstant.ATTRIBUTE_PACKAGE_PATH, path);
        Node categories = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_CATEGORIES);
        Node owners = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_OWNERS);
        Node objects = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_OBJECTS);
        Node edges = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_EDGES);
        Node preferences = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_PREFERENCES);
        try {
            xmlHandler.addDiagramCategoriesElement(diagram, categories);
            xmlHandler.addDiagramOwnersElement(diagram, owners);
            xmlHandler.addDiagramObjectsElement(diagram, objects);
            xmlHandler.addDiagramEdgesElement(diagram, edges);
            xmlHandler.addDiagramPreferencesElement(diagram, preferences);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return diagram;
    }

    public boolean isExist(FmmlxDiagram diagram) {
        Node diagrams = xmlHandler.getDiagramsNode();

        NodeList diagramList = diagrams.getChildNodes();

        for(int i =0; i<diagramList.getLength(); i++){
            if(diagramList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) diagramList.item(i);
                if(element.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(diagram.getDiagramLabel())){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void add(Node node) {
        Node diagrams = xmlHandler.getDiagramsNode();
        try {
            xmlHandler.addDiagramElement(diagrams, node);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Node node) {

    }

    @Override
    public List<Node> getAll() {
        return null;
    }


    public void remove(FmmlxDiagram diagram) throws TransformerException {
        xmlHandler.removeDiagram(diagram);
    }

    public Node getDiagramsNode() {
        return xmlHandler.getDiagramsNode();
    }

    public List getAllDiagrams() {
        return null;
    }
}
