package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import tool.clients.serializer.interfaces.ILog;
import tool.clients.serializer.interfaces.IXmlManager;

import javax.xml.transform.TransformerException;
import java.util.List;

public class LogXmlManager implements ILog, IXmlManager {
    private final XmlHandler xmlHandler;

    public LogXmlManager() {
        this.xmlHandler = new XmlHandler();
    }

    public LogXmlManager(XmlHandler xmlHandler) {
        this.xmlHandler = xmlHandler;
    }

    @Override
    public synchronized void add(Node node) {
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
    public synchronized void back(int diagramId) {
        xmlHandler.moveCurrentStateBackward(diagramId);
    }

    @Override
    public synchronized void forward(int diagramId) {
        xmlHandler.moveCurrentStateForward(diagramId);
    }

    public synchronized Node createNewMetaClassLog(String name, int Owner) {
        Element node = (Element) xmlHandler.createElement(XmlConstant.TAG_NAME_OBJECT);
        node.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        node.setAttribute(XmlConstant.ATTRIBUTE_OWNER, Owner+"");
        return node;
    }

    @Override
    public synchronized void backToLatestSave(int diagramId, String diagramLabel) {
        xmlHandler.getLatestSave(diagramId, diagramLabel);
    }

    @Override
    public Element getCurrentState() {
        return (Element) xmlHandler.getCurrentLog();
    }

    @Override
    public String toString() {
        return "Log{" +
                "xmlLogHandler=" + xmlHandler.toString() +
                '}';
    }
}
