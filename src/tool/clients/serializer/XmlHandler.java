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
    private final String sourcePath;

    private static XmlHandler xmlHandlerInstance;

    public static synchronized XmlHandler getXmlHandlerInstance(){
        if(xmlHandlerInstance == null){
            xmlHandlerInstance = new XmlHandler();
        }
        return xmlHandlerInstance;
    }

    private XmlHandler() {
        this.sourcePath = XmlCreator.getPath();
        this.document = buildDocument(sourcePath);
        this.xmlHelper = new XmlHelper(getDocument());
    }

    public void clearAllChildren() throws TransformerException {
        xmlHelper.removeAllChildren(getCategoriesNode());
        xmlHelper.removeAllChildren(getProjectsNode());
        xmlHelper.removeAllChildren(getDiagramsNode());
        xmlHelper.removeAllChildren(getLogsNode());
    }

    public void clearLogs() throws TransformerException {
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

    protected Node getLogsNode() {
        Node root = xmlHelper.getRootNode();
        return xmlHelper.getNodeByTag(root, XmlConstant.TAG_NAME_LOGS);
    }

    protected Node createElement(String name){
        return xmlHelper.createElement(name);
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

    public void addDiagramPreferencesElement(Element diagram, Node preferences) throws TransformerException {
        xmlHelper.addXmlElement(diagram, preferences);
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

    public void replaceNode(Node projectsNode, String hallo) throws TransformerException {
        Node newNode = xmlHelper.createElement(hallo);
        xmlHelper.getRootNode().replaceChild(newNode, projectsNode);

        xmlHelper.getRootNode().normalize();

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(new File(XmlConstant.USER_XML_FILE_NAME));
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
    }

    public void removeDiagram(FmmlxDiagram diagram) throws TransformerException {
        Node diagrams = getDiagramsNode();
        NodeList diagramsChildNodes = diagrams.getChildNodes();

        for(int i = 0 ; i< diagramsChildNodes.getLength(); i++){
            if(diagramsChildNodes.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) diagramsChildNodes.item(i);
                if(element.getAttribute(XmlConstant.ATTRIBUTE_ID).equals(diagram.getID()+"")
                        && element.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(diagram.getDiagramLabel())){
                    xmlHelper.removeChild(diagrams, element);
                }
            }
        }
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
                    if(element
                            .getAttribute(attributeName).equals(attributeValue)){
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

        public void removeAllChildren(Node parentNode) throws TransformerException {
            NodeList nodeList = parentNode.getChildNodes();
            for(int i = 0 ; i< nodeList.getLength(); i++){
                if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                    parentNode.removeChild(nodeList.item(i));
                }
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult result = new StreamResult(new File(XmlConstant.USER_XML_FILE_NAME));
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
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

        public void removeNodeByTag(Node parentNode, String tagName) throws TransformerException {
            NodeList childNodes = parentNode.getChildNodes();
            for(int i = 0 ; i< childNodes.getLength(); i++){
                if(childNodes.item(i).getNodeName().equals(tagName)){
                    parentNode.removeChild(childNodes.item(i));
                    return;
                }
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult result = new StreamResult(new File(XmlConstant.USER_XML_FILE_NAME));
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
        }


        public void removeChild(Node parent, Node node) throws TransformerException {
            parent.removeChild(node);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult result = new StreamResult(new File(XmlConstant.USER_XML_FILE_NAME));
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
        }
    }
}
