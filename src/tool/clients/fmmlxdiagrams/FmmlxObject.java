package tool.clients.fmmlxdiagrams;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.menus.ObjectContextMenu;
import tool.clients.fmmlxdiagrams.newpalette.PaletteItem;
import tool.clients.fmmlxdiagrams.newpalette.PaletteTool;
import tool.clients.fmmlxdiagrams.newpalette.ToolClass;

import java.util.*;

public class FmmlxObject implements CanvasElement, FmmlxProperty, Comparable<FmmlxObject> {

//	public static HashMap<Integer, Paint> colors = null;
	private String name;
	
	//@Deprecated int id;
	//@Deprecated private Integer of;
	//@Deprecated private Vector<Integer> parents;
	
	private String ownPath;
	private String ofPath;
	private Vector<String> parentsPaths;
	
	private double x;
	private double y;
	private boolean hidden;
	private boolean isAbstract;
	int level;
	private int width;
	private int height;
	Object highlightedElement;
    
	private final static NodeLabel.Action NO_ACTION = () -> {};
	
	private transient double mouseMoveOffsetX;
	private transient double mouseMoveOffsetY;
	
	private transient Point2D lastClick = null;
	
	private FmmlxObjectPort ports;

	boolean usePreferredWidth = false; //not implemented yet

	int preferredWidth = 0;
	int minWidth = 100;

	private boolean showOperations = true;
	private boolean showOperationValues = true;
	private boolean showSlots = true;
	private boolean showGettersAndSetters = true;
	private boolean showDerivedOperations = true;
	private boolean showDerivedAttributes = true;

	static int testDiff = 10;

	static int GAP = 5;

	final int INST_LEVEL_WIDTH = 7;
	final int MIN_BOX_HEIGHT = 4;
	final int EXTRA_Y_PER_LINE = 3;
	private Vector<NodeElement> nodeElements = new Vector<>();

	Vector<FmmlxSlot> slots = new Vector<>();
	Vector<FmmlxOperationValue> operationValues = new Vector<>();

	private Vector<FmmlxAttribute> ownAttributes = new Vector<>();
	private Vector<FmmlxAttribute> otherAttributes = new Vector<>();

	private Vector<FmmlxOperation> ownOperations = new Vector<>();
	private Vector<FmmlxOperation> otherOperations = new Vector<>();

	private FmmlxDiagram diagram;
	private PropertyType propertyType = PropertyType.Class;
	private transient boolean requiresReLayout;
		
	public void triggerLayout() {
		this.requiresReLayout = true;
	}

