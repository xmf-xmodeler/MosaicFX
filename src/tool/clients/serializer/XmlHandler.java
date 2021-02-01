package tool.clients.serializer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class XmlHandler {
    private final Document document;
    private final XmlHelper xmlHelper;
    private static String sourcePath;

    public XmlHandler(String sourcePath){
        XmlHandler.sourcePath = sourcePath;
        this.document = buildDocument(sourcePath);
        this.xmlHelper = XmlHelper.getInstance(getDocument());
    }

    public String getSourcePath() {
        return sourcePath;
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

    public XmlHelper getXmlHelper() {
        return this.xmlHelper;
    }

    public Document getDocument() {
        return this.document;
    }

    public void addXmlElement(Element parent, Element element){
        getXmlHelper().addXmlNode(parent, element);
    }

    public void removeChildElement(Element parent, Element children){
        getXmlHelper().removeChildNode(parent, children);
    }

    public void removeAllChildren(Element element){
        getXmlHelper().removeAllChildrenNode(element);
    }

    protected Element createXmlElement(String name){
        return (Element) getXmlHelper().createXmlNode(name);
    }

    public void replaceElement(Element oldElement, String newNodeName) {
        Node newNode = getXmlHelper().createXmlNode(newNodeName);
        getXmlHelper().getRootNode().replaceChild(newNode, oldElement);
        getXmlHelper().getRootNode().normalize();
    }

    public Element getChildWithTag(Element parent, String child) {
        return (Element) getXmlHelper().getNodeByTag(parent, child);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        try{
            this.document.getDocumentElement().normalize();
            stringBuilder.append("Root element : ").append(getXmlHelper().getRootNode().getNodeName());

            NodeList nList = getXmlHelper().getRootNode().getChildNodes();

            stringBuilder.append("\n----------------------------");
            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    stringBuilder.append("\n").append(eElement.getTagName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public void flushData() throws TransformerException {
        getXmlHelper().flush();
    }

    public Element getRoot() {
        return (Element) getXmlHelper().getRootNode();
    }

    public Node getChildrenByAttributeValue(Element element, String attributeName, String value) {
        return getXmlHelper().getChildrenByAttributeValue(element, attributeName, value);
    }

    public static class XmlHelper {
        private final Document document;

        private static synchronized XmlHelper getInstance(Document document) {
            return new XmlHelper(document);
        }

        private XmlHelper(Document document) {
            this.document = document;
        }

        private Node getRootNode(){
            return this.document.getDocumentElement();
        }

        private Element getChildrenByAttributeValue(Node parentNode, String attributeName, String attributeValue){
            NodeList nodeList = parentNode.getChildNodes();
            for(int i = 0 ; i< nodeList.getLength(); i++){
                if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) nodeList.item(i);
                    if(element.getAttribute(attributeName).equals(attributeValue)){
                        return element;
                    }
                }
            }
            return null;
        }

        private Node createXmlNode(String tagName){
            return this.document.createElement(tagName);
        }

        private void addXmlNode(Node parent, Node newNode) {
            assert parent != null;
            parent.appendChild(newNode);
        }

        private void removeAllChildrenNode(Node parentNode) {
            while(parentNode.hasChildNodes()){
                parentNode.removeChild(parentNode.getFirstChild());
            }
        }

        private Node getNodeByTag(Node parentNode, String tagName) {
            NodeList nodeList = parentNode.getChildNodes();

            for(int i = 0 ; i < nodeList.getLength(); i++){
                if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) nodeList.item(i);
                    if(element.getTagName().equals(tagName)){
                        return nodeList.item(i);
                    }
                }
            }
            return null;
        }

        private void removeChildNode(Node parent, Node node) {
            parent.removeChild(node);
            int i = 0;
            while (parent.getChildNodes().item(i)!=null) {
                if (parent.getChildNodes().item(i).getNodeName().equalsIgnoreCase("#text")) {
                    parent.removeChild(parent.getChildNodes().item(i));
                }
                i=i+1;
            }
        }

        private void flush() throws TransformerException {
            DOMSource source = new DOMSource(this.document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            assert transformer != null;
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StreamResult result = new StreamResult(sourcePath);
            transformer.transform(source, result);
        }
    }


}
