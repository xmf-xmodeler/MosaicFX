package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;

import org.w3c.dom.Element;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeImage extends NodeBaseElement {
	String iconSource;
	public NodeImage(double x, double y, String iconSource, FmmlxProperty o, Action action) {
		super(new Affine(1,0,x,0,1,y), null, o, action);
		this.image = new javafx.scene.image.Image(new File(iconSource).toURI().toString());
		this.iconSource = iconSource;
//		bounds = new BoundingBox(0, - image.getHeight(), image.getWidth(), image.getHeight());
	}
	
	private javafx.scene.image.Image image;
//	private Bounds bounds;

	@Override
	public void paintOn(FmmlxDiagram.DiagramViewPane diagramView,
			boolean objectIsSelected) {
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();
		g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
		g.drawImage(image, 0, 0 - image.getHeight());		
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, FmmlxDiagram.DiagramViewPane diagram) {
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

	@Override
	public Bounds getBounds() {
		return null;
	}

	@Override
	public void updateBounds() {}

}
