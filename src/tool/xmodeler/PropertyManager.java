package tool.xmodeler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tool.clients.menus.MenuClient;
import tool.helper.IconGenerator;
import tool.helper.fXAuxilary.JavaFxButtonAuxilary;

//This class is structures in a Model View-Controller-Style. The basis class serves as model while the inner class represents model and controller
public class PropertyManager {
	static File userPropertiesFile = new File("user.properties");
	static Properties properties = new Properties(new DefaultUserProperties());
	
	public PropertyManager() {
		loadProperties();
	}

	private void loadProperties() {
		try {
			properties.load(new FileInputStream(userPropertiesFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void storeProperties() {
        setXmfDebugging();
		try {
			properties.store(new FileOutputStream(userPropertiesFile.toString()), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
		storeProperties();
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public static int getProperty(String key, int defaultValue) {
		return Integer.parseInt(properties.getProperty(key, defaultValue+""));
	}

	public static boolean getProperty(String key, boolean defaultValue) {
		return Boolean.parseBoolean(properties.getProperty(key, defaultValue+""));
	}
	
	public void showPropertyManagerStage() {
		new PropertyManagerStage().show();
	}
	
	//set xmf debugging values
	private static void setXmfDebugging() {
	MenuClient.setClientCommunicationMonitoring(getProperty("MONITOR_CLIENT_COMMUNICATION", false));
	MenuClient.setDaemonMonitoring(getProperty("MONITOR_DAEMON_FIRING", false));
	}

	public static void setXmfSettings() {
	if (properties != null) setXmfDebugging();
	}
	
	//View + Controller
	class PropertyManagerStage extends Stage{
		
		public PropertyManagerStage() {
				VBox root = new VBox();
				Scene scene = new Scene(root);
				setTitle("Preferences");
				getIcons().add(IconGenerator.getImage("shell/mosaic32"));
				setWidth(450);
				setResizable(false);
				setScene(scene);
				initModality(Modality.APPLICATION_MODAL);
				addTabs(root);
			}

		private void addTabs(VBox root) {
			TabPane tabPane = new TabPane();
			root.getChildren().add(tabPane);
			Tab directoriesTab = new Tab("Directories");
			Tab userInterfaceTab  = new Tab("UserInterface");
			//debugTab not running currently
			//Tab debugTab = new Tab("Debugging");
			tabPane.getTabs().addAll(directoriesTab,userInterfaceTab /*,debugTab */);
			buildDirectoriesTab(directoriesTab);
			buildUserInterfaceTab(userInterfaceTab);
			//buildDebugGrid(debugTab);
		}
		
		private void buildDirectoriesTab(Tab directoriesTab) {
			String savedModlesPath = getProperty("savedModelsPath");
			
			GridPane saveTabContentGrid = new GridPane();
			formatGrid(saveTabContentGrid);
						
			Label saveLabel = new Label("Saved models ");
			Label currentFolderPath = new Label((savedModlesPath == null) ? "Choose File" : savedModlesPath);
			currentFolderPath.setPrefWidth(250);
			currentFolderPath.setMaxWidth(250);
			
			//TODO:Use JavaFXAuxilarity
			Image aboutIcon = new Image(new File("resources/gif/img/about.gif").toURI().toString());
		    ImageView aboutImageView = new ImageView(aboutIcon);	
			Button infoButton = JavaFxButtonAuxilary.createButton("", this::showInfoDialog);
			infoButton.setGraphic(aboutImageView);
					
			Image icon = new Image(new File("resources/gif/Package.gif").toURI().toString());
		    ImageView imageView = new ImageView(icon);

		    Button saveDirectory = new Button();
			saveDirectory.setGraphic(imageView);
			saveDirectory.setOnAction(e -> {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				if (savedModlesPath != null) {
					File file = new File(savedModlesPath);
					directoryChooser.setInitialDirectory(file);	
				}				
				String savedModelsPath = directoryChooser.showDialog(this).toString();
				setProperty("savedModelsPath", savedModelsPath);
				currentFolderPath.setText(savedModelsPath);
			});
					
			saveTabContentGrid.add(saveLabel, 0, 1);
			saveTabContentGrid.add(infoButton, 1, 1);		
			saveTabContentGrid.add(currentFolderPath, 2, 1);
			saveTabContentGrid.add(saveDirectory, 3,1);
			directoriesTab.setContent(saveTabContentGrid);
		}
		
		private void showInfoDialog(ActionEvent actionEvent) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText("Set path to saved models");
			alert.setContentText("All files in the path and all subfolders with .xml extension are tried to be loaded by the start of XModeler."
					+ "\n All Folders which name start with \".\" are not included.");
			alert.showAndWait();
		}
	
		private void buildUserInterfaceTab(Tab userInterfaceTab) {
			VBox userInterfaceTabContent = new VBox();
			GridPane userInterfaceAppearanceGrid = buildUserInterfaceAppearanceGrid();
			userInterfaceTabContent.getChildren().addAll(userInterfaceAppearanceGrid);
			userInterfaceTab.setContent(userInterfaceTabContent);
		}
		
		private GridPane buildUserInterfaceAppearanceGrid() {
			GridPane userInterfaceAppearanceGrid = new GridPane();
			formatGrid(userInterfaceAppearanceGrid);
			
			Label header = new Label("User Interface appearance (confirm changes on enter)"
					+ "\n Changes are applied on restart!");
			header.setStyle("-fx-font-weight: bold");
			
			Label toolX = new Label("Screen_X: ");
			TextField toolXField = new TextField(getProperty("toolX"));
			toolXField.setMaxWidth(80);
			toolXField.setOnAction( e -> setProperty("toolX", toolXField.getCharacters().toString()));
			
			Label toolY = new Label("Screen_Y: ");
			TextField toolYField = new TextField(getProperty("toolY"));
			toolYField.setMaxWidth(80);
			toolYField.setOnAction( e -> setProperty("toolY", toolYField.getCharacters().toString()));
			
			Label toolWidth = new Label("Tool Width: ");
			TextField toolWidthField = new TextField(getProperty("toolWidth"));
			toolWidthField.setMaxWidth(80);
			toolWidthField.setOnAction( e -> setProperty("toolWidth", toolWidthField.getCharacters().toString()));
			
			Label toolHeight = new Label("Tool Height: ");
			TextField toolHeightField = new TextField(getProperty("toolHeight"));
			toolHeightField.setMaxWidth(80);
			toolHeightField.setOnAction( e -> setProperty("toolHeight", toolHeightField.getCharacters().toString()));
			
			Separator separator = new Separator();
			separator.setOrientation(Orientation.HORIZONTAL);
			
			userInterfaceAppearanceGrid.add(header, 0, 0);
			userInterfaceAppearanceGrid.add(toolX, 0, 1);
			userInterfaceAppearanceGrid.add(toolXField, 0,1);
			GridPane.setHalignment(toolXField, HPos.LEFT);
			GridPane.setHalignment(toolXField, HPos.RIGHT);
			userInterfaceAppearanceGrid.add(toolY, 0, 2);
			userInterfaceAppearanceGrid.add(toolYField, 0,2);
			GridPane.setHalignment(toolYField, HPos.RIGHT);
			userInterfaceAppearanceGrid.add(toolWidth, 0, 3);
			userInterfaceAppearanceGrid.add(toolWidthField, 0,3);
			GridPane.setHalignment(toolWidthField, HPos.RIGHT);
			userInterfaceAppearanceGrid.add(toolHeight, 0, 4);
			userInterfaceAppearanceGrid.add(toolHeightField, 0,4);
			GridPane.setHalignment(toolHeightField, HPos.RIGHT);
			userInterfaceAppearanceGrid.add(separator, 0, 5);
			return userInterfaceAppearanceGrid;
		}
	
		private void formatGrid (GridPane grid) {
			grid.setPadding(new Insets(5,5,5,5));
			grid.setVgap(5);
			grid.setHgap(5);
		}
	}	
		/* debugTab not running currently
		 * 
		 * private void buildDebugGrid(Tab debugTab) { Button btnCancelDebug = new
		 * Button("Cancel"); btnCancelDebug.setOnAction(this::onCancelButtonClicked);
		 * 
		 * GridPane debugGrid = new GridPane(); debugGrid.setPadding(new
		 * Insets(5,5,5,5)); debugGrid.setVgap(5); debugGrid.setHgap(5); Label debugInfo
		 * = new Label("Degugging Options");
		 * debugInfo.setStyle("-fx-font-weight: bold"); CheckBox
		 * monitorClientCommunication = new CheckBox("MONITOR_CLIENT_COMMUNICATION");
		 * monitorClientCommunication.setSelected(false); CheckBox monitorDaemonFiring =
		 * new CheckBox("MONITOR_DAEMON_FIRING");
		 * monitorDaemonFiring.setSelected(false); CheckBox monitorIgnoreSaveImage = new
		 * CheckBox("IGNORE_SAVE_IMAGE"); monitorIgnoreSaveImage.setSelected(false);
		 * CheckBox LogXmfOutput = new CheckBox("LOG_XMF_OUTPUT");
		 * LogXmfOutput.setSelected(false); Label monitorCalls = new
		 * Label("Monitor calls"); Button btnMonitorCalls = new Button("Open");
		 * btnMonitorCalls.setOnAction(actionEvent->MenuClient.openCallMonitor()); Label
		 * perfomanceMonitor = new Label("Performance monitor"); Button
		 * btnPerfomanceMonitor = new Button("Open");
		 * btnPerfomanceMonitor.setOnAction(actionEvent->MenuClient.
		 * openPerformanceMonitor());
		 * 
		 * debugGrid.add(debugInfo, 0,0); debugGrid.add(monitorClientCommunication,0,
		 * 1); debugGrid.add(monitorDaemonFiring, 0,2);
		 * debugGrid.add(monitorIgnoreSaveImage, 0,3); debugGrid.add(LogXmfOutput, 0,4);
		 * debugGrid.add(monitorCalls, 0,5); debugGrid.add(btnMonitorCalls, 1,5);
		 * debugGrid.add(perfomanceMonitor, 0,6); debugGrid.add(btnPerfomanceMonitor,
		 * 1,6); debugGrid.add(btnCancelDebug, 1, 7);
		 * GridPane.setHalignment(btnCancelDebug, HPos.LEFT);
		 * 
		 * debugTab.setContent(debugGrid); debugTab.setDisable(true); }
		 */		
}