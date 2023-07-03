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
	OBJECTS("Objects"),
	VIEWS("Views"),
	VIEW("View"),
	OBJECT("Object"),
	DIAGRAMS_DISPLAY_PROPERTIES("DiagramsDisplayProperties");
	
    public final String name;

    private XMLTags(String name) {
	this.name = name;
    }

	public String getName() {
		return name;
	}
}