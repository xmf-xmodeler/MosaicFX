package tool.clients.serializer;

import javafx.geometry.Point2D;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.serializer.interfaces.IXmlManager;

import javax.xml.transform.TransformerException;
import java.util.List;
import java.util.Vector;

public class ObjectXmlManager implements IXmlManager {
    private final XmlHandler xmlHandler;

    public ObjectXmlManager(String file) {
        this.xmlHandler =  new XmlHandler(file);
    }

    public Element createObjectElement(String objectPath, Integer x, Integer y, Boolean hidden) {
//        String name = fmmlxObject.getName();
//        int level= fmmlxObject.getLevel();
//        String ofName = fmmlxObject.getOfPath();
//        Vector<String> parents = fmmlxObject.getParentsPaths();
//        String projectPath = fmmlxObject.getPath();
//        String owner = diagram.getDiagramLabel();
//        double x = fmmlxObject.getX();
//        double y = fmmlxObject.getY();

        Element object = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_OBJECT);
//        object.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
//        object.setAttribute(XmlConstant.ATTRIBUTE_LEVEL, level+"");
//        object.setAttribute(XmlConstant.ATTRIBUTE_OF, ofName+"");
//        object.setAttribute(XmlConstant.ATTRIBUTE_PARENTS, parents+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, objectPath);
//        object.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner);
        object.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X, x+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y, y+"");
        return object;
    }

    @Override
    public void add(Element element) {

        Node diagrams = xmlHandler.getDiagramsNode();
        NodeList diagramNodeList = diagrams.getChildNodes();

        for(int i=0 ; i<diagramNodeList.getLength(); i++){
            if(diagramNodeList.item(i).getNodeType()==Node.ELEMENT_NODE){
                Element diagram = (Element) diagramNodeList.item(i);
                if(diagram.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(element.getAttribute(XmlConstant.ATTRIBUTE_OWNER))){
                    Element objects = (Element) getObjectsNode(diagram);
                    try {
                        xmlHandler.addObjectElement(objects, element);
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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

    public void addOperation(Node objectNode, Node newNode) throws TransformerException {
        if(newNode!= null){
            Node operationsNode = getOperationsNode((Element) objectNode);
            Element newOperation= (Element) newNode;

            xmlHandler.addOperationsElement((Element) operationsNode, newOperation);
        }
    }

    private Node getOperationsNode(Element objectNode) {
        return xmlHandler.getXmlHelper().getNodeByTag(objectNode, XmlConstant.TAG_NAME_OPERATIONS);
    }

    public void addAttribute(Node objectNode, Node attributeNode) throws TransformerException {
        if(attributeNode!= null){
            Node attributesNode = getAttributesNode(objectNode);
            Element newAttribute = (Element) attributeNode;

            xmlHandler.addAttributesElement((Element) attributesNode, newAttribute);
        }
    }

    private Node getAttributesNode(Node objectNode) {
        return xmlHandler.getXmlHelper().getNodeByTag(objectNode, XmlConstant.TAG_NAME_ATTRIBUTES);
    }

    @Deprecated
    public void alignObjects(String diagramName, FmmlxDiagramCommunicator communicator) {
        Node diagrams = xmlHandler.getDiagramsNode();
        NodeList diagramList = diagrams.getChildNodes();

        Node diagramNode = null;

        for (int i = 0 ; i< diagramList.getLength(); i++){
            if(diagramList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element tmp = (Element) diagramList.item(i);
                if (tmp.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(diagramName)){
                    diagramNode = tmp;
                }
            }
        }

        Node objectsNode = xmlHandler.getChildWithName(diagramNode, XmlConstant.TAG_NAME_OBJECTS);
        NodeList objectList = objectsNode.getChildNodes();
        for(int i = 0 ; i < objectList.getLength(); i++){
            if(objectList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element tmp = (Element) objectList.item(i);
                double x = Double.parseDouble(tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X));
                double y = Double.parseDouble(tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y));
                String objectPath = tmp.getAttribute(XmlConstant.ATTRIBUTE_REFERENCE);
                communicator.sendCurrentPosition(communicator.getDiagramIdFromName(diagramName), objectPath, (int)Math.round(x), (int)Math.round(y));
            }
        }
        System.out.println("align objects in "+diagramName+" : finished ");
    }

    public void alignObjects(FmmlxDiagram fmmlxDiagram) {
        Node diagrams = xmlHandler.getDiagramsNode();
        NodeList diagramList = diagrams.getChildNodes();

        Node diagramNode = null;

        for (int i = 0 ; i< diagramList.getLength(); i++){
            if(diagramList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element tmp = (Element) diagramList.item(i);
                if (tmp.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(fmmlxDiagram.getDiagramLabel())){
                    diagramNode = tmp;
                }
            }
        }

        List<FmmlxObject>allObjects = fmmlxDiagram.getObjects();
        for(FmmlxObject object : allObjects){
            Point2D initCoordinate = new Point2D(object.getX(), object.getY());
            Point2D coordinate = getCoordinate(diagramNode, object.getName(),initCoordinate);
            object.moveTo(coordinate.getX(), coordinate.getY(), fmmlxDiagram);
            fmmlxDiagram.getComm().sendCurrentPosition(fmmlxDiagram.getComm().getDiagramIdFromName(fmmlxDiagram.getDiagramLabel()), object.getPath(), (int)Math.round(object.getX()), (int)Math.round(object.getY()));
        }
        System.out.println("align objects in "+fmmlxDiagram.getDiagramLabel()+" : finished ");
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
}
