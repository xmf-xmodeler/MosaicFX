package test.stress;

import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.stage.Stage;
import test.util.FmmlxDiagramTestUtils;
import test.util.TestUtils;
import test.util.test_templates.XModelerTestTemplate;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class CreateMultipleDiagrams extends XModelerTestTemplate {

	//Tried this and it worked
	// Commented this test out because it should not run in normal test routine
	//@Test
	public void createMultipleDiagrams() {
		for (int i = 0; i < 150; i++) {
			FmmlxDiagram diagram = controlCenterTestUtils.createRandomDiagram();
			for (int j = 0; j < 5; j++) {
				FmmlxDiagramTestUtils.createMetaClass(diagram, 1, TestUtils.getShortRandomId());
				TestUtils.waitWithoutCatch(500);
			}
			diagram.updateDiagram();
			Stage stage = TestUtils.getFocusedStage();
			Platform.runLater(() -> {
				stage.close();
			});
			System.err.println(i);
		}
		TestUtils.keepAliveAfterTest();
	}
}