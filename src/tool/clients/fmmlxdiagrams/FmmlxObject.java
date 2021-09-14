package tool.clients.fmmlxdiagrams;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer.PathNotFoundException;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.graphics.NodeBaseElement;
import tool.clients.fmmlxdiagrams.menus.ObjectContextMenu;
import tool.clients.fmmlxdiagrams.newpalette.PaletteItem;
import tool.clients.fmmlxdiagrams.newpalette.PaletteTool;
import tool.clients.fmmlxdiagrams.newpalette.ToolClass;

import java.util.*;

public class FmmlxObject extends Node implements CanvasElement, FmmlxProperty, Comparable<FmmlxObject> {

	final String name;
	final String ownPath;
	final String ofPath;
	private final Vector<String> parentsPaths;
	
	private final boolean isAbstract;
	final int level;
    
	Vector<FmmlxSlot> slots = new Vector<>();
	private Vector<FmmlxOperationValue> operationValues = new Vector<>();

	private Vector<FmmlxAttribute> ownAttributes = new Vector<>();
	private Vector<FmmlxAttribute> otherAttributes = new Vector<>();

	private Vector<FmmlxOperation> ownOperations = new Vector<>();
	private Vector<FmmlxOperation> otherOperations = new Vector<>();
	
	private Vector<Constraint> constraints = new Vector<>();

	private AbstractPackageViewer diagram;

	 boolean showOperations = true;
	 boolean showOperationValues = true;
	 boolean showSlots = true;
	 boolean showGettersAndSetters = true;
	 boolean showDerivedOperations = true;
	 boolean showDerivedAttributes = true;
	 boolean showConstraints = true;

	public FmmlxObject(
			String name, 
			int level, 
			String ownPath,
			String ofPath,
			Vector<String> parentPaths,
			Boolean isAbstract,
			Integer lastKnownX, Integer lastKnownY, Boolean hidden,
			AbstractPackageViewer diagram) {
		super();
		this.name = name;
		this.diagram = diagram;
		this.x = lastKnownX;
		this.y = lastKnownY;
		this.hidden = hidden;

		width = 150;
		height = 80;
		this.level = level;
		this.isAbstract = isAbstract;

		this.ownPath = ownPath;
		this.ofPath = ofPath;
		this.parentsPaths = parentPaths;
		
		if(diagram instanceof FmmlxDiagram) {
			FmmlxDiagram D = (FmmlxDiagram) diagram;
			this.showOperations = D.isShowOperations();
			this.showOperationValues = D.isShowOperationValues();
			this.showSlots = D.isShowSlots();
			this.showDerivedOperations = D.isShowDerivedOperations();
			this.showDerivedAttributes = D.isShowDerivedAttributes();
			this.showGettersAndSetters = D.isShowGetterAndSetter();
		}
	}

	/// Getters
	
	public ObservableList<FmmlxAttribute> getAllAttributesAsList() { 
		return FXCollections.observableArrayList(getAllAttributes());}
	public Vector<FmmlxOperation> getOwnOperations() { return ownOperations; }
	public Vector<FmmlxOperation> getOtherOperations() { return otherOperations; }
	public Vector<FmmlxAttribute> getOwnAttributes() { return new Vector<>(ownAttributes); }
	public Vector<FmmlxAttribute> getOtherAttributes() { return new Vector<>(otherAttributes); }	
	public String getName() { return name; }
	public int getLevel() { return level; }
	public String getOfPath() { return ofPath; }
	public Vector<FmmlxAssociation> getAllRelatedAssociations() { return diagram.getRelatedAssociationByObject(this); }
	@Override public PropertyType getPropertyType() { return PropertyType.Class; }

	public Vector<FmmlxAttribute> getAllAttributes() {
		Vector<FmmlxAttribute> result = new Vector<>();
		result.addAll(ownAttributes);
		result.addAll(otherAttributes);
		return result;
	}


	public Vector<FmmlxAssociation> findAssociationsForLinks(){
		Vector<FmmlxAssociation> associationForLinks = new Vector<>();
		for (FmmlxAssociation asso : diagram.getAssociations()) {
			if (this.level == asso.getLevelSource() && this.isInstanceOf(asso.sourceNode, this.level) || 
				this.level == asso.getLevelTarget() && this.isInstanceOf(asso.targetNode, this.level)) {
				associationForLinks.add(asso);
			}
		}
		return  associationForLinks;
	}
	
	public Vector<FmmlxOperation> getDelegatedOperations() {		
		Vector<FmmlxOperation> delegatedOperations = new Vector<>();
		FmmlxObject delegatesTo = getDelegatesTo(false);
		if(delegatesTo != null) {
			delegatedOperations.addAll(delegatesTo.getAllOperations());
		}

		for(FmmlxObject ancestor : getAllAncestors()) {
			Vector<FmmlxOperation> opsFromAncestors = ancestor.getDelegatedOperations();
			for(FmmlxOperation o : opsFromAncestors) {

				if(o.getLevel() < level &&! delegatedOperations.contains(o)) delegatedOperations.add(o);
			}
		}
		return delegatedOperations;
	}
	
