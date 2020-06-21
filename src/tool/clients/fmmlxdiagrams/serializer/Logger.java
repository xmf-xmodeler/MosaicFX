package tool.clients.fmmlxdiagrams.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;

public class Logger implements ILog {
    private final XmlHandler xmlHandler;

    public Logger() {
        this.xmlHandler = XmlHandler.getInstance();
    }

    public Logger(XmlHandler xmlHandler) {
        this.xmlHandler = xmlHandler;
    }

    public XmlHandler getXmlHandler() {
        return xmlHandler;
    }

    @Override
    public void back(int diagramId) {
        xmlHandler.moveCurrentStateBackward(diagramId);
    }

    @Override
    public void forward(int diagramId) {
        xmlHandler.moveCurrentStateForward(diagramId);
    }

    @Override
    public void addLog(Node node) {
        try {
            xmlHandler.addXmlLogElement(node);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void backToLatestSave(int diaramId) {
        xmlHandler.getLatestSave(diaramId);
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

    @Override
    public Node createLog(String name) {
        return xmlHandler.createLog(name);
    }
}
