package tool.clients.fmmlxdiagrams;

import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.w3c.dom.Element;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeLabel extends NodeBaseElement implements NodeElement {
	
	private Pos alignment;
	private final FontWeight fontWeight;
	private final FontPosture fontPosture;
	private final int fontSize = 14;
	private final double fontScale;
	private Color fgColor;
	private Color bgColor;
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
		String textLocal = setTextLocal(text);

		if (alignment != Pos.BASELINE_LEFT) {
			hAlign = (alignment == Pos.BASELINE_CENTER ? 0.5 : 1) * textWidth;
		}
		if (selected || bgColor != null) {
			g.setFill(selected ? Color.DARKGREY : bgColor);
			g.fillRect(x - hAlign + xOffset - BOX_GAP, y + yOffset - BOX_GAP - textHeight, textWidth + 2 * BOX_GAP, textHeight + 2 * BOX_GAP);
		}

		g.setFont(Font.font(FmmlxDiagram.FONT.getFamily(), fontWeight, fontPosture, fontSize * fontScale));
		
		g.setFill(fgColor);
		g.fillText(textLocal, x - hAlign + xOffset, y + yOffset - Y_BASELINE_DIFF);
		// Resetting font to standard in case font was changed
		
	}

	public NodeLabel(Pos alignment, double x, double y, Color fgColor, Color bgColor, FmmlxProperty actionObject, Action action,
			 String text) {
		this(alignment, x, y, fgColor, bgColor, actionObject, action, text, FontPosture.REGULAR, FontWeight.NORMAL, 1.);
	}
	
	public NodeLabel(Pos alignment, double x, double y, Color fgColor, Color bgColor, FmmlxProperty actionObject, Action action,
			 String text, FontPosture fontPosture, FontWeight fontWeight) {
		this(alignment, x, y, fgColor, bgColor, actionObject, action, text, fontPosture, fontWeight, 1.);
	}
	
	public NodeLabel(Pos alignment, double x, double y, Color fgColor, Color bgColor, FmmlxProperty actionObject, Action action,
				 String text, FontPosture fontPosture, FontWeight fontWeight, double fontScale) {
		super(x, y, actionObject, action);
		this.alignment = alignment;
		this.fgColor = fgColor;
		this.bgColor = bgColor;
		this.text = text;
		this.fontWeight = fontWeight;
		this.fontPosture = fontPosture;
		this.selected = false;
		this.fontScale = fontScale;
		
		textWidth = FmmlxDiagram.calculateTextWidth(text);
		textHeight = FmmlxDiagram.calculateTextHeight()*fontScale;
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

	@Override
	public void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element group, double xOffset, double yOffset, boolean objectIsSelected) {
		double hAlign = 0;
		String textLocal = setTextLocal(text);
		if (alignment != Pos.BASELINE_LEFT) {
			hAlign = (alignment == Pos.BASELINE_CENTER ? 0.5 : 1) * textWidth;
		}
		if(selected || bgColor!=null){
			Color color = selected ? Color.DARKGREY : bgColor;
			String backgroundColor = color.toString().split("x")[1].substring(0,6);
			String styleString = "fill: #"+backgroundColor+";";
			Element rect = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_RECT);
			rect.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, (x - hAlign + xOffset - BOX_GAP)+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, (y + yOffset - BOX_GAP - textHeight)+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_HEIGHT, ( textHeight + 2 * BOX_GAP)+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_WIDTH, (textWidth + 2 * BOX_GAP)+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_FILL_OPACITY, bgColor.getOpacity()+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_STYLE, styleString);
			xmlHandler.addXmlElement(xmlHandler.getRoot(), rect);
		}
		String color = fgColor.toString().split("x")[1].substring(0,6);

		Element text = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_TEXT);
		text.setAttribute(SvgConstant.ATTRIBUTE_FONT_FAMILY, "Arial");

		if(alignment == Pos.BASELINE_CENTER){
			text.setAttribute(SvgConstant.ATTRIBUTE_TEXT_ANCHOR, "middle");
			text.setAttribute(SvgConstant.ATTRIBUTE_TEXT_ALIGN, "center");
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, (x + xOffset)+"");
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, (y + yOffset - Y_BASELINE_DIFF)+"");
		} else {
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, (x - hAlign + xOffset)+"");
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, (y + yOffset - Y_BASELINE_DIFF)+"");
		}
		text.setAttribute(SvgConstant.ATTRIBUTE_FONT_SIZE, ((fontSize-1)*fontScale)+"");
		text.setAttribute(SvgConstant.ATTRIBUTE_FONT_OPACITY, fgColor.getOpacity()+"");
		text.setAttribute(SvgConstant.ATTRIBUTE_FONT_STYLE, fgColor.getOpacity()+"");
		text.setAttribute(SvgConstant.ATTRIBUTE_FILL, "#"+color);
		text.setTextContent(textLocal);
		xmlHandler.addXmlElement(group, text);
	}

	private String setTextLocal(String text) {
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
		return textLocal;
	}
}
