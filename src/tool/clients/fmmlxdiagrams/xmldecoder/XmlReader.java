package tool.clients.fmmlxdiagrams.xmldecoder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XmlReader {

    private final String sourcePath;
    private final Document document;

    public XmlReader(String sourcePath) {
        this.sourcePath = sourcePath;
        this.document = buildDocument(sourcePath);
    }

    private Document buildDocument(String sourcePath) {
        Document doc = null;
        try {
            File fXmlFile = new File(sourcePath);
            if (fXmlFile.exists()) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.parse(fXmlFile);
                doc.getDocumentElement().normalize();
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace(System.err);
        }
        return doc;
    }

    public Element getRootElement(){
        return document.getDocumentElement();
    }

    public Element getChildrenById(Node parentNode, String id){
        NodeList nodeList = parentNode.getChildNodes();
        for(int i = 0 ; i< nodeList.getLength(); i++){
            if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) nodeList.item(i);
                if(element.getAttribute("id").equals(id)){
                    return element;
                }
            }
        }
        return null;
    }

}
