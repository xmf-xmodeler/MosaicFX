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
        Logger logger = new Logger(xmlHandler);
        Element log = (Element) logger.createLog("test");
        log.setAttribute("object_name", "object1");
        logger.addLog(log);
    }
}