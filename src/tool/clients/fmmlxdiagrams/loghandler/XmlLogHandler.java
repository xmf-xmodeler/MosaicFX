package tool.clients.fmmlxdiagrams.loghandler;

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

public class XmlLogHandler {

    private final Document document;

    public XmlLogHandler(String sourcePath) {
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

    public Node getRootNode(){
        return document.getDocumentElement();
    }

    public void backToLatestSave(){
        setAllLogCurrentStateValueToNull();
    }

    private void setAllLogCurrentStateValueToNull() {
        Node logNode = getActionLog();
        NodeList logs = logNode.getChildNodes();

        for (int i = 0 ; i<logs.getLength(); i++){
            if(logs.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) logs.item(i);
                if(element.getAttributes().getNamedItem("current").getNodeValue().equals("1")){
                    element.getAttributes().getNamedItem("current").setNodeValue("0");
                }
            }
        }
    }

    public void backToBeforeLatestActionNode(){
        Node actionNode = getActionLog();
        NodeList nodeList = actionNode.getChildNodes();
        setAllLogCurrentStateValueToNull();
        Node beforeLatestActionNode = nodeList.item(nodeList.getLength()-2);
        Element beforeLatestAction = (Element) beforeLatestActionNode;
        beforeLatestAction.getAttributes().getNamedItem("current").setNodeValue("1");
    }

    public void saveState() {
        //TODO
    }

    public void addXmlLogElement(Node node) {
        Node actionLog = getActionLog();
        //TODO
    }

    private Node getActionLog() {
        Node root = getRootNode();
        return getChildrenByAttributeValue(root, "type", "PROCESS");
    }

    public Node getCurrentLog() {
        Node logNode = getLogNode();
        if(logNode!=null){
            //TODO
        }
        return null;
    }

    private Node getLatestSaveNode() {
        Node root = getRootNode();
        return getChildrenByAttributeValue(root, "type", "LATESTSAVE");
    }

    private Node getLogNode() {
        Node root = getRootNode();
        Node logNode = getChildrenByAttributeValue(root, "type", "PROCESS");
        NodeList logs = logNode.getChildNodes();

        for (int i = 0 ; i<logs.getLength(); i++){
            if(logs.item(i).getNodeType() == Node.ELEMENT_NODE){
                return logNode;
            }
        }
        return null;
    }

    public Node getChildrenById(Node parentNode, String id){
        NodeList nodeList = parentNode.getChildNodes();
        for(int i = 0 ; i< nodeList.getLength(); i++){
            if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) nodeList.item(i);
                if(element.getAttribute("id").equals(id)){
                    return nodeList.item(i);
                }
            }
        }
        return null;
    }

    public Node getChildrenByAttributeValue(Node parentNode, String attributeType, String attributeValue){
        NodeList nodeList = parentNode.getChildNodes();
        for(int i = 0 ; i< nodeList.getLength(); i++){
            if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) nodeList.item(i);
                if(element.getAttribute(attributeType).equals(attributeValue)){
                    return nodeList.item(i);
                }
            }
        }
        return null;
    }

    public void removeLastChild(Node parentNode){
        NodeList nodeList = parentNode.getChildNodes();
        Node lastItem = nodeList.item(nodeList.getLength()-1);
        Element element = (Element) parentNode;
        element.removeChild(lastItem);
    }

    public void removeAllChildren(Node parentNode){
        NodeList nodeList = parentNode.getChildNodes();
        Element parent = (Element) parentNode;
        for(int i = 0 ; i< nodeList.getLength(); i++){
            Element element = (Element) nodeList.item(i);
            parent.removeChild(element);
        }
    }

    public void removeNodeByTag(Node parentNode, String tag){
        NodeList nodeList = parentNode.getChildNodes();
        for(int i = 0 ; i< nodeList.getLength(); i++){
            if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) nodeList.item(i);
                if(element.getTagName().equals(tag)){
                    element.getParentNode().removeChild(element);
                }
            }
        }
    }
}
