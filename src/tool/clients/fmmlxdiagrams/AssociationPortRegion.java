package tool.clients.fmmlxdiagrams;

public class AssociationPortRegion {
	
	public enum PortType {TOP(0), BOTTOM(1), LEFT(2), RIGHT(3); 
		  int id; 
		  private PortType(int id) {this.id = id;}
		  private int getID() {return id;}
		  private static PortType getPortType(int id) {
			  for(PortType portType : PortType.values()) if(portType.id == id) return portType;
			  throw new IllegalArgumentException("HeadStyle id " + id + " not in use!");
		  }
	}
	
	private double x;
	private double y;
	private double width;
	private double height;
	private PortType portType;
	
	public AssociationPortRegion(double x, double y, double width, double height, int portTypeId) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.portType = PortType.getPortType(portTypeId);
	}
	
	public PortType getPortType() {
		return portType;
	}

	public void setPortType(PortType portType) {
		this.portType = portType;
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
}
