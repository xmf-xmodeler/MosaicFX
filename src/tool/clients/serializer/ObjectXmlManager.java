package tool.clients.serializer;

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

    public ObjectXmlManager(){
        this.xmlHandler = new XmlHandler();
    }

    public Node createObject(FmmlxDiagram diagram, FmmlxObject fmmlxObject) {
        String name = fmmlxObject.getName();
        int level= fmmlxObject.getLevel();
        String ofName = fmmlxObject.getOfName();
        Vector<String> parents = fmmlxObject.getParentsNames();
        String projectPath = diagram.getPackagePath()+"::"+name;
        String owner = diagram.getDiagramLabel();
        double x = fmmlxObject.getX();
        double y = fmmlxObject.getY();

        Element object = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_OBJECT);
        object.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        object.setAttribute(XmlConstant.ATTRIBUTE_LEVEL, level+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_OF, ofName+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_PARENTS, parents+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, projectPath);
        object.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner);
        object.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X, x+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y, y+"");
        return object;
    }

    public Node createOperationXmlNode(FmmlxDiagram fmmlxDiagram, FmmlxObject object, FmmlxOperation fmmlxOperation) throws TransformerException {
        String name = fmmlxOperation.getName();
        String owner = object.getName();
        String diagramOwner = fmmlxDiagram.getDiagramLabel();
        int level = fmmlxOperation.getLevel();
        boolean isMonitored = fmmlxOperation.isMonitored();
        String type = fmmlxOperation.getType();
        String body = fmmlxOperation.getBody();
        String projectPath = fmmlxDiagram.getPackagePath();
        Vector<String> paramNames= fmmlxOperation.getParamNames();
        Vector<String> paramTypes= fmmlxOperation.getParamTypes();

        Element operation = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_OPERATION);
        operation.setAttribute(XmlConstant.ATTRIBUTE_DIAGRAM_OWNER, diagramOwner);
        operation.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner);
        operation.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, projectPath);
        operation.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        operation.setAttribute(XmlConstant.ATTRIBUTE_LEVEL, level+"");
        operation.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        operation.setAttribute(XmlConstant.ATTRIBUTE_IS_MONITORED, isMonitored+"");

        Node paramNode = xmlHandler.createXmlElement(XmlConstant.TAG_PARAM);
        for(int i=0; i<paramNames.size(); i++){
            Element paramNameElement = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_PARAM_NAME);
            paramNameElement.setAttribute(XmlConstant.ATTRIBUTE_NAME, paramNames.get(i));
            paramNameElement.setAttribute(XmlConstant.ATTRIBUTE_TYPE, paramTypes.get(i));
            paramNode.appendChild(paramNameElement);
        }

        Element bodyNode = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_BODY);
        bodyNode.setTextContent(body);

        xmlHandler.addParamElement(operation, paramNode);
        xmlHandler.addBodyElement(operation, bodyNode);
        return operation;
    }

    public Node createAttributeXmlNode(FmmlxDiagram fmmlxDiagram, FmmlxObject object, FmmlxAttribute attribute) {
        String name = attribute.getName();
        String owner = object.getName();
        String diagramOwner = fmmlxDiagram.getDiagramLabel();
        int level = attribute.getLevel();
        String type = attribute.getType();
        Multiplicity multiplicity = attribute.getMultiplicity();

        Element attributeNode = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_ATTRIBUTE);
        attributeNode.setAttribute(XmlConstant.ATTRIBUTE_DIAGRAM_OWNER, diagramOwner);
        attributeNode.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner);
        attributeNode.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        attributeNode.setAttribute(XmlConstant.ATTRIBUTE_LEVEL, level+"");
        attributeNode.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        attributeNode.setAttribute(XmlConstant.ATTRIBUTE_MULTIPLICITY, multiplicity.toString());

        return attributeNode;
    }

    @Override
    public void add(Node node) {
        Element newObject = (Element) node;

        Node diagrams = xmlHandler.getDiagramsNode();
        NodeList diagramNodeList = diagrams.getChildNodes();

        for(int i=0 ; i<diagramNodeList.getLength(); i++){
            if(diagramNodeList.item(i).getNodeType()==Node.ELEMENT_NODE){
                Element diagram = (Element) diagramNodeList.item(i);
                if(diagram.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(newObject.getAttribute(XmlConstant.ATTRIBUTE_OWNER))){
                    Element objects = (Element) getObjectsNode(diagram);
                    try {
                        xmlHandler.addObjectElement(objects, newObject);
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
    public void remove(Node node) {
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
            Coordinate initCoordingate = new Coordinate(object.getX(), object.getY());
            Coordinate coordinate = getCoordinate(diagramNode, object.getName(),initCoordingate);
            object.moveTo(coordinate.getX(), coordinate.getY(), fmmlxDiagram);
        }
        fmmlxDiagram.objectsMoved = true;
    }

    private Coordinate getCoordinate(Node diagramNone, String name, Coordinate initCoordingate) {
        Node objectsNode = xmlHandler.getChildWithName(diagramNone, XmlConstant.TAG_NAME_OBJECTS);
        NodeList objectList = objectsNode.getChildNodes();
        Coordinate coordinate = initCoordingate;

        for (int i = 0 ; i< objectList.getLength() ; i++){
            if (objectList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element object_tmp = (Element) objectList.item(i);
                if(object_tmp.getAttribute(XmlConstant.ATTRIBUTE_NAME).equals(name)){
                    double x = Double.parseDouble(object_tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X));
                    double y = Double.parseDouble(object_tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y));
                    coordinate.setX(x);
                    coordinate.setY(y);
                }
            }
        }
        return coordinate;
    }

    private class Coordinate {
        double x;
        double y;

        public Coordinate(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return "Coordinat{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
