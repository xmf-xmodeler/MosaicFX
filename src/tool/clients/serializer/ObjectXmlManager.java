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

    public Element createObjectElement(FmmlxDiagram diagram, FmmlxObject fmmlxObject) {
        String name = fmmlxObject.getName();
        int level= fmmlxObject.getLevel();
        String ofName = fmmlxObject.getOfPath();
        Vector<String> parents = fmmlxObject.getParentsPaths();
        String projectPath = diagram.getPackagePath()+"::"+name;
        String owner = diagram.getDiagramLabel();
        double x = fmmlxObject.getX();
        double y = fmmlxObject.getY();

        Element object = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_OBJECT);
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

    public Element createOperationXmlNode(FmmlxDiagram fmmlxDiagram, FmmlxObject object, FmmlxOperation fmmlxOperation) throws TransformerException {
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

        Element operation = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_OPERATION);
        operation.setAttribute(XmlConstant.ATTRIBUTE_DIAGRAM_OWNER, diagramOwner);
        operation.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner);
        operation.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, projectPath);
        operation.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        operation.setAttribute(XmlConstant.ATTRIBUTE_LEVEL, level+"");
        operation.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        operation.setAttribute(XmlConstant.ATTRIBUTE_IS_MONITORED, isMonitored+"");

        Element paramElement = xmlHandler.createXmlElement(XmlConstant.TAG_PARAM);
        for(int i=0; i<paramNames.size(); i++){
            Element paramNameElement = xmlHandler.createXmlElement(XmlConstant.TAG_PARAM_NAME);
            paramNameElement.setAttribute(XmlConstant.ATTRIBUTE_NAME, paramNames.get(i));
            paramNameElement.setAttribute(XmlConstant.ATTRIBUTE_TYPE, paramTypes.get(i));
            paramElement.appendChild(paramNameElement);
        }

        Element bodyElement = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_BODY);
        bodyElement.setTextContent(body);

        xmlHandler.addParamElement(operation, paramElement);
        xmlHandler.addBodyElement(operation, bodyElement);
        return operation;
    }

    public Element createAttributeXmlNode(FmmlxDiagram fmmlxDiagram, FmmlxObject object, FmmlxAttribute attribute) {
        String name = attribute.getName();
        String owner = object.getName();
        String diagramOwner = fmmlxDiagram.getDiagramLabel();
        int level = attribute.getLevel();
        String type = attribute.getType();
        Multiplicity multiplicity = attribute.getMultiplicity();

        Element attributeElement = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_ATTRIBUTE);
        attributeElement.setAttribute(XmlConstant.ATTRIBUTE_DIAGRAM_OWNER, diagramOwner);
        attributeElement.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner);
        attributeElement.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        attributeElement.setAttribute(XmlConstant.ATTRIBUTE_LEVEL, level+"");
        attributeElement.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        attributeElement.setAttribute(XmlConstant.ATTRIBUTE_MULTIPLICITY, multiplicity.toString());

        return attributeElement;
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
            fmmlxDiagram.getComm().sendCurrentPosition(fmmlxDiagram.getID(), object);
        }
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
