package tool.clients.fmmlxdiagrams.loghandler;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface LogHelper {
    void back();
    void forward();
    void addLog(Node node);
    void backToOriginal();
    void saveState();
    Element getCurrentState();
}
