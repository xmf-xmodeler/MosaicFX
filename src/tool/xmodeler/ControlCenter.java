package tool.xmodeler;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableListValue;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.classbrowser.ModelBrowser;
import tool.clients.fmmlxdiagrams.dialogs.InputChecker;
import tool.clients.fmmlxdiagrams.graphics.ConcreteSyntaxWizard;
import tool.clients.workbench.WorkbenchClient;
import tool.helper.IconGenerator;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.Vector;

public class ControlCenter extends Stage {
	
	private final ControlCenterClient controlCenterClient;
	//private final ObservableList<String> categoryList = FXCollections.observableArrayList();
	//private final ListView<String> categoryLV = new ListView<String>();
	//private final ObservableList<String> projectList = FXCollections.observableArrayList();
	private final TreeView<String> projectTree = new TreeView<String>();
	private final ListView<String> projectLV = new ListView<String>();
	//private final ObservableList<String> modelList = FXCollections.observableArrayList();
	private final ListView<String> modelLV = new ListView<String>();
	//private final ObservableList<String> diagramList = FXCollections.observableArrayList();
	private final ListView<String> diagramLV = new ListView<String>();
	//private ModelBrowser stage;
	private MenuBar menuBar;
	private HashMap<String, ModelBrowser> modelBrowsers = new HashMap<>();

	public ControlCenterClient getControlCenterClient() {
		return controlCenterClient;
	}

