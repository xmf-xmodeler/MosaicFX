package tool.clients.workbench;

import java.util.Optional;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tool.clients.EventHandler;
import tool.clients.diagrams.DiagramClient;
import tool.clients.editors.EditorClient;
import tool.console.ConsoleView;
import xos.Message;
import xos.Value;

public class SceneContainer {

	private final Node node;
	boolean showAsStage;
	private final String name;
	private final TabPane tabPane;
	private final EventHandler eventHandler;
	private final String id;

	private SceneContainer(Node node, boolean showAsStage, String name, TabPane tabPane, EventHandler eventHandler, String id) {

		this.tabPane = tabPane;
		this.node = node;
		this.showAsStage = showAsStage;
		this.name=name;
		this.eventHandler=eventHandler;
		this.id=id;
	}	
	
	public static SceneContainer createAndShow(Node node, boolean showAsStage, String name, TabPane tabPane, EventHandler eventHandler, String id) {
		
		SceneContainer sceneContainer = new SceneContainer(node, showAsStage, name, tabPane, eventHandler,id);

		if (showAsStage)
			sceneContainer.createStage();
		else
			sceneContainer.createTab();
	
		return null;
	
	
	}
	

	private void createStage() {
		  Stage stage = new Stage();
	      BorderPane border = new BorderPane();
		  border.setCenter(node);
		  Scene scene = new Scene(border, 1000, 605);
		  stage.setScene(scene);
		  stage.setTitle(name);
		  stage.show();
		  stage.setOnCloseRequest((e)->closeScene(stage,e));
	}

	private void createTab() {
		Tab tab = new Tab(name);
		tab.setTooltip(new Tooltip(name)); 
		//TODO tooltip: Tab.setTooltip.setText() 
		tab.setContent(node);
		tab.setClosable(true);
		EditorClient.tabs.put(id, tab);
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().selectLast();
		tab.setOnCloseRequest((e)->closeTab(tab,e));
	}

		
	private void closeTab(Tab item, Event wevent) { 
		// Careful because the diagrams and files share the same tab folder...
		
		if (id != null && (EditorClient.editors.containsKey(id) || EditorClient.browsers.containsKey(id))) {

			Alert alert = new Alert(AlertType.CONFIRMATION);

			ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
			ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
			ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
			alert.setTitle("Open tab in separate window instead?");
			alert.setHeaderText(null);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get().getButtonData() == ButtonData.YES) {
				EditorClient.tabs.remove(id);
				createStage();

			} else if (result.get().getButtonData() == ButtonData.CANCEL_CLOSE) {
				wevent.consume();
			} else {
				Message message = eventHandler.newMessage("textClosed", 1);
				message.args[0] = new Value(id);
				eventHandler.raiseEvent(message);
				EditorClient.editors.remove(id);
				EditorClient.browsers.remove(id);
				EditorClient.tabs.remove(id);
			}
		} else {
			DiagramClient.theClient().close(id); // TODO: consider reimplementing in javafx
		}
	}
	
	private void closeScene(Stage stage, Event wevent) { 
		// Careful because the diagrams and files share the same tab folder...
		
		if (id != null && (EditorClient.editors.containsKey(id) || EditorClient.browsers.containsKey(id))) {

			Alert alert = new Alert(AlertType.CONFIRMATION);

			ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
			ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
			ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
			alert.setTitle("Open stage as tab in editor instead?");
			alert.setHeaderText(null);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get().getButtonData() == ButtonData.YES) {
				createTab();
			} else if (result.get().getButtonData() == ButtonData.CANCEL_CLOSE) {
				wevent.consume();
			} else {
				Message message = eventHandler.newMessage("textClosed", 1);
				message.args[0] = new Value(id);
				eventHandler.raiseEvent(message);
				EditorClient.editors.remove(id);
				EditorClient.browsers.remove(id);
			}
		} else {
			DiagramClient.theClient().close(id); // TODO: consider reimplementing in javafx
		}
	}

}
