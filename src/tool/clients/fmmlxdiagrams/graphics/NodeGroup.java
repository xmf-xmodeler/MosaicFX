package tool.clients.fmmlxdiagrams.graphics;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeGroup extends NodeElement {
	
	protected Vector<NodeElement> nodeElements = new Vector<>();
	private transient Affine dragAffine;
	private Bounds bounds;
	
	public NodeGroup(Affine myTransform) {
		this.myTransform = myTransform;
		dragAffine = new Affine();
		updateBounds();
	}
	
	public NodeGroup() {
		myTransform = new Affine();
	}
	
	public NodeGroup(Node node) {
		myTransform = new Affine();
		Vector<NodeElement> children = SVGReader.readChildren(node);
		this.addAllNodeElements(children);
	}
	
	public final void addNodeElement(NodeElement nodeElement) {
		nodeElements.add(nodeElement);
		nodeElement.setOwner(this);
		updateBounds();
	}
	
	public final void addAllNodeElements(Vector<NodeElement> nodeElementList) {
		nodeElements.addAll(nodeElementList);
		for(NodeElement e : nodeElements) {
			e.setOwner(this);
		}
	}
	
	@Override
	public void paintOn(FmmlxDiagram.DiagramViewPane diagramView, boolean objectIsSelected) {
		for (NodeElement e : new Vector<>(nodeElements)) {
			e.paintOn(diagramView, objectIsSelected);
		}
//		
//		if(bounds == null) updateBounds();
//		if(bounds == null) return;
	}

	@Override
	public boolean isHit(double mouseX, double mouseY, FmmlxDiagram.DiagramViewPane diagram) {
		for(NodeElement n : new Vector<>(nodeElements)) {
			if(n.isHit(mouseX, mouseY, diagram)) return true;
		}
		return false;
	}

	@Override
	public NodeBaseElement getHitLabel(Point2D mouse, GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane diagram) {
		NodeBaseElement hitLabel = null;
		for(NodeElement e : nodeElements) if(hitLabel == null) {
			 hitLabel =  e.getHitLabel(mouse, g, currentTransform, diagram);
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

	
	
	public Affine getTotalTransform(Affine canvasTransform) {
		Affine a = new Affine(owner == null?canvasTransform:owner.getTotalTransform(canvasTransform));
		//System.err.println("a = " + a + " myTransform = " + myTransform);
		a.append(myTransform);
		
		a.append(getDragAffine());
		return a;
	}

	public void setOwner(NodeElement owner) {
		this.owner = owner;
		updateBounds();
	}

	private void updateBounds() {
		Bounds bounds = null;
		for (NodeElement e : new Vector<>(nodeElements)) {
			if(bounds == null) {
				bounds = e.getBounds(); 
			} else {
				Bounds bounds2 = e.getBounds();
				if(bounds2 != null) {
					double minX = Math.min(bounds.getMinX(), bounds2.getMinX());
					double minY = Math.min(bounds.getMinY(), bounds2.getMinY());
					double maxX = Math.max(bounds.getMaxX(), bounds2.getMaxX());
					double maxY = Math.max(bounds.getMaxY(), bounds2.getMaxY());					
					
					bounds = new BoundingBox(minX, minY, maxX - minX, maxY - minY);
				}
			}
		}
//		System.err.println("Bounds updated (NodeGroup): " + bounds);
		this.bounds = bounds;
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

	@Override
	public Bounds getBounds() {
		if(bounds == null) updateBounds();
		return bounds;
	}
	
	

}
