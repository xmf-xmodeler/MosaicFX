package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class NodeLabel extends NodeBaseElement implements NodeElement {
	
	private Pos alignment;
	private final FontWeight fontWeight;
	private final FontPosture fontPosture;
	private final int fontSize = 14;
	private Color fgColor = Color.BLACK;
	private Color bgColor = null;
	private String text;
	private double textWidth;
	private double textHeight;
	private final static int Y_BASELINE_DIFF = 3;
	private final static int BOX_GAP = 1;
	
	private boolean special = false;
	private double availableWidth;

	@Override
	public void paintOn(GraphicsContext g, double xOffset, double yOffset, FmmlxDiagram diagram, boolean objectIsSelected) {
		double hAlign = 0;
		String textLocal = text;
		
		if(special) {
			int length = text.length();
			double neededWidth = FmmlxDiagram.calculateTextWidth(text);
			if(neededWidth > availableWidth) {
				textLocal = text + "     " + text;
				int cycle = length + 5;
				final int SPEED = 400;
				int step = (int)((System.currentTimeMillis() % (cycle * SPEED)) / SPEED);
				textLocal = textLocal.substring(step);
				textLocal = textLocal.substring(0,(int)(length * availableWidth / neededWidth + .5));
			}
		}

		if (alignment != Pos.BASELINE_LEFT) {
			hAlign = (alignment == Pos.BASELINE_CENTER ? 0.5 : 1) * textWidth;
		}
		if (selected || bgColor != null) {
			g.setFill(selected ? Color.DARKGREY : bgColor);
			g.fillRect(x - hAlign + xOffset - BOX_GAP, y + yOffset - BOX_GAP - textHeight, textWidth + 2 * BOX_GAP, textHeight + 2 * BOX_GAP);
		}

		g.setFont(Font.font(FmmlxDiagram.FONT.getFamily(), fontWeight, fontPosture, fontSize));
		
		g.setFill(fgColor);
		g.fillText(textLocal, x - hAlign + xOffset, y + yOffset - Y_BASELINE_DIFF);
		// Resetting font to standard in case font was changed
		
	}

	public NodeLabel(Pos alignment, double x, double y, Color fgColor, Color bgColor, FmmlxProperty actionObject, Action action,
			 String text) {
		this(alignment, x, y, fgColor, bgColor, actionObject, action, text, FontPosture.REGULAR, FontWeight.NORMAL);
	}
	
	public NodeLabel(Pos alignment, double x, double y, Color fgColor, Color bgColor, FmmlxProperty actionObject, Action action,
				 String text, FontPosture fontPosture, FontWeight fontWeight) {
		super(x, y, actionObject, action);
		this.alignment = alignment;
		this.fgColor = fgColor;
		this.bgColor = bgColor;
		this.text = text;
		this.fontWeight = fontWeight;
		this.fontPosture = fontPosture;
		this.selected = false;
		
		textWidth = FmmlxDiagram.calculateTextWidth(text);
		textHeight = FmmlxDiagram.calculateTextHeight();
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

	public String getText() {
		return text;
	}

	public void activateSpecialMode(double availableWidth) {
		this.special = true;
		this.availableWidth = availableWidth;
	}

	public double getWidth() {
		return FmmlxDiagram.calculateTextWidth(text);
	}
}
