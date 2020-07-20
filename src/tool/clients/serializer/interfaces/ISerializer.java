package tool.clients.serializer.interfaces;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public interface ISerializer {
    void saveState(FmmlxDiagram diagram) throws TransformerException, ParserConfigurationException;
    void loadState(int diagramId, String diagramLabel);
}
