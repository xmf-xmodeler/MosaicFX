package tool.clients.fmmlxdiagrams;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.menus.ObjectContextMenu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class FmmlxObject implements CanvasElement, FmmlxProperty {

	//	private String[] levelBackgroundColors = {"#8C8C8C", "#FFFFFF", "#000000", "#3111DB", "#dd2244", "#119955"};
	private static HashMap<Integer, Paint> colors = null;
	private String name;
	int id;
	private double x;
	private double y;
	private boolean isAbstract;
	int level;
	Integer of;
	private Vector<Integer> parents;
	private int width;
	private int height;
	Object highlightedElement;

	private transient double mouseMoveOffsetX;
	private transient double mouseMoveOffsetY;
	private transient double lastValidX;
	private transient double lastValidY;
	
	boolean usePreferredWidth = false; //not implemented yet

	int preferredWidth = 0;
	int minWidth = 100;

	private boolean showOperations = true;
	private boolean showOperationValues = true;
	private boolean showSlots = true;

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

	static {
		colors = new HashMap<>();
//		private String[] levelBackgroundColors = {"#8C8C8C", "#FFFFFF", "#000000", "#3111DB", "#dd2244", "#119955"};
		colors.put(0, Color.valueOf("#8C8C8C"));
		colors.put(1, Color.valueOf("#FFFFFF"));
		colors.put(2, Color.valueOf("#000000"));
		colors.put(3, Color.valueOf("#3111DB"));
		colors.put(4, Color.valueOf("#BB1133"));
		colors.put(5, Color.valueOf("#119955"));
		colors.put(6, new LinearGradient(0, 0, 20, 10, false, CycleMethod.REPEAT,
				new Stop(.24, Color.valueOf("#22cc55")),
				new Stop(.26, Color.valueOf("#ffdd00")),
				new Stop(.74, Color.valueOf("#ffdd00")),
				new Stop(.76, Color.valueOf("#22cc55"))));
		colors.put(7, new LinearGradient(0, 0, 60, 25, false, CycleMethod.REPEAT,
				new Stop(0. / 6, Color.valueOf("#ff4444")),
				new Stop(0.8 / 6, Color.valueOf("#ffff00")),
				new Stop(1.2 / 6, Color.valueOf("#ffff00")),
				new Stop(2. / 6, Color.valueOf("#44ff44")),
				new Stop(2.8 / 6, Color.valueOf("#00ffff")),
				new Stop(3.2 / 6, Color.valueOf("#00ffff")),
				new Stop(4. / 6, Color.valueOf("#6666ff")),
				new Stop(4.8 / 6, Color.valueOf("#ff22ff")),
				new Stop(5.2 / 6, Color.valueOf("#ff22ff")),
				new Stop(6. / 6, Color.valueOf("#ff4444"))));
	}

	public FmmlxObject(Integer id, String name, int level, Integer of, Vector<Integer> parents, Boolean isAbstract, Integer lastKnownX, Integer lastKnownY, FmmlxDiagram diagram) {
		this.name = name;
		this.id = id;
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

		width = 150;
		height = 80;
		this.level = level;
		this.isAbstract = isAbstract;
		this.of = of;
		this.parents = parents;
		
		this.showOperations = diagram.isShowOperations();
		this.showOperationValues = diagram.isShowOperationValues();
		this.showSlots = diagram.isShowSlots();
	}

	private String getParentsListString(FmmlxDiagram diagram) {
		String parentsList = "extends ";
		for (Integer parentID : getParents()) {
			String parentName;
			try {
				parentName = diagram.getObjectById(parentID).name;
			} catch (Exception e) {
				parentName = e.getMessage();
			}
			parentsList += parentName + ", ";
		}
		return parentsList.substring(0, parentsList.length() - 2);
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public int getId() {
		return id;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getRightBorder() {
		return y + width;
	}

	public double getBottomBorder() {
		return x + height;
	}

	public Point2D getBottomRightPoint() {
		return new Point2D(getX() + height, y + width);
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getOf() {
		return of;
	}

	public Vector<FmmlxAttribute> getOwnAttributes() {
		return new Vector<FmmlxAttribute>(ownAttributes);
	}

	public Vector<FmmlxAttribute> getOtherAttributes() {
		return new Vector<FmmlxAttribute>(otherAttributes);
	}

	public Vector<FmmlxAttribute> getAllAttributes() {
		Vector<FmmlxAttribute> result = new Vector<FmmlxAttribute>();
		result.addAll(ownAttributes);
		result.addAll(otherAttributes);
		return result;
	}

	public Vector<FmmlxAssociation> getAllRelatedAssociations() {
		Vector<FmmlxAssociation> result = new Vector<FmmlxAssociation>();
		result = diagram.getRelatedAssociationByObject(this);
		return result;
	}

	public ObservableList<FmmlxAttribute> getAllAttributesAsList() {
		ObservableList<FmmlxAttribute> result = FXCollections.observableArrayList(getAllAttributes());
		return result;
	}

	public Vector<FmmlxOperation> getOwnOperations() {
		return ownOperations;
	}

	public Vector<FmmlxOperation> getOtherOperations() {
		return otherOperations;
	}

	private Vector<FmmlxOperation> getAllOperations() {
		Vector<FmmlxOperation> result = new Vector<FmmlxOperation>();
		result.addAll(ownOperations);
		result.addAll(otherOperations);
		return result;
	}


	public Vector<Integer> getParents() {
		return parents;
	}

	public Vector<FmmlxObject> getInstances() {
		Vector<FmmlxObject> result = new Vector<>();
		for (FmmlxObject object : diagram.getObjects()) {
			if (object.getOf() == this.getId()) {
				result.add(object);
			}
		}
		return result;
	}

	public Vector<FmmlxObject> getInstancesByLevel(Integer level) {
		Vector<FmmlxObject> result = new Vector<FmmlxObject>();
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


	public void setParents(Vector<Integer> parents) {
		this.parents = parents;
	}

	public double getMaxBottom() {
		return y + height;
	}

	public double getCenterX() {
		return x + width / 2;
	}

	public double getCenterY() {
		return y + height / 2;
	}

	public double getMaxRight() {
		return x + width;
	}

	public Paint getLevelBackgroundColor() {
		return colors.containsKey(level) ? colors.get(level) : Color.valueOf("#ffaa00");
	}

	public String getLevelFontColor() {
		return new Vector<Integer>(Arrays.asList(2, 3, 4)).contains(level) ? "#ffffff" : "000000";
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

	public Vector<NodeElement> getNodes() {
		return nodeElements;
	}

	public void setShowOperations(boolean show) {
		requiresReLayout = showOperations!=show;
		showOperations = show;
	}

	public void setShowOperationValues(boolean show) {
		requiresReLayout = showOperationValues!=show;
		showOperationValues = show;
	}

	public void setShowSlots(boolean show) {
		requiresReLayout = showSlots!=show;
		showSlots = show;
	}

	public void layout(FmmlxDiagram diagram) {
		requiresReLayout = false;
		
		nodeElements = new Vector<>();
//		double neededHeight = 0;
		double neededWidth = calculateNeededWidth(diagram);
		//determine text height
		double textHeight = diagram.calculateTextHeight();
		double currentY = 0;

		int headerLines = hasParents() ? 3 : 2;
		NodeBox header = new NodeBox(0, currentY, neededWidth, textHeight * headerLines, getLevelBackgroundColor(), Color.BLACK, (x) -> {return 1.;}, PropertyType.Class);
		nodeElements.addElement(header);
		String ofName;
		try {
			ofName = diagram.getObjectById(of).name;
		} catch (Exception e) {
			ofName = e.getMessage();
		}
		if (ofName != null) {
//			ofName = "^" + ofName + "^";
		} else {
			ofName = "MetaClass";
		}
		NodeLabel metaclassLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight, Color.valueOf(getLevelFontColor() + "75"), null, this, "^" + ofName + "^");
		NodeLabel levelLabel = new NodeLabel(Pos.BASELINE_LEFT, 4, textHeight, Color.valueOf(getLevelFontColor() + "75"), null, this, "" + level);
		NodeLabel nameLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight * 2, Color.valueOf(getLevelFontColor()), null, this, name, isAbstract);
		header.nodeElements.add(metaclassLabel);
		header.nodeElements.add(levelLabel);
		header.nodeElements.add(nameLabel);

		if (hasParents()) {
			NodeLabel parentsLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight * 3, Color.valueOf(getLevelFontColor()), null, this, getParentsListString(diagram), isAbstract);
			header.nodeElements.add(parentsLabel);
		}

		currentY += headerLines * textHeight;

		double lineHeight = textHeight + EXTRA_Y_PER_LINE;

		int attSize = ownAttributes.size() + otherAttributes.size();
		double attBoxHeight = Math.max(lineHeight * attSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double yAfterAttBox = currentY + attBoxHeight;
		double attY = 0;
		NodeBox attBox = new NodeBox(0, currentY, neededWidth, attBoxHeight, Color.WHITE, Color.BLACK, (x) -> {return 1.;}, PropertyType.Attribute);
		nodeElements.addElement(attBox);

		for (FmmlxAttribute att : ownAttributes) {
			attY += lineHeight;
			NodeLabel attLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, attY, Color.BLACK, null, att, att.getName() + ":" + att.type);
			attBox.nodeElements.add(attLabel);
			NodeLabel attLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, attY, Color.WHITE, Color.BLACK, att, att.level + "");
			attBox.nodeElements.add(attLevelLabel);
		}
		for (FmmlxAttribute att : otherAttributes) {
			attY += lineHeight;
			NodeLabel attLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, attY, Color.GRAY, null, att, att.getName() + ":" + att.type + " (from " + diagram.getObjectById(att.owner).name + ")");
			attBox.nodeElements.add(attLabel);
			NodeLabel attLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, attY, Color.WHITE, Color.GRAY, att, att.level + "");
			attBox.nodeElements.add(attLevelLabel);
		}
		currentY = yAfterAttBox;

		double yAfterOpsBox = currentY;

		int opsSize = ownOperations.size() + otherOperations.size();
//		double lineHeight = textHeight + EXTRA_Y_PER_LINE;
		double opsBoxHeight = Math.max(lineHeight * opsSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double opsY = 0;
		NodeBox opsBox = new NodeBox(0, currentY, neededWidth, opsBoxHeight, Color.WHITE, Color.BLACK, (x) -> {return 1.;}, PropertyType.Operation);
		if (showOperations && opsSize > 0) {
			yAfterOpsBox = currentY + opsBoxHeight;
			nodeElements.addElement(opsBox);
			for (FmmlxOperation o : ownOperations) {
				opsY += lineHeight;
				NodeLabel attLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, opsY, Color.BLACK, null, o, o.getName() + "():" + o.getType());
				opsBox.nodeElements.add(attLabel);
				NodeLabel attLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, opsY, Color.WHITE, Color.BLACK, o, o.getLevelString() + "");
				opsBox.nodeElements.add(attLevelLabel);
			}
			for (FmmlxOperation o : otherOperations) {
				opsY += lineHeight;
				NodeLabel oLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, opsY, Color.GRAY, null, o, o.getName() + "():" + o.getType() + " (from " + diagram.getObjectById(o.getOwner()).name + ")");
				opsBox.nodeElements.add(oLabel);
				NodeLabel oLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, opsY, Color.WHITE, Color.GRAY, o, o.getLevelString() + "");
				opsBox.nodeElements.add(oLevelLabel);
			}
		}
		currentY = yAfterOpsBox;

		double yAfterSlotBox = currentY;
		int slotSize = slots.size();