	public FmmlxObject(
			@Deprecated Integer _id, 
			String name, 
			int level, 
			@Deprecated Integer _of, 
			@Deprecated Vector<Integer> _parents, 
			String ownPath,
			String ofPath,
			Vector<String> parentPaths,
			Boolean isAbstract,
			Integer lastKnownX, Integer lastKnownY, Boolean hidden,
//			Integer delegatesTo, Integer roleFiller, 
			FmmlxDiagram diagram) {
		this.name = name;
		this.diagram = diagram;
		if (lastKnownX != null && lastKnownX != 0) {
			x = lastKnownX;
		} else {
			x = testDiff;
			testDiff += 150;
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

		
//		this.delegatesTo = delegatesTo;
//		this.roleFiller = roleFiller;

		this.showOperations = diagram.isShowOperations();
		this.showOperationValues = diagram.isShowOperationValues();
		this.showSlots = diagram.isShowSlots();
		this.showDerivedOperations = diagram.isShowDerivedOperations();
		this.showDerivedAttributes = diagram.isShowDerivedAttributes();
		this.showGettersAndSetters = diagram.isShowGetterAndSetter();
		
		this.ports = new FmmlxObjectPort(this);
	}

	private String getParentsList(FmmlxDiagram diagram) {
		StringBuilder parentsList = new StringBuilder("extends ");
		for (String parentName : getParentsPaths()) {
			try {
				FmmlxObject parent = diagram.getObjectByName(parentName);
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
	public double getX() { return x; }
	public double getY() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public double getCenterX() { return x + width / 2.; }
	public double getCenterY() { return y + height / 2.; }
	public double getRightX() { return x + width; }
	public double getBottomY() { return y + height; }
//	@Deprecated private int getOf() { return of; }
//
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

	public Vector<FmmlxOperation> getAllOperations() {
		Vector<FmmlxOperation> result = new Vector<>();
		result.addAll(ownOperations);
		result.addAll(otherOperations);
		return result;
	}

	public Vector<String> getParentsPaths() {
		Vector<String> parentsName = new Vector<>();
		for(String i : parentsPaths){
			FmmlxObject o = diagram.getObjectByName(i);
			parentsName.add(o.getName());
		}
		return parentsName;
	}

	public Vector<FmmlxObject> getInstances() {
		Vector<FmmlxObject> result = new Vector<>();
		for (FmmlxObject object : diagram.getObjects()) {
			if (object.getOfPath().equals(this.getName())) {
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

	public Paint getLevelBackgroundColor() {
		return diagram.levelColorScheme.getLevelBgColor(level);
		//return colors.containsKey(level) ? colors.get(level) : Color.valueOf("#ffaa00");
	}

	public Color getLevelFontColor(double opacity) {
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
		NodeBox header = new NodeBox(0, currentY, neededWidth, textHeight * headerLines + EXTRA_Y_PER_LINE, getLevelBackgroundColor(), Color.BLACK, (x) -> 1., PropertyType.Class);
		nodeElements.addElement(header);
		FmmlxObject ofObj = null;
		try {
			ofObj = diagram.getObjectByName(getOfPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String ofName = (ofObj == null) ? "MetaClass" : ofObj.name;
		
		NodeLabel metaclassLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight, getLevelFontColor(.65), null, this, NO_ACTION, "^" + ofName + "^", false) ;
		NodeLabel levelLabel = new NodeLabel(Pos.BASELINE_LEFT, 4, textHeight, getLevelFontColor(.65), null, this, NO_ACTION, "" + level, false);
		NodeLabel nameLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight * 2, getLevelFontColor(1.), null, this, NO_ACTION, name, isAbstract);
		header.nodeElements.add(metaclassLabel);
		header.nodeElements.add(levelLabel);
		header.nodeElements.add(nameLabel);

		if ((!"".equals(parentString))) {
			NodeLabel parentsLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight * 3, getLevelFontColor(1.), null, this, NO_ACTION, parentString, isAbstract);
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
				
				NodeLabel issueLabel = new NodeLabel(Pos.BASELINE_LEFT, IssueBox.BOX_SIZE * 1.5, issY, new Color(1., .8, 0., 1.), null, this, () -> i.performResolveAction(diagram), i.getText(), false);
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
			NodeLabel attLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, attY, Color.BLACK, null, att, changeAttNameAction, att.getName() + ":" + att.getTypeShort() +"["+ att.getMultiplicity() + "]" , false);
			attBox.nodeElements.add(attLabel);
			NodeLabel.Action changeAttLevelAction = () -> diagram.getActions().changeLevelDialog(this, PropertyType.Attribute);
			NodeLabel attLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, attY, Color.WHITE, Color.BLACK, att, changeAttLevelAction, att.level + "", false);
			attBox.nodeElements.add(attLevelLabel);
		}
		for (FmmlxAttribute att : otherAttributes) {
			if(showDerivedAttributes) {
			attY += lineHeight;
			NodeLabel attLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, attY, Color.GRAY, null, att, NO_ACTION, att.getName() + ":" + att.getTypeShort() +"["+ att.getMultiplicity() + "]" + " (from " + diagram.getObjectByName(att.owner).name + ")", false);
			attBox.nodeElements.add(attLabel);
			NodeLabel attLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, attY, Color.WHITE, Color.GRAY, att, NO_ACTION, att.level + "", false);
			attBox.nodeElements.add(attLevelLabel);
			}
		}
		currentY = yAfterAttBox;

		double yAfterOpsBox = currentY;

		int opsSize = countOperationsToBeShown();
//		double lineHeight = textHeight + EXTRA_Y_PER_LINE;
		double opsBoxHeight = Math.max(lineHeight * opsSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double opsY = 0;
		NodeBox opsBox = new NodeBox(0, currentY, neededWidth, opsBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.Operation);
		if (showOperations && opsSize > 0) {
			yAfterOpsBox = currentY + opsBoxHeight;
			nodeElements.addElement(opsBox);
			for (FmmlxOperation o : ownOperations) {
				if(showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
					opsY += lineHeight;
					NodeLabel.Action changeOpNameAction = () -> diagram.getActions().changeNameDialog(this, PropertyType.Operation, o);
					NodeLabel opLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, opsY, Color.BLACK, null, o, changeOpNameAction, o.getFullString(diagram), false);
					opsBox.nodeElements.add(opLabel);
					NodeLabel.Action changeOpLevelAction = () -> diagram.getActions().changeLevelDialog(this, PropertyType.Operation);
					NodeLabel opLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, opsY, Color.WHITE, Color.BLACK, o, changeOpLevelAction, o.getLevelString() + "", false);
					opsBox.nodeElements.add(opLevelLabel);
				}
			}
			for (FmmlxOperation o : otherOperations) {
				if(showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
				if(showDerivedOperations) {
				opsY += lineHeight;
				NodeLabel oLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, opsY, Color.GRAY, null, o, NO_ACTION, o.getFullString(diagram) + " (from " + diagram.getObjectByName(o.getOwner()).name + ")", false);
				opsBox.nodeElements.add(oLabel);
				NodeLabel oLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, opsY, Color.WHITE, Color.GRAY, o, NO_ACTION, o.getLevelString() + "", false);
				opsBox.nodeElements.add(oLevelLabel);
				}
				}
			}
		}
		currentY = yAfterOpsBox;

