package tool.clients.fmmlxdiagrams.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface ILog {
    void back(int diagramId);
    void forward(int diagramId);
    Node createLog(String name);
    void addLog(Node node);
    void backToLatestSave(int diagramId);
    Element getCurrentState();
}
