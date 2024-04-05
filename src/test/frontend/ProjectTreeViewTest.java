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
import tool.xmodeler.XModeler;

public class ProjectTreeViewTest extends ApplicationTest {

	@Override
	public void start(Stage stage) {
		String[] programmArgs = { "./ini3.txt" };
		XModeler.main(programmArgs);
		XModeler xModeler = new XModeler();
		try {
			xModeler.start(new Stage());
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	@Test
	public void checkProjectNameInTreeView() {
		Button button = (Button) lookup("#createProjectButton").query();
		clickOn(button);
		write("testProj");
		clickOn("OK");

		try {
			// dangerous to wait fix amount of time. Better way should be found
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		TreeView<String> projectLV = (TreeView<String>) lookup("#projectTreeView").query();
		assertTrue(getAllItems(projectLV).contains("testProj"));
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