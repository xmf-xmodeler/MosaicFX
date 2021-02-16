package tool.clients.serializer;

import javafx.geometry.Point2D;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.serializer.interfaces.XmlManager;

import java.util.List;

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
        Element objects = getObjectsElement(diagramElement);
        xmlHandler.addXmlElement(objects, element);
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

    public Element getDiagramsElement(){
        Element Root = xmlHandler.getRoot();
        return xmlHandler.getChildWithTag(Root, XmlConstant.TAG_NAME_DIAGRAMS);
    }

    public Element getObjectsElement(Element diagramsElement){
        return xmlHandler.getChildWithTag(diagramsElement, XmlConstant.TAG_NAME_OBJECTS);
    }

    public void addOperation(Node objectNode, Node newNode)  {
        if(newNode!= null){
            Element operationsNode = getOperationsNode((Element) objectNode);
            Element newOperation= (Element) newNode;

            xmlHandler.addXmlElement(operationsNode, newOperation);
        }
    }

    private Element getOperationsNode(Element objectNode) {
        return xmlHandler.getChildWithTag(objectNode, XmlConstant.TAG_NAME_OPERATIONS);
    }

    public void addAttribute(Element objectElement, Element attributeElement)  {
        if(attributeElement!= null){
            Element attributesNode = getAttributesNode(objectElement);

            xmlHandler.addXmlElement(attributesNode, attributeElement);
        }
    }

    private Element getAttributesNode(Element objectNode) {
        return xmlHandler.getChildWithTag(objectNode, XmlConstant.TAG_NAME_ATTRIBUTES);
    }

    public void alignObjects(Element diagramElement, String diagramName, FmmlxDiagramCommunicator communicator) {
        Node objectsNode = xmlHandler.getChildWithTag(diagramElement, XmlConstant.TAG_NAME_OBJECTS);
        NodeList objectList = objectsNode.getChildNodes();
        for(int i = 0 ; i < objectList.getLength(); i++){
            if(objectList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element tmp = (Element) objectList.item(i);
                double x = Double.parseDouble(tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X));
                double y = Double.parseDouble(tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y));
                String objectPath = tmp.getAttribute(XmlConstant.ATTRIBUTE_REFERENCE);
                System.out.println("diagram id"+FmmlxDiagramCommunicator.getDiagramIdFromName(diagramName) +", " +objectPath+", x :"+ (int)Math.round(x) +", y : "+(int)Math.round(y));
                communicator.sendCurrentPosition(FmmlxDiagramCommunicator.getDiagramIdFromName(diagramName), objectPath, (int)Math.round(x), (int)Math.round(y));
            }
        }
        System.out.println("align objects in "+diagramName+" : finished ");
    }


    private Point2D getCoordinate(Element diagramElement, String name, Point2D initCoordingate) {
        Node objectsNode = getObjectsElement(diagramElement);
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

    @Deprecated
    public void alignObjects2(Element diagramElement, FmmlxDiagram fmmlxDiagram) {
        List<FmmlxObject>allObjects = fmmlxDiagram.getObjects();
        for(FmmlxObject object : allObjects){
            Point2D initCoordinate = new Point2D(object.getX(), object.getY());
            Point2D coordinate = getCoordinate(diagramElement, object.getName(),initCoordinate);
            object.moveTo(coordinate.getX(), coordinate.getY(), fmmlxDiagram);
            fmmlxDiagram.getComm().sendCurrentPosition(FmmlxDiagramCommunicator.getDiagramIdFromName(fmmlxDiagram.getDiagramLabel()), object.getPath(), (int)Math.round(object.getX()), (int)Math.round(object.getY()));
        }
    }
}
