package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.NodeBaseElement.Action;

public class ExperimentalFmmlxObjectDisplay extends AbstractFmmlxObjectDisplay {

	public ExperimentalFmmlxObjectDisplay(FmmlxDiagram diagram, FmmlxObject object) {
		super(diagram, object);
	}

	public void layout() {
		object.nodeElements = new Vector<>();
		FmmlxSlot sizeSlot = object.getSlot("size");
		System.err.println("size = " + sizeSlot);
		double size = sizeSlot==null?0:Double.parseDouble(sizeSlot.getValue());
		double radius = size / 3. + 30;
		double moonRadius = radius/5;
		FmmlxSlot colorSlot = object.getSlot("color");
		System.err.println("color = " + colorSlot);
		String color = colorSlot==null?"000000":colorSlot.getValue();
		Color bgColor = Color.BLACK;
		try{bgColor = Color.web("0x" + color);} catch (IllegalArgumentException e) {}
		
		FmmlxSlot moonsSlot = object.getSlot("moons");
		int moons = moonsSlot==null?0:Integer.parseInt(moonsSlot.getValue());

		Action changeSlotValueAction = () -> {if (moonsSlot != null) diagram.getActions().changeSlotValue(object, moonsSlot);};

		NodeCircle mainCircle = new NodeCircle(0, 0, radius, bgColor, moonsSlot, changeSlotValueAction);
		
		object.nodeElements.addElement(mainCircle);
		
		for(int M = 0; M < moons; M++) {
			double x = radius/2 + radius * 1.5 * Math.sin((2 * Math.PI * M) / moons) - moonRadius/2;
			double y = radius/2 + radius * 1.5 * Math.cos((2 * Math.PI * M) / moons) - moonRadius/2;
			NodeCircle moonCircle = new NodeCircle(x, y, moonRadius, Color.GRAY, null, () -> {});
			object.nodeElements.addElement(moonCircle);
		}
	}

}
