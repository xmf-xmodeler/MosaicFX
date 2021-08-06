package tool.clients.xmlManipulator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.serializer.SerializerConstant;

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

/*As the name suggests, the XML Creator is a class that is tasked with making XML files needed later to save XML-Document.
*This class has two creators.
*   First, Creator to create XML files used to keep FMMlx Data Data.
*   Second, creator to make the SVG file used to export the FmmlxDiagram into a graphic asset*/
public class XmlCreator {
    private static final int version = SerializerConstant.SERIALIZER_VERSION;

    public void createXml(String file) throws TransformerException, ParserConfigurationException {
        Document document = createDocument();
        initXML(document);
        transformDocument(document, new File(file));
    }

    public void createSvg(String file, double width, double height) throws TransformerException, ParserConfigurationException {
        Document document = createDocument();
        initSvg(document, width, height);
        transformDocument(document, new File(file));
    }

    /*This function initializes the basic structure of the XML file that later will be able to be manipulated which aims to store FMMLXDiagram data.*/
    private void initXML(Document document) {
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
    private void initSvg(Document document, double width, double height){
        Element root = document.createElement(SvgConstant.TAG_NAME_ROOT);
        root.setAttribute(SvgConstant.ATTRIBUTE_XMLNS, SvgConstant.XMLNS_VALUE);
        root.setAttribute(SvgConstant.ATTRIBUTE_XMLNS_XLINK, SvgConstant.XMLNS_XLINK_VALUE);
        root.setAttribute(SvgConstant.ATTRIBUTE_WIDTH, width+"");
        root.setAttribute(SvgConstant.ATTRIBUTE_HEIGHT, height+"");
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
