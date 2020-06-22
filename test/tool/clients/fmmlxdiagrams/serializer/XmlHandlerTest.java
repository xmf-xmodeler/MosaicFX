package tool.clients.fmmlxdiagrams.serializer;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import static org.junit.jupiter.api.Assertions.*;

class XmlHandlerTest {

    @Test
    void xmlCreateTest() throws TransformerException, ParserConfigurationException {
        XMLCreator xmlCreator = new XMLCreator();
        xmlCreator.create();
    }

    @Test
    void checkXmlFile() throws TransformerException, ParserConfigurationException {
        XMLCreator xmlCreator = new XMLCreator();
        xmlCreator.create();
        assertTrue(xmlCreator.checkFileExist());
    }

    @Test
    void deleteXmlFileTest(){
        XMLCreator xmlCreator = new XMLCreator();
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
        XMLCreator xmlCreator = new XMLCreator();
        xmlCreator.create();
        XmlHandler xmlHandler = new XmlHandler();
        assertNotNull(xmlHandler);
        System.out.println(xmlHandler.toString());
    }

    @Test
    void getRootElementTest() throws TransformerException, ParserConfigurationException {
        XMLCreator xmlCreator = new XMLCreator();
        xmlCreator.create();
        XmlHandler xmlHandler = new XmlHandler();
        assertNotNull(xmlHandler);
        System.out.println(xmlHandler.getXmlHelper().getRootNode().getNodeName());
    }

    @Test
    void addXmlLogElement() throws TransformerException, ParserConfigurationException {
        XMLCreator xmlCreator = new XMLCreator();
        xmlCreator.create();
        XmlHandler xmlHandler = new XmlHandler();
        LogManager logManager = new LogManager(xmlHandler);
        Element log = (Element) logManager.createLog("AddMetaClass");
        log.setAttribute("object_name", "metaclass2");
        log.setAttribute("diagramId", "1");
        logManager.addLog(log);
    }
}