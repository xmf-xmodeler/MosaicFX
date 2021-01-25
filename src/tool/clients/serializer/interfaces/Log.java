package tool.clients.serializer.interfaces;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface Log {
    void back(int diagramId);
    void forward(int diagramId);
    void backToLatestSave(int diagramId, String diagramLabel);

}
