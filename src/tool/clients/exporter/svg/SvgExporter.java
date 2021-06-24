package tool.clients.exporter.svg;

import tool.clients.fmmlxdiagrams.*;
import tool.clients.xmlManipulator.XmlCreator;
import tool.clients.xmlManipulator.XmlHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class SvgExporter {
    private final XmlHandler xmlHandler;
    private final String file;

    public SvgExporter(String file, double width, double height) throws TransformerException, ParserConfigurationException {
        this.file = initUserSVGFile(file, width, height);
        this.xmlHandler = new XmlHandler(this.file);
    }

    public String initUserSVGFile(String file, double width, double height) throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        return xmlCreator.createSvg(file, width, height);
    }
    public void export(AbstractPackageViewer diagram) throws TransformerException {
        ((FmmlxDiagram)diagram).paintToSvg(xmlHandler);
        this.xmlHandler.flushData();
    }

    public void clearAllData() {
        xmlHandler.removeAllChildren(xmlHandler.getRoot());
    }
}
