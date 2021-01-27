package tool.clients.serializer;

import javafx.geometry.Point2D;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.serializer.interfaces.XmlManager;

import java.util.List;
import java.util.Vector;

public class ObjectXmlManager implements XmlManager {
    private final XmlHandler xmlHandler;

    public ObjectXmlManager(XmlHandler xmlHandler) {
        this.xmlHandler = xmlHandler ;
    }

    public Element createObjectElement(String objectPath, Integer x, Integer y, Boolean hidden) {
        Element object = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_OBJECT);
        object.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, objectPath);
        object.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X, x+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y, y+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_HIDDEN, hidden+"");
        return object;
    }

    @Override
    public void add(Element diagramElement, Element element) {
        Element objects = (Element) getObjectsNode(diagramElement);
        xmlHandler.addObjectElement(objects, element);

    }

    private Node getObjectsNode(Node diagramNode){
        return xmlHandler.getXmlHelper().getNodeByTag(diagramNode, XmlConstant.TAG_NAME_OBJECTS);
    }

    @Override
    public void remove(Element element) {
        //TODO
    }

    @Override
    public List<Node> getAll() {
        //TODO
        return null;
    }

    public void addOperation(Node objectNode, Node newNode)  {
        if(newNode!= null){
            Node operationsNode = getOperationsNode((Element) objectNode);
            Element newOperation= (Element) newNode;

            xmlHandler.addOperationsElement((Element) operationsNode, newOperation);
        }
    }

    private Node getOperationsNode(Element objectNode) {
        return xmlHandler.getXmlHelper().getNodeByTag(objectNode, XmlConstant.TAG_NAME_OPERATIONS);
    }

    public void addAttribute(Node objectNode, Node attributeNode)  {
        if(attributeNode!= null){
            Node attributesNode = getAttributesNode(objectNode);
            Element newAttribute = (Element) attributeNode;

            xmlHandler.addAttributesElement((Element) attributesNode, newAttribute);
        }
    }

    private Node getAttributesNode(Node objectNode) {
        return xmlHandler.getXmlHelper().getNodeByTag(objectNode, XmlConstant.TAG_NAME_ATTRIBUTES);
    }

    public void alignObjects(Element diagramElement, String diagramName, FmmlxDiagramCommunicator communicator) {
        Node objectsNode = xmlHandler.getChildWithName(diagramElement, XmlConstant.TAG_NAME_OBJECTS);
        NodeList objectList = objectsNode.getChildNodes();
        for(int i = 0 ; i < objectList.getLength(); i++){
            if(objectList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element tmp = (Element) objectList.item(i);
                double x = Double.parseDouble(tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X));
                double y = Double.parseDouble(tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y));
                String objectPath = tmp.getAttribute(XmlConstant.ATTRIBUTE_REFERENCE);
                communicator.sendCurrentPosition(FmmlxDiagramCommunicator.getDiagramIdFromName(diagramName), objectPath, (int)Math.round(x), (int)Math.round(y));
            }
        }
        System.out.println("align objects in "+diagramName+" : finished ");
    }


    private Point2D getCoordinate(Node diagramNone, String name, Point2D initCoordingate) {
        Node objectsNode = xmlHandler.getChildWithName(diagramNone, XmlConstant.TAG_NAME_OBJECTS);
        NodeList objectList = objectsNode.getChildNodes();

        for (int i = 0 ; i< objectList.getLength() ; i++){
            if (objectList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element object_tmp = (Element) objectList.item(i);
                if(object_tmp.getAttribute(XmlConstant.ATTRIBUTE_NAME).equals(name)){
                    double x = Double.parseDouble(object_tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X));
                    double y = Double.parseDouble(object_tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y));
                    return new Point2D(x, y);
                }
            }
        }
        return initCoordingate;
    }

    public void alignObjects(Integer id, Vector<FmmlxObject> objects) {
        Element diagrams = xmlHandler.getDiagramsElement();
        NodeList diagramList = diagrams.getChildNodes();

        Node diagramNode = null;

        for (int i = 0 ; i< diagramList.getLength(); i++){
            if(diagramList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element tmp = (Element) diagramList.item(i);
                int tmp_id = FmmlxDiagramCommunicator.getDiagramIdFromName(tmp.getAttribute(XmlConstant.ATTRIBUTE_LABEL));
                if (tmp_id==id){
                    diagramNode = tmp;
                }
            }
        }

        for(FmmlxObject object : objects){
            Point2D initCoordinate = new Point2D(object.getX(), object.getY());
            Point2D coordinate = getCoordinate(diagramNode, object.getName(),initCoordinate);
            FmmlxDiagramCommunicator.getCommunicator().sendCurrentPosition(id, object.getPath(), (int)Math.round(coordinate.getX()), (int)Math.round(coordinate.getY()));
        }
    }

    @Deprecated
    public void alignObjects2(Node diagramNode, FmmlxDiagram fmmlxDiagram) {
        List<FmmlxObject>allObjects = fmmlxDiagram.getObjects();
        for(FmmlxObject object : allObjects){
            Point2D initCoordinate = new Point2D(object.getX(), object.getY());
            Point2D coordinate = getCoordinate(diagramNode, object.getName(),initCoordinate);
            object.moveTo(coordinate.getX(), coordinate.getY(), fmmlxDiagram);
            fmmlxDiagram.getComm().sendCurrentPosition(FmmlxDiagramCommunicator.getDiagramIdFromName(fmmlxDiagram.getDiagramLabel()), object.getPath(), (int)Math.round(object.getX()), (int)Math.round(object.getY()));
        }
    }
}
