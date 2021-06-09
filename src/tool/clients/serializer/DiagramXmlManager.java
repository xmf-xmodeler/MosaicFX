package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.xmlManipulator.XmlHandler;

import java.util.List;
import java.util.Vector;

public class DiagramXmlManager  {
    private final XmlHandler xmlHandler;

    protected DiagramXmlManager(XmlHandler xmlHandler){
        this.xmlHandler = xmlHandler;
    }

    public Element createDiagramElement(String label, String path) {

        Element diagram = xmlHandler.createXmlElement(SerializerConstant.TAG_NAME_DIAGRAM);
        diagram.setAttribute(SerializerConstant.ATTRIBUTE_LABEL, label);
        diagram.setAttribute(SerializerConstant.ATTRIBUTE_PACKAGE_PATH, path);
        Element categories = xmlHandler.createXmlElement(SerializerConstant.TAG_NAME_CATEGORIES);
        Element owners = xmlHandler.createXmlElement(SerializerConstant.TAG_NAME_OWNERS);
        Element objects = xmlHandler.createXmlElement(SerializerConstant.TAG_NAME_OBJECTS);
        Element edges = xmlHandler.createXmlElement(SerializerConstant.TAG_NAME_EDGES);
        Element labels = xmlHandler.createXmlElement(SerializerConstant.TAG_NAME_LABELS);
        Element preferences = xmlHandler.createXmlElement(SerializerConstant.TAG_NAME_PREFERENCES);
        xmlHandler.addXmlElement(diagram, categories);
        xmlHandler.addXmlElement(diagram, owners);
        xmlHandler.addXmlElement(diagram, objects);
        xmlHandler.addXmlElement(diagram, edges);
        xmlHandler.addXmlElement(diagram, labels);
        xmlHandler.addXmlElement(diagram, preferences);
        return diagram;
    }

    public boolean isExist(String label) {
        Node diagrams = getDiagramsElement();

        NodeList diagramList = diagrams.getChildNodes();

        for(int i =0; i<diagramList.getLength(); i++){
            if(diagramList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) diagramList.item(i);
                if(element.getAttribute(SerializerConstant.ATTRIBUTE_LABEL).equals(label)){
                    return true;
                }
            }
        }
        return false;
    }

    public Element getDiagramsElement(){
        Element Root = xmlHandler.getRoot();
        return xmlHandler.getChildWithTag(Root, SerializerConstant.TAG_NAME_DIAGRAMS);
    }

    public void add(Element parent, Element element) {
        xmlHandler.addXmlElement(parent, element);
    }

    public void remove(Element element) {

    }

    public List<Node> getAll() {
        return null;
    }

    public void remove(String label)  {
        Element diagrams = getDiagramsElement();
        NodeList diagramsChildNodes = diagrams.getChildNodes();

        for(int i = 0 ; i< diagramsChildNodes.getLength(); i++){
            if(diagramsChildNodes.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element tmp_element = (Element) diagramsChildNodes.item(i);
                if(tmp_element.getAttribute(SerializerConstant.ATTRIBUTE_LABEL).equals(label)
                        && tmp_element.getAttribute(SerializerConstant.ATTRIBUTE_LABEL).equals(label)){
                    xmlHandler.removeChildElement(diagrams, tmp_element);
                }
            }
        }
    }

    public Vector<String> getAllDiagramNames() {
        Vector<String> diagrams = new Vector<>();
        Node diagramsNode = getDiagramsElement();
        NodeList diagramNodeList = diagramsNode.getChildNodes();

        for(int i =0; i< diagramNodeList.getLength(); i++){
            Node tmp = diagramNodeList.item(i);
            if(tmp.getNodeType()==Node.ELEMENT_NODE){
                String diagramLabel = ((Element) tmp).getAttribute(SerializerConstant.ATTRIBUTE_LABEL);
                diagrams.add(diagramLabel);
            }
        }
        return diagrams;
    }

    public void removeAllDiagrams() {
        Element diagramsElement = getDiagramsElement();
        xmlHandler.removeAllChildren(diagramsElement);
    }


}
