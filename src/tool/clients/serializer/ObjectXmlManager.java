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
        int id = fmmlxObject.getId();
        String name = fmmlxObject.getName();
        int level= fmmlxObject.getLevel();
        int of = fmmlxObject.getOf();
        Vector<Integer> parents = fmmlxObject.getParents();
        String projectPath = diagram.getPackagePath()+"::"+name;
        int owner = diagram.getID();
        double x = fmmlxObject.getX();
        double y = fmmlxObject.getY();

        Element object = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_OBJECT);
        object.setAttribute(XmlConstant.ATTRIBUTE_ID, id+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        object.setAttribute(XmlConstant.ATTRIBUTE_LEVEL, level+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_OF, of+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_PARENTS, parents+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, projectPath);
        object.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X, x+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y, y+"");
        Node attributes = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_ATTRIBUTES);
        Node operations = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_OPERATIONS);
        try {
            xmlHandler.addOperationsElement(object, operations);
            xmlHandler.addAttributesElement(object, attributes);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
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
        operation.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner+"");
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
        attributeNode.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner+"");
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
                if(diagram.getAttribute(XmlConstant.ATTRIBUTE_ID).equals(newObject.getAttribute(XmlConstant.ATTRIBUTE_OWNER))){
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
}
