package tool.helper.persistence;

/**
 * This Enum contains all Tag-names, that are used in the XML-export of packages.
 */
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
	DIAGRAM_DISPLAY_PROPERTIES("DiagramDisplayProperties"),
	IMPORTS("Imports"),
	PACKAGE_IMPORT("PackageImport"),
	
	//Tags for Notes
	NOTES("Notes"),
	NOTE("Note"),
	NOTEID("NoteId"),
	NOTECOLOR("NoteColor"),
	NOTECONTENT("NoteContent"),
	NOTEPOSITION("NotePosition"),
	
	XPOSITION("XPosition"),
	YPOSITION("YPosition")	
	;
	
	
    public final String name;

    private XMLTags(String name) {
	this.name = name;
    }

	public String getName() {
		return name;
	}
}