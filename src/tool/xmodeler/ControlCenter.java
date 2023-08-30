package tool.xmodeler;

import java.awt.Desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;
import java.util.stream.Stream;
import java.util.Arrays;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.scene.Node;
import javafx.collections.*;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.classbrowser.ModelBrowser;
import tool.clients.fmmlxdiagrams.dialogs.InputChecker;
import tool.clients.fmmlxdiagrams.graphdb.UploadConfig;
import tool.clients.fmmlxdiagrams.graphics.wizard.ConcreteSyntaxWizard;
import tool.clients.workbench.WorkbenchClient;
import tool.helper.IconGenerator;

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
		getIcons().add(IconGenerator.getImage("shell/mosaic32"));
		ControlCenterClient.init(this);
		controlCenterClient = ControlCenterClient.getClient();
	
		VBox root = new VBox();
		menuBar = new ControlCenterMenuBar();
		GridPane grid = buildGridPane(); 
		root.getChildren().addAll(menuBar, grid);
		
		int toolWidth = Integer.valueOf(PropertyManager.getProperty("toolWidth"));
		int toolHeight = Integer.valueOf(PropertyManager.getProperty("toolHeight"));
		Scene scene = new Scene(root, toolWidth, toolHeight);
		setScene(scene);
				
		this.setOnShown((event) -> controlCenterClient.getAllCategories());
		setOnCloseRequest(closeEvent -> showCloseWarningDialog(closeEvent));
				
		controlCenterClient.getAllProjects();	
		if (Boolean.valueOf(PropertyManager.getProperty(UserProperty.LOAD_MODELS_BY_STARTUP.toString()))) {
			new StartupModelLoader().loadModelsFromSavedModelsPath();			
		}
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
			
			MenuItem getProjectInformationItem = new MenuItem("Get Project Information");
			getProjectInformationItem.setOnAction(e->openWebpage("https://le4mm.org/"));
			
			MenuItem getSourceCodeItem = new MenuItem("Get Source Code");
			getSourceCodeItem.setOnAction(e->openWebpage("https://github.com/xmf-xmodeler"));
			
			MenuItem getBluebook = new MenuItem("Show XMF Bluebook");
			getBluebook.setOnAction(e-> openBluebook());
			
			MenuItem aboutItem = new MenuItem("About");
			aboutItem.setOnAction(e-> callAboutStage());
							
			helpMenu.getItems().addAll(getProjectInformationItem,getSourceCodeItem, getBluebook, aboutItem);
			
			Menu GraphDB = new Menu("GraphDB");
			getMenus().add(GraphDB);
			
			MenuItem saveModelToGraphDB = new MenuItem("GraphDB Configurations");
			saveModelToGraphDB.setOnAction(e -> openUploadDialog());
			
			GraphDB.getItems().addAll(saveModelToGraphDB);

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
		
		//build first row
		Label projectLabel = new Label("Projects");
		grid.add(projectLabel, 2, 1);
				
		Button newProject = new Button("Create Project");
		newProject.setOnAction((event) -> {controlCenterClient.createNewProject();controlCenterClient.getAllProjects();});
		grid.add(newProject, 2, 1);
		GridPane.setHalignment(newProject, HPos.CENTER);

		Button refreshAll = new Button("refresh");
		refreshAll.setOnAction((event) -> controlCenterClient.getAllProjects());
		grid.add(refreshAll, 2, 1);
		GridPane.setHalignment(refreshAll, HPos.RIGHT);
		
		Label modelLabel = new Label("Models");
		grid.add(modelLabel, 3, 1);
		
		Button newModel = new Button("Create Model");
		newModel.setDisable(true);
		grid.add(newModel, 3, 1);
		GridPane.setHalignment(newModel, HPos.RIGHT);
		
		Label diagramLabel = new Label("Diagrams");
		grid.add(diagramLabel, 4, 1);

		Button newDiagram2 = new Button("Create UML Diagram");
		newDiagram2.setDisable(true);
		newDiagram2.disableProperty().bind(
				Bindings.isNull(modelLV.getSelectionModel().selectedItemProperty())
				);
		newDiagram2.setOnAction(e -> callNewDiagramDialog(true)); 
		
		Button newDiagram = new Button("Create FMMLx Diagram");
		newDiagram.setDisable(true);
		newDiagram.disableProperty().bind(
				Bindings.isNull(modelLV.getSelectionModel().selectedItemProperty())
				);
		newDiagram.setOnAction(e -> callNewDiagramDialog(false)); 
		
		grid.add(newDiagram, 4, 1);
		GridPane.setHalignment(newDiagram, HPos.RIGHT);
		
		//build second column
		projectTree.setPrefSize(250, 150);
		grid.add(projectTree, 2, 2);
		TreeItem loading = new TreeItem("Loading");
		projectTree.setRoot(loading);
		projectTree.getSelectionModel().selectedItemProperty().addListener((prop, old, NEWW)->controlCenterClient.getProjectModels(getProjectPath(projectTree.getSelectionModel().getSelectedItem())));
		final Image image = new Image(new File("resources/gif/Projects/Project.gif").toURI().toString());
		projectTree.setCellFactory(new ProjectTreeCellFactory(image));
		
		modelLV.setPrefSize(250, 150);
		grid.add(modelLV, 3, 2);
		modelLV.setOnMouseClicked(e->{if (e.getClickCount()==2 && e.getButton()==MouseButton.PRIMARY) modelDoubleClick(e);});
		modelLV.getSelectionModel().selectedItemProperty().addListener((prop, old, NEWW)->newModelSelected(NEWW));
		
		diagramLV.setOnMouseClicked(me -> handelClickOnDiagramListView(me));
		diagramLV.setPrefSize(250, 150);
		grid.add(diagramLV, 4, 2);

		//build third row
		Button concreteSyntaxWizardStart = new Button("Concrete Syntax Wizard");
		concreteSyntaxWizardStart.setOnAction(e -> callConcreteSyntaxWizard());		
		grid.add(concreteSyntaxWizardStart, 3, 4);
		grid.add(newDiagram2, 4, 4);
		
		return grid;
	}

	private void handelClickOnDiagramListView(MouseEvent me) {
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

	private void callNewDiagramDialog(boolean umlMode) {
		TextInputDialog dialog = new TextInputDialog();
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
		public TreeCell<String> call(TreeView<String> p){
			return new TreeCell<String>() {
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
		
		ObservableList<Integer> currSel = projectTree.getSelectionModel().getSelectedIndices();
		Integer[] integerArray = Arrays.copyOf(currSel.toArray(), currSel.toArray().length, Integer[].class);
		
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
		
		//if( integerArray.length > 0 ) {
			
			//for( int i : integerArray) {
				//projectTree.getSelectionModel().selectIndices( i ); // Remember the selected indices from the listView after update
				// Might be counterintuitive if new projects are created in the meanwhile
				// Instead derive the id based on the projectname
				// For now always do the below
			//}
		//} else {
			// Default select the first Element under "myProjects" if no selection is made
			int i = 0;
			try {
				while( i < 500000) {
					TreeItem<String> currEl = projectTree.getTreeItem(i);
					i++;
					if ( currEl.getValue().equals("MyProjects")) {
						break; // i contains the first custom project
					}
				}
			} catch( Exception e ) {
				i = 0; // Select root element
			}
			
			projectTree.getSelectionModel().select(i);
		//}
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
	
	public void openUploadDialog()
	{
		Platform.runLater(() -> 
		{
			UploadConfig uc = new UploadConfig();
			uc.setUriTextfield(PropertyManager.getProperty("graphDBUri"));
			uc.setUserTextfield(PropertyManager.getProperty("graphDBUser"));
			Optional<UploadConfig.Result> result = uc.showAndWait();
			

			if (result.isPresent()) {
				final UploadConfig.Result mcdResult = result.get();
//				Preferences userPreferences = Preferences.userRoot();
				PropertyManager.setProperty("graphDBUri", mcdResult.uri);
				PropertyManager.setProperty("graphDBUser", mcdResult.user);
				PropertyManager.setProperty("graphDBPassword", mcdResult.password);

			}
			
			
		});
	}
}
