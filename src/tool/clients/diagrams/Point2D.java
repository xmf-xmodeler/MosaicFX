package tool.clients.diagrams;

public class Point2D {
	private final double x;
	private final double y;

	public static final Point2D ZERO = new Point2D(0, 0);

	public static Point2D createRectangular(double x, double y) {
		return new Point2D(x, y);
	}

	public static Point2D createPolar(double magnitude, double angle) {
		return new Point2D(magnitude * Math.cos(angle), magnitude * Math.sin(angle));
	}

	private Point2D(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getMagnitude() {
		return Math.sqrt(x * x + y * y);
	}

	public double getAngle() {
		return Math.atan2(y, x);
	}
	

	public Point2D negate() {
		return new Point2D(-x, -y);
	}

	public Point2D add(Point2D r) {
		return new Point2D(x + r.x, y + r.y);
	}

	
	public String toString() {
		return "Rect(" + x + "," + y + ")";
	}
}
