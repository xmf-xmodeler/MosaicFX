package tool.clients.serializer.interfaces;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public interface ISerializer {
    void saveState(FmmlxDiagram diagram) throws TransformerException, ParserConfigurationException;
    void loadState(String path, FmmlxDiagramCommunicator fmmlxDiagramCommunicator);
}
