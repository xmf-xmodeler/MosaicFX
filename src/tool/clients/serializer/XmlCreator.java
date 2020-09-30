package tool.clients.serializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import java.nio.file.Files;
import java.nio.file.Paths;

public class XmlCreator {
    public static final String path= XmlConstant.USER_XML_FILE_NAME;
    private static final int version = XmlConstant.SERIALIZER_VERSION;

    public XmlCreator() {
    }

    public static String getPath() {
        return path;
    }

    public boolean checkFileExist(){
        return Files.exists(Paths.get(path));
    }

    public void create() throws ParserConfigurationException,
            TransformerException {
        if(!checkFileExist()){

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(true);
            documentBuilderFactory.setIgnoringElementContentWhitespace(true);

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            Element root = document.createElement(XmlConstant.TAG_NAME_ROOT);
            document.appendChild(root);

            Element formatVersion = document.createElement(XmlConstant.TAG_NAME_VERSION);
            formatVersion.setTextContent(String.valueOf(version));

            Element categories = document.createElement(XmlConstant.TAG_NAME_CATEGORIES);
            Element projects = document.createElement(XmlConstant.TAG_NAME_PROJECTS);
            Element diagrams = document.createElement(XmlConstant.TAG_NAME_DIAGRAMS);
            Element logs = document.createElement(XmlConstant.TAG_NAME_LOGS);

            root.appendChild(formatVersion);
            root.appendChild(categories);
            root.appendChild(projects);
            root.appendChild(diagrams);
            root.appendChild(logs);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(path));
            transformer.transform(domSource, streamResult);
        }
    }

    public void deleteUserXmlFile(){
        File file = new File(path);
        file.delete();
    }
}
