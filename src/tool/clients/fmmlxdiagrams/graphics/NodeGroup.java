package tool.clients.fmmlxdiagrams.graphics;

import java.util.Vector;

import org.apache.batik.anim.dom.SVGOMGElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeGroup extends NodeElement {
	
	protected Vector<NodeElement> nodeElements = new Vector<>();
	
	/*
	 * This should be on a higher level, as only the whole group can be dragged
	 */
	private transient Affine dragAffine;
	
	public NodeGroup(Affine myTransform) {
		this.myTransform = myTransform;
		dragAffine = new Affine();
		updateBounds();
	}
	
	public NodeGroup() {
		this(new Affine());
	}
	
	public NodeGroup(SVGOMGElement node, SVGOMSVGElement svgOMElement) {
		this.myTransform = SVGReader.readTransform(node);//)transformNode==null?new Affine():TransformReader.getTransform(transformNode.getNodeValue());
		setID(node);
		Vector<NodeElement> children = SVGReader.readChildren(node, svgOMElement);
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
	public void paintOn(View diagramView, boolean objectIsSelected) {
		for (NodeElement e : new Vector<>(nodeElements)) {
			e.paintOn(diagramView, objectIsSelected);
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

		group.setAttribute(SvgConstant.ATTRIBUTE_TRANSFORM, "matrix("+ getMyTransform().getMxx() +","+ getMyTransform().getMxy()+","+getMyTransform().getMyx()+","+getMyTransform().getMyy()+","+getMyTransform().getTx()+","+getMyTransform().getTy()+")");

		for(NodeElement nodeElement : nodeElements){
			nodeElement.paintToSvg(diagram, xmlHandler, group);
		}
		
		xmlHandler.addXmlElement(parentGroup, group);		
	}

	
	
	public Affine getTotalTransform(Affine canvasTransform) {
		Affine a = new Affine(owner == null?canvasTransform:owner.getTotalTransform(canvasTransform));
		a.append(myTransform);
		a.append(getDragAffine());
		return a;
	}

	public void setOwner(NodeElement owner) {
		this.owner = owner;
		updateBounds();
	}

	@Override
	public void updateBounds() {
		Bounds bounds = null;
		for (NodeElement e : new Vector<>(nodeElements)) {
			e.updateBounds();
			
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
		if(bounds == null) 
			updateBounds();
		return bounds;
	}

	@Override
	protected Vector<NodeElement> getChildren() {
		return nodeElements;
	}
	
	@Override
	public String toString() {
		return "G"+ (id==null?"":("("+id+")"));
	}

	public Node save(Document document) {
		Element myElement = document.createElement("Group");
		myElement.setAttribute("xx", myTransform.getMxx()+"");
		myElement.setAttribute("yy", myTransform.getMyy()+"");
		myElement.setAttribute("xy", myTransform.getMxy()+"");
		myElement.setAttribute("yx", myTransform.getMyx()+"");
		myElement.setAttribute("tx", myTransform.getTx()+"");
		myElement.setAttribute("ty", myTransform.getTy()+"");
		ConcreteSyntax.saveChildren(document, nodeElements, myElement);
		return myElement;
	}

	private static Modification findMod(Vector<Modification> modifications, NodeElement nodeElement) {
		for(Modification mod : modifications) {
			if(nodeElement.matchID(mod.getParentID(), mod.getID()))
				return mod;
		}
		return null;
	}
	
	@Override
	protected NodeGroup createInstance(FmmlxObject object, Vector<Modification> modifications) {
		NodeGroup that = new NodeGroup(new Affine(this.myTransform));
		for(NodeElement nodeElement : this.nodeElements) {
			Modification mod = findMod(modifications, nodeElement);
			boolean add = mod == null || mod.getConsequence() == Modification.Consequence.SHOW_ALWAYS
					|| mod.getConsequence() == Modification.Consequence.SHOW_IF && mod.getCondition().eval(object)
					|| mod.getConsequence() == Modification.Consequence.SHOW_IF_NOT && !mod.getCondition().eval(object);
			
			/// Special case for labels( and later also texts):
			if(nodeElement instanceof NodeLabel) {
				if(add) {
					that.addNodeElement(nodeElement.createInstance(object, modifications));
				} else {
					if(mod.getConsequence() == Modification.Consequence.READ_FROM_SLOT) {
						NodeLabel thatLabel = ((NodeLabel)nodeElement).createInstance(object, modifications);
						thatLabel.setText((mod.getCondition()).evalText(object));
						that.addNodeElement(thatLabel);
					}
				}
			} else {
				if(add) {
					that.addNodeElement(nodeElement.createInstance(object, modifications));
				}
			}			
		}
		return that;
	}	
	
}
