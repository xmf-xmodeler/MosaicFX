package tool.clients.fmmlxdiagrams;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class AssociationPort {
	
	private int id;
	private double x;
	private double y;
	private double width;
	private double height;
	
	public AssociationPort(int id, double x, double y, double width, double height) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void resize(int id, double width, double height) {
		if (getId()==id) {
			this.width = width;
			this.height = height;
		}
	}
	
	public void paintHover(GraphicsContext gc, double x, double y, double xOffset, double yOffset) {
	    Paint c = gc.getStroke();
	    gc.setStroke(Color.RED);
	    gc.strokeRect(x + getX() + xOffset, y + getY() + yOffset, getWidth(), getHeight());
	    gc.setStroke(c);
	}
	
	public boolean contains(double x, double y) {
	    return x >= getX() && y >= getY() && x <= (getX() + getWidth()) && y <= (getY() + getHeight());
	  }
}
