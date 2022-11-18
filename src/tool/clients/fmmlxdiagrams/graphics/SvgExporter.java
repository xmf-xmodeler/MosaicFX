package tool.clients.fmmlxdiagrams.graphics;

import tool.clients.fmmlxdiagrams.*;
import tool.clients.xmlManipulator.XmlCreator;
import tool.clients.xmlManipulator.XmlHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import javafx.geometry.Bounds;

public class SvgExporter {
    private final XmlHandler xmlHandler;
    private final String filePath;

    public SvgExporter(String filePath, Bounds bounds, double extraHeight) throws TransformerException, ParserConfigurationException {
        this.filePath = filePath;
        initUserSVGFile(filePath, bounds, extraHeight);
        this.xmlHandler = new XmlHandler(this.filePath);
    }

    public void initUserSVGFile(String file, Bounds bounds, double extraHeight) throws TransformerException, ParserConfigurationException {
        XmlCreator xmlCreator = new XmlCreator();
        xmlCreator.createSvg(file, bounds, extraHeight);
    }
    public void export(AbstractPackageViewer diagram, double extraHeight) throws TransformerException {
        ((FmmlxDiagram)diagram).paintToSvg(xmlHandler, extraHeight);
        this.xmlHandler.flushData();
    }

    public void clearAllData() {
        xmlHandler.removeAllChildren(xmlHandler.getRoot());
    }
}
