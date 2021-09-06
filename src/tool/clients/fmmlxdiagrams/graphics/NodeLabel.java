package tool.clients.fmmlxdiagrams.graphics;

import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Affine;

import org.w3c.dom.Element;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
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
	boolean isIssue;
	int issueNumber;
	
	private boolean special = false;
	private double availableWidth;

	public NodeLabel(Pos alignment, double x, double y, Color fgColor, Color bgColor, FmmlxObject actionObject, Action action, String text, boolean b, int issueNumber) {
		this(alignment, x, y, fgColor, bgColor, actionObject, action, text, FontPosture.REGULAR, FontWeight.NORMAL, 1.);
		this.isIssue = b;
		this.issueNumber = issueNumber;
	}

	@Override
	public void paintOn(GraphicsContext g, Affine currentTransform, FmmlxDiagram diagram, boolean objectIsSelected) {
		double hAlign = 0;
		String textLocal = setTextLocal(text);
		g.setFont(Font.font(FmmlxDiagram.FONT.getFamily(), fontWeight, fontPosture, fontSize * fontScale));

		if (alignment != Pos.BASELINE_LEFT) {
			hAlign = (alignment == Pos.BASELINE_CENTER ? 0.5 : 1) * textWidth;
		}

		Affine boxTransform = new Affine(currentTransform); // copy
		boxTransform.append(new Affine(1, 0, getX() - hAlign - BOX_GAP, 0, 1,  getY() - BOX_GAP - textHeight));
		g.setTransform(boxTransform);
		
//		if("getStudent(): Student".equals(getText())) {
//			g.setFill(Color.web("#ff8800"));g.fillText(boxTransform+"",300, 0);
//		}
		
		if (selected || bgColor != null) {
			g.setFill(selected ? Color.DARKGREY : bgColor);
			g.fillRect(0, 0, textWidth + 2 * BOX_GAP, textHeight + 2 * BOX_GAP);
		}
		
		Affine textTransform = new Affine(currentTransform); // copy
		textTransform.append(new Affine(1, 0, getX() - hAlign, 0, 1, getY() - Y_BASELINE_DIFF));
		g.setTransform(textTransform);		
		
		g.setFill(fgColor);
		g.fillText(textLocal, 0, 0);
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
	public boolean isHit(double mouseX, double mouseY, GraphicsContext g, Affine currentTransform) {
		currentTransform = new Affine(currentTransform); // copy
		currentTransform.append(myTransform);
		if (alignment == Pos.BASELINE_LEFT) {
			return isHitBaseLineLeft(mouseX, mouseY, g, currentTransform);
		} else if (alignment == Pos.BASELINE_CENTER) {
			return isHitBaseLineCenter(mouseX, mouseY, g, currentTransform);
		}
		return false;
	}

	private boolean isHitBaseLineCenter(double mouseX, double mouseY, GraphicsContext g,  Affine currentTransform) {
		boolean hit = false; 
		g.setTransform(currentTransform);
		g.beginPath();
		g.moveTo(- 0.5 * textWidth, 0); 
		g.lineTo(- 0.5 * textWidth, - textHeight); 
		g.lineTo(  0.5 * textWidth, - textHeight); 
		g.lineTo(  0.5 * textWidth, 0); 
		g.lineTo(- 0.5 * textWidth, 0);
		hit = g.isPointInPath(mouseX, mouseY);
		g.closePath();
		return hit;
//
//		Rectangle rec = new Rectangle(getX() - 0.5 * textWidth, getY() - textHeight, textWidth, textHeight);
//		return rec.contains(mouseX, mouseY);
	}

	private boolean isHitBaseLineLeft(double mouseX, double mouseY, GraphicsContext g,  Affine currentTransform) {
		boolean hit = false; 
		g.setTransform(currentTransform);
		g.beginPath();
		g.moveTo(0, 0); 
		g.lineTo(0, - textHeight); 
		g.lineTo(textWidth, - textHeight); 
		g.lineTo(textWidth, 0); 
		g.lineTo(0, 0);
//		if("getStudent(): Student".equals(getText())) {
//			System.err.println(currentTransform+"");
//			System.err.println(mouseX+"/"+mouseY);
//		}
		hit = g.isPointInPath(mouseX, mouseY);
		g.closePath();
		return hit;
//
//		Rectangle rec = new Rectangle(getX(), getY() - textHeight, textWidth, textHeight);
//		return rec.contains(mouseX, mouseY);
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
			rect.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, (getX() - hAlign + xOffset - BOX_GAP)+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, (getY() + yOffset - BOX_GAP - textHeight)+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_HEIGHT, ( textHeight + 2 * BOX_GAP)+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_WIDTH, (textWidth + 2 * BOX_GAP)+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_FILL_OPACITY, bgColor.getOpacity()+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_STYLE, styleString);
			xmlHandler.addXmlElement(group, rect);
		}
		String color = fgColor.toString().split("x")[1].substring(0,6);

		Element text = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_TEXT);
		text.setAttribute(SvgConstant.ATTRIBUTE_FONT_FAMILY, "Arial");

		if(alignment == Pos.BASELINE_CENTER){
			text.setAttribute(SvgConstant.ATTRIBUTE_TEXT_ANCHOR, "middle");
			text.setAttribute(SvgConstant.ATTRIBUTE_TEXT_ALIGN, "center");
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, (getX() + xOffset)+"");
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, (getY() + yOffset - Y_BASELINE_DIFF)+"");
		} else {
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, (getX() - hAlign + xOffset)+"");
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, (getY() + yOffset - Y_BASELINE_DIFF)+"");
		}
		text.setAttribute(SvgConstant.ATTRIBUTE_FONT_SIZE, ((fontSize-1)*fontScale)+"");
		text.setAttribute(SvgConstant.ATTRIBUTE_FONT_OPACITY, fgColor.getOpacity()+"");
		text.setAttribute(SvgConstant.ATTRIBUTE_FONT_STYLE, fgColor.getOpacity()+"");
		text.setAttribute(SvgConstant.ATTRIBUTE_FILL, "#"+color);
		if(isIssue){
			text.setTextContent(" issue ["+issueNumber+"]");
		} else {
			text.setTextContent(textLocal);
		}
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
