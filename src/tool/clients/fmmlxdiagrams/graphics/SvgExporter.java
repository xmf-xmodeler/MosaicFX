package tool.clients.fmmlxdiagrams.graphics;

import tool.clients.fmmlxdiagrams.*;
import tool.clients.xmlManipulator.XmlCreator;
import tool.clients.xmlManipulator.XmlHandler;
import tool.helper.persistence.SerializerConstant;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.geometry.Bounds;

public class SvgExporter {
    private final XmlHandler xmlHandler;
    private final String filePath;
    private static final int version = SerializerConstant.SERIALIZER_VERSION;

    public SvgExporter(String filePath, Bounds bounds, double extraHeight) throws TransformerException, ParserConfigurationException {
        this.filePath = filePath;
        initUserSVGFile(filePath, bounds, extraHeight);
        this.xmlHandler = new XmlHandler(this.filePath);
    }

    private void initUserSVGFile(String file, Bounds bounds, double extraHeight) throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        xmlCreator.createSvg(file, bounds, extraHeight);
    }
    public void export(AbstractPackageViewer diagram, double extraHeight) throws TransformerException {
        ((FmmlxDiagram)diagram).paintToSvg(xmlHandler, extraHeight);
        this.xmlHandler.flushData();
    }

    public void createSvg(String file, Bounds bounds, double extraHeight) throws TransformerException, ParserConfigurationException {
        Document document = createDocument();
        initSvg(document, bounds, extraHeight);
        transformDocument(document, new File(file));
    }

    /*This function initializes the basic structure of the XML file that later will be able to be manipulated which aims to store FMMLXDiagram data.*/
    public static void initXML(Document document) {	
    	Element root = document.createElement(SerializerConstant.TAG_NAME_ROOT);
        document.appendChild(root);

        Element formatVersion = document.createElement(SerializerConstant.TAG_NAME_VERSION);
        formatVersion.setTextContent(String.valueOf(version));
        Element categories = document.createElement(SerializerConstant.TAG_NAME_CATEGORIES);
        Element projects = document.createElement(SerializerConstant.TAG_NAME_PROJECTS);
        Element diagrams = document.createElement(SerializerConstant.TAG_NAME_DIAGRAMS);
        Element logs = document.createElement(SerializerConstant.TAG_NAME_LOGS);
        
        root.appendChild(formatVersion);
        root.appendChild(categories);
        root.appendChild(projects);
        root.appendChild(diagrams);
        root.appendChild(logs);
    }

    /*This function initializes the basic structure of the XML file that later will be able to be manipulated which aims to store svg data.*/
    private void initSvg(Document document, Bounds bounds, double extraHeight){
        Element root = document.createElement(SvgConstant.TAG_NAME_ROOT);
        root.setAttribute("viewBox", bounds.getMinX()+" "+bounds.getMinY()+" "+bounds.getWidth()+" "+bounds.getHeight()+extraHeight+" ");
        root.setAttribute(SvgConstant.ATTRIBUTE_XMLNS, SvgConstant.XMLNS_VALUE);
        root.setAttribute(SvgConstant.ATTRIBUTE_XMLNS_XLINK, SvgConstant.XMLNS_XLINK_VALUE);
        root.setAttribute(SvgConstant.ATTRIBUTE_WIDTH, bounds.getWidth()+"");
        root.setAttribute(SvgConstant.ATTRIBUTE_HEIGHT, bounds.getHeight()+extraHeight*3+"");
        document.appendChild(root);
    }

    private Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(true);
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.newDocument();
    }

    /* this method transform a source tree into a result tree.
    * The method will also change the XML-node structure on the document to be easier to read (Beautify)
    * */
    private void transformDocument(Document document, File file) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(file);
        transformer.transform(domSource, streamResult);
    }
}
