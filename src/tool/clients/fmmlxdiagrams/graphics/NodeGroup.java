package tool.clients.fmmlxdiagrams.graphics;

import java.util.Vector;

import org.w3c.dom.Element;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeGroup implements NodeElement {
	
	private Vector<NodeElementData> elements = new Vector<>();
	private Affine selfTransform;
	
	public NodeGroup(Affine selfTransform) {
		this.selfTransform = selfTransform;
	}
	
	public void addElement(NodeElement element, boolean showSelectedOnly, boolean showUnselectedOnly) {
		elements.add(new NodeElementData(element, showSelectedOnly, showUnselectedOnly));
	}
	
	private class NodeElementData  {
		NodeElement element;
		boolean showSelectedOnly;
		boolean showUnselectedOnly;
		
		private NodeElementData(NodeElement element, boolean showSelectedOnly, boolean showUnselectedOnly) {
			super();
			this.element = element;
			this.showSelectedOnly = showSelectedOnly;
			this.showUnselectedOnly = showUnselectedOnly;
		}
	}

	@Override
	public void paintOn(GraphicsContext g, Affine transform, FmmlxDiagram diagram, boolean objectIsSelected) {
		Affine newTransform = transform.clone(); newTransform.append(selfTransform);
		for(NodeElementData ned : elements)  {
			if(!((ned.showUnselectedOnly && objectIsSelected) || (ned.showSelectedOnly && !objectIsSelected)))
			ned.element.paintOn(g, newTransform, diagram, objectIsSelected);
		}

	}

	@Override
	public boolean isHit(double mouseX, double mouseY, GraphicsContext g,  Affine currentTransform) {
		Point2D p = new Point2D(mouseX, mouseY);
		currentTransform = new Affine(currentTransform); // copy
		currentTransform.append(selfTransform);
//		try {
//			p = selfTransform.createInverse().transform(p);
			for (NodeElementData ned : elements) {
				if (ned.element.isHit(mouseX, mouseY, g, currentTransform))
					return true;
			}
//		} catch (NonInvertibleTransformException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		return false;
	}

	@Override
	public NodeBaseElement getHitLabel(Point2D mouse, GraphicsContext g, Affine currentTransform) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element group, double xOffset, double yOffset,
			boolean objectIsSelected) {
		// TODO Auto-generated method stub
	}
	
	@Override public double getX() {return selfTransform.getTx();}
	@Override public double getY() {return selfTransform.getTy();}



}
