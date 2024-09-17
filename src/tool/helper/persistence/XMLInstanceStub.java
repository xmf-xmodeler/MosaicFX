package tool.helper.persistence;

public class XMLInstanceStub {

	private String ref;
	private boolean hidden;
	private int xCoordinate;
	private int yCoordinate;

	public XMLInstanceStub(String ref, boolean hidden, int xCoordinate, int yCoordinate) {
		super();
		this.hidden = hidden;
		this.ref = ref;
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
	}

	public boolean isHidden() {
		return hidden;
	}

	public String getRef() {
		return ref;
	}

	public int getxCoordinate() {
		return xCoordinate;
	}

	public int getyCoordinate() {
		return yCoordinate;
	}
}
