package tool.xmodeler;

import javafx.scene.canvas.GraphicsContext;

public class Class extends Operation {

	public Class() {
		name = "Class";
	}

	@Override
	public void draw(GraphicsContext gc, double xPos, double yPos) {
		gc.strokeRect(xPos, yPos, 50, 20);
	}

}
