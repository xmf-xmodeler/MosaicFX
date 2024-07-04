package tool.xmodeler;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.Vector;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.classbrowser.ModelBrowser;
import tool.clients.fmmlxdiagrams.dialogs.InputChecker;
import tool.clients.fmmlxdiagrams.graphics.wizard.ConcreteSyntaxWizard;
import tool.helper.HowToDialog;
import tool.helper.IconGenerator;
import tool.helper.auxilaryFX.JavaFxButtonAuxilary;
import tool.helper.persistence.StartupModelLoader;
import tool.helper.user_properties.PropertyManager;
import tool.helper.user_properties.UserProperty;
import tool.xmodeler.tool_introduction.ToolIntroductionManager;

public class ControlCenter extends Stage {
	
	private final ControlCenterClient controlCenterClient;
	private final TreeView<String> projectTree = new TreeView<String>();
	private final ListView<String> projectLV = new ListView<String>();
	private final ListView<String> modelLV = new ListView<String>();
	private final ListView<String> diagramLV = new ListView<String>();
	private MenuBar menuBar;
	private HashMap<String, ModelBrowser> modelBrowsers = new HashMap<>();

	public ControlCenterClient getControlCenterClient() {
		return controlCenterClient;
	}

	public ControlCenter() {
		setTitle("XModeler ML Control Center");
		if(Boolean.parseBoolean((PropertyManager.getProperty(UserProperty.DIDACTIC_MODE.toString())))) {
		setTitle("XModeler UML Control Center");}
		getIcons().add(IconGenerator.getImage("shell/mosaic32"));
		ControlCenterClient.init(this);
		controlCenterClient = ControlCenterClient.getClient();
	
		VBox root = new VBox();
		menuBar = new ControlCenterMenuBar();
		GridPane grid = buildGridPane(); 
		root.getChildren().addAll(menuBar, grid);
		int toolWidth = Integer.valueOf(PropertyManager.getProperty("toolWidth"));
		if(Boolean.parseBoolean((PropertyManager.getProperty(UserProperty.DIDACTIC_MODE.toString())))) {
			toolWidth = Integer.valueOf(PropertyManager.getProperty("toolWidth"))-237;	//Adjustment for removed elements
		}
		int toolHeight = Integer.valueOf(PropertyManager.getProperty("toolHeight"));
		Scene scene = new Scene(root, toolWidth, toolHeight);
		setScene(scene);
				
		this.setOnShown((event) -> controlCenterClient.getAllCategories());
		setOnCloseRequest(closeEvent -> showCloseWarningDialog(closeEvent));
				
		controlCenterClient.getAllProjects();	
		/*
		 * There is the idea of starting a repository of models by startup. The problem is, that the XML-Parser uses waitForNextRequestReturned(). This can only run on application thread and even Platform.runLater() won´t fix it as long as there is no solution found you can only load the repo by click on the new implemented button.  
		 * 
		 * if (Boolean.valueOf(PropertyManager.getProperty(UserProperty.
		 * LOAD_MODELS_BY_STARTUP.toString()))) { new
		 * StartupModelLoader().loadModelsFromSavedModelsPath(); }
		 */
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {	
				final KeyCombination keyCombinationShiftC = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);
				if (keyCombinationShiftC.match(event)) {
					testDiagramViewIntro();
				}		
			}
		});
				
	}
	
	protected void testDiagramViewIntro() {
		new ToolIntroductionManager(this).start();
	}

	private void showCloseWarningDialog(Event event) {
		if (!Boolean.valueOf(PropertyManager.getProperty(UserProperty.APPLICATION_CLOSING_WARNING.toString()))){
			return;
		}
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Close Warning");
		alert.setHeaderText("Application is closing!");
		alert.setContentText("Proceed?");
		
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().add(buttonTypeCancel);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			//TODO WorkbenchClient.theClient().shutdownEvent();
			Runtime.getRuntime().halt(0);
		} else {
			event.consume(); 
		}
	}

	private final class ControlCenterMenuBar extends MenuBar{
		
		public ControlCenterMenuBar() {
			Menu helpMenu = new Menu("Help");
			getMenus().add(helpMenu);
			buildHelpMenu(helpMenu);
		}

		private void buildHelpMenu(Menu helpMenu) {
			MenuItem getProjectInformationItem = new MenuItem("Get Project Information");
			getProjectInformationItem.setOnAction(e->openWebpage("https://le4mm.org/"));
			
			MenuItem getSourceCodeItem = new MenuItem("Get Source Code");
			getSourceCodeItem.setOnAction(e->openWebpage("https://github.com/xmf-xmodeler"));
			
			MenuItem getBluebook = new MenuItem("Show XMF Bluebook");
			getBluebook.setOnAction(e-> openBluebook());
			
			MenuItem aboutItem = new MenuItem("About");
			aboutItem.setOnAction(e-> callAboutStage());
							
			helpMenu.getItems().addAll(getProjectInformationItem,getSourceCodeItem, getBluebook, aboutItem);
		}
		
		private void openWebpage(String url) {
			Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(new URL(url).toURI());
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
		}
		
		private void openBluebook() {
			File file = new File("doc/Bluebook.pdf");
			Desktop desktop = Desktop.getDesktop();
			if(file.exists() && Desktop.isDesktopSupported()) {
				try {
					desktop.open(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void callAboutStage() {
			Stage stage = new Stage();
			stage.setTitle("About XModeler");
			VBox root = new VBox();
			root.setAlignment(Pos.BASELINE_CENTER);
			Scene scene = new Scene(root,400,400);
			Image image = null;
			FileInputStream inputstream;
			try {
				inputstream = new FileInputStream("resources/jpeg/LE4MM-Hintergrund-Terassen.jpg");
				image = new Image(inputstream); 
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			Label header = new Label("Imagine you had a tool...");
			header.fontProperty().setValue(new Font(25));
			Label filler = new Label(); 
			
			ImageView imageView = new ImageView(image);
			imageView.setFitHeight(300); 
			imageView.setFitWidth(300);
			
			Label versionLable = new Label("Version: " + XModeler.getVersion()); 
			Label dateLable = new Label("Build Date: " + XModeler.getBuildDate());
			
			root.getChildren().addAll(header, filler, imageView, versionLable, dateLable);
			
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();
		}
	}
		

	private GridPane buildGridPane() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		
		Label projectLabel = new Label("Projects");
		grid.add(projectLabel, 2, 1);
				
		Button newProject = new Button("Create Project");
		newProject.setOnAction((event) -> {controlCenterClient.createNewProject();controlCenterClient.getAllProjects();});
		grid.add(newProject, 2, 1);
		GridPane.setHalignment(newProject, HPos.CENTER);
		
//		Button renameProject = new Button("Rename Project");
//		renameProject.setOnAction((event) -> {controlCenterClient.renameProject(modelLV.getSelectionModel().getSelectedItem());controlCenterClient.getAllProjects();});
//		grid.add(renameProject, 2, 5);
//		GridPane.setHalignment(renameProject, HPos.LEFT);
//		
//		Button removeProject = new Button("Delete Project");
//		removeProject.setOnAction((event) -> {controlCenterClient.removeProject(modelLV.getSelectionModel().getSelectedItem());controlCenterClient.getAllProjects();});
//		grid.add(removeProject, 2, 6);
//		GridPane.setHalignment(removeProject, HPos.LEFT);

		Button refreshAll = new Button("refresh");
		refreshAll.setOnAction((event) -> controlCenterClient.getAllProjects());
		GridPane.setHalignment(refreshAll, HPos.RIGHT);
		
		Label modelLabel = new Label("Models");	//Button added later because of DidacticMode check
		
		Button newModel = new Button("Create Model"); //Button added later because of DidacticMode check
		newModel.setDisable(true);
		GridPane.setHalignment(newModel, HPos.RIGHT);
		
		Label diagramLabel = new Label("Diagrams");
		grid.add(diagramLabel, 4, 1);

		Button newDiagram2 = new Button("Create UML Diagram");		//reactivated by Tom for uml concrete syntax implementation, also some buttons deactivated for simplicity for dumb users
		newDiagram2.setDisable(true);
		newDiagram2.disableProperty().bind(
				Bindings.isNull(modelLV.getSelectionModel().selectedItemProperty())
				);
		newDiagram2.setOnAction(e -> callNewDiagramDialog(true, "UMLDiagram")); 
		GridPane.setHalignment(newDiagram2, HPos.RIGHT);
		Button newDiagram = new Button("Create FMMLx Diagram");
		newDiagram.setDisable(true);
		newDiagram.disableProperty().bind(
				Bindings.isNull(modelLV.getSelectionModel().selectedItemProperty())
				);
		newDiagram.setOnAction(e -> callNewDiagramDialog(false, getDiagramNameSuggestion())); 
		
		grid.add(newDiagram, 4, 1);			
		GridPane.setHalignment(newDiagram, HPos.RIGHT);
		
		projectTree.setPrefSize(250, 150);
		grid.add(projectTree, 2, 2);
		TreeItem<String> loading = new TreeItem<String>("Loading");
		projectTree.setRoot(loading);
		projectTree.getSelectionModel().selectedItemProperty().addListener((prop, old, NEWW)->controlCenterClient.getProjectModels(getProjectPath(projectTree.getSelectionModel().getSelectedItem())));
		final Image image = new Image(new File("resources/gif/Projects/Project.gif").toURI().toString());
		projectTree.setCellFactory(new ProjectTreeCellFactory(image));
		
		modelLV.setPrefSize(250, 150);	//added Later because of DidacticMode check
		modelLV.setOnMouseClicked(e->{if (e.getClickCount()==2 && e.getButton()==MouseButton.PRIMARY) modelDoubleClick(e);});
		modelLV.getSelectionModel().selectedItemProperty().addListener((prop, old, NEWW)->newModelSelected(NEWW));
		
		diagramLV.setOnMouseClicked(me -> handleClickOnDiagramListView(me));
		diagramLV.setPrefSize(250, 150);
		grid.add(diagramLV, 4, 2);

		Button concreteSyntaxWizardStart = new Button("Concrete Syntax Wizard");
		concreteSyntaxWizardStart.setOnAction(e -> callConcreteSyntaxWizard());
		Button loadModelDir = JavaFxButtonAuxilary.createButton("Load Model Directory", (e) -> {new StartupModelLoader().loadModelsFromSavedModelsPath();});
		
		Button howToStart = new Button("How to...");
			howToStart.setOnAction(e -> {
				HowToDialog d = new HowToDialog();
				d.showAndWait();
			});
			
		if(!Boolean.parseBoolean((PropertyManager.getProperty(UserProperty.DIDACTIC_MODE.toString())))) {
		grid.add(refreshAll, 2, 1);
		grid.add(concreteSyntaxWizardStart, 3, 4);
		grid.add(loadModelDir, 2, 4);
		grid.add(modelLabel, 3, 1);
		grid.add(modelLV, 3, 2);
		grid.add(newModel, 3, 1);
		grid.add(howToStart, 4, 4);
		}
		else {
		grid.add(newDiagram2, 4, 4);
		}
		
		
//		grid.add(concreteSyntaxWizardStart, 3, 4);
		
		return grid;
	}

	private String getDiagramNameSuggestion() {
		int i = 1;
		while(diagramLV.getItems().contains("diagram" + i)) i++;
		return "diagram" + i;
	}

	private void handleClickOnDiagramListView(MouseEvent me) {
		if(me.getClickCount() == 2 && me.getButton() == MouseButton.PRIMARY) {
			String selectedDiagramString = diagramLV.getSelectionModel().getSelectedItem();
			if(selectedDiagramString != null) {
				String selectedModelString = modelLV.getSelectionModel().getSelectedItem();
				if(selectedModelString != null) {
					FmmlxDiagramCommunicator.getCommunicator().openDiagram(selectedModelString, selectedDiagramString);
		        }
		    }
		}
	}

	private void callConcreteSyntaxWizard() {
		ConcreteSyntaxWizard wizard = new ConcreteSyntaxWizard();
		wizard.start(new Stage());
	}

	private void callNewDiagramDialog(boolean umlMode, String defaultName) {
		TextInputDialog dialog = new TextInputDialog(defaultName);
		dialog.setTitle("Create new Diagram");
		dialog.setContentText("New diagram name:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			if(InputChecker.isValidIdentifier(result.get())) {
			FmmlxDiagramCommunicator.getCommunicator().createDiagram(
				modelLV.getSelectionModel().getSelectedItem(), 
				result.get(), "", FmmlxDiagramCommunicator.DiagramType.ClassDiagram, umlMode, 
				diagramID->{
					controlCenterClient.getDiagrams(modelLV.getSelectionModel().getSelectedItem());
				});
			    
			}  else {
				new Alert(AlertType.ERROR, 
					"\"" + result.get() + "\" is not a valid identifier.", 
					new ButtonType("Damned", ButtonData.YES)).showAndWait();
			};
		}
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

	public Stage getStageForConsole() {
		return new Stage();
	}
	
	public Stage getStageForEditor() {
		return new Stage();
	}
	
	private final class ProjectTreeCellFactory implements Callback<TreeView<String>, TreeCell<String>> {
		private final Image image;
		private ProjectTreeCellFactory(Image image) {
			this.image = image;
		}

		@Override
		public TreeCellWithMenu<String> call(TreeView<String> p){
			return new TreeCellWithMenu<String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if(empty) {
						setText(null);
						setGraphic(null);
					}else {
						setText(getTreeItem().getValue());
						if(getTreeItem().isLeaf()) {
							setGraphic(new ImageView(image));
						}else {
							setGraphic(null);
						}
					}
				}
			};
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
		this.removeNoneProjectEntries();
});	}
	
	private void removeNoneProjectEntries() {		//removes Child nodes which are not Projects from the models tree e.g. compiler etc.
		if(Boolean.parseBoolean((PropertyManager.getProperty(UserProperty.DIDACTIC_MODE.toString())))) {
			if(projectTree.getRoot().getChildren().get(0).getChildren().size()>1) {
				projectTree.getRoot().getChildren().get(0).getChildren().remove(1);
			}
			else {
				projectTree.getRoot().getChildren().get(0).getChildren().remove(0);
			}
		}
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
	
	private class TreeCellWithMenu<Type> extends TextFieldTreeCell<Type> {

	    ContextMenu menu;

	    public TreeCellWithMenu() {
	        //ContextMenu with one entry	    	
	    	MenuItem renameProject = new MenuItem("Rename Project");
			renameProject.setOnAction((event) -> {controlCenterClient.renameProject(modelLV.getSelectionModel().getSelectedItem());controlCenterClient.getAllProjects();});
			
			MenuItem removeProject = new MenuItem("Delete Project");
			removeProject.setOnAction((event) -> {controlCenterClient.removeProject(modelLV.getSelectionModel().getSelectedItem());controlCenterClient.getAllProjects();});
			
	        menu = new ContextMenu(renameProject, removeProject);
	    }

	    @Override
	    public void updateItem(Type t, boolean bln) {
	        //Call the super class so everything works as before
	        super.updateItem(t, bln);
	        //Check to show the context menu for this TreeItem
	        if (showMenu(t, bln)) {
	            setContextMenu(menu);
	        }else{
	            //If no menu for this TreeItem is used, deactivate the menu
	            setContextMenu(null);
	        }
	    }
	    
	    //Decide if a menu should be shown or not
	    private boolean showMenu(Type t, boolean bln){
	        if (t != null && !t.equals("Root")) {
	            return true;
	        }
	        return false;
	    }        

	}
}