		double yAfterSlotBox = currentY;
		int slotSize = slots.size();
//		double lineHeight = textHeight + EXTRA_Y_PER_LINE;
		double slotBoxHeight = Math.max(lineHeight * slotSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double slotsY = 0;
		NodeBox slotsBox = new NodeBox(0, currentY, neededWidth, slotBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.Slot);
		if (showSlots && slotSize > 0) {
			yAfterSlotBox = currentY + slotBoxHeight;
			nodeElements.addElement(slotsBox);
			for (FmmlxSlot s : slots) {
				slotsY += lineHeight;
				NodeLabel.Action changeSlotValueAction = () -> diagram.getActions().changeSlotValue(this, s);
				NodeLabel slotNameLabel = new NodeLabel(Pos.BASELINE_LEFT, 3, slotsY, Color.BLACK, null, s, changeSlotValueAction, s.getName() + " = ", false);
				slotsBox.nodeElements.add(slotNameLabel);
				NodeLabel slotValueLabel = new NodeLabel(Pos.BASELINE_LEFT, 3 + slotNameLabel.getWidth(), slotsY, new Color(0.0,0.4,0.2,1.0), new Color(0.85,0.9,0.85,1.0), s, changeSlotValueAction, "" + s.getValue(), false);
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
				NodeLabel opvNameLabel = new NodeLabel(Pos.BASELINE_LEFT, 3, opvY, Color.BLACK, null, opv, NO_ACTION, opv.getName() + "()->", false);
				opvBox.nodeElements.add(opvNameLabel);
				NodeLabel opvValueLabel = new NodeLabel(Pos.BASELINE_LEFT, 5 + opvNameLabel.getWidth(), opvY, opv.isInRange()?Color.YELLOW:Color.RED, Color.BLACK, opv, NO_ACTION, "" + opv.getValue(), false);
				opvBox.nodeElements.add(opvValueLabel);
			}
		}
		currentY = yAfterOPVBox;

		NodeBox selectionBox = new NodeBox(0, 0, neededWidth, currentY, new Color(0, 0, 0, 0), Color.BLACK, (selected) -> selected?3:1, PropertyType.Selection);
		nodeElements.addElement(selectionBox);


		this.width = (int) neededWidth;
		this.height = (int) currentY;

		handlePressedOnNodeElement(lastClick);
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

		FmmlxObject of = diagram.getObjectByName(ofPath);
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
			neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(att.name + ":" + att.getTypeShort() +"["+ att.getMultiplicity() + "]" + " (from " + diagram.getObjectByName(att.owner).name + ")") + INST_LEVEL_WIDTH, neededWidth);
			}
		}
