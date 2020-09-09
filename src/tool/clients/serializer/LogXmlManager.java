package tool.clients.serializer;

import kodkod.engine.bool.Int;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.FaXML;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.serializer.interfaces.ILog;
import tool.clients.serializer.interfaces.IXmlManager;

import javax.xml.transform.TransformerException;
import java.util.List;
import java.util.Vector;

public class LogXmlManager implements ILog, IXmlManager {
    private final XmlHandler xmlHandler;
    FmmlxDiagram diagram;

    public LogXmlManager(FmmlxDiagram fmmlxDiagram) {
        this.xmlHandler = new XmlHandler();
        this.diagram = fmmlxDiagram;
    }

    @Override
    public void add(Node node) {
        Node logs = xmlHandler.getLogsNode();
        try {
            xmlHandler.addLogElement(logs, node);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
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

    @Override
    public void back(int diagramId) {
        //TODO
    }

    @Override
    public void forward(int diagramId) {
        //TODO
    }

    public Node createNewLogFromFaXML(FaXML faXML){
        Element node = (Element) xmlHandler.createXmlElement(faXML.getName());
        for(String attName : faXML.getAttributes()){
            node.setAttribute(attName, faXML.getAttributeValue(attName));
        }
        return node;
    }

    public void clearLog() throws TransformerException {
        xmlHandler.clearLogs();
    }

    @Override
    public void backToLatestSave(int diagramId, String diagramLabel) {
        //TODO
    }

    @Override
    public String toString() {
        return "Log{" +
                "xmlLogHandler=" + xmlHandler.toString() +
                '}';
    }

    public void reproduceFromLog() {
        Node logs = xmlHandler.getLogsNode();
        Node diagrams = xmlHandler.getDiagramsNode();

        NodeList logList = logs.getChildNodes();

        for(int i = 0 ; i<logList.getLength(); i++){
            if(logList.item(i).getNodeType()==Node.ELEMENT_NODE){
                Element logElement = (Element) logList.item(i);
                reproduceDiagramElement(logElement);
            }
        }
    }

    private void reproduceDiagramElement(Element logElement) {
        String tagName = logElement.getTagName();
        if(tagName.equals("addMetaClass")){
            String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
            int level = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_LEVEL));
            Vector<String> parents = new Vector<>();
            boolean isAbstract = logElement.hasAttribute(XmlConstant.ATTRIBUTE_IS_ABSTRACT);
            int x = 0;
            int y = 0;
            diagram.getComm().addMetaClass(diagram, name, level, parents, isAbstract, x, y);
        } else if(tagName.equals("addOperation2")){

        }
    }
}