	public Vector<FmmlxOperation> getDelegateToClassOperations() {
		Vector<FmmlxOperation> delelegateToClassOperations = new Vector<>();
		try {
			FmmlxObject of = diagram.getObjectByPath(ofPath);
			Vector<FmmlxOperation> ofOps = new Vector<>(of.ownOperations);
			ofOps.addAll(of.otherOperations);
			ofOps.addAll(of.getDelegatedOperations());
			
			for(FmmlxOperation o : ofOps) {
				if(o.isDelegateToClassAllowed() && o.getLevel() == this.level) {
					delelegateToClassOperations.add(o);
				}
			}
		} catch (PathNotFoundException e) {} // of not found
		return delelegateToClassOperations;
	}
	
	public FmmlxObject getDelegatesTo(boolean includeAncestors) {
		DelegationEdge de = getDelegatesToEdge(includeAncestors);
		if(de != null) return de.targetNode;
		return null;
	}	

	public DelegationEdge getDelegatesToEdge(boolean includeAncestors) {
		for(Edge<?> e : diagram.getEdges()) {
			if(e instanceof DelegationEdge) {
				DelegationEdge de = (DelegationEdge) e;
				if(de.sourceNode == this || (includeAncestors && this.getAllAncestors().contains(de.sourceNode))) {
					return de;
				}
			}
		}
		return null;
	}
	
	public FmmlxObject getRoleFiller() {
		for(Edge<?> e : diagram.getEdges()) {
			if(e instanceof RoleFillerEdge) {
				RoleFillerEdge rfe = (RoleFillerEdge) e;
				if(rfe.sourceNode == this || this.getAllAncestors().contains(rfe.sourceNode)) {
					return rfe.targetNode;
				}
			}
		}
		return null;
	}

	public Vector<FmmlxOperation> getAllOperations() {
		Vector<FmmlxOperation> result = new Vector<>();
		result.addAll(ownOperations);
		result.addAll(otherOperations);
		result.addAll(getDelegatedOperations());
		result.addAll(getDelegateToClassOperations());
		return result;
	}

	public Vector<FmmlxOperationValue> getOperationValues() {
		Vector<FmmlxOperationValue> result = new Vector<>();
		result.addAll(operationValues);
		return result;
	}
	
	public Vector<String> getParentsPaths() {
		return parentsPaths;
	}
	
	public Vector<FmmlxObject> getInstances() {
		Vector<FmmlxObject> result = new Vector<>();
		for (FmmlxObject object : diagram.getObjects()) {
			if (object.getOfPath().equals(this.ownPath)) {
				result.add(object);
			}
		}
		return result;
	}

	public Vector<FmmlxObject> getInstancesByLevel(Integer level) {
		Vector<FmmlxObject> result = new Vector<>();
		if (this.getInstances().size() != 0) {

			if (this.getInstances().get(0).getLevel() == level) {
				result.addAll(this.getInstances());
			} else {
				for (FmmlxObject tmp : this.getInstances()) {
					result.addAll(tmp.getInstancesByLevel(level));
				}
			}
		}
		return result;
	}
	
	public boolean isAbstract() {return isAbstract;}

	private Vector<String> getSlotNames() {
		Vector<String> slotNames = new Vector<>();
		for (FmmlxObject ancestor : getAllAncestors()) {
			for (FmmlxAttribute attribute : ancestor.getAllAttributes()) {
				if (attribute.level == this.level && !slotNames.contains(attribute.name)) {
					slotNames.add(attribute.name);
				}
			}
		}
		return slotNames;
	}

	private Vector<String> getMonitoredOperationsNames() {
		Vector<String> monitorNames = new Vector<>();
		for (FmmlxObject ancestor : getAllAncestors()) {
			Vector<FmmlxOperation> ops = new Vector<>();
			ops.addAll(ancestor.getAllOperations());
			ops.addAll(ancestor.getDelegatedOperations());
			for (FmmlxOperation operation : ops) {
				if (operation.getLevel() == this.level && operation.isMonitored() && !monitorNames.contains(operation.getName())) {
					monitorNames.add(operation.getName());
				}
			}
		}
		return monitorNames;
	}

