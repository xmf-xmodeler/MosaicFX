package tool.xmodeler;

import javafx.scene.canvas.GraphicsContext;

public class Circle extends Operation {
	

	public Circle() {
		name = "Circle";
	}

	@Override
	public void draw(GraphicsContext gc, double xPos, double yPos) {
		gc.strokeOval(xPos, yPos, 50, 50);
		gc.fillText("BEsipiel", xPos, yPos+10);
		gc.strokeText("StrokeText", xPos, yPos+40);
		
	}
	
	

}