	public ControlCenter() {
		setTitle("XModeler ML Control Center");
		getIcons().add(IconGenerator.getImage("shell/mosaic32"));
		ControlCenterClient.init(this);
		controlCenterClient = ControlCenterClient.getClient();
		
		final Image image = new Image(new File("resources/gif/Projects/Project.gif").toURI().toString());
		
		VBox vBox = new VBox();
		HBox hBox = new HBox();
		GridPane grid = new GridPane();
				
		Button newCategorie = new Button("new");
		newCategorie.setDisable(true);
//		Label categorieLabel = new Label("Categories");
		
		Button refreshAll = new Button("refresh");
		Button newProject = new Button("Create Project");
		Label projectLabel = new Label("Projects");
		CreatedModifiedGridPane projectGridPane = new CreatedModifiedGridPane();
		
		Button newModel = new Button("Create Model");
		newModel.setDisable(true);
		
		Label modelLabel = new Label("Models");
		CreatedModifiedGridPane modelGridPane = new CreatedModifiedGridPane();
		
		Button concreteSyntaxWizardStart = new Button("Concrete Syntax Wizard"); //Button for ConcreteSyntaxWizard
		concreteSyntaxWizardStart.setOnAction(e -> { 
			ConcreteSyntaxWizard wizard = new ConcreteSyntaxWizard();
			try {
				wizard.start(new Stage());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		Button newDiagram = new Button("Create Diagram");
		newDiagram.setDisable(true);
		newDiagram.disableProperty().bind(
			    Bindings.isNull(modelLV.getSelectionModel().selectedItemProperty())
			);
		Label diagramLabel = new Label("Diagrams");
		CreatedModifiedGridPane diagramsGridPane = new CreatedModifiedGridPane();
		
		newDiagram.setOnAction(e -> { 
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Create new Diagram");
			dialog.setContentText("New diagram name:");
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				if(InputChecker.isValidIdentifier(result.get())) {
			    	Integer diagramID = FmmlxDiagramCommunicator.getCommunicator().createDiagram(
			        modelLV.getSelectionModel().getSelectedItem(), 
			        result.get(), "", FmmlxDiagramCommunicator.DiagramType.ClassDiagram);
			    System.err.println("diagramID "  +diagramID);
			    }  else {
					new Alert(AlertType.ERROR, 
							"\"" + result.get() + "\" is not a valid identifier.", 
							new ButtonType("Damned", ButtonData.YES)).showAndWait();
				};
			controlCenterClient.getDiagrams(modelLV.getSelectionModel().getSelectedItem());
		}}); 
		diagramLV.setOnMouseClicked(me -> {

		        if (me.getClickCount() == 2 && me.getButton() == MouseButton.PRIMARY) {
		           String selectedDiagramString = diagramLV.getSelectionModel().getSelectedItem();
		           if(selectedDiagramString != null) {
		        	   String selectedModelString = modelLV.getSelectionModel().getSelectedItem();
			           if(selectedModelString != null) {
			        	  FmmlxDiagramCommunicator.getCommunicator().openDiagram(
			        			  selectedModelString, 
			        			  selectedDiagramString);
			        	  
			           }
		           }
		        }
	
		    });
		
//		Menu file = new Menu("File");
//		Menu browsers = new Menu("Browser");
//		Menu tools = new Menu("Tools");
//		Menu windows = new Menu("Windows");
//		Menu settings = new Menu("Settings");
//		Menu help = new Menu ("Help");
		menuBar = new MenuBar();
		//menuBar.getMenus().addAll(file, browsers, tools, windows, settings, help);
		HBox.setHgrow(menuBar, Priority.ALWAYS);
		hBox.getChildren().add(menuBar);
		grid.setHgap(10);
		grid.setVgap(10);
		//grid.add(categorieLabel, 1, 1);
		//grid.add(newCategorie, 1, 1);
		GridPane.setHalignment(newCategorie, HPos.RIGHT);
		//categoryLV.setPrefSize(200, 150);
		//grid.add(categoryLV, 1, 2);
	
		grid.add(projectLabel, 2, 1);
		grid.add(refreshAll, 2, 1);
		GridPane.setHalignment(refreshAll, HPos.RIGHT);
		grid.add(newProject, 2, 1);
		GridPane.setHalignment(newProject, HPos.CENTER);
		//projectLV.setPrefSize(250, 150);
		//grid.add(projectLV, 2, 2);
		projectTree.setPrefSize(250, 150);
		grid.add(projectTree, 2, 2);
		grid.add(projectGridPane, 2, 3);
	
		grid.add(modelLabel, 3, 1);
		grid.add(newModel, 3, 1);
		GridPane.setHalignment(newModel, HPos.RIGHT);
		modelLV.setPrefSize(250, 150);
		grid.add(modelLV, 3, 2);
		grid.add(modelGridPane, 3, 3);
		
		modelLV.setOnMouseClicked(e->{if (e.getClickCount()==2 && e.getButton()==MouseButton.PRIMARY) modelDoubleClick(e);});
		modelLV.getSelectionModel().selectedItemProperty().addListener((prop, old, NEWW)->newModelSelected(NEWW));
		TreeItem loading = new TreeItem("Loading");
		projectTree.setRoot(loading);
		
		grid.add(diagramLabel, 4, 1);
		grid.add(newDiagram, 4, 1);
		GridPane.setHalignment(newDiagram, HPos.RIGHT);
		diagramLV.setPrefSize(250, 150);
		grid.add(diagramLV, 4, 2);
		grid.add(diagramsGridPane, 4, 3);
		grid.add(concreteSyntaxWizardStart, 4, 4); //Buton for ConcreteSyntaxWizard

		init();
		//categoryLV.getSelectionModel().selectedItemProperty().addListener((prop, old, NEWW)->categorySelected(NEWW));
		//projectLV.getSelectionModel().selectedItemProperty().addListener((prop, old, NEWW)->controlCenterClient.getProjectModels(NEWW));
		projectTree.getSelectionModel().selectedItemProperty().addListener((prop, old, NEWW)->controlCenterClient.getProjectModels(getProjectPath(projectTree.getSelectionModel().getSelectedItem())));
		
		projectTree.setCellFactory(new Callback<TreeView<String>,TreeCell<String>>(){
            @Override
            public TreeCell<String> call(TreeView<String> p) {
                return new TreeCell<String>() {
                	@Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
             
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                        	setText(getTreeItem().getValue());
                            if (getTreeItem().isLeaf()) {
                            	setGraphic(new ImageView(image));
                            } else {
                            	setGraphic(null);
                            }
                        }
                    }
                };
            }
        });

		vBox.getChildren().addAll(hBox, grid);
		Scene scene = new Scene(vBox, 800, 300);
		setScene(scene);
		this.setOnShown((event) -> controlCenterClient.getAllCategories());
		refreshAll.setOnAction((event) -> controlCenterClient.getAllProjects());
		newProject.setOnAction((event) -> {controlCenterClient.createNewProject();controlCenterClient.getAllProjects();});	
		
		this.setOnCloseRequest(event -> {
			if (PropertyManager.getProperty("IGNORE_SAVE_IMAGE", true)) {
				Runtime.getRuntime().halt(0);
			} else {
			    WorkbenchClient.theClient().shutdownEvent();
			}
			event.consume();
		});
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	controlCenterClient.getAllProjects();
		            } 
		        }, 2500
		);
	}



	private void newModelSelected(String modelName) {
		controlCenterClient.getDiagrams(modelName);
	}



	public MenuBar getMenuBar() {
		return menuBar;
	}

	private void modelDoubleClick(MouseEvent e) {
		showModelBrowser(projectLV.getSelectionModel().getSelectedItem(), modelLV.getSelectionModel().getSelectedItem(), modelLV.getItems());
	}
	
	public ModelBrowser showModelBrowser(String project, String model, Collection<String> models) {
		ModelBrowser modelBrowser = modelBrowsers.get(model);
		if(modelBrowser==null) {
			modelBrowser = new ModelBrowser(project, model, models);
			modelBrowsers.put(model, modelBrowser);
			modelBrowser.setOnCloseRequest((b)-> modelBrowsers.remove(model));
			modelBrowser.show();	
		} else {
			if(modelBrowser.isIconified()) modelBrowser.setIconified(false);
			modelBrowser.toFront();
		}
		return modelBrowser;
	}

