package tool.clients.fmmlxdiagrams;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.graphics.NodeGroup;
import tool.clients.fmmlxdiagrams.graphics.NodeLabel;
import tool.clients.fmmlxdiagrams.graphics.NodePath;

public class ExperimentalFmmlxObjectDisplay extends AbstractFmmlxObjectDisplay {

	public ExperimentalFmmlxObjectDisplay(FmmlxDiagram diagram, FmmlxObject object) {
		super(diagram, object);
	}

	public void layout() {
		object.rootNodeElement = new NodeGroup(new Affine(1, 0, object.x, 0, 1, object.y));
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

//		NodeGroup g = new NodeGroup(a);
		object.rootNodeElement.addNodeElement(p1);
		object.rootNodeElement.addNodeElement(p2);
		object.rootNodeElement.addNodeElement(p3);
		object.rootNodeElement.addNodeElement(p4a);
		object.rootNodeElement.addNodeElement(p5);
//		
//		object.rootNodeElement.addElement(g);
		
		
		
		
		
		
		
	}

	public void layoutStartEvent() {
		object.rootNodeElement = new NodeGroup(new Affine(1, 0, object.x, 0, 1, object.y));
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(), 
				"m 17.376953,-5.8261719 c -0.409,0.004 -0.733656,0.1103594 -0.972656,0.3183594 "
				+ "-0.238,0.208 -0.359234,0.4934687 -0.365235,0.8554687 0.011,0.297 0.108829,0.5405156 "
				+ "0.298829,0.7285157 0.188,0.189 0.406297,0.3550937 0.654297,0.4960937 0.248999,0.14 "
				+ "0.466296,0.2865 0.654296,0.4375 0.189,0.152 0.288828,0.3405 0.298828,0.5625 "
				+ "10e-4,0.131 -0.05778,0.2531406 -0.175781,0.3691406 -0.116,0.117 -0.297015,0.1775469 "
				+ "-0.541015,0.1855469 -0.219,-10e-4 -0.428,-0.055063 -0.625,-0.1640625 -0.197,-0.109 "
				+ "-0.376063,-0.2676094 -0.539063,-0.4746094 v 0.8007813 c 0.182,0.138 "
				+ "0.375172,0.2396406 0.576172,0.3066406 0.201,0.067 0.421203,0.1006094 "
				+ "0.658203,0.099609 0.362,-0.006 0.655813,-0.1198906 0.882813,-0.3378906 "
				+ "0.227,-0.2190001 0.344562,-0.5044219 0.351562,-0.8574219 -0.011,-0.344 "
				+ "-0.109828,-0.6173125 -0.298828,-0.8203125 -0.189,-0.204 -0.40825,-0.3709063 "
				+ "-0.65625,-0.5039063 -0.248,-0.133 -0.464344,-0.2663906 -0.652344,-0.4003906 "
				+ "-0.188,-0.134 -0.289828,-0.3047656 -0.298828,-0.5097656 0.003,-0.158 "
				+ "0.06831,-0.2829532 0.195313,-0.3769532 0.127,-0.094 0.293953,-0.1434843 "
				+ "0.501953,-0.1464843 0.149,-0.003 0.313281,0.027703 0.488281,0.095703 0.176,0.067 "
				+ "0.355922,0.1892812 0.544922,0.3632813 v -0.7187497 c -0.155,-0.104 "
				+ "-0.313516,-0.1814218 -0.478516,-0.2324218 -0.166,-0.052 -0.332953,-0.076172 "
				+ "-0.501953,-0.076172 z m 7.244141,0.03125 -1.894532,4.4316406 h 0.646485 l "
				+ "0.556641,-1.2988281 h 1.873046 l 0.587891,1.2988281 h 0.646484 l -1.970703,-4.4316406 "
				+ "z m -5.447266,0.025391 v 0.5683594 h 1.523438 v 3.8378906 h 0.632812 v -3.8378906 h "
				+ "1.525391 v -0.5683598 z m 8.681641,0 v 4.40625 h 0.630859 v -1.9121094 h 0.267578 c "
				+ "0.105,-0.004 0.21236,0.015547 0.31836,0.060547 0.105,0.044 0.226328,0.1391563 "
				+ "0.361328,0.2851563 0.134,0.145 0.298234,0.3651093 0.490234,0.6621094 l 0.580078,"
				+ "0.9042968 h 0.75586 c -0.146,-0.201 -0.26175,-0.3605156 -0.34375,-0.4785156 "
				+ "-0.084,-0.119 -0.170813,-0.2505781 -0.257813,-0.3925781 -0.164,-0.275 "
				+ "-0.317797,-0.51475 -0.466797,-0.71875 -0.147,-0.203 -0.319625,-0.3709063 "
				+ "-0.515625,-0.5039063 0.237,-0.095 0.419875,-0.2339687 0.546875,-0.4179687 "
				+ "0.126,-0.184 0.187453,-0.4031563 0.189453,-0.6601563 -0.005,-0.385 -0.134625,-0.6853437 "
				+ "-0.390625,-0.9023437 -0.254,-0.217 -0.60564,-0.3280313 -1.05664,-0.3320313 z m "
				+ "3.328125,0 v 0.5683594 h 1.52539 v 3.8378906 h 0.632813 v -3.8378906 h 1.525391 "
				+ "v -0.5683598 z m -2.697266,0.5429688 h 0.408203 c 0.305,0.002 0.52511,0.060734 "
				+ "0.66211,0.1777343 0.135,0.117 0.205078,0.2792813 0.205078,0.4882813 0.01,0.191 "
				+ "-0.04978,0.3560938 -0.175781,0.4960938 -0.127,0.14 -0.357407,0.2137031 "
				+ "-0.691407,0.2207031 h -0.408203 z m -3.654297,0.4023437 0.710938,1.5957031 H 24.173828 "
				+ "Z M 0,0 17.835938,31.324219 35.671875,0 Z M 1.4824219,0.8613281 H 34.189453 L "
				+ "17.835938,29.582031 Z", Color.web("#a2b400"), Color.TRANSPARENT, null, () -> {}));
		addEventChange(object.rootNodeElement);
		NodeLabel.Action changeNameAction = () -> diagram.getActions().changeNameDialog(object, PropertyType.Class);
		object.rootNodeElement.addNodeElement(new NodeLabel(
				Pos.BASELINE_CENTER, 18, 50, 
				Color.BLACK, Color.TRANSPARENT, null, 
				changeNameAction, 
				object.getName()));
	}

	public void layoutEvent() {
		object.rootNodeElement = new NodeGroup(new Affine(1, 0, object.x, 0, 1, object.y));
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(), 
				"M 17.955,31.533 0,0 H 35.909 Z M 1.721,0.99999996 17.955,29.512 34.188,0.99999996 Z"
				, Color.CORNFLOWERBLUE, Color.TRANSPARENT, null, () -> {}));
		addEventChange(object.rootNodeElement);
		NodeLabel.Action changeNameAction = () -> diagram.getActions().changeNameDialog(object, PropertyType.Class);
		object.rootNodeElement.addNodeElement(new NodeLabel(
				Pos.BASELINE_CENTER, 18, 50, 
				Color.BLACK, Color.TRANSPARENT, null, 
				changeNameAction, 
				object.getName()));
	}

	public void layoutStopEvent() {
		object.rootNodeElement = new NodeGroup(new Affine(1, 0, object.x, 0, 1, object.y));
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(), 
				"M 19.998047 -5.8261719 C 19.590047 -5.8221719 19.265344 -5.7168125 19.027344 -5.5078125 C 18.788344 -5.2998125 18.667109 -5.0123906 18.662109 -4.6503906 C 18.671109 -4.3533906 18.770984 -4.109875 18.958984 -3.921875 C 19.147984 -3.732875 19.366281 -3.5687344 19.613281 -3.4277344 C 19.861281 -3.2877344 20.081531 -3.1392813 20.269531 -2.9882812 C 20.457531 -2.8362812 20.556406 -2.6507344 20.566406 -2.4277344 C 20.567406 -2.2967344 20.510578 -2.1726406 20.392578 -2.0566406 C 20.275578 -1.9396406 20.094609 -1.8790938 19.849609 -1.8710938 C 19.630609 -1.8720937 19.424562 -1.9261563 19.226562 -2.0351562 C 19.029563 -2.1441562 18.8495 -2.3047187 18.6875 -2.5117188 L 18.6875 -1.7109375 C 18.8705 -1.5719375 19.059719 -1.4683437 19.261719 -1.4023438 C 19.461719 -1.3353438 19.682875 -1.3036875 19.921875 -1.3046875 C 20.283875 -1.3106875 20.577688 -1.422625 20.804688 -1.640625 C 21.030687 -1.859625 21.147297 -2.1450469 21.154297 -2.4980469 C 21.144297 -2.8420469 21.044469 -3.1153594 20.855469 -3.3183594 C 20.666469 -3.5213594 20.448172 -3.6912188 20.201172 -3.8242188 C 19.952172 -3.9572188 19.735875 -4.0906094 19.546875 -4.2246094 C 19.358875 -4.3586094 19.259047 -4.5274219 19.248047 -4.7324219 C 19.252047 -4.8904219 19.318312 -5.0173281 19.445312 -5.1113281 C 19.571313 -5.2063281 19.738312 -5.2548125 19.945312 -5.2578125 C 20.095312 -5.2608125 20.259547 -5.2271563 20.435547 -5.1601562 C 20.610547 -5.0931562 20.791469 -4.9728281 20.980469 -4.7988281 L 20.980469 -5.5175781 C 20.824469 -5.6215781 20.664047 -5.6970469 20.498047 -5.7480469 C 20.333047 -5.8000469 20.167047 -5.8261719 19.998047 -5.8261719 z M 28.191406 -5.8261719 C 27.712406 -5.8211719 27.295359 -5.7176719 26.943359 -5.5136719 C 26.589359 -5.3086719 26.315047 -5.0353125 26.123047 -4.6953125 C 25.928047 -4.3543125 25.828172 -3.9784062 25.826172 -3.5664062 C 25.828172 -3.1624063 25.925234 -2.7882656 26.115234 -2.4472656 C 26.306234 -2.1062656 26.576734 -1.833 26.927734 -1.625 C 27.278734 -1.417 27.695734 -1.3096875 28.177734 -1.3046875 C 28.677734 -1.3096875 29.104984 -1.4179531 29.458984 -1.6269531 C 29.813984 -1.8349531 30.085391 -2.1091719 30.275391 -2.4511719 C 30.463391 -2.7911719 30.558547 -3.1644062 30.560547 -3.5664062 C 30.557547 -3.9804063 30.461531 -4.3572656 30.269531 -4.6972656 C 30.078531 -5.0372656 29.805172 -5.3096719 29.451172 -5.5136719 C 29.098172 -5.7176719 28.678406 -5.8211719 28.191406 -5.8261719 z M 21.794922 -5.7695312 L 21.794922 -5.2011719 L 23.318359 -5.2011719 L 23.318359 -1.3632812 L 23.951172 -1.3632812 L 23.951172 -5.2011719 L 25.476562 -5.2011719 L 25.476562 -5.7695312 L 21.794922 -5.7695312 z M 31.630859 -5.7695312 L 31.630859 -1.3632812 L 32.263672 -1.3632812 L 32.263672 -3.2695312 L 32.923828 -3.2695312 C 33.431828 -3.2775313 33.805828 -3.3969531 34.048828 -3.6269531 C 34.289828 -3.8569531 34.40925 -4.1479531 34.40625 -4.5019531 C 34.40925 -4.8599531 34.296359 -5.1584844 34.068359 -5.3964844 C 33.839359 -5.6354844 33.484859 -5.7595313 33.005859 -5.7695312 L 31.630859 -5.7695312 z M 28.193359 -5.2460938 C 28.697359 -5.2370937 29.106875 -5.0775313 29.421875 -4.7695312 C 29.737875 -4.4605312 29.901156 -4.0614063 29.910156 -3.5664062 C 29.901156 -3.0754062 29.737875 -2.6732813 29.421875 -2.3632812 C 29.106875 -2.0542812 28.696359 -1.8947656 28.193359 -1.8847656 C 27.691359 -1.8947656 27.282797 -2.0542813 26.966797 -2.3632812 C 26.651797 -2.6732813 26.489516 -3.0754062 26.478516 -3.5664062 C 26.489516 -4.0604062 26.651797 -4.4605313 26.966797 -4.7695312 C 27.281797 -5.0775312 27.691359 -5.2370937 28.193359 -5.2460938 z M 32.263672 -5.2011719 L 33.007812 -5.2011719 C 33.289812 -5.1931719 33.486656 -5.1213281 33.597656 -4.9863281 C 33.708656 -4.8503281 33.761813 -4.6963438 33.757812 -4.5273438 C 33.753813 -4.3063437 33.682922 -4.1365781 33.544922 -4.0175781 C 33.406922 -3.8975781 33.218469 -3.8388906 32.980469 -3.8378906 L 32.263672 -3.8378906 L 32.263672 -5.2011719 z M 0 0 L 17.835938 31.324219 L 35.671875 0 L 0 0 z M 1.4824219 0.86132812 L 34.189453 0.86132812 L 17.835938 29.582031 L 1.4824219 0.86132812 z "
				, Color.web("#c80034"), Color.TRANSPARENT, null, () -> {}));
		addEventChange(object.rootNodeElement);
		NodeLabel.Action changeNameAction = () -> diagram.getActions().changeNameDialog(object, PropertyType.Class);
		object.rootNodeElement.addNodeElement(new NodeLabel(
				Pos.BASELINE_CENTER, 18, 50, 
				Color.BLACK, Color.TRANSPARENT, null, 
				changeNameAction, 
				object.getName()));
	}
	
	private void addEventChange(NodeGroup rootNodeElement) {
		// FOLDER YELLOW BG
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(new Translate(-4.473,-6.866)), 
				"M 10.449,28.791 V 13.158 h 7.892 v 1.97 l 15.557,-0.015 v 13.678 z"
				, Color.web("#fffde8"), Color.TRANSPARENT, null, () -> {}));
		// FOLDER BLACK FG
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(), 
				"M 7.7753906,3.994141 7.5429688,5.947266 H 5.6308594 V 22.269531 H 29.730469 L 31.6875,5.947266 16.126953,5.964846 16.363281,3.994143 Z M 6.1484375,6.464844 H 13.695312 V 8.435547 L 29.253906,8.419917 V 21.753906 H 6.1484375 Z m 1.78125,5.605468 v 0.167969 H 21.609375 v -0.167969 z m 0,1.953126 v 0.169921 H 21.609375 v -0.169921 z m 0,1.955078 v 0.167968 H 21.609375 v -0.167968 z"
				, Color.BLACK, Color.TRANSPARENT, null, () -> {}));
	}

	public void layoutComputerSupportedProcess() {
		object.rootNodeElement = new NodeGroup(new Affine(1, 0, object.x, 0, 1, object.y));
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(), 
				"M 71.735,28.693 H 0 V 0 H 71.735 L 89.67,14.346 Z"
				, Color.web("#c6d4dc"), Color.TRANSPARENT, null, () -> {}));
		// Table
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(new Translate(-28.205,-18.042)), 
				"m 79.137,34.542 h 6.455 V 32.389 H 62.638 v 2.153 h 12.196 v 19.367 h 4.303 z"
				, Color.BLACK, Color.TRANSPARENT, null, () -> {}));
		// Body BG
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(), 
				"m 27.361328,-7.6113281 c -2.318,0 -4.203125,1.886125 -4.203125,4.203125 0,2.318 1.885125,4.2050781 4.203125,4.2050781 2.318,0 4.204125,-1.8860781 4.203125,-4.2050781 0,-2.317 -1.886125,-4.203125 -4.203125,-4.203125 z m -1.474609,8.8515625 c -2.913215,0 -5.292969,2.3825421 -5.292969,5.2949218 V 18.484375 l -0.002,0.162109 v 0.0059 c 0,2.886674 2.353719,5.254818 5.28125,5.294922 h 0.0059 7.560547 v 12.544922 h 5.296875 5.294922 v -0.990235 c 0,-1.185285 -0.72964,-2.18575 -1.699219,-2.824219 -0.722051,-0.475471 -1.629647,-0.728767 -2.59375,-0.859375 l -0.0098,-8.863281 c 0,-2.913214 -2.383939,-5.294922 -5.296875,-5.294922 h -3.248047 v -2.320312 h 3.310547 5.298828 v -0.992188 c 0,-1.185948 -0.733226,-2.186055 -1.705078,-2.824218 -0.971853,-0.638166 -2.224409,-0.990235 -3.59375,-0.990235 H 31.183594 V 6.5351562 c 0,-2.9132145 -2.382886,-5.2949218 -5.296875,-5.2949218 z"
				, Color.BLACK, Color.TRANSPARENT, null, () -> {}));
		// Body FG
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(), 
				"M 21.584,18.652 21.586,18.49 V 6.536 c 0,-2.377 1.924,-4.304 4.301,-4.304 2.378,0 4.305,1.927 4.305,4.304 v 4.99 h 4.303 c 2.389,0 4.305,1.263 4.305,2.822 h -4.305 -4.304 v 4.305 h 4.241 c 2.377,0 4.304,1.926 4.304,4.303 l 0.013,9.723 c 2.377,0 4.291,1.264 4.291,2.822 H 38.736 34.432 V 22.955 h -8.546 c -2.409,-0.033 -4.302,-1.948 -4.302,-4.303 z"
				, Color.web("#e7e7e8"), Color.TRANSPARENT, null, () -> {}));
		// Head FG
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(), 
				"M 27.361328 -6.734375 C 25.525328 -6.734375 24.033203 -5.24125 24.033203 -3.40625 C 24.033203 -1.57125 25.525328 -0.078125 27.361328 -0.078125 C 29.196328 -0.078125 30.689453 -1.57025 30.689453 -3.40625 C 30.690453 -5.24125 29.196328 -6.734375 27.361328 -6.734375 z "
				, Color.web("#fffde8"), Color.TRANSPARENT, null, () -> {}));
		// PC BG
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(), 
				"m 45.929688,-6.1289062 -0.734376,0.6035156 -2.652343,13.265625 0.734375,0.8964844 h 8.365234 l 0.002,1.3730472 h -9.353515 v 0.75 4.33789 h 14.441406 v -5.08789 H 55.095703 V 3.4296875 c 0,-0.6655796 -0.461504,-1.1368085 -1.033203,-1.4199219 l 1.447266,-7.2421875 -0.734375,-0.8964843 z"
				, Color.BLACK, Color.TRANSPARENT, null, () -> {}));
		// PC FG
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(), 
				"m 45.931641,-5.3789062 -2.654297,13.265625 h 8.84375 l 0.267578,-1.3398438 0.810547,-4.0488281 1.576172,-7.8769531 z m 0.755859,0.9199218 h 6.962891 L 51.367188,6.9648438 h -6.775391 -0.189453 z m 7.242188,7.1191406 c -0.398436,1.9921789 -0.796703,3.9843907 -1.195313,5.9765626 h -0.341797 l 0.002,1.3730472 H 54.34575 V 3.4316406 c 5e-6,-0.3242484 -0.170785,-0.5968042 -0.416015,-0.7714844 z M 43.041016,10.761719 v 3.585937 h 12.941406 v -3.585937 z m 1.160156,1.292969 h 4.220703 v 1 h -4.220703 z"
				, Color.WHITE, Color.TRANSPARENT, null, () -> {}));
		NodeLabel.Action changeNameAction = () -> diagram.getActions().changeNameDialog(object, PropertyType.Class);
		object.rootNodeElement.addNodeElement(new NodeLabel(
				Pos.BASELINE_CENTER, 45, 60, 
				Color.BLACK, Color.TRANSPARENT, null, 
				changeNameAction, 
				object.getName()));		
	}

	public void layoutAutomatedProcess() {
		object.rootNodeElement = new NodeGroup(new Affine(1, 0, object.x, 0, 1, object.y));
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(), 
				"M 71.735,28.693 H 0 V 0 H 71.735 L 89.67,14.346 Z"
				, Color.web("#c6d4dc"), Color.TRANSPARENT, null, () -> {}));
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(), 
				"M 46.629,-7.174 V 35.867 H 25.108 V -7.174 Z"
				, Color.web("#e7e7e8"), Color.TRANSPARENT, null, () -> {}));
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(), 
				"m 46.629,35.867 c -7.173667,0 -14.347333,0 -21.521,0 0,-1.195667 0,-2.391333 0,-3.587 7.173667,-3.33e-4 14.347333,-6.67e-4 21.521,-10e-4 0,1.196 0,2.392 0,3.588 z"
				, Color.web("#c4c5c6"), Color.TRANSPARENT, null, () -> {}));
		object.rootNodeElement.addNodeElement(new NodePath(new Affine(), 
				"M 24.107422 -8.1738281 L 24.107422 36.867188 L 47.628906 36.867188 L 47.628906 -8.1738281 L 24.107422 -8.1738281 z M 26.107422 -6.1738281 L 45.628906 -6.1738281 L 45.628906 34.867188 L 26.107422 34.867188 L 26.107422 -6.1738281 z M 28.193359 -4.0878906 L 28.193359 0.5 L 43.542969 0.5 L 43.542969 -4.0878906 L 28.193359 -4.0878906 z M 29.193359 -3.0878906 L 42.542969 -3.0878906 L 42.542969 -0.5 L 29.193359 -0.5 L 29.193359 -3.0878906 z M 28.193359 3.0859375 L 28.193359 7.671875 L 43.542969 7.671875 L 43.542969 3.0859375 L 28.193359 3.0859375 z M 29.193359 4.0859375 L 42.542969 4.0859375 L 42.542969 6.671875 L 29.193359 6.671875 L 29.193359 4.0859375 z M 28.193359 10.259766 L 28.193359 14.845703 L 43.542969 14.845703 L 43.542969 10.259766 L 28.193359 10.259766 z M 29.193359 11.259766 L 42.542969 11.259766 L 42.542969 13.845703 L 29.193359 13.845703 L 29.193359 11.259766 z M 31.78125 17.433594 L 31.78125 22.019531 L 39.953125 22.019531 L 39.953125 17.433594 L 31.78125 17.433594 z M 32.78125 18.433594 L 38.953125 18.433594 L 38.953125 21.019531 L 32.78125 21.019531 L 32.78125 18.433594 z M 32.28125 22.677734 L 32.28125 23.310547 L 39.453125 23.310547 L 39.453125 22.677734 L 32.28125 22.677734 z M 32.28125 24.472656 L 32.28125 25.105469 L 39.453125 25.105469 L 39.453125 24.472656 L 32.28125 24.472656 z M 32.28125 26.265625 L 32.28125 26.898438 L 39.453125 26.898438 L 39.453125 26.265625 L 32.28125 26.265625 z M 32.28125 28.060547 L 32.28125 28.693359 L 39.453125 28.693359 L 39.453125 28.060547 L 32.28125 28.060547 z M 32.28125 29.853516 L 32.28125 30.486328 L 39.453125 30.486328 L 39.453125 29.853516 L 32.28125 29.853516 z M 32.28125 31.646484 L 32.28125 32.279297 L 39.453125 32.279297 L 39.453125 31.646484 L 32.28125 31.646484 z "
				, Color.BLACK, Color.TRANSPARENT, null, () -> {}));
		NodeLabel.Action changeNameAction = () -> diagram.getActions().changeNameDialog(object, PropertyType.Class);
		object.rootNodeElement.addNodeElement(new NodeLabel(
				Pos.BASELINE_CENTER, 45, 60, 
				Color.BLACK, Color.TRANSPARENT, null, 
				changeNameAction, 
				object.getName()));		
	}

}
