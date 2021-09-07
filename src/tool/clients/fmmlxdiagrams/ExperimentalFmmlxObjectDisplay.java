package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import tool.clients.fmmlxdiagrams.graphics.NodeCircle;
import tool.clients.fmmlxdiagrams.graphics.NodeGroup;
import tool.clients.fmmlxdiagrams.graphics.NodePath;
import tool.clients.fmmlxdiagrams.graphics.NodeBaseElement.Action;

public class ExperimentalFmmlxObjectDisplay extends AbstractFmmlxObjectDisplay {

	public ExperimentalFmmlxObjectDisplay(FmmlxDiagram diagram, FmmlxObject object) {
		super(diagram, object);
	}

	public void layout() {
		object.nodeElements = new Vector<>();
		FmmlxSlot sizeSlot = object.getSlot("size");
		double size = 1. + (sizeSlot==null?0:Double.parseDouble(sizeSlot.getValue()));
		FmmlxSlot angleSlot = object.getSlot("angle");
		double angle = angleSlot==null?0:Double.parseDouble(sizeSlot.getValue());
//		double radius = size / 3. + 30;
//		double moonRadius = radius/5;
//		FmmlxSlot colorSlot = object.getSlot("color");
//		String color = colorSlot==null?"000000":colorSlot.getValue();
//		Color bgColor = Color.BLACK;
//		try{bgColor = Color.web("0x" + color);} catch (IllegalArgumentException e) {}
//		
//		FmmlxSlot moonsSlot = object.getSlot("moons");
//		int moons = moonsSlot==null?0:Integer.parseInt(moonsSlot.getValue());
//
//		Action changeSlotValueAction = () -> {if (moonsSlot != null) diagram.getActions().changeSlotValue(object, moonsSlot);};
//
//		NodeCircle mainCircle = new NodeCircle(0, 0, radius, bgColor, moonsSlot, changeSlotValueAction);
//		
//		object.nodeElements.addElement(mainCircle);
//		
//		for(int M = 0; M < moons; M++) {
//			double angle = M * 4 * Math.PI / (1 - Math.sqrt(5));
//			double x = radius/2 + radius * .04 * (20 + M) * Math.sin(angle) - moonRadius/2;
//			double y = radius/2 + radius * .04 * (20 + M) * Math.cos(angle) - moonRadius/2;
//			NodeCircle moonCircle = new NodeCircle(x, y, moonRadius, Color.GRAY, null, () -> {});
//			object.nodeElements.addElement(moonCircle);
//		}
//		
//		Affine t = new Affine(); t.prependRotation(60,20,20); t.prependTranslation(0, 100);
//		NodePath p = new NodePath(t,
//			"M 12.415178,0.13399594 C 6.7155769,0.08589594 3.1893849,1.0527359 3.1893849,5.8261559 V 24.536126 c 0,4.68309 6.047695,7.18147 6.047695,7.18147 l 11.5279781,1.32292 v 6.04769 "
//			+ "l -17.0087821,0.18862 -3.59099378,15.68638 49.51480388,0.94465 -4.157368,-16.63103 -18.143077,-0.75603 -0.377756,-4.72426 12.47314,0.37776 "
//			+ "c 0,0 5.581722,-1.0352 5.669423,-5.85856 l 0.37827,-20.7889101 "
//			+ "c 0.089,-4.89623 0.360029,-6.44771 -5.480802,-6.61457996 l -26.458333,-0.75603 c -0.398891,-0.0114 -0.788431,-0.019 -1.168405,-0.0222 z m 2.302187,3.80234996 21.733555,0.75603 "
//			+ "c 4.198821,-0.6852 4.012872,2.28389 3.780131,5.29166 L 40.41967,25.859036 c 0.01345,4.03954 -2.927308,2.90829 -4.724257,3.77962 "
//			+ "L 11.504641,27.370576 C 9.3503109,26.576666 6.7844453,26.262886 7.5358911,22.078906 L 8.102782,8.8497459 c 0.2507774,-2.50639 1.8455958,-4.41512 6.614583,-4.9134 z", 
//			Color.web("0xffaa00"), Color.web("0xaa0000"), 
//			moonsSlot, changeSlotValueAction);
//		object.nodeElements.addElement(p);
		
		NodePath p1 = new NodePath(new Affine(),
			"M 3.832,4.068 H 15.121 V 20.669999 H 3.832 Z",
			Color.WHITE, Color.TRANSPARENT,
			null, () -> {});
		NodePath p2 = new NodePath(new Affine(),
			"M 15.121,20.67 H 3.832 V 4.068 H 15.121 Z M 4.633,19.869 h 9.688 v -15 H 4.633 Z",
			Color.BLACK, Color.TRANSPARENT,
			null, () -> {});		
		NodePath p3 = new NodePath(new Affine(),
			"m 12.467,1.754 -0.42,0.441 0.744,1.88 -1.575,4.654 -1.734,1.041 0.066,0.605 2.556,0.867 -1.542,4.559 0.374,2.248 1.604,-1.504 1.593,-4.616 2.566,0.869 0.42,-0.44 -0.744,-1.88 1.576,-4.656 1.734,-1.04 -0.066,-0.606 z",
			Color.BLACK, Color.TRANSPARENT,
			null, () -> {});		
		NodePath p4a = new NodePath(new Affine(),
			"M 15.705,10.61 15.699,10.379 17.355,5.49 17.5,5.309 l 1.218,-0.73 -5.78,-1.957 0.523,1.321 0.005,0.23 -1.654,4.888 -0.145,0.181 -1.218,0.731 5.779,1.957 z",
			Color.web("0xe30613"), Color.TRANSPARENT,
			null, () -> {});		
		NodePath p4b = new NodePath(new Affine(),
			"M 15.705,10.61 15.699,10.379 17.355,5.49 17.5,5.309 l 1.218,-0.73 -5.78,-1.957 0.523,1.321 0.005,0.23 -1.654,4.888 -0.145,0.181 -1.218,0.731 5.779,1.957 z",
			Color.web("0x13e306"), Color.TRANSPARENT,
			null, () -> {});		
		NodePath p5 = new NodePath(new Affine(),
			"m 13.343,12.205 -0.768,-0.262 -1.323,3.914 0.141,0.844 0.627,-0.588 z",
			Color.web("0x706f6f"), Color.TRANSPARENT,
			null, () -> {});		
		
		Affine a = new Affine();
		angle = 360 / (1 - Math.sqrt(5));
		a.prependRotation(angle, 10, 10);
		size = 2;
		a.prependScale(size, size);

		NodeGroup g = new NodeGroup(a);
//		g.addElement(p1, false, false);
//		g.addElement(p2, false, false);
//		g.addElement(p3, false, false);
//		g.addElement(p4a, false, true);
//		g.addElement(p4b, true, false);
//		g.addElement(p5, false, false);
//		
//		object.nodeElements.addElement(g);
		
		
		
		
		
		
		
	}

}
