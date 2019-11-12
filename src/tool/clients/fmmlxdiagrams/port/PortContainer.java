package tool.clients.fmmlxdiagrams.port;

import com.sun.javafx.geom.Point2D;

public class PortContainer {
	
	private transient Point2D coordinat;
	private transient double width;
	private transient double height;
	private ContainerType type;	
	
	public PortContainer(Point2D coordinat, double width, double height, ContainerType type) {
		super();
		this.coordinat=coordinat;
		this.width = width;
		this.height = height;
		this.type = type;
	}

	public ContainerType getType() {
		return type;
	}

	public void setType(ContainerType type) {
		this.type = type;
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

	public void setHigh(double height) {
		this.height = height;
	}

	public Point2D getCoordinat() {
		return coordinat;
	}

	public void setCoordinat(Point2D coordinat) {
		this.coordinat = coordinat;
	}

	public boolean isHit(double mouseX, double mouseY) {
		return
				mouseX > coordinat.x &&
				mouseY > coordinat.y &&
				mouseX < coordinat.x + width &&
				mouseY < coordinat.y + height;
	}
	
}
