package tool.clients.serializer.interfaces;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.TimeOutException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Vector;

public interface Serializer {

    String initUserXMLFile(String file) throws TransformerException, ParserConfigurationException;

    void saveAsXml(String diagramPath, String initLabel, FmmlxDiagramCommunicator communicator) throws TimeOutException, TransformerException;

    void save(String diagramPath, String label, Integer id, FmmlxDiagramCommunicator communicator);

    boolean checkFileExist(String file);

    void clearAllData();
}
