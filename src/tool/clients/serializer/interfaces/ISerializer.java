package tool.clients.serializer.interfaces;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public interface ISerializer {

    void saveAsXml(FmmlxDiagram diagram, String file) throws TransformerException, ParserConfigurationException;
}
