package tool.clients.fmmlxdiagrams.serializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XMLCreator {
    private static final String path= "userXmlFile.xml";
    private static final int version = 1;

    public XMLCreator() {
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

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            Element root = document.createElement("XModeler");
            document.appendChild(root);

            Element formatVersion = document.createElement("Version");
            formatVersion.setTextContent(String.valueOf(version));

            Element categories = document.createElement("Categories");
            Element packages = document.createElement("Packages");
            Element diagrams = document.createElement("Diagrams");
            Element objects = document.createElement("Objects");
            Element logs = document.createElement("Logs");

            root.appendChild(formatVersion);
            root.appendChild(categories);
            root.appendChild(packages);
            root.appendChild(diagrams);
            root.appendChild(objects);
            root.appendChild(logs);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(path));

            transformer.transform(domSource, streamResult);
        } else {
            //All this Codes are just for Test purpose
            deleteUserXmlfile();
            create();
            //----------------------------------------
        }
    }

    public void deleteUserXmlfile(){
        File file = new File(path);
        file.delete();
    }
}
