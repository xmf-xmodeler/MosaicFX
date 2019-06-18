package tool.clients.fmmlxdiagrams;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

import java.util.Vector;

public class NodeBox implements NodeElement {
	double x;
	double y;
	double width;
	double height;
	Paint bgColor;
	Paint fgColor;
	double lineWidth = 1;
	Vector<NodeElement> nodeElements = new Vector<>();

	public NodeBox(double x, double y, double width, double height, Paint bgColor, Paint fgColor, double lineWidth) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.bgColor = bgColor;
		this.fgColor = fgColor;
		this.lineWidth = lineWidth;
	}

	@Override
	public void paintOn(GraphicsContext g, double xOffset, double yOffset, FmmlxDiagram diagram) {
		g.setFill(bgColor);
		g.fillRect(x + xOffset, y + yOffset, width, height);
		g.setStroke(fgColor);
		g.setLineWidth(lineWidth);
		g.strokeRect(x + xOffset, y + yOffset, width, height);
		for (NodeElement e : nodeElements) {
			e.paintOn(g, x + xOffset, y + yOffset, diagram);
		}
	}
}
