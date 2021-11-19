package tool.clients.fmmlxdiagrams.graphics;

import tool.clients.fmmlxdiagrams.*;
import tool.clients.xmlManipulator.XmlCreator;
import tool.clients.xmlManipulator.XmlHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class SvgExporter {
    private final XmlHandler xmlHandler;
    private final String filePath;

    public SvgExporter(String filePath, double width, double height) throws TransformerException, ParserConfigurationException {
        this.filePath = filePath;
        initUserSVGFile(filePath, width, height);
        this.xmlHandler = new XmlHandler(this.filePath);
    }

    public void initUserSVGFile(String file, double width, double height) throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        xmlCreator.createSvg(file, width, height);
    }
    public void export(AbstractPackageViewer diagram, double extraHeight) throws TransformerException {
        ((FmmlxDiagram)diagram).paintToSvg(xmlHandler, extraHeight);
        this.xmlHandler.flushData();
    }

    public void clearAllData() {
        xmlHandler.removeAllChildren(xmlHandler.getRoot());
    }
}
