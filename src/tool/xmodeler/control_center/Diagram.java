package tool.xmodeler.control_center;

class Diagram {

	private final String name;
	private final boolean umlMode;
	
	
	Diagram(String name, boolean umlMode) {
		super();
		this.name = name;
		this.umlMode = umlMode;
	}


	public String getName() {
		return name;
	}

	public boolean getUmlMode() {
		return umlMode;
	}
	
	public String toString() {
		return name + " [" + (umlMode? "UML++" : "FMMLx") + "]";
	}

}
