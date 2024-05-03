package tool.xmodeler.tool_introduction;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;

public class SucessCondition {
	
	public static boolean checkSucessCondition(FmmlxDiagram diagram) {
		switch (diagram.getViewPane().getDiagramViewState().getPrecedence()) {
		case 1:
			return isClassCreated(diagram);
		
		default:
			return false;
		}
	}
		
	static boolean isClassCreated(FmmlxDiagram diagram) {
		String objPath = diagram.getPackagePath() + "::" + "Movie";
		
		FmmlxObject obj = null;
		try {
			obj = diagram.getObjectByPath(objPath);				
		} catch (Exception e) {
			return false;
		}
		return (obj != null);
	}

}
