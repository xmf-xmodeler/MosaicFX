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
	
	protected Vector<NodeElement> nodeElements = new Vector<>();
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
	
	@Override
	public void paintOn(FmmlxDiagram.DiagramViewPane diagram, boolean objectIsSelected) {
		for (NodeElement e : new Vector<>(nodeElements)) {
			e.paintOn(diagram, objectIsSelected);
		}
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

	public final Affine getMyTransform() {	return myTransform; }
	
	public Affine getTotalTransform(Affine canvasTransform) {
		Affine a = new Affine(owner == null?canvasTransform:owner.getTotalTransform(canvasTransform));
		a.append(myTransform);
		a.append(getDragAffine());
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
