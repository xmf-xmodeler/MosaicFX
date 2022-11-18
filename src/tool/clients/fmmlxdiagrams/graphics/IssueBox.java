package tool.clients.fmmlxdiagrams.graphics;

//import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class IssueBox extends NodeBox {
	
	public final static double BOX_SIZE = 16;

	public IssueBox(double x, double y, double width, double height, Color bgColor, Color fgColor,
			LineWidthGetter lineWidth, PropertyType propertyType) {
		super(x, y, width, height, bgColor, fgColor, lineWidth, propertyType);
	}
	
//	@Override
//	public void paintOn(View diagramView, boolean objectIsSelected) {
//		super.paintOn(diagramView, objectIsSelected);
//			GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();
//			final double LAMP_SIZE = 2 * BOX_SIZE / (1 + Math.sqrt(5));
//			
//			double X1 = 0;
//			double X2 = width - BOX_SIZE;
//			double Y = 3;
//			
//			g.setTransform(getTotalTransform(diagramView.getCanvasTransform()));
//			
//			g.setFill(new Color(1., .8, 0., 1.));
//			if(System.currentTimeMillis()%1600<800) {
//				g.fillOval(X1 + BOX_SIZE/2 - LAMP_SIZE/2, Y + BOX_SIZE/2 - LAMP_SIZE/2, LAMP_SIZE, LAMP_SIZE);
//			} else {
//				g.fillOval(X2 + BOX_SIZE/2 - LAMP_SIZE/2, Y + BOX_SIZE/2 - LAMP_SIZE/2, LAMP_SIZE, LAMP_SIZE);
//			}
//	}
}
