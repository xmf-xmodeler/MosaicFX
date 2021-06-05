package tool.clients.fmmlxdiagrams;

import java.io.File;

import javafx.scene.canvas.GraphicsContext;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeImage extends NodeBaseElement implements NodeElement {
	
	public NodeImage(double x, double y, String iconSource, FmmlxProperty o, Action action) {
		super(x, y, o, action);
		this.image = new javafx.scene.image.Image(new File(iconSource).toURI().toString());
	}
	
	private javafx.scene.image.Image image;	

	@Override
	public void paintOn(GraphicsContext g, double xOffset, double yOffset, FmmlxDiagram diagram,
			boolean objectIsSelected) {
		g.drawImage(image, xOffset + x, yOffset + y - image.getHeight());
		
	}

	@Override
	public boolean isHit(double mouseX, double mouseY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, double x, double y) {

	}

}
