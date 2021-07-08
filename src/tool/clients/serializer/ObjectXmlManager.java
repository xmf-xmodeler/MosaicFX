package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.xmlManipulator.XmlHandler;

import java.util.List;

public class ObjectXmlManager {
    private final XmlHandler xmlHandler;

    public ObjectXmlManager(XmlHandler xmlHandler) {
        this.xmlHandler = xmlHandler ;
    }

    public Element createObjectElement(String objectPath, Integer x, Integer y, Boolean hidden) {
        Element object = xmlHandler.createXmlElement(SerializerConstant.TAG_NAME_OBJECT);
        object.setAttribute(SerializerConstant.ATTRIBUTE_REFERENCE, objectPath);
        object.setAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_X, x+"");
        object.setAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_Y, y+"");
        object.setAttribute(SerializerConstant.ATTRIBUTE_HIDDEN, hidden+"");
        return object;
    }

    public void add(Element diagramElement, Element element) {
        Element objects = getObjectsElement(diagramElement);
        xmlHandler.addXmlElement(objects, element);
    }

    public void remove(Element element) {
        //TODO
    }

    public List<Node> getAll() {
        //TODO
        return null;
    }

    public Element getDiagramsElement(){
        Element Root = xmlHandler.getRoot();
        return xmlHandler.getChildWithTag(Root, SerializerConstant.TAG_NAME_DIAGRAMS);
    }

    public Element getObjectsElement(Element diagramsElement){
        return xmlHandler.getChildWithTag(diagramsElement, SerializerConstant.TAG_NAME_OBJECTS);
    }


    public void alignObjects(Element diagramElement, int diagramID, FmmlxDiagramCommunicator communicator) {
        Node objectsNode = xmlHandler.getChildWithTag(diagramElement, SerializerConstant.TAG_NAME_OBJECTS);
        NodeList objectList = objectsNode.getChildNodes();
        for(int i = 0 ; i < objectList.getLength(); i++){
            if(objectList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element tmp = (Element) objectList.item(i);
                double x = Double.parseDouble(tmp.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_X));
                double y = Double.parseDouble(tmp.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_Y));
                boolean hidden = "true".equals(tmp.getAttribute(SerializerConstant.ATTRIBUTE_HIDDEN));
                String objectPath = tmp.getAttribute(SerializerConstant.ATTRIBUTE_REFERENCE);
                communicator.sendCurrentPosition(diagramID, objectPath, (int)Math.round(x), (int)Math.round(y), hidden);
            }
        }
    }
}
