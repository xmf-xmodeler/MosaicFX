package tool.xmodeler.didactic_ml.diagram_preperation_actions;

import java.util.Vector;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.Level;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.Level.UnparseableException;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;

/**
 * If in any stage of the tool introduction automatically added elements are
 * needed the functions that perform these actions should be defined in a subclass.
 * Generic functions are offered by this superclass.
 */
public abstract class DiagramPreparationActions {
	
	/**
	 * In this function the actions should be defined that should be executed before reaching the next tasks.
	 * For example structure see ToolIntroductionPreperation.
	 * In the case statement use the precedence before the task description, you will need the elements for.
	 * If in task 6 things should be there, the case must be 5.
	 * 
	 * @param diagram on which these actions are performed.
	 */
	public abstract void prepair(FmmlxDiagram diagram);

	static void createAttributeOnLevelNull(FmmlxDiagram diagram, String className, String attName,
			String type) {
		Multiplicity multOne = new tool.clients.fmmlxdiagrams.Multiplicity(1, 1, true, false, false);
		diagram.getComm().addAttribute(diagram.getID(), diagram.getClassPath(className), attName, getLevelNull(), type,
				multOne, true, false, false);
	}

	static Level getLevelNull() {
		Level levelNull;
		try {
			levelNull = Level.parseLevel("0");
		} catch (UnparseableException e) {
			throw new RuntimeException(e);
		}
		return levelNull;
	}

	// TODO: This function is copied from test.utils please reference this. Call the other function with overloading.
	static void createMetaClass(FmmlxDiagram diagram, Integer level, String className, int[] position) {
		Level classLevel = null;
		try {
			classLevel = Level.parseLevel(level.toString());
		} catch (UnparseableException e) {
			throw new RuntimeException(e);
		}
		diagram.getComm().addMetaClass(diagram.getID(), className, classLevel, new Vector<>(), false, false, position[0], position[1],
				false);
	}
	
	static void addAssociationOnLevelNull(FmmlxDiagram diagram, String sourceName, String targetName, String assocName, Multiplicity targetToSourceMult, Multiplicity sourceToTargetMult) {
		diagram.getComm().addAssociation(diagram.getID(), sourceName, targetName,
				sourceName.toLowerCase(), targetName.toLowerCase(), assocName,
				diagram.getDefaultAssociation().path, targetToSourceMult, sourceToTargetMult, 0, 0, 0, 0, false, true,
				false, false, null, null, null, null);
	}
	
	static void updateSlot(FmmlxDiagram diagram, FmmlxObject obj, String slotName, String newValue) {
		FmmlxSlot slot = obj.getSlot(slotName);
		diagram.getComm().changeSlotValue(diagram.getID(), obj.getName(), slot.getName(), newValue);
	}
}