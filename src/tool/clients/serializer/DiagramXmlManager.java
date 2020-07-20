package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.serializer.interfaces.IXmlManager;

import javax.xml.transform.TransformerException;
import java.util.List;

public class DiagramXmlManager implements IXmlManager {
    private final XmlHandler xmlHandler;

    public DiagramXmlManager(){
        this.xmlHandler = new XmlHandler();
    }

    public Node createDiagram(FmmlxDiagram fmmlxDiagram) {
        int id = fmmlxDiagram.getID();
        String label =fmmlxDiagram.getDiagramLabel();
        String path = fmmlxDiagram.getPackagePath();

        Element diagram = (Element) xmlHandler.createElement(XmlConstant.TAG_NAME_DIAGRAM);
        diagram.setAttribute(XmlConstant.ATTRIBUTE_ID, id+"");
        diagram.setAttribute(XmlConstant.ATTRIBUTE_LABEL, label);
        diagram.setAttribute(XmlConstant.ATTRIBUTE_PACKAGE_PATH, path);
        Node categories = xmlHandler.createElement(XmlConstant.TAG_NAME_CATEGORIES);
        Node owners = xmlHandler.createElement(XmlConstant.TAG_NAME_OWNERS);
        Node objects = xmlHandler.createElement(XmlConstant.TAG_NAME_OBJECTS);
        Node edges = xmlHandler.createElement(XmlConstant.TAG_NAME_EDGES);
        Node preferences = xmlHandler.createElement(XmlConstant.TAG_NAME_PREFERENCES);
        try {
            xmlHandler.addDiagramCategoriesElement(diagram, categories);
            xmlHandler.addDiagramOwnersElement(diagram, owners);
            xmlHandler.addDiagramObjectsElement(diagram, objects);
            xmlHandler.addDiagramEdgesElement(diagram, edges);
            xmlHandler.addDiagramPreferencesElemet(diagram, preferences);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return diagram;
    }

    public boolean exists(int id, String diagramLabel, String packagePath) {
        Node diagrams = xmlHandler.getDiagramsNode();
        NodeList children = diagrams.getChildNodes();

        for(int i=0 ; i< children.getLength(); i++){
            Element element = (Element) children.item(i);
            String idAtt = element.getAttribute(XmlConstant.ATTRIBUTE_ID);
            String labelAtt = element.getAttribute(XmlConstant.ATTRIBUTE_LABEL);
            String packagePathAtt = element.getAttribute(XmlConstant.ATTRIBUTE_PACKAGE_PATH);

            if(idAtt.equals(id+"") && labelAtt.equals(diagramLabel) && packagePathAtt.equals(packagePath)){
                return true;
            }
        }
        return false;
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