//		//determine maximal width of operations
		if (showOperations) {
			for (FmmlxOperation o : ownOperations) {
				if(showGettersAndSetters  ||  !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
				String text = o.getFullString(diagram);
				neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(text) + INST_LEVEL_WIDTH, neededWidth);
				}
			}	
			for (FmmlxOperation o : otherOperations) {
				if(showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
				if(showDerivedOperations) {


				neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(o.getFullString(diagram) + " (from " + diagram.getObjectByName(o.getOwner()).name + ")") + INST_LEVEL_WIDTH, neededWidth);
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

		if (hasParents()) {
			neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(getParentsList(diagram)), neededWidth);
		}
//
//		//determine maximal width of operation values
//		if (showOperationValues) {
//			for (FmmlxOperationValue operationValue : operationValues) {
//				Text text = new Text(operationValue.name + " = " + operationValue.value);
//				neededWidth = Math.max(text.getLayoutBounds().getWidth(), neededWidth);
//			}
//		}


		//if minimum width is not reached just paint minimum
		return Math.max(neededWidth + 2 * GAP, minWidth);
	}

	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram diagram) {
		
		if(hidden) return;
		
		if(requiresReLayout) layout(diagram);

		boolean selected = diagram.isSelected(this);

		g.setFont(diagram.getFont());

		for (NodeElement e : nodeElements) {
			e.paintOn(g, x + xOffset, y + yOffset, diagram, selected);
		}
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
	}

	public void fetchDataValues(FmmlxDiagramCommunicator comm) throws TimeOutException {
		slots = comm.fetchSlots(diagram, this, this.getSlotNames());

		operationValues = comm.fetchOperationValues(diagram, this.name, this.getMonitoredOperationsNames());
	}

	public boolean isHit(double mouseX, double mouseY) {
		return
	        !hidden && 
			mouseX > x &&
			mouseY > y &&
			mouseX < x + width &&
			mouseY < y + height;
	}

	@Override
	public ObjectContextMenu getContextMenu(DiagramActions actions, Point2D absolutePoint) {
		Point2D relativePoint = new Point2D(absolutePoint.getX() - getX(), absolutePoint.getY() - getY());
		return new ObjectContextMenu(this, actions, relativePoint);
	}

	@Override
	public void moveTo(double x, double y, FmmlxDiagram diagram) {
		this.x = Math.max(x, 0.0);
		this.y = Math.max(y, 0.0);

//		for(Edge edge : diagram.getEdges()) {
//			if (edge.isStartNode(this)) edge.moveStartPoint(x + width/2, y + height/2);
//			if (edge.isEndNode(this)) edge.moveEndPoint(x + width/2, y + height/2);
//		}
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
		Vector<FmmlxObject> result1 = new Vector<>();
		if (ofPath != null) {
			FmmlxObject of = diagram.getObjectByName(getOfPath());
			if(of!=null){
				result1.add(of);
			}
		}
		for (String p : getParentsPaths()) {
			FmmlxObject parent = diagram.getObjectByName(p);

			if(parent!=null){
				result1.add(parent);
			}
		}
		Vector<FmmlxObject> result2 = new Vector<>(result1);
		for (FmmlxObject o : result1) {
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

	@Override
	public void highlightElementAt(Point2D p) {
//		if(p == null) highlightedElement = null; else {
//			double X = p.getX() - this.x;
//			double Y = p.getY() - this.y;
//			for (NodeElement e : nodeElements) {
//				e.isHit(mouseX, mouseY)
//			}
//		}
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

	@Override
	public void setOffsetAndStoreLastValidPosition(Point2D p) {
		mouseMoveOffsetX = p.getX() - x;
		mouseMoveOffsetY = p.getY() - y;
//		lastValidX = x;
//		lastValidY = y;
	}

	@Override public double getMouseMoveOffsetX() {return mouseMoveOffsetX;}
	@Override public double getMouseMoveOffsetY() {return mouseMoveOffsetY;}

	public Point2D getPointForEdge(Edge.End edge, boolean isStartNode) {
		return ports.getPointForEdge(edge, isStartNode);
	}

	public PortRegion getDirectionForEdge(Edge.End edge, boolean isStartNode) {
		return ports.getDirectionForEdge(edge, isStartNode);
	}
	
	public void setDirectionForEdge(Edge.End edge, boolean isStartNode, PortRegion newPortRegion) {
		ports.setDirectionForEdge(edge, isStartNode, newPortRegion);
	}
	
	public void addEdgeEnd(Edge.End edge, PortRegion direction) {
		ports.addNewEdge(edge, direction);
	}

	public void updatePortOder() {
		ports.sortAllPorts();
	}

	@Override
	public void unHighlight() {	}
	
	public FmmlxProperty handlePressedOnNodeElement(Point2D relativePoint) {
		if(relativePoint == null) return null;
		if(!diagram.isSelected(this)) {
			lastClick = null; return null;
 		}
		lastClick = relativePoint;
		NodeLabel hitLabel = getHitLabel(relativePoint);
		if (hitLabel != null) {
			if (hitLabel.getActionObject().getPropertyType() != PropertyType.Class) {
				hitLabel.setSelected();
				return hitLabel.getActionObject();
			}
		}
		return null;
	}

	public NodeLabel getHitLabel(Point2D relativePoint) {
		NodeLabel hitLabel = null;
		for(NodeElement e : nodeElements) if(hitLabel == null) {
			 hitLabel =  e.getHitLabel(relativePoint);//new Point2D(relativePoint.getX() - e.getX(), relativePoint.getY() - e.getY()));
		}
		return hitLabel;
	}

	public void performDoubleClickAction(Point2D p) {
		if(p == null) return;
		NodeLabel hitLabel = getHitLabel(p);
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

	@Override
	public String toString() {
		return name;
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

	public boolean isAttributeExists(String name) {
		for(FmmlxAttribute attribute : getAllAttributes()){
			if(attribute.getName().equals(name)){
				return true;
			}
		}
		return false;
	}

	public boolean operationIsExists(String name) {
		for(FmmlxOperation operation : getAllOperations()){
			if(operation.getName().equals(name)){
				return true;
			}
		}
		return false;
	}

	public String getPath() {
		return ownPath;
	}

	public boolean isHidden() {
		return hidden;
	}

	public String getOwnPath() {
		return ownPath ;
	}
}
