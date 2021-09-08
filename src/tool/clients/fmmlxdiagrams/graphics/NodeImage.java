package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

import javafx.scene.canvas.GraphicsContext;

import org.w3c.dom.Element;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeImage extends NodeBaseElement implements NodeElement {
	String iconSource;
	public NodeImage(double x, double y, String iconSource, FmmlxProperty o, Action action) {
		super(x, y, o, action);
		this.image = new javafx.scene.image.Image(new File(iconSource).toURI().toString());
		this.iconSource = iconSource;
	}
	
	private javafx.scene.image.Image image;	

	@Override
	public void paintOn(GraphicsContext g, FmmlxDiagram.DiagramViewPane diagram,
			boolean objectIsSelected) {
		g.setTransform(getTotalTransform(diagram.getCanvasTransform()));
		g.drawImage(image, 0, 0 - image.getHeight());		
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, GraphicsContext g, FmmlxDiagram.DiagramViewPane diagram) {
		return false;
	}

	@Override
	public void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element parentGroup) {
		Element group = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_GROUP);
		group.setAttribute(SvgConstant.ATTRIBUTE_TRANSFORM, "matrix(1,0,0,1,"+getMyTransform().getTx()+","+getMyTransform().getTy()+")");
		xmlHandler.addXmlElement(parentGroup, group);

		Element imageElement = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_IMAGE);
		imageElement.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, "0");
		imageElement.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y,(- image.getHeight())+"");
		imageElement.setAttribute(SvgConstant.ATTRIBUTE_XLINK_HREF, "data:image/png;base64,"+encodeFileToBase64Binary(new File(iconSource)));
		xmlHandler.addXmlElement(group,imageElement);
	}

	private static String encodeFileToBase64Binary(File file){
		String encodedfile = null;
		try {
			FileInputStream fileInputStreamReader = new FileInputStream(file);
			byte[] bytes = new byte[(int)file.length()];
			fileInputStreamReader.read(bytes);
			encodedfile = Base64.getEncoder().encodeToString(bytes);
			fileInputStreamReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 

		return encodedfile;
	}

}
