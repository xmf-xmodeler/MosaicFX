package tool.clients.fmmlxdiagrams;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

import java.util.Vector;

public class NodeBox implements NodeElement {

	public interface LineWidthGetter {
		public double getWidth(boolean selected);
	}

	double x;
	double y;
	double width;
	double height;
	Paint bgColor;
	Paint fgColor;
	LineWidthGetter lineWidth;
	Vector<NodeElement> nodeElements = new Vector<>();
	private PropertyType propertyType;

	public NodeBox(double x, double y, double width, double height, Paint bgColor, Paint fgColor, LineWidthGetter lineWidth, PropertyType propertyType) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.bgColor = bgColor;
		this.fgColor = fgColor;
		this.lineWidth = lineWidth;
		this.propertyType = propertyType;
	}

	@Override
	public void paintOn(GraphicsContext g, double xOffset, double yOffset, FmmlxDiagram diagram, boolean objectIsSelected) {
		g.setFill(bgColor);
		g.fillRect(x + xOffset, y + yOffset, width, height);
		g.setStroke(fgColor);
		g.setLineWidth(lineWidth.getWidth(objectIsSelected));
		g.strokeRect(x + xOffset, y + yOffset, width, height);
		for (NodeElement e : nodeElements) {
			e.paintOn(g, x + xOffset, y + yOffset, diagram, objectIsSelected);
		}
	}

	@Override
	public boolean isHit(double mouseX, double mouseY) {
		Rectangle rec = new Rectangle(x, y, width, height);
		return rec.contains(mouseX, mouseY);
	}

	public PropertyType getElementType() {
		return propertyType;
	}
}
