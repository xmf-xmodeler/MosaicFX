package tool.clients.fmmlxdiagrams;

import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import tool.clients.fmmlxdiagrams.menus.ObjectContextMenu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Vector;

public class FmmlxObject implements CanvasElement, Selectable {

	private String[] levelBackgroundColors = {"#8C8C8C", "#FFFFFF", "#000000", "#3111DB", "#dd2244", "#119955"};

	private String name;
	int id;
	private int x;
	private int y;
	int level;
	Integer of;
	Vector<Integer> parents;
	int width;
	int height;

	public transient double mouseMoveOffsetX;
	public transient double mouseMoveOffsetY;

	boolean usePreferredWidth = false; //not implemented yet

	int preferredWidth = 0;
	int minWidth = 100;

	boolean showOperations = true;
	boolean showOperationValues = true;
	boolean showSlots = true;

	static int testDiff = 10;

	static int gap = 5;

	Vector<FmmlxSlot> slots;
	@Deprecated
	Vector<FmmlxOperation> operations;
	Vector<FmmlxOperationValue> operationValues;

	Vector<FmmlxAttribute> ownAttributes;
	Vector<FmmlxAttribute> otherAttributes;
	Vector<FmmlxOperation> ownOperations = new Vector<>();
	Vector<FmmlxOperation> otherOperations = new Vector<>();

	private Font font;

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public int getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getRightBorder() {
		return y + width;
	}

	public int getBottomBorder() {
		return x + height;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Vector<FmmlxAttribute> getOwnAttributes() {
		return ownAttributes;
	}

	public Vector<FmmlxAttribute> getOtherAttributes() {
		return otherAttributes;
	}

	//
//	public void setAttributes(Vector<FmmlxAttribute> attributes) {
//		this.attributes = attributes;
//	}

	public FmmlxObject(Integer id, String name, int level, Integer of, Vector<Integer> parents, Integer lastKnownX, Integer lastKnownY) {
		this.name = name;
		this.id = id;
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
		this.of = of;
		this.parents = parents;

		try {
			font = Font.loadFont(new FileInputStream("resources/fonts/DejaVuSansMono.ttf"), 14);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	final int INST_LEVEL_WIDTH = 7;
	final int MIN_BOX_HEIGHT = 4;
	final int EXTRA_Y_PER_LINE = 3;
	Vector<NodeElement> nodeElements = new Vector<>();

	private void layout(FmmlxDiagram diagram) {

		nodeElements = new Vector<>();
//		double neededHeight = 0;
		double neededWidth = calculateNeededWidth(diagram);

		//determine text height
		double textHeight = calculateTextHeight();
		double currentY = 0;

		NodeBox header = new NodeBox(0, currentY, neededWidth, textHeight * 2, Color.valueOf(getLevelBackgroundColor()), Color.BLACK);
		nodeElements.addElement(header);
		String ofName = "ClassNotFound";
		try {
			ofName = diagram.getObjectById(of).name;
		} catch (Exception e) {
			ofName = e.getMessage();
		}
		NodeLabel metaclassLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight, Color.valueOf(getLevelFontColor() + "75"), null, this, "^" + ofName + "^");
		NodeLabel levelLabel = new NodeLabel(Pos.BASELINE_LEFT, 4, textHeight, Color.valueOf(getLevelFontColor() + "75"), null, this, "" + level);
		NodeLabel nameLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight * 2, Color.valueOf(getLevelFontColor()), null, this, name);
		header.nodeElements.add(metaclassLabel);
		header.nodeElements.add(levelLabel);
		header.nodeElements.add(nameLabel);
		currentY += 2 * textHeight;

		double lineHeight = textHeight + EXTRA_Y_PER_LINE;

		int attSize = ownAttributes.size() + otherAttributes.size();
		double attBoxHeight = Math.max(lineHeight * attSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double yAfterAttBox = currentY + attBoxHeight;
		double attY = 0;
		NodeBox attBox = new NodeBox(0, currentY, neededWidth, attBoxHeight, Color.WHITE, Color.BLACK);
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

		int opsSize = ownOperations.size() + otherOperations.size();
//		double lineHeight = textHeight + EXTRA_Y_PER_LINE;
		double opsBoxHeight = Math.max(lineHeight * opsSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double yAfterOpsBox = currentY + opsBoxHeight;
		double opsY = 0;
		NodeBox opsBox = new NodeBox(0, currentY, neededWidth, opsBoxHeight, Color.WHITE, Color.BLACK);
		nodeElements.addElement(opsBox);

		for (FmmlxOperation o : ownOperations) {
			opsY += lineHeight;
			NodeLabel attLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, opsY, Color.BLACK, null, o, o.getName() + "():" + o.getType());
			opsBox.nodeElements.add(attLabel);
			NodeLabel attLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, opsY, Color.WHITE, Color.BLACK, o, o.getLevel() + "");
			opsBox.nodeElements.add(attLevelLabel);
		}
		for (FmmlxOperation o : otherOperations) {
			opsY += lineHeight;
			NodeLabel oLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, opsY, Color.GRAY, null, o, o.getName() + ":" + o.getType() + " (from " + diagram.getObjectById(o.getOwner()).name + ")");
			opsBox.nodeElements.add(oLabel);
			NodeLabel oLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, opsY, Color.WHITE, Color.GRAY, o, o.getLevel()+"");
			opsBox.nodeElements.add(oLevelLabel);
		}		
		
