package tool.clients.serializer.interfaces;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.util.List;

public interface XmlManager {
    void add(Element element) throws TransformerException;
    void remove(Element element);
    List<Node> getAll();
}
