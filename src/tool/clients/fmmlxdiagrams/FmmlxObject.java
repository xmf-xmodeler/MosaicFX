package tool.clients.fmmlxdiagrams;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer.PathNotFoundException;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.graphics.ConcreteSyntax;
import tool.clients.fmmlxdiagrams.graphics.NodeElement;
import tool.clients.fmmlxdiagrams.graphics.wizard.ConcreteSyntaxIcon;
import tool.clients.fmmlxdiagrams.menus.ObjectContextMenu;
import tool.clients.fmmlxdiagrams.newpalette.PaletteItem;
import tool.clients.fmmlxdiagrams.newpalette.PaletteTool;
import tool.clients.fmmlxdiagrams.newpalette.ToolClass;
import tool.clients.fmmlxdiagrams.uml.UmlObjectDisplay;

import java.util.*;

public class FmmlxObject extends Node implements CanvasElement, FmmlxProperty, Comparable<FmmlxObject> {

	final String name;
	final String ownPath;
	final String ofPath;
	private final Vector<String> parentsPaths;

	private final boolean isAbstract;
	private final boolean isSingleton;
	final Level level;
    
	Vector<FmmlxSlot> slots = new Vector<>();
	Vector<FmmlxOperationValue> operationValues = new Vector<>();

	private Vector<FmmlxAttribute> ownAttributes = new Vector<>();
	private Vector<FmmlxAttribute> otherAttributes = new Vector<>();

	private Vector<FmmlxOperation> ownOperations = new Vector<>();
	private Vector<FmmlxOperation> otherOperations = new Vector<>();
	
	private Vector<Constraint> constraints = new Vector<>();

	private AbstractPackageViewer diagram;

	public FmmlxObject(
			String name, 
			Integer minlevel, 
			Integer maxLevel, 
			String ownPath,
			String ofPath,
			Vector<String> parentPaths,
			Boolean isAbstract,
			Boolean isSingleton,
			Integer lastKnownX, Integer lastKnownY, Boolean hidden,
			AbstractPackageViewer diagram) {
		super();
		this.name = name;
		this.diagram = diagram;
		this.x = lastKnownX;
		this.y = lastKnownY;
		this.hidden = hidden;
		this.level = new Level(minlevel, maxLevel);
		this.isAbstract = isAbstract;
		this.isSingleton = isSingleton;

		this.ownPath = ownPath;
		this.ofPath = ofPath;
		this.parentsPaths = parentPaths;
	}

	/// Getters
	
