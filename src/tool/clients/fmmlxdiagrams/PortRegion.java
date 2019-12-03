package tool.clients.fmmlxdiagrams;

public enum PortRegion {
	NORTH,SOUTH,EAST,WEST;

	public boolean isHorizontal() {return this == EAST || this == WEST;}
	
	public boolean isVertical() {return !isHorizontal();}

	public boolean isOpposite(PortRegion that) {
		return this == NORTH && that == SOUTH || this == SOUTH && that == NORTH || 
			   this == WEST  && that == EAST  || this == EAST  && that == WEST;
	}
}
