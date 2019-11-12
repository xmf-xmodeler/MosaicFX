package tool.clients.fmmlxdiagrams.port;

public class AssociationPortContainer {
	
	private PortContainer upperContainer;
	private PortContainer underContainer;
	private PortContainer rightContainer;
	private PortContainer leftContainer;
	
	public AssociationPortContainer(PortContainer upperContainer, PortContainer underContainer,
			PortContainer rightContainer, PortContainer leftContainer) {
		super();
		this.upperContainer = upperContainer;
		this.underContainer = underContainer;
		this.rightContainer = rightContainer;
		this.leftContainer = leftContainer;
	}
	
	public boolean isHit(double mouseX, double mouseY) {
			if(upperContainer.isHit(mouseX, mouseY)) {
				return true;
			} else if (underContainer.isHit(mouseX, mouseY)) {
				return true;
			} else if (rightContainer.isHit(mouseX, mouseY)) {
				return true;
			} else if (leftContainer.isHit(mouseX, mouseY)) {
				return true;
			} else {
				return false;
			}
	}
	
	public PortContainer getUpperContainer() {
		return upperContainer;
	}

	public void setUpperContainer(PortContainer upperContainer) {
		this.upperContainer = upperContainer;
	}

	public PortContainer getUnderContainer() {
		return underContainer;
	}

	public void setUnderContainer(PortContainer underContainer) {
		this.underContainer = underContainer;
	}

	public PortContainer getRightContainer() {
		return rightContainer;
	}

	public void setRightContainer(PortContainer rightContainer) {
		this.rightContainer = rightContainer;
	}

	public PortContainer getLeftContainer() {
		return leftContainer;
	}

	public void setLeftContainer(PortContainer leftContainer) {
		this.leftContainer = leftContainer;
	}	

}
