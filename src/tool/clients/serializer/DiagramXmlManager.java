package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import tool.clients.serializer.interfaces.IXmlManager;

import javax.xml.transform.TransformerException;
import java.util.List;

public class DiagramXmlManager implements IXmlManager {
    private final XmlHandler xmlHandler;

    public DiagramXmlManager(){
        this.xmlHandler = new XmlHandler();
    }

    public Node createDiagram(int id, String label, String path) {
        Element diagram = (Element) xmlHandler.createElement(XmlConstant.TAG_NAME_DIAGRAM);
        diagram.setAttribute(XmlConstant.ATTRIBUTE_ID, id+"");
        diagram.setAttribute(XmlConstant.ATTRIBUTE_LABEL, label);
        diagram.setAttribute(XmlConstant.ATTRIBUTE_PACKAGE_PATH, path);
        Node categories = xmlHandler.createElement(XmlConstant.TAG_NAME_CATEGORIES);
        Node owners = xmlHandler.createElement(XmlConstant.TAG_NAME_OWNERS);
        Node objects = xmlHandler.createElement(XmlConstant.TAG_NAME_OBJECTS);
        try {
            xmlHandler.addDiagramCategoriesElement(diagram, categories);
            xmlHandler.addDiagramOwnersElement(diagram, owners);
            xmlHandler.addDiagramObjectsElement(diagram, objects);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return diagram;
    }

    @Override
    public void add(Node node) {
        Node diagrams = xmlHandler.getDiagramsNode();
        try {
            xmlHandler.addDiagramElement(diagrams, node);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Node node) {

    }

    @Override
    public List<Node> getAll() {
        return null;
    }
}
