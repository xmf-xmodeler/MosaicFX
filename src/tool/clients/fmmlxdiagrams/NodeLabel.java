package tool.clients.fmmlxdiagrams;

import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
	private FmmlxProperty actionObject; // Change to interface ~ HasContextMenu
	private String text;
	private double textWidth;
	private double textHeight;
	private final static int Y_BASELINE_DIFF = 3;
	private final static int BOX_GAP = 1;
	private boolean selected;

	@Override
	public void paintOn(GraphicsContext g, double xOffset, double yOffset, FmmlxDiagram diagram, boolean objectIsSelected) {
		double hAlign = 0;
		textWidth = diagram.calculateTextWidth(text);
		textHeight = diagram.calculateTextHeight();

		if (alignment != Pos.BASELINE_LEFT) {
			hAlign = (alignment == Pos.BASELINE_CENTER ? 0.5 : 1) * textWidth;
		}
		if (selected || bgColor != null) {
			g.setFill(selected ? Color.DARKGREY : bgColor);
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
		// Resetting font to standard in case font was changed
		g.setFont(diagram.getFont());
	}

	public NodeLabel(Pos alignment, double x, double y, Color fgColor, Color bgColor, FmmlxProperty actionObject,
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
		this.selected = false;
	}

	public NodeLabel(Pos alignment, double x, double y, Color fgColor, Color bgColor, FmmlxProperty actionObject,
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
		this.selected = false;
	}

	@Override
	public boolean isHit(double mouseX, double mouseY) {
		if (alignment == Pos.BASELINE_LEFT) {
			return isHitBaseLineLeft(mouseX, mouseY);
		} else if (alignment == Pos.BASELINE_CENTER) {
			return isHitBaseLineCenter(mouseX, mouseY);
		}
		return false;
	}

	private boolean isHitBaseLineCenter(double mouseX, double mouseY) {
		Rectangle rec = new Rectangle(x - 0.5 * textWidth, y - textHeight, textWidth, textHeight);
		return rec.contains(mouseX, mouseY);
	}

	private boolean isHitBaseLineLeft(double mouseX, double mouseY) {
		Rectangle rec = new Rectangle(x, y - textHeight, textWidth, textHeight);
		return rec.contains(mouseX, mouseY);
	}

	public void setSelected() {
		selected = true;
	}

	public void setDeselected() {
		selected = false;
	}

	public FmmlxProperty getActionObject() {
		return actionObject;
	}

	public String getText() {
		return text;
	}
}