	public Vector<FmmlxObject> getAllAncestors() {
		if("Root::XCore::Class".equals(ownPath)) return new Vector<FmmlxObject>();
		Vector<FmmlxObject> result1 = new Vector<>();
		if (ofPath != null) {
			try{FmmlxObject of = diagram.getObjectByPath(getOfPath());result1.add(of);}
			catch (PathNotFoundException e) {} // if(of==null){}
		}
		for (String p : getParentsPaths()) {
			try
			  {FmmlxObject parent = diagram.getObjectByPath(p); result1.add(parent); }
			catch(PathNotFoundException e) {} // if(parent==null){}
		}
		Vector<FmmlxObject> result2 = new Vector<>(result1);
		for (FmmlxObject o : result1) if (o.level != -1) {
			result2.addAll(o.getAllAncestors());
		}
		return result2;
	}


	public boolean isInstanceOf(FmmlxObject theClass, Integer myLevel) {
		if (myLevel != level) return false;
		return this.getAllAncestors().contains(theClass);
	}


	public String getAvailableInstanceName() {
		String s = name;
		if (level == 1) {
			s = s.substring(0, 1).toLowerCase() + s.substring(1);
		}
		int i = 1;
		String t;
		boolean ok;
		do {
			t = s + i;
			ok = diagram.isNameAvailable(t);
			i++;
		} while (!ok);
		return t;
	}	

	public FmmlxSlot getSlot(String slotName) {
		for(FmmlxSlot slot : slots) {
			if(slot.getName().equals(slotName)) return slot;
		}
		return null;
	}

	public Vector<FmmlxObject> getAllChildren() {
		System.err.println("FmmlxObject::getAllChildren() not yet implemented.");
		return new Vector<>();
	}
	
    public int getAttributeCountByLevel(int level) {
		int count = 0;
		for(FmmlxAttribute attribute : getAllAttributes()){
			if(attribute.getLevel()==level){
				count++;
			}
		}
		return count;
    }

	public boolean attributeExists(String name) {
		for(FmmlxAttribute attribute : getAllAttributes()){
			if(attribute.getName().equals(name)){
				return true;
			}
		}
		return false;
	}

	public boolean operationExists(String name) {
		for(FmmlxOperation operation : getAllOperations()){
			if(operation.getName().equals(name)){
				return true;
			}
		}
		return false;
	}

	public String getPath() { return ownPath; }
	@Override public String toString() { return name; }
	
	public String getMetaClassName() {
		try {
			FmmlxObject of = diagram.getObjectByPath(ofPath); return of.name;
		} catch (PathNotFoundException e) {
			if ("Root::FMML::MetaClass".equals(ofPath)) return "MetaClass";
			return ofPath;
		}
	}
	
	public FmmlxAttribute getAttributeByName(String name){
		for (FmmlxAttribute att : getAllAttributes()){
			if (att.getName().equals(name)){
				return att;
			}
		}
		return null;
	}

	public FmmlxOperation getOperationByName(String newValue) {		
		for (FmmlxOperation op : getAllOperations()) {
			if(op.getName().equals(newValue)) {
				return op;
			}
		}
		return null;
	}
	
	public Vector<FmmlxSlot> getAllSlots(){
		return new Vector<FmmlxSlot> (slots);
	}

	public Vector<FmmlxOperationValue> getAllOperationValues(){
		return new Vector<FmmlxOperationValue> (operationValues);
	}
	
	public Vector<Constraint> getConstraints() {
		return new Vector<>(constraints);
	}

/// Graphics

	public void setShowOperations(boolean show) {
		requiresReLayout |= showOperations!=show;
		showOperations = show;
	}

	public void setShowOperationValues(boolean show) {
		requiresReLayout |= showOperationValues!=show;
		showOperationValues = show;
	}

	public void setShowSlots(boolean show) {
		requiresReLayout |= showSlots!=show;
		showSlots = show;
	}
	
	public void setShowGettersAndSetters(boolean show) {
		requiresReLayout |= showGettersAndSetters!=show;
		showGettersAndSetters = show;
	}
	
	public void setShowDerivedOperations(boolean show) {
		requiresReLayout |= showDerivedOperations!=show;
		showDerivedOperations = show;
	}
	
	public void setShowDerivedAttributes(boolean show) {
		requiresReLayout |= showDerivedAttributes!=show;
		showDerivedAttributes = show;
	}	

	
	/// Setters

	public void setAttributes(Vector<FmmlxAttribute> ownAttributes, Vector<FmmlxAttribute> otherAttributes) {
		this.ownAttributes = ownAttributes;
		ownAttributes.sort(Collections.reverseOrder());
		this.otherAttributes = otherAttributes;
		otherAttributes.sort(Collections.reverseOrder());
	}
	
	public void setOperations(Vector<FmmlxOperation> operations) {
		ownOperations = new Vector<>();
		otherOperations = new Vector<>();
		for (FmmlxOperation o : operations) {
			if (o.getOwner().equals(this.ownPath)) {
				ownOperations.add(o);
				ownOperations.sort(Collections.reverseOrder());
			} else {
				otherOperations.add(o);
				otherOperations.sort(Collections.reverseOrder());
			}
		}
	}

