package tool.clients.serializer.interfaces;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.TimeOutException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Vector;

public interface Serializer {

    String initUserXMLFile(String file) throws TransformerException, ParserConfigurationException;

    void saveAsXml(Vector<FmmlxDiagram> diagrams) throws TransformerException, ParserConfigurationException, TimeOutException;

    void save(FmmlxDiagram diagram);

    boolean checkFileExist(String file);

    void clearAllData();
}
