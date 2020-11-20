package tool.xmodeler;

import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
//import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControlCenter extends Stage {
	
	private final ControlCenterClient controlCenterClient;
	private final ObservableList<String> categoryList = FXCollections.observableArrayList();
	private final ListView<String> categoryLV = new ListView<String>();
	private final ObservableList<String> projectList = FXCollections.observableArrayList();
	private final ListView<String> projectLV = new ListView<String>();
	private final ObservableList<String> modelList = FXCollections.observableArrayList();
	private final ListView<String> modelLV = new ListView<String>();
	private final ObservableList<String> diagramList = FXCollections.observableArrayList();
	private final ListView<String> diagramLV = new ListView<String>();
			
	public ControlCenter() {
		setTitle("XModeler ML Control Center");
		
		ControlCenterClient.init(this);
		controlCenterClient = ControlCenterClient.getClient();

		VBox vBox = new VBox();
		HBox hBox = new HBox();
		GridPane grid = new GridPane();
				
		Button newCategorie = new Button("new");
		Label categorieLabel = new Label("Categories");
		
				
		Button newProject = new Button("new");
		Label projectLabel = new Label("Projects");
		CreatedModifiedGridPane projectGridPane = new CreatedModifiedGridPane();
		
		Button newModel = new Button("new");
		Label modelLabel = new Label("Models");
		CreatedModifiedGridPane modelGridPane = new CreatedModifiedGridPane();
		
		Button newDiagram = new Button("new");
		Label diagramLabel = new Label("Diagrams");
		CreatedModifiedGridPane diagramsGridPane = new CreatedModifiedGridPane();
		
		Menu file = new Menu("File");
		Menu browsers = new Menu("Browser");
		Menu tools = new Menu("Tools");
		Menu windows = new Menu("Windows");
		Menu settings = new Menu("Settings");
		Menu help = new Menu ("Help");
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(file, browsers, tools, windows, settings, help);
		HBox.setHgrow(menuBar, Priority.ALWAYS);
		hBox.getChildren().add(menuBar);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(categorieLabel, 1, 1);
		grid.add(newCategorie, 1, 1);
		GridPane.setHalignment(newCategorie, HPos.RIGHT);
		categoryLV.setPrefSize(200, 150);
		grid.add(categoryLV, 1, 2);
	
		grid.add(projectLabel, 2, 1);
		grid.add(newProject, 2, 1);
		GridPane.setHalignment(newProject, HPos.RIGHT);
		projectLV.setPrefSize(200, 150);
		grid.add(projectLV, 2, 2);
		grid.add(projectGridPane, 2, 3);
	
		grid.add(modelLabel, 3, 1);
		grid.add(newModel, 3, 1);
		GridPane.setHalignment(newModel, HPos.RIGHT);
		modelLV.setPrefSize(200, 150);
		grid.add(modelLV, 3, 2);
		grid.add(modelGridPane, 3, 3);
		
		grid.add(diagramLabel, 4, 1);
		grid.add(newDiagram, 4, 1);
		GridPane.setHalignment(newDiagram, HPos.RIGHT);
		diagramLV.setPrefSize(200, 150);
		grid.add(diagramLV, 4, 2);
		grid.add(diagramsGridPane, 4, 3);
		
		init();
		categoryLV.getSelectionModel().selectedItemProperty().addListener((prop, old, NEWW)->categorySelected(NEWW));
		
		vBox.getChildren().addAll(hBox, grid);
		Scene scene = new Scene(vBox, 900, 300);
		setScene(scene);
		
		newProject.setOnAction((e) -> {controlCenterClient.getAllProjects();});
	}
	
	private void categorySelected(String nEWW) {
		;
	}

	private void init() {
		categoryLV.getItems().clear();
		categoryLV.getItems().addAll(controlCenterClient.getAllCategories());
		}

	public Stage getStageForConsole() {
		return new Stage();
	}
	
	public Stage getStageForEditor() {
		return new Stage();
	}
	
	private static class CreatedModifiedGridPane extends GridPane{
		
		Label modified=new Label("31-03-1999");
		Label created = new Label("31-03-2000");
				
		private CreatedModifiedGridPane() {
			
			this.add(new Label("created: "), 1, 1);
			this.add(created, 2, 1);
			this.add(new Label("modified: "), 1, 2);
			this.add(modified, 2, 2);
			
		}
		
		private void setModified(String modified) {
			this.modified.setText(modified);
		}
		private void setCreated(String created) {
			this.created.setText(created);
		}
	}

	public void setAllProjects(Vector<String> vec) {
		projectLV.getItems().clear();
		projectLV.getItems().addAll(vec);
		
	}
	
}
