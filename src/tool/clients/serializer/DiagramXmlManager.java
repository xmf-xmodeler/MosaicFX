package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.serializer.interfaces.XmlManager;

import java.util.List;
import java.util.Vector;

public class DiagramXmlManager implements XmlManager {
    private final XmlHandler xmlHandler;

    protected DiagramXmlManager(XmlHandler xmlHandler){
        this.xmlHandler = xmlHandler;
    }

    public Element createDiagramElement(FmmlxDiagram fmmlxDiagram) {
        String label =fmmlxDiagram.getDiagramLabel();
        String path = fmmlxDiagram.getPackagePath();

        Element diagram = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_DIAGRAM);
        diagram.setAttribute(XmlConstant.ATTRIBUTE_LABEL, label);
        diagram.setAttribute(XmlConstant.ATTRIBUTE_PACKAGE_PATH, path);
        Element categories = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_CATEGORIES);
        Element owners = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_OWNERS);
        Element objects = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_OBJECTS);
        Element edges = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_EDGES);
        Element labels = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_LABELS);
        Element preferences = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_PREFERENCES);
        xmlHandler.addDiagramCategoriesElement(diagram, categories);
        xmlHandler.addDiagramOwnersElement(diagram, owners);
        xmlHandler.addDiagramObjectsElement(diagram, objects);
        xmlHandler.addDiagramEdgesElement(diagram, edges);
        xmlHandler.addDiagramLabelsElement(diagram, labels);
        xmlHandler.addDiagramPreferencesElement(diagram, preferences);
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
    public void add(Element element) {
        Node diagrams = xmlHandler.getDiagramsNode();
        xmlHandler.addDiagramElement(diagrams, element);
    }

    @Override
    public void remove(Element element) {

    }

    @Override
    public List<Node> getAll() {
        return null;
    }

    public void remove(FmmlxDiagram diagram)  {
        xmlHandler.removeDiagram(diagram);
    }

    public Vector<String> getAllDiagrams() {
        Vector<String> diagrams = new Vector<>();
        Node diagramsNode = xmlHandler.getDiagramsNode();
        NodeList diagramNodeList = diagramsNode.getChildNodes();

        for(int i =0; i< diagramNodeList.getLength(); i++){
            Node tmp = diagramNodeList.item(i);
            if(tmp.getNodeType()==Node.ELEMENT_NODE){
                String diagramLabel = ((Element) tmp).getAttribute(XmlConstant.ATTRIBUTE_LABEL);
                diagrams.add(diagramLabel);
            }
        }
        return diagrams;
    }

    public void removeAllDiagrams() {
        xmlHandler.clearDiagrams();
    }
}
