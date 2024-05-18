package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
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
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.xmlManipulator.XmlHandler;

public class NodeGroup extends NodeElement {
	
	protected Vector<NodeElement> nodeElements = new Vector<>();
		
	public NodeGroup(Affine myTransform) {
		super();
		this.myTransform = myTransform;
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
	
	public final void addNodeElementAtFirst(NodeElement nodeElement) {
		nodeElements.add(0, nodeElement);
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
	public NodeElement getHitElement(Point2D mouse, GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane diagram) {
		NodeElement hitLabel = null;
		for(NodeElement e : nodeElements) if(hitLabel == null) {
			 hitLabel =  e.getHitElement(mouse, g, currentTransform, diagram);
		}
		if(hitLabel==null && isHit(mouse.getX(), mouse.getY(), diagram)) return this;
		return hitLabel;
	}
	
	@Override
	public Action getAction(Point2D mouse, GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane diagram) {
		Action action = null;
		for(NodeElement e : nodeElements) if(action == null) {
			action =  e.getAction(mouse, g, currentTransform, diagram);
		}
		if(action==null && isHit(mouse.getX(), mouse.getY(), diagram)) return this.action;
		return action;
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

	public void setOwner(NodeGroup owner) {
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

	@Override
	public Bounds getBounds() {
		if(bounds == null) 
			updateBounds();
		return bounds;
	}

	@Override
	public Vector<NodeElement> getChildren() {
		return nodeElements;
	}
	
	@Override
	public String toString() {
		return "G"+ (id==null?"":("("+id+")"));
	}

	public Node save(Document document, File baseFile) {
		Element myElement = document.createElement("Group");
		saveTransformation(myElement);
		ConcreteSyntax.saveChildren(document, nodeElements, new Vector<Modification>(), myElement, baseFile);
		return myElement;
	}

	private static Modification findMod(Vector<Modification> modifications, NodeElement nodeElement) {
		for(Modification mod : modifications) {
			if(nodeElement.matchID(mod.getParentID(), mod.getID()))
				return mod;
		}
		return null;
	}
	
	private static Action findAction(Vector<ActionInfo> actions, NodeElement nodeElement, FmmlxObject object, FmmlxDiagram diagram)  {
		for(ActionInfo a : actions) {
			if(nodeElement.matchID(a.id, a.localId))
				return a.getAction(object, diagram);
		}
		return null;
	}
	
	@Override
	protected NodeGroup createInstance(FmmlxObject object, Vector<Modification> modifications, Vector<ActionInfo> actions, FmmlxDiagram diagram) {
		NodeGroup that = new NodeGroup(new Affine(this.myTransform));
		for(NodeElement nodeElement : this.nodeElements) {
			Modification mod = findMod(modifications, nodeElement);
			Action action = findAction(actions, nodeElement, object, diagram);
			nodeElement.setAction(action);
			boolean add = mod == null || mod.getConsequence() == Modification.Consequence.SHOW_ALWAYS
					|| mod.getConsequence() == Modification.Consequence.SHOW_IF && mod.getCondition().evalBool(object)
					|| mod.getConsequence() == Modification.Consequence.SHOW_IF_NOT && !mod.getCondition().evalBool(object);
			
			/// Special case for importing meta
			if(nodeElement instanceof ConcreteSyntax && ((ConcreteSyntax)nodeElement).isMetaImport()) {
				Vector<Modification> allMods = new Vector<>(); 
				allMods.addAll(modifications);
				allMods.addAll(((ConcreteSyntax)nodeElement).getModifications());
				NodeElement nl = nodeElement.createInstance(object.getOf(), allMods, actions, diagram);
				nl.setAction(action);
				that.addNodeElement(nl);
			} else
				
			/// Special case for changing colours
			if(nodeElement instanceof NodePath) {
				if(add) {
					NodeElement nl = nodeElement.createInstance(object, modifications, actions, diagram);
					nl.setAction(action);
					that.addNodeElement(nl);
				} else {
					if(mod.getConsequence() == Modification.Consequence.SET_COLOR) {
						NodePath thatPath = ((NodePath)nodeElement).createInstance(object, modifications, actions, diagram);
						thatPath.setColor((mod.getCondition()).evalString(object));
						thatPath.setAction(action);
						that.addNodeElement(thatPath);
					}
				}
			} else
			
			/// Special case for labels
			if(nodeElement instanceof NodeLabel) {
				if(add) {
					NodeElement nl = nodeElement.createInstance(object, modifications, actions, diagram);
					nl.setAction(action);
					that.addNodeElement(nl);
				} else {
					if(mod.getConsequence() == Modification.Consequence.READ_FROM_SLOT) {
						NodeLabel thatLabel = ((NodeLabel)nodeElement).createInstance(object, modifications, actions, diagram);
						thatLabel.setText((mod.getCondition()).evalString(object));
						thatLabel.setAction(action);
						that.addNodeElement(thatLabel);
					}
				}
			} else {
				if(add) {
					NodeElement ne = nodeElement.createInstance(object, modifications, actions, diagram);
					ne.setAction(action);
					that.addNodeElement(ne);
				}
			}	
		}
		return that;
	}	
	
	public NodeGroup getRoot() {
		NodeGroup owner = getOwner();
		return owner == null?this:owner.getRoot();
	}	

	public void setAction(Action action) {
		super.setAction(action);
	}

	public Vector<NodeElement> getNodeElements() {
		return nodeElements;
	}
}