package test.util.test_templates;

import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.jupiter.api.BeforeAll;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import test.util.ControlCenterTestUtils;
import test.util.TestUtils;
import tool.xmodeler.XModeler;

/**
 * This is the most generic Template in the XModelerTestFramework. Every other
 * test should inherit from this class. The class defined the basic test-setup
 * and tear-down mechanism. All waiting times in the Framework are a result of
 * experiments.
 * 
 * Please mind to set the needed environmental variables before starting a test
 * class. XMODELER_TEST=true
 * 
 * Please mind to use the right virtual machine arguments for testing.
 * -ea
 * --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web
 *
 * If you want to execute tests in headless modus please add the headless
 * environmental variable.
 * HEADLESS=true
 *
 */
public class XModelerTestTemplate extends ApplicationTest {

	protected ControlCenterTestUtils controlCenterTestUtils = new ControlCenterTestUtils();

	@BeforeAll
	public static void initXModeler() {
		String envVariableValue = System.getenv("HEADLESS");
		if (envVariableValue != null && envVariableValue.equals("true")) {
			setHeadlessProperties();
		}
		String[] programmArgs = { "./ini3.txt" };
		XModeler.main(programmArgs);
	}

	private static void setHeadlessProperties() {
		System.setProperty("testfx.robot", "glass");
		System.setProperty("testfx.headless", "true");
		System.setProperty("prism.order", "sw");
		System.setProperty("prism.text", "t2k");
		System.setProperty("java.awt.headless", "true");

	}

	@Override
	public void start(Stage stage) {
		XModeler x = new XModeler();
		try {
			x.start(stage);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		TestUtils.waitWithoutCatch(4000);
	}

	@After
	public void cleanup() throws TimeoutException {
		FxToolkit.cleanupStages();
		release(new KeyCode[] {});
		release(new MouseButton[] {});
	}
}