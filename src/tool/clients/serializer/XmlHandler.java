package tool.clients.serializer;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

public class XmlHandler {
    private final Document document;
    private final XmlHelper xmlHelper;
    private Node currentLog;
    private final String sourcePath;

    public XmlHandler() {
        this.sourcePath = XmlCreator.getPath();
        this.document = buildDocument(sourcePath);
        this.xmlHelper = new XmlHelper(getDocument());
        this.currentLog = null;
    }

    public void clearAllChildren(){
        xmlHelper.removeAllChildren(getCategoriesNode());
        xmlHelper.removeAllChildren(getProjectsNode());
        xmlHelper.removeAllChildren(getDiagramsNode());
        xmlHelper.removeAllChildren(getLogsNode());
    }

    public void clearLogs(){
        xmlHelper.removeAllChildren(getLogsNode());
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
        return xmlHelper;
    }

    public Document getDocument() {
        return document;
    }

    public Node getCategoriesNode(){
        Node root = xmlHelper.getRootNode();
        return xmlHelper.getNodeByTag(root, XmlConstant.TAG_NAME_CATEGORIES);
    }

    public Node getProjectsNode(){
        Node root = xmlHelper.getRootNode();
        return xmlHelper.getNodeByTag(root, XmlConstant.TAG_NAME_PROJECTS);
    }

    public Node getDiagramsNode() {
        Node root = xmlHelper.getRootNode();
        return xmlHelper.getNodeByTag(root, XmlConstant.TAG_NAME_DIAGRAMS);
    }

    public Node getObjectsNode() {
        Node root = xmlHelper.getRootNode();
        return xmlHelper.getNodeByTag(root, XmlConstant.TAG_NAME_OBJECTS);
    }

    protected Node getLogsNode() {
        Node root = xmlHelper.getRootNode();
        return xmlHelper.getNodeByTag(root, XmlConstant.TAG_NAME_LOGS);
    }

    protected Node createElement(String name){
        return xmlHelper.createElement(name);
    }

    protected void moveCurrentStateBackward(int diagramId){
        if(currentLog!=null){
            Node actionNode = getLogsNode();
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
            Node actionNode = getLogsNode();
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

    protected void addLogElement(Node logs, Node log)
            throws TransformerException {
        xmlHelper.addXmlElement(logs, log);
    }

    public void addElement(Node parents, Node node) throws TransformerException {
        xmlHelper.addXmlElement(parents, node);
    }

    public void addDiagramObjectsElement(Element diagram, Node objects) throws TransformerException {
        xmlHelper.addXmlElement(diagram, objects);
    }

    public void addObjectElement(Node objects, Node object)
            throws TransformerException {
        xmlHelper.addXmlElement(objects, object);
    }

    public void addEdgeElement(Element edges, Element newObject)
            throws TransformerException {
        xmlHelper.addXmlElement(edges, newObject);
    }

    public void addDiagramElement(Node diagrams, Node diagram)
            throws TransformerException {
        xmlHelper.addXmlElement(diagrams, diagram);
    }

    public void addDiagramOwnersElement(Element diagram, Node owners)
            throws TransformerException {
        xmlHelper.addXmlElement(diagram, owners);
    }

    public void addDiagramCategoriesElement(Element diagram, Node categories)
            throws TransformerException {
        xmlHelper.addXmlElement(diagram, categories);
    }

    public void addDiagramEdgesElement(Element diagram, Node edges)
            throws TransformerException {
        xmlHelper.addXmlElement(diagram, edges);
    }

    public void addDiagramPreferencesElemet(Element diagram, Node preferences) throws TransformerException {
        xmlHelper.addXmlElement(diagram, preferences);
    }

    protected Node getCurrentLog() {
        return currentLog;
    }

    protected void clearAllXmlChildren() {
        xmlHelper.removeAllChildren(getProjectsNode());
        xmlHelper.removeAllChildren(getCategoriesNode());
        xmlHelper.removeAllChildren(getDiagramsNode());
        xmlHelper.removeAllChildren(getLogsNode());
    }

    protected void getLatestSave(int diagramId, String diagramLabel){
        setAllLogCurrentStateValueToNull();
        this.currentLog = null;

        //TODO
    }

    private void setAllLogCurrentStateValueToNull() {
        Node logNode = getLogsNode();
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
                    stringBuilder.append("\n").append(eElement.getTagName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public void removeDiagramsChildren() {
        xmlHelper.removeAllChildren(getDiagramsNode());
    }




    public class XmlHelper {
        private final Document document;

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
            Node item;
            item = document.createElement(tagName);
            return item;
        }

        protected void addXmlElement(Node parent, Node node) throws TransformerException {
            Element parent1 = (Element) parent;
            assert parent1 != null;
            parent1.appendChild(node);

            DOMSource source = new DOMSource(document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StreamResult result = new StreamResult(sourcePath);
            transformer.transform(source, result);
        }

        public void removeAllChildren(Node parentNode){
            NodeList nodeList = parentNode.getChildNodes();
            for(int i = 0 ; i< nodeList.getLength(); i++){
                if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                    parentNode.removeChild(nodeList.item(i));
                }
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

        public void removeNodeByTag(Node parentNode, String tagName) throws TransformerException {
            NodeList childNodes = parentNode.getChildNodes();
            for(int i = 0 ; i< childNodes.getLength(); i++){
                if(childNodes.item(i).getNodeName().equals(tagName)){
                    parentNode.removeChild(childNodes.item(i));
                    return;
                }
            }

            parentNode.normalize();

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
        }

        public void removeNodeByAttributeValue(Node parentNode, String attributeName, String attributeValue){
            parentNode.removeChild(getChildrenByAttributeValue(parentNode, attributeName, attributeValue));
        }


    }
}
