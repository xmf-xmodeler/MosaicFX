package tool.clients.serializer.interfaces;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.TimeOutException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Vector;

public interface ISerializer {

    void saveAsXml(Vector<FmmlxDiagram> diagram, String file) throws TransformerException, ParserConfigurationException, TimeOutException;
}
