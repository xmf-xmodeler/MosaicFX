package test.frontend;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import test.util.test_templates.XModelerTestTemplate;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxObject;

/**
 * Example frontendTest. Please rewrite this code.
 */
public class CreateClassTest extends XModelerTestTemplate {



	@Test
	public void createClass() {
		String projectName = createProject();
		try {
			// Just for presentation purposes. You should not wait in real testcases.
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		TreeView<String> projectLV = (TreeView<String>) lookup("#projectTreeView").query();
		TreeItem<String> childItem = findTreeItemByText(projectName, projectLV.getRoot());
		if (childItem != null) {
			clickOn(childItem.getValue());
		}
		clickOn((Button) lookup("#createNewDiagramButton").query());
		String diagramName = "xyz";
		write(diagramName);
		clickOn("OK");
		doubleClickOn("xyz");
		rightClickOn();
		moveTo("Add");
		clickOn("Class...");
		TextField nameF = (TextField) lookup("#metaClassNameField").query();
		String className = "ClassA";
		clickOn(nameF).write(className);
		TextField levelF = (TextField) lookup("#levelTextField").query();
		clickOn(levelF).write("1");
		clickOn("OK");
		clickOn(MouseButton.PRIMARY);

		try {
		
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		FmmlxDiagram diagram = FmmlxDiagramCommunicator.getCommunicator().getDiagram(0);
		//raises exception if no object is found	
		String objPath = "Root" + "::" + projectName + "::" + className;
		FmmlxObject obj = diagram.getObjectByPath(objPath);
		assertTrue(obj != null);
	}

	private String createProject() {
		String projectName = "testProj";
		Button button = (Button) lookup("#createProjectButton").query();
		clickOn(button);
		write(projectName);
		clickOn("OK");
		return projectName;
	}

	private TreeItem<String> findTreeItemByText(String text, TreeItem<String> root) {
		if (root.getValue().equals(text)) {
			return root;
		}
		for (TreeItem<String> child : root.getChildren()) {
			TreeItem<String> result = findTreeItemByText(text, child);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
}