package tool.clients.fmmlxdiagrams.loghandler;

import org.junit.jupiter.api.Test;
import org.w3c.dom.*;

import static org.junit.jupiter.api.Assertions.*;

class XmlLogHandlerTest {

    @Test
    void xmlReaderTest(){
        XmlLogHandler xmlLogHandler = new XmlLogHandler("mappingTest.xml");
        assertNotNull(xmlLogHandler);
        System.out.println(xmlLogHandler.toString());
    }

    @Test
    void getRootElementTest(){
        XmlLogHandler xmlLogHandler = new XmlLogHandler("mappingTest.xml");
        assertNotNull(xmlLogHandler);

        Element root = (Element) xmlLogHandler.getRootNode();
        assertNotNull(root);
        assertEquals("mapping", root.getTagName());
    }

    @Test void getChildrenByIdTest(){
        XmlLogHandler xmlLogHandler = new XmlLogHandler("mappingTest.xml");
        assertNotNull(xmlLogHandler);

        Node root = xmlLogHandler.getRootNode();
        assertNotNull(root);

        Element element = (Element) xmlLogHandler.getChildrenById(root, "2");
        assertEquals("object", element.getTagName());
        assertEquals("2", element.getAttribute("id"));
    }

    @Test void getMappingListToStringTest(){
        XmlLogHandler xmlLogHandler = new XmlLogHandler("mappingTest.xml");
        assertNotNull(xmlLogHandler);

        Element root = (Element) xmlLogHandler.getRootNode();
        assertNotNull(root);

        NodeList nodeList = root.getChildNodes();
        assertNotNull(nodeList);
        assertTrue(nodeList.getLength() > 0);

        for(int i = 0 ; i<nodeList.getLength(); i++){
            if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) nodeList.item(i);
                assertNotNull(element);
                System.out.println("tag name : " +element.getTagName());
                System.out.println("id : "+element.getAttribute("id"));

                NodeList logList = element.getChildNodes();
                assertNotNull(logList);
                for (int j = 0 ; j< logList.getLength(); j++){
                    if(logList.item(j).getNodeType() == Node.ELEMENT_NODE){
                    Element log = (Element) logList.item(j);
                    assertNotNull(log);
                        System.out.println(log.getTagName());

                        NodeList mapping = log.getChildNodes();
                        assertNotNull(mapping);
                        for (int k = 0 ; k<mapping.getLength(); k++){
                            if(mapping.item(k).getNodeType()==Node.ELEMENT_NODE){
                                Element mapElement = (Element) mapping.item(k);
                                assertNotNull(mapElement);
                                System.out.println(mapElement.getTagName() +" : "+ mapElement.getTextContent());
                            }
                        }
                    }
                }
                System.out.println();
            }
        }
    }
}