package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import tool.clients.fmmlxdiagrams.FaXML;
import tool.clients.serializer.interfaces.ILog;
import tool.clients.serializer.interfaces.IXmlManager;

import javax.xml.transform.TransformerException;
import java.util.List;

public class LogXmlManager implements ILog, IXmlManager {
    private final XmlHandler xmlHandler;

    public LogXmlManager() {
        this.xmlHandler = new XmlHandler();
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
}
