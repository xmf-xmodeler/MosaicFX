package tool.xmodeler;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tool.clients.menus.MenuClient;
import tool.helper.IconGenerator;
import tool.helper.FXAuxilary.JavaFxButtonAuxilary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

public class PropertyManager {

	//settings
	static String filePath;
	static Properties properties;
	static double javaVersion = Double.parseDouble(System.getProperty("java.specification.version"));	

	//ui
	Stage stage;
	GridPane generalGrid;
	GridPane debugGrid;
	GridPane pathGrid;
	

	public PropertyManager(String filePath) {
		PropertyManager.filePath = filePath;
		PropertyManager.properties = new Properties();
		loadProperties();
	}

	private void loadProperties() {
		try {
			properties.load(new FileInputStream(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void storeProperties() {
        setXmfDebugging();
		try {
			properties.store(new FileOutputStream(filePath), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//public

	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
		storeProperties();
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

	// GUI

	public void getUserInterface() {
		TabPane tabPane = new TabPane();
		Tab saveTab = new Tab("Saved Models");
		Tab userInterfaceTab  = new Tab("UserInterface");
		Tab debugTab = new Tab("Debugging");
		
		buildSaveTab(saveTab);
		
		Button btnCancel = new Button("Cancel");
		btnCancel.setOnAction(this::onCancelButtonClicked);
		Button btnCancelDebug = new Button("Cancel");
		btnCancelDebug.setOnAction(this::onCancelButtonClicked);
		Button btnSave = new Button("Save");
		btnSave.setOnAction(this::onSaveButtonClicked);
		Button btnSaveDebug = new Button("Save");
		btnSaveDebug.setOnAction(this::onSaveButtonClicked);
			    
		generalGrid = new GridPane();
		Label uiInfo = new Label("User Interface appearance");
		uiInfo.setStyle("-fx-font-weight: bold");
		Label toolX = new Label("Screen_X: ");
		TextField toolXField = new TextField("100");
		toolXField.setMaxWidth(120);
		Label toolY = new Label("Screen_Y: ");
		TextField toolYField = new TextField("100");
		toolYField.setMaxWidth(120);
		Label toolWidth = new Label("Tool Width: ");
		TextField toolWidthField = new TextField("1200");
		toolWidthField.setMaxWidth(120);
		Label toolHeight = new Label("Tool Height: ");
		TextField toolHeightField = new TextField("900");
		toolHeightField.setMaxWidth(120);
		Label separatInfo = new Label("Elements of the XModeler");
		separatInfo.setStyle("-fx-font-weight: bold");
		CheckBox formSeparately = new CheckBox("Forms in separat windows as default");
		formSeparately.setSelected(false);
		CheckBox consoleSeparately = new CheckBox("Console in a separat window as default");
		consoleSeparately.setSelected(false);
		CheckBox consoleShown = new CheckBox("Console shown as default");
		consoleShown.setSelected(false);
		CheckBox treeSeparately = new CheckBox("Editor tree in separat windows");
		treeSeparately.setSelected(false);
		Separator separator = new Separator();
		separator.setOrientation(Orientation.HORIZONTAL);
		generalGrid.setPadding(new Insets(5,5,5,5));
		generalGrid.setVgap(5);
		generalGrid.setHgap(5);
		generalGrid.add(uiInfo, 0, 0);
		generalGrid.add(toolX, 0, 1);
		generalGrid.add(toolXField, 0,1);
		GridPane.setHalignment(toolXField, HPos.LEFT);
		GridPane.setHalignment(toolXField, HPos.RIGHT);
		generalGrid.add(toolY, 0, 2);
		generalGrid.add(toolYField, 0,2);
		GridPane.setHalignment(toolYField, HPos.RIGHT);
		generalGrid.add(toolWidth, 0, 3);
		generalGrid.add(toolWidthField, 0,3);
		GridPane.setHalignment(toolWidthField, HPos.RIGHT);
		generalGrid.add(toolHeight, 0, 4);
		generalGrid.add(toolHeightField, 0,4);
		GridPane.setHalignment(toolHeightField, HPos.RIGHT);
		generalGrid.add(separator, 0, 5);
		generalGrid.add(separatInfo, 0, 6);
		generalGrid.add(formSeparately, 0, 7);
		generalGrid.add(consoleSeparately, 0, 8);
		generalGrid.add(consoleShown, 0, 9);
		generalGrid.add(treeSeparately, 0,10);
		generalGrid.add(btnSave, 0, 11);
		GridPane.setHalignment(btnSave, HPos.RIGHT);
		generalGrid.add(btnCancel, 1, 11);
		GridPane.setHalignment(btnCancel, HPos.LEFT);
		userInterfaceTab.setContent(generalGrid);
				
		debugGrid = new GridPane();
		debugGrid.setPadding(new Insets(5,5,5,5));
		debugGrid.setVgap(5);
		debugGrid.setHgap(5);
		Label debugInfo = new Label("Degugging Options");
		debugInfo.setStyle("-fx-font-weight: bold");
		CheckBox monitorClientCommunication = new CheckBox("MONITOR_CLIENT_COMMUNICATION");
		monitorClientCommunication.setSelected(false);
		CheckBox monitorDaemonFiring = new CheckBox("MONITOR_DAEMON_FIRING");
		monitorDaemonFiring.setSelected(false);
		CheckBox monitorIgnoreSaveImage = new CheckBox("IGNORE_SAVE_IMAGE");
		monitorIgnoreSaveImage.setSelected(false);
		CheckBox LogXmfOutput = new CheckBox("LOG_XMF_OUTPUT");
		LogXmfOutput.setSelected(false);
		Label monitorCalls = new Label("Monitor calls");
		Button btnMonitorCalls = new Button("Open");
		btnMonitorCalls.setOnAction(actionEvent->MenuClient.openCallMonitor());
		Label perfomanceMonitor = new Label("Performance monitor");
		Button btnPerfomanceMonitor = new Button("Open");
		btnPerfomanceMonitor.setOnAction(actionEvent->MenuClient.openPerformanceMonitor());
		
		debugGrid.add(debugInfo, 0,0);
		debugGrid.add(monitorClientCommunication,0, 1);
		debugGrid.add(monitorDaemonFiring, 0,2);
		debugGrid.add(monitorIgnoreSaveImage, 0,3);
		debugGrid.add(LogXmfOutput, 0,4);
		debugGrid.add(monitorCalls, 0,5);
		debugGrid.add(btnMonitorCalls, 1,5);
		debugGrid.add(perfomanceMonitor, 0,6);
		debugGrid.add(btnPerfomanceMonitor, 1,6);
		debugGrid.add(btnSaveDebug, 0, 7);
		GridPane.setHalignment(btnSaveDebug, HPos.RIGHT);
		debugGrid.add(btnCancelDebug, 1, 7);
		GridPane.setHalignment(btnCancelDebug, HPos.LEFT);
		
		debugTab.setContent(debugGrid);
		debugTab.setDisable(true);
		tabPane.getTabs().addAll(saveTab,userInterfaceTab,debugTab);
		VBox vBox = new VBox(tabPane);
		initScene(vBox);
	}

	private void buildSaveTab(Tab saveTab) {
		pathGrid = new GridPane();
		pathGrid.setPadding(new Insets(5,5,5,5));
		pathGrid.setVgap(5);
		pathGrid.setHgap(5);
		Label saveInfo = new Label("Locations for files");
		saveInfo.setStyle("-fx-font-weight: bold");
		File folder = new File("");
		folder=new File(folder.toURI()).getParentFile();
		Label saveLabel = new Label("Saved models ");
		TextField saveTextField = new TextField(new File(folder, "Saves").toString());
		
		
		Button btnSaveSaves = JavaFxButtonAuxilary.createButton("Save", this::onSaveButtonClicked);
		Button infoButton = JavaFxButtonAuxilary.createButtonWithGraphic("", this::showInfoDialog, null);
		
		Image icon = new Image(new File("resources/gif/Package.gif").toURI().toString());
	    ImageView imageView = new ImageView(icon);
	    
		DirectoryChooser saveDirectoryChooser = new DirectoryChooser();
		saveDirectoryChooser.setInitialDirectory(folder);
		Button saveDirectory = new Button();
		saveDirectory.setGraphic(imageView);
		saveDirectory.setOnAction(e -> {
            File selectedSaveDirectory = saveDirectoryChooser.showDialog(stage);
        });
		
		
		pathGrid.add(saveInfo, 0, 0);
		pathGrid.add(saveLabel, 0, 1);
		pathGrid.add(saveTextField, 1, 1);
		pathGrid.add(saveDirectory, 2,1);
		pathGrid.add(btnSaveSaves, 0, 4);
		GridPane.setHalignment(btnSaveSaves, HPos.RIGHT);
		
		
		saveTab.setContent(pathGrid);
		
	}

	private EventHandler<ActionEvent> showInfoDialog(ActionEvent actionEvent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void onCancelButtonClicked(ActionEvent actionEvent) {
		stage.close();
	}

	private void onSaveButtonClicked(ActionEvent actionEvent) {
		saveGridPane(generalGrid);
		saveGridPane(debugGrid);
		saveGridPane(pathGrid);
		stage.close();
	}

	private void saveGridPane(GridPane gridPane) {
		
			//parse rows and col into array
			Node[][] gridPaneNodes = getGridPaneRows(gridPane);

			//loop through all rows
			for (int row = 0; row < getGridLength(gridPane); row++) { //height
				// get key label
				String key = ((Label) gridPaneNodes[0][row]).getText();
				// get value field
				String value = getNodeValue(gridPaneNodes[1][row]);
				System.out.println(("key: " + key + ", val: " + value));
				if (value.isEmpty()) properties.remove(key);
				else properties.setProperty(key, value);
			
			}	 
		storeProperties();
	}

	private String getNodeValue(Node node) {
		String value = "";
		if (node instanceof TextField) {
			value = ((TextField) node).getText();
		}
		if (node instanceof CheckBox) {
			value = String.valueOf(((CheckBox) node).isSelected());
		}
		return value;
	}

	private Node[][] getGridPaneRows(GridPane gridPane) {
		Node[][] gridPaneNodes = new Node[2][getGridLength(gridPane)]; //width, height
		for (Node child : gridPane.getChildren()) {
			Integer column = GridPane.getColumnIndex(child);
			Integer row = GridPane.getRowIndex(child);
			if (column != null && row != null) {
				gridPaneNodes[column][row] = child;
			}
		}
		return gridPaneNodes;
	}

	private void initScene(VBox vb) {
		if (stage != null) stage.close();

		Scene scene = new Scene(vb);
		stage = new Stage();
		stage.setTitle("Preferences");
		stage.getIcons().add(IconGenerator.getImage("shell/mosaic32"));
		stage.setWidth(350);
		stage.setResizable(false);
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.show();
	}
	private int getGridLength(GridPane gridPane) { // Need for Reimplementing. Does not work for JAVA 14!
		int rows = 0;
		try {
			Method method = gridPane.getClass().getDeclaredMethod("getNumberOfRows");
			method.setAccessible(true);
			rows = (Integer) method.invoke(gridPane);
		} catch (Exception e) {
			System.err.println("Error @getGridLength @PropertyManager");
			//e.printStackTrace();
		}
		return rows;
	}
	
    //set xmf debugging values
    private static void setXmfDebugging() {
        MenuClient.setClientCommunicationMonitoring(getProperty("MONITOR_CLIENT_COMMUNICATION", false));
        MenuClient.setDaemonMonitoring(getProperty("MONITOR_DAEMON_FIRING", false));
    }

    public static void setXmfSettings() {
	    if (properties != null) setXmfDebugging();
    }
}
