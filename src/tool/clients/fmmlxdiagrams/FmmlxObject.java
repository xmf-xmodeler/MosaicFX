package tool.clients.fmmlxdiagrams;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.paint.*;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.menus.ObjectContextMenu;
import tool.clients.fmmlxdiagrams.newpalette.PaletteItem;
import tool.clients.fmmlxdiagrams.newpalette.PaletteTool;
import tool.clients.fmmlxdiagrams.newpalette.ToolClass;

import java.util.*;

public class FmmlxObject extends Node implements CanvasElement, FmmlxProperty, Comparable<FmmlxObject> {

	private String name;
	private String ownPath;
	private String ofPath;
	private Vector<String> parentsPaths;
	
	private boolean isAbstract;
	int level;
    
	private final static NodeBaseElement.Action NO_ACTION = () -> {};

	private boolean showOperations = true;
	private boolean showOperationValues = true;
	private boolean showSlots = true;
	private boolean showGettersAndSetters = true;
	private boolean showDerivedOperations = true;
	private boolean showDerivedAttributes = true;
	private boolean showConstraints = true;

	static int testDiff = 10;

	static int GAP = 5;

	final int INST_LEVEL_WIDTH = 7;
	final int MIN_BOX_HEIGHT = 4;
	final int EXTRA_Y_PER_LINE = 3;

	Vector<FmmlxSlot> slots = new Vector<>();
	private Vector<FmmlxOperationValue> operationValues = new Vector<>();

	private Vector<FmmlxAttribute> ownAttributes = new Vector<>();
	private Vector<FmmlxAttribute> otherAttributes = new Vector<>();

	private Vector<FmmlxOperation> ownOperations = new Vector<>();
	private Vector<FmmlxOperation> otherOperations = new Vector<>();
	
	private Vector<Constraint> constraints = new Vector<>();

	private AbstractPackageViewer diagram;
	private PropertyType propertyType = PropertyType.Class;


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
		if (lastKnownX != null && lastKnownX != 0) {
			x = lastKnownX;
		} else {
			x = testDiff;
			testDiff += 50;
		}
		if (lastKnownY != null && lastKnownY != 0) {
			y = lastKnownY;
		} else {
			y = 10;
		}
		
		this.hidden = hidden;

		width = 150;
		height = 80;
		this.level = level;
		this.isAbstract = isAbstract;

		this.ownPath = ownPath;
		this.ofPath = ofPath;
		this.parentsPaths = parentPaths;