	public void setConstraints(Vector<Constraint> constraints) {
		Collections.sort(constraints);
		this.constraints = constraints;
	}

	@Deprecated public void fetchDataValues(FmmlxDiagramCommunicator comm) throws TimeOutException {
		slots = comm.fetchSlots(diagram, this, this.getSlotNames());

		operationValues = comm.fetchOperationValues(diagram, this.name, this.getMonitoredOperationsNames());
	}

	/// User interaction
	
	@Override
	public ObjectContextMenu getContextMenu(FmmlxDiagram.DiagramViewPane fmmlxDiagram, Point2D absolutePoint) {
		Point2D relativePoint = new Point2D(absolutePoint.getX() - getX(), absolutePoint.getY() - getY());
		return new ObjectContextMenu(this, fmmlxDiagram, relativePoint);
	}
	
	public FmmlxProperty handlePressedOnNodeElement(Point2D relativePoint, FmmlxDiagram.DiagramViewPane view, GraphicsContext g, Affine currentTransform) {
		if(relativePoint == null) return null;
		if(!view.getDiagram().isSelected(this)) {
			lastClick = null; return null;
 		}
		lastClick = relativePoint;
		currentTransform = new Affine(currentTransform); // copy
		currentTransform.append(new Affine(1, 0, x, 0, 1, y));
		NodeBaseElement hitLabel = getHitLabel(relativePoint, g, currentTransform, view);
		if (hitLabel != null && hitLabel.getActionObject() != null) {
			if (hitLabel.getActionObject().getPropertyType() != PropertyType.Class) {
				hitLabel.setSelected();
				return hitLabel.getActionObject();
			}
		}
		return null;
	}

	public NodeBaseElement getHitLabel(Point2D mouse, GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane view) {
		NodeBaseElement hitLabel = null;
		if(rootNodeElement != null) if(hitLabel == null) {
			 hitLabel = rootNodeElement.getHitLabel(mouse, g, currentTransform, view);//new Point2D(relativePoint.getX() - e.getX(), relativePoint.getY() - e.getY()));
		}
		return hitLabel;
	}

	public void performDoubleClickAction(Point2D p, GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane view) {
		if(p == null) return;
		NodeBaseElement hitLabel = getHitLabel(p, g, currentTransform, view);
		if(hitLabel != null) hitLabel.performDoubleClickAction();
	}

	public PaletteItem toPaletteItem(FmmlxDiagram fmmlxDiagram) {
		PaletteTool tool = new ToolClass(fmmlxDiagram, getName(), ownPath+"", getLevel(), isAbstract, "");
		return new PaletteItem(tool);
	}

	@Override
	public int compareTo(FmmlxObject anotherObject) {
		if(this.getLevel()>anotherObject.getLevel()) {
			return 1;
		} else if (this.getLevel()<anotherObject.getLevel()) {
			return -1;
		} else {
			return this.name.compareTo(anotherObject.getName());
		}
	}

	public boolean notTraditionalDataTypeExists() {
		for (FmmlxAttribute att: getAllAttributes()) {
			switch (att.getType()) {
				case "String":
				case "Integer":
				case "Float":
				case "Boolean":
					break;
				default:
					return true;
			}
		}
		return false;
	}

	@Override
	protected void layout(FmmlxDiagram diagram) {
		if(ofPath.endsWith("StartEvent")) {
			new ExperimentalFmmlxObjectDisplay(diagram, this).layoutStartEvent();
		} else if(ofPath.endsWith("StopEvent")) {
			new ExperimentalFmmlxObjectDisplay(diagram, this).layoutStopEvent();
		} else if(ofPath.endsWith("Event")) {
			new ExperimentalFmmlxObjectDisplay(diagram, this).layoutEvent();
		} else if(ofPath.endsWith("Signal")) {
			new ExperimentalFmmlxObjectDisplay(diagram, this).layoutSignal();
		} else if(ofPath.endsWith("ComputerSupportedProcess")) {
			new ExperimentalFmmlxObjectDisplay(diagram, this).layoutComputerSupportedProcess();
		} else if(ofPath.endsWith("AutomatedProcess")) {
			new ExperimentalFmmlxObjectDisplay(diagram, this).layoutAutomatedProcess();
		} else {
			new DefaultFmmlxObjectDisplay(diagram, this).layout();
		}
	}

	public void dragTo(Affine dragAffine) {
		rootNodeElement.dragTo(dragAffine);
	}

	public void drop() {
		rootNodeElement.drop();
		this.x = rootNodeElement.getMyTransform().getTx();
		this.y = rootNodeElement.getMyTransform().getTy();
	}
}