//	private void categorySelected(String nEWW) {
//		
//	}
	
	public void getModelsFromProject() {
		
	}
	
	public void getDiagramsFromModel() {
		
	}
	
	private void init() {
		//categoryLV.getItems().clear();
		//categoryLV.getItems().addAll(controlCenterClient.getAllCategories());
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
			//Labels for creation & last modification of projects, models & diagrams! ToDo!
			///this.add(new Label("created: "), 1, 1);
			//this.add(created, 2, 1);
			//this.add(new Label("modified: "), 1, 2);
			//this.add(modified, 2, 2);
		}
		
		private void setModified(String modified) {
			this.modified.setText(modified);
		}
		private void setCreated(String created) {
			this.created.setText(created);
		}
	}

	public void setAllProjects(Vector<String> vec) {
		Platform.runLater(()->{
		TreeItem<String> root = new TreeItem<>("root");
		
		
		for (String projectString : vec) {
			String[] projectPath = projectString.split("::");
			TreeItem<String> currentTreeItemPosition = root;
			for (String s : projectPath) {
				TreeItem<String> newTreeItem = null;
				for (TreeItem<String> tempItem : currentTreeItemPosition.getChildren()) {
					if (tempItem.getValue().equals(s)) {
						newTreeItem = tempItem;
					}
				}
				if (newTreeItem == null) {
					newTreeItem = new TreeItem<>(s);
					currentTreeItemPosition.getChildren().add(newTreeItem);

				}
				currentTreeItemPosition = newTreeItem;


//				if(currentTreeItemPosition.getChildren().contains(newTreeItem)){
//					
//				}
			}
			projectTree.setRoot(root);
			projectTree.setShowRoot(false);
			
			for (TreeItem<String> temp : root.getChildren()) {
				temp.setExpanded(true);
				for (TreeItem<String> tempLvl2 : temp.getChildren()) {
					tempLvl2.setExpanded(true);
				}
			}
		}
		
		//projectLV.getItems().clear();
		//projectLV.getItems().addAll(vec);
		});
	}
	
	public void setAllCategories(Vector<String> vec) {
		Platform.runLater(()->{
//		categoryLV.getItems().clear();
//		categoryLV.getItems().addAll(vec);
		});
	}
	
	public void setProjectModels(Vector<String> vec) {
		Platform.runLater(()->{
			modelLV.getItems().clear();
			modelLV.getItems().addAll(vec);
			if(vec.size()>=1) {
				modelLV.getSelectionModel().selectFirst();
			}
		});
		
	}
	
	public String getProjectPath(TreeItem<String> item) {
		if(item==null) return null;
		StringBuilder projectPath= new StringBuilder(item.getValue());
		while (item.getParent()!=projectTree.getRoot()) {
			item=item.getParent();
			projectPath.insert(0, item.getValue() + "::");
		}
		return projectPath.toString();
	}

	public void setDiagrams(Vector<String> vec) {
		Platform.runLater(()->{
			diagramLV.getItems().clear();
			diagramLV.getItems().addAll(vec);
		});
	}
}
