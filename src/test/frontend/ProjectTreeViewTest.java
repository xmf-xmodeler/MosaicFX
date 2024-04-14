package test.frontend;

import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import test.util.test_templates.XModelerTestTemplate;
import tool.xmodeler.XModeler;

/**
 * Example frontend test. Please rewrite this code.s
 */
public class ProjectTreeViewTest extends XModelerTestTemplate {


	@Test
	public void checkProjectNameInTreeView() {
		Button button = (Button) lookup("#createProjectButton").query();
		clickOn(button);
		write("frontEndTest");
		clickOn("OK");

		try {
			// dangerous to wait fix amount of time. Better way should be found
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		TreeView<String> projectLV = (TreeView<String>) lookup("#projectTreeView").query();
		assertTrue(getAllItems(projectLV).contains("frontEndTest"));
	}

	public static <T> List<T> getAllItems(TreeView<T> treeView) {
		List<T> items = new ArrayList<>();
		TreeItem<T> root = treeView.getRoot();
		collectItems(root, items);
		return items;
	}

	private static <T> void collectItems(TreeItem<T> item, List<T> items) {
		if (item != null) {
			items.add(item.getValue());
			for (TreeItem<T> child : item.getChildren()) {
				collectItems(child, items);
			}
		}
	}
}