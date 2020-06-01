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
    private Node currentLog;

    public XmlLogHandler(String sourcePath) {
        this.document = buildDocument(sourcePath);
        this.currentLog = null;
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
        this.currentLog = null;
    }

    private void setAllLogCurrentStateValueToNull() {
        Node logNode = getActionLogNode();
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

    public void moveCurrentStateBackward(){
        if(currentLog!=null){
            Node actionNode = getActionLogNode();
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

    public void moveCurrentStateForward() {
        if(currentLog!=null){
            Node actionNode = getActionLogNode();
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

    public void saveState() {

        //TODO save all mapping element to Latest Save

        currentLog = null;
        removeAllChildren(getActionLogNode());
    }

    public void addXmlLogElement(Node node) {
        setAllLogCurrentStateValueToNull();
        Element actionLog = (Element) getActionLogNode();
        node.getAttributes().getNamedItem("current").setNodeValue("1");
        actionLog.appendChild(node);
        currentLog=node;
    }

    public Node getCurrentLog() {
        return currentLog;
    }

    public Node getLatestSaveNode() {
        Node root = getRootNode();
        return getChildrenByAttributeValue(root,
                "type", "LATESTSAVE");
    }

    private Node getActionLogNode() {
        Node root = getRootNode();
        return getChildrenByAttributeValue(root,
                "type", "PROCESS");
    }

    protected Node getChildrenById(Node parentNode, String id){
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

    private Node getChildrenByAttributeValue(Node parentNode, String attributeName, String attributeValue){
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

    private void removeAllChildren(Node parentNode){
        NodeList nodeList = parentNode.getChildNodes();
        Element parent = (Element) parentNode;
        for(int i = 0 ; i< nodeList.getLength(); i++){
            Element element = (Element) nodeList.item(i);
            parent.removeChild(element);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        try{
            document.getDocumentElement().normalize();
            stringBuilder.append("Root element : ").append(getRootNode().getNodeName());

            NodeList nList = getRootNode().getChildNodes();

            stringBuilder.append("\n----------------------------");
            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    stringBuilder.append("\nName : ").append(eElement.getTagName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
