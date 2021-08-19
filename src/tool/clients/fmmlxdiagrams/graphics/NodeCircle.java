package tool.clients.fmmlxdiagrams.graphics;

import org.w3c.dom.Element;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeCircle extends NodeBaseElement{
	final double diameter;
	final Paint bgColor;
	final Paint fgColor;

	public NodeCircle(double x, double y, double diameter, Paint bgColor, FmmlxProperty actionObject, Action action) {
		super(x, y, actionObject, action);
		this.diameter = diameter;
		this.fgColor = Color.BLACK;
		this.bgColor = bgColor;
	}

	@Override
	public void paintOn(GraphicsContext g, Affine transform, FmmlxDiagram diagram,
			boolean objectIsSelected) {
		try {
			g.setFill(bgColor);
			g.fillOval(x + transform.getTx(), y + transform.getTy(), diameter, diameter);
			g.setStroke(/*objectIsSelected&&System.currentTimeMillis()%2400<500?new Color(1.,.8,0.,1.):*/fgColor);
			g.setLineWidth(1);
			g.strokeOval(x + transform.getTx(), y + transform.getTy(), diameter, diameter);
		} catch (Exception e){
			e.printStackTrace();
		}		
	}

	@Override
	public boolean isHit(double mouseX, double mouseY) {
		return  (x - mouseX + diameter/2) * 
				(x - mouseX + diameter/2) + 
				(y - mouseY + diameter/2) * 
				(y - mouseY + diameter/2) < diameter * diameter / 4;
	}

	@Override
	public void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element group, double xOffset, double yOffset,
			boolean objectIsSelected) { throw new RuntimeException("Not yet implemented!");}
	
	

}
