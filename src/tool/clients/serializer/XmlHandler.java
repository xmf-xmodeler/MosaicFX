package tool.clients.serializer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class XmlHandler {
    private final Document document;
    private final XmlHelper xmlHelper;
    private static String sourcePath;

    public static synchronized XmlHandler getInstance(String file) {
        return new XmlHandler(file);
    }

    private XmlHandler(String sourcePath){
        XmlHandler.sourcePath = sourcePath;
        this.document = buildDocument(sourcePath);
        this.xmlHelper = new XmlHelper(getDocument());
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

    protected Node getLogsNode() {
        Node root = xmlHelper.getRootNode();
        return xmlHelper.getNodeByTag(root, XmlConstant.TAG_NAME_LOGS);
    }

    protected void addLogElement(Node logs, Node log) {
        xmlHelper.addXmlElement(logs, log);
    }

    public void addElement(Node parents, Element element) {
        xmlHelper.addXmlElement(parents, element);
    }

    public void addDiagramObjectsElement(Element diagram, Element objectsElement) {
        xmlHelper.addXmlElement(diagram, objectsElement);
    }

    public void addObjectElement(Node objects, Element objectElement) {
        xmlHelper.addXmlElement(objects, objectElement);
    }

    public void addEdgeElement(Element edges, Element newObject) {
        xmlHelper.addXmlElement(edges, newObject);
    }
    
    public void addLabelElement(Node labels, Element labelElement) {
        xmlHelper.addXmlElement(labels, labelElement);
    }

    public void addDiagramElement(Node diagrams, Element diagramElement) {
        xmlHelper.addXmlElement(diagrams, diagramElement);
    }

    public void addDiagramOwnersElement(Element diagram, Element ownersElement) {
        xmlHelper.addXmlElement(diagram, ownersElement);
    }

    public void addDiagramCategoriesElement(Element diagram, Element categoriesElement) {
        xmlHelper.addXmlElement(diagram, categoriesElement);
    }

    public void addDiagramEdgesElement(Element diagram, Element edgesElement) {
        xmlHelper.addXmlElement(diagram, edgesElement);
    }

	public void addDiagramLabelsElement(Element diagram, Element labelsElement) {
		xmlHelper.addXmlElement(diagram, labelsElement);
	}

    public void addAttributesElement(Element object, Element attributesElement) {
        xmlHelper.addXmlElement(object, attributesElement);
    }

    public void addDiagramPreferencesElement(Element diagram, Element preferencesElement) {
        xmlHelper.addXmlElement(diagram, preferencesElement);
    }

    public void addOperationsElement(Element object, Element operationsElement) {
        xmlHelper.addXmlElement(object, operationsElement);
    }

    public void addIntermediatePointsElement(Element edge, Element intermediatePointsElement) {
        xmlHelper.addXmlElement(edge, intermediatePointsElement);
    }

    public void removeAllProject() {
        xmlHelper.removeAllChildren(getProjectsNode());
        while(getProjectsNode().hasChildNodes()){
            getProjectsNode().removeChild(getProjectsNode().getFirstChild());
        }
    }

    public void removeDiagram(FmmlxDiagram diagram) {
        Node diagrams = getDiagramsNode();
        NodeList diagramsChildNodes = diagrams.getChildNodes();

        for(int i = 0 ; i< diagramsChildNodes.getLength(); i++){
            if(diagramsChildNodes.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) diagramsChildNodes.item(i);
                if(element.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(diagram.getDiagramLabel())
                        && element.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(diagram.getDiagramLabel())){
                    xmlHelper.removeChild(diagrams, element);
                }
            }
        }
    }

    public void clearAllChildren() {
        xmlHelper.removeAllChildren(getCategoriesNode());
        xmlHelper.removeAllChildren(getProjectsNode());
        xmlHelper.removeAllChildren(getDiagramsNode());
        xmlHelper.removeAllChildren(getLogsNode());
    }

    public void clearLogs() {
        xmlHelper.removeAllChildren(getLogsNode());
    }

    protected Element createXmlElement(String name){
        return xmlHelper.createXmlElement(name);
    }

    public void replaceNode(Node projectsNode, String newNodeName) {
        Node newNode = xmlHelper.createXmlElement(newNodeName);
        xmlHelper.getRootNode().replaceChild(newNode, projectsNode);
        xmlHelper.getRootNode().normalize();
    }

    public Node getChildWithName(Node diagramNone, String child) {
        return xmlHelper.getNodeByTag(diagramNone, child);
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

    public void flushData() throws TransformerException {
        xmlHelper.flush();
    }

    public static class XmlHelper {
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
                    if(element
                            .getAttribute(attributeName).equals(attributeValue)){
                        return nodeList.item(i);
                    }
                }
            }
            return null;
        }

        public Element createXmlElement(String tagName){
            Element item;
            item = document.createElement(tagName);
            return item;
        }

        protected void addXmlElement(Node parent, Node newNode) {
            Element parent1 = (Element) parent;
            assert parent1 != null;
            parent1.appendChild(newNode);
        }

        public void removeAllChildren(Node parentNode) {
            while(parentNode.hasChildNodes()){
                parentNode.removeChild(parentNode.getFirstChild());
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

        public void removeChild(Node parent, Node node) {
            parent.removeChild(node);
            int i = 0;
            while (parent.getChildNodes().item(i)!=null) {
                if (parent.getChildNodes().item(i).getNodeName().equalsIgnoreCase("#text")) {
                    parent.removeChild(parent.getChildNodes().item(i));
                }
                i=i+1;
            }
        }

        public void flush() throws TransformerException {
            DOMSource source = new DOMSource(document);

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