		this.showOperations = true;
		this.showOperationValues = true;
		this.showSlots = true;
		this.showDerivedOperations = true;
		this.showDerivedAttributes = true;
		this.showGettersAndSetters = true;
		
	}

	private String getParentsList(FmmlxDiagram diagram) {
		StringBuilder parentsList = new StringBuilder("extends ");
		for (String parentName : getParentsPaths()) {
			try {
				FmmlxObject parent = diagram.getObjectByPath(parentName);
				InheritanceEdge edge = diagram.getInheritanceEdge(this, parent);
				if(edge != null && !edge.isVisible()) {
					parentName = parent.name;
					parentsList.append(parentName).append(", ");
				} 
			} catch (Exception e) {
				e.printStackTrace();
				parentName = e.getMessage();
				parentsList.append(parentName).append(", ");
			}
		}
		if(!("extends ".equals(parentsList.toString()))) return parentsList.substring(0, parentsList.length() - 2);
		return "";
	}

	public String getName() { return name; }
	public int getLevel() { return level; }

	public String getOfPath() {
		return ofPath;
	}

	public Vector<FmmlxAttribute> getOwnAttributes() {
		return new Vector<>(ownAttributes);
	}

	public Vector<FmmlxAttribute> getOtherAttributes() {
		return new Vector<>(otherAttributes);
	}

	public Vector<FmmlxAttribute> getAllAttributes() {
		Vector<FmmlxAttribute> result = new Vector<>();
		result.addAll(ownAttributes);
		result.addAll(otherAttributes);
		return result;
	}

	public Vector<FmmlxAssociation> getAllRelatedAssociations() {
		return diagram.getRelatedAssociationByObject(this);
	}

	public ObservableList<FmmlxAttribute> getAllAttributesAsList() {
		return FXCollections.observableArrayList(getAllAttributes());
	}

	public Vector<FmmlxOperation> getOwnOperations() {
		return ownOperations;
	}

	public Vector<FmmlxOperation> getOtherOperations() {
		return otherOperations;
	}
	
	public Vector<FmmlxOperation> getDelegatedOperations() {
		Vector<FmmlxOperation> delegatedOperations = new Vector<>();
		FmmlxObject delegatesTo = getDelegatesTo();
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
		FmmlxObject of = diagram.getObjectByPath(ofPath);
		if(of != null) {
			Vector<FmmlxOperation> ofOps = new Vector<>(of.ownOperations);
			ofOps.addAll(of.otherOperations);
			ofOps.addAll(of.getDelegatedOperations());
			
			for(FmmlxOperation o : ofOps) {
				if(o.isDelegateToClassAllowed() && o.getLevel() == this.level) {
					delelegateToClassOperations.add(o);
				}
			}
		}
		return delelegateToClassOperations;
	}
	
	public FmmlxObject getDelegatesTo() {
		for(Edge e : diagram.getEdges()) {
			if(e instanceof DelegationEdge) {
				DelegationEdge de = (DelegationEdge) e;
				if(de.sourceNode == this) {
					return (FmmlxObject) de.targetNode;
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

	public Paint getLevelBackgroundColor(FmmlxDiagram diagram) {
		return diagram.levelColorScheme.getLevelBgColor(level);
		//return colors.containsKey(level) ? colors.get(level) : Color.valueOf("#ffaa00");
	}

	public Color getLevelFontColor(double opacity, FmmlxDiagram diagram) {
		return diagram.levelColorScheme.getLevelFgColor(level, opacity);
		///return new Vector<>(Arrays.asList(2, 3, 4, 5)).contains(level) ? "#ffffff" : "000000";
	}

	public boolean getShowOperations() {
		return showOperations;
	}

	public boolean getShowOperationValues() {
		return showOperationValues;
	}

	public boolean getShowSlots() {
		return showSlots;
	}
	
	public boolean getShowGetterAndSetter() {
		return showGettersAndSetters;
	}

	public Vector<NodeElement> getNodes() {
		return nodeElements;
	}

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


	public void layout(FmmlxDiagram diagram) {
		requiresReLayout = false;
		nodeElements = new Vector<>();
		double neededWidth = calculateNeededWidth(diagram);
		//determine text height
		double textHeight = FmmlxDiagram.calculateTextHeight();
		double lineHeight = textHeight + EXTRA_Y_PER_LINE;
		double currentY = 0;		

		String parentString = getParentsList(diagram);
		int headerLines = /*hasParents()*/(!"".equals(parentString)) ? 3 : 2;
		NodeBox header = new NodeBox(0, currentY, neededWidth, textHeight * headerLines + EXTRA_Y_PER_LINE, getLevelBackgroundColor(diagram), Color.BLACK, (x) -> 1., PropertyType.Class);
		nodeElements.addElement(header);
		FmmlxObject ofObj = null;
		try {
			ofObj = diagram.getObjectByPath(getOfPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String ofName = (ofObj == null) ? "MetaClass" : ofObj.name;
		
		NodeLabel metaclassLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight, getLevelFontColor(.65, diagram), null, this, NO_ACTION, "^" + ofName + "^", FontPosture.REGULAR, FontWeight.BOLD) ;
		NodeLabel levelLabel = new NodeLabel(Pos.BASELINE_LEFT, 4, textHeight * 2, getLevelFontColor(.4, diagram), null, this, NO_ACTION, "" + level, FontPosture.REGULAR, FontWeight.BOLD, 3.);
		NodeLabel nameLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight * 2, getLevelFontColor(1., diagram), null, this, NO_ACTION, name, isAbstract?FontPosture.ITALIC:FontPosture.REGULAR, FontWeight.BOLD);
		header.nodeElements.add(metaclassLabel);
		header.nodeElements.add(levelLabel);
		header.nodeElements.add(nameLabel);

		if ((!"".equals(parentString))) {
			NodeLabel parentsLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight * 3, getLevelFontColor(1., diagram), null, this, NO_ACTION, parentString, isAbstract?FontPosture.ITALIC:FontPosture.REGULAR, FontWeight.NORMAL);
			header.nodeElements.add(parentsLabel);
		}

		currentY += headerLines * textHeight + EXTRA_Y_PER_LINE;
		
		Vector<Issue> issues = diagram.getIssues(this);
		if(issues.size() > 0) {
			double issueBoxHeight = lineHeight * issues.size() + EXTRA_Y_PER_LINE;
			NodeBox issueBox = new IssueBox(0, currentY, neededWidth, issueBoxHeight, 
				Color.BLACK, Color.BLACK, (x) -> 1., PropertyType.Issue);
			nodeElements.addElement(issueBox);
			double issY = 0;
			
			for(Issue i : issues) {
				issY += lineHeight;
				
				NodeLabel issueLabel = new NodeLabel(Pos.BASELINE_LEFT, IssueBox.BOX_SIZE * 1.5, issY, new Color(1., .8, 0., 1.), null, this, () -> i.performResolveAction(diagram), i.getText());
				issueBox.nodeElements.add(issueLabel);
				issueLabel.activateSpecialMode(neededWidth - 3 * IssueBox.BOX_SIZE);
			}
		
			currentY += issueBoxHeight;
		}

		int attSize = countAttributesToBeShown();
		double attBoxHeight = Math.max(lineHeight * attSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double yAfterAttBox = currentY + attBoxHeight;
		double attY = 0;
		NodeBox attBox = new NodeBox(0, currentY, neededWidth, attBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.Attribute);
		nodeElements.addElement(attBox);

		for (FmmlxAttribute att : ownAttributes) {
			attY += lineHeight;
			NodeLabel.Action changeAttNameAction = () -> diagram.getActions().changeNameDialog(this, PropertyType.Attribute, att);
			NodeLabel attLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, attY, Color.BLACK, null, att, changeAttNameAction, att.getName() + ":" + att.getTypeShort() +"["+ att.getMultiplicity() + "]");
			attBox.nodeElements.add(attLabel);
			NodeLabel.Action changeAttLevelAction = () -> diagram.getActions().changeLevelDialog(this, PropertyType.Attribute);
			NodeLabel attLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, attY, Color.WHITE, Color.BLACK, att, changeAttLevelAction, att.level + "");
			attBox.nodeElements.add(attLevelLabel);
		}
		for (FmmlxAttribute att : otherAttributes) {
			if(showDerivedAttributes) {
			attY += lineHeight;
			NodeLabel attLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, attY, Color.GRAY, null, att, NO_ACTION, att.getName() + ":" + att.getTypeShort() +"["+ att.getMultiplicity() + "]" + " (from " + diagram.getObjectByPath(att.owner).name + ")");
			attBox.nodeElements.add(attLabel);
			NodeLabel attLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, attY, Color.WHITE, Color.GRAY, att, NO_ACTION, att.level + "");
			attBox.nodeElements.add(attLevelLabel);
			}
		}
		currentY = yAfterAttBox;

		double yAfterOpsBox = currentY;

		int opsSize = countOperationsToBeShown();
		double opsBoxHeight = Math.max(lineHeight * opsSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double opsY = 0;
		NodeBox opsBox = new NodeBox(0, currentY, neededWidth, opsBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.Operation);
		if (showOperations && opsSize > 0) {
			yAfterOpsBox = currentY + opsBoxHeight;
			nodeElements.addElement(opsBox);
			for (FmmlxOperation o : ownOperations) {
				if(showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
					opsY += lineHeight;
					NodeLabel.Action changeOpLevelAction = () -> diagram.getActions().changeLevelDialog(this, PropertyType.Operation);
					NodeLabel opLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, opsY, Color.WHITE, Color.BLACK, o, changeOpLevelAction, o.getLevelString() + "");
					opsBox.nodeElements.add(opLevelLabel);
					int labelX = 14;
					if(o.isDelegateToClassAllowed()) {
						NodeImage delIcon = new NodeImage(14, opsY, "resources/gif/XCore/delegationDown.png", o, NO_ACTION);
						opsBox.nodeElements.add(delIcon);
						labelX +=16;
					}					
					NodeLabel.Action changeOpNameAction = () -> diagram.getActions().changeNameDialog(this, PropertyType.Operation, o);
					NodeLabel opLabel = new NodeLabel(Pos.BASELINE_LEFT, labelX, opsY, Color.BLACK, null, o, changeOpNameAction, o.getFullString(diagram));
					opsBox.nodeElements.add(opLabel);
				}
			}
			for (FmmlxOperation o : otherOperations) {
				if(showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
					if(showDerivedOperations) {
					opsY += lineHeight;
					NodeLabel oLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, opsY, Color.WHITE, Color.GRAY, o, NO_ACTION, o.getLevelString() + "");
					opsBox.nodeElements.add(oLevelLabel);
					NodeImage inhIcon = new NodeImage(14, opsY, (diagram.getObjectByPath(o.getOwner()).getLevel() == level) ? "resources/gif/Inheritance.gif" : "resources/gif/Dependency.gif", o, NO_ACTION);
					opsBox.nodeElements.add(inhIcon);
					int labelX = 30;
					if(o.isDelegateToClassAllowed()) {
						NodeImage delIcon = new NodeImage(30, opsY, "resources/gif/XCore/delegationDown.png", o, NO_ACTION);
						opsBox.nodeElements.add(delIcon);
						labelX +=16;
					}	
					NodeLabel oLabel = new NodeLabel(Pos.BASELINE_LEFT, labelX, opsY, Color.GRAY, null, o, NO_ACTION, o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).name + ")");
					opsBox.nodeElements.add(oLabel);
					}
				}
			}
			for (FmmlxOperation o : getDelegatedOperations()) {
				if(showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
					if(showDerivedOperations) {
					opsY += lineHeight;
					NodeLabel oLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, opsY, Color.WHITE, Color.GRAY, o, NO_ACTION, o.getLevelString() + "");
					opsBox.nodeElements.add(oLevelLabel);
					String iconS = "resources/gif/Inheritance.gif";
					if(diagram.getObjectByPath(ofPath) != null && diagram.getObjectByPath(ofPath).getAllOperations().contains(o)) iconS = "resources/gif/Dependency.gif";
					if(getDelegatesTo() != null && getDelegatesTo().getAllOperations().contains(o)) iconS = "resources/gif/XCore/delegation.png";
						
					NodeImage delIcon = new NodeImage(14, opsY, iconS, o, NO_ACTION);
					opsBox.nodeElements.add(delIcon);
					int labelX = 30;
					if(o.isDelegateToClassAllowed()) {
						NodeImage del2Icon = new NodeImage(30, opsY, "resources/gif/XCore/delegationDown.png", o, NO_ACTION);
						opsBox.nodeElements.add(del2Icon);
						labelX +=16;
					}	
					NodeLabel oLabel = new NodeLabel(Pos.BASELINE_LEFT, labelX, opsY, Color.GRAY, null, o, NO_ACTION, o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).name + ")");
					opsBox.nodeElements.add(oLabel);
					}
				}
			}			
			for (FmmlxOperation o : getDelegateToClassOperations()) {
				if(showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
					if(showDerivedOperations) {
						opsY += lineHeight;
						NodeLabel oLabel = new NodeLabel(Pos.BASELINE_LEFT, 30, opsY, Color.GRAY, null, o, NO_ACTION, o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).name + ")");
						opsBox.nodeElements.add(oLabel);
						NodeLabel oLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, opsY, Color.WHITE, Color.GRAY, o, NO_ACTION, o.getLevelString() + "");
						opsBox.nodeElements.add(oLevelLabel);
						NodeImage delIcon = new NodeImage(14, opsY, "resources/gif/XCore/delegationUp.png", o, NO_ACTION);
						opsBox.nodeElements.add(delIcon);
					}
				}
			}
		}
		currentY = yAfterOpsBox;
		
		double yAfterConstraintBox = currentY;
		int constraintSize = constraints.size();
		double constraintBoxHeight = Math.max(lineHeight * constraintSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double constraintY = 0;
		NodeBox coinstraintsBox = new NodeBox(0, currentY, neededWidth, constraintBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.Constraint);
		if (showConstraints && constraintSize > 0) {
			yAfterConstraintBox = currentY + constraintBoxHeight;
			nodeElements.addElement(coinstraintsBox);
			for (Constraint con : constraints) {
				constraintY += lineHeight;
				NodeLabel constraintLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, constraintY, new Color(.8,0,0,1), null, con, NO_ACTION, con.getName());
				coinstraintsBox.nodeElements.add(constraintLabel);
				NodeLabel constraintLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, constraintY, Color.WHITE, new Color(.8,0,0,1), con, NO_ACTION, con.level + "");
				coinstraintsBox.nodeElements.add(constraintLevelLabel);
			}
		}
		currentY = yAfterConstraintBox;

		double yAfterSlotBox = currentY;
		int slotSize = slots.size();
		double slotBoxHeight = Math.max(lineHeight * slotSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double slotsY = 0;
		NodeBox slotsBox = new NodeBox(0, currentY, neededWidth, slotBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.Slot);
		if (showSlots && slotSize > 0) {
			yAfterSlotBox = currentY + slotBoxHeight;
			nodeElements.addElement(slotsBox);
			for (FmmlxSlot s : slots) {
				slotsY += lineHeight;
				NodeLabel.Action changeSlotValueAction = () -> diagram.getActions().changeSlotValue(this, s);
				NodeLabel slotNameLabel = new NodeLabel(Pos.BASELINE_LEFT, 3, slotsY, Color.BLACK, null, s, changeSlotValueAction, s.getName() + " = ");
				slotsBox.nodeElements.add(slotNameLabel);
				NodeLabel slotValueLabel = new NodeLabel(Pos.BASELINE_LEFT, 3 + slotNameLabel.getWidth(), slotsY, new Color(0.0,0.4,0.2,1.0), new Color(0.85,0.9,0.85,1.0), s, changeSlotValueAction, "" + s.getValue());
				slotsBox.nodeElements.add(slotValueLabel);
			}
		}
		currentY = yAfterSlotBox;

		double yAfterOPVBox = currentY;
		int opvSize = operationValues.size();
//		double lineHeight = textHeight + EXTRA_Y_PER_LINE;
		double opvBoxHeight = Math.max(lineHeight * opvSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double opvY = 0;
		NodeBox opvBox = new NodeBox(0, currentY, neededWidth, opvBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.OperationValue);
		if (showOperationValues && opvSize > 0) {
			yAfterOPVBox = currentY + opvBoxHeight;
			nodeElements.addElement(opvBox);
			for (FmmlxOperationValue opv : operationValues) {
				opvY += lineHeight;
				NodeLabel opvNameLabel = new NodeLabel(Pos.BASELINE_LEFT, 3, opvY, Color.BLACK, null, opv, NO_ACTION, opv.getName() + "()->");
				opvBox.nodeElements.add(opvNameLabel);
				NodeLabel opvValueLabel = new NodeLabel(Pos.BASELINE_LEFT, 5 + opvNameLabel.getWidth(), opvY, opv.isInRange()?Color.YELLOW:Color.RED, Color.BLACK, opv, NO_ACTION, "" + opv.getValue());
				opvBox.nodeElements.add(opvValueLabel);
			}
		}
		currentY = yAfterOPVBox;

		NodeBox selectionBox = new NodeBox(0, 0, neededWidth, currentY, new Color(0, 0, 0, 0), Color.BLACK, (selected) -> selected?3:1, PropertyType.Selection);
		nodeElements.addElement(selectionBox);


		this.width = (int) neededWidth;
		this.height = (int) currentY;

		handlePressedOnNodeElement(lastClick, diagram);
	}

	private int countOperationsToBeShown() {
		int counter=0;
		for (FmmlxOperation o : ownOperations) {
			if(showGettersAndSetters  ||  !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
			counter++;	
			}
		}

		for (FmmlxOperation o : otherOperations) {
			if(showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
				if(showDerivedOperations) {
				counter++;
				}
			}
		}		
		for (FmmlxOperation o : getDelegatedOperations()) {
			if(showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
				if(showDerivedOperations) {
				counter++;
				}
			}
		}		
		for (FmmlxOperation o : getDelegateToClassOperations()) {
			if(showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
				if(showDerivedOperations) {
				counter++;
				}
			}
		}
		return counter;
	}
	
	private int countAttributesToBeShown() {
		return otherAttributes.size() + ownAttributes.size();
	}

	private boolean hasParents() {
		return parentsPaths.size() != 0;
	}

	private double calculateNeededWidth(FmmlxDiagram diagram) {
		double neededWidth = FmmlxDiagram.calculateTextWidth(name);

		FmmlxObject of = diagram.getObjectByPath(ofPath);
		if (of!=null) {
			neededWidth = Math.max(neededWidth, FmmlxDiagram.calculateTextWidth(getLevel() + "^" + of.name + "^") + 16);
		} else {
			neededWidth = Math.max(neededWidth, FmmlxDiagram.calculateTextWidth(getLevel() + "^MetaClass^") + 16);
		}

		//determine maximal width of attributes
		for (FmmlxAttribute att : ownAttributes) {
			neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(att.name + ":" + att.getTypeShort() +"["+ att.getMultiplicity() + "]") + INST_LEVEL_WIDTH, neededWidth);
		}
		for (FmmlxAttribute att : otherAttributes) {
			if(showDerivedAttributes) {
			neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(att.name + ":" + att.getTypeShort() +"["+ att.getMultiplicity() + "]" + " (from " + diagram.getObjectByPath(att.owner).name + ")") + INST_LEVEL_WIDTH, neededWidth);
			}
		}
