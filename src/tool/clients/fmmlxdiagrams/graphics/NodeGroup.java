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
	
	private Vector<NodeElement> nodeElements = new Vector<>();
	private Affine myTransform;
	private NodeElement owner;
	private transient Affine dragAffine;
	
	public void addNodeElement(NodeElement nodeElement) {
		nodeElements.add(nodeElement);
		nodeElement.setOwner(this);
		dragAffine = new Affine();
	}
	
	public NodeGroup(Affine myTransform) {
		this.myTransform = myTransform;
	}
	
//	public void addElement(NodeElement element, boolean showSelectedOnly, boolean showUnselectedOnly) {
//		elements.add(new NodeElementData(element, showSelectedOnly, showUnselectedOnly));
//		element.setOwner(this);
//	}
	
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
	public void paintOn(GraphicsContext g, Affine currentTransform, FmmlxDiagram diagram, boolean objectIsSelected) {
//		Affine newTransform = transform.clone(); newTransform.append(myTransform);
//		for(NodeElementData ned : elements)  {
//			if(!((ned.showUnselectedOnly && objectIsSelected) || (ned.showSelectedOnly && !objectIsSelected)))
//			ned.element.paintOn(g, newTransform, diagram, objectIsSelected);
//		}
		
//		Affine myTransform = new Affine(1, 0, x, 0, 1, y);
		currentTransform = new Affine(currentTransform); // copy
		currentTransform.append(myTransform);
		currentTransform.append(dragAffine);
		g.setTransform(currentTransform);
		for (NodeElement e : nodeElements) {
			e.paintOn(g, currentTransform, diagram, objectIsSelected);
		}
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, GraphicsContext g, FmmlxDiagram diagram) {
//		Point2D p = new Point2D(mouseX, mouseY);
//		currentTransform = new Affine(currentTransform); // copy
//		currentTransform.append(myTransform);
////		try {
////			p = selfTransform.createInverse().transform(p);
//			for (NodeElementData ned : elements) {
//				if (ned.element.isHit(mouseX, mouseY, g, currentTransform))
//					return true;
//			}
////		} catch (NonInvertibleTransformException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}

//		currentTransform = new Affine(currentTransform); // copy
//		currentTransform.append(myTransform);
		for(NodeElement n : nodeElements) {
			if(n.isHit(mouseX, mouseY, g, diagram)) return true;
		}

		return false;
	}

	@Override
	public NodeBaseElement getHitLabel(Point2D mouse, GraphicsContext g, Affine currentTransform, FmmlxDiagram diagram) {
		NodeBaseElement hitLabel = null;
		for(NodeElement e : nodeElements) if(hitLabel == null) {
			 hitLabel =  e.getHitLabel(mouse, g, currentTransform, diagram);//new Point2D(relativePoint.getX() - e.getX(), relativePoint.getY() - e.getY()));
		}
		return hitLabel;
	}

	@Override
	public void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element group, double xOffset, double yOffset,
			boolean objectIsSelected) {
		// TODO Auto-generated method stub
	}
	
	@Override public double getX() {return myTransform.getTx();}
	@Override public double getY() {return myTransform.getTy();}

	public final Affine getMyTransform() {	return myTransform; }
	
	public Affine getTotalTransform(Affine canvasTransform) {
		Affine a = new Affine(owner == null?canvasTransform:owner.getTotalTransform(canvasTransform));
		a.append(myTransform);
		a.append(dragAffine);
		return a;
	}

	public void setOwner(NodeElement owner) {
		this.owner = owner;
	}

	public void dragTo(Affine dragAffine) {
		this.dragAffine = dragAffine;		
	}

	public void drop() {
		myTransform.append(dragAffine);
		dragAffine = new Affine();
		
	}

}
