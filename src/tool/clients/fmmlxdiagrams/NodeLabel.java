package tool.clients.fmmlxdiagrams;

import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class NodeLabel implements NodeElement {
	private Pos alignment;
	private double x;
	private double y;
	private boolean isAbstract;
	private Color fgColor = Color.BLACK;
	private Color bgColor = null;
	private Object actionObject; // Change to interface ~ HasContextMenu
	private String text;

	private final static int Y_BASELINE_DIFF = 3;
	private final static int BOX_GAP = 1;

	@Override
	public void paintOn(GraphicsContext g, double xOffset, double yOffset, FmmlxDiagram diagram) {
		double hAlign = 0;
		double textWidth = diagram.calculateTextWidth(text);
		double textHeight = diagram.calculateTextHeight();

		if (alignment != Pos.BASELINE_LEFT) {
			hAlign = (alignment == Pos.BASELINE_CENTER ? 0.5 : 1) * textWidth;
		}
		if (bgColor != null) {
			g.setFill(bgColor);
			g.fillRect(x - hAlign + xOffset - BOX_GAP, y + yOffset - BOX_GAP - textHeight, textWidth + 2 * BOX_GAP, textHeight + 2 * BOX_GAP);
		}

		// Changing font to oblique(italic)
		if (isAbstract) {
			try {
				g.setFont(Font.loadFont(new FileInputStream("resources/fonts/DejaVuSansMono-Oblique.ttf"), 14));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		g.setFill(fgColor);
		g.fillText(text, x - hAlign + xOffset, y + yOffset - Y_BASELINE_DIFF);
		// Resetting font to standard font in case font was changed
		g.setFont(diagram.getFont());
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
		this.isAbstract = false;
	}

	public NodeLabel(Pos alignment, double x, double y, Color fgColor, Color bgColor, Object actionObject,
					 String text, boolean isAbstract) {
		super();
		this.alignment = alignment;
		this.x = x;
		this.y = y;
		this.fgColor = fgColor;
		this.bgColor = bgColor;
		this.actionObject = actionObject;
		this.text = text;
		this.isAbstract = isAbstract;
	}
}
