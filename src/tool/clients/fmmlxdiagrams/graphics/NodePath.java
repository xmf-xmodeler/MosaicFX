package tool.clients.fmmlxdiagrams.graphics;

import org.w3c.dom.Element;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.xmlManipulator.XmlHandler;

public class NodePath extends NodeBaseElement{
	
	String textPath;
	Paint bgColor;
	Paint fgColor;
	private transient GraphicsContext g; // evil hack
	private transient Affine lastTransform;
	private Affine selfTransform;
	
	public NodePath(Affine selfTransform, String textPath, Paint bgColor, Paint fgColor, FmmlxProperty actionObject, Action action) {
		super(selfTransform.getTx(), selfTransform.getTy(), actionObject, action);
		this.selfTransform = selfTransform;
		this.bgColor = bgColor;
		this.fgColor = fgColor;
		this.textPath = textPath;
	}

	@Override
	public void paintOn(GraphicsContext g, Affine currentTransform, FmmlxDiagram diagram,
			boolean objectIsSelected) {this.g = g;
//		Affine oldTransform = g.getTransform();
		Affine newtransform = currentTransform.clone();
//		newtransform.appendTranslation(transform.getTx(), transform.getTy());
//		newtransform.prepend(transform);
		newtransform.prepend(selfTransform);
		lastTransform = newtransform;
		g.setTransform(newtransform);
		g.beginPath();
		g.appendSVGPath(textPath);
		g.setFill(bgColor);
		g.fill();
		g.setStroke(fgColor);
		g.stroke();
		g.closePath();
//		g.setTransform(oldTransform);
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, GraphicsContext g,  Affine currentTransform) {
		if (g == null || lastTransform == null) return false;
		Affine transform = g.getTransform();
		Affine newtransform = transform.clone();
		newtransform.append(selfTransform);
		g.setTransform(newtransform);
		g.beginPath();
		g.appendSVGPath(textPath);
		boolean result = g.isPointInPath(mouseX, mouseY);
		g.closePath();
		g.setTransform(transform);
		return result;
	}

	@Override
	public void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element group, double xOffset, double yOffset,
			boolean objectIsSelected) {
		// TODO Auto-generated method stub
		
	}

}
