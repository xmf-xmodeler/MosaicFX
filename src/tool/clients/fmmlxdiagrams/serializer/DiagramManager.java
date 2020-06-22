package tool.clients.fmmlxdiagrams.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;

public class DiagramManager {
    private final XmlHandler xmlHandler;

    public DiagramManager(){
        this.xmlHandler = new XmlHandler();
    }

    public Node createDiagram(String name) {
        Element diagram = (Element) xmlHandler.createElement(name);
        Node categories = xmlHandler.createElement("Categories");
        Node owners = xmlHandler.createElement("Owners");
        try {
            xmlHandler.addDiagramCategoriesElement(diagram, categories);
            xmlHandler.addDiagramOwnersElement(diagram, owners);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return diagram;
    }

    public void addDiagram(Node node) {
        Node diagrams = xmlHandler.getDiagramsNode();
        try {
            xmlHandler.addDiagramElement(diagrams, node);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