//		//determine maximal width of operations
		if (showOperations) {
			for (FmmlxOperation o : ownOperations) {
				if(showGettersAndSetters  ||  !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
				String text = o.getFullString(diagram);
				neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(text) + INST_LEVEL_WIDTH + (o.isDelegateToClassAllowed()?16:0), neededWidth);
				}
			}	
			for (FmmlxOperation o : otherOperations) {
				if(showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
					if(showDerivedOperations) {
						neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).name + ")") + 4 * INST_LEVEL_WIDTH + (o.isDelegateToClassAllowed()?16:0), neededWidth);
					}
				}
			}	
			for (FmmlxOperation o : getDelegatedOperations()) {
				if(showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
					if(showDerivedOperations) {
						neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).name + ")") + 4 * INST_LEVEL_WIDTH + (o.isDelegateToClassAllowed()?16:0), neededWidth);
					}
				}
			}
			for (FmmlxOperation o : getDelegateToClassOperations()) {
				if(showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
					if(showDerivedOperations) {
						neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).name + ")") + 4 * INST_LEVEL_WIDTH, neededWidth);
					}
				}
			}
		}
		//determine maximal width of slots
		if (showSlots && slots.size() > 0) {
			for (FmmlxSlot slot : slots) {
				neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(slot.getName() + " = " + slot.getValue()), neededWidth);

			}
		}
		if (showOperationValues) {
			for (FmmlxOperationValue opValue : operationValues) {
				neededWidth = Math.max(2+FmmlxDiagram.calculateTextWidth(opValue.getName() + " -> " + opValue.getValue()), neededWidth);
			}
		}
		
		if (showConstraints) {
			for (Constraint con : constraints) {
				neededWidth = Math.max(2+FmmlxDiagram.calculateTextWidth(con.getName()) + INST_LEVEL_WIDTH, neededWidth);
			}
		}

		if (hasParents()) {
			neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(getParentsList(diagram)), neededWidth);
		}

		//if minimum width is not reached just paint minimum
		return Math.max(neededWidth + 2 * GAP, minWidth);
	}

	public void fetchDataDefinitions(FmmlxDiagramCommunicator comm) throws TimeOutException {
		Vector<Vector<FmmlxAttribute>> attributeList = comm.fetchAttributes(diagram, this.name);
		ownAttributes = attributeList.get(0);
		ownAttributes.sort(Collections.reverseOrder());
		otherAttributes = attributeList.get(1);
		otherAttributes.sort(Collections.reverseOrder());
		Vector<FmmlxOperation> operations = comm.fetchOperations(diagram, this.name);
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
		constraints = comm.fetchConstraints(diagram, this.name);
		Collections.sort(constraints);
		
	}

	public void fetchDataValues(FmmlxDiagramCommunicator comm) throws TimeOutException {
		slots = comm.fetchSlots(diagram, this, this.getSlotNames());

		operationValues = comm.fetchOperationValues(diagram, this.name, this.getMonitoredOperationsNames());
	}

	@Override
	public ObjectContextMenu getContextMenu(FmmlxDiagram fmmlxDiagram, Point2D absolutePoint) {
		Point2D relativePoint = new Point2D(absolutePoint.getX() - getX(), absolutePoint.getY() - getY());
		return new ObjectContextMenu(this, fmmlxDiagram, relativePoint);
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
			for (FmmlxOperation operation : ancestor.getAllOperations()) {
				if (operation.getLevel() == this.level && operation.isMonitored() && !monitorNames.contains(operation.getName())) {
					monitorNames.add(operation.getName());
				}
			}
		}
		return monitorNames;
	}

	private Vector<FmmlxObject> getAllAncestors() {
		if("Root::XCore::Class".equals(ownPath)) return new Vector<FmmlxObject>();
		Vector<FmmlxObject> result1 = new Vector<>();
		if (ofPath != null) {
			FmmlxObject of = diagram.getObjectByPath(getOfPath());
			if(of!=null){
				result1.add(of);
			}
		}
		for (String p : getParentsPaths()) {
			FmmlxObject parent = diagram.getObjectByPath(p);

			if(parent!=null){
				result1.add(parent);
			}
		}
		Vector<FmmlxObject> result2 = new Vector<>(result1);
		for (FmmlxObject o : result1) if (o.level != -1) {
			result2.addAll(o.getAllAncestors());
		}
		return result2;
	}

	@Override
	public PropertyType getPropertyType() {
		return propertyType;
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


	
	public FmmlxProperty handlePressedOnNodeElement(Point2D relativePoint, FmmlxDiagram diagram) {
		if(relativePoint == null) return null;
		if(!diagram.isSelected(this)) {
			lastClick = null; return null;
 		}
		lastClick = relativePoint;
		NodeBaseElement hitLabel = getHitLabel(relativePoint);
		if (hitLabel != null) {
			if (hitLabel.getActionObject().getPropertyType() != PropertyType.Class) {
				hitLabel.setSelected();
				return hitLabel.getActionObject();
			}
		}
		return null;
	}

	public NodeBaseElement getHitLabel(Point2D relativePoint) {
		NodeBaseElement hitLabel = null;
		for(NodeElement e : nodeElements) if(hitLabel == null) {
			 hitLabel =  e.getHitLabel(relativePoint);//new Point2D(relativePoint.getX() - e.getX(), relativePoint.getY() - e.getY()));
		}
		return hitLabel;
	}

	public void performDoubleClickAction(Point2D p) {
		if(p == null) return;
		NodeBaseElement hitLabel = getHitLabel(p);
		if(hitLabel != null) hitLabel.performDoubleClickAction();
	}

	public PaletteItem toPaletteItem(FmmlxDiagram fmmlxDiagram) {
		PaletteTool tool = new ToolClass(fmmlxDiagram, getName(), ownPath+"", getLevel(), isAbstract, "");
		return new PaletteItem(tool);
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

	public List<String> getAllAttributesString() {
		List<String> result = new LinkedList<>();
		
		for(FmmlxAttribute att : getAllAttributes()) {
			result.add("("+att.getLevel()+") "+att.getName());
		}
		return result;
	}

	public FmmlxAttribute getAttributeByName(String name){
		for (FmmlxAttribute att : getAllAttributes()){
			if (att.getName().equals(name)){
				return att;
			}
		}
		return null;
	}

	public List<String> getAllOperationsString() {
		List<String> result = new LinkedList<>();
		
		for(FmmlxOperation op : getAllOperations()) {
			StringBuilder stringBuilderType = new StringBuilder();
			int paramLength= op.getParamTypes().size();		
			for(int i =0 ; i<paramLength;i++) {			
				stringBuilderType.append(op.getParamTypes().get(i).split("::")[2]);
				if(i!=paramLength-1) {
					stringBuilderType.append(", ");
				}
			}
			
			result.add(op.getName()+" ("+stringBuilderType.toString()+")");
		}
		return result;
	}

	public List<String> getAllRelatedAssociationsString() {
		List<String> result = new LinkedList<>();
		
		for(FmmlxAssociation as : getAllRelatedAssociations()) {
			result.add(as.getName());
		}
		return result;
	}

	public FmmlxOperation getOperationByName(String newValue) {		
		for (FmmlxOperation op : getAllOperations()) {
			if(op.getName().equals(newValue)) {
				return op;
			}
		}
		return null;
	}

	public List<String> getAllSlotString() {
		List<String> result = new LinkedList<>();
		
		for(FmmlxSlot slot : slots) {
			result.add(slot.getName()+" = "+slot.getValue());
		}
		return result;
	}
	
	public Vector<FmmlxSlot> getAllSlots(){
		return new Vector<FmmlxSlot> (slots);
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
		FmmlxObject of = diagram.getObjectByPath(ofPath);
		if (of==null) {
			if ("Root::FMML::MetaClass".equals(ofPath)) {
			return "MetaClass";
			} return ofPath;
		} else {
			return of.name;
		}
	}
	
	public String getIsAbstract() {
		return Boolean.toString(isAbstract);
	}

	public Vector<Constraint> getConstraints() {
		return new Vector<Constraint>(constraints);
	}
	
	
}
