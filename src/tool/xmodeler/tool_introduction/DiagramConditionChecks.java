package tool.xmodeler.tool_introduction;

import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxLink;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;

public class DiagramConditionChecks {
	
	public static boolean containsClass(FmmlxDiagram diagram, String name) {
		FmmlxObject obj = diagram.getObjectByName(name);
		return (obj != null);
	}
	
	public static boolean containsLink(FmmlxDiagram diagram, String source, String target, String linkName) {
		FmmlxLink link = FmmlxLink.getFmmlxLink(diagram, source, target, linkName);
		if (link == null) {
			return false;
		}
		return true;
	}
	
	public static boolean hasAttributeOfType(FmmlxObject obj, String attrName, String type) {
		FmmlxAttribute attr = obj.getAttributeByName(attrName);
		if (attr == null) {
			return false;
		}
		return attr.getType().equals(type);
	}
	
	public static boolean hasMatchingSlotValue(FmmlxDiagram diagram, String objectName, String slotName, String slotValue) {
		FmmlxObject obj = diagram.getObjectByName(objectName);
		boolean hasCorrectSlotValue;
		try {
			FmmlxSlot slot = obj.getSlot(slotName);
			hasCorrectSlotValue = slot.getValue().equals(slotValue);
		} catch (Exception e) {
			// if obj or slot null
			return false;
		}
		return hasCorrectSlotValue;
	}
	
	/**
	 * Checks if diagram has issues
	 * @return true if no issues
	 */
	public static boolean hasIssues(FmmlxDiagram diagram) {
		return !diagram.getIssues().isEmpty();
	}
}
