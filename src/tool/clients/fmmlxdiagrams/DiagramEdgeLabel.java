package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.w3c.dom.Element;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.xmlManipulator.XmlHandler;
import xos.Value;

public class DiagramEdgeLabel<ConcreteNode extends Node> implements CanvasElement {
	
	final Edge<ConcreteNode> owner;
	public final int localID;
	private final Runnable action;
	private final String text;
	private final ContextMenu menu;
	public double relativeX;
	public double relativeY;
	private double width;
	private double height;
	private Vector<ConcreteNode> anchors;
	private transient double mouseMoveOffsetX;
	private transient double mouseMoveOffsetY;
	private transient boolean highlighted = false;
	private final Color bgColor;
	private Color fontColor;
	private final static int MARGIN = 1;

	public DiagramEdgeLabel(Edge<ConcreteNode> owner, int localID, Runnable action, ContextMenu menu, Vector<ConcreteNode> anchors, String value, 
			double relativeX, double relativeY, double w, double h,
			Color fontColor, Color bgColor) {
		this.owner = owner;
		this.localID = localID;
		this.action = action;
		this.menu = menu;
		this.text = value;
		this.relativeX = relativeX;
		this.relativeY = relativeY;
		this.width = w + 2 * MARGIN;
		this.height = h + 2 * MARGIN;
		this.anchors = anchors;
		this.fontColor = fontColor;
		this.bgColor = bgColor;
	}



