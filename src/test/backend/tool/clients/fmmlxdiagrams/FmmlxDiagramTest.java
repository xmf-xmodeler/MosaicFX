package test.backend.tool.clients.fmmlxdiagrams;

import static org.junit.Assert.assertTrue;

import java.util.Vector;

import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.application.Platform;
import javafx.stage.Stage;
import test.util.ControlCenterTestUtils;
import test.util.TestUtils;
import test.util.test_templates.DiagramTestTemplate;
import test.util.test_templates.XModelerTestTemplate;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Level;
import tool.clients.fmmlxdiagrams.Level.UnparseableException;

public class FmmlxDiagramTest extends DiagramTestTemplate {
	
	//parameterize for different combinations of metaclasses
	@Test
	public void createMetaClass() {
		String metaCassName = "testClass";
		String metaClassLevel = "1";
		Level level = null;
		try {
			level = Level.parseLevel(metaClassLevel);
		} catch (UnparseableException e) {
			throw new RuntimeException(e);
		}
		getCurrentDiagram().getComm().addMetaClass(getCurrentDiagram().getID(), metaCassName, level, new Vector<>(), false, false, (0), (0),
				false);
		getCurrentDiagram().updateDiagram();

		TestUtils.waitWithoutCatch(1000);

		// raises exception if no object is found
		String objPath = getCurrentDiagram().getPackagePath() + "::" + metaCassName;
		FmmlxObject obj = getCurrentDiagram().getObjectByPath(objPath);
		assertTrue(obj != null);
		assertTrue(obj.getLevel().getMaxLevel() == 1);
		assertTrue(obj.getLevel().getMinLevel() == 1);
	}
}