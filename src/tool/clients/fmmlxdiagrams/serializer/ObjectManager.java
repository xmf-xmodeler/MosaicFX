package tool.clients.fmmlxdiagrams.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;

public class ObjectManager {
    private final XmlHandler xmlHandler;

    public ObjectManager(){
        this.xmlHandler = new XmlHandler("logTest.xml");
    }

    public Node createObject() {
        Element object = (Element) xmlHandler.createElement("Object");
        return object;
    }

    public void addOwner(Node node, int diagramId, int x, int y, int width, int length){
        Element object = (Element) node;
        Element owner = (Element) xmlHandler.createElement("Owner");
        owner.setAttribute("diagram_id", diagramId+"");
        owner.setAttribute("x", x+"");
        owner.setAttribute("y", y+"");
        owner.setAttribute("width", width+"");
        owner.setAttribute("length", length+"");
        try {
            xmlHandler.addXmlElement(object, owner);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public void addObject(Node node) {
        Node objects = xmlHandler.getObjectsNode();
        try {
            xmlHandler.addXmlElement(objects, node);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
