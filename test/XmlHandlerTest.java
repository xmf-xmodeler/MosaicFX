import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.serializer.LogXmlManager;
import tool.clients.serializer.XmlCreator;
import tool.clients.serializer.XmlHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import static org.junit.jupiter.api.Assertions.*;

class XmlHandlerTest {

    @Test
    void xmlCreateTest() throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        xmlCreator.create();
    }

    @Test
    void checkXmlFile() throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        xmlCreator.create();
        assertTrue(xmlCreator.checkFileExist());
    }

    @Test
    void deleteXmlFileTest(){
        XmlCreator xmlCreator = new XmlCreator();
        if(xmlCreator.checkFileExist()){
            xmlCreator.deleteUserXmlfile();
        }
    }

    @Test
    void clearAllChildrenTest(){
        XmlHandler xmlHandler = new XmlHandler();
        xmlHandler.clearAllChildren();
    }

    @Test
    void xmlReaderTest() throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        xmlCreator.create();
        XmlHandler xmlHandler = new XmlHandler();
        assertNotNull(xmlHandler);
        System.out.println(xmlHandler.toString());
    }

    @Test
    void getRootElementTest() throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        xmlCreator.create();
        XmlHandler xmlHandler = new XmlHandler();
        assertNotNull(xmlHandler);
        System.out.println(xmlHandler.getXmlHelper().getRootNode().getNodeName());
    }

    @Test
    void getDiagramsElementTest(){
        XmlHandler xmlHandler = new XmlHandler();
        System.out.println(xmlHandler.getDiagramsNode().getNodeName());
    }

    @Test
    void removeProjectsNodeTest() throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        xmlCreator.create();
        XmlHandler xmlHandler = new XmlHandler();
        Node root = xmlHandler.getXmlHelper().getRootNode();
        Node Projects = xmlHandler.getProjectsNode();
        xmlHandler.getXmlHelper().removeNodeByTag(root, "Version");
    }
}