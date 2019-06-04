package tool.clients.fmmlxdiagrams;

import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class NodeLabel implements NodeElement {
	Pos alignment;
	double x;
	double y;
	Color fgColor = Color.BLACK;
	Color bgColor = null;
	Object actionObject; // Change to interface ~ HasContextMenu
	String text;

	private final static int Y_BASELINE_DIFF = 3;
	private final static int BOX_GAP = 1;


	@Override
	public void paintOn(GraphicsContext g, double xOffset, double yOffset, FmmlxDiagram diagram) {
		double hAlign = 0;
		Text testText = new Text(text);
		double textWidth = testText.getLayoutBounds().getWidth();
		double textHeight = testText.getLayoutBounds().getHeight();

		if (alignment != Pos.BASELINE_LEFT) {
			hAlign = (alignment == Pos.BASELINE_CENTER ? 0.5 : 1) * textWidth;
		}
		if (bgColor != null) {
			g.setFill(bgColor);
			g.fillRect(x - hAlign + xOffset - BOX_GAP, y + yOffset - BOX_GAP - textHeight, textWidth + 2 * BOX_GAP, textHeight + 2 * BOX_GAP);
		}

		g.setFill(fgColor);
		g.fillText(text, x - hAlign + xOffset, y + yOffset - Y_BASELINE_DIFF);

	}

	public NodeLabel(Pos alignment, double x, double y, Color fgColor, Color bgColor, Object actionObject,
					 String text) {
		super();
		this.alignment = alignment;
		this.x = x;
		this.y = y;
		this.fgColor = fgColor;
		this.bgColor = bgColor;
		this.actionObject = actionObject;
		this.text = text;
	}
}
