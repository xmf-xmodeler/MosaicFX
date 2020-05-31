package tool.clients.fmmlxdiagrams.loghandler;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Log implements LogHelper {
    private final XmlLogHandler xmlLogHandler;

    public Log(String xmlPath) {
        this.xmlLogHandler = new XmlLogHandler(xmlPath);
    }

    @Override
    public void back() {
        xmlLogHandler.backToBeforeLatestActionNode();
    }

    @Override
    public void forward() {
        //TODO
    }

    @Override
    public void addLog(Node node) {
        xmlLogHandler.addXmlLogElement(node);
    }

    @Override
    public void backToOriginal() {
        xmlLogHandler.backToLatestSave();
    }

    @Override
    public void saveState() {
        xmlLogHandler.saveState();
    }

    @Override
    public Element getCurrentState() {
        return (Element) xmlLogHandler.getCurrentLog();
    }
}
