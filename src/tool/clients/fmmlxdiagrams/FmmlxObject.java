package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FmmlxObject {

	String name;
	int x; 
	int y;
	int level;
	int width;
	int height;
	static int testDiff = 10;
	Vector<FmmlxAttribute> attributes;
	Vector<FmmlxSlot> slots;
	Vector<FmmlxOperation> operations;
	Vector<FmmlxOperationValue> operationValues;
	
	public FmmlxObject(String name) {
		this.name = name;
		x = 10;
		y = testDiff;
		testDiff += 100;
		width = 150;
		height = 80;
		level = name.hashCode()%5;
	}

	public void paintOn(GraphicsContext g, int xOffset, int yOffset) {
		// check Size?
		// draw border
		g.strokeRect(x, y, width, height);
		// draw title bar
		g.setFill(Color.RED);
		g.fillRect(x, y, width, 20);
		g.setFill(Color.BLACK);
		g.fillText(name, 10, y + 20);
		
		int Y = 40+y;		
		for(FmmlxAttribute att : attributes) {
			g.fillText("[" + att.level + "] " + att.name + ":" + att.type, 10, Y);
			Y += 20;
		}
	}

	public void fetchData(FmmlxDiagramCommunicator comm) {
		attributes = comm.fetchAttributes(this.name);
		slots = comm.fetchSlots(this.name);
		operations = comm.fetchOperations(this.name);
		operationValues = comm.fetchOperationValues(this.name);
		
	}

}
