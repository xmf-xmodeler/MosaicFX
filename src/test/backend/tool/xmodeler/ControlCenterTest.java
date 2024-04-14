package test.backend.tool.xmodeler;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import test.util.ControlCenterTestUtils;
import test.util.TestUtils;
import test.util.test_templates.XModelerTestTemplate;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxObject;

/**
 * Example Backend test. Please check logic and refactor/ rewrite this class.
 */
public class ControlCenterTest extends XModelerTestTemplate {
	


	//use parameterized to test bad case for invalid name and double name
	@Test
	public void testProjectCreation() {
		String projectName = "testProject";
		controlCenterTestUtils.createProject(projectName);

		//think maybe about a better backend logic to request this
		TreeView<String> projectLV = (TreeView<String>) lookup("#projectTreeView").query();
		assertTrue(getAllItems(projectLV).contains(projectName));
	}
	
	@Test
	public void testDiagramCreation() {
		FmmlxDiagram diagram = controlCenterTestUtils.createRandomDiagram();
		int diagramId = diagram.getID();
		String diagramName = diagram.diagramName;
		
		
		
		FmmlxDiagram backendDiagram = FmmlxDiagramCommunicator.getCommunicator().getDiagram(diagramId);
		assertTrue(backendDiagram.diagramName.equals(diagramName));
		System.err.println(backendDiagram.getObjectsReadOnly().toString());
		assertTrue(backendDiagram.getObjectsReadOnly().isEmpty());
		
		
		
		//Move to frontend test
//		ListView<String> diagramLv = (ListView<String>) lookup("#diagramListView").query();
	//	assertTrue(diagramLv.getItems().contains(diagramName));
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
