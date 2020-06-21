package tool.clients.fmmlxdiagrams.loghandler;

import org.junit.jupiter.api.Test;
import org.w3c.dom.*;

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

        Element root = (Element) xmlHandler.getRootNode();
        assertNotNull(root);
        assertEquals("diagram", root.getTagName());
    }

    @Test
    void addXmlLogElement(){

    }
}