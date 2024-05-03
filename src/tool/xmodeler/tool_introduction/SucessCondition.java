package tool.xmodeler.tool_introduction;

import java.util.Vector;

import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;

public class SucessCondition {

	public static boolean checkSucessCondition(FmmlxDiagram diagram) {
		switch (diagram.getViewPane().getDiagramViewState().getPrecedence()) {
		case 1:
			return isClassCreated(diagram);
		case 2:
			return areAttributesAddes(diagram);
		case 3:
			return isSecondClassCreated(diagram);

		default:
			return false;
		}
	}

	private static boolean isSecondClassCreated(FmmlxDiagram diagram) {
		if (!containsClass(diagram, "MovieShowing")) {
			return false;
		}
		Vector<FmmlxAttribute> ownAttributes = requestObject(diagram, "MovieShowing").getOwnAttributes();
		boolean containsShowDate = false;
		for (FmmlxAttribute fmmlxAttribute : ownAttributes) {
			if (!containsShowDate) {
				containsShowDate = fmmlxAttribute.hasNameAndType("showDate", "Date");				
			}
		}
		return containsShowDate;
	}

	private static boolean areAttributesAddes(FmmlxDiagram diagram) {
		FmmlxObject obj = requestObject(diagram, "Movie");
		Vector<FmmlxAttribute> ownAttributes = obj.getOwnAttributes();
		boolean containsTitle = false;
		boolean containsDurationInMinutes = false;
		for (FmmlxAttribute fmmlxAttribute : ownAttributes) {
			if (!containsDurationInMinutes) {
				containsDurationInMinutes = fmmlxAttribute.hasNameAndType("durationInMinutes", "Integer");
			}
			if (!containsTitle) {
				containsTitle = fmmlxAttribute.hasNameAndType("title", "String");			
			}
		}
		return containsTitle && containsDurationInMinutes;
	}

	static boolean isClassCreated(FmmlxDiagram diagram) {
		return containsClass(diagram, "Movie");
	}

	private static boolean containsClass(FmmlxDiagram diagram, String name) {
		FmmlxObject obj = requestObject(diagram, name);
		return (obj != null);
	}

	private static FmmlxObject requestObject(FmmlxDiagram diagram, String name) {
		String objPath = diagram.getPackagePath() + "::" + name;
		try {
			return diagram.getObjectByPath(objPath);
		} catch (Exception e) {
			return null;
		}
	}
}