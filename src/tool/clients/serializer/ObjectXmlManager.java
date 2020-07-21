package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.serializer.interfaces.IXmlManager;

import javax.xml.transform.TransformerException;
import java.util.List;
import java.util.Vector;

public class ObjectXmlManager implements IXmlManager {
    private final XmlHandler xmlHandler;

    public ObjectXmlManager(){
        this.xmlHandler = XmlHandler.getXmlHandlerInstance();
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

        Element object = (Element) xmlHandler.createElement(XmlConstant.TAG_NAME_OBJECT);
        object.setAttribute(XmlConstant.ATTRIBUTE_ID, id+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        object.setAttribute(XmlConstant.ATTRIBUTE_LEVEL, level+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_OF, of+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_PARENTS, parents+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, projectPath);
        object.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X, x+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y, y+"");
        return object;
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
}
