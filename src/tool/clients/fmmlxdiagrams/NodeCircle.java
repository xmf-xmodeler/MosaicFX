package tool.clients.fmmlxdiagrams;

import org.w3c.dom.Element;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
	public void paintOn(GraphicsContext g, double xOffset, double yOffset, FmmlxDiagram diagram,
			boolean objectIsSelected) {
		try {
			g.setFill(bgColor);
			g.fillOval(x + xOffset, y + yOffset, diameter, diameter);
			g.setStroke(/*objectIsSelected&&System.currentTimeMillis()%2400<500?new Color(1.,.8,0.,1.):*/fgColor);
			g.setLineWidth(1);
			g.strokeOval(x + xOffset, y + yOffset, diameter, diameter);
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
