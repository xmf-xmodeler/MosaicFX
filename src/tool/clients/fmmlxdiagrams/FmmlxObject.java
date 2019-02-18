package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class FmmlxObject {

	String name;
	int id;
	int x; 
	int y;
	int level;
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
	
	Vector<FmmlxAttribute> attributes;
	Vector<FmmlxSlot> slots;
	Vector<FmmlxOperation> operations;
	Vector<FmmlxOperationValue> operationValues;
	
	public FmmlxObject(String name) {
		this.name = name;
		x = testDiff;
		y = 10;
		testDiff += 150;
		width = 150;
		height = 80;
		level = name.hashCode()%5;
	}

	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram diagram) {

		boolean selected = diagram.isSelected(this);
		g.setStroke(selected?Color.GREEN:Color.BLACK);

		double calculatedHeight = 0;
		double calculatedWidth = 0;
			
		//determine attributes to paint
		Vector<FmmlxAttribute> attributesToPaint = new Vector<FmmlxAttribute>();
		
		for(FmmlxAttribute att : attributes) {
			if (passReqs(att)) {
				attributesToPaint.add(att);
				// determine maximal width
				Text text = new Text("[" + att.level + "] " + att.name + ":" + att.type);
				calculatedWidth = Math.max(text.getLayoutBounds().getWidth(), calculatedWidth);
			}
		}
		
		//determine maximal width of operations
		if (showOperations) {
			for (FmmlxOperation operation : operations) {
				Text text = new Text(operation.name);
				calculatedWidth = Math.max(text.getLayoutBounds().getWidth(), calculatedWidth);
			}
		}
		//determine maximal width of slots
		if (showSlots) {
			for (FmmlxSlot slot : slots) {
				Text text = new Text(slot.name + " = " + slot.value);
				calculatedWidth = Math.max(text.getLayoutBounds().getWidth(), calculatedWidth);
			}
		}
		
		//determine maximal width of operation values
		if (showOperationValues) {
			for (FmmlxOperationValue operationValue : operationValues) {
				Text text = new Text(operationValue.name + " = "+ operationValue.value);
				calculatedWidth = Math.max(text.getLayoutBounds().getWidth(), calculatedWidth);
			}
		}
		
		//if minimum width is not reached just paint minimum
		calculatedWidth = Math.max(calculatedWidth + 2*gap, minWidth);
		
		//calculating header height 
		Text header = new Text(name);
		double headerheight = header.getLayoutBounds().getHeight() + 2*gap;
		
		//determine text height
		double textheight = new Text(attributesToPaint.get(0).name).getLayoutBounds().getHeight();
		
		//calculate starting position for text
		double Y = 0 + headerheight + 2*gap;	// just a guess
		calculatedHeight = Y + (attributesToPaint.size() -1) *(textheight + gap) + gap;
		
		if (showOperations)
			calculatedHeight += (operations.size()-1) * (textheight + gap) + textheight + 2*gap ;
		
		if (showOperationValues)
			calculatedHeight += (operationValues.size()-1) * (textheight + gap) + textheight + 2*gap;
		
		if (showSlots)
			calculatedHeight += (slots.size()-1) * (textheight + gap) + textheight + 2*gap;
		
		calculatedHeight += 10;// just a guess
		
		Y += y;
		
		//set background
		g.setFill(Color.WHITE);
		g.fillRect(x, y, calculatedWidth, calculatedHeight);
		g.setFill(Color.BLACK);
		
		//write attributes
		for (FmmlxAttribute att : attributesToPaint) {
			Text text = new Text("[" + att.level + "] " + att.name + ":" + att.type);
			g.fillText(text.getText(),x + gap,Y + gap);
			if (!att.equals(attributesToPaint.lastElement()))
			Y += text.getLayoutBounds().getHeight() + gap;
		}
		
		//write operations
		if (showOperations) {
			
			//draw divider
			g.strokeLine(x, Y + textheight, x + calculatedWidth, Y + textheight);
			Y += textheight + 2*gap;
			
			//write operations
			for (FmmlxOperation op : operations) {
				Text text = new Text(op.name);
				g.fillText(text.getText(),x + gap,Y + gap);
				if (!op.equals(operations.lastElement()))
				Y += text.getLayoutBounds().getHeight() + gap;
			}
		}
		
		//write slots
		if (showSlots) {
			
			//draw divider
			g.strokeLine(x, Y + textheight, x + calculatedWidth, Y + textheight);
			Y += textheight + 2*gap;
			
			//write operations
			for (FmmlxSlot slot : slots) {
				Text text = new Text(slot.name + " = " + slot.value);
				g.fillText(text.getText(),x + gap,Y + gap);
				if (!slot.equals(slots.lastElement()))
				Y += text.getLayoutBounds().getHeight() + gap;
			}
		}
		
		//write slots
				if (showOperationValues) {
					
					//draw divider
					g.strokeLine(x, Y + textheight, x + calculatedWidth, Y + textheight);
					Y += textheight + 2*gap;
					
					//write operations
					for (FmmlxOperationValue opValue : operationValues) {
						Text text = new Text(opValue.name + " = " + opValue.value);
						g.fillText(text.getText(),x + gap,Y + gap);
						if (!opValue.equals(operationValues.lastElement()))
						Y += text.getLayoutBounds().getHeight() + gap;
					}
				}
		
		//drawing the rectangle
		
		g.setFill(Color.RED);
		g.fillRect(x, y, calculatedWidth , headerheight);
		g.setFill(Color.BLACK);
		g.fillText(name, x + gap, y + headerheight/2 + gap);
		g.strokeRect(x, y, calculatedWidth  , calculatedHeight);

		this.height = (int) calculatedHeight;
		this.width = (int) calculatedWidth;

	}

	public void fetchData(FmmlxDiagramCommunicator comm) {
		attributes = comm.fetchAttributes(this.name);
		slots = comm.fetchSlots(this.name);
		operations = comm.fetchOperations(this.name);
		operationValues = comm.fetchOperationValues(this.name);
		
	}
	
	private boolean passReqs (FmmlxAttribute att) {
		
		return true;
	}

	public boolean isHit(double mouseX, double mouseY) {
		return 
				mouseX > x &&
				mouseY > y &&
				mouseX < x + width &&
				mouseY < y + height;
	}
	
}
