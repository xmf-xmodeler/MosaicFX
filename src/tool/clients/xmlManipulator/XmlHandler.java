package tool.clients.xmlManipulator;

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

/*XMLHandler is a major class for the XML/SVG Manipulation process.
XMLHandler can also be considered as an interface that makes it easy for us to manipulate XML-Document.
This class manage two important components, namely XMLHelper and DOCUMENT.
In addition, this class also has functions that are not found in basic functions on DOM-Library*/
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

    /*This method serves to create a dom-document instance,
    then copy the existing data on the XML/SVG-file then transformed into a dom-document that can be manipulated.*/
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

    //This method remove an element from parent-element if parent-element has child-element with certain TAG
    @Deprecated
    public void removeChildElement(Element parent, Element children){
        getXmlHelper().removeChildNode(parent, children);
    }

    @Deprecated
    public void removeAllChildren(Element element){
        getXmlHelper().removeAllChildrenNode(element);
    }

    public Element createXmlElement(String name){
        return (Element) getXmlHelper().createXmlNode(name);
    }

    @Deprecated
    public void replaceElement(Element oldElement, String newNodeName) {
        Node newNode = getXmlHelper().createXmlNode(newNodeName);
        getXmlHelper().getRootNode().replaceChild(newNode, oldElement);
        getXmlHelper().getRootNode().normalize();
    }

    //This method return an element if parent-element has child-element with certain TAG
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

    //This methode store manipulated dom-document to the existing xml-file
    public void flushData() throws TransformerException {
        getXmlHelper().flush();
    }

    public Element getRoot() {
        return (Element) getXmlHelper().getRootNode();
    }

    /*
    *XMLHelper is a class that functions to be the main communicator between the XML-File and Dom-Document.
    *Before being manipulated, the data from the XML-file loaded into Dom-Document and the Instance of this DOM-Document will be manipulated which can later be saved back to the real XML-file.
    *Besides, XMLHelper has basic functions to manipulate XML-Document.*/
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

        //This function makes one XML-node with the name of the tag according to the parameters
        private Node createXmlNode(String tagName){
            return this.document.createElement(tagName);
        }

        private void addXmlNode(Node parent, Node newNode) {
            assert parent != null;
            parent.appendChild(newNode);
        }

        @Deprecated
        private void removeAllChildrenNode(Node parentNode) {
            while(parentNode.hasChildNodes()){
                parentNode.removeChild(parentNode.getFirstChild());
            }
        }

        //This method returns a node according to its name from a certain parent node
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

        //This removes a certain childNode from from its parent
        @Deprecated private void removeChildNode(Node parent, Node node) {
            parent.removeChild(node);
            int i = 0;
            while (parent.getChildNodes().item(i)!=null) {
                if (parent.getChildNodes().item(i).getNodeName().equalsIgnoreCase("#text")) {
                    parent.removeChild(parent.getChildNodes().item(i));
                }
                i=i+1;
            }
        }

        //This methode store manipulated dom-document to the existing xml-file
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
