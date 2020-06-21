package tool.clients.fmmlxdiagrams.serializer;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class XmlHandler {
    private final Document document;
    private XmlHelper xmlHelper;
    private Node currentLog;
    private static XmlHandler instance;

    protected static synchronized XmlHandler getInstance(){
        if(instance==null){
            instance = new XmlHandler("logTest.txt");
        }
        return instance;
    }

    public XmlHandler(String sourcePath) {
        this.document = buildDocument(sourcePath);
        this.xmlHelper = new XmlHelper(document);
        this.currentLog = null;
    }

    public XmlHelper getXmlHelper() {
        return xmlHelper;
    }

    public Document getDocument() {
        return document;
    }

    protected Node createLog(String name){
        return xmlHelper.createElement(name);
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

    //Logging
    protected void moveCurrentStateBackward(int diagramId){
        if(currentLog!=null){
            Node actionNode = getLogNode();
            assert actionNode != null;
            NodeList nodeList = actionNode.getChildNodes();
            int latestPosition=0;

            for(int i = 0; i<nodeList.getLength(); i++){
                if(nodeList.item(i).getNodeType()==Node.ELEMENT_NODE){
                    Element element = (Element) nodeList.item(i);
                    if(element.getAttributes().getNamedItem("current").getNodeValue().equals("1") && latestPosition==0){
                        this.currentLog = null;
                        return;
                    } else if(element.getAttributes().getNamedItem("current").getNodeValue().equals("1")){
                        setAllLogCurrentStateValueToNull();
                        nodeList.item(latestPosition).getAttributes().getNamedItem("current").setNodeValue("1");
                        this.currentLog = nodeList.item(latestPosition);
                        return;
                    }
                    latestPosition = i;
                }
            }
        }
    }

    protected void moveCurrentStateForward(int diagramId) {
        if(currentLog!=null){
            Node actionNode = getLogNode();
            assert actionNode != null;
            NodeList nodeList = actionNode.getChildNodes();
            int latestPosition=0;

            for(int i = 0; i<nodeList.getLength(); i++){
                if(nodeList.item(i).getNodeType()==Node.ELEMENT_NODE){
                    Element element = (Element) nodeList.item(i);
                    if(element.getAttributes().getNamedItem("current").getNodeValue().equals("1")){
                        latestPosition = i;
                        break;
                    }
                }
            }

            for(int i = latestPosition; i<nodeList.getLength(); i++){
                if(nodeList.item(i).getNodeType()==Node.ELEMENT_NODE){
                    Element element = (Element) nodeList.item(i);
                    setAllLogCurrentStateValueToNull();
                    element.getAttributes().getNamedItem("current").setNodeValue("1");
                    this.currentLog = element;
                }
            }
        }
    }

    protected void addXmlLogElement(Node node) throws TransformerException {
        setAllLogCurrentStateValueToNull();
        Element log = (Element) getLogNode();
        assert log != null;
        log.appendChild(node);

        DOMSource source = new DOMSource(document);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StreamResult result = new StreamResult("logTest.xml");
        transformer.transform(source, result);
    }

    protected Node getCurrentLog() {
        return currentLog;
    }

    protected Node getLogNode() {
        Node root = xmlHelper.getRootNode();
        return xmlHelper.getNodeByTag(root, "Logs");
    }

    //Serialize
    protected void saveState() {

        //TODO save all mapping element to Latest Save

        currentLog = null;
        assert getLogNode() != null;
        xmlHelper.removeAllChildren(getLogNode());
    }

    protected void getLatestSave(int diagramId){
        setAllLogCurrentStateValueToNull();
        this.currentLog = null;

        //TODO
    }

    //Helper
    private void setAllLogCurrentStateValueToNull() {
        Node logNode = getLogNode();
        assert logNode != null;
        NodeList logs = logNode.getChildNodes();
        //TODO
//        for (int i = 0 ; i<logs.getLength(); i++){
//            if(logs.item(i).getNodeType() == Node.ELEMENT_NODE){
//                Element element = (Element) logs.item(i);
//                if(element.getAttributes().getNamedItem("current").getNodeValue().equals("1")){
//                    element.getAttributes().getNamedItem("current").setNodeValue("0");
//                }
//            }
//        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        try{
            document.getDocumentElement().normalize();
            stringBuilder.append("Root element : ").append(xmlHelper.getRootNode().getNodeName());

            NodeList nList = xmlHelper.getRootNode().getChildNodes();

            stringBuilder.append("\n----------------------------");
            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    stringBuilder.append("\n"+eElement.getTagName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public class XmlHelper {
        private Document document;

        public XmlHelper(Document document) {
            this.document = document;
        }

        public Node getRootNode(){
            return document.getDocumentElement();
        }

        public Node getChildrenByAttributeValue(Node parentNode, String attributeName, String attributeValue){
            NodeList nodeList = parentNode.getChildNodes();
            for(int i = 0 ; i< nodeList.getLength(); i++){
                if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) nodeList.item(i);
                    if(element.getAttribute(attributeName).equals(attributeValue)){
                        return nodeList.item(i);
                    }
                }
            }
            return null;
        }

        public Node createElement(String tagName){
            Node item = null;
            item = document.createElement(tagName);
            return item;
        }

        public void addXmlElement(Node parentNode, Node node){
            parentNode.appendChild(node);
        }

        public void removeAllChildren(Node parentNode){
            NodeList nodeList = parentNode.getChildNodes();
            Element parent = (Element) parentNode;
            for(int i = 0 ; i< nodeList.getLength(); i++){
                Element element = (Element) nodeList.item(i);
                parent.removeChild(element);
            }
        }

        public Node getNodeByTag(Node parentNode, String tagName) {
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

        public Node getNodeByValue(Node parentNode, String value){
            NodeList nodeList = parentNode.getChildNodes();

            for(int i = 0 ; i < nodeList.getLength(); i++){
                if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) nodeList.item(i);
                    if(element.getNodeValue().equals(value)){
                        return nodeList.item(i);
                    }
                }
            }
            return null;
        }

        public void removeNodeByTag(Node parentNode, String tagName){
            NodeList childNodes = parentNode.getChildNodes();
            for(int i = 0 ; i< childNodes.getLength(); i++){
                if(childNodes.item(i).getNodeName().equals(tagName)){
                    parentNode.removeChild(childNodes.item(i));
                    return;
                }
            }
        }

        public void removeNodeByAttributeValue(Node parentNode, String attributeName, String attributeValue){
            parentNode.removeChild(getChildrenByAttributeValue(parentNode, attributeName, attributeValue));
        }


    }
}
