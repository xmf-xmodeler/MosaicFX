package tool.clients.fmmlxdiagrams.graphics;

import java.util.Vector;

import org.w3c.dom.Element;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import tool.clients.exporter.svg.SvgConstant;
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
	
//	private class NodeElementData  {
//		NodeElement element;
//		boolean showSelectedOnly;
//		boolean showUnselectedOnly;
//		
//		private NodeElementData(NodeElement element, boolean showSelectedOnly, boolean showUnselectedOnly) {
//			super();
//			this.element = element;
//			this.showSelectedOnly = showSelectedOnly;
//			this.showUnselectedOnly = showUnselectedOnly;
//		}
//	}

	@Override
	public void paintOn(GraphicsContext g, FmmlxDiagram.DiagramViewPane diagram, boolean objectIsSelected) {
		for (NodeElement e : new Vector<>(nodeElements)) {
			e.paintOn(g, diagram, objectIsSelected);
		}
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, GraphicsContext g, FmmlxDiagram.DiagramViewPane diagram) {
		for(NodeElement n : new Vector<>(nodeElements)) {
			if(n.isHit(mouseX, mouseY, g, diagram)) return true;
		}
		return false;
	}

	@Override
	public NodeBaseElement getHitLabel(Point2D mouse, GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane diagram) {
		NodeBaseElement hitLabel = null;
		for(NodeElement e : nodeElements) if(hitLabel == null) {
			 hitLabel =  e.getHitLabel(mouse, g, currentTransform, diagram);//new Point2D(relativePoint.getX() - e.getX(), relativePoint.getY() - e.getY()));
		}
		return hitLabel;
	}

	@Override
	public void paintToSvg(FmmlxDiagram diagram, XmlHandler xmlHandler, Element parentGroup) {
		Element group = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_GROUP);

		group.setAttribute(SvgConstant.ATTRIBUTE_TRANSFORM, "matrix(1,0,0,1,"+getMyTransform().getTx()+","+getMyTransform().getTy()+")");

		for(NodeElement nodeElement : nodeElements){
			nodeElement.paintToSvg(diagram, xmlHandler, group);
		}
		
		xmlHandler.addXmlElement(parentGroup, group);
		
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

	public Transform getDragAffine() {
		if(dragAffine == null) return new Affine(); // HACK
		return dragAffine;
	}

}
