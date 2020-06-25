import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
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
    void addXmlLogElement() throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        xmlCreator.create();
        XmlHandler xmlHandler = new XmlHandler();
        LogXmlManager logManager = new LogXmlManager(xmlHandler);
        Element log = (Element) logManager.createNewMetaClassLog("AddMetaClass", 1);
        log.setAttribute("object_name", "metaclass2");
        log.setAttribute("diagramId", "1");
        logManager.add(log);
    }
}