//		double lineHeight = textHeight + EXTRA_Y_PER_LINE;
		double slotBoxHeight = Math.max(lineHeight * slotSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double slotsY = 0;
		NodeBox slotsBox = new NodeBox(0, currentY, neededWidth, slotBoxHeight, Color.WHITE, Color.BLACK, (x) -> {return 1.;}, PropertyType.Slot);
		if (showSlots && slotSize > 0) {
			yAfterSlotBox = currentY + slotBoxHeight;
			nodeElements.addElement(slotsBox);
			for (FmmlxSlot s : slots) {
				slotsY += lineHeight;
				NodeLabel slotLabel = new NodeLabel(Pos.BASELINE_LEFT, 3, slotsY, Color.BLACK, null, s, s.getName() + " = " + s.getValue());
				slotsBox.nodeElements.add(slotLabel);
			}
		}
		currentY = yAfterSlotBox;

		double yAfterOPVBox = currentY;
		int opvSize = operationValues.size();
//		double lineHeight = textHeight + EXTRA_Y_PER_LINE;
		double opvBoxHeight = Math.max(lineHeight * opvSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double opvY = 0;
		NodeBox opvBox = new NodeBox(0, currentY, neededWidth, opvBoxHeight, Color.WHITE, Color.BLACK, (x) -> {return 1.;}, PropertyType.OperationValue);
		if (showOperationValues && opvSize > 0) {
			yAfterOPVBox = currentY + opvBoxHeight;
			nodeElements.addElement(opvBox);
			for (FmmlxOperationValue opv : operationValues) {
				opvY += lineHeight;
				NodeLabel opvLabel = new NodeLabel(Pos.BASELINE_LEFT, 3, opvY, Color.BLACK, null, opv, opv.getName() + " = " + opv.getValue());
				opvBox.nodeElements.add(opvLabel);
			}
		}
		currentY = yAfterOPVBox;

		NodeBox selectionBox = new NodeBox(0, 0, neededWidth, currentY, new Color(0, 0, 0, 0), Color.BLACK, (selected) -> {return selected?3:1;}, PropertyType.Selection);
		nodeElements.addElement(selectionBox);


		this.width = (int) neededWidth;
		this.height = (int) currentY;
	}

	private boolean hasParents() {
		return getParents().size() != 0;
	}

	private double calculateNeededWidth(FmmlxDiagram diagram) {
		double neededWidth = diagram.calculateTextWidth(name);

		if (of >= 0) {
			neededWidth = Math.max(neededWidth, diagram.calculateTextWidth(getLevel() + "^" + diagram.getObjectById(of).name + "^") + 16);
		} else {
			neededWidth = Math.max(neededWidth, diagram.calculateTextWidth(getLevel() + "^MetaClass^") + 16);
		}

		//determine maximal width of attributes
		for (FmmlxAttribute att : ownAttributes) {
			neededWidth = Math.max(diagram.calculateTextWidth(att.name + ":" + att.type) + INST_LEVEL_WIDTH, neededWidth);
		}
		for (FmmlxAttribute att : otherAttributes) {
			neededWidth = Math.max(diagram.calculateTextWidth(att.name + ":" + att.type + " (from " + diagram.getObjectById(att.owner).name + ")") + INST_LEVEL_WIDTH, neededWidth);
		}
//		//determine maximal width of operations
		if (showOperations) {
			for (FmmlxOperation o : ownOperations) {
				String text = o.name + "():" + o.type;
				neededWidth = Math.max(diagram.calculateTextWidth(text) + INST_LEVEL_WIDTH, neededWidth);
			}
			for (FmmlxOperation o : otherOperations) {
				neededWidth = Math.max(diagram.calculateTextWidth(o.name + "():" + o.type + " (from " + diagram.getObjectById(o.owner).name + ")") + INST_LEVEL_WIDTH, neededWidth);
			}
		}
		//determine maximal width of slots
		if (showSlots && slots.size() > 0) {
			for (FmmlxSlot slot : slots) {
				neededWidth = Math.max(diagram.calculateTextWidth(slot.getName() + " = " + slot.getValue()), neededWidth);

			}
		}
		if (showOperationValues) {
			for (FmmlxOperationValue opValue : operationValues) {
				neededWidth = Math.max(diagram.calculateTextWidth(opValue.getName() + " = " + opValue.getValue()), neededWidth);
			}
		}

		if (hasParents()) {
			neededWidth = Math.max(diagram.calculateTextWidth(getParentsListString(diagram)), neededWidth);
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
		
		if(requiresReLayout) layout(diagram);

		boolean selected = diagram.isSelected(this);

		g.setFont(diagram.getFont());

		for (NodeElement e : nodeElements) {
			e.paintOn(g, x + xOffset, y + yOffset, diagram, selected);
		}
	}

	public void fetchDataDefinitions(FmmlxDiagramCommunicator comm) {
		Vector<Vector<FmmlxAttribute>> attributeList = comm.fetchAttributes(this.name);
		ownAttributes = attributeList.get(0);
		otherAttributes = attributeList.get(1);
		Vector<FmmlxOperation> operations = comm.fetchOperations(this.name);
		ownOperations = new Vector<FmmlxOperation>();
		otherOperations = new Vector<FmmlxOperation>();
		for (FmmlxOperation o : operations) {
			if (o.owner == this.id) {
				ownOperations.add(o);
			} else {
				otherOperations.add(o);
			}
		}
	}

	public void fetchDataValues(FmmlxDiagramCommunicator comm) {
		slots = comm.fetchSlots(this.name, this.getSlotNames());

		operationValues = comm.fetchOperationValues(this.name, this.getMonitoredOperationsNames());
	}

	public boolean isHit(double mouseX, double mouseY) {
		return
			mouseX > x &&
			mouseY > y &&
			mouseX < x + width &&
			mouseY < y + height;
	}

	@Override
	public ObjectContextMenu getContextMenu(DiagramActions actions) {
		return new ObjectContextMenu(this, actions);
	}

	@Override
	public void moveTo(double x, double y, FmmlxDiagram diagram) {
	    this.x = x;
	    this.y = y;
		setX((int) x);
		setY((int) y);
		for(Edge edge : diagram.getEdges()) {
			if (edge.isStartNode(this)) edge.moveStartPoint(x + width/2, y + height/2);
			if (edge.isEndNode(this)) edge.moveEndPoint(x + width/2, y + height/2);
		}
	}
	
	public boolean isAbstract() {return isAbstract;}

	private Vector<String> getSlotNames() {
		Vector<String> slotNames = new Vector<String>();
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
		Vector<String> monitorNames = new Vector<String>();
		for (FmmlxObject ancestor : getAllAncestors()) {
			for (FmmlxOperation operation : ancestor.getAllOperations()) {
				if (operation.level == this.level && operation.isMonitored() && !monitorNames.contains(operation.name)) {
					monitorNames.add(operation.name);
				}
			}
		}
		return monitorNames;
	}

	private Vector<FmmlxObject> getAllAncestors() {
		Vector<FmmlxObject> result1 = new Vector<FmmlxObject>();
		Vector<FmmlxObject> result2 = new Vector<FmmlxObject>();
		if (of != null && of >= 0)
			result1.add(diagram.getObjectById(of));
		for (Integer p : parents)
			result1.add(diagram.getObjectById(p));
		result2.addAll(result1);
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
		boolean ok = false;
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
		lastValidX = x;
		lastValidY = y;
	}

	@Override public double getMouseMoveOffsetX() {return mouseMoveOffsetX;}
	@Override public double getMouseMoveOffsetY() {return mouseMoveOffsetY;}

}
