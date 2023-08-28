package tool.helper.persistence;

public enum XMLTags {
	
	ROOT("XModelerPackage"),
	DIAGRAMS("Diagrams"),
	MODEL("Model"),
	EDGES("Edges"),
	DIAGRAM("Diagram"),
	EDGE("Edge"),
	INTERMEDIATE_POINTS("IntermediatePoints"),
	INTERMEDIATE_POINT("IntermediatePoint"),
	LABELS("Labels"),
	LABEL("Label"),
	INSTANCES("Instances"),
	VIEWS("Views"),
	VIEW("View"),
	INSTANCE("Instance"),
	DIAGRAM_DISPLAY_PROPERTIES("DiagramDisplayProperties");
	
    public final String name;

    private XMLTags(String name) {
	this.name = name;
    }

	public String getName() {
		return name;
	}
}