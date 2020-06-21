package tool.clients.fmmlxdiagrams.loghandler;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Log implements ILog {
    private final XmlHandler xmlHandler;

    public Log() {
        this.xmlHandler = XmlHandler.getInstance();
    }

    @Override
    public void back() {
        xmlHandler.moveCurrentStateBackward();
    }

    @Override
    public void forward() {
        xmlHandler.moveCurrentStateForward();
    }

    @Override
    public void addLog(Node node) {
        xmlHandler.addXmlLogElement(node);
    }

    @Override
    public void backToLatestSave() {
        xmlHandler.getLatestSave();
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