		currentY = yAfterOpsBox;

		this.width = (int) neededWidth;
		this.height = (int) currentY;
	}

	private double calculateTextHeight() {
		Text t = new Text("TestText");
		t.setFont(font);
		return t.getLayoutBounds().getHeight();
	}

	private double calculateTextWidth(String text) {
		Text t = new Text(text);
		t.setFont(font);
		return t.getLayoutBounds().getWidth();
	}

	private double calculateNeededWidth(FmmlxDiagram diagram) {
		double neededWidth = 0;

		//determine maximal width of attributes
		for (FmmlxAttribute att : ownAttributes) {
			Text text = new Text(att.name + ":" + att.type);
			neededWidth = Math.max(text.getLayoutBounds().getWidth() + INST_LEVEL_WIDTH, neededWidth);
		}
		for (FmmlxAttribute att : otherAttributes) {
			Text text = new Text(att.name + ":" + att.type + " (from " + diagram.getObjectById(att.owner).name + ")");
			neededWidth = Math.max(text.getLayoutBounds().getWidth() + INST_LEVEL_WIDTH, neededWidth);
		}
//		//determine maximal width of operations
//		if (showOperations) {
		for (FmmlxOperation o : ownOperations) {
			Text text = new Text(o.name + ":" + o.type);
			neededWidth = Math.max(text.getLayoutBounds().getWidth() + INST_LEVEL_WIDTH, neededWidth);
		}
		for (FmmlxOperation o : otherOperations) {
			Text text = new Text(o.name + ":" + o.type + " (from " + diagram.getObjectById(o.owner).name + ")");
			neededWidth = Math.max(text.getLayoutBounds().getWidth() + INST_LEVEL_WIDTH, neededWidth);
		}
//		}
//		//determine maximal width of slots
//		if (showSlots) {
//			for (FmmlxSlot slot : slots) {
//				Text text = new Text(slot.name + " = " + slot.value);
//				neededWidth = Math.max(text.getLayoutBounds().getWidth(), neededWidth);
//			}
//		}
//
//		//determine maximal width of operation values
//		if (showOperationValues) {
//			for (FmmlxOperationValue operationValue : operationValues) {
//				Text text = new Text(operationValue.name + " = " + operationValue.value);
//				neededWidth = Math.max(text.getLayoutBounds().getWidth(), neededWidth);
//			}
//		}

		//if minimum width is not reached just paint minimum
		return Math.max(neededWidth + 2 * gap, minWidth);
	}

	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram diagram) {

		boolean selected = diagram.isSelected(this);
		layout(diagram);
		g.setFont(font);

		for (NodeElement e : nodeElements) {
			e.paintOn(g, x + xOffset, y + yOffset, diagram);
		}

//		g.setStroke(selected ? Color.GREEN : Color.BLACK);
//
//		double calculatedHeight = 0;
//		double calculatedWidth = 0;
//
//		//determine attributes to paint
//		Vector<FmmlxAttribute> ownAttributesToPaint = new Vector<FmmlxAttribute>();
//		Vector<FmmlxAttribute> otherAttributesToPaint = new Vector<FmmlxAttribute>();
//
//		for (FmmlxAttribute att : ownAttributes) {
//			if (passReqs(att)) {
//				ownAttributesToPaint.add(att);
//				// determine maximal width
//				Text text = new Text("[" + att.level + "] " + att.name + ":" + att.type);
//				calculatedWidth = Math.max(text.getLayoutBounds().getWidth(), calculatedWidth);
//			}
//		}
//		
//		for (FmmlxAttribute att : otherAttributes) {
//			if (passReqs(att)) {
//				otherAttributesToPaint.add(att);
//				// determine maximal width
//				Text text = new Text("[" + att.level + "] " + att.name + ":" + att.type);
//				calculatedWidth = Math.max(text.getLayoutBounds().getWidth(), calculatedWidth);
//			}
//		}
//
//		//determine maximal width of operations
//		if (showOperations) {
//			for (FmmlxOperation operation : operations) {
//				Text text = new Text(operation.name);
//				calculatedWidth = Math.max(text.getLayoutBounds().getWidth(), calculatedWidth);
//			}
//		}
//		//determine maximal width of slots
//		if (showSlots) {
//			for (FmmlxSlot slot : slots) {
//				Text text = new Text(slot.name + " = " + slot.value);
//				calculatedWidth = Math.max(text.getLayoutBounds().getWidth(), calculatedWidth);
//			}
//		}
//
//		//determine maximal width of operation values
//		if (showOperationValues) {
//			for (FmmlxOperationValue operationValue : operationValues) {
//				Text text = new Text(operationValue.name + " = " + operationValue.value);
//				calculatedWidth = Math.max(text.getLayoutBounds().getWidth(), calculatedWidth);
//			}
//		}
//
//		//if minimum width is not reached just paint minimum
//		calculatedWidth = Math.max(calculatedWidth + 2 * gap, minWidth);
//
//		//calculating header height
//		Text header = new Text("[" + level + "]" + name);
//		double headerheight = header.getLayoutBounds().getHeight() + 2 * gap;
//
//		//determine text height
//		double textheight = new Text("TextForLayout").getLayoutBounds().getHeight();
//
//		//calculate starting position for text
//		double Y = 0 + headerheight + 2 * gap;    // just a guess
//		calculatedHeight = Y + (ownAttributesToPaint.size() + otherAttributesToPaint.size() - 1) * (textheight + gap) + gap;
//
//		if (showOperations)
//			calculatedHeight += (operations.size() - 1) * (textheight + gap) + textheight + 2 * gap;
//
//		if (showOperationValues)
//			calculatedHeight += (operationValues.size() - 1) * (textheight + gap) + textheight + 2 * gap;
//
//		if (showSlots)
//			calculatedHeight += (slots.size() - 1) * (textheight + gap) + textheight + 2 * gap;
//
//		calculatedHeight += 10;// just a guess
//
//		Y += y;
//
//		//set background
//		g.setFill(Color.WHITE);
//		g.fillRect(x, y, calculatedWidth, calculatedHeight);
//		g.setFill(Color.BLACK);
//
//		//write attributes
//		for (FmmlxAttribute att : ownAttributesToPaint) {
//			Text text = new Text("[" + att.level + "] " + att.name + ":" + att.type);
//			g.fillText(text.getText(), x + gap, Y + gap);
////			if (!att.equals(ownAttributesToPaint.lastElement()))
//				Y += text.getLayoutBounds().getHeight() + gap;
//		}
//
//		g.setFill(Color.GRAY);
//		for (FmmlxAttribute att : otherAttributesToPaint) {
//			Text text = new Text("[" + att.level + "] " + att.name + ":" + att.type);
//			g.fillText(text.getText(), x + gap, Y + gap);
////			if (!att.equals(otherAttributesToPaint.lastElement()))
//				Y += text.getLayoutBounds().getHeight() + gap;
//		}
//		g.setFill(Color.BLACK);
//
//		//write operations
//		if (showOperations) {
//
//			//draw divider
//			g.strokeLine(x, Y + textheight, x + calculatedWidth, Y + textheight);
//			Y += textheight + 2 * gap;
//
//			//write operations
//			for (FmmlxOperation op : operations) {
//				Text text = new Text(op.name);
//				g.fillText(text.getText(), x + gap, Y + gap);
//				if (!op.equals(operations.lastElement()))
//					Y += text.getLayoutBounds().getHeight() + gap;
//			}
//		}
//
//		//write slots
//		if (showSlots) {
//
//			//draw divider
//			g.strokeLine(x, Y + textheight, x + calculatedWidth, Y + textheight);
//			Y += textheight + 2 * gap;
//
//			//write operations
//			for (FmmlxSlot slot : slots) {
//				Text text = new Text(slot.name + " = " + slot.value);
//				g.fillText(text.getText(), x + gap, Y + gap);
//				if (!slot.equals(slots.lastElement()))
//					Y += text.getLayoutBounds().getHeight() + gap;
//			}
//		}
//
//		//write slots
//		if (showOperationValues) {
//
//			//draw divider
//			g.strokeLine(x, Y + textheight, x + calculatedWidth, Y + textheight);
//			Y += textheight + 2 * gap;
//
//			//write operations
//			for (FmmlxOperationValue opValue : operationValues) {
//				Text text = new Text(opValue.name + " = " + opValue.value);
//				g.fillText(text.getText(), x + gap, Y + gap);
//				if (!opValue.equals(operationValues.lastElement()))
//					Y += text.getLayoutBounds().getHeight() + gap;
//			}
//		}
//
//		//drawing the rectangle
//
//		g.setFill(Paint.valueOf(getColor()));
//		g.fillRect(x, y, calculatedWidth, headerheight);
//
//		// set color to white for level 2 & 3
//		if (level == 3 || level == 2) {
//			g.setFill(Color.WHITE);
//		} else {
//			g.setFill(Color.BLACK);
//		}
//		g.fillText("[" + level + "]" + name, x + gap, y + headerheight / 2 + gap);
//
//		// draw divider between class name and attributes
//		g.strokeLine(x, y + headerheight, x + calculatedWidth, y + headerheight);
//		g.strokeRect(x, y, calculatedWidth, calculatedHeight);
//
//		this.height = (int) calculatedHeight;
//		this.width = (int) calculatedWidth;

	}

	public void fetchData(FmmlxDiagramCommunicator comm) {
		Vector<Vector<FmmlxAttribute>> attributeList = comm.fetchAttributes(this.name);
		ownAttributes = attributeList.get(0);
		otherAttributes = attributeList.get(1);
		slots = comm.fetchSlots(this.name);
		Vector<FmmlxOperation> operations = comm.fetchOperations(this.name);
		ownOperations = new Vector<FmmlxOperation>();
		otherOperations = new Vector<FmmlxOperation>();
		for(FmmlxOperation o : operations) {
			if(o.owner == this.id) {
				ownOperations.add(o); 
			} else {
				otherOperations.add(o);
			}
		}
		operationValues = comm.fetchOperationValues(this.name);

	}

	private boolean passReqs(FmmlxAttribute att) {
		return true;
	}

	public boolean isHit(double mouseX, double mouseY) {
		return
				mouseX > x &&
						mouseY > y &&
						mouseX < x + width &&
						mouseY < y + height;
	}

	public double getMaxBottom() {
		return y + height;
	}

	public double getMaxRight() {
		return x + width;
	}

	public String getLevelBackgroundColor() {
		return level < 6 ? levelBackgroundColors[level] : "#ffaa00";
	}

	public String getLevelFontColor() {
		return new Vector<Integer>(Arrays.asList(2, 3)).contains(level) ? "#ffffff" : "000000";
	}

	@Override
	public ObjectContextMenu getContextMenu(DiagramActions actions) {
		return new ObjectContextMenu(this, actions);
	}

	@Override
	public void moveTo(double x, double y, FmmlxDiagram diagram) {
		setX((int) x);
		setY((int) y);
		for (Edge edge : diagram.getEdges()) {
			if (edge.isStartNode(this)) edge.moveStartPoint();
			if (edge.isEndNode(this)) edge.moveEndPoint();
		}
	}
}
