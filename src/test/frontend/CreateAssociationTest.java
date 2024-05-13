package test.frontend;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import test.util.test_templates.XModelerTestTemplate;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class CreateAssociationTest extends XModelerTestTemplate {
	FmmlxDiagram diagram;
	@Test
	public void associationTest() {
		
		String projectName = createProject();
		
		try {
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
		
		diagram = FmmlxDiagramCommunicator.getCommunicator().getDiagram(0);

		createClass("KlasseA",2);
		createClass("KlasseB",2);	

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		createAssociation("testAssco","KlasseA", "KlasseB");
		

		//raises exception if no object is found	
		diagram = FmmlxDiagramCommunicator.getCommunicator().getDiagram(0);
		assertTrue(diagram.getAssociations().size()>0);
	}

	private String createProject() {
		String projectName = "testProj";
		Button button = (Button) lookup("#createProjectButton").query();
		clickOn(button);
		write(projectName);
		clickOn("OK");

		return projectName;
	}
	
	private void createClass(String className, Integer level) {
		rightClickOn();
		moveTo("Add");
		clickOn("Class...");
		TextField nameF = (TextField) lookup("#metaClassNameField").query();
		clickOn(nameF).write(className);
		TextField levelF = (TextField) lookup("#levelTextField").query();
		clickOn(levelF).write(level.toString());
		clickOn("OK");
		clickOn(MouseButton.PRIMARY);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void createAssociation(String name, String startName, String endName) {
		rightClickOn();
		moveTo("Add");
		moveTo("Class...");	//Workaround for the mouse slipping off
		clickOn("Association...");
		TextField nameF = (TextField) lookup("#associationNameField").query();
		clickOn(nameF).write(name);
		
		DialogPane assocDialog = (DialogPane) lookup("#associationDialog").query();
		ComboBox<FmmlxObject> assocStart = (ComboBox) lookup("#associationStartComboBox").query();
		ComboBox<FmmlxObject> assocEnd = (ComboBox) lookup("#associationEndComboBox").query();
		FmmlxObject start = null;
		FmmlxObject end = null;
		for(int i = 0;i<assocStart.getItems().size();i++) {
			if(((FmmlxObject) assocStart.getItems().get(i)).getName().equals(startName)) {
				start = (FmmlxObject) assocStart.getItems().get(i);
			}
			else if(((FmmlxObject) assocStart.getItems().get(i)).getName().equals(endName)) {
				end = (FmmlxObject) assocStart.getItems().get(i);
			}
		}

//		assocStart.setValue(start);
//		assocEnd.setValue(end);
	//	clickOn(assocStart.getItems().get(startI));
		//clickOn(assocEnd);
	//	clickOn(endName);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		ComboBox levelStart = (ComboBox) lookup("#sourceLevel").query();
		ComboBox levelEnd = (ComboBox) lookup("#targetLevel").query();
//		levelStart.setValue( start.getLevel().getMinLevel());
//		levelEnd.setValue( end.getLevel().getMinLevel());

//		clickOn(levelStart);
//		clickOn(levelStart.getItems().get(levelStart.getItems().size()-1).toString());
//		clickOn(levelEnd);
//		clickOn(levelEnd.getItems().get(levelEnd.getItems().size()-1).toString());
				try {
		Thread.sleep(500);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
		moveTo("OK");		
		clickOn("OK");
		clickOn(MouseButton.PRIMARY);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
