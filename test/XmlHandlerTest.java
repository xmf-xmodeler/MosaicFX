import org.junit.jupiter.api.Test;
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
            xmlCreator.deleteUserXmlFile();
        }
    }

    @Test
    void clearAllChildrenTest() throws TransformerException {
        XmlHandler xmlHandler = XmlHandler.getXmlHandlerInstance();
        xmlHandler.clearAllChildren();
    }

    @Test
    void xmlReaderTest() throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        xmlCreator.create();
        XmlHandler xmlHandler = XmlHandler.getXmlHandlerInstance();
        assertNotNull(xmlHandler);
        System.out.println(xmlHandler.toString());
    }

    @Test
    void getRootElementTest() throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        xmlCreator.create();
        XmlHandler xmlHandler = XmlHandler.getXmlHandlerInstance();
        assertNotNull(xmlHandler);
        System.out.println(xmlHandler.getXmlHelper().getRootNode().getNodeName());
    }

    @Test
    void getDiagramsElementTest(){
        XmlHandler xmlHandler = XmlHandler.getXmlHandlerInstance();
        System.out.println(xmlHandler.getDiagramsNode().getNodeName());
    }

    @Test
    void removeProjectsNodeTest() throws TransformerException, ParserConfigurationException {
        XmlHandler xmlHandler = XmlHandler.getXmlHandlerInstance();
        assertNotNull(xmlHandler.getProjectsNode());
        xmlHandler.replaceNode(xmlHandler.getProjectsNode(), "Hallo");
    }
}