package tool.clients.fmmlxdiagrams.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;

public class ObjectManager {
    private final XmlHandler xmlHandler;

    public ObjectManager(){
        this.xmlHandler = new XmlHandler();
    }

    public Node createObject() {
        Element object = (Element) xmlHandler.createElement("Object");
        return object;
    }

    public void addOwner(Node node, int diagramId, String diagramLabel, int x, int y){
        Element object = (Element) node;
        Element owner = (Element) xmlHandler.createElement("Owner");
        owner.setAttribute("diagram_id", diagramId+"");
        owner.setAttribute("label", diagramLabel);
        owner.setAttribute("x", x+"");
        owner.setAttribute("y", y+"");
        try {
            xmlHandler.addObjectOwnerElement(object, owner);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public void addObject(Node node) {
        Node objects = xmlHandler.getObjectsNode();
        try {
            xmlHandler.addObjectElement(objects, node);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
