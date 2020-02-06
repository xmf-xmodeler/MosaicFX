package tool.clients.fmmlxdiagrams;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class IssueBox extends NodeBox{
	
	public final static double BOX_SIZE = 16;

	public IssueBox(double x, double y, double width, double height, Paint bgColor, Paint fgColor,
			LineWidthGetter lineWidth, PropertyType propertyType) {
		super(x, y, width, height, bgColor, fgColor, lineWidth, propertyType);
	}
	
	@Override
	public void paintOn(GraphicsContext g, double xOffset, double yOffset, FmmlxDiagram diagram, boolean objectIsSelected) {
		super.paintOn(g, xOffset, yOffset, diagram, objectIsSelected);
		
			final double LAMP_SIZE = 2 * BOX_SIZE / (1 + Math.sqrt(5));
			
			double X1 = x + xOffset;
			double X2 = x + xOffset + width - BOX_SIZE;
			double Y = y + yOffset + 3;
			
//			g.setFill(Color.BLACK);
//			g.fillRect(X1, Y, BOX_SIZE, BOX_SIZE);
//			g.fillRect(X2, Y, BOX_SIZE, BOX_SIZE);

			g.setFill(new Color(1., .8, 0., 1.));
			if(System.currentTimeMillis()%1600<800) {
				g.fillOval(X1 + BOX_SIZE/2 - LAMP_SIZE/2, Y + BOX_SIZE/2 - LAMP_SIZE/2, LAMP_SIZE, LAMP_SIZE);
			} else {
				g.fillOval(X2 + BOX_SIZE/2 - LAMP_SIZE/2, Y + BOX_SIZE/2 - LAMP_SIZE/2, LAMP_SIZE, LAMP_SIZE);
			}
	}
}
