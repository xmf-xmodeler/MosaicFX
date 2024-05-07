package tool.xmodeler.tool_introduction;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;

public class DiagramConditionChecks {
	
	public static boolean containsClass(FmmlxDiagram diagram, String name) {
		FmmlxObject obj = diagram.getObjectByName(name);
		return (obj != null);
	}

}
