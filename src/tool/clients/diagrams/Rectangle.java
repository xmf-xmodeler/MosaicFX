package tool.clients.diagrams;

public class Rectangle {
	public final int x;
    public final int y;
	public final int width;
    public final int heigth;
    
	public Rectangle(int x, int y, int width, int heigth) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.heigth = heigth;
	}

	public boolean contains(int X, int Y) {
		if(X < x) return false;
		if(Y < y) return false;
		if(X > x+width) return false;
		if(Y > y+heigth) return false;
		return true;
	}
    
}
