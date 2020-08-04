package tool.clients.serializer.interfaces;

import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.util.List;

public interface IXmlManager {

    void add(Node node) throws TransformerException;
    void remove(Node node);
    List<Node> getAll();
}