	public ObservableList<FmmlxAttribute> getAllAttributesAsList() { 
		return FXCollections.observableArrayList(getAllAttributes());}
	public Vector<FmmlxOperation> getOwnOperations() { return ownOperations; }
	public Vector<FmmlxOperation> getOtherOperations() { return otherOperations; }
	public Vector<FmmlxAttribute> getOwnAttributes() { return new Vector<>(ownAttributes); }
	public Vector<FmmlxAttribute> getOtherAttributes() { return new Vector<>(otherAttributes); }	
	public String getName() { return name; }
	public Level getLevel() { return level; }
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
			if (this.level.getMinLevel() == asso.getLevelSource() && this.isInstanceOf(asso.sourceNode, this.level.getMinLevel()) || 
				this.level.getMinLevel() == asso.getLevelTarget() && this.isInstanceOf(asso.targetNode, this.level.getMinLevel())) {
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

				if(o.getLevel() < level.getMinLevel() &&! delegatedOperations.contains(o)) delegatedOperations.add(o);
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
				if(o.isDelegateToClassAllowed() && o.getLevel() == this.level.getMinLevel()) {
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
	
	public FmmlxOperationValue getOperationValue(String opVName) {
		for(FmmlxOperationValue opValue : operationValues) {
			if(opValue.getName().equals(opVName)) return opValue;
		}
		return null;
	}
	
	public Vector<String> getParentsPaths() {
		return parentsPaths;
	}
	
	public Vector<FmmlxObject> getInstances() {
		Vector<FmmlxObject> result = new Vector<>();
		for (FmmlxObject object : diagram.getObjectsReadOnly()) {
			if (object.getOfPath().equals(this.ownPath)) {
				result.add(object);
			}
		}
		return result;
	}

	public HashSet<FmmlxObject> getInstancesByLevel(Integer level) {
		HashSet<FmmlxObject> allClasses = this.getAllSubclasses();
		while (allClasses.size()>0 && allClasses.iterator().next().level.getMinLevel()>level) {
			HashSet<FmmlxObject> allInstances = new HashSet<FmmlxObject>();
			for (FmmlxObject classs : allClasses) {
				allInstances.addAll(classs.getInstances());
			}
			allClasses = allInstances;
		}
		return allClasses;
	}

	public boolean isAbstract() {return isAbstract;}
	public boolean isSingleton() {return isSingleton;}

	Vector<String> getSlotNames() {
		Vector<String> slotNames = new Vector<>();
		Vector<FmmlxObject> ancestors = getAllAncestors();
		ancestors.add(this);
		for (FmmlxObject ancestor : ancestors) {
			for (FmmlxAttribute attribute : ancestor.getAllAttributes()) {
				if (attribute.level == this.level.getMinLevel() && !slotNames.contains(attribute.name)) {
					slotNames.add(attribute.name);
				}
			}
		}
		for (FmmlxObject ancestor : getAllAncestorsNextLevel()) {
			for (FmmlxAttribute attribute : ancestor.getAllAttributes()) {
				if (attribute.level == -1 && !slotNames.contains(attribute.name)) {
					slotNames.add(attribute.name);
				}
			}
		}
		return slotNames;
	}

	Vector<String> getMonitoredOperationsNames() {
		Vector<String> monitorNames = new Vector<>();
		for (FmmlxObject ancestor : getAllAncestors()) {
			Vector<FmmlxOperation> ops = new Vector<>();
			ops.addAll(ancestor.getAllOperations());
			ops.addAll(ancestor.getDelegatedOperations());
			for (FmmlxOperation operation : ops) {
				if (operation.getLevel() == this.level.getMinLevel() && operation.isMonitored() && !monitorNames.contains(operation.getName())) {
					monitorNames.add(operation.getName());
				}
			}
		}
		return monitorNames;
	}
	
	public Vector<String> getAvailableNoArgumentOperationNames() {
		Vector<String> availableNames = new Vector<>();
		for (FmmlxObject ancestor : getAllAncestors()) {
			Vector<FmmlxOperation> ops = new Vector<>();
			ops.addAll(ancestor.getAllOperations());
			ops.addAll(ancestor.getDelegatedOperations());
			for (FmmlxOperation operation : ops) {
				if (operation.getLevel() == this.level.getMinLevel() && !availableNames.contains(operation.getName()) && operation.getParamNames().size() == 0) {
					availableNames.add(operation.getName());
				}
			}
		}
		return availableNames;
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
		for (FmmlxObject o : result1) if (o.level.isFixedLevelClass()) {
			result2.addAll(o.getAllAncestors());
		}
		return result2;
	}
	
	public Vector<FmmlxObject> getAllAncestorsNextLevel() {
		if("Root::XCore::Class".equals(ownPath)) return new Vector<FmmlxObject>();
		Vector<FmmlxObject> result1 = new Vector<>();
		if (ofPath != null) {
			try{FmmlxObject of = diagram.getObjectByPath(getOfPath());result1.add(of);}
			catch (PathNotFoundException e) {} 
		}
		Vector<FmmlxObject> result2 = new Vector<>(result1);
		while(!result1.isEmpty()) {
			FmmlxObject first = result1.firstElement();
			result1.remove(0);
			if(!result2.contains(first)) {
				result2.add(first);
			
				for (String p : first.getParentsPaths()) {
					try
					  {FmmlxObject parent = diagram.getObjectByPath(p); 
					  if(!result1.contains(parent)) result1.add(parent); }
					catch(PathNotFoundException e) {}
				}
			}
		}
		return result2;
	}


	public boolean isInstanceOf(FmmlxObject theClass, Integer myLevel) {
		// -1 is allowed for contingent level associations
		if (myLevel != level.getMinLevel() && myLevel != -1) return false;
		return this.getAllAncestors().contains(theClass);
	}


	public String getAvailableInstanceName() {
		String s = name;
		if (level.getMinLevel() == 1) {
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

	public HashSet<FmmlxObject> getAllSubclasses() {
		HashSet<FmmlxObject> subclasses = new HashSet<FmmlxObject>();
		subclasses.add(this);
		for (FmmlxObject o : diagram.getObjectsReadOnly()) {
			if(o.parentsPaths.contains(this.ownPath)) {
				subclasses.addAll(o.getAllSubclasses());
			}
		}
		return subclasses;
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
			if ("Root::FMMLx::MetaClass".equals(ofPath)) return "MetaClass";
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
	
	public Vector<Constraint> getAllConstraints() {
		HashSet<Constraint> allConstraints = new HashSet<>(constraints);
		for(FmmlxObject o : getAllAncestors()) {
			allConstraints.addAll(o.getConstraints());
		}
		return new Vector<>(allConstraints);
	}

	/// Setters

	public void setAttributes(Vector<FmmlxAttribute> ownAttributes, Vector<FmmlxAttribute> otherAttributes) {
		this.ownAttributes = ownAttributes;
		Collections.sort(ownAttributes);
		this.otherAttributes = otherAttributes;
		Collections.sort(otherAttributes);
	}
	
	public void setOperations(Vector<FmmlxOperation> operations) {
		ownOperations = new Vector<>();
		otherOperations = new Vector<>();
		for (FmmlxOperation o : operations) {
			if (o.getOwner().equals(this.ownPath)) {
				ownOperations.add(o);
				Collections.sort(ownOperations);
			} else {
				otherOperations.add(o);
				Collections.sort(otherOperations);
			}
		}
	}

	public void setConstraints(Vector<Constraint> constraints) {
		Collections.sort(constraints);
		this.constraints = constraints;
	}

	@Override
	public ObjectContextMenu getContextMenu(FmmlxDiagram.DiagramViewPane fmmlxDiagram, Point2D absolutePoint) {
		return new ObjectContextMenu(this, fmmlxDiagram, absolutePoint);
	}
	
	public FmmlxProperty handlePressedOnNodeElement(Point2D relativePoint, FmmlxDiagram.DiagramViewPane view, GraphicsContext g, Affine currentTransform) {
		if(relativePoint == null) return null;
		if(!view.getDiagram().isSelected(this)) {
			lastClick = null; return null;
 		}
		lastClick = relativePoint;
		currentTransform = new Affine(currentTransform); // copy
		currentTransform.append(new Affine(1, 0, x, 0, 1, y));
		NodeElement hitLabel = getHitElement(relativePoint, g, currentTransform, view);
		if (hitLabel != null && hitLabel.getActionObject() != null) {
			if (hitLabel.getActionObject().getPropertyType() != PropertyType.Class) {
				hitLabel.setSelected();
				return hitLabel.getActionObject();
			}
		}
		return null;
	}

	public NodeElement getHitElement(Point2D mouse, GraphicsContext g, Affine currentTransform, FmmlxDiagram.DiagramViewPane view) {
		NodeElement hitLabel = null;
		if(rootNodeElement != null) if(hitLabel == null) {
			 hitLabel = rootNodeElement.getHitElement(mouse, g, currentTransform, view);//new Point2D(relativePoint.getX() - e.getX(), relativePoint.getY() - e.getY()));
		}
		return hitLabel;
	}

	public PaletteItem toPaletteItem(FmmlxDiagram fmmlxDiagram) {
		PaletteTool tool = new ToolClass(fmmlxDiagram, getName(), ownPath+"", getLevel().getMinLevel(), isAbstract||isSingleton, "");
		return new PaletteItem(tool);
	}

	@Override
	public int compareTo(FmmlxObject anotherObject) {
		if(this.getLevel().getMinLevel()>anotherObject.getLevel().getMinLevel()) {
			return -1;
		} else if (this.getLevel().getMinLevel()<anotherObject.getLevel().getMinLevel()) {
			return 1;
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

	protected void layout(FmmlxDiagram diagram, Map<DiagramDisplayProperty, Boolean> diagramToolBarProperties) {
		if (!diagramToolBarProperties.get(DiagramDisplayProperty.CONCRETESYNTAX)){
			if(diagram.umlMode) {
				new UmlObjectDisplay(diagram, this).layout(diagramToolBarProperties);
			} else {
				new DefaultFmmlxObjectDisplay(diagram, this).layout(diagramToolBarProperties);
			}
			return;
		}
		
		// try to find concrete syntax:
		ConcreteSyntax myConcreteSyntax = findMyConcreteSyntax(diagram,0);
		
		if(myConcreteSyntax != null) {
			rootNodeElement = myConcreteSyntax.createInstance(this, diagram);
		} else {	
			if(diagram.umlMode) {
				new UmlObjectDisplay(diagram, this).layout(diagramToolBarProperties);
			} else {
				new DefaultFmmlxObjectDisplay(diagram, this).layout(diagramToolBarProperties);
			}
		}
		
		if(rootNodeElement != null) rootNodeElement.updateBounds();
	}

	private ConcreteSyntax findMyConcreteSyntax(FmmlxDiagram diagram, int levelDiff) {
		ConcreteSyntax myConcreteSyntax = null;
		for(ConcreteSyntax c : new Vector<>(diagram.syntaxes.values())) if(myConcreteSyntax == null) {
			try{
				FmmlxObject classs = diagram.getObjectByPath(c.classPath);
				if(this.isInstanceOf(classs, this.level.getMinLevel()) && this.level.getMinLevel() == c.level+levelDiff) {
					myConcreteSyntax = c;
				}
				if(this.ownPath.equals(c.classPath) && this.level.getMinLevel() == c.level+levelDiff) {
					myConcreteSyntax = c;
				}
			} catch (Exception e) {}
		}
		return myConcreteSyntax;
	}

	public void dragTo(Affine dragAffine) {
		rootNodeElement.dragTo(dragAffine);
	}

	public void drop() {
		rootNodeElement.drop();
		this.x = rootNodeElement.getMyTransform().getTx();
		this.y = rootNodeElement.getMyTransform().getTy();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o==null) return false;
		if(o instanceof FmmlxObject) {
			return ownPath.equals(((FmmlxObject)o).ownPath);
		} else return false;
		
	}

	public FmmlxObject getOf() {
		try{
			return diagram.getObjectByPath(ofPath);
		} catch (Exception e) {
			throw new IllegalStateException("The meta-class of this element is not available.");
		}
	}

	@Override
	protected void layout(FmmlxDiagram diagram) {
		layout(diagram, diagram.getDiagramViewToolBarModel().getDisplayPropertiesMap());
	}
	
	private transient Vector<Issue> cachedIssues = null;
	public Vector<Issue> getIssues() {
		if(cachedIssues == null) cachedIssues = diagram.getIssues(this);
		return cachedIssues;
	}
	
	public static String getRelativePath(String fullPathNameSource, String fullPathNameTarget) { 
		String[] fromPath = fullPathNameSource.split("::");
		String[] toPath = fullPathNameTarget.split("::");
		int i = 0; 
		while(fromPath.length > i && toPath.length > i && fromPath[i].equals(toPath[i])) i++;
		String path = "";
		while(toPath.length > i ) {
			path += "::" + toPath[i];
			i++;
		}
		return path.substring(2); 
	}
	
	public AbstractPackageViewer getDiagram() {
		return this.diagram;
	}

	@Deprecated
	/* TODO create a new class for non-Fmmlx-Objects */
	public String type;

	public boolean isClass() {
		return level.isClass();
	}

	public javafx.scene.Node getConcreteSyntaxIcon(int size) {
		if(!(diagram instanceof FmmlxDiagram)) return null;
		ConcreteSyntax myConcreteSyntax = findMyConcreteSyntax((FmmlxDiagram)diagram, 1);
		if(myConcreteSyntax == null) return null;
		return new ConcreteSyntaxIcon(myConcreteSyntax, size);
	}

	public String getRelativeName() {
		return FmmlxObject.getRelativePath(diagram.packagePath, getPath());
	}

	public boolean hasIssue(String constraintName) {
		for(Issue i : getIssues()) {
			if(i.getName().equals(constraintName)) return true;
		}
		return false;
	}

}
