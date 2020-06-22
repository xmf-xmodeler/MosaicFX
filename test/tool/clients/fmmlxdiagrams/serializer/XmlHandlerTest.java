package tool.clients.fmmlxdiagrams.serializer;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import static org.junit.jupiter.api.Assertions.*;

class XmlHandlerTest {

    @Test
    void xmlReaderTest(){
        XmlHandler xmlHandler = new XmlHandler("logTest.xml");
        assertNotNull(xmlHandler);
        System.out.println(xmlHandler.toString());
    }

    @Test
    void getRootElementTest(){
        XmlHandler xmlHandler = new XmlHandler("logTest.xml");
        assertNotNull(xmlHandler);
        System.out.println(xmlHandler.getXmlHelper().getRootNode().getNodeName());
    }

    @Test
    void addXmlLogElement(){
        XmlHandler xmlHandler = new XmlHandler("logTest.xml");
        LogManager logManager = new LogManager(xmlHandler);
        Element log = (Element) logManager.createLog("AddMetaClass");
        log.setAttribute("object_name", "metaclass2");
        log.setAttribute("diagramId", "1");
        logManager.addLog(log);
    }
}