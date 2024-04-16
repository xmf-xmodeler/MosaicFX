package test.util;

import java.util.Vector;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.Level;
import tool.clients.fmmlxdiagrams.Level.UnparseableException;

public class FmmlxDiagramTestUtils {
	
	/**
	 * Creates "normal" meta class.
	 * 
	 * @param diagram in which the metaClass should be created
	 * @param level integer value of the level (this function only produces non contingent classes)
	 * @param className
	 */
	public static void createMetaClass(FmmlxDiagram diagram, Integer level, String className) {
		Level classLevel = null;
		try {
			classLevel = Level.parseLevel(level.toString());
		} catch (UnparseableException e) {
			throw new RuntimeException(e);
		}
		diagram.getComm().addMetaClass(diagram.getID(), TestUtils.getShortRandomId(), classLevel, new Vector<>(),
				false, false, (0), (0), false);
	}
}