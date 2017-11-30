package tool.xmodeler;

import javafx.scene.canvas.GraphicsContext;

public abstract class Operation {
		
	String name; 
	
	public Operation() {
		
	}

	public abstract void draw(GraphicsContext gc, double xPos, double yPos);
	
	@Override
	public String toString() {
		return name;
	}

}
