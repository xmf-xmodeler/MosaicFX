package tool.clients.fmmlxdiagrams.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;

public class LogManager implements ILog {
    private final XmlHandler xmlHandler;

    public LogManager() {
        this.xmlHandler = new XmlHandler("logTest.xml");
    }

    public LogManager(XmlHandler xmlHandler) {
        this.xmlHandler = xmlHandler;
    }

    @Override
    public synchronized void back(int diagramId) {
        xmlHandler.moveCurrentStateBackward(diagramId);
    }

    @Override
    public synchronized void forward(int diagramId) {
        xmlHandler.moveCurrentStateForward(diagramId);
    }

    @Override
    public synchronized Node createLog(String name) {
        return xmlHandler.createElement(name);
    }

    @Override
    public synchronized void addLog(Node node) {
        Node logs = xmlHandler.getLogsNode();
        try {
            xmlHandler.addLogElement(logs, node);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void backToLatestSave(int diaramId) {
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
}