	@Override
	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram) {
		if(!owner.isVisible()) return;		
		int size=16;
		g.setFill(bgColor);
		g.fillRect(this.getReferenceX() + relativeX, this.getReferenceY() + relativeY, this.width, this.height);
		
		g.setFill(highlighted ? new Color(1.,0.,0.,1.):fontColor);
		g.setFont(Font.font(FmmlxDiagram.FONT.getFamily(), FontWeight.NORMAL, FontPosture.REGULAR, 14));

		g.fillText(this.text, this.getReferenceX() + relativeX + MARGIN, this.getReferenceY() + relativeY + height - MARGIN-2);
		if(anchors.size()>=2) {
			if(anchors.firstElement().getCenterX() <= anchors.elementAt(1).getCenterX()) {		
				g.fillPolygon(new double[] { this.getReferenceX()+relativeX+width, this.getReferenceX()+relativeX+width + size-5, this.getReferenceX()+relativeX+width },
						      new double[] { this.getReferenceY()+relativeY, this.getReferenceY()+relativeY + size /2, this.getReferenceY()+relativeY+ size}, 3);
			}else {
				g.fillPolygon(new double[] { this.getReferenceX()+relativeX, this.getReferenceX()+relativeX - size+5, this.getReferenceX()+relativeX },
						      new double[] { this.getReferenceY()+relativeY, this.getReferenceY()+relativeY + size /2, this.getReferenceY()+relativeY+ size}, 3);
			}
		if (highlighted) { 
			//This will highlight the edge label when you hover over it and lines from the label to the ports from the associations are drawn. 
			//TODO: Make thoughts about more efficient way
			g.setStroke(Color.TOMATO);
			if(anchors.get(0).getPointForEdge(owner.sourceEnd, true).getX()< anchors.get(1).getPointForEdge(owner.targetEnd,true).getX()) {
				if(anchors.get(0).getPointForEdge(owner.sourceEnd, true).getX() < relativeX+this.getReferenceX() ) {
					g.strokeLine(anchors.get(0).getPointForEdge(owner.sourceEnd, true).getX(), anchors.get(0).getPointForEdge(owner.sourceEnd, true).getY(), this.getReferenceX()+relativeX-2*MARGIN, this.getReferenceY()+relativeY+0.5*height);
				}
				if(anchors.get(0).getPointForEdge(owner.sourceEnd, true).getX() > relativeX+this.getReferenceX() ) {
					g.strokeLine(anchors.get(0).getPointForEdge(owner.sourceEnd, true).getX(), anchors.get(0).getPointForEdge(owner.sourceEnd, true).getY(), this.getReferenceX()+relativeX+18*MARGIN+width, this.getReferenceY()+relativeY+0.5*height);
				}
				if(anchors.get(1).getPointForEdge(owner.targetEnd, true).getX() < relativeX+this.getReferenceX() ) {
					g.strokeLine(anchors.get(1).getPointForEdge(owner.targetEnd, true).getX(), anchors.get(1).getPointForEdge(owner.targetEnd, true).getY(), this.getReferenceX()+relativeX-2*MARGIN, this.getReferenceY()+relativeY+0.5*height);
				}
				if(anchors.get(1).getPointForEdge(owner.targetEnd, true).getX() > relativeX+this.getReferenceX()) {
					g.strokeLine(anchors.get(1).getPointForEdge(owner.targetEnd, true).getX(), anchors.get(1).getPointForEdge(owner.targetEnd, true).getY(), this.getReferenceX()+relativeX+18*MARGIN+width, this.getReferenceY()+relativeY+0.5*height);
				}
			}else if(anchors.get(0) == anchors.get(1)) {
				if(anchors.get(0).getPointForEdge(owner.sourceEnd, true).getX() < relativeX+this.getReferenceX() ) {
					g.strokeLine(anchors.get(0).getPointForEdge(owner.sourceEnd, true).getX(), anchors.get(0).getPointForEdge(owner.sourceEnd, true).getY(), this.getReferenceX()+relativeX-2*MARGIN, this.getReferenceY()+relativeY+0.5*height);
				}
				if(anchors.get(0).getPointForEdge(owner.sourceEnd, true).getX() > relativeX+this.getReferenceX() ) {
					g.strokeLine(anchors.get(0).getPointForEdge(owner.sourceEnd, true).getX(), anchors.get(0).getPointForEdge(owner.sourceEnd, true).getY(), this.getReferenceX()+relativeX+18*MARGIN+width, this.getReferenceY()+relativeY+0.5*height);
				}
				if(anchors.get(1).getPointForEdge(owner.targetEnd, true).getX() > relativeX+this.getReferenceX() ) {
					g.strokeLine(anchors.get(1).getPointForEdge(owner.targetEnd, true).getX(), anchors.get(1).getPointForEdge(owner.targetEnd, true).getY(), this.getReferenceX()+relativeX+18*MARGIN+width, this.getReferenceY()+relativeY+0.5*height);
				}
				if(anchors.get(1).getPointForEdge(owner.targetEnd, true).getX() < relativeX+this.getReferenceX() ) {
					g.strokeLine(anchors.get(1).getPointForEdge(owner.targetEnd, true).getX(), anchors.get(1).getPointForEdge(owner.targetEnd, true).getY(), this.getReferenceX()+relativeX-2*MARGIN, this.getReferenceY()+relativeY+0.5*height);
				}
			}else {
				if(anchors.get(0).getPointForEdge(owner.sourceEnd, true).getX() < relativeX+this.getReferenceX() ) {
					g.strokeLine(anchors.get(0).getPointForEdge(owner.sourceEnd, true).getX(), anchors.get(0).getPointForEdge(owner.sourceEnd, true).getY(), this.getReferenceX()+relativeX-18*MARGIN, this.getReferenceY()+relativeY+0.5*height);
				}
				if(anchors.get(0).getPointForEdge(owner.sourceEnd, true).getX() > relativeX+this.getReferenceX() ) {
					g.strokeLine(anchors.get(0).getPointForEdge(owner.sourceEnd, true).getX(), anchors.get(0).getPointForEdge(owner.sourceEnd, true).getY(), this.getReferenceX()+relativeX+width+2*MARGIN, this.getReferenceY()+relativeY+0.5*height);
				}
				if(anchors.get(1).getPointForEdge(owner.targetEnd, true).getX() > relativeX+this.getReferenceX() ) {
					g.strokeLine(anchors.get(1).getPointForEdge(owner.targetEnd, true).getX(), anchors.get(1).getPointForEdge(owner.targetEnd, true).getY(), this.getReferenceX()+relativeX+2*MARGIN+width, this.getReferenceY()+relativeY+0.5*height);
				}
				if(anchors.get(1).getPointForEdge(owner.targetEnd, true).getX() < relativeX+this.getReferenceX() ) {
					g.strokeLine(anchors.get(1).getPointForEdge(owner.targetEnd, true).getX(), anchors.get(1).getPointForEdge(owner.targetEnd, true).getY(), this.getReferenceX()+relativeX-18*MARGIN, this.getReferenceY()+relativeY+0.5*height);
				}
			}
		}
		}
	}
		
	

	public Edge<ConcreteNode> getOwner() {
		return owner;
	}

	public String getText() {
		return text;
	}

	private double getReferenceX() {
		double x = 0;
		for(ConcreteNode o : anchors) {
			x += o.getCenterX();
		}
		x /= anchors.size();
		return x;
	}

	private double getReferenceY() {
		double y = 0;
		for(ConcreteNode o : anchors) {
			y += o.getCenterY();
		}
		y /= anchors.size();
		return y;
	}

	public double getRelativeX() {
		return relativeX;
	}

	public double getRelativeY() {
		return relativeY;
	}

	public void setRelativeX(double relativeX) {
		this.relativeX = relativeX;
	}

	public void setRelativeY(double relativeY) {
		this.relativeY = relativeY;
	}

	@Override
	public ContextMenu getContextMenu(FmmlxDiagram diagram, Point2D absolutePoint) {
		return menu;
	}

	@Override
	public void moveTo(double x, double y, FmmlxDiagram diagram) {
		this.relativeX = x;// - getReferenceX();
		this.relativeY = y;// - getReferenceY();
	}
	

	@Override
	public void highlightElementAt(Point2D p) {
		this.highlighted=true;
	}

	@Override
	public boolean isHit(double mouseX, double mouseY) {
		return
			owner.isVisible() &&
			mouseX > getReferenceX() + relativeX &&
			mouseY > getReferenceY() + relativeY &&
			mouseX < getReferenceX() + relativeX + width &&
			mouseY < getReferenceY() + relativeY + height;
	}
	
	@Override
	public void setOffsetAndStoreLastValidPosition(Point2D p) {
		mouseMoveOffsetX = p.getX()/* - getReferenceX() */ - relativeX;
		mouseMoveOffsetY = p.getY()/*  - getReferenceY() */ - relativeY;
//		lastValidRelativeX = - getReferenceX() + relativeX;
//		lastValidRelativeY = - getReferenceY() + relativeY;
	}

	@Override public double getMouseMoveOffsetX() {return mouseMoveOffsetX;}
	@Override public double getMouseMoveOffsetY() {return mouseMoveOffsetY;}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch(NumberFormatException | NullPointerException e) {
			return false;
		}
		return true;
	}

	public void performAction() {
		action.run();
	}

	public Value[] getInfo4XMF() {
		return new Value[]{
			new Value(new Value[] {new Value(owner.diagram.getID()), new Value(-1)}),
			new Value(owner.path),
			new Value(localID),
			new Value((float)relativeX),
			new Value((float)relativeY)};
	}

	public Vector<ConcreteNode> getAnchors() {
		return anchors;
	}

	@Override
	public void unHighlight() {
	this.highlighted=false;	
	}

	@Override
	public void paintToSvg(XmlHandler xmlHandler, int xOffset, int yOffset, FmmlxDiagram diagram) {
		if(!owner.isVisible()) return;
		int size=16;
		Element group = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_GROUP);
		group.setAttribute(SvgConstant.ATTRIBUTE_GROUP_TYPE, "edge_label");
		if (isInteger(text)){

			String color = bgColor.toString().split("x")[1].substring(0,6);
			String styleString = "fill: #"+color+";";
			Element rect = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_RECT);
			rect.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, (this.getReferenceX() + relativeX)+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, (this.getReferenceY() + relativeY)+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_HEIGHT, height+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_WIDTH, width+"");
			rect.setAttribute(SvgConstant.ATTRIBUTE_STYLE, styleString);
			xmlHandler.addXmlElement(group, rect);

			Element text = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_TEXT);
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, (this.getReferenceX() + relativeX + MARGIN)+"");
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, (this.getReferenceY() + relativeY + height - MARGIN-2)+"");
			text.setAttribute(SvgConstant.ATTRIBUTE_FONT_FAMILY, "Arial");
			text.setAttribute(SvgConstant.ATTRIBUTE_FONT_SIZE, "13");
			text.setAttribute(SvgConstant.ATTRIBUTE_FILL, "white");
			text.setTextContent(this.text);
			xmlHandler.addXmlElement(group, text);

		} else {
			Element text = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_TEXT);
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, (this.getReferenceX() + relativeX + MARGIN)+"");
			text.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, (this.getReferenceY() + relativeY + height - MARGIN-2)+"");
			text.setAttribute(SvgConstant.ATTRIBUTE_FONT_SIZE, "13");
			text.setAttribute(SvgConstant.ATTRIBUTE_FONT_FAMILY, "Arial");
			text.setAttribute(SvgConstant.ATTRIBUTE_FILL, "black");
			text.setTextContent(this.text);
			xmlHandler.addXmlElement(group, text);
		}
		Element polygon = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_POLYGON);
		StringBuilder points = new StringBuilder();
		if(anchors.size()>=2) {
			if (anchors.firstElement().getCenterX() <= anchors.elementAt(1).getCenterX()) {
				points.append(this.getReferenceX()+relativeX+width).append(",").append(this.getReferenceY()+relativeY).append(" ");
				points.append(this.getReferenceX()+relativeX+width + size-5).append(",").append(this.getReferenceY()+relativeY + size /2).append(" ");
				points.append(this.getReferenceX()+relativeX+width).append(",").append(this.getReferenceY()+relativeY+ size);

			} else {
				points.append(this.getReferenceX()+relativeX).append(",").append(this.getReferenceY()+relativeY).append(" ");
				points.append(this.getReferenceX()+relativeX - size+5).append(",").append(this.getReferenceY()+relativeY + size /2).append(" ");
				points.append(this.getReferenceX()+relativeX).append(",").append(this.getReferenceY()+relativeY+ size);

			}
			polygon.setAttribute(SvgConstant.ATTRIBUTE_POINTS, points.toString());
			xmlHandler.addXmlElement(group, polygon);
		}
		xmlHandler.addXmlElement(xmlHandler.getRoot(), group);
	}

}
