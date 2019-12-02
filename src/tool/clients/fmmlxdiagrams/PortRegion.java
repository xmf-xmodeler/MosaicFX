package tool.clients.fmmlxdiagrams;

public enum PortRegion {
	NORTH,SOUTH,EAST,WEST;

	public boolean isHorizontal() {return this == EAST || this == WEST;}
	
	public boolean isVertical() {return !isHorizontal();}
}
