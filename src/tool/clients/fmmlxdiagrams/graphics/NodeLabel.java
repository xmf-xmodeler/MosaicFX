package tool.clients.fmmlxdiagrams.graphics;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeLabel extends NodeBaseElement {
	
	private Pos alignment;
	private FontWeight fontWeight;
	private final FontPosture fontPosture;
	private final int fontSize = 14;
	private final double fontScale;
	private Color fgColor;
	private Color bgColor;
	private String text;
	private double textWidth;
	private double textHeight;
	private final static int Y_BASELINE_DIFF = 4;
	private final static int BOX_GAP = 1;
	boolean isIssue;
	int issueNumber;
	
	private boolean special = false;
	private double availableWidth;
	
	public void setTextWidth(double textWidth) {
		this.textWidth = textWidth;
	}

	public NodeLabel(Pos alignment, Affine a, Color fgColor, Color bgColor, FmmlxObject actionObject, Action action, String text, boolean isIssue, int issueNumber) {
		this(alignment, a, fgColor, bgColor, actionObject, action, text, FontPosture.REGULAR, FontWeight.NORMAL, 1.);
		this.isIssue = isIssue;
		this.issueNumber = issueNumber;
	}
	
	public NodeLabel(Pos alignment, double x, double y, Color fgColor, Color bgColor, FmmlxObject actionObject, Action action, String text, boolean isIssue, int issueNumber) {
		this(alignment, new Affine(1,0,x,0,1,y), fgColor, bgColor, actionObject, action, text, FontPosture.REGULAR, FontWeight.NORMAL, 1.);
		this.isIssue = isIssue;
		this.issueNumber = issueNumber;
	}
	
	public NodeLabel(Pos alignment, double x, double y, Color fgColor, Color bgColor, FmmlxProperty actionObject, Action action,
			 String text) {
		this(alignment, new Affine(1,0,x,0,1,y), fgColor, bgColor, actionObject, action, text, FontPosture.REGULAR, FontWeight.NORMAL, 1.);
	}
	
	public NodeLabel(Pos alignment, double x, double y, Color fgColor, Color bgColor, FmmlxProperty actionObject, Action action,
			 String text, FontPosture fontPosture, FontWeight fontWeight) {
		this(alignment, new Affine(1,0,x,0,1,y), fgColor, bgColor, actionObject, action, text, fontPosture, fontWeight, 1.);
	}
	
	public NodeLabel(Pos alignment, Affine a, Color fgColor, Color bgColor, FmmlxProperty actionObject, Action action,
				 String text, FontPosture fontPosture, FontWeight fontWeight, double fontScale) {
		super(a, null, actionObject, action);
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
	
	/**
	 * Returns default NodeLabel. For default variable values see method body. The object then can be adjusted by the getter and setter of the class.
	 */
	public NodeLabel(String text) {
		super();
		this.fontWeight = FontWeight.NORMAL;
		this.fontPosture = FontPosture.REGULAR;
		this.fontScale = 1;
		setBgColor(Color.TRANSPARENT);
		setFgColor(Color.BLACK);
		this.text = text;
	}
	
	private Affine getBoxTransform(Affine canvasTransform) {
		double hAlign = 0;
		if (alignment != Pos.BASELINE_LEFT) {
			hAlign = (alignment == Pos.BASELINE_CENTER ? 0.5 : 1) * textWidth;
		}
		Affine total = getTotalTransform(canvasTransform);
		total.append(new Translate( - hAlign - BOX_GAP,  - BOX_GAP - textHeight));
		total.append(getDragAffine());
		return total;
	}
	
	protected Affine getTextTransform(Affine canvasTransform) {
		double hAlign = 0;
		if (alignment != Pos.BASELINE_LEFT) {
			hAlign = (alignment == Pos.BASELINE_CENTER ? 0.5 : 1) * textWidth;
		}
		Affine total = getTotalTransform(canvasTransform);
		total.append(new Translate( - hAlign, - Y_BASELINE_DIFF));
		total.append(getDragAffine());
		return total;
	}

	@Override
	public void paintOn(View diagramView, boolean objectIsSelected) {
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();
		String textLocal = setTextLocal(text);
		g.setFont(Font.font(FmmlxDiagram.FONT.getFamily(), fontWeight, fontPosture, fontSize * fontScale));

		g.setTransform(getBoxTransform(diagramView.getCanvasTransform()));
		
		if (selected || bgColor != null) {
			g.setFill(selected ? Color.DARKGREY : bgColor);
			g.fillRect(0, 0, textWidth + 2 * BOX_GAP, textHeight + 2 * BOX_GAP);
		}
		
		g.setTransform(getTextTransform(diagramView.getCanvasTransform()));		
		
		g.setFill(fgColor);
		g.fillText(textLocal, 0, 0);
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, FmmlxDiagram.DiagramViewPane diagramView) {
		boolean hit = false;
		GraphicsContext g = diagramView.getCanvas().getGraphicsContext2D();
		g.setTransform(getBoxTransform(diagramView.getCanvasTransform()));
		g.beginPath();
		g.moveTo(0, 0); 
		g.lineTo(0, textHeight + 2 * BOX_GAP); 
		g.lineTo(textWidth + 2 * BOX_GAP, textHeight + 2 * BOX_GAP); 
		g.lineTo(textWidth + 2 * BOX_GAP, 0); 
		g.lineTo(0, 0);
		hit = g.isPointInPath(mouseX, mouseY);
		g.closePath();
		return hit;
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
	public void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element parentGroup) {
		Element group = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_GROUP);
		double x = myTransform.getTx() - BOX_GAP;// + (alignment == Pos.BASELINE_CENTER ? 0.5 : 1) * textWidth;
		double y = myTransform.getTy() - BOX_GAP;//- Y_BASELINE_DIFF + BOX_GAP;
		group.setAttribute(SvgConstant.ATTRIBUTE_TRANSFORM, "matrix(1,0,0,1,"+x+","+y+")");
		group.setAttribute("XModeler", "NodeLabel");
		xmlHandler.addXmlElement(parentGroup, group);
		
		double hAlign = 0;
		String textLocal = setTextLocal(text);
		if (alignment != Pos.BASELINE_LEFT) {
			hAlign = (alignment == Pos.BASELINE_CENTER ? 0.5 : 1) * textWidth;
		}
//		if(selected || bgColor!=null){
		if(bgColor!=null && !bgColor.equals(Color.TRANSPARENT)) {
//			Color color = selected ? Color.DARKGREY : bgColor;
			String styleString = "fill:"+NodeBaseElement.toRGBHexString(bgColor)+";";
//			styleString += "italic;"
			Element rect = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_RECT);
			rect.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, ( - hAlign - BOX_GAP)+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, ( - BOX_GAP - textHeight)+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_HEIGHT, ( textHeight + 2 * BOX_GAP)+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_WIDTH, (textWidth + 2 * BOX_GAP)+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_FILL_OPACITY, bgColor.getOpacity()<.5?"0":"1");
			rect.setAttribute(SvgConstant.ATTRIBUTE_STYLE, styleString);
			rect.setAttribute(SvgConstant.ATTRIBUTE_FILL, NodeBaseElement.toRGBHexString(bgColor));
			xmlHandler.addXmlElement(group, rect);
		}
		String color = fgColor.toString().split("x")[1].substring(0,6);

		Element text = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_TEXT);
		text.setAttribute(SvgConstant.ATTRIBUTE_FONT_FAMILY, "Arial");

		if(alignment == Pos.BASELINE_CENTER){
			text.setAttribute(SvgConstant.ATTRIBUTE_TEXT_ANCHOR, "middle");
			text.setAttribute(SvgConstant.ATTRIBUTE_TEXT_ALIGN, "center");
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, "0");
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, (- Y_BASELINE_DIFF)+"");
		} else {
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, (- hAlign)+"");
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, (- Y_BASELINE_DIFF)+"");
		}
		text.setAttribute(SvgConstant.ATTRIBUTE_FONT_SIZE, ((fontSize-1)*fontScale)+"");
		text.setAttribute(SvgConstant.ATTRIBUTE_FONT_OPACITY, fgColor.getOpacity()+"");
		text.setAttribute(SvgConstant.ATTRIBUTE_FONT_STYLE, fontPosture == FontPosture.ITALIC?"italic":"normal");
		text.setAttribute("font-weight", fontWeight == FontWeight.BOLD?"bold":"normal");
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

	@Override
	public Bounds getBounds() {
		return null;
	}

	@Override
	public void updateBounds() {}

	public Node save(Document document) {
		Element myElement = document.createElement("Label");
		myElement.setAttribute("align", alignment==Pos.BASELINE_CENTER?"CENTER":alignment==Pos.BASELINE_RIGHT?"RIGHT":"LEFT");
		saveTransformation(myElement);
		myElement.setAttribute("id", id);
		myElement.setAttribute("color", NodeElement.color2Web(fgColor));
		myElement.setAttribute("bgColor", NodeElement.color2Web(bgColor));
		return myElement;
	}

	@Override
	protected NodeLabel createInstance(FmmlxObject object, Vector<Modification> modifications, Vector<ActionInfo> actions, FmmlxDiagram diagram) {
		NodeLabel that = new NodeLabel(alignment, myTransform, fgColor, bgColor, actionObject, action, text, fontPosture, fontWeight, fontScale);
		return that;
	}
	
	void setText(String newText) {
		this.text = newText;
		textWidth = FmmlxDiagram.calculateTextWidth(text);
	}

	public Pos getAlignment() {
		return alignment;
	}

	public void setAlignment(Pos alignment) {
		this.alignment = alignment;
	}

	public Color getFgColor() {
		return fgColor;
	}

	public void setFgColor(Color fgColor) {
		this.fgColor = fgColor;
	}

	public Color getBgColor() {
		return bgColor;
	}

	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	public void setFontWeight(FontWeight fontWeight) {
		this.fontWeight = fontWeight;
	}

	public double getTextHeight() {
		return textHeight;
	}
}