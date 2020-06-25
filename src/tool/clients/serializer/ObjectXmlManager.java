package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.serializer.interfaces.IXmlManager;

import javax.xml.transform.TransformerException;
import java.util.List;

public class ObjectXmlManager implements IXmlManager {
    private final XmlHandler xmlHandler;

    public ObjectXmlManager(){
        this.xmlHandler = new XmlHandler();
    }

    public Node createObject(String name, String projectPath, int owner, int x, int y) {
        Element object = (Element) xmlHandler.createElement(XmlConstant.TAG_NAME_OBJECT);
        object.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        object.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, projectPath+"::"+name);
        object.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_COORDINAT_X, x+"");
        object.setAttribute(XmlConstant.ATTRIBUTE_COORDINAT_Y, y+"");
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
